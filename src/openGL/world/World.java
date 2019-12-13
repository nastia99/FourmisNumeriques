package openGL.world;

import engineTester.MainGameLoop;
import openGL.entities.RenderableObject;
import openGL.textures.ModelTexture;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class World extends RenderableObject {

    private int sizeX;
    private int sizeZ;
    private Chunk[][] chunks;

    public World(int sizeX, int sizeZ) {
        super(null, new Vector3f(Chunk.SIZE * sizeX/2, 0, Chunk.SIZE * sizeZ/2), 0, 0, 0, 0);
        this.sizeX = sizeX;
        this.sizeZ = sizeZ;
        chunks = new Chunk[sizeX][sizeZ];
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeZ; j++) {
                chunks[i][j] = new Chunk(i, j);
            }
        }
    }

    public World(Chunk[][] chunks) {
        super(null, new Vector3f(Chunk.SIZE * chunks.length/2, 0, Chunk.SIZE * chunks[0].length/2), 0, 0, 0, 0);
        this.chunks = chunks;
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
}
