package ugr.gbv.cognimobile.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;

import ugr.gbv.cognimobile.interfaces.LoadDraw;

/**
 * Class to wrap the canvas to draw
 */
public class DrawingView extends View {

    private Path drawPath;
    private Paint drawPaint, canvasPaint;
    private Bitmap canvasBitmap;
    private Canvas drawCanvas;
    private ArrayList<Point> drawnTraces;
    private ArrayList<Point> drawPoints;
    private ArrayList<Point> erasedPoints;
    boolean initialized = false;
    private LoadDraw callBack;
    private boolean freeDrawing;

    /**
     * Constructor
     *
     * @param context from the parent activity
     * @param attrs   attribute set to initialize the view
     */
    public DrawingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        drawnTraces = new ArrayList<>();
        drawPoints = new ArrayList<>();
        erasedPoints = new ArrayList<>();
        setUpDrawing();
    }

    /**
     * Initialization of the draw path, canvas and draw paint
     */
    private void setUpDrawing() {
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(Color.parseColor("#000000"));
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(8);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    /**
     * Override {@link View#onSizeChanged(int, int, int, int)}
     *
     * @param w    Current width of this view.
     * @param h    Current height of this view.
     * @param oldw Old width of this view.
     * @param oldh Old height of this view.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    /**
     * Override {@link View#onDraw(Canvas)}
     *
     * @param canvas the canvas on which the background will be drawn
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
        callBack.loadDraw();
    }

    /**
     * Override {@link View#onTouchEvent(MotionEvent)}
     *
     * @param event The motion event.
     * @return True if the event was handled, false otherwise.
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (freeDrawing) {
            int action = event.getAction();
            float touchX = event.getX();
            float touchY = event.getY();


            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    drawPath.moveTo(touchX, touchY);
                    drawPath.lineTo(touchX, touchY);
                    drawPoints.add(new Point(touchX, touchY));
                    drawnTraces.add(new Point(touchX, touchY));
                    break;

                case MotionEvent.ACTION_MOVE:
                    drawPath.lineTo(touchX, touchY);
                    drawPoints.add(new Point(touchX, touchY));
                    break;

                case MotionEvent.ACTION_UP:
                    drawPath.moveTo(touchX, touchY);
                    drawPoints.add(new Point(touchX, touchY));
                    drawnTraces.add(new Point(touchX, touchY));
                    break;

                default:
                    return false;
            }


            invalidate();
        }
        return freeDrawing;
    }

    /**
     * Clears the canvas
     */
    public void clearCanvas() {
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        drawPath.reset();
        initialized = false;
        invalidate();
    }

    /**
     * Getter for the canvas height
     *
     * @return the canvas height
     */
    public int getCanvasHeight() {
        return canvasBitmap.getHeight();
    }

    /**
     * Getter for the canvas width
     *
     * @return the canvas width
     */
    public int getCanvasWidth() {
        return canvasBitmap.getWidth();
    }

    /**
     * Draws a line from the last point to the new one.
     *
     * @param point new point where the line will finish
     */
    public void drawToPoint(Point point) {

        if (initialized) {
            drawPath.lineTo(point.getX(), point.getY());
            drawPath.moveTo(point.getX(), point.getY());
        } else {
            drawPath.moveTo(point.getX(), point.getY());
            initialized = true;
        }

        drawPoints.add(point);

        drawCanvas.save();

        invalidate();
    }

    /**
     * Sets the callback with the parent activity
     *
     * @param callBack          to call the parent activity methods
     * @param isTaskFreeDrawing flag to enable the free drawing
     */
    public void setCallBack(LoadDraw callBack, boolean isTaskFreeDrawing) {
        this.callBack = callBack;
        freeDrawing = isTaskFreeDrawing;
    }

    /**
     * Undo the last operation in the canvas.
     */
    public void undoLastOperation() {
        if (freeDrawing) {
            drawPoints.clear();
            clearCanvas();
        } else {
            if (drawPoints.size() > 0) {
                if (drawPoints.size() > 1) {
                    erasedPoints.add(drawPoints.get(drawPoints.size() - 2));
                    erasedPoints.add(drawPoints.get(drawPoints.size() - 1));
                }
                drawPoints.remove(drawPoints.size() - 1);
                clearCanvas();
                drawPath();
            }
        }
    }

    /**
     * Draw the path store, this is done when the user undo the last operation.
     * The canvas clears and the path is redrawn.
     */
    private void drawPath() {
        for (Point point : drawPoints) {
            if (initialized) {
                drawPath.lineTo(point.getX(), point.getY());
                drawPath.moveTo(point.getX(), point.getY());
            } else {
                drawPath.moveTo(point.getX(), point.getY());
                initialized = true;
            }
        }

    }

    /**
     * Getter for the drawn path, to be included in both result json and contextual json
     *
     * @return array of floats containing the coordinates of every point.
     */
    public float[] getDrawnPath() {
        return retrievePointsFromArrayList(drawPoints);
    }

    /**
     * Getter for the erased path that the user has undone, to be included in both result json and contextual json
     *
     * @return array of floats containing the coordinates of every point.
     */
    public float[] getErasedPath() {
        return retrievePointsFromArrayList(erasedPoints);
    }

    /**
     * Getter for the traces of the draw, where every line starts and ends,
     * to be included in both result json and contextual json
     *
     * @return array of floats containing the coordinates of every point.
     */
    public float[] getDrawnTraces() {
        return retrievePointsFromArrayList(drawnTraces);
    }

    /**
     * Method to retrieve the coordinates from an arraylist of Points.
     *
     * @param arrayList of Points
     * @return array of float coordinates
     */
    private float[] retrievePointsFromArrayList(ArrayList<Point> arrayList) {
        int size = arrayList.size() * 2;

        float[] value = new float[size];

        for (int i = 0, k = 0; i < size; i += 2, ++k) {
            value[i] = arrayList.get(k).getX();
            value[i + 1] = arrayList.get(k).getY();
        }

        return value;
    }


}
