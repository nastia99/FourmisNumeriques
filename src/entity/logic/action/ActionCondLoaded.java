package entity.logic.action;

import entity.Ant;
import openGL.world.World;

public class ActionCondLoaded extends Action {

    public ActionCondLoaded() {
        isConditional = true;
    }

    @Override
    public void execute(Ant a, World world) {
        return;
    }

    @Override
    public boolean isConditionSatisfied(Ant a, World world) {
        return a.isCarryingFood();
    }

    @Override
    public String toString() {
        return "isLoaded";
    }
}
