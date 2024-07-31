package ugr.gbv.cognimobile.utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class PathGenerator {


    private int[] numbers;

    /**
     * Generates the points for the graph in the {@link ugr.gbv.cognimobile.fragments.Task#GRAPH}
     *
     * @param height of the canvas
     * @param width  of the canvas
     * @return The points to be displayed in {@link ugr.gbv.cognimobile.fragments.DrawTask}
     */
    public ArrayList<Point> makePath(int height, int width) {


        height -= height/4;
        width += width/4;


        ArrayList<Point> points = new ArrayList<>();

        numbers = new int[10];
        Random r = new Random();
        numbers[0] = r.nextInt(10 - 1) + 1;
        for (int i = 1; i < 10; ++i)
            numbers[i] = (numbers[i - 1] % 10) + 1;


        points.add(new Point(0.30859375f * width, 0.42083335f * height, Integer.toString(numbers[0])));
        points.add(new Point(0.4638672f * width, 0.11666667f * height, Integer.toString(numbers[1])));
        points.add(new Point(0.5703125f * width, 0.2208333f * height, Integer.toString(numbers[2])));
        points.add(new Point(0.453125f * width, 0.375f * height, Integer.toString(numbers[3])));
        points.add(new Point(0.57421875f * width, 0.60694444f * height, Integer.toString(numbers[4])));
        points.add(new Point(0.3544922f * width, 0.73055553f * height, Integer.toString(numbers[5])));
        points.add(new Point(0.3857422f * width, 0.55277777f * height, Integer.toString(numbers[6])));
        points.add(new Point(0.14355469f * width, 0.65694445f * height, Integer.toString(numbers[7])));
        points.add(new Point(0.13378906f * width, 0.30972224f * height, Integer.toString(numbers[8])));
        points.add(new Point(0.27148438f * width, 0.0902778f * height, Integer.toString(numbers[9])));


        points.sort((o1, o2) -> {
            int value = 1;
            if (Integer.parseInt(o1.getLabel()) < Integer.parseInt(o2.getLabel())) {
                value = -1;
            } else if (Integer.parseInt(o1.getLabel()) == Integer.parseInt(o2.getLabel())) {
                value = 0;
            }
            return value;
        });


        return points;

    }

    /**
     * Getter for the index numbers, to be displayed in {@link ugr.gbv.cognimobile.fragments.DrawTask}
     *
     * @return index numbers, to be displayed in {@link ugr.gbv.cognimobile.fragments.DrawTask}
     */
    public int[] getNumbers() {
        return numbers;
    }
}