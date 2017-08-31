package ch.epfl.xblast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A cell
 * 
 * @author Gabriel Hayat (246239)
 * @author Adrian Guerra Alejos (246225)
 */

public final class Cell {
    public static final int COLUMNS = 15;
    public static final int ROWS = 13;
    public static final int COUNT = 15 * 13;
    private final int x, y;

    public static final List<Cell> ROW_MAJOR_ORDER = Collections
            .unmodifiableList(rowMajorOrder());

    public static final List<Cell> SPIRAL_ORDER = Collections
            .unmodifiableList(spiralOrder());

    /**
     * Constructs a cell with given coordinates
     * 
     * @param x
     *            column number of the cell (modulo the number of columns)
     * @param y
     *            row number of the cell (modulo the number of rows)
     */

    public Cell(int x, int y) {
        this.x = Math.floorMod(x, COLUMNS);
        this.y = Math.floorMod(y, ROWS);
    }

    /**
     * Returns the normalized x-coordinate of the cell
     * 
     * @return the normalized x-coordinate of the cell
     */
    public int x() {
        return this.x;
    }

    /**
     * Returns the normalized y-coordinate of the cell
     * 
     * @return the normalized y-coordinate of the cell
     */
    public int y() {
        return this.y;
    }

    /**
     * Returns the cell's index in the row major order
     * 
     * @return the cell's index in the row major order
     */
    public int rowMajorIndex() {
        return y() * COLUMNS + x();
    }

    /**
     * 
     * @param dir
     *            Direction towards which the neighbor will be
     * @return the neighboring cell in the direction of the parameter
     */
    public Cell neighbor(Direction dir) {
        switch (dir) {
        case N:
            return new Cell(this.x, this.y - 1);

        case S:
            return new Cell(this.x, this.y + 1);

        case E:
            return new Cell(this.x + 1, this.y);

        case W:
            return new Cell(this.x - 1, this.y);

        default:
            return null;
        }
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
        return (that instanceof Cell && this.x == ((Cell) that).x && this.y == ((Cell) that).y);
    }

    /**
     * Returns the rowMajorIndex as hashCode
     * 
     * @return rowMajorIndex
     */
    @Override
    public int hashCode() {
        return rowMajorIndex();
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

    /**
     * Returns an array of the entire grid ordered in the row major order
     * 
     * @return an array of the entire grid ordered in the row major order
     */
    private static ArrayList<Cell> rowMajorOrder() {
        ArrayList<Cell> temp = new ArrayList<Cell>();

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                temp.add(new Cell(j, i));
            }
        }
        return temp;
    }

    /**
     * Returns an array of the entire grid ordered in the spiral order
     * 
     * @return an array of the entire grid ordered in the spiral order
     */

    private static ArrayList<Cell> spiralOrder() {
        ArrayList<Integer> ix = new ArrayList<Integer>();
        ArrayList<Integer> iy = new ArrayList<Integer>();
        ArrayList<Cell> spiral = new ArrayList<Cell>();

        boolean horizontal = true;
        ArrayList<Integer> i1;
        ArrayList<Integer> i2;
        int c2;

        for (int i = 0; i < ROWS; i++) {
            ix.add(i);
        }

        for (int j = 0; j < COLUMNS; j++) {
            iy.add(j);
        }

        while (!(ix.isEmpty() || iy.isEmpty())) {
            if (!horizontal) {
                i1 = ix;
                i2 = iy;
            } else {
                i1 = iy;
                i2 = ix;
            }

            c2 = i2.get(0);
            i2.remove(0);

            for (int i = 0; i < i1.size(); i++) {
                if (horizontal) {
                    spiral.add(new Cell(i1.get(i), c2));
                } else {
                    spiral.add(new Cell(c2, i1.get(i)));
                }
            }
            Collections.reverse(i1);
            horizontal = !horizontal;
        }
        return spiral;
    }
}
