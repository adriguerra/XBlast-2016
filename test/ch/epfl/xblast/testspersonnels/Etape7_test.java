///*
//    Author: Gabriel Hayat
//    Date: 18 avr. 2016
//
// */
//
//package ch.epfl.xblast.testspersonnels;
//
//import static org.junit.Assert.assertEquals;
//
//import java.io.ByteArrayInputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.junit.Test;
//
//import ch.epfl.cs108.Sq;
//import ch.epfl.xblast.Cell;
//import ch.epfl.xblast.Direction;
//import ch.epfl.xblast.PlayerID;
//import ch.epfl.xblast.SubCell;
//import ch.epfl.xblast.server.Block;
//import ch.epfl.xblast.server.BlockImage;
//import ch.epfl.xblast.server.Board;
//import ch.epfl.xblast.server.BoardPainter;
//import ch.epfl.xblast.server.Bomb;
//import ch.epfl.xblast.server.ExplosionPainter;
//import ch.epfl.xblast.server.GameState;
//import ch.epfl.xblast.server.Level;
//import ch.epfl.xblast.server.Player;
//import ch.epfl.xblast.server.Player.DirectedPosition;
//import ch.epfl.xblast.server.Player.LifeState;
//import ch.epfl.xblast.server.Player.LifeState.State;
//import ch.epfl.xblast.server.debug.GameStatePrinter;
//
//public class Etape7_test {
//
//    @Test
//    public void BoardPainterTest() throws IOException {
//
//        List<Sq<Block>> blocks = Arrays.asList(Sq.constant(Block.BONUS_BOMB),
//                Sq.constant(Block.CRUMBLING_WALL),
//                Sq.constant(Block.BONUS_RANGE),
//                Sq.constant(Block.DESTRUCTIBLE_WALL),
//                Sq.constant(Block.DESTRUCTIBLE_WALL), Sq.constant(Block.FREE),
//                Sq.constant(Block.FREE));
//        Board b = new Board(blocks);
//        Map<Block, BlockImage> palette = new HashMap<>();
//
//        palette.put(Block.BONUS_BOMB, BlockImage.BONUS_BOMB);
//        palette.put(Block.FREE, BlockImage.IRON_FLOOR);
//        palette.put(Block.CRUMBLING_WALL, BlockImage.EXTRA_O);
//        palette.put(Block.BONUS_RANGE, BlockImage.BONUS_RANGE);
//        palette.put(Block.DESTRUCTIBLE_WALL, BlockImage.EXTRA);
//
//        BoardPainter bp = new BoardPainter(palette, BlockImage.IRON_FLOOR_S);
//
//        byte[] expected = new byte[] { 005, 004, 006, 003, 003, 001, 000 };
//        InputStream s = new ByteArrayInputStream(expected);
//
//        for (int i = 0; i < 7; i++)
//            assertEquals(s.read(), bp.byteForCell(b, new Cell(i, 0)));
//
//        s.close();
//
//    }
//
//    // decommenter dans el constructeur board
//    // exception
//
//    @Test
//    public void ExplisionPainter() {
//
//        Bomb b1 = new Bomb(PlayerID.PLAYER_1, new Cell(0, 0), 4, 4);
//        Bomb b2 = new Bomb(PlayerID.PLAYER_1, new Cell(0, 0), 1024, 4);
//        Bomb b3 = new Bomb(PlayerID.PLAYER_1, new Cell(0, 0), 5, 4);
//
//        assertEquals((byte) 021, ExplosionPainter.byteForBomb(b1));
//        assertEquals((byte) 021, ExplosionPainter.byteForBomb(b2));
//        assertEquals((byte) 020, ExplosionPainter.byteForBomb(b3));
//
//        assertEquals(15, ExplosionPainter.byteForBlast(true, true, true, true));
//        assertEquals(00,
//                ExplosionPainter.byteForBlast(false, false, false, false));
//        assertEquals(12,
//                ExplosionPainter.byteForBlast(false, false, true, true));
//        assertEquals(05,
//                ExplosionPainter.byteForBlast(true, false, true, false));
//    }
//
//    @Test
//    public void PlayerPainter() {
//
//        Player p1 = new Player(PlayerID.PLAYER_1, Sq.constant(new LifeState(2,
//                State.INVULNERABLE)), Sq.constant(new DirectedPosition(
//                new SubCell(0, 0), Direction.E)), 2, 2);
//        Player p2 = new Player(PlayerID.PLAYER_2, Sq.constant(new LifeState(0,
//                State.DEAD)), Sq.constant(new DirectedPosition(
//                new SubCell(3, 0), Direction.E)), 2, 2);
//        Player p3 = new Player(PlayerID.PLAYER_3, Sq.constant(new LifeState(2,
//                State.DYING)), Sq.constant(new DirectedPosition(new SubCell(3,
//                0), Direction.E)), 2, 2);
//        Player p4 = new Player(PlayerID.PLAYER_4, Sq.constant(new LifeState(2,
//                State.DEAD)), Sq.constant(new DirectedPosition(
//                new SubCell(0, 0), Direction.E)), 2, 2);
//        Player p9 = new Player(PlayerID.PLAYER_3, Sq.constant(new LifeState(1,
//                State.DYING)), Sq.constant(new DirectedPosition(new SubCell(3,
//                0), Direction.E)), 2, 2);
//
//        Player p5 = new Player(PlayerID.PLAYER_1, Sq.constant(new LifeState(2,
//                State.VULNERABLE)), Sq.constant(new DirectedPosition(
//                new SubCell(3, 0), Direction.E)), 2, 2);
//        Player p6 = new Player(PlayerID.PLAYER_2, Sq.constant(new LifeState(2,
//                State.VULNERABLE)), Sq.constant(new DirectedPosition(
//                new SubCell(0, 3), Direction.N)), 2, 2);
//        Player p7 = new Player(PlayerID.PLAYER_3, Sq.constant(new LifeState(2,
//                State.VULNERABLE)), Sq.constant(new DirectedPosition(
//                new SubCell(1, 0), Direction.W)), 2, 2);
//        Player p8 = new Player(PlayerID.PLAYER_4, Sq.constant(new LifeState(2,
//                State.VULNERABLE)), Sq.constant(new DirectedPosition(
//                new SubCell(0, 1), Direction.S)), 2, 2);
//
//        // test images normales
//        assertEquals(5,
//                ch.epfl.xblast.server.PlayerPainter.byteForPlayer(1, p5));
//        assertEquals(22,
//                ch.epfl.xblast.server.PlayerPainter.byteForPlayer(1, p6));
//        assertEquals(50,
//                ch.epfl.xblast.server.PlayerPainter.byteForPlayer(1, p7));
//        assertEquals(67,
//                ch.epfl.xblast.server.PlayerPainter.byteForPlayer(1, p8));
//
//        // test images invulnérables
//        assertEquals(83,
//                ch.epfl.xblast.server.PlayerPainter.byteForPlayer(1, p1));
//        assertEquals(3,
//                ch.epfl.xblast.server.PlayerPainter.byteForPlayer(2, p1));
//
//        // test images joueurs morts
//        // assertEquals(33, ch.epfl.xblast.server.PlayerPainter.byteForPlayer(1,
//        // p2));
//        assertEquals(52,
//                ch.epfl.xblast.server.PlayerPainter.byteForPlayer(1, p3));
//        assertEquals(53,
//                ch.epfl.xblast.server.PlayerPainter.byteForPlayer(1, p9));
//
//        // si il est mort, resultat += 13, pas 15;
//        // si el joueur est dead, pas d image!! une image seulement si il est
//        // dying, += 12 ou += 13 dependedant si il a plus d une vie
//        
//        List<Player> players = new ArrayList<>(); 
//        
//        players.add(new Player(PlayerID.PLAYER_1, 5, new Cell(1,1), 2, 5));
//        players.add(new Player(PlayerID.PLAYER_2, 5, new Cell(13,1), 2, 5));
//        players.add(new Player(PlayerID.PLAYER_3, 5, new Cell(1,11), 2, 5));
//        players.add(new Player(PlayerID.PLAYER_4, 5, new Cell(13,11), 2, 5));
//        
//        GameStatePrinter.printGameState(new GameState(Level.defaultGameState(), players));
//    }
//
//}