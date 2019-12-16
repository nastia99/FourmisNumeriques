package entity.logic.action;

import entity.Ant;
import openGL.world.World;

public class ActionCondFood extends Action {

    public ActionCondFood() {
        isConditional = true;
    }

    @Override
    public void execute(Ant a, World world) {

    }

    @Override
    public boolean isConditionSatisfied(Ant a, World world) {
        return false;
    }
}
