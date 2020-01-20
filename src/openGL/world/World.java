package openGL.world;

import configs.Configs;
import entity.*;
import openGL.entities.RenderableObject;
import openGL.utils.Maths;
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
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class World extends RenderableObject {

    private static int sizeX = 50;
    private static int sizeZ = 50;
    private Chunk[][] chunks;
    private HeightsGenerator generator;
    private Population population;

    public World() {
        super(null, new Vector3f(sizeX / 2.0f, 0, sizeZ / 2.0f), 0, 0, 0, 0);
        generator = new HeightsGenerator(sizeX * (Chunk.VERTEX_COUNT - 1), sizeZ * (Chunk.VERTEX_COUNT - 1));
        generator.generateHeight(new Vector2f(sizeX/2f, sizeZ/2f));
        chunks = new Tile[sizeX][sizeZ];
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeZ; j++) {
                chunks[i][j] = new Tile(i, j, generator);
            }
        }
        fertilize((int) (Configs.nbAnts * Configs.maxNbFoodPerAnt));
    }

    public void regenerate(List<AntHill> homes) {
        chunks = new Tile[sizeX][sizeZ];
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeZ; j++) {
                chunks[i][j] = new Tile(i, j, generator);
            }
        }
        fertilize((int) (Configs.nbAnts * Configs.maxNbFoodPerAnt));
        for (AntHill anthil : homes)
            addEntity(anthil);
    }

    public void fertilize(int nbFood) {
        Random rand = new Random();
        for (int i = 0; i < nbFood; i++) {
            boolean succesfullyAdded;
            int tries = 0;
            do {
                tries++;
                Vector3f pos = new Vector3f(rand.nextInt(sizeX) + .5f, 0, rand.nextInt(sizeZ) + .5f);
                pos.y = getHeight(pos.x, pos.z);

                Vector2f rotXZ = Maths.calculateXZRotations(this, pos.x, pos.z);
                float rX = rotXZ.x;
                float rZ = rotXZ.y;

                succesfullyAdded = addEntity(new Food(pos, rX, rand.nextInt(360), rZ));
                if (tries > 5) break;
            } while (!succesfullyAdded);
        }
    }

    public World(int seed) {
        super(null, new Vector3f(sizeX / 2.0f, 0, sizeZ / 2.0f), 0, 0, 0, 0);
        generator = new HeightsGenerator(seed, sizeX * (Chunk.VERTEX_COUNT - 1), sizeZ * (Chunk.VERTEX_COUNT - 1));
        generator.generateHeight(new Vector2f(sizeX/2f, sizeZ/2f));
        chunks = new Tile[sizeX][sizeZ];
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeZ; j++) {
                chunks[i][j] = new Tile(i, j, generator);
            }
        }
        fertilize(300);
    }

    public Population getPopulation() {
        return population;
    }

    public void setPopulation(Population population) {
        this.population = population;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeZ() {
        return sizeZ;
    }

    public boolean addEntity(RenderableObject entity) {
        Tile tile = (Tile) getChunk((int)(entity.getPosition().x), (int) entity.getPosition().z);
        if (tile != null && entity instanceof Food &&!tile.contains(EntityTypes.FOOD)) {
            tile.addEntity(entity);
            return true;
        }
        if (tile != null && entity instanceof AntHill &&!tile.contains(EntityTypes.ANTHILL)) {
            tile.addEntity(entity);
            return true;
        }
        return false;
    }

    public List<Chunk> getChunks() {
        List<Chunk> chunkList = new ArrayList<Chunk>();
        for (Chunk[] row : chunks) {
            chunkList.addAll(Arrays.asList(row));
        }
        return chunkList;
    }

    public Chunk getChunk(int x, int z) {
        if (x >= 0 && x < sizeX && z >= 0 && z < sizeZ)
            return chunks[x][z];
        return null;
    }

    public Chunk getChunkInWorldCoords(float x, float z) {
        if (x >= 0 && x < sizeX && z >= 0 && z < sizeZ)
            return chunks[(int) x][(int) z];
        return null;
    }

    public float getHeight(float x, float z) {
        if (x >= sizeX)
            x -= sizeX;
        if (x < 0)
            x += sizeX;
        if (z >= sizeZ)
            z -= sizeZ;
        if (z < 0)
            z += sizeZ;
        int gridX = (int) Math.floor(x);
        int gridZ = (int) Math.floor(z);
        return generator.getHeightInChunk(gridX, gridZ,(x - gridX) * (Chunk.VERTEX_COUNT - 1), (z - gridZ) * (Chunk.VERTEX_COUNT - 1));
    }

    public Vector3f getNormal(float x, float z) {
        if (x >= sizeX)
            x -= sizeX;
        if (x < 0)
            x += sizeX;
        if (z >= sizeZ)
            z -= sizeZ;
        if (z < 0)
            z += sizeZ;
        int gridX = (int) Math.floor(x);
        int gridZ = (int) Math.floor(z);
        return chunks[gridX][gridZ].getNormal((x - gridX) * (Chunk.VERTEX_COUNT - 1), (z - gridZ) * (Chunk.VERTEX_COUNT - 1));
    }

    public List<RenderableObject> extractEntities() {
        List<RenderableObject> list = new ArrayList<RenderableObject>();
        for (int i = 0; i < getSizeX(); i++) {
            for (int j = 0; j < getSizeZ(); j++) {
                Tile tile = (Tile) getChunk(i, j);
                if (tile != null && (tile.contains(EntityTypes.FOOD) || tile.contains(EntityTypes.ANTHILL))) {
                    list.addAll(tile.getEntities());
                }
            }
        }
        return list;
    }

    public List<RenderableObject> extractEntities(EntityTypes type) {
        List<RenderableObject> list = new ArrayList<RenderableObject>();
        for (int i = 0; i < getSizeX(); i++) {
            for (int j = 0; j < getSizeZ(); j++) {
                Tile tile = (Tile) getChunk(i, j);
                if (tile != null && tile.contains(type)) {
                    list.addAll(tile.getEntities(type));
                }
            }
        }
        return list;
    }

    /**
     * Get the world as a DOM element
     * @param document the Document used to generate the elements
     * @return a DOM element representing the world
     */
    public Element getAsElement(Document document) {
        Element worldNode = document.createElement("world");
        worldNode.setAttribute("seed", String.valueOf(generator.getSeed()));

        Element populationNode = population.getAsElement(document);
        worldNode.appendChild(populationNode);

        Element objectsNode = document.createElement("objects");
        for (RenderableObject obj : extractEntities()) {
            if (obj instanceof AntHill) {
                Element elem = document.createElement("anthil");
                elem.setAttribute("id", String.valueOf(((AntHill)obj).getId()));
                elem.setAttribute("posX", String.valueOf(obj.getPosition().x));
                elem.setAttribute("rotY", String.valueOf(obj.getRotY()));
                elem.setAttribute("posZ", String.valueOf(obj.getPosition().z));
                objectsNode.appendChild(elem);
            }
            if (obj instanceof Food) {
                Element elem = document.createElement("food");
                elem.setAttribute("posX", String.valueOf(obj.getPosition().x));
                elem.setAttribute("rotY", String.valueOf(obj.getRotY()));
                elem.setAttribute("posZ", String.valueOf(obj.getPosition().z));
                objectsNode.appendChild(elem);
            }
        }
        worldNode.appendChild(objectsNode);
        return worldNode;
    }

    /**
     * Save the current world to an XML file
     * containing the world seed, the population and the objects
     * basically save the current simulation
     * @param path the file to save to
     */
    public void saveToXML(String path) {
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            Element worldNode = getAsElement(document);
            document.appendChild(worldNode);

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
     * Get a world from an XML file
     * @param path the file representing the world
     * @return a world characterized by the file
     */
    public static World loadFromXML(String path) {
        World world = new World();
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            File fileXML = new File(path);
            Document xml;

            xml = builder.parse(fileXML);
            Element worldNode = (Element) xml.getElementsByTagName("world").item(0);
            world = getFromElement(worldNode);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return world;
    }

    /**
     * Get a world from an DOM element
     * @param element DOM element representing the world
     * @return a world characterized by the DOM element
     */
    public static World getFromElement(Element element) {

        if (element == null)
            return null;
        if (!element.getTagName().equals("world"))
            return null;
        int seed = Integer.parseInt(element.getAttribute("seed"));
        Node objectNode = element.getElementsByTagName("objects").item(0);
        Node populationNode = element.getElementsByTagName("population").item(0);
        if (objectNode == null)
            return null;
        if (populationNode == null)
            return null;
        World world = new World(seed);
        world.population = Population.getFromElement((Element) populationNode);
        NodeList objects = objectNode.getChildNodes();
        for (int i = 0; i < objects.getLength(); i++) {
            Node node = objects.item(i);
            if (node.getNodeName().equals("food") || node.getNodeName().equals("anthil")) {
                Element elem = (Element) node;
                float posX = Float.parseFloat(elem.getAttribute("posX"));
                float posZ = Float.parseFloat(elem.getAttribute("posZ"));
                float rotY = Float.parseFloat(elem.getAttribute("rotY"));
                if (elem.getTagName().equals("food")) {
                    Vector2f rotXZ = Maths.calculateXZRotations(world, posX, posZ);
                    Food food = new Food(new Vector3f(posX, world.getHeight(posX, posZ), posZ), rotXZ.x, rotY, rotXZ.y);
                    world.addEntity(food);
                }
                if (elem.getTagName().equals("anthil")) {
                    int id = Integer.parseInt(elem.getAttribute("id"));
                    Vector2f rotXZ = Maths.calculateXZRotations(world, posX, posZ);
                    AntHill antHill = new AntHill(new Vector3f(posX, world.getHeight(posX, posZ), posZ), rotXZ.x, rotY, rotXZ.y);
                    antHill.setId(id);
                    world.addEntity(antHill);
                }
            }
        }
        return world;
    }

    /**
     * Repopulate a world from an DOM element
     * @param element DOM element representing the world
     */
    public void loadFromElement(Element element) {
        if (element == null)
            return;
        if (!element.getTagName().equals("world"))
            return;
        Node objectNode = element.getElementsByTagName("objects").item(0);
        Node populationNode = element.getElementsByTagName("population").item(0);
        if (objectNode == null)
            return;
        if (populationNode == null)
            return;
        population = Population.getFromElement((Element) populationNode);
        NodeList objects = objectNode.getChildNodes();
        for (int i = 0; i < objects.getLength(); i++) {
            Node node = objects.item(i);
            if (node.getNodeName().equals("food") || node.getNodeName().equals("anthil")) {
                Element elem = (Element) node;
                float posX = Float.parseFloat(elem.getAttribute("posX"));
                float posZ = Float.parseFloat(elem.getAttribute("posZ"));
                float rotY = Float.parseFloat(elem.getAttribute("rotY"));
                if (elem.getTagName().equals("food")) {
                    Vector2f rotXZ = Maths.calculateXZRotations(this, posX, posZ);
                    Food food = new Food(new Vector3f(posX, this.getHeight(posX, posZ), posZ), rotXZ.x, rotY, rotXZ.y);
                    this.addEntity(food);
                }
                if (elem.getTagName().equals("anthil")) {
                    int id = Integer.parseInt(elem.getAttribute("id"));
                    Vector2f rotXZ = Maths.calculateXZRotations(this, posX, posZ);
                    AntHill antHill = new AntHill(new Vector3f(posX, this.getHeight(posX, posZ), posZ), rotXZ.x, rotY, rotXZ.y);
                    antHill.setId(id);
                    this.addEntity(antHill);
                }
            }
        }
    }
}
