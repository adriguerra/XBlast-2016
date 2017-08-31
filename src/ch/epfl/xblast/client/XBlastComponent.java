package ch.epfl.xblast.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JComponent;

import ch.epfl.xblast.Cell;
import ch.epfl.xblast.PlayerID;
import ch.epfl.xblast.client.GameState.Player;

/**
 * An XBlastComponent, allowing the graphical representation of the game
 * 
 * @author Gabriel Hayat (246239)
 * @author Adrian Guerra Alejos (246225)
 */
public class XBlastComponent extends JComponent {
    private GameState gs = null;
    private PlayerID id;
    private final static int MAX_WIDTH = 960;
    private final static int MAX_HEIGHT = 688;
    private final static int posX[] = { 96, 240, 768, 912 };
    private final static int posY[] = { 659, 659, 659, 659 };

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(MAX_WIDTH, MAX_HEIGHT);
    }

    @Override
    protected void paintComponent(Graphics g0) {
        if (gs != null) {
            Graphics2D g = (Graphics2D) g0;
            drawImages(g, gs.board(), gs.bombsAndExplosions());
            Font font = new Font("Arial", Font.BOLD, 25);
            g.setColor(Color.WHITE);
            g.setFont(font);
            drawScores(g, gs.players());
            drawPlayers(g, gs.players());
        }
    }

    /**
     * Draws each image of the board, associated with its potential image from
     * the bomb list. The method is declared private as its only purpose is to
     * be called in the method paintComponent
     * 
     * @param g
     *            paint brush, Graphics2D
     * @param board
     *            the list of images of each block of the board
     * @param bombs
     *            the list of potential images of blasts associated with the
     *            block (the image is null if there is none)
     */

    private void drawImages(Graphics2D g, List<Image> listBoard,
            List<Image> listBombs) {
        int w = 0;
        int h = 0;
        int width = listBoard.get(0).getWidth(null);
        int height = listBoard.get(0).getHeight(null);

        for (int i = 0; i < listBoard.size() && i < listBombs.size(); i++) {
            g.drawImage(listBoard.get(i), w, h, null);
            Image bombImg = listBombs.get(i);
            if (bombImg != null) {
                g.drawImage(bombImg, w, h, null);
            }
            w += width;
            if (w >= MAX_WIDTH) {
                w = 0;
                h += height;
            }
        }
    }

    /**
     * Writes the scores of each players of the list players in the appropriate
     * location on the component. The method is declared private as its only
     * purpose is to be called in the method paintComponent
     * 
     * @param g
     *            paint brush, Graphics2D
     * @param posX
     *            a fixed length table, representing the x components of the
     *            writing
     * @param posY
     *            a fixed length table, representing the y components of the
     *            writing
     * @param players
     *            each player's score in this list is going to be written on the
     *            board
     */

    private void drawScores(Graphics2D g, List<Player> listPlayers) {
        int width = gs.scores().get(0).getWidth(null);
        int height = gs.scores().get(0).getHeight(null);
        int h = gs.scores().get(0).getHeight(null) * Cell.ROWS, w = 0;

        for (int i = 0; i < gs.scores().size(); i++) {
            g.drawImage(gs.scores().get(i), w, h, null);
            w += width;
        }

        h += height;
        w = 0;

        width = gs.time().get(0).getWidth(null);

        for (int i = 0; i < gs.time().size(); i++) {
            g.drawImage(gs.time().get(i), w, h, null);
            w += width;
        }
        if (posX.length != posY.length || posX.length != listPlayers.size())
            throw new IllegalArgumentException();

        for (int i = 0; i < posX.length; i++) {
            g.drawString(Integer.toString(listPlayers.get(i).lives()), posX[i],
                    posY[i]);
        }
    }

    /**
     * Draws the image of each player of the list of players given in parameter.
     * The method takes into account the order in which the images should be
     * painted. The method is declared private as its only purpose is to be
     * called in the method paintComponent.
     * 
     * @param g
     *            the paint brush
     * @param players
     *            each player's image is going to be brush at the right location
     */

    private void drawPlayers(Graphics2D g, List<Player> players) {
        List<Player> sortedPlayers = new ArrayList<>(players);
        Comparator<Player> comparatorPlayer = Comparator.comparingInt(u -> u
                .position().y());
        Comparator<Player> rotation = Comparator.comparingInt(u -> Math
                .floorMod(u.id().ordinal() - this.id.ordinal() - 1,
                        players.size()));
        Collections.sort(sortedPlayers,
                comparatorPlayer.thenComparing(rotation));

        for (Player p : sortedPlayers) {
            int posX = 4 * p.position().x() - 24;
            int posY = 3 * p.position().y() - 52;
            g.drawImage(p.image(), posX, posY, null);
        }
    }

    /**
     * Set a gameState that needs to be painted
     * 
     * @param gs
     *            the current gameState
     * @param id
     *            the player's ID to whom the graphical display is associated
     */

    public void setGameState(GameState gs, PlayerID id) {
        this.gs = gs;
        this.id = id;
        repaint();
    }

    public PlayerID getPlayerID() {
        return id;
    }
}
