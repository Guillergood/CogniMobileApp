package ugr.gbv.cognimobile.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import org.json.JSONException;

import java.util.ArrayList;

import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.interfaces.LoadContent;
import ugr.gbv.cognimobile.utilities.DrawingView;
import ugr.gbv.cognimobile.utilities.ImageConversor;
import ugr.gbv.cognimobile.utilities.TestDataSender;
import ugr.gbv.cognimobile.utilities.JsonAnswerWrapper;
import ugr.gbv.cognimobile.utilities.PathGenerator;
import ugr.gbv.cognimobile.utilities.Point;

public class DrawTask extends Task implements LoadContent {


    private DrawingView drawingView;
    private ArrayList<Point> secuence;
    private ArrayList<String> answer;
    private View view;
    private LinearLayout leftButtonContainer;
    private int undoTimes;


    private ArrayList<Button> pressedButtons;


    public DrawTask(int taskType, LoadContent callBack){
        this.taskType = taskType;
        pressedButtons = new ArrayList<>();
        this.callBack = callBack;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.draw_task,container, false);
        leftButtonContainer = view.findViewById(R.id.leftButtonContainer);
        leftButtonContainer.setVisibility(View.INVISIBLE);
        drawingView = view.findViewById(R.id.drawingSpace);
        banner = view.findViewById(R.id.banner);
        mainLayout = view.findViewById(R.id.drawTaskLayout);


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

        undoTimes = getResources().getInteger(R.integer.undo_times);

        centerButton = view.findViewById(R.id.centerButton);

        buildDialog();


        rightButton = view.findViewById(R.id.rightButton);




        leftButton = view.findViewById(R.id.leftButton);
        leftButton.setOnClickListener(view -> {
            if(checkIfHasUndoActions()) {
                decrementUndoAction();
                drawingView.undoLastOperation();
                if (taskType == GRAPH) {
                    undoLastButton();
                }
            }
            showUndoTimes();
        });

        answer = new ArrayList<>();
        providedTask = true;

        setNextButtonStandardBehaviour();


        return view;
    }

    private void decrementUndoAction() {
        undoTimes--;
    }

    private boolean checkIfHasUndoActions() {
        return undoTimes > 0;
    }

    private void undoLastButton() {

        if (answer.size() > 0) {
            answer.remove(answer.size() - 1);
            pressedButtons.get(pressedButtons.size() - 1).setBackground(getResources().getDrawable(R.drawable.circle_no_fill, context.getTheme()));
            pressedButtons.get(pressedButtons.size() - 1).setTextColor(getResources().getColor(R.color.black, context.getTheme()));
            pressedButtons.remove(pressedButtons.size() - 1);
        }

    }

    private void showUndoTimes() {
        if (undoTimes > 0)
            Toast.makeText(context, getResources().getString(R.string.times_left, undoTimes), Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context, getResources().getString(R.string.no_times_left), Toast.LENGTH_LONG).show();
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
            bannerText = view.findViewById(R.id.banner_text);

            TextView label = view.findViewById(R.id.leftButtonLabel);
            switch (taskType) {
                case GRAPH:
                    int height = drawingView.getCanvasHeight();
                    int width = drawingView.getCanvasWidth();
                    PathGenerator pathGenerator = new PathGenerator();
                    secuence = pathGenerator.makePath(height, width);
                    drawButtons(secuence);
                    bannerText.setText(R.string.graph_instructions);
                    break;
                case CUBE:
                    leftButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_delete_forever_black_24dp, context.getTheme()));
                    label.setText(R.string.clear_title_button);
                    bannerText.setText(R.string.cube_instructions);
                    imageView.setVisibility(View.VISIBLE);
                    break;
                case WATCH:
                    leftButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_delete_forever_black_24dp, context.getTheme()));
                    label.setText(R.string.clear_title_button);
                    bannerText.setText(R.string.clock_instructions);
                    break;
                default:
                    throw new RuntimeException("Task type not supported");
            }

            leftButtonContainer.setVisibility(View.VISIBLE);
            loaded = true;
        }
    }


    //TODO LIberar estos metedos de LoadContent
    @Override
    public void hideKeyboard() {
        callBack.hideKeyboard();
    }

    @Override
    public JsonAnswerWrapper getJsonAnswerWrapper() {
        return null;
    }


    @Override
    void saveResults() throws JSONException {
        switch (taskType) {
            case GRAPH:
                setScoring();
                callBack.getJsonAnswerWrapper().addArray("answer_secuence",answer);
                callBack.getJsonAnswerWrapper().addField("score",score);
            case CUBE:
            case WATCH:
                callBack.getJsonAnswerWrapper().addField("answer_image", ImageConversor.getInstance().encodeTobase64(drawingView.getCanvasBitmap()));
                callBack.getJsonAnswerWrapper().addTaskField();
                break;
            default:
                throw new RuntimeException("INVALID TASKTYPE");

        }
    }

    @Override
    void setScoring() {
        int size = answer.size();
        boolean stop = false;
        score = 1;
        for(int i = 0;i < size && !stop; ++i)
            if(!answer.get(i).equals(Integer.toString(i))){
                stop = true;
                score = 0;
            }
    }
}
