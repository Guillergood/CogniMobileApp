package ugr.gbv.myapplication.utilities;

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

import ugr.gbv.myapplication.interfaces.LoadContent;

public class DrawingView extends View {

    private Path drawPath;
    private Paint drawPaint,canvasPaint;
    private Bitmap canvasBitmap;
    private Canvas drawCanvas;
    private ArrayList<Point> points;
    boolean initialized = false;
    private LoadContent callBack;
    private boolean freeDrawing;
    private ArrayList<Path> paths = new ArrayList<>();

    public DrawingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        points = new ArrayList<>();
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
                    break;

                case MotionEvent.ACTION_MOVE:
                    drawPath.lineTo(touchX, touchY);
                    break;

                case MotionEvent.ACTION_UP:
                    drawPath.moveTo(touchX, touchY);
                    break;

                default:
                    return false;
            }

            paths.add(drawPath);

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

        points.add(punto);


        drawCanvas.save();


        invalidate();
    }

    public void setCallBack(LoadContent callBack,boolean isTaskFreeDrawing){
        this.callBack = callBack;
        freeDrawing = isTaskFreeDrawing;
    }

    public void undoLastOperation(){
        if(freeDrawing){
            clearCanvas();
        }
        else {
            if (points.size() > 0) {
                points.remove(points.size() - 1);
                clearCanvas();
                drawPath();
            }
        }
    }

    private void drawPath(){

        for (Point point : points) {
            if (initialized) {
                drawPath.lineTo(point.getX(), point.getY());
                drawPath.moveTo(point.getX(), point.getY());
            } else {
                drawPath.moveTo(point.getX(), point.getY());
                initialized = true;
            }
        }

    }
}
