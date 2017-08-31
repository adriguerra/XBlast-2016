package ch.epfl.xblast.server;

import java.io.IOException;

/**
 * Main program of server. Runs the server once the number of players expected
 * to join have joined the game
 * 
 * @author Gabriel Hayat (246239)
 * @author Adrian Guerra Alejos (246225)
 */
public class Main {
    public static int DEFAULT_NUMBER_OF_PLAYERS = 4;

    public static void main(String[] args) throws IOException,
            InterruptedException {
        int numberOfPlayers = DEFAULT_NUMBER_OF_PLAYERS;
        if (args.length > 0) {
            try {
                numberOfPlayers = Integer.valueOf(args[0]);
                if (numberOfPlayers > DEFAULT_NUMBER_OF_PLAYERS)
                    throw new IllegalArgumentException(
                            "The number of players cannot exceed 4");
            }

            catch (NumberFormatException e) {
                throw new IllegalArgumentException(
                        "Argument must be an integer !");
            }
        }

        Server server = new Server(numberOfPlayers);
        if (server.waitForPlayers())
            server.run();
    }

}
