/************************************************************************
 * Mälardalen University - Learning Systems                             *
 * Lab Assignment 1 - Construction of artificial neural network         *
 * Students: Fco. Javier Fernández-Bravo Peñuela & Alicia García Sastre *
 * File: geneticANN.Perceptron.java                                     *
 ************************************************************************/
package geneticANN;

import listas.Lista;

public abstract class Perceptron implements Comparable<Perceptron> {

    protected double outputValue;
    protected Lista<Synapse> outputConnections;

    public double getOutputValue() {
        return outputValue;
    }

    public void setOutputConnections(Lista<Synapse> oc) {
        outputConnections = oc;
    }

    protected void updateOutputValues() {
        for (int n = 1; n <= outputConnections.longitud(); n++) {
            outputConnections.elementoN(n).setValue(outputValue);
        }
    }
    
    @Override
    public int compareTo(Perceptron other) {
        if (outputValue < other.outputValue)
            return -1;
        if (outputValue > other.outputValue)
            return 1;
        else
            return 0;
    }
}
