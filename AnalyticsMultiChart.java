import java.awt.*;
import java.util.LinkedList;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import javax.swing.*;

import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.SeriesMarkers;

/**
 * Creates a real-time chart using SwingWorker
 */
public class AnalyticsMultiChart {

    public  JPanel population_chart_panel;
    public  XYChart population_chart;
    public  JFrame chart_frame;
    public parallelBelZab CA1Dref;
    private  String chart_title;
    private  LinkedList<Double>[] fifo_population;

    private XYChart createChart(String chart_title, String x_axis_name, String y_axis_name) {
        XYChart chart;
        chart = new XYChartBuilder()
                .title(chart_title).xAxisTitle(x_axis_name)
                .yAxisTitle(y_axis_name).width(600).height(300).build();
        chart.getStyler().setLegendVisible(true);
        chart.getStyler().setXAxisTicksVisible(true);
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);

        return chart;
    }

    AnalyticsMultiChart(String chart_title, String x_axis_name, String y_axis_name) {
        this.chart_title = chart_title;
        population_chart = createChart(chart_title, x_axis_name, y_axis_name);
    }

    public void setRef(parallelBelZab ref) {
        CA1Dref = ref;
    }

    public void getDataPopulation() {

        fifo_population = CA1Dref.getPopulation();
        double[][] array = new double[CA1Dref.states_number][fifo_population[0].size()];

        for (int j = 0; j < CA1Dref.states_number; j++) {
            for (int i = 0; i < fifo_population[j].size(); i++)
                array[j][i] = fifo_population[j].get(i)+0.0;
            population_chart.updateXYSeries("state "+(j),null, array[j], null);
        }
    }

    public void createSeries() {
        int[] initialValues = CA1Dref.getInitialPopulation();
       if(population_chart.getSeriesMap().size()<1)
            for (int i = 0; i < CA1Dref.states_number ; i++) {
                population_chart.addSeries("state "+(i),new double[]{0}, new double[]{initialValues[i]})
                        .setXYSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line).setMarker(SeriesMarkers.NONE);
            }
    }

    public void plot() {
        getDataPopulation();
        population_chart_panel.revalidate();
        population_chart_panel.repaint();
    }

    public void show() {
        population_chart_panel = new XChartPanel(population_chart);

        chart_frame = new JFrame("Charts");
        GridLayout layout = new GridLayout(1,1);
        chart_frame.setLayout(layout);
        chart_frame.add(population_chart_panel);

        chart_frame.setSize(600,600);
        chart_frame.setMaximumSize(new Dimension(200,600));

        chart_frame.setAlwaysOnTop(true);
        chart_frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        chart_frame.setTitle(chart_title);
        chart_frame.setOpacity(1);
        chart_frame.setBackground(Color.WHITE);
        chart_frame.setVisible(true);
        chart_frame.pack();
    }
}