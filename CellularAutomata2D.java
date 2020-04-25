import javafx.scene.control.Cell;

import java.util.LinkedList;
import java.util.Random;
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



public class CellularAutomata2D implements Runnable
{

    private static int[] initialPopulation;
    public static AtomicIntegerArray population_counter;
    private int [] local_population_counter;
    private static LinkedList<Double>[] population;
    public static MainCanvas canvasTemplateRef;
    public static AnalyticsMultiChart population_chart_ref;

    public static float [][][] a;
    public static float [][][] b;
    public static float [][][] c;

    private float c_a;
    private float c_b;
    private float c_c;
    private float value;

    private static float alpha,beta,gamma;
    public static int p ,q;


    public float[][][] getData() { return a; }
    public void plug(MainCanvas ref) { canvasTemplateRef = ref; }
    public void plugPopulationChart(AnalyticsMultiChart ref) { population_chart_ref = ref;}

    private static int width, height;

    public static int states_number = 2;
    private static int cfrontier = 0;
    private static int cells_number;
    public static int generations;
    private static Random randomGenerator;

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
                for (int j = 0; j < states_number; j++) {
                    population_counter.getAndAdd(j,this.local_population_counter[j]);
                }

                if(barrier.getParties() == 0)
                    barrier.reset();

                l = barrier.await();


                if(this.task_number==1) {
                    canvasTemplateRef.revalidate();
                    canvasTemplateRef.repaint();
                    Thread.sleep(0,10);

                    for (int j = 0; j < states_number; j++) {
                        population[j].add((double)population_counter.get(j));
                    }
                    population_counter = new AtomicIntegerArray(states_number);

                    if(CellularAutomata2D.population_chart_ref != null)
                        CellularAutomata2D.population_chart_ref.plot();
                    changeRefs();
                }

                if(barrier.getParties() == 0)
                    barrier.reset();

                l = barrier.await();


                if(barrier.getParties() == 0)
                    barrier.reset();
            }catch(Exception e){}
        }

    }

    public int[] getInitialPopulation(){
        return initialPopulation;
    }

    public CellularAutomata2D() {}

    public CellularAutomata2D(int i) {
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
        CellularAutomata2D[] tareas = new CellularAutomata2D[nt];

        for(int t = 0; t < nt; t++)
        {
            tareas[t] = new CellularAutomata2D(t+1);
            myPool.execute(tareas[t]);

        }

        myPool.shutdown();
        try{
            myPool.awaitTermination(10, TimeUnit.HOURS);
        } catch(Exception e){
            System.out.println(e.toString());
        }

    }

    public LinkedList<Double>[] getPopulation(){
        return population;
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
        randomGenerator = new Random();

        width = cells_number;
        height = cells_number;

        population_counter = new AtomicIntegerArray(states_number);

        CellularAutomata2D.cells_number = cells_number;
        CellularAutomata2D.generations = generations;
        CellularAutomata2D.cfrontier = cfrontier;
        CellularAutomata2D.alpha = alpha;
        CellularAutomata2D.beta = beta;
        CellularAutomata2D.gamma = gamma;

        population = new LinkedList[states_number];
        initialPopulation = new int[states_number];


        for (int i = 0; i < states_number; i++) {
            population[i] = new LinkedList<Double>();
        }

        for (int j = 0; j < states_number; j++) {
            population[j].add((double)initialPopulation[j]);
        }
        if(CellularAutomata2D.population_chart_ref != null)
            CellularAutomata2D.population_chart_ref.plot();

        CellularAutomata2D.a = new float [width][height][2];
        CellularAutomata2D.b = new float [width][height][2];
        CellularAutomata2D.c = new float [width][height][2];
        p = 0;
        q = 1;
        value = 0;

        long m = 1000;


        for(int x = 0 ; x < width; x++)
            for(int y = 0; y < height; y++)
            {
                a[x ][ y ][ p] = (float)Math.random ();
                b[x ][ y ][ p] = (float)Math.random ();
                c[x ][ y ][ p] = (float)Math.random ();


            }

    }

    public static int getIndex() {
        return p;
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

    public static void stop() {
        abort = true;
    }

    public static LinkedList<Double>[]caComputation(int nGen) {
        abort = false;
        generations = nGen;
        next_gen_concurrent(8,nGen);

        return population;
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

    public  LinkedList<Double>[] nextGen(int actual_gen) {

        local_population_counter = new int[states_number];

        for (int i = 0; i < states_number; i++) {
            this.local_population_counter[i]=0;
        }

        for(int x = 0; x< width; x++)
            for (int y = 0; y < width; y++) {
                if(abort)
                    break;
                c_a = 0;
                c_b = 0;
                c_c = 0;

                for (int i = x - 1; i <= x +1; i ++)
                {
                    for (int j = y - 1; j <= y +1; j ++)
                    {
                        c_a += a [( i+ width )% width ][( j+ height )% height ][ p ];
                        c_b += b [( i+ width )% width ][( j+ height )% height ][ p ];
                        c_c += c [( i+ width )% width ][( j+ height )% height ][ p ];
                    }
                }
                c_a /= 9.0;
                c_b /= 9.0;
                c_c /= 9.0;

                value = c_a + c_a * ( alpha *c_b - gamma*c_c );

                if(value < 0)
                    value=  0;
                if(value > 1)
                    value = 1;

                a[x ][ y ][ q] = value ;
                value = c_b + c_b * ( beta* c_c - alpha* c_a );

                if(value < 0)
                    value=  0;
                if(value > 1)
                    value = 1;

                b[x ][ y ][ q] = value;

                value =c_c + c_c * ( gamma* c_a -  beta* c_b );

                if(value < 0)
                    value=  0;
                if(value > 1)
                    value = 1;

                c[x ][ y ][ q] = value ;

            }

        return population;
    }

}