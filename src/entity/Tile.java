package entity;

import engineTester.MainGameLoop;
import openGL.renderEngine.Loader;
import openGL.textures.ModelTexture;
import openGL.world.Chunk;

public class Tile extends Chunk {

    public Tile(int gridX, int gridZ) {
        super(gridX, gridZ);
    }
}
