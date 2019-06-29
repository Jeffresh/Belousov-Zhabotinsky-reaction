import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.lang.*;
import java.lang.Math.*;
import javax.swing.JPanel;




public class parallelBelZab implements Runnable
{

	public static float [][][] a;
	public static float [][][] b;
	public static float [][][] c;

	private float c_a;
	private float c_b;
	private float c_c;
	private float value;

	private static float alfa,beta,gamma;
	public static int p ,q; 

	private static int ncels_;
	private static int width, height;

	private static ThreadPoolExecutor miPool;
	private static CyclicBarrier barrera = null;
	private static boolean parar =false;

	private static int tamPool;

	private static Gui.CanvasBelZab canvas;
	public static JPanel grafica_ref;

	public	void run()
	{
				
		// System.out.println("Entro en el run");


		 for(int i = 0; i<gens; i++)
		 {
		 	
	        if(parar)
		 		break;

		 	

			next_gen();
			try 
			{
				int l = barrera.await();
	   			if(barrera.getParties() == 0)
	                barrera.reset();
	        }catch(Exception e){}
	       	
	       	// System.out.println("Salgo de el run");
       		if(tarea==1)
	    		{
	    			canvas.paintImmediately(0,0,1000,1000);
	    			// grafica_ref.paintImmediately(0,0,100,100);
	    			if( p == 0)
    				{
    					p = 1; 
    					q = 0;
    				}
    				else
    				{
    					p = 0;
    					q = 1;
    				}
    			}
    		try 
			{
				int l = barrera.await();
				
	   			if(barrera.getParties() == 0)
	                barrera.reset();
	        }catch(Exception e){}



	        
	     }





	}


	private int tarea;
    private int in,fn;
    private static int numtareas;
    private static int gens;


    public void enchufa(Gui.CanvasBelZab c)
    {
    	canvas=c;
    }


	public parallelBelZab(int i)
	{
		tarea = i;

        int paso = ncels_ /numtareas;


        fn = paso * tarea;
        in = fn - paso;

        if( numtareas == tarea)
            fn =ncels_;

        System.out.println(in+" "+fn);

	}


	public void ini_Belzab(int w, int h,float al,float be, float ga, int s)
	{
		width = w;
		height = h ;

		// parar =false;

		a = new float [width][height][2];
		b = new float [width][height][2];
		c = new float [width][height][2];
		p = 0;
		q = 1;
		value = 0;

		long m = 1000;

		ncels_ = width;

       	// motor = new randomGenerator();


		for(int x = 0 ; x < width; x++)
			for(int y = 0; y < height; y++)
			{
				a[x ][ y ][ p] = (float)Math.random ();
				b[x ][ y ][ p] = (float)Math.random ();
				c[x ][ y ][ p] = (float)Math.random ();


			}

			alfa =al;
			beta =be;
			gamma =ga;
	}


	public parallelBelZab(int w, int h,float al,float be, float ga, int s)
	{

		width = w;
		height = h ;

		a = new float [width][height][2];
		b = new float [width][height][2];
		c = new float [width][height][2];
		p = 0;
		q = 1;

		value =0;

		long m = 1000;

		ncels_ = width;

       	// motor = new randomGenerator();

		for(int x = 0 ; x < width; x++)
			for(int y = 0; y < height; y++)
			{
				a[x ][ y ][ p] = (float)Math.random ();
				b[x ][ y ][ p] = (float)Math.random ();
				c[x ][ y ][ p] = (float)Math.random ();


			}

			alfa =al;
			beta =be;
			gamma =ga;

		for(int x = 0 ; x < width; x++)
			for(int y = 0; y < height; y++)
			{
				// m = motor.moore(m);
				
				a[x][y][p]= (float)Math.random();
				// (float)m/Integer.MAX_VALUE;
				// m = motor.moore(m);

				b[x][y][p]= (float)Math.random();
				// (float)m/Integer.MAX_VALUE;
				// m = motor.moore(m);

				c[x][y][p]= (float)Math.random();
				// (float)m/Integer.MAX_VALUE;


			}

			alfa =al;
			beta =be;
			gamma =ga;
			

	
	}


	public void stop()
	{
		miPool.shutdownNow();
		parar=true;
	}

	public void start()
	{
		parar =false;
	}


	public static void next_gen_concurrent(int nt,int g)
	{
		gens =g;


		tamPool =nt;

		barrera = new CyclicBarrier (tamPool);
			numtareas = tamPool;

		miPool = new ThreadPoolExecutor(
    									tamPool, tamPool, 60000L,
    									TimeUnit.MILLISECONDS,
    									new LinkedBlockingQueue<Runnable>());
		parallelBelZab[] tareas = new  parallelBelZab[nt];

		for(int t = 0; t < nt; t++)
		{
			tareas[t] = new parallelBelZab(t+1);
    		miPool.execute(tareas[t]);

		}


		miPool.shutdown();
		try{
		miPool.awaitTermination(10, TimeUnit.HOURS);}catch(Exception e){}


	}


	public void next_gen()
	{



	 value = 0;
		for (int x = 0; x < width ; x ++) 
		{
	    	for (int y = 0; y < height ; y ++)
	    	{
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

				value = c_a + c_a * ( alfa *c_b - gamma*c_c );

				if(value < 0)
				    value=  0;
				if(value > 1)
				    value = 1;

				a[x ][ y ][ q] = value ; 
				value = c_b + c_b * ( beta* c_c - alfa* c_a );

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
		}

	

		
	}



	public String toString()
    {
        String cout= new String();
        for(int i = 0 ; i < ncels_; i++)
        {
            cout +="| ";

            for (int j = 0; j< ncels_ ; j++ )
            {
                  cout+=a[i][j][p];
                  cout+=" | ";
                
            }

            cout+='\n';
          
        }
        return cout;
    }
}