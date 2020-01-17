package entity;

import configs.Configs;
import engineTester.MainGameLoop;
import openGL.world.World;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import simulation.Score;

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
import java.util.*;

public class Population {

    private ArrayList<Ant> ants;

    public Population() {
        ants = new ArrayList<Ant>();
    }

    /**
     * Initialize the population with random ants
     * @param nbAnts number of ants to generate
     */
    public void populate(int nbAnts, World world, List<AntHil> anthils) {
        Random rand = new Random();
        for (int i = 0; i < 200; i++) {
            AntHil anthil = anthils.get(rand.nextInt(anthils.size()));
            Vector2f home = new Vector2f(anthil.getPosition().x, anthil.getPosition().z);
            ants.add(new Ant(new Vector3f(rand.nextInt(world.getSizeX()) + 0.5f, 0, rand.nextInt(world.getSizeZ()) + 0.5f), 0, home));
        }
    }

    /**
     * get the best ant of the population
     * @return the ant with the best fitness score
     */
    public Ant getBest() {
        float maxFitness = Float.MIN_VALUE;
        Ant best = null;
        for (Ant ant : ants) {
            if (ant.getFitnessScore() > maxFitness) {
                maxFitness = ant.getFitnessScore();
                best = ant;
            }
        }
        return best;
    }

    /**
     * Get a list containing the ants used to generate the next generation of the population
     * @return a list of the ants that had the best behaviour regarding survivability
     */
    public List<Ant> getBestsAnts() {
        List<Ant> bests = new ArrayList<>();
        Collections.sort(ants);
        for (int i = 0; i < ants.size() * Configs.generationConservationRatio; i++) {
            bests.add(ants.get(i));
        }
        return bests;
    }

    /**
     * Generate the next generation of ants using the best ants of the current generation
     */
    public Score nextGeneration() {
        //Todo

        int generation = MainGameLoop.simulation.getGeneration() + 1;
        double max = getBest().getFitnessScore();
        double average = 0;
        for (Ant ant : ants) {
            average += ant.getFitnessScore() / ants.size();
        }
        Collections.sort(ants);
        double min =  ants.get(ants.size() - 1).getFitnessScore();
        return new Score(generation, max, average, min);
    }

    /**
     * Get the current population as a List of ants
     * @return a list of all the ants
     */
    public List<Ant> getAnts() {
        return ants;
    }

    /**
     * Return a population from a saved file, complete with position and decision trees
     * @param element a dom element representing the population
     * @return a population characterized by the file
     */
    public static Population getFromElement(Element element) {
        if (element.getTagName().equals("population")) {
            Population pop = new Population();
            NodeList nodes = element.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                if (node.getNodeName().equals("ant")) {
                    Element elem = (Element) node;
                    Ant ant = Ant.getFromElement(elem);
                    if (ant != null)
                        pop.ants.add(ant);
                }
            }
            return pop;
        }
        return null;
    }

    /**
     * make a complete save of the population to an XML file containing positions and decision trees
     * @param document the dom element used to generate the element
     * @return an element representing the population
     */
    public Element getAsElement(Document document) {
        Element populationNode = document.createElement("population");

        for(Ant ant : ants) {
            Element antNode = ant.getAsElement(document);
            populationNode.appendChild(antNode);
        }
        return populationNode;
    }

    public void saveBestAntsToXML(String file) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

            Element antsNode = document.createElement("ants");
            Collections.sort(ants);
            for (int i = 0; i < Configs.generationConservationRatio * ants.size(); i++) {
                Element antElem = ants.get(i).getAsElement(document);
                antsNode.appendChild(antElem);
            }
            document.appendChild(antsNode);

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(file));

            transformer.transform(domSource, streamResult);

        } catch (ParserConfigurationException | TransformerException pce) {
            pce.printStackTrace();
        }
    }

    public void loadBestAntsFromXML(String file) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            File fileXML = new File(file);
            Document xml;

            xml = builder.parse(fileXML);
            Element antsNode = (Element) xml.getElementsByTagName("ants").item(0);
            NodeList ants = antsNode.getElementsByTagName("ant");
            Collections.sort(this.ants);
            for (int i = 0; i < ants.getLength(); i++) {
                Element elem = (Element)ants.item(i);
                Ant ant = Ant.getFromElement(elem);
                if (ant != null)
                    this.ants.set(i, ant);
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }
}
