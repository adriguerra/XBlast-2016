package ch.epfl.xblast.server;

import ch.epfl.xblast.Direction;
import ch.epfl.xblast.server.Player.LifeState.State;

/**
 * A player painter
 * 
 * @author Gabriel Hayat (246239)
 * @author Adrian Guerra Alejos (246225)
 */
public final class PlayerPainter {
    private static final int RANGE_SIZE = 20, IS_DYING = 13, LOSES_LIFE = 12,
            IS_DEAD = 15, INVULNERABLE_RANGE = 80;
    private static final int WALKING_NORTH_DIRECTION = 0,
            WALKING_EAST_DIRECTION = 3, WALKING_SOUTH_DIRECTION = 6,
            WALKING_WEST_DIRECTION = 9;
    private static final int NUMBER_OF_PLAYERS = 4;
    private static final int LEFT_FOOT_FIRST = 1, RIGHT_FOOT_FIRST = 2;

    /**
     * A private constructor, making the class non-instantiable
     */
    private PlayerPainter() {
    }

    /**
     * This method returns the correct identifying number of the image in the
     * "player" image collection, based on the state and position of the player,
     * and the current tick of the gameState
     * 
     * @param tick
     * @param player
     *            the concerned player
     * @return the byte corresponding the right image
     */
    public static byte byteForPlayer(int tick, Player player) {
        int n = player.id().ordinal();
        byte result = (byte) (RANGE_SIZE * n);

        if (!player.isAlive()) {
            return (byte) (result + IS_DEAD);
        }

        if (player.lifeState().state() == State.DYING) {
            if (player.lives() == 1) {
                return (byte) (result + IS_DYING);
            } else {
                return (byte) (result + LOSES_LIFE);
            }
        }

        Direction d = player.direction();
        int pos = 0;

        if (player.lifeState().state() == State.INVULNERABLE && tick % 2 == 1) {
            result = INVULNERABLE_RANGE;
        }

        int[] offset = { WALKING_NORTH_DIRECTION, WALKING_EAST_DIRECTION,
                WALKING_SOUTH_DIRECTION, WALKING_WEST_DIRECTION };
        int[] posTab = { player.position().y(), player.position().x() };

        pos = posTab[d.ordinal() % 2];

        result += offset[d.ordinal()];

        switch (pos % NUMBER_OF_PLAYERS) {
        case 1:
            return (byte) (result + LEFT_FOOT_FIRST);
        case 3:
            return (byte) (result + RIGHT_FOOT_FIRST);
        default:
            return (byte) (result);
        }
    }
}