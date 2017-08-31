package ch.epfl.xblast;

/**
 * ArgumentChecker
 * 
 * @author Gabriel Hayat (246239)
 * @author Adrian Guerra Alejos (246225)
 */

public final class ArgumentChecker {

    private ArgumentChecker() {
    }

    /**
     * Returns the parameter if and only if the parameter's value is not
     * negative.
     * 
     * @throws IllegalArgumentException
     *             if negative
     * @param an
     *            integer value
     * @return the parameter if and only if the parameter's value is not
     *         negative
     */
    public static int requireNonNegative(int value) {
        if (value < 0) {
            throw new IllegalArgumentException();
        }
        return value;
    }
}
