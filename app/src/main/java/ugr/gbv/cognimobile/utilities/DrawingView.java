package ugr.gbv.cognimobile.utilities;

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

import ugr.gbv.cognimobile.interfaces.LoadContent;

public class DrawingView extends View {

    private Path drawPath;
    private Paint drawPaint,canvasPaint;
    private Bitmap canvasBitmap;
    private Canvas drawCanvas;
    private ArrayList<Point> drawnTraces;
    private ArrayList<Point> drawPoints;
    private ArrayList<Point> erasedPoints;
    boolean initialized = false;
    private LoadContent callBack;
    private boolean freeDrawing;

    public DrawingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        drawnTraces = new ArrayList<>();
        drawPoints = new ArrayList<>();
        erasedPoints = new ArrayList<>();
        setUpDrawing();
    }

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

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(canvasBitmap,0,0,canvasPaint);
        canvas.drawPath(drawPath,drawPaint);
        callBack.loadContent();
    }

   @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(freeDrawing) {
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

    public void clearCanvas()
    {
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        drawPath.reset();
        initialized = false;
        invalidate();
    }

    public int getCanvasHeight(){
        return canvasBitmap.getHeight();
    }

    public int getCanvasWidth(){
        return canvasBitmap.getWidth();
    }

    public void drawToPoint(Point punto) {

        if (initialized) {
            drawPath.lineTo(punto.getX(), punto.getY());
            drawPath.moveTo(punto.getX(), punto.getY());
        } else {
            drawPath.moveTo(punto.getX(), punto.getY());
            initialized = true;
        }

        drawPoints.add(punto);

        drawCanvas.save();

        invalidate();
    }

    public void setCallBack(LoadContent callBack,boolean isTaskFreeDrawing){
        this.callBack = callBack;
        freeDrawing = isTaskFreeDrawing;
    }

    public void undoLastOperation(){
        if(freeDrawing){
            drawPoints.clear();
            clearCanvas();
        }
        else {
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

    private void drawPath(){
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

    public float[] getDrawnPath() {
        return retrievePointsFromArrayList(drawPoints);
    }

    public float[] getErasedPath() {
        return retrievePointsFromArrayList(erasedPoints);
    }

    public float[] getDrawnTraces() {
        return retrievePointsFromArrayList(drawnTraces);
    }

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
