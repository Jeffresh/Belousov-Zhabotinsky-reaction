import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class belZab
{
    public  float [][][] a;
    public  float [][][] b;
    public  float [][][] c;

    private float alpha,beta,gamma;
    public  int p ,q;
    private int width, height;
    public  int generations;

    public belZab() {}


    private  void randomInitializer() {
        for(int x = 0 ; x < width; x++)
            for(int y = 0; y < height; y++) {
                a[x ][ y ][ p] = (float)Math.random ();
                b[x ][ y ][ p] = (float)Math.random ();
                c[x ][ y ][ p] = (float)Math.random ();
            }
    }


    public void initializer (int cells_number, int generations, int cfrontier, float alpha, float beta, float gamma) {

        width = cells_number;
        height = cells_number;
        this.generations = generations;
        this.alpha = alpha;
        this.beta = beta;
        this.gamma = gamma;

        p = 0;
        q = 1;

        this.a = new float [width][height][2];
        this.b = new float [width][height][2];
        this.c = new float [width][height][2];
        this.randomInitializer();


    }

    public void changeRefs() {
        if( p == 0) {
            p = 1;
            q = 0;
        }
        else {
            p = 0;
            q = 1;
        }
    }

    private float computeVonNeumannNeighborhood(int i, int j, float[][][] matrix) {
        float cellsAlive = 0 ;

        for(int x = i-1; x<=i+1; x++) {
            for(int y = j-1; y<=j+1; y++) {
                cellsAlive += matrix[(x+ width )% width ][(y+ height )% height ][ p ];
            }
        }

        return cellsAlive;
    }

    private float transitionFunction(float a, float b , float c, float factor1, float factor2) {
        float value = a + a * ( factor1 * b - factor2 * c );

        if(value < 0)
            value=  0;
        if(value > 1)
            value = 1;

        return value;
    }

    public void nextGen() {

        for (int g = 0 ; g < generations; g++) {
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    float c_a = 0;
                    float c_b = 0;
                    float c_c = 0;

                    c_a = computeVonNeumannNeighborhood(x, y, a) / (float) 9.0;
                    c_b = computeVonNeumannNeighborhood(x, y, b) / (float) 9.0;
                    c_c = computeVonNeumannNeighborhood(x, y, c) / (float) 9.0;

                    a[x][y][q] = transitionFunction(c_a, c_b, c_c, alpha, gamma);
                    b[x][y][q] = transitionFunction(c_b, c_c, c_a, beta, alpha);
                    c[x][y][q] = transitionFunction(c_c, c_a, c_b, gamma, beta);

                }
                changeRefs();
            }
        }
    }

    public static void main(String[] args) {
        belZab simulation = new belZab();
        simulation.initializer(200, 200, 0 , 1, 1, 1);
        simulation.nextGen();

    }

}