package ch.epfl.xblast.server;

import java.util.Objects;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.ArgumentChecker;
import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Direction;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.SubCell;
import ch.epfl.xblast.server.Player.LifeState.State;

/**
 * Player class has LifeState, DirectedPosition and State static classes nested
 * in its immutable class
 * 
 * @author Gabriel Hayat (246239)
 * @author Adrian Guerra Alejos (246225)
 */
public final class Player {
    private final PlayerID id;
    private final Sq<LifeState> lifeStates;
    private final Sq<DirectedPosition> directedPos;
    private final int maxBombs;
    private final int bombRange;
    private final int speedFactor;
    
    private final static int DEFAULT_SPEED_FACTOR = 1;
    /**
     * Constructs a player with the following arguments:
     * 
     * @param id
     * @param lifeStates
     * @param directedPos
     * @param maxBombs
     * @param bombRange
     * @param speedFactor
     */
    public Player(PlayerID id, Sq<LifeState> lifeStates,
            Sq<DirectedPosition> directedPos, int maxBombs, int bombRange,
            int speedFactor) {
        this.id = Objects.requireNonNull(id);
        this.lifeStates = Objects.requireNonNull(lifeStates);
        this.directedPos = Objects.requireNonNull(directedPos);
        this.maxBombs = ArgumentChecker.requireNonNegative(maxBombs);
        this.bombRange = ArgumentChecker.requireNonNegative(bombRange);
        this.speedFactor = ArgumentChecker.requireNonNegative(speedFactor);
    }

    /**
     * Constructs a player with the following arguments:
     * 
     * @param id
     * @param lifeStates
     * @param directedPos
     * @param maxBombs
     * @param bombRange
     *            Throws a nullPointerException if one of the first three
     *            arguments is null or an IllegalArgumentException if one of
     *            last two integer arguments are negative
     * 
     */
    public Player(PlayerID id, Sq<LifeState> lifeStates,
            Sq<DirectedPosition> directedPos, int maxBombs, int bombRange) {
        this(id, lifeStates, directedPos, maxBombs, bombRange, DEFAULT_SPEED_FACTOR);
    }

    /**
     * Constructs a player with the following arguments:
     * 
     * @param id
     * @param lives
     * @param position
     * @param maxBombs
     * @param bombRange
     *            Computes the sequence of life states and the sequence of
     *            directed positions in order to call the first constructor
     *            throws a nullPointerException if id is null or if
     *            newLifeStates or directedPos return a null value throws an
     *            IllegalArgumentException if maxBombs or bombRange is negative
     */

    public Player(PlayerID id, int lives, Cell position, int maxBombs,
            int bombRange) {
        this(id, newLifeStates(lives), directedPos(position), maxBombs,
                bombRange);
    }

    public PlayerID id() {
        return id;
    }

    public Sq<LifeState> lifeStates() {
        return lifeStates;
    }

    public LifeState lifeState() {
        return lifeStates().head();
    }

    /**
     * Returns the sequence of the next life state of the player, which begins
     * by a sequence of a player dying followed by a sequence of the player
     * being vulnerable and invulnerable
     * 
     * @return the concatenated sequence
     */
    public Sq<LifeState> statesForNextLife() {
        if (!this.isAlive()) {
            return Sq.constant(new LifeState(0, State.DEAD));
        }

        Sq<LifeState> a = Sq.constant(
                new LifeState(this.lifeState().lives(), State.DYING)).limit(
                Ticks.PLAYER_DYING_TICKS);
        Sq<LifeState> b = newLifeStates(this.lives() - 1);
        return a.concat(b);
    }

    public int lives() {
        return lifeState().lives();
    }

    public boolean isAlive() {
        return (lives() > 0);
    }

    public Sq<DirectedPosition> directedPositions() {
        return directedPos;
    }

    public SubCell position() {
        return directedPositions().head().position();
    }

    public Direction direction() {
        return directedPositions().head().direction();
    }

    public int maxBombs() {
        return this.maxBombs;
    }

    /**
     * Returns a new player identical to the calling player except with a
     * different maximum number of bombs
     * 
     * @param newMaxBombs
     *            the maximum number of bombs the new player has
     * @return the new player with the maximum number of bombs
     */
    public Player withMaxBombs(int newMaxBombs) {
        return new Player(this.id(), this.lifeStates(),
                this.directedPositions(), newMaxBombs, this.bombRange(), this.maxBombs());
    }

    public int bombRange() {
        return this.bombRange;
    }

    /**
     * Returns a new player identical to the calling player except with a
     * different bomb range
     * 
     * @param the
     *            bomb range newBombRange of the new player
     * @returna new player identical to the calling player except with a
     *          different bomb range
     */
    public Player withBombRange(int newBombRange) {
        return new Player(this.id(), this.lifeStates(),
                this.directedPositions(), this.maxBombs(), newBombRange, this.speedFactor());
    }
    
    public int speedFactor(){
        return this.speedFactor;
    }

    public Player withSpeedFactor(int speedFactor) {
        if (speedFactor > 2) {
            throw new IllegalArgumentException();
        }
        return new Player(this.id(), this.lifeStates(),
                this.directedPositions(), this.maxBombs(), this.bombRange(),
                speedFactor);
    }

    /**
     * Returns a new bomb with the position of the calling player
     * 
     * @return a new bomb with the position of the calling player
     */
    public Bomb newBomb() {
        return new Bomb(this.id(), this.position().containingCell(),
                Ticks.BOMB_FUSE_TICKS, this.bombRange());
    }

    /**
     * Concatenates a sequence of invulnerable and vulnerable life states if the
     * player is not dead
     * 
     * @param number
     *            of lives
     * @return the concatenated sequence
     */
    private static Sq<LifeState> newLifeStates(int lives) {
        if (lives == 0) {
            return Sq.constant(new LifeState(0, State.DEAD));
        }

        Sq<LifeState> a = Sq.constant(new LifeState(lives, State.INVULNERABLE))
                .limit(Ticks.PLAYER_INVULNERABLE_TICKS);
        Sq<LifeState> b = Sq.constant(new LifeState(lives, State.VULNERABLE));
        return a.concat(b);
    }

    /**
     * Returns the sequence of the directed positions of when the player has
     * stopped and looking south
     * 
     * @param position
     *            of the player
     * @return the sequence of the directed positions of when the player has
     *         stopped and looking south
     */
    private static Sq<DirectedPosition> directedPos(Cell position) {
        return DirectedPosition.stopped(new DirectedPosition(SubCell
                .centralSubCellOf(position), Direction.S));
    }

    public static final class LifeState {
        private final int lives;
        private final State state;

        /**
         * Constructs a life state with a number of lives and a state
         * 
         * @param lives
         * @param state
         *            throws an IllegalArgumentException if lives is negative
         *            and a NullPointerException if state is null
         */
        public LifeState(int lives, State state) {
            this.lives = ArgumentChecker.requireNonNegative(lives);
            this.state = Objects.requireNonNull(state);
        }

        public int lives() {
            return lives;
        }

        public State state() {
            return state;
        }

        public boolean canMove() {
            return (state == State.INVULNERABLE || state == State.VULNERABLE);
        }

        public enum State {
            INVULNERABLE, VULNERABLE, DYING, DEAD;
        }
    }

    public static final class DirectedPosition {
        private final SubCell position;
        private final Direction direction;

        /**
         * Constructs a new directed position
         * 
         * @param position
         * @param direction
         *            throws an nullPointerException if position or the
         *            direction is null
         */
        public DirectedPosition(SubCell position, Direction direction) {
            this.position = Objects.requireNonNull(position);
            this.direction = Objects.requireNonNull(direction);
        }

        /**
         * Returns an infinite sequence composed only of a given directed
         * position and represents a player stopped at that position
         * 
         * @param the
         *            directed position p
         * @return an infinite sequence composed only of a given directed
         *         position and represents a player stopped at that position
         */

        public static Sq<DirectedPosition> stopped(DirectedPosition p) {
            return Sq.constant(p);
        }

        /**
         * Returns an infinite sequence of directed positions representing a
         * player moving in the direction in which he is looking
         * 
         * @param Directed
         *            position p
         * @return an infinite sequence of directed positions representing a
         *         player moving in the direction in which he is looking
         */
        public static Sq<DirectedPosition> moving(DirectedPosition p) {
            return Sq.iterate(p, c -> new DirectedPosition(c.position()
                    .neighbor(p.direction()), p.direction()));
        }

        public SubCell position() {
            return position;
        }

        public DirectedPosition withPosition(SubCell newPosition) {
            return new DirectedPosition(newPosition, this.direction);
        }

        public Direction direction() {
            return this.direction;
        }

        public DirectedPosition withDirection(Direction newDirection) {
            return new DirectedPosition(this.position, newDirection);
        }

    }
}
