package ugr.gbv.myapplication.utilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class AV {
    //TODO CAMBIAR TODO ESTO PORQUE YA NO TIENE SENTIDO EL ANGLUIN AND VALIANT

    public ArrayList<Point> makePath(int height, int width) {
        // construct an n-by-n grid
        /*int n = 4;
        Node[][] node = new Node[n][n];
        List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                nodes.add((node[i][j] = new Node()));
            }
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i >= 1) {
                    if (j >= 1) {
                        node[i - 1][j - 1].addEdge(node[i][j]);
                    }
                    node[i - 1][j].addEdge(node[i][j]);
                    if (j < n - 1) {
                        node[i - 1][j + 1].addEdge(node[i][j]);
                    }
                }
                if (j >= 1) {
                    node[i][j - 1].addEdge(node[i][j]);
                }
            }
        }
        findPath(nodes);
        labelPath(nodes);

        ArrayList<Point> puntos = new ArrayList<>();
        float xProportion = width/4f;
        float yProportion = height/4f;
        int a = 0;
        for (int i = 0; i < n && a < 10; i++) {
            for (int j = 0; j < n && a < 10; j++) {
                float x = i*xProportion;
                float y = j*yProportion;
                puntos.add(new Point(x, y,Integer.toString(node[i][j].label)));
                ++a;
                if(node[i][j].label % 2 == 0){
                    char letra =(char)('A' + ((node[i][j].label/2)-1));
                    if(letra >= 'A' &&  letra <= 'E') {
                        puntos.add(new Point(x, y));
                    }
                }
                else{
                    int operacion = (node[i][j].label/2)+1;
                    if(operacion >= 1 &&  operacion <= 5) {
                        puntos.add(new Point(x, y));
                    }
                }
            }
        }

        char character = 'A';
        int index = 1;
        int threshold = 'A' - '1';

        for (Point p:puntos) {

            int operation = character - (char)(index+'0');
            if(operation == threshold) {
                p.setLabel(Integer.toString(index));
                index++;
            }
            else{
                p.setLabel(Character.toString(character));
                character++;
            }
        }*/
        /*Collections.sort(puntos, new Comparator<Point>() {
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

        for (int i = 0; i < a-4; i++){
            boolean resultado = intersects(
                    puntos.get(i).getX(),puntos.get(i).getY(),
                    puntos.get(i+1).getX(),puntos.get(i+1).getY(),
                    puntos.get(i+2).getX(),puntos.get(i+2).getY(),
                    puntos.get(i+3).getX(),puntos.get(i+3).getY()
            );

            if(resultado){
                Log.d("ASD"," INTERSECCION PUNTOS " + i + (i+1) + (i+2) + (i+3) );
            }
        }*/



        ArrayList<Point> puntos = new ArrayList<>();

        //TODO CAMBIAR ESTO TAMBIEN PARA QUE ESTE EN VALORES
        int[]numbers = new int[10];
        Random r = new Random();
        numbers[0] = r.nextInt(10 - 1) + 1;
        for (int i = 1; i < 10; ++i)
            numbers[i] = (numbers[i-1] % 10) + 1 ;


        puntos.add( new Point( 0.30859375f * height , 0.67083335f * width , Integer.toString(numbers[0])));
        puntos.add( new Point( 0.4638672f * height , 0.36666667f * width , Integer.toString(numbers[1])));
        puntos.add( new Point( 0.5703125f * height , 0.5708333f * width , Integer.toString(numbers[2])));
        puntos.add( new Point( 0.453125f * height , 0.625f * width , Integer.toString(numbers[3])));
        puntos.add( new Point( 0.57421875f * height , 0.85694444f * width , Integer.toString(numbers[4])));
        puntos.add( new Point( 0.3544922f * height , 0.98055553f * width , Integer.toString(numbers[5])));
        puntos.add( new Point( 0.3857422f * height , 0.80277777f * width , Integer.toString(numbers[6])));
        puntos.add( new Point( 0.14355469f * height , 0.90694445f * width , Integer.toString(numbers[7])));
        puntos.add( new Point( 0.13378906f * height , 0.55972224f * width , Integer.toString(numbers[8])));
        puntos.add( new Point( 0.27148438f * height , 0.3402778f * width , Integer.toString(numbers[9])));

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


    private static void findPath(List<Node> nodes) {
        for (Node node : nodes) {
            node.isOnPath = false;
        }
        Random random = new Random();
        Node sink = nodes.get(random.nextInt(nodes.size()));
        sink.isOnPath = true;
        int isNotOnPathCount = nodes.size() - 1;
        while (isNotOnPathCount > 0) {
            sink.pathOut = sink.out.get(random.nextInt(sink.out.size()));
            sink = sink.pathOut.head;
            if (sink.isOnPath) {
                // rotate
                sink = sink.pathOut.head;
                Arc reverse = null;
                Node node = sink;
                do {
                    Arc temp = node.pathOut;
                    node.pathOut = reverse;
                    reverse = temp.reverse;
                    node = temp.head;
                } while (node != sink);
            } else {
                // extend
                sink.isOnPath = true;
                isNotOnPathCount--;
            }
        }
    }

    private static void labelPath(Collection<Node> nodes) {
        for (Node node : nodes) {
            node.isSource = true;
        }
        for (Node node : nodes) {
            if (node.pathOut != null) {
                node.pathOut.head.isSource = false;
            }
        }
        Node source = null;
        for (Node node : nodes) {
            if (node.isSource) {
                source = node;
                break;
            }
        }
        int count = 0;
        while (true) {
            if (source != null) {
                source.label = ++count;

                if (source.pathOut == null) {
                    break;
                }
                source = source.pathOut.head;
            }
        }
    }


    boolean intersects(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        float bx = x2 - x1;
        float by = y2 - y1;
        float dx = x4 - x3;
        float dy = y4 - y3;
        float b_dot_d_perp = bx * dy - by * dx;
        if (b_dot_d_perp == 0) {
            return false;
        }
        float cx = x3 - x1;
        float cy = y3 - y1;
        float t = (cx * dy - cy * dx) / b_dot_d_perp;
        if (t < 0 || t > 1) {
            return false;
        }
        float u = (cx * by - cy * bx) / b_dot_d_perp;
        if (u < 0 || u > 1) {
            return false;
        }
        return true;
    }
}

class Node {
    final List<Arc> out = new ArrayList<Arc>();
    boolean isOnPath;
    Arc pathOut;
    boolean isSource;
    int label;

    void addEdge(Node that) {
        Arc arc = new Arc(this, that);
        this.out.add(arc.reverse);
        that.out.add(arc);
    }
}

class Arc {
    final Node head;
    final Arc reverse;

    private Arc(Node head, Arc reverse) {
        this.head = head;
        this.reverse = reverse;
    }

    Arc(Node head, Node tail) {
        this.head = head;
        this.reverse = new Arc(tail, this);
    }
}