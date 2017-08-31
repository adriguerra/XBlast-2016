package ch.epfl.xblast.client;

import java.awt.Image;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.SubCell;
import ch.epfl.xblast.client.GameState.Player;

/**
 * An uninstanciable class, allowing the deserialization of a gameState in the
 * form of a list
 * 
 * @author Gabriel Hayat (246239)
 * @author Adrian Guerra Alejos (246225)
 */
public final class GameStateDeserializer {

    private static final int NUMBER_OF_IMAGES_IN_TIMELINE = 60;
    private static final int VOID_TILE_SIZE = 8;
    private static final int NUMBER_OF_PLAYER_ATTRIBUTES = 16;
    private static final int VOID_TILE = 12;
    private static final int LED_OFF = 20;
    private static final int LED_ON = 21;
    private static final ImageCollection block = loadImageCollection("block");
    private static final ImageCollection explosion = loadImageCollection("explosion");
    private static final ImageCollection player = loadImageCollection("player");
    private static final ImageCollection score = loadImageCollection("score");

    /**
     * private constructor, making the class uninstanciable
     */
    private GameStateDeserializer() {
    };

    /**
     * Returns the image collection associated to the name given in parameters
     * 
     * @param String
     *            s name of the image collection
     * @return the image collection
     */
    private static ImageCollection loadImageCollection(String s) {
        ImageCollection c = null;
        try {
            c = new ImageCollection(s);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        return c;
    }

    /**
     * The method in charge of deserializing a list, representing the serialized
     * version of the gameState
     * 
     * @param encodedList
     *            , the encodedList representing the gameState
     * @return a new GameState, containing the images to graphically display the
     *         features of the game
     * @throws URISyntaxException
     *             , possible exception while running through an image
     *             collection
     * @throws IOException
     *             , possible exception while running through an image
     *             collection
     */
    public static GameState deserializeGameState(List<Byte> encodedList)
            throws URISyntaxException, IOException {

        if (encodedList.isEmpty())
            return null;

        int ListSize = encodedList.get(0);

        List<Byte> encodedBoard = new ArrayList<>(encodedList.subList(1,
                ++ListSize));

        List<Image> board = byteToBoard(spiralToRowMajor(deserialize(encodedBoard)));

        List<Image> bombs = new ArrayList<>();

        List<Byte> encodedBombs = new ArrayList<>(encodedList.subList(
                ListSize + 1, ListSize += (encodedList.get(ListSize) + 1)));
        bombs = byteToBomb(deserialize(encodedBombs));

        List<Player> players = byteToPlayer(encodedList.subList(ListSize,
                ListSize + NUMBER_OF_PLAYER_ATTRIBUTES));

        List<Image> scores = scores(players);

        byte remainingTime = encodedList.get(encodedList.size() - 1);

        List<Image> time = time(remainingTime);

        return new GameState(players, board, bombs, scores, time);
    }

    /**
     * The method deserializes a list, following the suggested process
     * 
     * @param encodedBoard
     *            , the list to be decoded
     * @return a expended, deserialized version of the list given in argument
     */
    private static List<Byte> deserialize(List<Byte> encodedBoard) {
        Iterator<Byte> it = encodedBoard.iterator();
        List<Byte> decodedBoard = new ArrayList<>();
        while (it.hasNext()) {
            byte b;

            if ((b = it.next()) < 0 && it.hasNext()) {
                byte futur = it.next();
                for (int i = 0; i < (2 - b); i++) {
                    decodedBoard.add(futur);
                }
            } else
                decodedBoard.add(b);
        }
        return Collections.unmodifiableList(decodedBoard);
    }

    /**
     * This method adds the correct proportion of "led_on" and "led_off" images
     * to a list, based on the progress of the game
     * 
     * @param remainingTime
     *            represents a compressed version of the time(in s) left in the
     *            game
     * @return a list containing the right number of "led_on" and "left_off"
     *         images
     * @throws URISyntaxException
     *             possible exception while running through an image collection
     * @throws IOException
     *             possible exception while running through an image collection
     */
    private static List<Image> time(byte remainingTime) {
        List<Image> result = new ArrayList<>();

        for (int i = 0; i < remainingTime; i++)
            result.add(score.image(LED_ON));
        for (int i = 0; i < NUMBER_OF_IMAGES_IN_TIMELINE - remainingTime; i++)
            result.add(score.image(LED_OFF));

        return Collections.unmodifiableList(result);
    }

    /**
     * The method adds images composing the score line to a list, based on a 4
     * sized player list given in argument
     * 
     * @param players
     *            a list of players that will be used to determine the state of
     *            each player
     * @return a list of the right images to compose the score line
     * @throws URISyntaxException
     *             possible exception while running through an image collection
     * @throws IOException
     *             possible exception while running through an image collection
     */
    private static List<Image> scores(List<Player> players)
            throws URISyntaxException, IOException {
        List<Image> result = new ArrayList<>();
        for (int i = 0; i < players.size(); i++) {
            result.addAll(playerScores(players.get(i), i, score));
            if (i == 1) {
                for (int j = 0; j < VOID_TILE_SIZE; j++)
                    result.add(score.image(VOID_TILE));
            }
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * method is used in the method "scores" in order to add the correct images
     * of each player at the correct spot on the score line
     * 
     * @param p
     *            , the player according to which specific images will be added
     *            the list that will be returned
     * @param i
     *            , an integer allowing the access of the right image in the
     *            image collection
     * @param ic
     *            , the image collection to be browsed
     * @return a list of image containing 3 images, representing a specific
     *         player on the scoreline
     */
    private static List<Image> playerScores(Player p, int i, ImageCollection ic) {
        List<Image> result = new ArrayList<>();
        result.add(ic.image(p.isAlive() ? i * 2 : i * 2 + 1));
        result.add(ic.image(10));
        result.add(ic.image(11));
        return Collections.unmodifiableList(result);
    }

    /**
     * Method used in the method "deserializeGameState" to convert a list in
     * spiral order to a list in row major order. Useful in the deserialazation
     * process of the board
     * 
     * @param spiralList
     *            , the list in spiral order
     * @return the same list but in row major order
     */
    private static List<Byte> spiralToRowMajor(List<Byte> spiralList) {
        List<Byte> result = new ArrayList<>();
        for (Cell c : Cell.ROW_MAJOR_ORDER) {
            result.add(spiralList.get(Cell.SPIRAL_ORDER.indexOf(c)));
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * Method used in the method "deserializeGameState" to add the correct
     * images from the "block" image collection to draw the board
     * 
     * @param decodedBoard
     *            , a list that has already been decoded, each byte representing
     *            an image that will be added to the list that will be returned
     * @return a list of images used to graphically display the board
     * @throws URISyntaxException
     *             , possible exception while running through an image
     *             collection
     * @throws IOException
     *             , possible exception while running through an image
     *             collection
     */
    private static List<Image> byteToBoard(List<Byte> decodedBoard)
            throws URISyntaxException, IOException {
        List<Image> blocks = new ArrayList<>();
        for (Byte b : decodedBoard) {
            blocks.add(block.image(b));
        }
        return Collections.unmodifiableList(blocks);
    }

    /**
     * Method is used in the method "deserializeGameState" in order add the
     * right images, representing blast or bomb images
     * 
     * @param decodedBomb
     *            a list of byte, that has already been decoded, each byte
     *            representing a different image
     * @return a list of images to graphically display the bomb and explosions
     * @throws URISyntaxException
     *             possible exception while running through an image collection
     * @throws IOException
     *             possible exception while running through an image collection
     */

    private static List<Image> byteToBomb(List<Byte> decodedBomb)
            throws URISyntaxException, IOException {
        List<Image> bombs = new ArrayList<>();
        for (Byte b : decodedBomb) {
            bombs.add(explosion.imageOrNull(b));
        }
        return Collections.unmodifiableList(bombs);
    }

    /**
     * This method returns a list of four new players, based on the information
     * contained in the list given in argument
     * 
     * @param decodedPlayers
     *            a list of bytes containing the right information to create
     *            four new players
     * @return a list of 4 new players
     * @throws URISyntaxException
     *             possible exception while running through an image collection
     * @throws IOException
     *             possible exception while running through an image collection
     */
    private static List<Player> byteToPlayer(List<Byte> decodedPlayers)
            throws URISyntaxException, IOException {
        List<GameState.Player> players = new ArrayList<>();
        Iterator<Byte> it = decodedPlayers.iterator();

        players.add(new Player(PlayerID.PLAYER_1, it.next(), new SubCell(Byte
                .toUnsignedInt(it.next()), Byte.toUnsignedInt(it.next())),
                player.imageOrNull(it.next())));
        players.add(new Player(PlayerID.PLAYER_2, it.next(), new SubCell(Byte
                .toUnsignedInt(it.next()), Byte.toUnsignedInt(it.next())),
                player.imageOrNull(it.next())));
        players.add(new Player(PlayerID.PLAYER_3, it.next(), new SubCell(Byte
                .toUnsignedInt(it.next()), Byte.toUnsignedInt(it.next())),
                player.imageOrNull(it.next())));
        players.add(new Player(PlayerID.PLAYER_4, it.next(), new SubCell(Byte
                .toUnsignedInt(it.next()), Byte.toUnsignedInt(it.next())),
                player.imageOrNull(it.next())));

        return Collections.unmodifiableList(players);
    }

}
