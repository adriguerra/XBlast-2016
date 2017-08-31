package ch.epfl.xblast;

/**
 * Public interface Time allowing us to declare constants used to convert
 * measures of time
 * 
 * @author Gabriel Hayat (246239)
 * @author Adrian Guerra Alejos (246225)
 */

public interface Time {
    public static final int S_PER_MIN = 60;
    public static final int MS_PER_S = 1000;
    public static final int US_PER_S = 1000 * MS_PER_S;
    public static final int NS_PER_S = 1000 * US_PER_S;

}
