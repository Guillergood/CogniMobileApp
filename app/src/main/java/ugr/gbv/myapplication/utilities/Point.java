package ugr.gbv.myapplication.utilities;

import java.io.Serializable;

public class Point implements Serializable {
    private float x;
    private float y;
    private String label;

    Point(float xValue, float yValue){
        x = xValue;
        y = yValue;
        label = "";
    }


    Point(float xValue, float yValue, String label){
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
}
