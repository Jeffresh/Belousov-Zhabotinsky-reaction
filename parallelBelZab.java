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



public class parallelBelZab implements Runnable {

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

    private static float alpha,beta,gamma;
    public static int p ,q;


    public float[][][] getData() {
        return a;
    }
    public void plug(MainCanvas ref) {
        canvasTemplateRef = ref;
    }
    public void plugPopulationChart(AnalyticsMultiChart ref) {
        population_chart_ref = ref;
    }

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

                    if(parallelBelZab.population_chart_ref != null)
                        parallelBelZab.population_chart_ref.plot();
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

    public parallelBelZab() {}

    public parallelBelZab(int i) {
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
        parallelBelZab[] tareas = new parallelBelZab[nt];

        for(int t = 0; t < nt; t++) {
            tareas[t] = new parallelBelZab(t+1);
            myPool.execute(tareas[t]);

        }

        myPool.shutdown();
        try {
            myPool.awaitTermination(10, TimeUnit.HOURS);
        } catch(Exception e) {
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

        width = cells_number;
        height = cells_number;

        population_counter = new AtomicIntegerArray(states_number);

        parallelBelZab.cells_number = cells_number;
        parallelBelZab.generations = generations;
        parallelBelZab.cfrontier = cfrontier;
        parallelBelZab.alpha = alpha;
        parallelBelZab.beta = beta;
        parallelBelZab.gamma = gamma;

        population = new LinkedList[states_number];
        initialPopulation = new int[states_number];


        for (int i = 0; i < states_number; i++) {
            population[i] = new LinkedList<Double>();
        }

        for (int j = 0; j < states_number; j++) {
            population[j].add((double)initialPopulation[j]);
        }
        if(parallelBelZab.population_chart_ref != null)
            parallelBelZab.population_chart_ref.plot();

        p = 0;
        q = 1;

        parallelBelZab.a = new float [width][height][2];
        parallelBelZab.b = new float [width][height][2];
        parallelBelZab.c = new float [width][height][2];
        parallelBelZab.randomInitializer();


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

    private float transitionFunction(float a, float b , float c, float factor1, float factor2) {
        float value = a + a * ( factor1 * b - factor2 * c );

        if(value < 0)
            value=  0;
        if(value > 1)
            value = 1;

        return value;
    }

    public void addPopulation(float value, int index) {
        if(value>0.5){
            local_population_counter[index]++;
        }
    }

    public  LinkedList<Double>[] nextGen(int actual_gen) {

        local_population_counter = new int[states_number];

        for (int i = 0; i < states_number; i++) {
            this.local_population_counter[i]=0;
        }

        for(int x = 0; x < width; x++) {
            for (int y = in; y < fn; y++) {
                if (abort)
                    break;
                c_a = 0;
                c_b = 0;
                c_c = 0;

                c_a = computeVonNeumannNeighborhood(x, y, a) / (float) 9.0;
                c_b = computeVonNeumannNeighborhood(x, y, b) / (float) 9.0;
                c_c = computeVonNeumannNeighborhood(x, y, c) / (float) 9.0;

                a[x][y][q] = transitionFunction(c_a, c_b, c_c, alpha, gamma);
                b[x][y][q] = transitionFunction(c_b, c_c, c_a, beta, alpha);
                c[x][y][q] = transitionFunction(c_c, c_a, c_b, gamma, beta);

            }
        }

        return population;
    }
}