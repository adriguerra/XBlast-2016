package ch.epfl.xblast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A List
 * 
 * @author Gabriel Hayat (246239)
 * @author Adrian Guerra Alejos (246225)
 */
public final class Lists {

    /**
     * Non-instantiable constructor
     */

    private Lists() {
    };

    /**
     * Returns a symmetric list from a given generic list
     * 
     * @param l
     *            generic list
     * @return the symmetric list
     * @throws IllegalArgumentException
     */

    public static <T> List<T> mirrored(List<T> l)
            throws IllegalArgumentException {
        List<T> copy = new ArrayList<T>();
        List<T> newList = new ArrayList<T>();

        newList.addAll(l);
        copy.addAll(newList);
        Collections.reverse(copy);
        newList.addAll(copy.subList(1, copy.size()));

        return Collections.unmodifiableList(newList);
    }

    /**
     * Returns a list of all the permutations possible of a list given in
     * argument. If an empty list is given as an argument, the method returns an
     * immutable list of an empty list.
     * 
     * @param List
     *            l from which all permutations are computed
     * @return a list of all the permutations possible of a list given in
     *         argument
     */
    public static <T> List<List<T>> permutations(List<T> l) {
        if (l.isEmpty()) {
            return Collections.singletonList(Collections.emptyList());
        }
        List<List<T>> result = new ArrayList<List<T>>();
        List<T> temp = new ArrayList<T>(l);

        if (temp.size() == 1) {
            return Arrays.asList(temp);
        }

        T head = temp.remove(0);

        if (temp.size() == 1) {
            for (int i = 0; i < l.size(); i++) {
                temp.add(i, head);
                result.add(new ArrayList<T>(temp));
                temp.remove(i);
            }
            return result;
        } else {
            result = permutations(temp);
            int b = result.size();

            for (int i = 0; i < b; i++) {

                for (int j = 0; j <= result.get(i).size(); j++) {
                    List<T> temp2 = new ArrayList<>(result.get(i));
                    temp2.add(j, head);
                    result.add(temp2);
                }
            }
            int c = 0;
            for (int i = 0; i < b; i++) {
                result.remove(i - c);
                c++;
            }
        }
        for (List<T> element : result) {
            Collections.unmodifiableList(element);
        }

        return result;
    }
}
