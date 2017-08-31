package ch.epfl.xblast.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.ArgumentChecker;
import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Direction;
import ch.epfl.xblast.Lists;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.SubCell;
import ch.epfl.xblast.server.Player.DirectedPosition;
import ch.epfl.xblast.server.Player.LifeState;
import ch.epfl.xblast.server.Player.LifeState.State;

/**
 * GameState represents the state of the game
 * 
 * @author Gabriel Hayat (246239)
 * @author Adrian Guerra Alejos (246225)
 */

public final class GameState {
    private final int ticks;
    private final Board board;
    private final List<Player> players;
    private final List<Bomb> bombs;
    private final List<Sq<Sq<Cell>>> explosions;
    private final List<Sq<Cell>> blasts;
    private final static List<List<PlayerID>> permutations = Collections
            .unmodifiableList(Lists.permutations(Arrays.asList(
                    PlayerID.PLAYER_1, PlayerID.PLAYER_2, PlayerID.PLAYER_3,
                    PlayerID.PLAYER_4)));
    private static final Random RANDOM = new Random(2016);

    /**
     * Constructs a GameState with the following arguments
     * 
     * @param ticks
     * @param board
     * @param players
     *            in the game, stored in a List
     * @param bombs
     *            stored in a List of sequence of sequence of cells
     * @param explosions
     *            stored in a List of sequence of sequence of cells
     * @param blasts
     *            stored in a list of sequence
     */
    public GameState(int ticks, Board board, List<Player> players,
            List<Bomb> bombs, List<Sq<Sq<Cell>>> explosions,
            List<Sq<Cell>> blasts) {
        if (players.size() != 4) {
            throw new IllegalArgumentException();
        }
        this.ticks = ArgumentChecker.requireNonNegative(ticks);
        this.board = Objects.requireNonNull(board);
        this.players = Objects.requireNonNull(Collections
                .unmodifiableList(new ArrayList<>(players)));
        this.bombs = Objects.requireNonNull(Collections
                .unmodifiableList(new ArrayList<>(bombs)));
        this.explosions = Objects.requireNonNull(Collections
                .unmodifiableList(new ArrayList<>(explosions)));
        this.blasts = Objects.requireNonNull(Collections
                .unmodifiableList(new ArrayList<>(blasts)));
    }

    /**
     * Constructs a GameState for the board and players given at the tick 0,
     * with no bombs, explosions or blasts. Empty lists are used to represent no
     * bombs, explosions or blasts. This second constructor is mainly used to
     * represent the beginning of a game.
     * 
     * @param board
     * @param players
     */
    public GameState(Board board, List<Player> players) {
        this(0, board, players, Collections.emptyList(), Collections
                .emptyList(), Collections.emptyList());
    }

    public int ticks() {
        return ticks;
    }

    /**
     * Returns true if and only if the game is over, i.e. if remaining time is 0
     * or if there is only one player alive
     * 
     * @return true if and only if the game is over.
     */
    public boolean isGameOver() {
        int alive = 0;
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).isAlive())
                alive++;
        }

        return (alive <= 1 || this.remainingTime() < 0);
    }

    /**
     * Returns the time left in the game in seconds
     * 
     * @return time left in the game in seconds
     */
    public double remainingTime() {
        return (double) (Ticks.TOTAL_TICKS - ticks()) / Ticks.TICKS_PER_SECOND;
    }

    /**
     * Returns the identity of the winner of the game if there is one, otherwise
     * an empty Optional instance.
     * 
     * @return the identity of the winner of the game if there is one, otherwise
     *         an empty Optional instance
     */
    public Optional<PlayerID> winner() {
        if (alivePlayers().size() == 1) {
            return Optional.of(alivePlayers().get(0).id());
        }
        return Optional.empty();

    }

    public Board board() {
        return board;
    }

    public List<Player> players() {
        return players;
    }

    /**
     * Returns a list of players still alive in the game
     * 
     * @return a list of players still alive in the game
     */
    public List<Player> alivePlayers() {
        List<Player> playersAlive = new ArrayList<>();
        for (Player p : players) {
            if (p.isAlive())
                playersAlive.add(p);
        }
        return Collections.unmodifiableList(playersAlive);
    }

    /**
     * Returns a map associating each bomb to its position
     * 
     * @return a map associating each bomb to its position
     */

    public Map<Cell, Bomb> bombedCells() {
        Map<Cell, Bomb> temp = new HashMap<>();

        for (Bomb b : this.bombs) {
            temp.put(b.position(), b);
        }
        return Collections.unmodifiableMap(temp);
    }

    /**
     * Returns the set of cells on which there's at least 1 explosion particle
     * 
     * @return the set of cells on which there's at least 1 explosion particle
     */
    public Set<Cell> blastedCells() {
        Set<Cell> temp = new HashSet<>();

        for (Sq<Cell> s : this.blasts) {
            temp.add(new Cell(s.head().x(), s.head().y()));
        }
        return Collections.unmodifiableSet(temp);
    }

    /**
     * Returns the GameState of the next tick, taking into account the bombs
     * dropped by the players and the direction changes of the players at the
     * last tick.
     * 
     * @param speedChangeEvents
     *            a map of the direction changes of each player
     * @param bombDropEvents
     *            represents a list of the players that dropped a bomb at the
     *            last tick
     * @return the GameState of the next tick
     */

    public GameState next(Map<PlayerID, Optional<Direction>> speedChangeEvents,
            Set<PlayerID> bombDropEvents) {

        /*
         * 1. Blasted cells evolve
         */

        List<Sq<Cell>> blasts1 = GameState.nextBlasts(this.blasts, this.board,
                this.explosions);

        /*
         * We have to compute the consumed bonuses and the players that collect
         * bonuses using the order of priority for players that reach a bonus at
         * the same time
         * 
         * 2. Board evolves
         */

        List<PlayerID> players0 = GameState.permutations.get(ticks
                % permutations.size());
        List<Player> playersPriority = new ArrayList<>();
        for (PlayerID i : players0) {
            playersPriority.add(this.players.get(i.ordinal()));
        }

        Set<Cell> consumedBonuses = new HashSet<>();
        Map<PlayerID, Bonus> playerBonuses = new HashMap<>();

        for (Player p : playersPriority) {
            SubCell sc = p.position();
            if (this.board.blockAt(sc.containingCell()).isBonus()
                    && sc.isCentral()
                    && !consumedBonuses.contains(sc.containingCell())) {
                consumedBonuses.add(sc.containingCell());
                playerBonuses.put(p.id(),
                        this.board.blockAt(sc.containingCell())
                                .associatedBonus());
            } else {
                playerBonuses.put(p.id(), null);
            }
        }

        Set<Cell> blastedCells1 = new HashSet<>();

        for (Sq<Cell> element : blasts1) {
            blastedCells1.add(element.head());

        }

        Board board1 = GameState.nextBoard(this.board, consumedBonuses,
                blastedCells1);

        /*
         * 3. Explosions evolve
         */
        List<Sq<Sq<Cell>>> explosions1 = GameState
                .nextExplosions(this.explosions);

        /*
         * 4. Evolution of the bombs, generating new explosions that are added
         * to the list of explosions calculated in the previous step
         */

        List<Bomb> newlyDroppedBombs = GameState.newlyDroppedBombs(
                playersPriority, bombDropEvents, this.bombs);

        List<Bomb> bombs1 = new ArrayList<>();
        for (Bomb b : this.bombs) {
            if (blastedCells1.contains(b.position())) {
                explosions1.addAll(b.explosion());
                continue;
            }

            try {
                bombs1.add(new Bomb(b.ownerId(), b.position(), b.fuseLengths()
                        .tail(), b.range()));
            } catch (IllegalArgumentException e) {
                explosions1.addAll(b.explosion());
            }
        }
        bombs1.addAll(newlyDroppedBombs);
        Set<Cell> bombedCells1 = new HashSet<>();
        for (Bomb b : bombs1) {
            bombedCells1.add(b.position());
        }
        /*
         * 5. Players evolve
         */

        List<Player> players1 = GameState.nextPlayers(this.players,
                playerBonuses, bombedCells1, board1, blastedCells1,
                speedChangeEvents);

        return new GameState(this.ticks() + 1, board1, players1, bombs1,
                explosions1, blasts1);
    }

    /**
     * Returns a list of the explosion particles of the next game state based on
     * the current game state, the board and current explosions
     * 
     * @param blasts0
     * @param board0
     * @param explosions0
     * @return a list of the explosion particles of the next game state
     */
    private static List<Sq<Cell>> nextBlasts(List<Sq<Cell>> blasts0,
            Board board0, List<Sq<Sq<Cell>>> explosions0) {
        List<Sq<Cell>> temp = new ArrayList<>();
        for (Sq<Cell> s : blasts0) {
            if (board0.blockAt(s.head()).isFree() && !s.tail().isEmpty()) {
                temp.add(s.tail());
            }
        }

        for (Sq<Sq<Cell>> s : explosions0) {
            try {
                temp.add(s.head());
            } catch (NoSuchElementException e) {

            }
        }
        return temp;
    }

    /**
     * Returns the board of the next game state based on the current one, the
     * bonuses consumed by the players and the new particles of explosion given.
     * All bonus blocks in consumed bonuses must disappear, all destructible
     * walls hit by an explosion particle become crumbling walls for a number of
     * ticks before either becoming a bonus or a free block.
     * 
     * @param current
     *            board board0
     * @param consumed
     *            bonuses
     * @param new particles of explosion blastedCells1
     * @return new Board
     */
    private static Board nextBoard(Board board0, Set<Cell> consumedBonuses,
            Set<Cell> blastedCells1) {
        List<Sq<Block>> blocks = new ArrayList<>();

        for (Cell c : Cell.ROW_MAJOR_ORDER) {

            if (blastedCells1.contains(c)
                    && board0.blockAt(c).equals(Block.DESTRUCTIBLE_WALL)) {

                int a = RANDOM.nextInt(4);
                Block randomBlock;

                switch (a) {
                case 0:
                    randomBlock = Block.BONUS_BOMB;
                    break;
                case 1:
                    randomBlock = Block.BONUS_RANGE;
                    break;
                case 2:
                    randomBlock = Block.BONUS_SPEED;
                    break;
                case 3:
                    randomBlock = Block.FREE;
                    break;
                default:
                    randomBlock = null;
                }
                blocks.add(Sq.repeat(Ticks.WALL_CRUMBLING_TICKS,
                        Block.CRUMBLING_WALL).concat(Sq.constant(randomBlock)));
            }

            else if (consumedBonuses.contains(c)) {
                blocks.add(Sq.constant(Block.FREE));
            }

            else if (blastedCells1.contains(c) && board0.blockAt(c).isBonus()) {
                blocks.add(board0.blocksAt(c).tail()
                        .limit(Ticks.BONUS_DISAPPEARING_TICKS)
                        .concat(Sq.constant(Block.FREE)));
            } else {
                blocks.add(board0.blocksAt(c).tail());
            }
        }
        return new Board(blocks);
    }

    /**
     * Returns a list of the 4 players evolved according to the arguments of the
     * method.
     * 
     * @param player0
     *            list contaning 4 players
     * @param playerBonuses
     *            a map associating different bonuses to the players that should
     *            consume it
     * @param bombedCells1
     *            a set of the postions of the unexploded bombs on the board
     * @param board1
     *            the board at the current tick
     * @param blastedCells1
     *            a set of all the positions of blastedCells at the current tick
     * @param speedChangeEvents
     *            a map associating the identities of the players to their
     *            possible change of directions
     * @return a list of the 4 players evolved according to the arguments of the
     *         met
     */

    private static List<Player> nextPlayers(List<Player> players0,
            Map<PlayerID, Bonus> playerBonuses, Set<Cell> bombedCells1,
            Board board1, Set<Cell> blastedCells1,
            Map<PlayerID, Optional<Direction>> speedChangeEvents) {
        List<Player> players1 = new ArrayList<>();

        for (Player p0 : players0) {

            Sq<DirectedPosition> directedPos1;
            Sq<DirectedPosition> directedPos0 = p0.directedPositions();

            boolean speedChange = false;

            Direction d = null;

            if (speedChangeEvents.containsKey(p0.id())) {
                speedChange = true;
                if (speedChangeEvents.get(p0.id()).isPresent()) {
                    d = speedChangeEvents.get(p0.id()).get();
                }
            } else if (isMoving(directedPos0)) {
                d = p0.direction();
            }

            if (d == null) {
                directedPos1 = directedPos0.takeWhile(u -> !u.position()
                        .isCentral());
                directedPos1 = directedPos1.concat(DirectedPosition
                        .stopped(directedPos0.findFirst(u -> u.position()
                                .isCentral())));
            }

            else if (d == p0.direction()) { // does not change direction
                if (speedChange)
                    directedPos1 = DirectedPosition.moving(directedPos0.head());
                else
                    directedPos1 = directedPos0;
            }

            else if (d.isParallelTo(p0.direction())) {
                directedPos1 = DirectedPosition.moving(new DirectedPosition(p0
                        .position(), d));
            }

            else { // perpendicular change
                directedPos1 = directedPos0.takeWhile(u -> !u.position()
                        .isCentral());
                directedPos1 = directedPos1
                        .concat(DirectedPosition
                                .moving(directedPos0.findFirst(
                                        u -> u.position().isCentral())
                                        .withDirection(d)));
            }

            if (!p0.lifeState().canMove()) {
                players1.add(new Player(p0.id(), p0.lifeStates().tail(),
                        directedPos1, p0.maxBombs(), p0.bombRange()));
                continue;
            }

            int i = 0;
            while (isAbleToMove(p0, board1, directedPos1, bombedCells1)
                    && i++ < p0.speedFactor()
                    && !(directedPos1.head().position().isCentral() && !board1
                            .blockAt(
                                    directedPos1
                                            .head()
                                            .position()
                                            .containingCell()
                                            .neighbor(
                                                    directedPos1.head()
                                                            .direction()))
                            .canHostPlayer())) {
                directedPos1 = directedPos1.tail();
            }

            if (playerBonuses.get(p0.id()) != null) {
                p0 = playerBonuses.get(p0.id()).applyTo(p0);
            }

            Sq<LifeState> lifeStates1;
            /*
            Sq<Boolean> isSick = p0.isSick();
            Sq<Integer> newBombRange = p0.bombRange().tail();

            for (int i = 0; i < players0.size(); i++) {
                Player mayBeSickPlayer = players0.get(i);
                if (p0.position().equals(mayBeSickPlayer.position())
                        && mayBeSickPlayer.isSick().head()
                        && !p0.id().equals(mayBeSickPlayer.id()))
                    isSick = mayBeSickPlayer.isSick();
            }

            isSick = isSick.tail();*/

            if (blastedCells1.contains(directedPos1.head().position()
                    .containingCell())
                    && p0.lifeState().state().equals(State.VULNERABLE)) {
                lifeStates1 = p0.statesForNextLife();
            } else {
                lifeStates1 = p0.lifeStates().tail();
            }

            players1.add(new Player(p0.id(), lifeStates1, directedPos1, p0
                    .maxBombs(), p0.bombRange(), p0.speedFactor()));
        }
        return players1;
    }

    /**
     * Returns true if the player is able to move, taking into account the
     * method's arguments
     * 
     * @param p0
     *            the player to evaluate
     * @param board1
     *            the current board
     * @param directedPos1
     *            the next directedPosition of the player, calculated in the
     *            method nextPlayers()
     * @param bombedCells1
     *            a set of the current bombs' positions on the board
     * @return true if the player is able to move, taking into account the
     *         method's arguments
     */
    private static boolean isAbleToMove(Player p0, Board board1,
            Sq<DirectedPosition> directedPos1, Set<Cell> bombedCells1) {
        return (!((p0.position().isCentral() && !board1.blockAt(
                p0.position().containingCell()
                        .neighbor(directedPos1.head().direction()))
                .canHostPlayer()) || (bombedCells1.contains(p0.position()
                .containingCell()) && p0.position().distanceToCentral() == 6 && SubCell
                .centralSubCellOf(p0.position().containingCell()).equals(
                        directedPos1.tail()
                                .findFirst(u -> u.position().isCentral())
                                .position()))));

        /*
         * return (!((p0.position().isCentral() && !board1.blockAt(
         * p0.position().containingCell()
         * .neighbor(directedPos1.head().direction())) .canHostPlayer()) ||
         * (bombedCells1.contains(p0.position() .containingCell()) &&
         * p0.position().distanceToCentral() == 6 && SubCell
         * .centralSubCellOf(p0.position().containingCell()).equals(
         * directedPos1.tail() .findFirst(u -> u.position().isCentral())
         * .position()))));
         */
    }

    /**
     * Returns true if the sequence of directedPosition is not evolving on its
     * spot
     * 
     * @param directedPos0
     * @return true if the sequence of directedPosition is not evolving on its
     *         spot
     */
    private static boolean isMoving(Sq<DirectedPosition> directedPos0) {
        return (!(directedPos0.head().position().x() == directedPos0.tail()
                .head().position().x() && directedPos0.head().position().y() == directedPos0
                .tail().head().position().y()));
    }

    /**
     * Calculates the explosions for the next game state based on the current
     * explosions
     * 
     * @param explosions0
     * @return the new list of explosions
     */
    // not unmodifiable
    private static List<Sq<Sq<Cell>>> nextExplosions(
            List<Sq<Sq<Cell>>> explosions0) {
        List<Sq<Sq<Cell>>> temp = new ArrayList<>();
        for (Sq<Sq<Cell>> a : explosions0) {
            try {
                temp.add(a.tail());
            } catch (NoSuchElementException e) {

            }
        }
        return temp;
    }

    /**
     * Calculates the newly dropped bombs based on the current players, the set
     * of bombs dropped, and the list of current bombs. In order for the player
     * to to be able to drop a bomb he must be alive, the cell in which he's in
     * cannot already have a bomb and his number of bombs dropped must not
     * exceed his limit
     * 
     * @param players0
     *            list of players
     * @param bombDropEvents
     *            set of events of bombs dropped by the players
     * @param bombs0
     *            previous bombs
     * @return List of newly dropped bombs
     */
    private static List<Bomb> newlyDroppedBombs(List<Player> players0,
            Set<PlayerID> bombDropEvents, List<Bomb> bombs0) {

        if (bombDropEvents.isEmpty()) {
            return Collections.emptyList();
        }
        List<Bomb> bombs1 = new ArrayList<>();

        for (Player p : players0) {
            if (p.isAlive() && bombDropEvents.contains(p.id())) {
                if (!GameState.contains(bombs0, p)
                        && !GameState.contains(bombs1, p)
                        && GameState.containsMax(bombs0, p)) {
                    Bomb b = p.newBomb();
                    bombs1.add(new Bomb(b.ownerId(), b.position(), b
                            .fuseLengths().tail(), b.range()));
                }
            }
        }
        return Collections.unmodifiableList(bombs1);
    }

    /**
     * Checks if there already exists a bomb in the cell in which the player is
     * in
     * 
     * @param list
     *            of bombs
     * @param player
     *            that wants to drop a bomb
     * @return boolean returns true if the cell in which player is in already
     *         has a bomb
     */
    private static <T> boolean contains(List<Bomb> list, Player p) {
        for (Bomb b : list) {
            if (b.position().equals(p.position().containingCell())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the player has not exceeded his limit of number of bombs
     * dropped by counting his bombs currently owned and comparing it to the
     * maximum number of bombs he can drop
     * 
     * @param List
     *            of bombs
     * @param Player
     * @return boolean true if the bombs he has dropped is smaller than the
     *         maximum number of bombs he can drop false otherwise
     */
    private static boolean containsMax(List<Bomb> list, Player p) {
        int count = 0;
        for (Bomb element : list) {
            if (element.ownerId().equals(p.id())) {
                count++;
            }
        }
        return (count < p.maxBombs());
    }

}
