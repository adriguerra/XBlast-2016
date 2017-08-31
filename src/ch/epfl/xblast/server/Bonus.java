package ch.epfl.xblast.server;

/**
 * A bonus on the board
 * 
 * @author Gabriel Hayat (246239)
 * @author Adrian Guerra Alejos (246225)
 */

public enum Bonus {

    INC_BOMB {
        /**
         * Returns a new player with increased maxBombs by one unit if and only
         * if its current maxBombs does not exceed 9
         * 
         * @arg player to receive bonus
         * @return new player with increased maxBombs or the same original
         *         player
         */
        @Override
        public Player applyTo(Player player) {
            int max = player.maxBombs();
            if (max < 9) {
                return player.withMaxBombs(max + 1);
            }
            return player;
        }
    },

    INC_RANGE {
        /**
         * Returns a new player with increased bombRange by one unit if and only
         * if its current bombRange does not exceed 9
         * 
         * @arg player to receive bonus
         * @return new player with increased bombRange or the same original
         *         player
         */
        @Override
        public Player applyTo(Player player) {
            int max = player.bombRange();
            if (max < 9) {
                return player.withBombRange(max + 1);
            }
            return player;
        }
    },

    INC_SPEED {

        /**
         * Returns a new player with a speedFactor of 2 or the same original
         * player if the player already has that speedFactor
         * 
         * @arg player to receive bonus
         * @return new player with a speedFactor of 2
         */
        @Override
        public Player applyTo(Player player) {
            if (player.speedFactor() < 2) {
                return player.withSpeedFactor(2);
            }
            return player;
        }
    };

    abstract public Player applyTo(Player player);

}
