package entity.logic.action;

import entity.Ant;
import openGL.utils.Maths;
import openGL.world.World;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActionBackHome extends Action {

    private static final int A_STAR_WEIGHT_MULTIPLIER = 5;

    @Override
    public void execute(Ant a, World world) {
        Vector2f end = new Vector2f(0, 0);
        Vector2f path = aStar(new Vector2f(a.getPosition().x - .5f, a.getPosition().z - .5f), end, world);
        if (path != null) {
            a.setTargetPosition(new Vector3f(path.x + .5f, 0, path.y + .5f));
        }
    }

    private float h(Vector2f start, Vector2f end, World world) {
        float dist = Float.MAX_VALUE;
        Vector2f[] loopedEnd = {
                new Vector2f(end.x, end.y),
                new Vector2f(end.x + world.getSizeX(), end.y),
                new Vector2f(end.x - world.getSizeX(), end.y),
                new Vector2f(end.x, end.y + world.getSizeZ()),
                new Vector2f(end.x, end.y - world.getSizeZ()),
                new Vector2f(end.x + world.getSizeX(), end.y + world.getSizeZ()),
                new Vector2f(end.x + world.getSizeX(), end.y - world.getSizeZ()),
                new Vector2f(end.x - world.getSizeX(), end.y + world.getSizeZ()),
                new Vector2f(end.x - world.getSizeX(), end.y - world.getSizeZ())
        };
        for(Vector2f potentialEnd : loopedEnd) {
            float endDist = (float) Math.sqrt(Math.pow(start.x - potentialEnd.x, 2) + Math.pow(start.y - potentialEnd.y, 2));
            if (endDist < dist)
                dist = endDist;
        }
        return A_STAR_WEIGHT_MULTIPLIER * dist;
    }

    private float getValue(Map<Vector2f, Float> map, Vector2f vec) {
        return (map.get(vec) == null ? Float.MAX_VALUE - 1 : map.get(vec));
    }

    private Vector2f aStar(Vector2f start, Vector2f end, World world) {
        List<Vector2f> openList = new ArrayList<>();
        openList.add(start);

        Map<Vector2f, Vector2f> cameFrom = new HashMap<>();
        Map<Vector2f, Float> gScore = new HashMap<>();
        Map<Vector2f, Float> fScore = new HashMap<>();

        gScore.put(start, 0f);
        fScore.put(start, (h(start, end, world)));

        while(openList.size() > 0) {
            Vector2f current = openList.get(0);
            for (Vector2f v : openList) {
                if (getValue(fScore, v) < getValue(fScore, current))
                    current = v;
            }

            if (current.equals(end)) {
                Vector2f last = null;
                while (cameFrom.containsKey(current)) {
                    last = current;
                    current = cameFrom.get(current);
                }
                return last;
            }
            openList.remove(current);
            Vector2f worldSize = new Vector2f(world.getSizeX(), world.getSizeZ());
            Vector2f[] neighbors = {
                    Maths.loopVector(new Vector2f(current.x - 1, current.y), worldSize),
                    Maths.loopVector(new Vector2f(current.x + 1, current.y), worldSize),
                    Maths.loopVector(new Vector2f(current.x, current.y - 1), worldSize),
                    Maths.loopVector(new Vector2f(current.x, current.y + 1), worldSize)
            };

            for (Vector2f vec : neighbors) {
                float newGScore = getValue(gScore, current) + 1;
                if (newGScore < getValue(gScore, vec)) {

                    cameFrom.put(vec, current);
                    gScore.put(vec, newGScore);
                    fScore.put(vec, newGScore + h(vec, end, world));
                    if (!openList.contains(vec)) {
                        openList.add(vec);
                    }
                }
            }
        }
        return null;
    }
}
