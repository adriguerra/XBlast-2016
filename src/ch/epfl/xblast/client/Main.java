package ch.epfl.xblast.client;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.StandardProtocolFamily;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import ch.epfl.xblast.PlayerAction;
import ch.epfl.xblast.PlayerID;

/**
 * Main program of client. Receives the serialized game state of the server,
 * deserializes it and displays the game with the data sent by the server
 * 
 * @author Gabriel Hayat (246239)
 * @author Adrian Guerra Alejos (246225)
 */
public class Main {
    private static XBlastComponent xbc = new XBlastComponent();
    private static final int PORT = 2016;
    private static final int DEFAULT_ALLOCATION = 500;
    private static final int ONE_SECOND_IN_MS = 1000;

    public static void main(String[] args) throws IOException,
            InterruptedException, InvocationTargetException, URISyntaxException {
        DatagramChannel channel = DatagramChannel
                .open(StandardProtocolFamily.INET);
        channel.configureBlocking(false);

        String host = "localhost";

        if (args.length != 0) {
            host = args[0];
        }

        SocketAddress address = new InetSocketAddress(host, PORT);
        SwingUtilities.invokeAndWait(() -> createUI(channel, address));

        ByteBuffer joinGame = ByteBuffer.allocate(1);
        joinGame.put((byte) PlayerAction.JOIN_GAME.ordinal());
        joinGame.flip();
        ByteBuffer serializedGs = ByteBuffer.allocate(DEFAULT_ALLOCATION);

        SocketAddress sa;
        while ((sa = channel.receive(serializedGs)) == null) {
            channel.send(joinGame, address);
            Thread.sleep(ONE_SECOND_IN_MS);
        }
        channel.configureBlocking(true);

        while (sa != null) {
            serializedGs.flip();
            PlayerID clientID = PlayerID.values()[serializedGs.get()];
            List<Byte> serialized = new ArrayList<>();
            while (serializedGs.hasRemaining()) {
                serialized.add(serializedGs.get());
            }

            GameState gs = GameStateDeserializer
                    .deserializeGameState(serialized);
            xbc.setGameState(gs, clientID);
            serializedGs = ByteBuffer.allocate(DEFAULT_ALLOCATION);
            sa = channel.receive(serializedGs);
        }

    }

    /**
     * Creates user interface and an event dispatching thread on the
     * client's computer
     * 
     * @param channel
     *            through which relevant data is sent to server
     * @param address
     *            of server to which data is sent
     */
    public static void createUI(DatagramChannel channel, SocketAddress address) {
        JFrame f = new JFrame("XBlast2016");
        Map<Integer, PlayerAction> kb = new HashMap<>();

        kb.put(KeyEvent.VK_UP, PlayerAction.MOVE_N);
        kb.put(KeyEvent.VK_RIGHT, PlayerAction.MOVE_E);
        kb.put(KeyEvent.VK_DOWN, PlayerAction.MOVE_S);
        kb.put(KeyEvent.VK_LEFT, PlayerAction.MOVE_W);
        kb.put(KeyEvent.VK_SPACE, PlayerAction.DROP_BOMB);
        kb.put(KeyEvent.VK_SHIFT, PlayerAction.STOP);

        KeyboardEventHandler keh = new KeyboardEventHandler(kb,
                p -> sendPacket(p.ordinal(), channel, address));

        f.getContentPane().add(xbc, BorderLayout.CENTER);

        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
        f.pack();
        xbc.addKeyListener(keh);
        xbc.requestFocusInWindow();
    }

    /**
     * Sends the player's desired action through the given channel to the
     * address of the server given in parameters
     * 
     * @param action
     *            of player
     * @param channel
     *            through which data is sent
     * @param address
     *            of server
     */
    private static void sendPacket(int action, DatagramChannel channel,
            SocketAddress address) {
        ByteBuffer packet = ByteBuffer.allocate(1).put((byte) action);
        packet.flip();
        try {
            channel.send(packet, address);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
