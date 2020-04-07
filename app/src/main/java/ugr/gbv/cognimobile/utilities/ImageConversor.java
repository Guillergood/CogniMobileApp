package ugr.gbv.cognimobile.utilities;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;


public class ImageConversor implements Serializable {


    private static volatile ImageConversor instantiated;

    private ImageConversor(){

        if (instantiated != null){
            throw new RuntimeException("Use .getInstance() to instantiate ImageConversor");
        }
    }

    public static ImageConversor getInstance() {
        if (instantiated == null) {
            synchronized (ImageConversor.class) {
                if (instantiated == null) instantiated = new ImageConversor();
            }
        }

        return instantiated;
    }




    public String encodeTobase64(Bitmap image)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        boolean result = image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();

        return Base64.encodeToString(b,Base64.NO_WRAP);
    }
}
