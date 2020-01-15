package gui;

import engineTester.MainGameLoop;
import entity.Ant;
import entity.logic.Tree;
import openGL.world.World;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Collections;
import java.util.List;

public class MainGUI {

    private JTabbedPane tabContainer;
    public JPanel mainPanel;
    private JPanel selected;
    private JPanel best;
    private JPanel option;
    private JButton saveSelected;
    private JButton loadSelected;
    private JButton isolateSelected;
    private JButton editSelected;
    private JPanel selectedControlPanel;
    private JPanel selectedTreeSubPanel;
    private JButton saveBest;
    private JButton loadBest;
    private JButton isolateBest;
    private JButton editBest;
    private JPanel bestControlPanel;
    private JPanel bestTreeSubPanel;
    private JButton saveWorld;
    private JPanel optionButtonPanel;
    private JButton loadWorld;
    private JButton saveBests;
    private JButton loadBests;
    private JButton pauseSimulation;
    private JButton forceGen;
    private JButton savSim;
    private JButton loadSim;
    private JList<Ant> antList;
    private JPanel infoPanel;
    private TreePanel selectedTreePanel;
    private TreePanel bestTreePanel;
    private Ant selectedAnt;
    private Ant bestAnt;

    private XYChart scoreChart;
    private XYSeries averageScore;
    private XYSeries bestScore;
    private XYSeries minScore;
    private XChartPanel<XYChart> chartPane;

    public MainGUI() {
        Toolkit.getDefaultToolkit().setDynamicLayout( false );

        selectedTreePanel = new TreePanel();
        selectedTreePanel.setMinimumSize(new Dimension(1000, 600));
        selectedTreePanel.setBorder(BorderFactory.createRaisedBevelBorder());
        selectedTreeSubPanel.add(selectedTreePanel);

        bestTreePanel = new TreePanel();
        bestTreePanel.setMinimumSize(new Dimension(1000, 600));
        bestTreePanel.setBorder(BorderFactory.createRaisedBevelBorder());
        bestTreeSubPanel.add(bestTreePanel);

        saveSelected.addActionListener(actionEvent -> {
            MainGameLoop.simulation.pause();
            if (selectedAnt != null) saveTreeToFile(selectedAnt.getDecisionTree());
            MainGameLoop.simulation.resume();
        });
        loadSelected.addActionListener(actionEvent -> {
            MainGameLoop.simulation.pause();
            if (selectedAnt != null) {
                Tree tree = loadTreeFromFile();
                if (tree != null) selectedAnt.setDecisionTree(tree);
            }
            MainGameLoop.simulation.resume();
        });
        isolateSelected.addActionListener(actionEvent -> {
            if (selectedAnt != null) {
                boolean state = MainGameLoop.simulation.isRenderSelectedOnly();
                MainGameLoop.simulation.setRenderSelectedOnly(!state);
                if (!state) isolateSelected.setText("Show All");
                else isolateSelected.setText("Isolate");
            }
        });

        saveBest.addActionListener(actionEvent -> {
            MainGameLoop.simulation.pause();
            if (bestAnt != null) saveTreeToFile(bestAnt.getDecisionTree());
            MainGameLoop.simulation.resume();
        });
        loadBest.addActionListener(actionEvent -> {
            MainGameLoop.simulation.pause();
            if (bestAnt != null) {
                Tree tree = loadTreeFromFile();
                if (tree != null) bestAnt.setDecisionTree(tree);
            }
            MainGameLoop.simulation.resume();
        });
        isolateBest.addActionListener(actionEvent -> {
            if (bestAnt != null) {
                boolean state = MainGameLoop.simulation.isRenderBestOnly();
                MainGameLoop.simulation.setRenderBestOnly(!state);
                if (!state) isolateBest.setText("Show All");
                else isolateBest.setText("Isolate");
            }
        });

        antList.setModel(new DefaultListModel<Ant>());
        antList.setFixedCellWidth(200);
        antList.setIgnoreRepaint(true);
        scoreChart = new XYChartBuilder().width(800).height(600).title(getClass().getSimpleName()).xAxisTitle("Score").yAxisTitle("Génération").build();

        averageScore = scoreChart.addSeries("Average", new double[]{0.5, .8});
        averageScore.setMarker(SeriesMarkers.CIRCLE);
        bestScore = scoreChart.addSeries("Best", new double[]{.8, 1});
        bestScore.setMarker(SeriesMarkers.CIRCLE);
        minScore = scoreChart.addSeries("Lowest", new double[]{.3, .5});
        minScore.setMarker(SeriesMarkers.CIRCLE);
        chartPane = new XChartPanel<>(scoreChart);
        infoPanel.add(chartPane);

        antList.addListSelectionListener(listSelectionEvent -> {
            if (antList.getSelectedValue() != null) {
                MainGameLoop.simulation.pause();
                selectedAnt = antList.getSelectedValue();
                MainGameLoop.simulation.setSelectedAnt(selectedAnt);
                antList.setSelectedValue(selectedAnt, true);
                MainGameLoop.simulation.resume();
            }
        });

        pauseSimulation.addActionListener(actionEvent -> {
            if (!MainGameLoop.simulation.isRunning()) {
                MainGameLoop.simulation.resume();
                pauseSimulation.setText("Pause Simulation");
            } else {
                MainGameLoop.simulation.pause();
                pauseSimulation.setText("Resume Simulation");
            }
        });
        savSim.addActionListener(actionEvent -> {
            MainGameLoop.simulation.pause();
            saveSimulationToFile();
            MainGameLoop.simulation.resume();
        });
        loadSim.addActionListener(actionEvent -> {
            MainGameLoop.simulation.pause();
            loadSimulationFromFile();
            MainGameLoop.simulation.resume();
        });

        saveBests.addActionListener(actionEvent -> {
            MainGameLoop.simulation.pause();
            saveBestAnts();
            MainGameLoop.simulation.resume();
        });

        loadBests.addActionListener(actionEvent -> {
            MainGameLoop.simulation.pause();
            loadBestAnts();
            MainGameLoop.simulation.resume();
        });
    }

    public void setSelectedAnt(Ant ant) {
        SwingUtilities.invokeLater(() -> {
            selectedAnt = ant;
            selectedTreePanel.setAnt(ant);
            selectedTreePanel.updateUI();
        });
    }

    public void setBestAnt(Ant ant) {
        SwingUtilities.invokeLater(() -> {
            bestAnt = ant;
            bestTreePanel.setAnt(ant);
            bestTreePanel.updateUI();
        });
    }

    public void setWorld(World world) {
        SwingUtilities.invokeLater(() -> {
            selectedTreePanel.setWorld(world);
            bestTreePanel.setWorld(world);
        });
    }

    public void updateCanvas() {
        SwingUtilities.invokeLater(() -> {
            selectedTreePanel.updateUI();
            bestTreePanel.updateUI();
        });
    }

    private void saveTreeToFile(Tree tree) {
        JFileChooser fc = new JFileChooser();
        File file = null;
        int returnVal = fc.showOpenDialog(this.mainPanel);

        if (returnVal == JFileChooser.APPROVE_OPTION)
            file = fc.getSelectedFile();
        else
            return;
       tree.saveToXML(file.getAbsolutePath());
    }

    private void saveBestAnts() {
        JFileChooser fc = new JFileChooser();
        File file = null;
        int returnVal = fc.showOpenDialog(this.mainPanel);

        if (returnVal == JFileChooser.APPROVE_OPTION)
            file = fc.getSelectedFile();
        else
            return;
        MainGameLoop.simulation.getPopulation().saveBestAntsToXML(file.getAbsolutePath());
    }

    private void loadBestAnts() {
        JFileChooser fc = new JFileChooser();
        File file = null;
        int returnVal = fc.showOpenDialog(this.mainPanel);

        if (returnVal == JFileChooser.APPROVE_OPTION)
            file = fc.getSelectedFile();
        else
            return;
        MainGameLoop.simulation.getPopulation().loadBestAntsFromXML(file.getAbsolutePath());
    }

    private Tree loadTreeFromFile() {
        JFileChooser fc = new JFileChooser();
        File file = null;
        int returnVal = fc.showOpenDialog(this.mainPanel);

        if (returnVal == JFileChooser.APPROVE_OPTION)
            file = fc.getSelectedFile();
        else
            return null;
        return Tree.loadFromXML(file.getAbsolutePath());
    }

    private void saveSimulationToFile() {
        JFileChooser fc = new JFileChooser();
        File file = null;
        int returnVal = fc.showOpenDialog(this.mainPanel);

        if (returnVal == JFileChooser.APPROVE_OPTION)
            file = fc.getSelectedFile();
        else
            return;
        MainGameLoop.simulation.dispatchSaveEvent(file.getAbsolutePath());
    }

    private void loadSimulationFromFile() {
        JFileChooser fc = new JFileChooser();
        File file = null;
        int returnVal = fc.showOpenDialog(this.mainPanel);

        if (returnVal == JFileChooser.APPROVE_OPTION)
            file = fc.getSelectedFile();
        else
            return;
        MainGameLoop.simulation.dispatchLoadEvent(file.getAbsolutePath());
    }

    public void setAntList(List<Ant> ants) {
        Collections.sort(ants);
        SwingUtilities.invokeLater(() -> {
            ((DefaultListModel<Ant>)antList.getModel()).clear();
            ((DefaultListModel<Ant>)antList.getModel()).addAll(ants);
            antList.setSelectedValue(selectedAnt, true);
            antList.repaint();
        });
    }

    public void setScore(List<double[]> scores) {
        SwingUtilities.invokeLater(() -> {
            scoreChart.updateXYSeries("Average", scores.get(0), scores.get(1), null);
            scoreChart.updateXYSeries("Best", scores.get(0), scores.get(2), null);
            scoreChart.updateXYSeries("Lowest", scores.get(0), scores.get(3), null);
            chartPane.repaint();
        });
    }
}
