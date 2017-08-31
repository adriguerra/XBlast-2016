//package ch.epfl.xblast.testspersonnels;
//
//import static org.junit.Assert.assertEquals;
//
//import java.io.IOException;
//import java.net.URISyntaxException;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import org.junit.Test;
//
//import ch.epfl.cs108.Sq;
//import ch.epfl.xblast.Cell;
//import ch.epfl.xblast.PlayerID;
//import ch.epfl.xblast.RunLengthEncoder;
//import ch.epfl.xblast.client.GameState;
//import ch.epfl.xblast.client.GameStateDeserializer;
//import ch.epfl.xblast.server.Block;
//import ch.epfl.xblast.server.Board;
//import ch.epfl.xblast.server.GameStateSerializer;
//import ch.epfl.xblast.server.Level;
//import ch.epfl.xblast.server.Player;
//import ch.epfl.xblast.server.debug.GameStatePrinter;
//
//public class Etape8Test {
//
//    public static void main(String[] args) {
//        List<Byte> test = new ArrayList<>(Arrays.asList((byte) 10, (byte) 20,
//                (byte) 30, (byte) 10, (byte) 10, (byte) 40, (byte) 40,
//                (byte) 40, (byte) 40, (byte) 40, (byte) 40, (byte) 40,
//                (byte) 10));
//        
//        List<Integer> test2 = Arrays.asList(16, 16, 16, 16, 16, 16, 16, 16, 16,
//                16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16,
//                16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16,
//                16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16,
//                16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16,
//                16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16,
//                16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16,
//                16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16,
//                16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
//                16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16,
//                16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16,
//                16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16,
//                16, 16, 16, 16, 16, 16, 16, 16, 16, 16);
//
//        for (int i = 0; i < 136; i++) {
//            test.add((byte) 3);
//        }
//        test.add((byte) 4);
//        int b = 0;
//        
//        for (int i = 0; i < test2.size(); i++) {
//            if (test2.get(i) != 16) {
//                b = i;
//            }
//        }
//
//        List<Byte> actual2 = new ArrayList<>();
//
//        for (int i = 0; i < test2.size(); i++) {
//            if (i == b) {
//                actual2.add((byte) 20);
//            } else {
//                actual2.add((byte) 16);
//            }
//        }
//
//        List<Byte> encoded = RunLengthEncoder.encode(actual2);
//        System.out.println(b);
//        for (int i = 0; i < encoded.size(); i++) {
//            System.out.print(encoded.get(i) + " ");
//        }
//    }
//
//    @Test
//    public void boardInitial() {
//        List<Integer> expected = Arrays.asList(121, -50, 2, 1, -2, 0, 3, 1, 3,
//                1, -2, 0, 1, 1, 3, 1, 3, 1, 3, 1, 1, -2, 0, 1, 3, 1, 3, -2, 0,
//                -1, 1, 3, 1, 3, 1, 3, 1, 1, 2, 3, 2, 3, 2, 3, 2, 3, 2, 3, 2, 3,
//                2, 3, 2, 3, 2, 3, 2, 3, 2, 3, 2, 3, 2, 3, 2, 3, 2, 3, 2, 3, 2,
//                3, 2, 3, 1, 0, 0, 3, 1, 3, 1, 0, 0, 1, 1, 3, 1, 1, 0, 0, 1, 3,
//                1, 3, 0, 0, -1, 1, 3, 1, 1, -5, 2, 3, 2, 3, -5, 2, 3, 2, 3, 1,
//                -2, 0, 3, -2, 0, 1, 3, 2, 1, 2,
//
//                4, -128, 16, -63, 16,
//
//                3, 24, 24, 6, 3, -40, 24, 26, 3, -40, -72, 46, 3, 24, -72, 66,
//
//                60);
//
//        List<Byte> actual = GameStateSerializer.serialize(
//                Level.defaultBoardPainter(), Level.defaultGameState());
//
//        GameStatePrinter.printGameState(Level.defaultGameState());
//        System.out.print(expected.get(121) + " " + actual.get(121));
//        // assertEquals(expected.size(), actual.size());
//        for (int i = 1; i < actual.size(); i++) {
//            // System.out.print(i + " ");
//            assertEquals(expected.get(i), Integer.valueOf(actual.get(i)));
//        }
//    }
//
//    @Test
//    public void deserialize() throws URISyntaxException, IOException {
//        List<Integer> expected = Arrays.asList(121, -50, 2, 1, -2, 0, 3, 1, 3,
//                1, -2, 0, 1, 1, 3, 1, 3, 1, 3, 1, 1, -2, 0, 1, 3, 1, 3, -2, 0,
//                -1, 1, 3, 1, 3, 1, 3, 1, 1, 2, 3, 2, 3, 2, 3, 2, 3, 2, 3, 2, 3,
//                2, 3, 2, 3, 2, 3, 2, 3, 2, 3, 2, 3, 2, 3, 2, 3, 2, 3, 2, 3, 2,
//                3, 2, 3, 1, 0, 0, 3, 1, 3, 1, 0, 0, 1, 1, 3, 1, 1, 0, 0, 1, 3,
//                1, 3, 0, 0, -1, 1, 3, 1, 1, -5, 2, 3, 2, 3, -5, 2, 3, 2, 3, 1,
//                -2, 0, 3, -2, 0, 1, 3, 2, 1, 2,
//
//                4, -128, 16, -63, 16,
//
//                3, 24, 24, 6, 3, -40, 24, 26, 3, -40, -72, 46, 3, 24, -72, 66,
//
//                60);
//        List<Byte> serialized = GameStateSerializer.serialize(
//                Level.defaultBoardPainter(), Level.createGSwithBombs());
//        for (int i = 0; i < serialized.size(); i++) {
//            assertEquals(expected.get(i), Integer.valueOf(serialized.get(i)));
//        }
//        GameState gsClient = GameStateDeserializer
//                .deserializeGameState(serialized);
//
//    }
//}
