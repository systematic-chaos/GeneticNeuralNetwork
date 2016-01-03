/************************************************************************
 * Mälardalen University - Learning Systems                             *
 * Lab Assignment 1 - Construction of artificial neural network         *
 * Students: Fco. Javier Fernández-Bravo Peñuela & Alicia García Sastre *
 * File: geneticANN.ArtificialNeuralNetwork.java                        *
 ************************************************************************/
package geneticANN;

import grafos08.GrafoMatriz;
import grafos08.Arco;
import grafos08.ArcoV;
import listas.Lista;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ArtificialNeuralNetwork {

	/* Graph representing the Neural Network */
	protected GrafoMatriz<Perceptron, Synapse> neuralNetwork;

	protected Lista<Perceptron> inputLayer, outputLayer;

	/* Attributes representing the characteristics of the Neural Network */
	protected int nInputs, nOutputs, nHiddenLayers, layerWidth;

	public ArtificialNeuralNetwork(int ni, int no, int nhl, int lw) {
		neuralNetwork = new GrafoMatriz<Perceptron, Synapse>(true);
		nInputs = ni;
		nOutputs = no;
		nHiddenLayers = nhl;
		layerWidth = lw;
		setupNeuralNetwork();
	}

	private void addPerceptron(Perceptron perceptron) {
		neuralNetwork.insertarVertice(perceptron);
	}

	private void addSynapse(Perceptron source, Perceptron target,
			Synapse synapse) {
		neuralNetwork.insertarArco(source, target, synapse);
	}

	/*
	 * Setups the neural network and its topology based on the given *
	 * specifications
	 */
	@SuppressWarnings("unchecked")
	private void setupNeuralNetwork() {
		Perceptron pAux;
		Synapse sAux;
		Lista<Perceptron> list1, list2;
		Random rand = new Random();
		double weight;
		Lista<Arco<Perceptron>> adjBranches;
		Lista<Synapse> connections;

		list1 = new Lista<Perceptron>();
		list2 = new Lista<Perceptron>();

		// Setup input layer
		for (int n = 0; n < nInputs; n++) {
			pAux = new PassivePerceptron();
			addPerceptron(pAux);
			list1.insertarFinal(pAux);
		}
		inputLayer = list1;

		// Setup hidden layers & output layer
		for (int i = 0; i <= nHiddenLayers; i++) {
			for (int j = 0; j < layerWidth; j++) {
				pAux = new GeneticActivePerceptron();
				addPerceptron(pAux);
				list2.insertarFinal(pAux);
				connections = new Lista<Synapse>();
				for (int n = 1; n <= list1.longitud(); n++) {
					weight = rand.nextDouble();
					if (rand.nextBoolean()) {
						weight /= 2.;
					} else {
						weight /= -2.;
					}
					sAux = new Synapse(weight);
					addSynapse(list1.elementoN(n), list2.ultimo(), sAux);
					connections.insertarFinal(sAux);
				}
				((ActivePerceptron) pAux).setInputConnections(connections);
			}
			for (int j = 1; j <= list1.longitud(); j++) {
				pAux = list1.elementoN(j);
				adjBranches = neuralNetwork.arcosAdyacentes(pAux);
				connections = new Lista<Synapse>();
				for (int n = 1; n <= adjBranches.longitud(); n++) {
					connections
							.insertarFinal(((ArcoV<Perceptron, Synapse>) (adjBranches
									.elementoN(n))).peso());
				}
				pAux.setOutputConnections(connections);
			}
			list1 = list2;
			list2 = new Lista<Perceptron>();
		}

		// Setup output layer
		outputLayer = list1;
		for (int i = 1; i <= outputLayer.longitud(); i++) {
			outputLayer.elementoN(i).setOutputConnections(new Lista<Synapse>());
		}
	}

	/* Trains the neural network from a set of training data */
	public double trainNeuralNetwork(double[] trainingDataSet, boolean dump) {
		if (trainingDataSet.length != nInputs + 1)
			return Double.NaN;

		double outputData;
		Lista<Perceptron> layer1, layer2;
		ExecutorService executor = Executors.newFixedThreadPool(layerWidth);
		ArrayList<Callable<Double>> tasks = new ArrayList<Callable<Double>>(
				layerWidth);

		setInputData(trainingDataSet);

		layer1 = inputLayer;
		// Perform the training process iteratively on the successive layers
		// of the neural network
		for (int i = 0; i <= nHiddenLayers; i++) {
			layer2 = new Lista<Perceptron>();
			layer2.concatenar(neuralNetwork.adyacentes(layer1.elementoN(1)));
			for (int j = 1; j <= layer2.longitud(); j++) {
				tasks.add(((ActivePerceptron) layer2.elementoN(j)).new TrainEntryPoint(
						trainingDataSet[nInputs]));
			}
			try {
				executor.invokeAll(tasks);
			} catch (InterruptedException ie) {
				System.err.println("InterruptedException: " + ie.getMessage());
			}
			tasks.clear();
			layer1 = layer2;
		}

		executor.shutdown();
		outputData = getOutputData();

		if (dump) {
			System.out.print("Expected value: " + trainingDataSet[nInputs]);
			System.out.print("\tComputed value: " + outputData);
			System.out.println("\tError: "
					+ Math.abs(trainingDataSet[nInputs] - outputData));
		}
		return outputData;
	}

	/* Trains the neural network from a battery of training data sets */
	public double[] trainNeuralNetwork(double[][] trainingDataSet, boolean dump) {
		if (trainingDataSet[0].length != nInputs + 1)
			return null;

		double[] outputData = new double[trainingDataSet.length];
		Lista<Perceptron> layer1, layer2, initLayer;
		ExecutorService masterExecutor = Executors
				.newFixedThreadPool((nHiddenLayers + 1) * layerWidth);
		ArrayList<Callable<Double>> tasks = new ArrayList<Callable<Double>>(
				(nHiddenLayers + 1) * layerWidth);

		initLayer = inputLayer;
		// Perform the training process iteratively on the successive layers
		// of the neural network
		for (int i = 0; i < nHiddenLayers + trainingDataSet.length; i++) {
			if (i < trainingDataSet.length)
				setInputData(trainingDataSet[i]);
			layer1 = initLayer;

			for (int j = Math.max(i + 1 - trainingDataSet.length, 0); j <= Math
					.min(i, nHiddenLayers); j++) {
				layer2 = neuralNetwork.adyacentes(layer1.elementoN(1));

				for (int k = 1; k <= layerWidth; k++) {
					tasks.add(((ActivePerceptron) layer2.elementoN(k)).new TrainEntryPoint(
							trainingDataSet[i - j][nInputs]));
				}

				if (i >= trainingDataSet.length
						&& j == i + 1 - trainingDataSet.length)
					initLayer = layer2;
				layer1 = layer2;
			}

			try {
				masterExecutor.invokeAll(tasks);
			} catch (InterruptedException ie) {
				System.err.println("InterruptedException: " + ie.getMessage());
			}
			tasks.clear();

			if (i >= nHiddenLayers) {
				outputData[i - nHiddenLayers] = getOutputData();
				if (dump) {
					System.out.print("Expected value: "
							+ trainingDataSet[i - nHiddenLayers][nInputs]);
					System.out.print("\tComputed value: "
							+ outputData[i - nHiddenLayers]);
					System.out
							.println("\tError: "
									+ Math.abs(trainingDataSet[i
											- nHiddenLayers][nInputs]
											- outputData[i - nHiddenLayers]));
				}
			}
		}

		masterExecutor.shutdown();

		return outputData;
	}

	/*
	 * Computes a result by introducing a set of test data in the neural *
	 * network
	 */
	public double testNeuralNetwork(double[] testDataSet, boolean dump) {
		if (testDataSet.length != nInputs + 1)
			return Double.NaN;

		double outputData;
		Lista<Perceptron> layer1, layer2;
		ExecutorService executor = Executors.newFixedThreadPool(layerWidth);
		ArrayList<Callable<Double>> tasks = new ArrayList<Callable<Double>>(
				layerWidth);

		setInputData(testDataSet);

		layer1 = inputLayer;
		// Perform the computing test process iteratively on the successive
		// layers of the neural network
		for (int i = 0; i <= nHiddenLayers; i++) {
			layer2 = new Lista<Perceptron>();
			layer2.concatenar(neuralNetwork.adyacentes(layer1.elementoN(1)));
			for (int j = 1; j <= layer2.longitud(); j++) {
				tasks.add(((ActivePerceptron) layer2.elementoN(j)).new TestEntryPoint(
						testDataSet[nInputs]));
			}
			try {
				executor.invokeAll(tasks);
			} catch (InterruptedException ie) {
				System.err.println("InterruptedException: " + ie.getMessage());
			}
			tasks.clear();
			layer1 = layer2;
		}

		executor.shutdown();
		outputData = getOutputData();

		if (dump) {
			System.out.print("Expected value: " + testDataSet[nInputs]);
			System.out.print("\tComputed value: " + outputData);
			System.out.println("\tError: "
					+ Math.abs(testDataSet[nInputs] - outputData));
		}
		return outputData;
	}

	/*
	 * Computes a result by introducing a set of test data in the neural *
	 * network
	 */
	public double[] testNeuralNetwork(double[][] testDataSet, boolean dump) {
		if (testDataSet[0].length != nInputs + 1)
			return null;

		double[] outputData = new double[testDataSet.length];
		Lista<Perceptron> layer1, layer2, initLayer;
		ExecutorService masterExecutor = Executors
				.newFixedThreadPool((nHiddenLayers + 1) * layerWidth);
		ArrayList<Callable<Double>> tasks = new ArrayList<Callable<Double>>(
				(nHiddenLayers + 1) * layerWidth);

		initLayer = inputLayer;
		// Perform the computing test process iteratively on the successive
		// layers of the neural network
		for (int i = 0; i < nHiddenLayers + testDataSet.length; i++) {
			if (i < testDataSet.length)
				setInputData(testDataSet[i]);
			layer1 = initLayer;

			for (int j = Math.max(i + 1 - testDataSet.length, 0); j <= Math
					.min(i, nHiddenLayers); j++) {
				layer2 = neuralNetwork.adyacentes(layer1.elementoN(1));

				for (int k = 1; k <= layerWidth; k++) {
					tasks.add(((ActivePerceptron) layer2.elementoN(k)).new TestEntryPoint(
							testDataSet[i - j][nInputs]));
				}

				if (i >= testDataSet.length && j == i + 1 - testDataSet.length)
					initLayer = layer2;
				layer1 = layer2;
			}

			try {
				masterExecutor.invokeAll(tasks);
			} catch (InterruptedException ie) {
				System.err.println("InterruptedException: " + ie.getMessage());
			}
			tasks.clear();

			if (i >= nHiddenLayers) {
				outputData[i - nHiddenLayers] = getOutputData();
				if (dump) {
					System.out.print("Expected value: "
							+ testDataSet[i - nHiddenLayers][nInputs]);
					System.out.print("\tComputed value: "
							+ outputData[i - nHiddenLayers]);
					System.out.println("\t Error: "
							+ Math.abs(testDataSet[i - nHiddenLayers][nInputs]
									- outputData[i - nHiddenLayers]));
				}
			}
		}

		masterExecutor.shutdown();

		return outputData;
	}

	/*
	 * Set the values of the input layer from a training process of the * neural
	 * network from a given data set
	 */
	final protected void setInputData(double[] dataSet) {
		for (int n = 0; n < nInputs; n++) {
			((PassivePerceptron) inputLayer.elementoN(n + 1))
					.setInputValue(dataSet[n]);
		}
	}

	/* Get and return the values from the output layer of the neural network */
	final protected double getOutputData() {
		double outputData = 0.;
		for (int n = 0; n < nOutputs; n++) {
			outputData += outputLayer.elementoN(n + 1).getOutputValue();
		}
		return outputData / nOutputs;
	}
}
