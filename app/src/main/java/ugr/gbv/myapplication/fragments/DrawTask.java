package ugr.gbv.myapplication.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import ugr.gbv.myapplication.R;
import ugr.gbv.myapplication.utilities.AV;
import ugr.gbv.myapplication.utilities.DrawingView;
import ugr.gbv.myapplication.utilities.Point;

public class DrawTask extends Fragment {

    private Context context;
    private View view;
    private DrawingView drawingView;
    private ArrayList<Point> secuence;
    private ArrayList<Point> answer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.draw_task,container, false);
        drawingView = view.findViewById(R.id.drawingSpace);

        context = getContext();

        Button nextButton = view.findViewById(R.id.nextTaskButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int height = drawingView.getCanvasHeight();
                int width = drawingView.getCanvasWidth();
                AV angluin = new AV();
                secuence = angluin.makePath(height,width);
                //drawingView.drawPoints(secuence);
                drawButtons(secuence);
            }
        });

        FloatingActionButton helpButton = view.findViewById(R.id.helpButton);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawingView.clearCanvas();
            }
        });



        Button prevButtonQUITAR = view.findViewById(R.id.prevButtonQuitar);
        prevButtonQUITAR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog builder = new Dialog(context);
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

                ImageView imageView = new ImageView(context);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.cube));
                imageView.setBackgroundColor(getResources().getColor(R.color.white));
                //set the image in dialog popup
                //below code fullfil the requirement of xml layout file for dialoge popup

                builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
                );
                builder.show();
            }
        });



        answer = new ArrayList<>();


        return view;
    }

    private void drawButtons(ArrayList<Point> points) {
        ConstraintLayout layout = view.findViewById(R.id.drawTaskLayout);
        ConstraintSet set = new ConstraintSet();
        set.clone(layout);

        for (Point point:points) {
            Button button = new Button(context);
            button.setId(View.generateViewId());
            button.setBackground(getResources().getDrawable(R.drawable.circle_no_fill));
            button.setText(point.getLabel());
            button.setTag(point.getLabel());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button button = (Button) v;
                    button.setBackground(getResources().getDrawable(R.drawable.circle_with_fill));
                    boolean continua = true;
                    for(int i = 0; i < secuence.size() && continua; ++i){
                        if(button.getText().equals(secuence.get(i).getLabel())){
                            drawingView.drawToPoint(secuence.get(i));
                            answer.add(secuence.get(i));
                            continua = false;
                        }
                    }
                }
            });
            layout.addView(button);
            int dimens = getResources().getInteger(R.integer.circle);
            int halfDimen = dimens/2;
            set.connect(button.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
            set.connect(button.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
            set.setTranslation(button.getId(),point.getX()-halfDimen, point.getY()-halfDimen);
            set.constrainHeight(button.getId(), dimens);
            set.constrainWidth(button.getId(), dimens);


            set.applyTo(layout);

        }

    }




}
