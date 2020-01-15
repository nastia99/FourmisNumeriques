package simulation;

public class Score {

    private int generation;
    private double max;
    private double average;
    private double min;

    public Score(int generation, double max, double average, double min) {
        this.generation = generation;
        this.max = max;
        this.average = average;
        this.min = min;
    }

    public int getGeneration() {
        return generation;
    }

    public double getMax() {
        return max;
    }

    public double getAverage() {
        return average;
    }

    public double getMin() {
        return min;
    }
}
