package ch.epfl.xblast;

/**
 * A direction
 * 
 * @author Gabriel Hayat (246239)
 * @author Adrian Guerra Alejos (246225)
 */
public enum Direction {
    N, E, S, W;

    /**
     * Returns the opposite direction of a given direction.
     * 
     * @return the opposite direction of a given direction.
     */
    public Direction opposite() {
        switch (this) {
        case N:
            return S;
        case E:
            return W;
        case S:
            return N;
        case W:
            return E;
        default:
            return null;
        }
    }

    /**
     * Returns true if East or West
     * 
     * @return true if East or West
     */
    public boolean isHorizontal() {
        return (this.equals(E) || this.equals(W));     
    }

    /**
     * Determines if a given direction is parallel to the parameter.
     * 
     * @param that
     *            another direction
     * @return true if direction is parallel to that
     */

    public boolean isParallelTo(Direction that) {
        return (this.equals(that) || this.equals(that.opposite()));
    }

}
