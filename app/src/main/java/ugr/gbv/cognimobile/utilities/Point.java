package ugr.gbv.cognimobile.utilities;

import java.io.Serializable;

/**
 * Class to wrap the coordinates of the points in {@link ugr.gbv.cognimobile.fragments.DrawTask}
 */
public class Point implements Serializable {
    private float x;
    private float y;
    private String label;

    /**
     * Constructor
     *
     * @param xValue x value
     * @param yValue y value
     */
    Point(float xValue, float yValue) {
        x = xValue;
        y = yValue;
        label = "";
    }

    /**
     * Constructor
     *
     * @param xValue x value
     * @param yValue y value
     * @param label  to be displayed inside of the button
     */
    Point(float xValue, float yValue, String label) {
        x = xValue;
        y = yValue;
        this.label = label;
    }


    /**
     * Getter for the x value.
     *
     * @return the x value.
     */
    public float getX() {
        return x;
    }

    /**
     * Setter for the x value.
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Getter for the y value.
     *
     * @return the y value.
     */
    public float getY() {
        return y;
    }

    /**
     * Setter for the y value.
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * Getter for the label.
     *
     * @return the label.
     */
    public String getLabel() {
        return label;
    }
}
