/************************************************************************
 * Mälardalen University - Learning Systems                             *
 * Lab Assignment 1 - Construction of artificial neural network         *
 * Students: Fco. Javier Fernández-Bravo Peñuela & Alicia García Sastre *
 * File: geneticANN.PassivePerceptron.java                              *
 ************************************************************************/
package geneticANN;

public class PassivePerceptron extends Perceptron {

    public PassivePerceptron() {
    }

    public void setInputValue(double value) {
        outputValue = value;
        updateOutputValues();
    }
}
