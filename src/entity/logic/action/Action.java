package entity.logic.action;

import entity.Ant;
import openGL.world.World;

public abstract class Action {

    protected boolean isConditional = false;

    public boolean isConditional() {
        return isConditional;
    }

    public abstract void execute(Ant a, World world);

    public boolean isConditionSatisfied(Ant a, World world) {
        return true;
    }
}
