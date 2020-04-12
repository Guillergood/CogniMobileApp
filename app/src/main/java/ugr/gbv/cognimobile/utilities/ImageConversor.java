package ugr.gbv.cognimobile.utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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




    public String encodeToBase64(Bitmap image)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();

        return Base64.encodeToString(b,Base64.NO_WRAP);
    }

    public Bitmap decodeFromBase64(String image){
        byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }



}
