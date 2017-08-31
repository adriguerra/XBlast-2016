package ch.epfl.xblast.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.ArgumentChecker;
import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Direction;
import ch.epfl.xblast.PlayerID;

/**
 * Bomb
 * 
 * @author Gabriel Hayat (246239)
 * @author Adrian Guerra Alejos (246225)
 */

public final class Bomb {

    private final PlayerID ownerId;
    private final Cell position;
    private final Sq<Integer> fuseLengths;
    private final int range;

    /**
     * Constructs a bomb with the following parameters:
     * 
     * @param ownerId
     * @param position
     * @param fuseLengths
     * @param range
     *            Throws a nullPointerException if one of the first three
     *            arguments is null or an IllegalArgumentException if the
     *            sequence of integers of fuseLengths is empty or the range is
     *            negative
     */

    public Bomb(PlayerID ownerId, Cell position, Sq<Integer> fuseLengths,
            int range) {
        this.ownerId = Objects.requireNonNull(ownerId);
        this.position = Objects.requireNonNull(position);
        this.fuseLengths = Objects.requireNonNull(fuseLengths);
        if (fuseLengths.isEmpty())
            throw new IllegalArgumentException();
        this.range = ArgumentChecker.requireNonNegative(range);
    }

    /**
     * Constructs a bomb with the following parameters:
     * 
     * @param ownerId
     * @param position
     * @param fuseLength
     * @param range
     *            Computes the fuseLengths sequence of integers from the int
     *            fuseLength parameter and calls the first constructor
     */

    public Bomb(PlayerID ownerId, Cell position, int fuseLength, int range) {
        this(ownerId, position, Sq.iterate(fuseLength, u -> u - 1).limit(
                fuseLength), range);
    }

    public PlayerID ownerId() {
        return ownerId;
    }

    public Cell position() {
        return position;
    }

    public Sq<Integer> fuseLengths() {
        return fuseLengths;
    }

    public int fuseLength() {
        return fuseLengths().head();
    }

    public int range() {
        return range;
    }

    /**
     * Returns the 4 arms of the explosion in the form of a list of a sequence
     * of sequence of cells
     * 
     * @return the 4 arms of the explosion in the form of a list of a sequence
     *         of sequence of cells
     */

    public List<Sq<Sq<Cell>>> explosion() {
        ArrayList<Sq<Sq<Cell>>> temp = new ArrayList<>();
        for(Direction d : Direction.values()){
            temp.add(explosionArmTowards(d));
        }
        return Collections.unmodifiableList(temp);
    }

    /**
     * Returns the arm of the explosion in the direction of the given parameter
     * in the form of a sequence of sequence of cells
     * 
     * @param Direction
     *            dir at which the arm is sent
     * @return the arm of the explosion in the direction of the given parameter
     *         in the form of a sequence of sequence of cells
     */

    public Sq<Sq<Cell>> explosionArmTowards(Direction dir) {
        return Sq.repeat(Ticks.EXPLOSION_TICKS,
                Sq.iterate(this.position(), c -> c.neighbor(dir)).limit(range));
    }

}
