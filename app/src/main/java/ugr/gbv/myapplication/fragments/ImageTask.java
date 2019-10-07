package ugr.gbv.myapplication.fragments;

import android.app.Dialog;
import android.content.Context;
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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ugr.gbv.myapplication.R;
import ugr.gbv.myapplication.interfaces.LoadContent;

public class ImageTask extends Fragment {

    private Context context;
    private Dialog builder;
    private LoadContent callBack;
    private int selected;
    private int[] imagesId;
    private View mainView;

    public ImageTask(LoadContent callBack){
        this.callBack = callBack;
        selected = 0;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.image_task, container, false);

        context = getContext();


        final CardView layout = mainView.findViewById(R.id.cardView);

        final String[] imagesArray = context.getResources().getStringArray(R.array.images);


        imagesId = new int[imagesArray.length];



        for (int i = 0; i < imagesArray.length; ++i) {
            byte[] decodedString = Base64.decode(imagesArray[i], Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            ImageView imageView = new ImageView(context);
            imageView.setId(View.generateViewId());
            imageView.setImageBitmap(decodedByte);

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


            lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
            if (i > 0) {
                imageView.setVisibility(View.INVISIBLE);
            }



            layout.addView(imageView, lp);
            imagesId[i] = imageView.getId();
        }



        buildDialog();


        Button nextButton = mainView.findViewById(R.id.nextTaskButton);
        nextButton.setOnClickListener(view -> nextTask());

        Button idkButton = mainView.findViewById(R.id.idk_button);
        idkButton.setOnClickListener(view -> nextTask());

        FloatingActionButton helpButton = mainView.findViewById(R.id.helpButton);
        helpButton.setOnClickListener(view -> {
            if(builder != null){
                builder.show();
            }
        });


        return mainView;
    }

    private void nextTask() {
        if(selected == imagesId.length-1) {
            callBack.loadContent();
        }
        else{
            selected++;
            ImageView imageView = mainView.findViewById(imagesId[selected]);
            imageView.setVisibility(View.VISIBLE);
            if(selected > 0){
                imageView = mainView.findViewById(imagesId[selected-1]);
                imageView.setVisibility(View.INVISIBLE);
            }
        }
    }


    private void buildDialog(){
        builder = new Dialog(context);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = builder.getWindow();
        if(window != null) {
            window.setBackgroundDrawable(
                    new ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        builder.setOnDismissListener(dialogInterface -> {
            //nothing;
        });


        TextView content = new TextView(context);
        content.setText(getResources().getText(R.string.app_name));
        content.setBackgroundColor(getResources().getColor(R.color.white,context.getTheme()));
        builder.addContentView(content, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        );


    }

    /*private void removeAllImagesModifications(){
        selected.clearColorFilter();
        selected.setBackground(null);
    }

    private void addImageModifications(ImageView imageView){
        selected = imageView;
        selected.setColorFilter(R.color.blue, android.graphics.PorterDuff.Mode.SRC_OVER);
        selected.setBackground(context.getResources().getDrawable(R.drawable.background_selected_image, context.getTheme()));
    }*/



}

