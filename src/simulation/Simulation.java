package simulation;

import configs.Configs;
import engineTester.MainGameLoop;
import entity.*;
import gui.MainGUI;
import openGL.entities.Camera;
import openGL.entities.Light;
import openGL.entities.RenderableObject;
import openGL.renderEngine.DisplayManager;
import openGL.renderEngine.MasterRenderer;
import openGL.utils.Maths;
import openGL.utils.MousePicker;
import openGL.world.World;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Simulation {

    private MasterRenderer renderer;

    private World world;

    private Camera camera;
    private Light light;
    private MousePicker mousePicker;
    private MainGUI gui;

    private boolean renderSelectedOnly = false;
    private boolean renderBestOnly = false;
    private boolean running = true;
    private boolean needToLoad = false;
    private String fileToLoadFrom = "";
    private boolean needToSave = false;
    private String fileToSaveTo = "";

    private Ant selectedAnt = null;
    private Ant bestAnt = null;

    private int generation = 1;
    private List<Score> scores;

    public Simulation(String file, MainGUI gui) {
        world = World.loadFromXML(file);
        scores = new ArrayList<Score>();
        if (world == null) {
            world = new World();
            List<AntHil> homes = new ArrayList<>();
            Vector2f rotXZ = Maths.calculateXZRotations(world, 25.5f, 25.5f);
            AntHil home = new AntHil(new Vector3f(25.5f, world.getHeight(25.5f, 25.5f), 25.5f), rotXZ.x, 0, rotXZ.y);
            homes.add(home);
            ((Tile) world.getChunk(25, 25)).addEntity(home);
            Population population = new Population();
            population.populate(Configs.nbAnts, world, homes);
            world.setPopulation(population);
        }
        camera = new Camera(world);
        renderer = new MasterRenderer(world);
        light = new Light(new Vector3f(20000, 20000, 20000), new Vector3f(1, 1, 1));
        mousePicker = new MousePicker(camera, renderer.getProjectionMatrix(), world.getPopulation().getAnts());
        this.gui = gui;
        gui.setWorld(world);
        scores.add(new Score(0, 0, 0, 0));
    }

    public Simulation(MainGUI gui) {
        world = new World();
        scores = new ArrayList<Score>();
        List<AntHil> homes = new ArrayList<>();
        Vector2f rotXZ = Maths.calculateXZRotations(world, 25.5f, 25.5f);
        AntHil home = new AntHil(new Vector3f(25.5f, world.getHeight(25.5f, 25.5f), 25.5f), rotXZ.x, 0, rotXZ.y);
        homes.add(home);
        ((Tile) world.getChunk(25, 25)).addEntity(home);
        Population population = new Population();
        population.populate(Configs.nbAnts, world, homes);
        world.setPopulation(population);
        camera = new Camera(world);
        renderer = new MasterRenderer(world);
        light = new Light(new Vector3f(20000, 20000, 20000), new Vector3f(1, 1, 1));
        mousePicker = new MousePicker(camera, renderer.getProjectionMatrix(), world.getPopulation().getAnts());
        this.gui = gui;
        gui.setWorld(world);
        scores.add(new Score(0, 0, 0, 0));
    }

    public void run() {
        while (!Display.isCloseRequested()) {
            if (needToLoad && !fileToLoadFrom.equals("")) {
                loadFromXML(fileToLoadFrom);
                needToLoad = false;
                fileToLoadFrom = "";
            }
            if (needToSave && !fileToSaveTo.equals("")) {
                saveToXML(fileToSaveTo);
                needToSave = false;
                fileToSaveTo = "";
            }
            if (Keyboard.isKeyDown(Keyboard.KEY_W) && renderer.canToogleWireframe())
                renderer.toogleWireframe();
            camera.update();
            checkForFocus();
            if (running) {
                boolean newAction = DisplayManager.intervalHasPassed(Configs.ACTION_DURATION);
                if (renderSelectedOnly)
                    selectedAnt.update(newAction, world);
                else if (renderBestOnly)
                    bestAnt.update(newAction, world);
                else {
                    bestAnt = world.getPopulation().getAnts().get(0);
                    for (Ant ant : world.getPopulation().getAnts()) {
                        ant.update(newAction, world);
                        if (ant.getFitnessScore() > bestAnt.getFitnessScore())
                            bestAnt = ant;
                    }
                }
                gui.setBestAnt(bestAnt);
                gui.updateCanvas();
                gui.setAntList(world.getPopulation().getAnts());
            }
            if (renderSelectedOnly)
                renderer.registerAnt(selectedAnt);
            else if (renderBestOnly)
                renderer.registerAnt(bestAnt);
            else
                renderer.registerAnts(world.getPopulation().getAnts());
            renderer.registerRenderableObjects(world.extractEntities());
            renderer.render(light, camera, Keyboard.isKeyDown(Keyboard.KEY_H));
            DisplayManager.updateDisplay();
        }
    }

    private void checkForFocus() {
        mousePicker.update();
        if (Mouse.isButtonDown(0) && mousePicker.canSelectEntity()) {
            RenderableObject e = mousePicker.getClickedEntity(camera.getFocus());
            if (e != null) {
                camera.setFocus(e);
                if (e instanceof Ant) {
                    gui.setSelectedAnt((Ant) e);
                    selectedAnt = (Ant)e;
                } else {
                    gui.setSelectedAnt(null);
                    selectedAnt = null;
                }
            } else {
                camera.setFocus(world);
                gui.setSelectedAnt(null);
                selectedAnt = null;
            }
        }
    }

    public Population getPopulation() {
        if (world != null)
            return world.getPopulation();
        return null;
    }

    public void cleanUp() {
        renderer.clear();
    }

    public boolean isRunning() {
        return running;
    }

    public void pause() {
        this.running = false;
    }

    public void resume() { this.running = true; }

    public boolean isRenderSelectedOnly() {
        return renderSelectedOnly;
    }

    public void setRenderSelectedOnly(boolean renderSelectedOnly) {
        if (renderSelectedOnly && selectedAnt == null) return;
        this.renderSelectedOnly = renderSelectedOnly;
        if (renderSelectedOnly)
            renderBestOnly = false;
    }

    public boolean isRenderBestOnly() {
        return renderBestOnly;
    }

    public void setRenderBestOnly(boolean renderBestOnly) {
        if (renderBestOnly && bestAnt == null) return;
        this.renderBestOnly = renderBestOnly;
        if (renderBestOnly)
            renderSelectedOnly = false;
    }

    public int getGeneration() {
        return generation;
    }

    public void setSelectedAnt(Ant ant) {
        selectedAnt = ant;
        camera.setFocus(ant);
    }

    /**
     * Save the current simulation to an XML file
     * containing the world and the current simulation state (Generation, score graphs ...)
     * basically save the current simulation
     * @param path the file to save to
     */
    public void saveToXML(String path) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

            Element simulationNode = document.createElement("simulation");
            simulationNode.setAttribute("generation", String.valueOf(generation));

            Element worldNode = world.getAsElement(document);
            simulationNode.appendChild(worldNode);

            Element graphNode = document.createElement("graph");
            for (Score score : scores) {
                Element elem = document.createElement("point");
                elem.setAttribute("x", String.valueOf(score.getGeneration()));
                elem.setAttribute("max", String.valueOf(score.getMax()));
                elem.setAttribute("avg", String.valueOf(score.getAverage()));
                elem.setAttribute("min", String.valueOf(score.getMin()));
                graphNode.appendChild(elem);
            }
            simulationNode.appendChild(graphNode);

            document.appendChild(simulationNode);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(path));

            transformer.transform(domSource, streamResult);

        } catch (ParserConfigurationException | TransformerException pce) {
            pce.printStackTrace();
        }
    }

    /**
     * Get a simulation from an XML file
     * @param path the file representing the simulation
     */
    public void loadFromXML(String path) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            File fileXML = new File(path);
            Document xml;

            xml = builder.parse(fileXML);
            Element simulationNode = (Element) xml.getElementsByTagName("simulation").item(0);

            if (simulationNode == null)
                return;
            if (!simulationNode.getTagName().equals("simulation"))
                return;
            Node worldNode = simulationNode.getElementsByTagName("world").item(0);
            Node graphNode = simulationNode.getElementsByTagName("graph").item(0);
            if (worldNode == null)
                return;
            if (graphNode == null)
                return;
            world = World.getFromElement((Element)worldNode);
            generation = Integer.parseInt(simulationNode.getAttribute("generation"));
            NodeList scores = graphNode.getChildNodes();
            for (int i = 0; i < scores.getLength(); i++) {
                Node node = scores.item(i);
                if (node.getNodeName().equals("point")) {
                    Element elem = (Element) node;
                    int generation = Integer.parseInt(elem.getAttribute("x"));
                    double max = Double.parseDouble(elem.getAttribute("max"));
                    double avg = Double.parseDouble(elem.getAttribute("avg"));
                    double min = Double.parseDouble(elem.getAttribute("min"));
                    Score score = new Score(generation, max, avg, min);
                    this.scores.add(score);
                }
            }
            List<double[]> listPoints = new ArrayList<>();
            double[] x = new double[this.scores.size()];
            double[] avg = new double[this.scores.size()];
            double[] max = new double[this.scores.size()];
            double[] min = new double[this.scores.size()];
            for (int i = 0; i < this.scores.size(); i++) {
                Score score = this.scores.get(i);
                x[i] = score.getGeneration();
                max[i] = score.getMax();
                min[i] = score.getMin();
                avg[i] = score.getAverage();
            }
            listPoints.add(x);
            listPoints.add(avg);
            listPoints.add(max);
            listPoints.add(min);
            gui.setScore(listPoints);
            renderer.setWorld(world);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Notify the simulation that it need to be loaded from a file
     * Can be called from another thread, the reload will be executed by the main thread.
     * This is needed because the OpenGL context is only loaded in the main thread and is required to regenerate the world model
     * @param file the file to be loaded from
     */
    public void dispatchLoadEvent(String file) {
        fileToLoadFrom = file;
        needToLoad = true;
    }

    /**
     * Notify the simulation that it need to be saved to a file
     * Can be called from another thread, the save will be executed by the main thread.
     * this methods exist only for the sake of symmetry with the loading mechanic
     * @param file the file to save to
     */
    public void dispatchSaveEvent(String file) {
        fileToSaveTo = file;
        needToSave = true;
    }
}
