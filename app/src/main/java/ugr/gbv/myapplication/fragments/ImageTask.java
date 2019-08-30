package ugr.gbv.myapplication.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ugr.gbv.myapplication.R;
import ugr.gbv.myapplication.interfaces.LoadContent;

public class ImageTask extends Fragment {

    private Context context;
    private Dialog builder;
    private LoadContent callBack;
    private ImageView selected;


    public ImageTask(LoadContent callBack){
        this.callBack = callBack;
    }




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.image_task, container, false);

        context = getContext();


        final RelativeLayout layout = view.findViewById(R.id.imageContainer);

        final String[] imagesArray = context.getResources().getStringArray(R.array.images);


        int[] imagesId = new int[imagesArray.length];


        for (int i = 0; i < imagesArray.length; ++i) {
            byte[] decodedString = Base64.decode(imagesArray[i], Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            ImageView imageView = new ImageView(context);
            imageView.setId(View.generateViewId());
            imageView.setImageBitmap(decodedByte);

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


            lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
            if (i > 0) {
                lp.addRule(RelativeLayout.BELOW, imagesId[i - 1]);
            }

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageView imageView = (ImageView) v;
                    if (selected != null) {
                        removeAllImagesModifications();
                        if (selected.getId() != imageView.getId()) {
                            addImageModifications(imageView);
                        } else {
                            selected = null;
                        }
                    } else {
                        addImageModifications(imageView);
                    }

                }
            });


            layout.addView(imageView, lp);
            imagesId[i] = imageView.getId();
        }



        buildDialog();


        Button nextButton = view.findViewById(R.id.nextTaskButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callBack.loadContent();
            }
        });

        FloatingActionButton helpButton = view.findViewById(R.id.helpButton);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(builder != null){
                    builder.show();
                }
            }
        });


        return view;
    }





    private void buildDialog(){
        builder = new Dialog(context);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = builder.getWindow();
        if(window != null) {
            window.setBackgroundDrawable(
                    new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //nothing;
            }
        });


        TextView content = new TextView(context);
        content.setText(getResources().getText(R.string.app_name));
        content.setBackgroundColor(getResources().getColor(R.color.white,context.getTheme()));
        builder.addContentView(content, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        );


    }

    private void removeAllImagesModifications(){
        selected.clearColorFilter();
        selected.setBackground(null);
    }

    private void addImageModifications(ImageView imageView){
        selected = imageView;
        selected.setColorFilter(R.color.blue, android.graphics.PorterDuff.Mode.SRC_OVER);
        selected.setBackground(context.getResources().getDrawable(R.drawable.background_selected_image, context.getTheme()));
    }



}

