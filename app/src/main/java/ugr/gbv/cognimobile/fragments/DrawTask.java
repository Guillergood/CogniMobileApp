package ugr.gbv.cognimobile.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Pair;
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
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import ugr.gbv.cognimobile.R;
import ugr.gbv.cognimobile.dto.TaskType;
import ugr.gbv.cognimobile.interfaces.LoadContent;
import ugr.gbv.cognimobile.interfaces.LoadDraw;
import ugr.gbv.cognimobile.utilities.ContextDataRetriever;
import ugr.gbv.cognimobile.utilities.DrawingView;
import ugr.gbv.cognimobile.utilities.PathGenerator;
import ugr.gbv.cognimobile.utilities.Point;

/**
 * Class to display the task type "Draw":
 * {@link Task#GRAPH}
 * {@link Task#CUBE}
 * {@link Task#WATCH}
 */
public class DrawTask extends Task implements LoadDraw {


    private DrawingView drawingView;
    private ArrayList<Point> sequence;
    private ArrayList<String> answer;
    private View view;
    private LinearLayout leftButtonContainer;
    private int undoTimes;
    private Bundle bundle;

    private final ArrayList<Button> pressedButtons;
    private final ArrayList<String> alreadyPressedButtons;
    private final ArrayList<Long> timeBetweenClicks;


    /**
     * Constructor
     *
     * @param taskType type of the task
     * @param callBack callback to pass the parent the events
     * @param bundle   bundle of information to be filled into the task
     */
    public DrawTask(int taskType, LoadContent callBack, @Nullable Bundle bundle) {
        this.taskType = taskType;
        pressedButtons = new ArrayList<>();
        alreadyPressedButtons = new ArrayList<>();
        timeBetweenClicks = new ArrayList<>();
        this.callBack = callBack;
        if (bundle != null) {
            this.bundle = bundle;
        }
    }


    /**
     * Overrides {@link androidx.fragment.app.Fragment#onViewCreated(View, Bundle)}
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(this::shouldDisplayHelpAtBeginning);
    }

    /**
     * Overrides {@link androidx.fragment.app.Fragment#onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * Also sets all the necessary elements for the task to be displayed and be completed.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.draw_task, container, false);
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

        displayHelpAtBeginning = bundle.getBoolean("display_help");


        return view;
    }

    /**
     * Decrements the undoTimes variable
     */
    private void decrementUndoAction() {
        undoTimes--;
    }

    /**
     * Checks if the user can click the undo button
     *
     * @return true if the user has the possibility, false if not.
     */
    private boolean checkIfHasUndoActions() {
        return undoTimes > 0;
    }

    /**
     * Restores the last button, only on task type {@link Task#GRAPH}.
     */
    private void undoLastButton() {

        if (answer.size() > 0) {
            answer.remove(answer.size() - 1);
            pressedButtons.get(pressedButtons.size() - 1).setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.circle_no_fill, context.getTheme()));
            pressedButtons.get(pressedButtons.size() - 1).setTextColor(getResources().getColor(R.color.black, context.getTheme()));
            pressedButtons.remove(pressedButtons.size() - 1);
        }

    }

    /**
     * Displays how many attempts the user has left to push the undo button.
     */
    private void showUndoTimes() {
        if (undoTimes > 0)
            Toast.makeText(context, getResources().getString(R.string.times_left, undoTimes), Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context, getResources().getString(R.string.no_times_left), Toast.LENGTH_LONG).show();
    }

    /**
     * Draws on top of the canvas the buttons to be clicked, only on task type {@link Task#GRAPH}.
     *
     * @param points Coordinates where the points will be drawn.
     */
    private void drawButtons(ArrayList<Point> points) {
        CardView layout = view.findViewById(R.id.cardView);

        String[] tags = context.getResources().getStringArray(R.array.graphsValues);


        for (int i = 0; i < points.size(); ++i) {
            Button button = new Button(context);
            button.setId(View.generateViewId());
            button.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.circle_no_fill, context.getTheme()));
            button.setText(tags[i]);
            button.setTextSize(20);
            button.setTag(points.get(i).getLabel());
            button.setOnClickListener(v -> {
                Button graphCircle = (Button) v;
                boolean continua = true;
                for (int k = 0; k < sequence.size() && continua; ++k) {
                    if (graphCircle.getTag().equals(sequence.get(k).getLabel())) {
                        if(!answer.contains(graphCircle.getTag().toString())){
                            graphCircle.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.circle_with_fill, context.getTheme()));
                            graphCircle.setTextColor(getResources().getColor(R.color.white,context.getTheme()));
                            answer.add(graphCircle.getTag().toString());
                            drawingView.drawToPoint(sequence.get(k));
                            continua = false;
                            pressedButtons.add(graphCircle);
                            timeBetweenClicks.add(ContextDataRetriever.addTimeStamp());
                            startedTask();
                        } else {
                            alreadyPressedButtons.add(graphCircle.getTag().toString());
                        }
                    }
                }

            });
            layout.addView(button);
            int dimens = getResources().getDimensionPixelSize(R.dimen.circle);
            int halfDimen = dimens / 2;
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(dimens, dimens);
            button.setLayoutParams(lp);
            button.setX(points.get(i).getX() - halfDimen);
            button.setY(points.get(i).getY() - halfDimen);


        }


    }


    /**
     * Overrides {@link LoadDraw#loadDraw()}
     */
    @Override
    public void loadDraw() {
        if (!loaded) {

            ImageView imageView = view.findViewById(R.id.banner_image);
            bannerText = view.findViewById(R.id.banner_text);

            TextView label = view.findViewById(R.id.leftButtonLabel);
            switch (taskType) {
                case GRAPH:
                    int height = drawingView.getCanvasHeight();
                    int width = drawingView.getCanvasWidth();
                    PathGenerator pathGenerator = new PathGenerator();
                    sequence = pathGenerator.makePath(height, width);
                    drawButtons(sequence);
                    resultTask.setPatternSequence(Arrays.stream(pathGenerator.getNumbers())
                            .boxed().collect(Collectors.toList()));
                    bannerText.setText(R.string.graph_instructions);
                    break;
                case CUBE:
                    leftButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_delete_forever_black_24dp, context.getTheme()));
                    label.setText(R.string.clear_title_button);
                    bannerText.setText(R.string.cube_instructions);
                    imageView.setVisibility(View.VISIBLE);
                    break;
                case WATCH:
                    leftButton.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.ic_delete_forever_black_24dp, context.getTheme()));
                    label.setText(R.string.clear_title_button);
                    bannerText.setText(context.getResources().getString(R.string.clock_instructions, bundle.getString("hour")));
                    break;
                default:
                    throw new RuntimeException("Task type not supported");
            }

            leftButtonContainer.setVisibility(View.VISIBLE);
            loaded = true;
        }
    }

    /**
     * Overrides {@link LoadDraw#startedTask()}
     * This determines if a task has been started to skip it without having a confirmation
     * {@link Task#setNextButtonStandardBehaviour()} has the logic of that confirmation
     */
    @Override
    public void startedTask() {
        taskEnded = true;
    }


    /**
     * Overrides {@link Task#saveResults()}
     */
    @Override
    void saveResults() {
        resultTask.setTaskType(TaskType.values()[taskType]);
        resultEvent.setTaskType(TaskType.values()[taskType]);
        resultTask.setHeight(drawingView.getCanvasHeight());
        resultTask.setWidth(drawingView.getCanvasWidth());
        resultTask.setPointsSequence(convertFloatToDoubleList(drawingView.getDrawnPath()));

        switch (taskType) {
            case GRAPH:
                setScoring();
                resultTask.setAnswer(answer);
                resultTask.setScore(score);
                resultTask.setErasedPaths(convertFloatToDoubleList(drawingView.getErasedPath()));
                List<Double> list = new ArrayList<>();
                sequence.forEach( point -> {
                    list.add((double) point.getX());
                    list.add((double) point.getY());
                });
                resultTask.setPointsSequence(list);

                resultEvent.setSpecificATMAlreadyClickedButton(ContextDataRetriever.retrieveInformationFromStringArrayList(alreadyPressedButtons));
                resultEvent.setSpecificATMPoints(convertFloatToDoubleList(drawingView.getDrawnPath()));

                resultEvent.setSpecificATMDistanceBetweenCircles(ContextDataRetriever.retrieveInformationFromButtonArrayList(pressedButtons));
                resultEvent.setSpecificATMTimeBetweenClicks(ContextDataRetriever.retrieveInformationFromLongArrayList(timeBetweenClicks));
                break;
            case CUBE:
                resultTask.setTimes_wipe_canvas(getResources().getInteger(R.integer.undo_times) - undoTimes);
                Pair<String, String> cubeTraces = packTraces();
                resultEvent.setSpecificVSCubeStartDraw(cubeTraces.first);
                resultEvent.setSpecificVSCubeEndDraw(cubeTraces.second);
                resultEvent.setSpecificVSCubePoints(convertFloatToDoubleList(drawingView.getDrawnPath()));
                break;
            case WATCH:
                resultTask.setTimes_wipe_canvas(getResources().getInteger(R.integer.undo_times) - undoTimes);
                Pair<String, String> clockTraces = packTraces();
                resultEvent.setSpecificVSClockStartDraw(clockTraces.first);
                resultEvent.setSpecificVSClockEndDraw(clockTraces.second);
                resultEvent.setSpecificVSClockPoints(convertFloatToDoubleList(drawingView.getDrawnPath()));
                break;
            default:
                throw new RuntimeException("INVALID TASKTYPE");

        }


    }

    private List<Double> convertFloatToDoubleList(float[] drawnPath) {
        List<Double> doubles = new ArrayList<>();
        for (float number:drawnPath){
            doubles.add((double) number);
        }
        return doubles;
    }


    /**
     * Converts the points where the traces(starting point and end point) that user has drawn
     * on the canvas.
     *
     * @return a pair, where the first one is the first positions the user has drawn (x:y) and
     * on the second element of the pair is the last positions the user has drawn (x:y)
     */
    private Pair<String, String> packTraces() {
        float[] drawnTraces = drawingView.getDrawnTraces();
        StringBuilder startStringBuilder = new StringBuilder();
        StringBuilder endStringBuilder = new StringBuilder();
        for (int i = 0, k = 0; k < drawnTraces.length; ++i, k += 2) {
            if (i % 2 == 0) {
                startStringBuilder.append(drawnTraces[k]);
                startStringBuilder.append(":");
                startStringBuilder.append(drawnTraces[k + 1]);
                startStringBuilder.append(",");
            } else {
                endStringBuilder.append(drawnTraces[k]);
                endStringBuilder.append(":");
                endStringBuilder.append(drawnTraces[k + 1]);
                endStringBuilder.append(",");
            }

        }
        if (startStringBuilder.length() > 0)
            startStringBuilder.deleteCharAt(startStringBuilder.length() - 1);
        if (endStringBuilder.length() > 0)
            endStringBuilder.deleteCharAt(endStringBuilder.length() - 1);
        return new Pair<>(startStringBuilder.toString(), endStringBuilder.toString());
    }

    /**
     * Overrides {@link Task#setScoring()}
     */
    @Override
    void setScoring() {
        int size = answer.size();
        boolean goOn = true;
        score = size > 0 ? 1 : 0;
        for (int i = 0; i < size && goOn; ++i)
            if (!answer.get(i).equals(Integer.toString(i + 1))) {
                goOn = false;
                score = 0;
            }
    }
}
