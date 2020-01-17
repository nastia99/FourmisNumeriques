package entity.logic.action;

import entity.Ant;
import openGL.world.World;
import org.lwjgl.util.vector.Vector3f;

import java.util.Random;

public class ActionRandom extends Action {

    private Random random;

    public ActionRandom() {
        random = new Random();
    }

    /**
     * Set the ant's target position to an adjacent random tile and update it's target rotation and update it's fitness score
     * @param a the ant executing the action
     * @param world the world in which the action is executed
     */
    @Override
    public void execute(Ant a, World world) {
        switch(random.nextInt(2)) {
            case 1: //Left
                a.setTargetRot(a.getTargetRot() + 90);
                break;
            case 2: //Right
                a.setTargetRot(a.getTargetRot() - 90);
                break;
        }
        a.setTargetPosition(new Vector3f((float)(a.getPosition().x + Math.cos(Math.toRadians(a.getTargetRot()))), 0, (float)(a.getPosition().z - Math.sin(Math.toRadians(a.getTargetRot())))));
        a.addFitness(.1f);
    }

    /**
     * Return the action identifier as a string
     * @return action's identifier
     */
    @Override
    public String toString() {
        return "random";
    }
}
