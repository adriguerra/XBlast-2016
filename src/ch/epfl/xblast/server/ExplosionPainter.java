package ch.epfl.xblast.server;

import ch.epfl.xblast.ArgumentChecker;

/**
 * ExplosionPainter This class allows the painting of explosions
 * 
 * @author Gabriel Hayat (246239)
 * @author Adrian Guerra Alejos (246225)
 */
public final class ExplosionPainter {
    public static final byte BYTE_FOR_EMPTY = 16;
    public static final byte BOMB = 20;
    public static final byte WHITE_BOMB = 21;
    public static final byte SHIFT_BY_THREE = 3, SHIFT_BY_TWO = 2,
            SHIFT_BY_ONE = 1, NO_SHIFT = 0;

    private ExplosionPainter() {
    }

    /**
     * Returns a byte identifying the image of the bomb
     * 
     * @param bomb
     * @return a byte identifying the image of the bomb
     */
    public static byte byteForBomb(Bomb bomb) {
        return (byte) (Integer.bitCount(bomb.fuseLength()) == 1 ? WHITE_BOMB
                : BOMB);
    }

    /**
     * Returns the byte identifying the blast's image needed at each cell
     * depending on the cell's neighbors, expressed as booleans that are true if
     * a blast is indeed present in that direction
     * 
     * @param w
     *            West boolean
     * @param s
     *            South boolean
     * @param e
     *            East boolean
     * @param n
     *            North boolean
     * @return the byte identifying the blast's image
     */
    public static byte byteForBlast(boolean w, boolean s, boolean e, boolean n) {
        return (byte) (booleanToBit(n, SHIFT_BY_THREE) | booleanToBit(e, SHIFT_BY_TWO)
                | booleanToBit(s, SHIFT_BY_ONE) | booleanToBit(w, NO_SHIFT));
    }

    /**
     * Converts a boolean to a bit and shifts it to the left by the number given
     * 
     * @param dir
     *            the boolean converted into a bit
     * @param pos
     *            the number of positions the number is shifted
     * @return
     */
    public static int booleanToBit(boolean dir, int pos) {
        ArgumentChecker.requireNonNegative(pos);
        int n = dir ? 1 : 0;
        n = n << pos;
        return n;
    }
}
