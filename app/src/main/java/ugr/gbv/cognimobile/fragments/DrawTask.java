package ugr.gbv.cognimobile.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.interfaces.LoadContent;
import ugr.gbv.cognimobile.utilities.DrawingView;
import ugr.gbv.cognimobile.utilities.PathGenerator;
import ugr.gbv.cognimobile.utilities.Point;

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
        nextButton.setOnClickListener(view -> callBack.loadContent());

        FloatingActionButton helpButton = view.findViewById(R.id.helpButton);
        helpButton.setOnClickListener(view -> {
            if(builder != null){
                builder.show();
            }
        });



        Button undoButton = view.findViewById(R.id.undoButton);
        undoButton.setOnClickListener(view -> {
            drawingView.undoLastOperation();
            if(taskType == GRAPH) {
                undoLastButton();
            }
        });



        answer = new ArrayList<>();


        return view;
    }

    private void undoLastButton() {
        if(answer.size() > 0){
            answer.remove(answer.size()-1);
            pressedButtons.get(pressedButtons.size()-1).setBackground(getResources().getDrawable(R.drawable.circle_no_fill,context.getTheme()));
            pressedButtons.get(pressedButtons.size()-1).setTextColor(getResources().getColor(R.color.black,context.getTheme()));
            pressedButtons.remove(pressedButtons.size()-1);
        }
    }

    private void drawButtons(ArrayList<Point> points) {
        CardView layout = view.findViewById(R.id.cardView);

        String[] tags = context.getResources().getStringArray(R.array.graphsValues);


        for (int i = 0; i < points.size();++i) {
            Button button = new Button(context);
            button.setId(View.generateViewId());
            button.setBackground(getResources().getDrawable(R.drawable.circle_no_fill,context.getTheme()));
            button.setText(tags[i]);
            button.setTag(points.get(i).getLabel());
            button.setOnClickListener(v -> {
                Button button1 = (Button) v;
                boolean continua = true;
                for(int i1 = 0; i1 < secuence.size() && continua; ++i1){
                    if(button1.getTag().equals(secuence.get(i1).getLabel())){
                        if(!answer.contains(button1.getTag().toString())){
                            button1.setBackground(getResources().getDrawable(R.drawable.circle_with_fill,context.getTheme()));
                            button1.setTextColor(getResources().getColor(R.color.white,context.getTheme()));
                            answer.add(button1.getTag().toString());
                            drawingView.drawToPoint(secuence.get(i1));
                            continua = false;
                            pressedButtons.add(button1);
                        }
                    }
                }

            });
            layout.addView(button);
            int dimens = getResources().getDimensionPixelSize(R.dimen.circle);
            int halfDimen = dimens/2;
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(dimens, dimens);
            button.setLayoutParams(lp);
            button.setX(points.get(i).getX()-halfDimen);
            button.setY(points.get(i).getY()-halfDimen);


        }

    }


    @Override
    public void loadContent() {
        if(!loaded) {

            ImageView imageView = view.findViewById(R.id.banner_image);
            TextView textView = view.findViewById(R.id.banner_text);
            Button button = view.findViewById(R.id.undoButton);

            switch (taskType) {
                case GRAPH:
                    int height = drawingView.getCanvasHeight();
                    int width = drawingView.getCanvasWidth();
                    PathGenerator pathGenerator = new PathGenerator();
                    secuence = pathGenerator.makePath(height, width);
                    drawButtons(secuence);
                    textView.setText(R.string.graph_instructions);
                    break;
                case CUBE:
                    button.setText(R.string.clear_title_button);
                    textView.setText(R.string.cube_instructions);
                    imageView.setVisibility(View.VISIBLE);
                    break;
                case WATCH:
                    button.setText(R.string.clear_title_button);
                    textView.setText(R.string.clock_instructions);
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

        builder.setOnDismissListener(dialogInterface -> {
            //nothing;
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
                /*TextView message = new TextView(context);
                message.setText(getResources().getText(R.string.app_name));
                message.setBackgroundColor(getResources().getColor(R.color.white,context.getTheme()));
                builder.addContentView(message, new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
                );*/
                break;
            default:
                throw new RuntimeException("Unsupported Task Type");

        }


    }


}
