package ugr.gbv.cognimobile.utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.Serializable;


/**
 * Singleton class to manage the images
 */
public class ImageConverse implements Serializable {


    private static volatile ImageConverse instantiated;

    /**
     * Private constructor
     */
    private ImageConverse() {

        if (instantiated != null) {
            throw new RuntimeException("Use .instantiate() to instantiate ImageConversor");
        }
    }

    /**
     * Getter to return the unique instance in the app.
     *
     * @return the unique instance in the app.
     */
    public static ImageConverse getInstance() {
        if (instantiated == null) {
            synchronized (ImageConverse.class) {
                if (instantiated == null) instantiated = new ImageConverse();
            }
        }

        return instantiated;
    }


    /**
     * Decodes a image from a Base64 string.
     *
     * @param image Base64 encoded image string.
     * @return a bitmap image
     */
    public Bitmap decodeFromBase64(String image){
        byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }



}
