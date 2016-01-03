/************************************************************************
 * Mälardalen University - Learning Systems                             *
 * Lab Assignment 1 - Construction of artificial neural network         *
 * Students: Fco. Javier Fernández-Bravo Peñuela & Alicia García Sastre *
 * File: geneticANN.ActivePerceptron.java                               *
 ************************************************************************/
package geneticANN;

import java.util.concurrent.Callable;

import listas.Lista;

public class ActivePerceptron extends Perceptron {

	protected double[] inputValues;

	protected double[] inputWeights;

	protected Lista<Synapse> inputConnections;

	protected double resultValue;

	public ActivePerceptron() {
		inputConnections = null;
		outputConnections = null;
	}

	public void setInputConnections(Lista<Synapse> ic) {
		inputConnections = ic;
		inputValues = new double[inputConnections.longitud()];
		inputWeights = new double[inputConnections.longitud()];
		updateInputValues();
	}

	protected void updateInputValues() {
		for (int n = 0; n < inputConnections.longitud(); n++) {
			inputValues[n] = inputConnections.elementoN(n + 1).getValue();
			inputWeights[n] = inputConnections.elementoN(n + 1).getWeight();
		}
	}

	public void train(double trainingResult) {
		updateInputValues();
		setResultValue(trainingResult);
		compute();
		updateOutputValues();
		updateWeights();
	}

	public void test(double testResult) {
		updateInputValues();
		setResultValue(testResult);
		compute();
		updateOutputValues();
	}

	public void setResultValue(double rv) {
		resultValue = rv;
	}

	protected void compute() {
		double output = 0.;
		for (int n = 0; n < inputValues.length; n++) {
			output += inputValues[n] * inputWeights[n];
		}
		outputValue = sigmoid(output);
	}

	protected void updateWeights() {
		double incw;
		for (int n = 0; n < inputConnections.longitud(); n++) {
			incw = 0.1 * (resultValue - outputValue) * inputValues[n];
			inputWeights[n] += incw;
			inputConnections.elementoN(n + 1).setWeight(inputWeights[n]);
		}
	}

	private double sigmoid(double x) {
		return 2. / (1. + Math.pow(Math.E, -x)) + 4.;
	}

	@Override
	public int compareTo(Perceptron other) {
		if (!(other instanceof ActivePerceptron))
			return super.compareTo(other);

		ActivePerceptron otherActive = (ActivePerceptron) other;
		if (resultValue < otherActive.resultValue)
			return -1;
		if (resultValue > otherActive.resultValue)
			return 1;
		else
			return 0;
	}

	private abstract class EntryPoint implements Runnable, Callable<Double> {

		protected double result;

		public EntryPoint(double result) {
			this.result = result;
		}

		@Override
		public Double call() {
			run();
			return outputValue;
		}
	}

	public class TrainEntryPoint extends EntryPoint {

		public TrainEntryPoint(double trainingResult) {
			super(trainingResult);
		}

		@Override
		public void run() {
			train(result);
		}
	}

	public class TestEntryPoint extends EntryPoint {

		public TestEntryPoint(double testResult) {
			super(testResult);
		}

		@Override
		public void run() {
			test(result);
		}
	}
}
