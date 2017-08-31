package ch.epfl.xblast.server;

import java.util.Map;
import java.util.Objects;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Direction;

/**
 * BoardPainter This class enables the painting of the board
 * 
 * @author Gabriel Hayat (246239)
 * @author Adrian Guerra Alejos (246225)
 */
public final class BoardPainter {
    private final Map<Block, BlockImage> palette;
    private final BlockImage shadowedImage;

    /**
     * Constructs a board painter that has a map associating each block of the
     * board to its corresponding block image and a block image for the shadowed
     * images
     * 
     * @param palette
     * @param shadowedImage
     */
    public BoardPainter(Map<Block, BlockImage> palette, BlockImage shadowedImage) {
        this.palette = Objects.requireNonNull(palette);
        this.shadowedImage = Objects.requireNonNull(shadowedImage);
    }

    /**
     * Returns the byte identifying the image that must be used for the block at
     * a given cell of the board
     * 
     * @param board
     * @param cell
     * @return byte identifying the image that mused be used at a given cell
     */
    public byte byteForCell(Board board, Cell cell) {
        return (byte) (isShadowed(board, cell) ? shadowedImage.ordinal()
                : palette.get(board.blockAt(cell)).ordinal());

    }

    /**
     * Determines if a cell is shadowed or not
     * 
     * @param board
     * @param cell
     * @return true if the cell is shadowed, no otherwise.
     */
    public static boolean isShadowed(Board board, Cell cell) {
        try {
            return board.blockAt(cell.neighbor(Direction.W)).castsShadow()
                    && board.blockAt(cell).isFree();
        } catch (Exception e) {
            return false;
        }
    }

}
