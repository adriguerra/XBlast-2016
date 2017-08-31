package ch.epfl.xblast.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Lists;

/**
 * The board. Immutable class
 * 
 * @author Gabriel Hayat (246239)
 * @author Adrian Guerra Alejos (246225)
 */

public final class Board {

    private final List<Sq<Block>> blocks;
    private final static int INTERNAL_COLUMNS = Cell.COLUMNS - 2;
    private final static int INTERNAL_ROWS = Cell.ROWS - 2;
    private final static int QUADRANT_COLUMNS = 7;
    private final static int QUADRANT_ROWS = 6;

    /**
     * Constructs a board based on a given list of sequences of blocks
     * 
     * @param list
     *            of sequences of blocks
     * @throws IllegalArgumentException
     *             if the number of blocks isn't 195
     */

    public Board(List<Sq<Block>> blocks) throws IllegalArgumentException {
        if (blocks.size() != Cell.ROWS * Cell.COLUMNS) {
            throw new IllegalArgumentException();
        }
        this.blocks = Collections.unmodifiableList(new ArrayList<>(blocks));
    }

    /**
     * Returns a board from a given matrix of blocks
     * 
     * @param matrix
     *            rows
     * @return the board from a given matrix of blocks
     * @throws IllegalArgumentException
     *             if the size of the matrix isn't correct
     * 
     */

    public static Board ofRows(List<List<Block>> rows)
            throws IllegalArgumentException {
        checkBlockMatrix(rows, Cell.ROWS, Cell.COLUMNS);

        List<Sq<Block>> temp = new ArrayList<>();

        for (int i = 0; i < rows.size(); i++) {
            for (int j = 0; j < rows.get(0).size(); j++) {
                Sq<Block> s = Sq.constant(rows.get(i).get(j));
                temp.add(s);
            }
        }
        return new Board(temp);
    }

    /**
     * Returns a board surrounded by indestructible walls from a given matrix of
     * blocks
     * 
     * @param matrix
     *            innerBlocks of blocks to be surrounded
     * @return the board surrounded by indestructible walls from a given matrix
     *         of blocks
     * @throws IllegalArgumentException
     *             if the number of inner blocks isn't correct
     */

    public static Board ofInnerBlocksWalled(List<List<Block>> innerBlocks)
            throws IllegalArgumentException {
        checkBlockMatrix(innerBlocks, INTERNAL_ROWS, INTERNAL_COLUMNS);

        List<Sq<Block>> temp = new ArrayList<>();

        for (int i = 0; i < innerBlocks.size() + 2; i++) {
            for (int j = 0; j < innerBlocks.get(0).size() + 2; j++) {
                if (i == 0 || i == innerBlocks.size() + 1 || j == 0
                        || j == innerBlocks.get(0).size() + 1) {
                    temp.add(Sq.constant(Block.INDESTRUCTIBLE_WALL));
                } else {
                    Sq<Block> s = Sq
                            .constant(innerBlocks.get(i - 1).get(j - 1));
                    temp.add(s);
                }
            }
        }
        return new Board(temp);
    }

    /**
     * Returns a symmetric board surrounded by indestructible walls which the
     * North West Quadrant onto the North East quadrant and then mirrors the
     * North half onto the South half
     * 
     * @param quadrantNWBlocks
     *            A matrix of blocks to mirror and then to surround by walls
     * @return the symmetric board surrounded by indestructible walls
     */

    public static Board ofQuadrantNWBlocksWalled(
            List<List<Block>> quadrantNWBlocks) {
        checkBlockMatrix(quadrantNWBlocks, QUADRANT_ROWS, QUADRANT_COLUMNS);

        List<List<Block>> temp = new ArrayList<>();

        for (int i = 0; i < quadrantNWBlocks.size(); i++) {
            temp.add(Lists.mirrored(quadrantNWBlocks.get(i)));
        }

        return Board.ofInnerBlocksWalled(Lists.mirrored(temp));
    }

    /**
     * Returns the sequence of blocks at a given cell
     * 
     * @param Cell
     *            c
     * @return the sequence of blocks
     */

    public Sq<Block> blocksAt(Cell c) {
        return this.blocks.get(c.rowMajorIndex());
    }

    /**
     * Returns the current block at a given cell
     * 
     * @param Cell
     *            c
     * @return the block
     */

    public Block blockAt(Cell c) {
        return blocksAt(c).head();
    }

    /**
     * Checks if a given matrix' size corresponds to the number of rows and
     * number of columns specified
     * 
     * @param matrix
     *            to check size
     * @param number
     *            of rows wanted
     * @param number
     *            of columns wanted
     * @throws IllegalArgumentException
     *             if the matrix isn't squared
     */

    private static void checkBlockMatrix(List<List<Block>> matrix, int rows,
            int columns) {
        if (matrix == null || matrix.size() == 0 || matrix.size() != rows) {
            throw new IllegalArgumentException();
        }

        for (int i = 0; i < rows; i++) // for-each loop not appropriate as we
                                       // want
                                       // to make sure the matrix is squared
        {
            if (matrix.get(i).size() != columns) {
                throw new IllegalArgumentException();
            }
        }
    }

}
