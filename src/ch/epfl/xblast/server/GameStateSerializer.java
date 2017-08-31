package ch.epfl.xblast.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.Direction;
import ch.epfl.xblast.RunLengthEncoder;

/**
 * GameStateSerializer for the serialized version of the game state under the
 * form of an encoded byte list
 * 
 * @author Gabriel Hayat (246239)
 * @author Adrian Guerra Alejos (246225)
 */

public final class GameStateSerializer {

    GameStateSerializer() {
    };

    /**
     * Returns the serialized version of the game state under the form of an
     * encoded byte list
     * 
     * @param bp
     *            the BoardPainter associating an image to every block of the
     *            board
     * @param gs
     *            the game state
     * @return the serialized version of the game state under the form of an
     *         encoded byte list
     */
    public static List<Byte> serialize(BoardPainter bp, GameState gs) {
        List<Byte> board = new ArrayList<>();
        for (Cell c : Cell.SPIRAL_ORDER) {
            board.add(bp.byteForCell(gs.board(), c));
        }

        List<Byte> result = new ArrayList<>();

        List<Byte> encodedBoard = RunLengthEncoder.encode(board);
        result.add((byte) (encodedBoard.size()));
        result.addAll(encodedBoard);

        List<Byte> bombs = new ArrayList<>();

        for (Cell c : Cell.ROW_MAJOR_ORDER) {
            if (gs.bombedCells().containsKey(c)
                    && !gs.blastedCells().contains(c)) {
                Bomb bo = gs.bombedCells().get(c);
                byte b = ExplosionPainter.byteForBomb(bo);
                bombs.add(b);

            } else if (gs.blastedCells().contains(c)
                    && gs.board().blockAt(c).isFree()) {
                boolean w = gs.blastedCells().contains(c.neighbor(Direction.W));
                boolean s = gs.blastedCells().contains(c.neighbor(Direction.S));
                boolean e = gs.blastedCells().contains(c.neighbor(Direction.E));
                boolean n = gs.blastedCells().contains(c.neighbor(Direction.N));

                bombs.add(ExplosionPainter.byteForBlast(w, s, e, n));
            } else {
                bombs.add(ExplosionPainter.BYTE_FOR_EMPTY);
            }
        }
        List<Byte> encodedBombs = RunLengthEncoder.encode(bombs);

        result.add((byte) (encodedBombs.size()));
        result.addAll(encodedBombs);

        List<Byte> players = new ArrayList<>();

        for (Player p : gs.players())
            players.addAll(Arrays.asList((byte) p.lives(), (byte) p.position()
                    .x(), (byte) p.position().y(), PlayerPainter.byteForPlayer(
                    gs.ticks(), p)));

        result.addAll(new ArrayList<>(players));

        double remainingTime = Math.ceil(gs.remainingTime() / 2);
        result.add((byte) remainingTime);

        return Collections.unmodifiableList(result);
    }
}
