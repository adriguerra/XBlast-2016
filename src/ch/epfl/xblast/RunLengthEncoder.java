package ch.epfl.xblast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A class containing two static method enabling list to be encoded and decoded
 * according to the specifed process
 * 
 * @author Gabriel Hayat (246239)
 * @author Adrian Guerra Alejos (246225)
 */
public final class RunLengthEncoder {
    private final static int MAX_BYTES = 130;

    private RunLengthEncoder() {
    };

    /**
     * Returns an encoded list depending on the number of repeated integers
     * 
     * @param l
     *            the list to be encoded
     * @return the encoded list
     */

    public static List<Byte> encode(List<Byte> l) {
        List<Byte> encoded = new ArrayList<>();
        int i = 1;

        Iterator<Byte> it = l.iterator();
        Byte curr = null;
        Byte next = null;

        if (it.hasNext())
            curr = it.next();
        while (it.hasNext()) {
            while (it.hasNext() && curr.equals(next = it.next())) {
                if (++i >= MAX_BYTES) {
                    encoded.add((byte) (2 - i));
                    i = 0;
                    encoded.add(curr);
                }
            }
            if (i > 2) {
                encoded.add((byte) (2 - i));
                i = 1;
            } else if (i == 2) {
                encoded.add(curr);
                i = 1;
            }
            encoded.add(curr);
            curr = next;
        }
        if (next != null && next != encoded.get(encoded.size() - 1))
            encoded.add(next);
        return Collections.unmodifiableList(encoded);
    }

    /**
     * Returns the decoded list given an encoded list according to the specified
     * process
     * 
     * @param l
     *            , the encoded list that will be decoded
     * @return the decoded list
     */
    public static List<Byte> decode(List<Byte> l) {
        ArgumentChecker.requireNonNegative(l.get(l.size() - 1));
        List<Byte> decoded = new ArrayList<>();

        boolean sq = false;
        int repeat = 1;

        for (Byte b : l) {
            if (b < 0 && !sq) {
                sq = true;
                repeat = -(int) (b) + 2;
            } else if (b > 0) {
                for (int i = 0; i < repeat; i++) {
                    decoded.add(b);
                }
                sq = false;
                repeat = 1;
            } else if (sq && b < 0) {
                repeat += -(int) (b) + 2;
            }
        }
        return Collections.unmodifiableList(decoded);
    }
}
