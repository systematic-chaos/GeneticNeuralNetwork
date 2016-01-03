/************************************************************************
 * Mälardalen University - Learning Systems                             *
 * Lab Assignment 1 - Construction of artificial neural network         *
 * Students: Fco. Javier Fernández-Bravo Peñuela & Alicia García Sastre *
 * File: geneticANN.Synapse.java                                        *
 ************************************************************************/
package geneticANN;

public class Synapse implements Comparable<Synapse> {

    private double weight, value;

    public Synapse(double w) {
        weight = w;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double w) {
        weight = w;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double v) {
        value = v;
    }

    @Override
    public int compareTo(Synapse other) {
        assert other != null;

        if (value < other.value)
            return -1;
        if (value > other.value)
            return 1;
        else
            return 0;
    }
}
