package ugr.gbv.myapplication.utilities;

import java.io.Serializable;

public class Point implements Serializable {
    private float x;
    private float y;
    private String label;

    public Point(){
        x = 0;
        y = 0;
        label = "";
    }
    public Point(float xValue, float yValue){
        x = xValue;
        y = yValue;
        label = "";
    }


    public Point(float xValue, float yValue, String label){
        x = xValue;
        y = yValue;
        this.label = label;
    }


    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

    public String getLabel(){
        return label;
    }

    public void setX(float x) {
        this.x = x;
    }
    public void setY(float y) {
        this.y = y;
    }
    public void setLabel(String label) {
        this.label = label;
    }
}
