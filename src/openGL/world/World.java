package openGL.world;

import engineTester.MainGameLoop;
import entity.EntityTypes;
import entity.Tile;
import openGL.entities.RenderableObject;
import openGL.textures.ModelTexture;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class World extends RenderableObject {

    private int sizeX;
    private int sizeZ;
    private Chunk[][] chunks;
    private HeightsGenerator generator;

    public World(int sizeX, int sizeZ) {
        super(null, new Vector3f(sizeX / 2.0f, 0, sizeZ / 2.0f), 0, 0, 0, 0);
        generator = new HeightsGenerator(sizeX * (Chunk.VERTEX_COUNT - 1), sizeZ * (Chunk.VERTEX_COUNT - 1));
        generator.generateHeight(new Vector2f(sizeX/2f, sizeZ/2f));
        this.sizeX = sizeX;
        this.sizeZ = sizeZ;
        chunks = new Chunk[sizeX][sizeZ];
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeZ; j++) {
                chunks[i][j] = new Chunk(i, j, generator);
            }
        }
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeZ() {
        return sizeZ;
    }

    public World(Chunk[][] chunks, HeightsGenerator generator) {
        super(null, new Vector3f(chunks.length / 2.0f, 0, chunks[0].length / 2.0f), 0, 0, 0, 0);
        this.chunks = chunks;
        this.generator = generator;
        this.sizeX = chunks.length;
        this.sizeZ = chunks[0].length;
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
}
