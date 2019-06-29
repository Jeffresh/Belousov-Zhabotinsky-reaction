import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


import java.util.Scanner;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;


public class Gui extends Frame implements ActionListener, FocusListener
{


    private static BufferedImage image;



    public class CanvasBelZab extends JPanel 
    {
        public  parallelBelZab bz;



        public CanvasBelZab()
        { 

          bz= new parallelBelZab(puntosbelz,puntosbelz,(float)1,(float)1,(float)1,1);
          bz.enchufa(this);

          image = new BufferedImage(1000, 1000,BufferedImage.TYPE_BYTE_INDEXED);

        }

         public  BufferedImage imageGen()
         {
            Color color;

             for(int x=0;x<puntosbelz;x++)
            {
                for(int y=0;y<puntosbelz;y++)
                {
                    color = new Color(bz.a[x][y][bz.q],bz.a[x][y][bz.q],bz.a[x][y][bz.q]);

                    image.setRGB(x,y,color.getRGB());



               }
            }

            return image;
         }



        protected void paintComponent(Graphics g)
        {  
            super.paintComponent(g);    


            Graphics g2 = (Graphics2D)g;

            g2.drawImage(imageGen(),0,0,1000,1000,this);




        }
    }

   



    private static  JFrame frame;

    private static JMenuItem itemFileBelZab;
 
  

    private static JSplitPane todo;
    private static JSplitPane botonera;
    private static JMenuBar BarraSuperior;
    private static JMenu menuFile ; 
    private static JMenu menuAbout ; 
    private static JMenu menuHelp ;


    private JMenuBar createMibar()
    {

        BarraSuperior = new JMenuBar();
        menuFile =  new JMenu("File");
        menuAbout = new JMenu("Help");
        menuHelp = new JMenu("About");


        BarraSuperior.setOpaque(true);
        BarraSuperior.setBackground(new Color(0,0,0));
        BarraSuperior.setPreferredSize(new Dimension(200,20));

        BarraSuperior.add(menuFile);

        BarraSuperior.add(Box.createHorizontalGlue());
        BarraSuperior.add(menuHelp);
        BarraSuperior.add(menuAbout);

        itemFileBelZab = new JMenuItem("BelZha");
        itemFileBelZab.addActionListener(this);

        menuFile.add(itemFileBelZab);

        menuHelp.add("Busca en Google");
        menuAbout.add("Contacte con la UCA");



        return BarraSuperior;
    }



    private static JTextField tgensbelz, tpuntosbelz, talfa,tbeta,tgamma;
    private static JLabel lgensbelz, lpuntosbelz, lalfa, lbeta,lgamma;
    private static JButton startbelza, stopbelza;
    



    private JSplitPane createTextFieldsBelZab()
    {
        tgensbelz  = new JTextField();
        tgensbelz.setText(Integer.toString(gensbelz));
        tgensbelz.addFocusListener(this);

        tpuntosbelz = new JTextField();
        tpuntosbelz.setText(Integer.toString(puntosbelz));
        tpuntosbelz.addFocusListener(this);

        talfa = new JTextField();
        talfa.setText(Float.toString(alfabelz));
        talfa.addFocusListener(this);

        tbeta = new JTextField();
        tbeta.setText(Float.toString(betabelz));
        tbeta.addFocusListener(this);

        tgamma = new JTextField();
        tgamma.setText(Float.toString(gammabelz));
        tgamma.addFocusListener(this);

        lgensbelz = new JLabel("Generaciones: ");
        lgensbelz.setLabelFor(tgensbelz);

        lpuntosbelz =new JLabel("Puntos: ");
        lpuntosbelz.setLabelFor(tpuntosbelz);

        lalfa = new JLabel("Alfa: ");
        lalfa.setLabelFor(talfa);

        lbeta = new JLabel("Beta: ");
        lbeta.setLabelFor(tbeta);

        lgamma =new JLabel("Gamma: ");
        lgamma.setLabelFor(tgamma);




        //Lay out the text controls and the labels
        JPanel textControlsPane = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();

        textControlsPane.setLayout(gridbag);
        textControlsPane.setPreferredSize(new Dimension(100, 900));
        textControlsPane.setMinimumSize(new Dimension(100, 900));

        JLabel[] labels = {lgensbelz,lpuntosbelz,lalfa,lbeta,lgamma};
        JTextField[] textFields = {tgensbelz,tpuntosbelz,talfa,tbeta,tgamma};
        addLabelTextRows(labels,textFields,new JComboBox[0],textControlsPane);

        textControlsPane.setBorder(
                                   BorderFactory.createCompoundBorder(
                                                                      BorderFactory.createTitledBorder("Variables"),
                                                                      BorderFactory.createEmptyBorder(5,5,5,5)));

        startbelza = new JButton("Start");
        startbelza.addActionListener(this);
        stopbelza = new JButton("Reset");
        stopbelza.addActionListener(this);

        JPanel botonesPane = new JPanel();
        botonesPane.add(startbelza,BorderLayout.CENTER);
        botonesPane.add(stopbelza,BorderLayout.CENTER);

        botonesPane.setPreferredSize(new Dimension(100, 50));
        botonesPane.setMaximumSize(new Dimension(100, 50));

        botonesPane.setBorder(
                  BorderFactory.createCompoundBorder(
                                                     BorderFactory.createTitledBorder("Control"),
                                                     BorderFactory.createEmptyBorder(5,5,5,5)));



        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                              textControlsPane,
                                              botonesPane);
        
        


        splitPane.setOneTouchExpandable(true);


        return splitPane;   

 


    }    
 

    private void addLabelTextRows(JLabel[] labels,
                                  JTextField[] textFields, 
                                  JComboBox[] list,
                                  Container container)
    {
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        int numLabels = labels.length;
        int numlist = list.length;
 
        for (int i = 0; i < numLabels; i++) 
        {
            c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
            c.fill = GridBagConstraints.NONE;      //reset to default
            c.weightx = 1.0;                       //reset to default
            container.add(labels[i], c);
 
            c.gridwidth = GridBagConstraints.REMAINDER;     //end row
            c.fill = GridBagConstraints.NONE;
            c.weightx = 1.0;
            textFields[i].setColumns(3);
            container.add(textFields[i], c);
        }

        // c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
        // c.fill = GridBagConstraints.NONE;      //reset to default
        // c.weightx = 1.0;                       //reset to default
        // container.add(labels[1], c);
        for (int i = 0; i < numlist; i++) 
        {
            c.gridwidth = GridBagConstraints.REMAINDER;     //end row
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1.0;
            container.add(list[i], c);
        }
    }


    private static  void createAndShowGUI()
    {
        frame = new JFrame("BelZab");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        frame.setJMenuBar(new Gui().createMibar());

        caBelZab = new Gui().new CanvasBelZab();
        caBelZab.setMinimumSize(new Dimension(1000, 1000));
        caBelZab.setPreferredSize(new Dimension(1000, 1000));
            
        botonera =  new Gui().createTextFieldsBelZab();

        todo = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, caBelZab,botonera);

        frame.pack();
        frame.setExtendedState(frame.getExtendedState()|JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
        frame.setContentPane(todo);

        frame.validate();
        frame.repaint();
    }


    private static SwingWorker worker;



    private static int puntosbelz = 200;
    private static int gensbelz = 100;
    private static float alfabelz = 1;
    private static float betabelz = 1;
    private static float gammabelz = 1;

    private static CanvasBelZab caBelZab;
 


    public void focusGained(FocusEvent e) 
	{
    	//nothing
	}
	 public void focusLost(FocusEvent e) 
	 {


        if(e.getSource() == tgensbelz)
        {

            String nump=tgensbelz.getText();
            gensbelz = Integer.parseInt(nump);
        }

        if(e.getSource() == tpuntosbelz)
        {
            String nump=tpuntosbelz.getText();
            puntosbelz= Integer.parseInt(nump);
        }

        if(e.getSource() == talfa)
        {
            String nump=talfa.getText();
            alfabelz= Float.parseFloat(nump);
        }

        if(e.getSource() == tbeta)
        {

            String nump=tbeta.getText();
            betabelz= Float.parseFloat(nump);
        }

        if(e.getSource() == tgamma)
        {

            String nump=tgamma.getText();
            gammabelz= Float.parseFloat(nump);
        }
    }



    public void actionPerformed(ActionEvent e)
    {


        if(e.getSource()==stopbelza)
        {
            worker.cancel(true);
            caBelZab.bz.stop();

            caBelZab.bz.ini_Belzab(puntosbelz,puntosbelz,alfabelz,betabelz,gammabelz,1);
            frame.validate();
            frame.repaint();
            worker.cancel(false);
            caBelZab.bz.start();


        }


        if(e.getSource() == itemFileBelZab)
        {

            caBelZab = new CanvasBelZab();
            caBelZab.setMinimumSize(new Dimension(1000, 1000));
            caBelZab.setPreferredSize(new Dimension(1000, 1000));
            
            botonera =  new Gui().createTextFieldsBelZab();
            



            todo = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, caBelZab,botonera);

            todo.setOneTouchExpandable(true);

            frame.remove(todo);
            frame.setContentPane(todo);
            frame.validate();
            frame.repaint();


        }


        if(e.getSource()==startbelza)
        {

            worker = new SwingWorker<Void, Gui>() 
            {
                @Override
                protected Void doInBackground() 
                {
                    // for(int i = 0; i<100000000; i++)
                    // {
                    //                          series[0].add(i+1,i*3);

                                  
                    // }

                    try{parallelBelZab.next_gen_concurrent(4,gensbelz);}catch(Exception ex){};



        

                    return null;
              
                 }
            };

            worker.execute();
           
            System.out.println("\n");
            System.out.println("Generaciones: "+gensbelz);
            System.out.println("Puntos: "+puntosbelz);
            System.out.println("Alfa: "+alfabelz);
        }
    }
   


    public static void main(String[] args)
    {
        
        javax.swing.SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    createAndShowGUI();
                }
            });
    }
}

