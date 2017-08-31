package ch.epfl.xblast.client;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.SubCell;

/**
 * A gameState containing the visible information to the client
 * 
 * @author Gabriel Hayat (246239)
 * @author Adrian Guerra Alejos (246225)
 */
public final class GameState {
    private final List<Player> listPlayers;
    private final List<Image> board;
    private final List<Image> bombsAndExplosions;
    private final List<Image> scores;
    private final List<Image> time;

    /**
     * Constructs the a gameState based on the following argument
     * 
     * @param listPlayers
     *            a list of players
     * @param board
     *            a list of images associated with the current board of the
     *            gaemState
     * @param bombsAmdExplosion
     *            a list of images associated with the state of each block of
     *            the gameState
     * @param score
     *            the images composing the graphical display of the score line
     * @param time
     *            the images composing the graphical display of the time line
     */

    GameState(List<Player> listPlayers, List<Image> board,
            List<Image> bombsAndExplosions, List<Image> scores, List<Image> time) {
        this.listPlayers = Collections.unmodifiableList(new ArrayList<>(
                listPlayers));
        this.board = Collections.unmodifiableList(new ArrayList<>(board));
        this.bombsAndExplosions = Collections.unmodifiableList(new ArrayList<>(
                bombsAndExplosions));
        this.scores = Collections.unmodifiableList(new ArrayList<>(scores));
        this.time = Collections.unmodifiableList(new ArrayList<>(time));
    }

    public List<Image> board() {
        return board;
    }

    public List<Image> bombsAndExplosions() {
        return bombsAndExplosions;
    }

    public List<Image> scores() {
        return scores;
    }

    public List<Image> time() {
        return time;
    }

    public List<Player> players() {
        return listPlayers;
    }

    public static final class Player {
        private final PlayerID id;
        private final int lives;
        private final SubCell pos;
        private final Image image;

        /**
         * Constructs a player based on the following argument
         * 
         * @param id
         *            the id of the player
         * @param lives
         *            the number of lives
         * @param pos
         *            the position of the player, in the form of a subCell
         * @param image
         *            the graphical representation of the player
         */

        Player(PlayerID id, int lives, SubCell pos, Image image) {
            this.id = id;
            this.lives = lives;
            this.pos = pos;
            this.image = image;
        }

        public PlayerID id() {
            return this.id;
        }

        public int lives() {
            return this.lives;
        }

        public SubCell position() {
            return pos;
        }

        public Image image() {
            return image;
        }

        public boolean isAlive() {
            return lives() > 0;
        }
    }
}
