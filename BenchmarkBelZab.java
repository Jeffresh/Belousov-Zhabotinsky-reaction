import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * ClassNV.java
 * Purpose: generic Class that you can modify and adapt easily for any application
 * that need data visualization.
 * @author: Jeffrey Pallarés Núñez.
 * @version: 1.0 23/07/19
 */

public class BenchmarkBelZab implements Runnable
{
    public static float [][][] a;
    public static float [][][] b;
    public static float [][][] c;

    private float c_a;
    private float c_b;
    private float c_c;

    private static float alpha,beta,gamma;
    public static int p ,q;
    private static int width, height;

    public static int states_number = 2;
    private static int cfrontier = 0;
    private static int cells_number;
    public static int generations;

    private int task_number;
    private static int total_tasks;
    private static CyclicBarrier barrier = null;
    private int in;
    private int fn;
    public static Boolean abort = false;
    private static int gens;
    private static int size_pool;
    private static ThreadPoolExecutor myPool;


    public void run() {

        for (int i = 0; i < generations-1 ; i++) {
            if(abort)
                break;
            nextGen(i);

            try
            {
                int l = barrier.await();

                if(barrier.getParties() == 0)
                    barrier.reset();


                if(this.task_number==1) {
                    changeRefs();
                }

                l = barrier.await();


                if(barrier.getParties() == 0)
                    barrier.reset();
            }catch(Exception e){}
        }

    }


    public BenchmarkBelZab() {}

    public BenchmarkBelZab(int i) {
        task_number = i;

        int paso = cells_number /total_tasks;


        fn = paso * task_number;
        in = fn - paso;

        if( total_tasks == task_number)
            fn =cells_number;
    }

    public static void next_gen_concurrent(int nt,int g) {
        gens =g;

        size_pool =nt;

        barrier = new CyclicBarrier (size_pool);
        total_tasks = size_pool;

        myPool = new ThreadPoolExecutor(
                size_pool, size_pool, 60000L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
        BenchmarkBelZab[] tareas = new BenchmarkBelZab[nt];

        for(int t = 0; t < nt; t++)
        {
            tareas[t] = new BenchmarkBelZab(t+1);
            myPool.execute(tareas[t]);

        }

        myPool.shutdown();
        try{
            myPool.awaitTermination(10, TimeUnit.HOURS);
        } catch(Exception e){
            System.out.println(e.toString());
        }

    }

    private static void randomInitializer() {
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


        BenchmarkBelZab.cells_number = cells_number;
        BenchmarkBelZab.generations = generations;
        BenchmarkBelZab.cfrontier = cfrontier;
        BenchmarkBelZab.alpha = alpha;
        BenchmarkBelZab.beta = beta;
        BenchmarkBelZab.gamma = gamma;

        p = 0;
        q = 1;

        BenchmarkBelZab.a = new float [width][height][2];
        BenchmarkBelZab.b = new float [width][height][2];
        BenchmarkBelZab.c = new float [width][height][2];
        BenchmarkBelZab.randomInitializer();


    }

    public static void changeRefs() {
        if( p == 0) {
            p = 1;
            q = 0;
        }
        else {
            p = 0;
            q = 1;
        }
    }


    public static LinkedList<Long>caComputation(int taskNumber, int nGen) {
        LinkedList<Long> times = new LinkedList<>();
        generations = nGen;

        for (int i = 1; i <= taskNumber; i++) {
            System.out.println("\n Start simulation "+i+ " tasks");
            Instant startTime = Instant.now();
            next_gen_concurrent(i,nGen);
            Instant end = Instant.now();
            System.out.println("\n End simulation "+i+ "tasks");
            times.add(Duration.between(startTime,end).getSeconds());
        }

        return times;
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

    public void nextGen(int actual_gen) {

        for(int x = 0; x< width; x++)
            for (int y = in; y < fn; y++) {
                if(abort)
                    break;
                c_a = 0;
                c_b = 0;
                c_c = 0;

                c_a = computeVonNeumannNeighborhood(x, y, a)/(float)9.0;
                c_b = computeVonNeumannNeighborhood(x, y, b)/(float)9.0;
                c_c = computeVonNeumannNeighborhood(x, y, c)/(float)9.0;

                a[x][y][q] = transitionFunction(c_a, c_b, c_c, alpha, gamma );
                b[x][y][q] = transitionFunction(c_b, c_c, c_a, beta, alpha );
                c[x][y][q] = transitionFunction(c_c, c_a, c_b, gamma, beta );

            }
    }

    public static void main(String[] args) {
        BenchmarkBelZab simulation = new BenchmarkBelZab();
        simulation.initializer(1000, 1000, 0 , 1, 1, 1);
        LinkedList<Long> times = caComputation(8,1000);
        System.out.print("Medidas de tiempo \n");
        for (Long time: times )
            System.out.println(time);
        long firstValue =0;

        System.out.print("Medidas de Speed up\n");
        for (int i = 0; i < times.size() ; i++) {
            if (i==0)
                firstValue= times.get(i);
            System.out.println((firstValue+0.0)/(double)times.get(i));

        }

    }

}