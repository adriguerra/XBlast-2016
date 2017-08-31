package ch.epfl.xblast.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardProtocolFamily;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import ch.epfl.xblast.Direction;
import ch.epfl.xblast.PlayerAction;
import ch.epfl.xblast.PlayerID;

/**
 * Server of the game
 * 
 * @author Gabriel Hayat (246239)
 * @author Adrian Guerra Alejos (246225)
 */
public class Server {
    private final int numberOfPlayers;
    private Map<SocketAddress, PlayerID> mapSAToID;
    private Level level;
    private DatagramChannel channel;

    public Server(int n) throws IOException {
        numberOfPlayers = n;
        level = Level.DEFAULT_LEVEL;
        mapSAToID = new HashMap<>();
        channel = DatagramChannel.open(StandardProtocolFamily.INET);
        channel.bind(new InetSocketAddress(2016));
        channel.configureBlocking(false);
    }

    /**
     * Waits for the players to join the game and returns true when they have
     * all joined
     * 
     * @return true when all players have joined the game
     * @throws IOException
     * @throws InterruptedException
     */
    public boolean waitForPlayers() throws IOException, InterruptedException {
        ByteBuffer b = ByteBuffer.allocate(1);
        int i = 0;
        while (mapSAToID.size() < numberOfPlayers) {
            b.clear();
            SocketAddress sa = channel.receive(b);
            if (sa != null && !mapSAToID.containsKey(sa)
                    && b.get(0) == PlayerAction.JOIN_GAME.ordinal()) {
                mapSAToID.put(sa, PlayerID.values()[i++]);
            }
        }
        return true;
    }

    /**
     * Sends the serialized updated game state based on the players' bombs
     * dropped and speed change events
     * 
     * @throws IOException
     * @throws InterruptedException
     */
    public void run() throws IOException, InterruptedException {

        while (!level.getGameState().isGameOver()) {
            sendsSerializedUpdatedGameState();

            long a = sleepingTime();
            Thread.sleep(a / 1000000L, (int) (a % 1000000));

            Pair<Map<PlayerID, Optional<Direction>>, Set<PlayerID>> computeEvents = computeEvents();
            level = new Level(level.getBoardPainter(), level.getGameState()
                    .next(computeEvents.first(), computeEvents.second()));

        }
        sendsSerializedUpdatedGameState();

        Optional<PlayerID> PotentialWinner = level.getGameState().winner();
        System.out
                .println(PotentialWinner.isPresent() ? "Congratulations! The winner is "
                        + PotentialWinner.get()
                        : "No winner");
    }

    /**
     * Sends the serialized updated game state to all the players in the game
     * 
     * @throws IOException
     */

    private void sendsSerializedUpdatedGameState() throws IOException {
        byte[] serialized = listToArray(GameStateSerializer.serialize(
                level.getBoardPainter(), level.getGameState()));
        ByteBuffer packet = ByteBuffer.allocate(serialized.length + 1);
        for (Map.Entry<SocketAddress, PlayerID> e : mapSAToID.entrySet()) {
            packet.put((byte) e.getValue().ordinal());
            packet.put(serialized);
            packet.flip();
            channel.send(packet, e.getKey());
            packet.clear();
        }
    }

    /**
     * Converts a list of bytes to an array. This method was required as the
     * method toArray() from List returned an Object[]
     * 
     * @param l
     *            list of bytes to convert
     * @return array of bytes
     */
    public byte[] listToArray(List<Byte> l) {
        byte[] b = new byte[l.size()];
        for (int i = 0; i < l.size(); i++) {
            b[i] = l.get(i);
        }
        return b;
    }

    /**
     * Computes the time the server should sleep at each tick
     * 
     * @return the time the server must sleep
     */
    private long sleepingTime() {
        long actualTime = System.nanoTime();

        return (Math.floorDiv(actualTime, Ticks.TICK_NANOSECOND_DURATION) + 1)
                * Ticks.TICK_NANOSECOND_DURATION - actualTime;
    }

    /**
     * Computes the speed change events and the bombs dropped events
     * simultaneously of the players using the pair class defined
     * 
     * @return Pair of the speed change events and the bombs dropped events
     * @throws IOException
     */
    private Pair<Map<PlayerID, Optional<Direction>>, Set<PlayerID>> computeEvents()
            throws IOException {
        Map<PlayerID, Optional<Direction>> speedChangeEvent = new HashMap<>();
        Set<PlayerID> bombDropEvent = new HashSet<>();
        ByteBuffer packet = ByteBuffer.allocate(1);
        SocketAddress temp = null;

        while ((temp = channel.receive(packet)) != null) {
            packet.flip();
            byte playerActionOrdinal = packet.get();

            if (playerActionOrdinal == PlayerAction.DROP_BOMB.ordinal()) {
                bombDropEvent.add(mapSAToID.get(temp));
            } else if (playerActionOrdinal > PlayerAction.JOIN_GAME.ordinal())
                speedChangeEvent.put(mapSAToID.get(temp), this
                        .playerActionToOptionalDirection(playerActionOrdinal));
            packet.clear();
        }
        return new Pair<>(speedChangeEvent, bombDropEvent);
    }

    private Optional<Direction> playerActionToOptionalDirection(byte ordinal) {
        if (0 < ordinal && ordinal < 5) {
            return Optional.of(Direction.values()[ordinal - 1]);
        }
        return Optional.empty();
    }

    private final static class Pair<E, F> {
        private final E e;
        private final F f;

        public Pair(E e, F f) {
            this.e = e;
            this.f = f;
        }

        public E first() {
            return this.e;
        }

        public F second() {
            return this.f;
        }
    }
}
