package ugr.gbv.myapplication.utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class PathGenerator {

    /**
     * Método para generar los puntos en orden que tienen que ser recorridos para formar el grafo
     *
     *
     * @param height Altura de la zona de pintado
     * @param width Anchura de la zona de pintado
     * @return Los puntos que conforman el grafo, para ser pintado por DrawTask
     */
    public ArrayList<Point> makePath(int height, int width) {


        height -= height/4;
        width += width/4;


        ArrayList<Point> puntos = new ArrayList<>();

        int[]numbers = new int[10];
        Random r = new Random();
        numbers[0] = r.nextInt(10 - 1) + 1;
        for (int i = 1; i < 10; ++i)
            numbers[i] = (numbers[i-1] % 10) + 1 ;


        /*puntos.add( new Point( 0.30859375f * height , 0.67083335f * width , Integer.toString(numbers[0])));
        puntos.add( new Point( 0.4638672f * height , 0.36666667f * width , Integer.toString(numbers[1])));
        puntos.add( new Point( 0.5703125f * height , 0.5708333f * width , Integer.toString(numbers[2])));
        puntos.add( new Point( 0.453125f * height , 0.625f * width , Integer.toString(numbers[3])));
        puntos.add( new Point( 0.57421875f * height , 0.85694444f * width , Integer.toString(numbers[4])));
        puntos.add( new Point( 0.3544922f * height , 0.98055553f * width , Integer.toString(numbers[5])));
        puntos.add( new Point( 0.3857422f * height , 0.80277777f * width , Integer.toString(numbers[6])));
        puntos.add( new Point( 0.14355469f * height , 0.90694445f * width , Integer.toString(numbers[7])));
        puntos.add( new Point( 0.13378906f * height , 0.55972224f * width , Integer.toString(numbers[8])));
        puntos.add( new Point( 0.27148438f * height , 0.3402778f * width , Integer.toString(numbers[9])));*/

        puntos.add( new Point( 0.30859375f * width , 0.67083335f * height , Integer.toString(numbers[0])));
        puntos.add( new Point( 0.4638672f * width , 0.36666667f * height , Integer.toString(numbers[1])));
        puntos.add( new Point( 0.5703125f * width , 0.5708333f * height , Integer.toString(numbers[2])));
        puntos.add( new Point( 0.453125f * width , 0.625f * height , Integer.toString(numbers[3])));
        puntos.add( new Point( 0.57421875f * width , 0.85694444f * height , Integer.toString(numbers[4])));
        puntos.add( new Point( 0.3544922f * width , 0.98055553f * height , Integer.toString(numbers[5])));
        puntos.add( new Point( 0.3857422f * width , 0.80277777f * height , Integer.toString(numbers[6])));
        puntos.add( new Point( 0.14355469f * width , 0.90694445f * height , Integer.toString(numbers[7])));
        puntos.add( new Point( 0.13378906f * width , 0.55972224f * height , Integer.toString(numbers[8])));
        puntos.add( new Point( 0.27148438f * width , 0.3402778f * height , Integer.toString(numbers[9])));

        /*puntos.add( new Point( 0.67083335f * width  , 0.30859375f * height , Integer.toString(numbers[0])));
        puntos.add( new Point( 0.36666667f * width , 0.4638672f * height ,  Integer.toString(numbers[1])));
        puntos.add( new Point( 0.5708333f * width ,  0.5703125f * height , Integer.toString(numbers[2])));
        puntos.add( new Point( 0.625f * width ,  0.453125f * height , Integer.toString(numbers[3])));
        puntos.add( new Point( 0.85694444f * width , 0.57421875f * height ,  Integer.toString(numbers[4])));
        puntos.add( new Point( 0.98055553f * width , 0.3544922f * height ,  Integer.toString(numbers[5])));
        puntos.add( new Point( 0.80277777f * width , 0.3857422f * height ,  Integer.toString(numbers[6])));
        puntos.add( new Point( 0.90694445f * width , 0.14355469f * height ,  Integer.toString(numbers[7])));
        puntos.add( new Point( 0.55972224f * width , 0.13378906f * height ,  Integer.toString(numbers[8])));
        puntos.add( new Point( 0.3402778f * width ,  0.27148438f * height , Integer.toString(numbers[9])));*/

        Collections.sort(puntos, new Comparator<Point>() {
            @Override
            public int compare(Point o1, Point o2) {
                int value = 1;
                if(Integer.parseInt(o1.getLabel()) < Integer.parseInt(o2.getLabel())){
                    value = -1;
                }
                else if(Integer.parseInt(o1.getLabel()) == Integer.parseInt(o2.getLabel())){
                    value = 0;
                }
                return value;
            }
        });



        return puntos;

    }

}