package ugr.gbv.cognimobile.utilities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Base64;
import android.util.Log;
import android.view.View;

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


    /**
     * Get bitmap of a view
     *
     * @param view source view
     * @return generated bitmap object
     */
    public static Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.layout(0, 0, view.getWidth(), view.getHeight());
        Log.d("", "combineImages: width: " + view.getWidth());
        Log.d("", "combineImages: height: " + view.getHeight());
        view.draw(canvas);
        return bitmap;
    }


    public String encodeAnswerToBase64(View view)
    {
        Bitmap image = getBitmapFromView(view);
        /*int size = image.getRowBytes() * image.getHeight();
        byte[] byteArray;

        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        image.copyPixelsToBuffer(byteBuffer);
        byteArray = byteBuffer.array();*/



        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        image.recycle();
        byte[] b = baos.toByteArray();

        String imageString = "data:image/png;base64," + Base64.encodeToString(b, Base64.NO_WRAP);

        /*String hola = "hola";
        byte[] data;
        data = hola.getBytes(StandardCharsets.UTF_8);
        String imageString = Base64.encodeToString(data, Base64.DEFAULT);*/

        return imageString;
    }

    public Bitmap decodeFromBase64(String image){
        byte[] decodedString = Base64.decode(image, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }



}
