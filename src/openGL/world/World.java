package openGL.world;

import entity.*;
import entity.logic.Tree;
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

    public void addEntity(RenderableObject entity) {
        Tile tile = (Tile) getChunk((int)(entity.getPosition().x), (int) entity.getPosition().z);
        if (tile != null && entity instanceof Food &&!tile.contains(EntityTypes.FOOD)) {
            tile.addEntity(entity);
        }
        if (tile != null && entity instanceof AntHil &&!tile.contains(EntityTypes.ANTHIL)) {
            tile.addEntity(entity);
        }
    }

    public World(Chunk[][] chunks, HeightsGenerator generator) {
        super(null, new Vector3f(chunks.length / 2.0f, 0, chunks[0].length / 2.0f), 0, 0, 0, 0);
        this.chunks = chunks;
        this.generator = generator;
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
                if (tile != null && (tile.contains(EntityTypes.FOOD) || tile.contains(EntityTypes.ANTHIL))) {
                    list.addAll(tile.getEntities());
                }
            }
        }
        return list;
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
            Element worldNode = document.createElement("world");

            Element populationNode = population.getAsElement(document);
            worldNode.appendChild(populationNode);

            Element objectsNode = document.createElement("objects");
            for (RenderableObject obj : extractEntities()) {
                if (obj instanceof AntHil) {
                    Element elem = document.createElement("anthil");
                    elem.setAttribute("id", String.valueOf(((AntHil)obj).getId()));
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
            if (worldNode == null)
                return null;
            Node objectNode = xml.getElementsByTagName("objects").item(0);
            Node populationNode = xml.getElementsByTagName("population").item(0);
            if (objectNode == null)
                return null;
            if (populationNode == null)
                return null;
            world.population = Population.getFromElement((Element) populationNode);
            NodeList objects = objectNode.getChildNodes();
            for (int i = 0; i < objects.getLength(); i++) {
                org.w3c.dom.Node node = objects.item(i);
                if (node.getNodeName().equals("food") || node.getNodeName().equals("anthil")) {
                    Element elem = (Element)node;
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
                        AntHil antHil = new AntHil(new Vector3f(posX, world.getHeight(posX, posZ), posZ), rotXZ.x, rotY, rotXZ.y);
                        antHil.setId(id);
                        world.addEntity(antHil);
                    }
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return world;
    }
}
