package ugr.gbv.myapplication.tests;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import ugr.gbv.myapplication.R;
import ugr.gbv.myapplication.utilities.AV;
import ugr.gbv.myapplication.utilities.DrawingView;
import ugr.gbv.myapplication.utilities.Point;


//TODO ESTO VA A TENNER QUE SER UN FRAGMENT EL CUAL VA A ESTAR EN UNA ACTIVIDAD DE TEST
public class DrawTask extends Activity {

    private DrawingView drawingView;
    private ArrayList<Point> secuence;
    private ArrayList<Point> answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.draw_task);

        //getActionBar().hide();
        drawingView = findViewById(R.id.drawingSpace);



        Button nextButton = findViewById(R.id.nextTaskButton);
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

        FloatingActionButton helpButton = findViewById(R.id.helpButton);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawingView.clearCanvas();
            }
        });



        Button prevButtonQUITAR = findViewById(R.id.prevButtonQuitar);
        prevButtonQUITAR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog builder = new Dialog(DrawTask.this);
                builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
                builder.getWindow().setBackgroundDrawable(
                        new ColorDrawable(android.graphics.Color.TRANSPARENT));
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        //nothing;
                    }
                });

                ImageView imageView = new ImageView(DrawTask.this);
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
    }

    private void drawButtons(ArrayList<Point> points) {
        ConstraintLayout layout = findViewById(R.id.drawTaskLayout);
        ConstraintSet set = new ConstraintSet();
        set.clone(layout);

        for (Point point:points) {
            Button button = new Button(this);
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
            set.connect(button.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
            set.connect(button.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
            //TODO CAMBIAR PARA QUE ESTEN EN DIMENSIONES
            set.setTranslation(button.getId(),point.getX()-48, point.getY()-48);
            set.constrainHeight(button.getId(), 96);
            set.constrainWidth(button.getId(), 96);


            set.applyTo(layout);

        }

    }

}
