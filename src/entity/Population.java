package entity;

import configs.Configs;
import openGL.world.World;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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
    public void nextGeneration() {
        //Todo
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
}
