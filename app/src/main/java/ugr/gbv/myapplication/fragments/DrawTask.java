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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import ugr.gbv.myapplication.R;
import ugr.gbv.myapplication.interfaces.LoadContent;
import ugr.gbv.myapplication.utilities.DrawingView;
import ugr.gbv.myapplication.utilities.PathGenerator;
import ugr.gbv.myapplication.utilities.Point;

public class DrawTask extends Fragment implements LoadContent {

    private Context context;
    private View view;
    private DrawingView drawingView;
    private ArrayList<Point> secuence;
    private ArrayList<String> answer;
    private int taskType;
    private boolean loaded;
    private Dialog builder;
    private LoadContent callBack;

    // Constants
    public static final int GRAPH = 0;
    public static final int CUBE = 1;
    public static final int WATCH = 2;

    private ArrayList<Button> pressedButtons;


    public DrawTask(int taskType, LoadContent callBack){
        this.taskType = taskType;
        loaded = false;
        pressedButtons = new ArrayList<>();
        this.callBack = callBack;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.draw_task,container, false);
        drawingView = view.findViewById(R.id.drawingSpace);

        switch (taskType){
            case GRAPH:
                drawingView.setCallBack(this, false);
                break;
            case CUBE:
            case WATCH:
                drawingView.setCallBack(this, true);
                break;
            default:
                throw new RuntimeException("Task type unsupported");
        }

        context = getContext();

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



        Button undoButton = view.findViewById(R.id.undoButton);
        undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawingView.undoLastOperation();
                if(taskType == GRAPH) {
                    undoLastButton();
                }
            }
        });



        answer = new ArrayList<>();


        return view;
    }

    private void undoLastButton() {
        if(answer.size() > 0){
            answer.remove(answer.size()-1);
            pressedButtons.get(pressedButtons.size()-1).setBackground(getResources().getDrawable(R.drawable.circle_no_fill,context.getTheme()));
            pressedButtons.remove(pressedButtons.size()-1);
        }
    }

    private void drawButtons(ArrayList<Point> points) {
        ConstraintLayout layout = view.findViewById(R.id.drawTaskLayout);
        ConstraintSet set = new ConstraintSet();
        set.clone(layout);

        String[] tags = context.getResources().getStringArray(R.array.graphsValues);


        for (int i = 0; i < points.size();++i) {
            Button button = new Button(context);
            button.setId(View.generateViewId());
            button.setBackground(getResources().getDrawable(R.drawable.circle_no_fill,context.getTheme()));
            button.setText(tags[i]);
            button.setTag(points.get(i).getLabel());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button button = (Button) v;
                    button.setBackground(getResources().getDrawable(R.drawable.circle_with_fill,context.getTheme()));
                    boolean continua = true;
                    answer.add(button.getTag().toString());
                    for(int i = 0; i < secuence.size() && continua; ++i){
                        if(button.getTag().equals(secuence.get(i).getLabel())){
                            drawingView.drawToPoint(secuence.get(i));
                            continua = false;
                        }
                    }

                    pressedButtons.add(button);
                }
            });
            layout.addView(button);
            int dimens = getResources().getInteger(R.integer.circle);
            int halfDimen = dimens/2;
            set.connect(button.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
            set.connect(button.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, 0);
            set.setTranslation(button.getId(),points.get(i).getX()-halfDimen, points.get(i).getY()-halfDimen);
            set.constrainHeight(button.getId(), dimens);
            set.constrainWidth(button.getId(), dimens);


            set.applyTo(layout);

        }

    }


    @Override
    public void loadContent() {
        if(!loaded) {
            switch (taskType) {
                case GRAPH:
                    int height = drawingView.getCanvasHeight();
                    int width = drawingView.getCanvasWidth();
                    PathGenerator pathGenerator = new PathGenerator();
                    secuence = pathGenerator.makePath(height, width);
                    drawButtons(secuence);
                    break;
                case CUBE:
                    break;
                case WATCH:
                    break;
                default:
                    throw new RuntimeException("Task type not supported");
            }

            loaded = true;
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

        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //nothing;
            }
        });


        switch (taskType){
            case GRAPH:
                TextView content = new TextView(context);
                content.setText(getResources().getText(R.string.app_name));
                content.setBackgroundColor(getResources().getColor(R.color.white,context.getTheme()));
                builder.addContentView(content, new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
                );
                break;
            case CUBE:
                ImageView imageView = new ImageView(context);
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.cube,context.getTheme()));
                imageView.setBackgroundColor(getResources().getColor(R.color.white,context.getTheme()));
                builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
                );
                break;
            case WATCH:
                TextView message = new TextView(context);
                message.setText(getResources().getText(R.string.app_name));
                message.setBackgroundColor(getResources().getColor(R.color.white,context.getTheme()));
                builder.addContentView(message, new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
                );
                break;
            default:
                throw new RuntimeException("Unsupported Task Type");

        }


        //set the image in dialog popup
        //below code fullfil the requirement of xml layout file for dialoge popup


    }
}
