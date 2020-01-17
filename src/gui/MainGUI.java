package gui;

import engineTester.MainGameLoop;
import entity.Ant;
import entity.logic.Tree;
import openGL.world.World;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.internal.series.MarkerSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
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
    private JPanel graphPane;
    private TreePanel selectedTreePanel;
    private TreePanel bestTreePanel;
    private Ant selectedAnt;
    private Ant bestAnt;

    private XYChart bestChart;
    private XYChart averageChart;

    private XChartPanel<XYChart> bestChartPane;
    private XChartPanel<XYChart> averageChartPane;

    public MainGUI() {
        Toolkit.getDefaultToolkit().setDynamicLayout(false);

        selectedTreePanel = new TreePanel();
        selectedTreePanel.setMinimumSize(new Dimension(500, 300));
        selectedTreePanel.setBorder(BorderFactory.createRaisedBevelBorder());
        selectedTreeSubPanel.add(selectedTreePanel);

        bestTreePanel = new TreePanel();
        bestTreePanel.setMinimumSize(new Dimension(500, 300));
        bestTreePanel.setBorder(BorderFactory.createRaisedBevelBorder());
        bestTreeSubPanel.add(bestTreePanel);

        /**
         * Listeners
         */
        saveSelected.addActionListener(actionEvent -> {
            pauseSimAndUpdateButton();
            if (selectedAnt != null) saveTreeToFile(selectedAnt.getDecisionTree());
        });
        loadSelected.addActionListener(actionEvent -> {
            pauseSimAndUpdateButton();
            if (selectedAnt != null) {
                Tree tree = loadTreeFromFile();
                if (tree != null) selectedAnt.setDecisionTree(tree);
            }
        });
        isolateSelected.addActionListener(actionEvent -> {
            if (selectedAnt != null) {
                pauseSimAndUpdateButton();
                boolean state = MainGameLoop.simulation.isRenderSelectedOnly();
                MainGameLoop.simulation.setRenderSelectedOnly(!state);
                if (!state) isolateSelected.setText("Show All");
                else isolateSelected.setText("Isolate");
                resumeSimAndUpdateButton();
            }
        });
        saveBest.addActionListener(actionEvent -> {
            pauseSimAndUpdateButton();
            if (bestAnt != null) saveTreeToFile(bestAnt.getDecisionTree());
        });
        loadBest.addActionListener(actionEvent -> {
            pauseSimAndUpdateButton();
            if (bestAnt != null) {
                Tree tree = loadTreeFromFile();
                if (tree != null) bestAnt.setDecisionTree(tree);
            }
        });
        isolateBest.addActionListener(actionEvent -> {
            if (bestAnt != null) {
                pauseSimAndUpdateButton();
                boolean state = MainGameLoop.simulation.isRenderBestOnly();
                MainGameLoop.simulation.setRenderBestOnly(!state);
                if (!state) isolateBest.setText("Show All");
                else isolateBest.setText("Isolate");
                resumeSimAndUpdateButton();
            }
        });
        antList.addListSelectionListener(listSelectionEvent -> {
            if (antList.getSelectedValue() != null) {
                pauseSimAndUpdateButton();
                selectedAnt = antList.getSelectedValue();
                selectedTreePanel.setAnt(selectedAnt);
                selectedTreePanel.updateUI();
                MainGameLoop.simulation.setSelectedAnt(selectedAnt);
                antList.setSelectedValue(selectedAnt, true);
                MainGameLoop.simulation.resume();
            }
        });
        pauseSimulation.addActionListener(actionEvent -> {
            if (!MainGameLoop.simulation.isRunning()) {
                resumeSimAndUpdateButton();
            } else {
                pauseSimAndUpdateButton();
            }
        });
        savSim.addActionListener(actionEvent -> {
            pauseSimAndUpdateButton();
            saveSimulationToFile();
        });
        loadSim.addActionListener(actionEvent -> {
            pauseSimAndUpdateButton();
            loadSimulationFromFile();
        });
        saveBests.addActionListener(actionEvent -> {
            pauseSimAndUpdateButton();
            saveBestAnts();
        });
        loadBests.addActionListener(actionEvent -> {
            pauseSimAndUpdateButton();
            loadBestAnts();
        });
        saveWorld.addActionListener(actionEvent -> {
            pauseSimAndUpdateButton();
            saveCurrentWorld();
        });
        loadWorld.addActionListener(actionEvent -> {
            pauseSimAndUpdateButton();
            loadCurrentWorld();
        });
        forceGen.addActionListener(actionEvent -> {
            pauseSimAndUpdateButton();
            MainGameLoop.simulation.dispatchNewGenerationEvent();
            resumeSimAndUpdateButton();
        });

        antList.setModel(new DefaultListModel<Ant>());
        antList.setFixedCellWidth(150);
        antList.setIgnoreRepaint(true);

        bestChart = new XYChartBuilder().width(650).height(300).title(getClass().getSimpleName()).xAxisTitle("Génération").yAxisTitle("Score").title("Meilleur score par générations").build();
        XYSeries bestSerie = bestChart.addSeries("Best", new double[]{0});
        bestSerie.setLineColor(Color.ORANGE);
        bestSerie.setMarker(SeriesMarkers.NONE);
        bestChart.getStyler().setLegendVisible(false);

        averageChart = new XYChartBuilder().width(650).height(300).title(getClass().getSimpleName()).xAxisTitle("Génération").yAxisTitle("Score").title("Score moyen par génération").build();
        XYSeries averageSerie = averageChart.addSeries("Average", new double[]{0});
        averageSerie.setLineColor(Color.BLUE);
        averageSerie.setMarker(SeriesMarkers.NONE);
        averageChart.getStyler().setLegendVisible(false);

        bestChartPane = new XChartPanel<>(bestChart);
        averageChartPane = new XChartPanel<>(averageChart);

        graphPane.add(bestChartPane);
        graphPane.add(averageChartPane);
        graphPane.setBorder(BorderFactory.createRaisedBevelBorder());
    }

    private void pauseSimAndUpdateButton() {
        MainGameLoop.simulation.pause();
        antList.setEnabled(false);
        pauseSimulation.setText("Resume Simulation");
    }

    private void resumeSimAndUpdateButton() {
        MainGameLoop.simulation.resume();
        antList.setEnabled(true);
        pauseSimulation.setText("Pause Simulation");
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

    private void saveCurrentWorld() {
        JFileChooser fc = new JFileChooser();
        File file = null;
        int returnVal = fc.showOpenDialog(this.mainPanel);

        if (returnVal == JFileChooser.APPROVE_OPTION)
            file = fc.getSelectedFile();
        else
            return;
        MainGameLoop.simulation.dispatchSaveWorldEvent(file.getAbsolutePath());
    }

    private void loadCurrentWorld() {
        JFileChooser fc = new JFileChooser();
        File file = null;
        int returnVal = fc.showOpenDialog(this.mainPanel);

        if (returnVal == JFileChooser.APPROVE_OPTION)
            file = fc.getSelectedFile();
        else
            return;
        MainGameLoop.simulation.dispatchLoadWorldEvent(file.getAbsolutePath());
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
            averageChart.updateXYSeries("Average", scores.get(0), scores.get(1), null);
            averageChartPane.repaint();
            bestChart.updateXYSeries("Best", scores.get(0), scores.get(2), null);
            bestChartPane.repaint();
        });
    }
}
