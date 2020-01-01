package entity;

import openGL.entities.RenderableObject;
import openGL.world.Chunk;
import openGL.world.HeightsGenerator;

import java.util.ArrayList;
import java.util.List;

public class Tile extends Chunk {

    private List<RenderableObject> entities;

    public Tile(int gridX, int gridZ, HeightsGenerator generator) {
        super(gridX, gridZ, generator);
        entities = new ArrayList<RenderableObject>();
    }

    public void addEntity(RenderableObject entity) {
        entities.add(entity);
    }

    public RenderableObject getEntity(EntityTypes e) {
        switch (e) {
            case ANT:
                for (RenderableObject ro : entities)
                    if (ro instanceof Ant) {
                        entities.remove(ro);
                        return ro;
                    }
                break;
            case FOOD:
                for (RenderableObject ro : entities)
                    if (ro instanceof Food) {
                        entities.remove(ro);
                        return ro;
                    }
                break;
            case ANTHILL:
                //Todo
                break;
        }
        return null;
    }

    public boolean contains(EntityTypes e) {
        switch (e) {
            case ANT:
                for (RenderableObject ro : entities)
                    if (ro instanceof Ant)
                        return true;
                break;
            case FOOD:
                for (RenderableObject ro : entities)
                    if (ro instanceof Food)
                        return true;
                break;
            case ANTHILL:
                //Todo
                break;
        }
        return false;
    }

    public List<RenderableObject> getEntities() {
        return entities;
    }
}
