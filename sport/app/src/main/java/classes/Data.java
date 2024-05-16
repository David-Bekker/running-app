package classes;

public class Data {
    protected double step_size;
    protected double weight;

    public Data(double step_size, double weight) {
        this.step_size = step_size;
        this.weight = weight;
    }

    public double getStep_size() {
        return step_size;
    }

    public void setStep_size(double step_size) {
        this.step_size = step_size;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return "Data{" +
                "step_size=" + step_size +
                ", weight=" + weight +
                '}';
    }
}
