package ch.epfl.xblast;

/**
 * A subcell 209 rows and 241 columns of subcells in a board
 * 
 * @author Gabriel Hayat (246239)
 * @author Adrian Guerra Alejos (246225)
 */

public final class SubCell {
    private final int x, y;
    private final static int SUBCELLS = 16;

    /**
     * Constructs a subcell with given coordinates
     * 
     * @param x
     *            column number of the subcell (modulo the number of columns in
     *            the cell)
     * @param y
     *            row number of the subcell (modulo the number of rows in the
     *            cell)
     */

    public SubCell(int x, int y) {
        this.x = Math.floorMod(x, SUBCELLS * Cell.COLUMNS);
        this.y = Math.floorMod(y, SUBCELLS * Cell.ROWS);
    }

    /**
     * Returns the normalized x-coordinate of the subcell
     * 
     * @return the normalized x-coordinate of the subcell
     */
    public int x() {
        return this.x;
    }

    /**
     * Returns the normalized y-coordinate of the subcell
     * 
     * @return the normalized y-coordinate of the subcell
     */
    public int y() {
        return this.y;
    }

    /**
     * Returns the central subcell of a cell given in parameter
     * 
     * @param cell
     *            A cell for which you want to find its central subcell
     * @return The central subcell of the parameter
     */
    public static SubCell centralSubCellOf(Cell cell) {
        return new SubCell(cell.x() * SUBCELLS + (SUBCELLS / 2), cell.y()
                * SUBCELLS + (SUBCELLS / 2));
    }

    /**
     * Returns the distance between the calling subcell and the central subcell
     * of the cell
     * 
     * @return the distance between the calling subcell and the central subcell
     *         of the cell
     */

    public int distanceToCentral() {
        int h = this.x - centralSubCellOf(this.containingCell()).x;
        int k = this.y - centralSubCellOf(this.containingCell()).y;

        return Math.abs(h) + Math.abs(k);
    }

    /**
     * Returns true if the subcell is a central subcell
     * 
     * @return true if the subcell is a central subcell
     */
    public boolean isCentral() {
        return (distanceToCentral() == 0);
    }

    /**
     * 
     * @param dir
     *            Direction towards which the neighbor of the subcell will be
     * @return the neighboring subcell in the direction of the parameter
     */

    public SubCell neighbor(Direction d) {
        switch (d) {
        case N:
            return new SubCell(this.x, this.y - 1);

        case S:
            return new SubCell(this.x, this.y + 1);

        case E:
            return new SubCell(this.x + 1, this.y);

        case W:
            return new SubCell(this.x - 1, this.y);

        default:
            return null;
        }
    }

    /**
     * Returns the cell that contains the calling subcell
     * 
     * @return the cell that contains the calling subcell
     */
    public Cell containingCell() {
        int a = Math.floorDiv(this.x(), SUBCELLS);
        int b = Math.floorDiv(this.y(), SUBCELLS);
        return new Cell(a, b);
    }

    /**
     * Redefinition of the equals method of the class Object Determines whether
     * two cells are the same
     * 
     * @param Cell
     *            that will be compared
     * @return true if both are a cell and they have the same x and y
     *         coordinates
     */

    @Override
    public boolean equals(Object that) {
        return (that instanceof SubCell && this.x == ((SubCell) that).x && this.y == ((SubCell) that).y);
    }

    /**
     * Returns the rowMajorIndex of a SubCell
     * 
     * @return rowMajorIndex
     */
    @Override
    public int hashCode() {
        return y() * (SUBCELLS * Cell.COLUMNS + 1) + x();
    }

    /**
     * Redefinition of the method toString of the class Object
     * 
     * @return a string with the x and y coordinates
     */
    @Override
    public String toString() {
        String s = "(" + this.x() + "," + this.y() + ")";
        return s;
    }

}
