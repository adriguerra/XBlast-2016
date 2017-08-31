package ch.epfl.xblast.testspersonnels;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import ch.epfl.cs108.Sq;
import ch.epfl.xblast.Cell;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.RunLengthEncoder;
import ch.epfl.xblast.server.Block;
import ch.epfl.xblast.server.Board;
import ch.epfl.xblast.server.Bomb;
import ch.epfl.xblast.server.GameState;
import ch.epfl.xblast.server.Player;

public class Etape10Test {
    
    @Test
    public void EncodeDecodeTest() {
        List<Byte> test = new ArrayList<>(Arrays.asList((byte) 10, (byte) 20,
                (byte) 30, (byte) 10, (byte) 10, (byte) 40, (byte) 40,
                (byte) 40, (byte) 40, (byte) 40, (byte) 40, (byte) 40,
                (byte) 10));
        for (int i = 0; i < 265; i++) {
            test.add((byte) 3);
        }

        List<Byte> actual = RunLengthEncoder.decode(RunLengthEncoder.encode(test));
        assertTrue(actual.size() == test.size());
        for (int i = 0; i < actual.size(); i++) {
            assertTrue(test.get(i) == actual.get(i));
        }
    }
    
//    private static GameState createGSwithBombs() {
//        List<List<Block>> blocks0 = new ArrayList<>();
//        List<Sq<Block>> block = new ArrayList<>();
//
//        blocks0.add(Arrays.asList(Block.FREE, Block.FREE, Block.FREE,
//                Block.FREE, Block.FREE, Block.DESTRUCTIBLE_WALL, Block.FREE));
//        blocks0.add(Arrays.asList(Block.FREE, Block.INDESTRUCTIBLE_WALL,
//                Block.DESTRUCTIBLE_WALL, Block.INDESTRUCTIBLE_WALL,
//                Block.DESTRUCTIBLE_WALL, Block.INDESTRUCTIBLE_WALL,
//                Block.DESTRUCTIBLE_WALL));
//        blocks0.add(Arrays.asList(Block.FREE, Block.DESTRUCTIBLE_WALL,
//                Block.FREE, Block.FREE, Block.FREE, Block.DESTRUCTIBLE_WALL,
//                Block.FREE));
//        blocks0.add(Arrays.asList(Block.DESTRUCTIBLE_WALL,
//                Block.INDESTRUCTIBLE_WALL, Block.FREE,
//                Block.INDESTRUCTIBLE_WALL, Block.INDESTRUCTIBLE_WALL,
//                Block.INDESTRUCTIBLE_WALL, Block.INDESTRUCTIBLE_WALL));
//        blocks0.add(Arrays.asList(Block.FREE, Block.DESTRUCTIBLE_WALL,
//                Block.FREE, Block.DESTRUCTIBLE_WALL, Block.FREE, Block.FREE,
//                Block.FREE));
//        blocks0.add(Arrays.asList(Block.DESTRUCTIBLE_WALL,
//                Block.INDESTRUCTIBLE_WALL, Block.DESTRUCTIBLE_WALL,
//                Block.INDESTRUCTIBLE_WALL, Block.DESTRUCTIBLE_WALL,
//                Block.INDESTRUCTIBLE_WALL, Block.FREE));
//
//        Board board0 = Board.ofQuadrantNWBlocksWalled(blocks0);
//
//        List<Player> players = new ArrayList<>();
//
//        players.add(new Player(PlayerID.PLAYER_1, 3, new Cell(1, 1), 2, 5));
//        players.add(new Player(PlayerID.PLAYER_2, 3, new Cell(13, 1), 2, 5));
//        players.add(new Player(PlayerID.PLAYER_3, 3, new Cell(13, 11), 2, 5));
//        players.add(new Player(PlayerID.PLAYER_4, 3, new Cell(1, 11), 2, 5));
//
//        List<Bomb> bombs = new ArrayList<>();
//        bombs.add(new Bomb(PlayerID.PLAYER_1, new Cell(4, 5), 3, 4));
//
//        List<Sq<Sq<Cell>>> explosions = new ArrayList<>();
//        List<Sq<Cell>> blasts = new ArrayList<>();
//        blasts.add(Sq.repeat(3, (new Cell(3, 3))));
//        explosions.add(Sq.repeat(4, blasts.get(0)));
//
//        return new GameState(120, board0, players, bombs, explosions, blasts);
//    }
}
