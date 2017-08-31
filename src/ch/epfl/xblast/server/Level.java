package ch.epfl.xblast.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.PlayerID;

/**
 * A game Level
 * 
 * @author Gabriel Hayat (246239)
 * @author Adrian Guerra Alejos (246225)
 */
public final class Level {
    private final BoardPainter bp;
    private final GameState gs;
    private static final int INITIAL_NUMBER_OF_LIVES = 3;
    private static final Cell PLAYER_1_INITIAL_POSITION = new Cell(1, 1),
            PLAYER_2_INITIAL_POSITION = new Cell(13, 1),
            PLAYER_3_INITIAL_POSITION = new Cell(13, 11),
            PLAYER_4_INITIAL_POSITION = new Cell(1, 11);
    private static final int INITIAL_MAXBOMBS = 2, INITIAL_BOMB_RANGE = 3;
    /**
     * The initial game state along with its board painter
     */
    public final static Level DEFAULT_LEVEL = new Level(defaultBoardPainter(),
            defaultGameState());

    public Level(BoardPainter bp, GameState gs) {
        this.bp = Objects.requireNonNull(bp);
        this.gs = Objects.requireNonNull(gs);
    }

    public BoardPainter getBoardPainter() {
        return bp;
    }

    public GameState getGameState() {
        return gs;
    }

    /**
     * Creates the default game state i.e. default board with the players at
     * their starting positions
     * 
     * @return the default game state
     */
    private static GameState defaultGameState() {
        List<List<Block>> blocks0 = new ArrayList<>();

        blocks0.add(Arrays.asList(Block.FREE, Block.FREE, Block.FREE,
                Block.FREE, Block.FREE, Block.DESTRUCTIBLE_WALL, Block.FREE));
        blocks0.add(Arrays.asList(Block.FREE, Block.INDESTRUCTIBLE_WALL,
                Block.DESTRUCTIBLE_WALL, Block.INDESTRUCTIBLE_WALL,
                Block.DESTRUCTIBLE_WALL, Block.INDESTRUCTIBLE_WALL,
                Block.DESTRUCTIBLE_WALL));
        blocks0.add(Arrays.asList(Block.FREE, Block.DESTRUCTIBLE_WALL,
                Block.FREE, Block.FREE, Block.FREE, Block.DESTRUCTIBLE_WALL,
                Block.FREE));
        blocks0.add(Arrays.asList(Block.DESTRUCTIBLE_WALL,
                Block.INDESTRUCTIBLE_WALL, Block.FREE,
                Block.INDESTRUCTIBLE_WALL, Block.INDESTRUCTIBLE_WALL,
                Block.INDESTRUCTIBLE_WALL, Block.INDESTRUCTIBLE_WALL));
        blocks0.add(Arrays.asList(Block.FREE, Block.DESTRUCTIBLE_WALL,
                Block.FREE, Block.DESTRUCTIBLE_WALL, Block.FREE, Block.FREE,
                Block.FREE));
        blocks0.add(Arrays.asList(Block.DESTRUCTIBLE_WALL,
                Block.INDESTRUCTIBLE_WALL, Block.DESTRUCTIBLE_WALL,
                Block.INDESTRUCTIBLE_WALL, Block.DESTRUCTIBLE_WALL,
                Block.INDESTRUCTIBLE_WALL, Block.FREE));

        Board board0 = Board.ofQuadrantNWBlocksWalled(blocks0);

        List<Player> players = new ArrayList<>();

        players.add(new Player(PlayerID.PLAYER_1, INITIAL_NUMBER_OF_LIVES,
                PLAYER_1_INITIAL_POSITION, INITIAL_MAXBOMBS, INITIAL_BOMB_RANGE));
        players.add(new Player(PlayerID.PLAYER_2, INITIAL_NUMBER_OF_LIVES,
                PLAYER_2_INITIAL_POSITION, INITIAL_MAXBOMBS, INITIAL_BOMB_RANGE));
        players.add(new Player(PlayerID.PLAYER_3, INITIAL_NUMBER_OF_LIVES,
                PLAYER_3_INITIAL_POSITION, INITIAL_MAXBOMBS, INITIAL_BOMB_RANGE));
        players.add(new Player(PlayerID.PLAYER_4, INITIAL_NUMBER_OF_LIVES,
                PLAYER_4_INITIAL_POSITION, INITIAL_MAXBOMBS, INITIAL_BOMB_RANGE));

        return new GameState(board0, players);
    }

    /**
     * Returns the default BoardPainter associating each block to an image
     * 
     * @return the default BoardPainter
     */
    private static BoardPainter defaultBoardPainter() {
        Map<Block, BlockImage> palette = new HashMap<>();

        palette.put(Block.BONUS_BOMB, BlockImage.BONUS_BOMB);
        palette.put(Block.FREE, BlockImage.IRON_FLOOR);
        palette.put(Block.CRUMBLING_WALL, BlockImage.EXTRA_O);
        palette.put(Block.BONUS_RANGE, BlockImage.BONUS_RANGE);
        palette.put(Block.DESTRUCTIBLE_WALL, BlockImage.EXTRA);
        palette.put(Block.INDESTRUCTIBLE_WALL, BlockImage.DARK_BLOCK);
        palette.put(Block.BONUS_SPEED, BlockImage.BONUS_SPEED);

        return new BoardPainter(palette, BlockImage.IRON_FLOOR_S);
    }
}
