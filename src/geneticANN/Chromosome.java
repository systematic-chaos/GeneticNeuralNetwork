/************************************************************************
 * Mälardalen University - Learning Systems                             *
 * Lab Assignment 1 - Construction of artificial neural network         *
 * Students: Fco. Javier Fernández-Bravo Peñuela & Alicia García Sastre *
 * File: geneticANN.Chromosome.java                                     *
 ************************************************************************/
package geneticANN;

public class Chromosome implements Comparable<Chromosome>, Cloneable {

    private int id;
    private double[] weights;
    private double error, fitness;

    public Chromosome(int i, double[] g) {
        id = i;
        weights = g;
        fitness = (new java.util.Random()).nextDouble();
    }

    public Chromosome(int i, double[] g, double e, double f) {
        id = i;
        weights = g;
        error = e;
        fitness = f;
    }

    public double updateError(double[] input, double result) {
        double output = 0.;
        for (int n = 0; n < input.length; n++) {
            output += input[n] * weights[n];
        }

        output = sigmoid(output);

        error = Math.abs(output - result);
        return error;
    }

    private double sigmoid(double x) {
        return 2. / (1. + Math.pow(Math.E, -x)) + 4.;
    }

    public double updateFitness(double maxError) {
        fitness = maxError - error;
        return fitness;
    }

    public int getId() {
        return id;
    }

    public double[] getWeights() {
        return weights;
    }

    public double getError() {
        return error;
    }

    public double getFitness() {
        return fitness;
    }

    public int compareTo(Chromosome c) {
        int value = -1;
        if (fitness < c.getFitness()) {
            value = -1;
        }
        if (fitness == c.getFitness()) {
            if (id < c.getId()) {
                value = -1;
            }
            if (id == c.getId()) {
                value = 0;
            }
            if (id > c.getId()) {
                value = 1;
            }
        }
        if (fitness > c.getFitness()) {
            value = 1;
        }
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Chromosome) && (this.compareTo((Chromosome) o) == 0);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + this.id;
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.fitness) ^ (Double.doubleToLongBits(this.fitness) >>> 32));
        return hash;
    }

    @Override
    public Object clone() {
        double[] cWeights = new double[weights.length];
        System.arraycopy(weights, 0, cWeights, 0, cWeights.length);
        return new Chromosome(id + 6, cWeights, error, fitness);
    }
}
