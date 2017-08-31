package ch.epfl.xblast.client;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

import javax.imageio.ImageIO;

/**
 * An image collection
 * 
 * @author Gabriel Hayat (246239)
 * @author Adrian Guerra Alejos (246225)
 */
public final class ImageCollection {
    private final File dir;
    private final Map<Integer, Image> map;

    /**
     * constructs a new image collection, that can be browsed in order to get
     * its images
     * 
     * @param dirName
     *            , the name of the image collection to be browsed
     * @throws URISyntaxException
     *             , possible exception while running through an image
     *             collection, thrown if the dirName given in argument does not
     *             correspond to an existing image collection
     * @throws IOException
     *             , possible exception while running through an image
     *             collection, thrown if the dirName given in argument does not
     *             correspond to an existing image collection
     */
    public ImageCollection(String dirName) throws URISyntaxException,
            IOException {
        dir = new File(ImageCollection.class.getClassLoader()
                .getResource(dirName).toURI());
        map = new HashMap<>();

        for (File f : dir.listFiles()) {
            map.put(Integer.parseInt(f.getName().substring(0, 3)),
                    ImageIO.read(f));
        }
    }

    /**
     * This method accesses the right image in the image collection,
     * 
     * @param i
     *            , the position of the image that should be returned in the
     *            image collection if the parameter doesn t correspond to the
     *            position of any image in the image collection, the method
     *            throws a "NoSuchElementException"
     * @return the corresponding image
     */
    public Image image(int i) {
        try {
            return Objects.requireNonNull(imageOrNull(i));
        } catch (NullPointerException e) {
            throw new NoSuchElementException();
        }
    }

    /**
     * This method accesses the right image in the image collection,
     * 
     * @param i
     *            , the position of the image that should be returned in the
     *            image collection
     * @return the corresponding image or null if the parameter doesn t
     *         correspond to the position of any image in the image collection
     */
    public Image imageOrNull(int i) {
        return map.containsKey(i) ? map.get(i) : null;
    }
}
