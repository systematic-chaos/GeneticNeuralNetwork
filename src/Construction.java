/************************************************************************
 * Mälardalen University - Learning Systems                             *
 * Lab Assignment 1 - Construction of artificial neural network         *
 * Students: Fco. Javier Fernández-Bravo Peñuela & Alicia García Sastre *
 * File: Construction.java                                              *
 ************************************************************************/
import geneticANN.ArtificialNeuralNetwork;
import geneticANN.PipelinedNeuralNetwork;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Construction {

	public static void main(String[] args) {
		if (args.length == 2) {
			PipelinedNeuralNetwork ann = new PipelinedNeuralNetwork(3, 1, 21, 7);
			Thread neuralNetworkThread = new Thread(ann);
			neuralNetworkThread.start();
			int[] features = { 1, 18, 19, 21 };
			constructANN(ann, args[0], args[1], features, true);
			neuralNetworkThread.interrupt();
		} else {
			System.out
					.println("java ANN <training data set file> <test data set file>");
		}
	}

	public static void constructANN(ArtificialNeuralNetwork ann,
			String trainingFile, String testFile, int[] features, boolean debug) {
		int nDatasets;
		long elapsedTime;

		System.out
				.println("Artificial Neural Network test process without training:");
		nDatasets = testANNBatch(ann, testFile, features, false);

		elapsedTime = System.nanoTime();
		nDatasets = trainANNBatch(ann, trainingFile, features, debug);
		elapsedTime = System.nanoTime() - elapsedTime;
		elapsedTime /= 1000000;
		if (nDatasets > 0) {
			System.out.print("Artificial Neural Network training process");
			System.out.println(" completed in " + elapsedTime + " ms");
			System.out.println(nDatasets + " data sets were processed");
		} else {
			System.out.print("Artificial Neural Network training process");
			System.out.println(" could not be completed successfully");
		}

		elapsedTime = System.nanoTime();
		nDatasets = testANN(ann, testFile, features, debug);
		elapsedTime = System.nanoTime() - elapsedTime;
		elapsedTime /= 1000000;
		if (nDatasets > 0) {
			System.out.print("Artificial Neural Network test process");
			System.out.println(" completed in " + elapsedTime + " ms");
			System.out.println(nDatasets + " data sets were processed");
		} else {
			System.out.print("Artificial Neural Network test process");
			System.out.println(" could not be completed successfully");
		}
	}

	public static int trainANN(ArtificialNeuralNetwork ann, String file,
			int[] columns, boolean debug) {
		FileReader fr = null;
		BufferedReader br = null;
		String trainingData = "";
		int nDatasets = -1;

		try {
			fr = new FileReader(file);
		} catch (FileNotFoundException fnfe) {
			System.err.println("FileNotFoundException: " + fnfe.getMessage());
		}
		if (fr != null) {
			br = new BufferedReader(fr);
			try {
				for (nDatasets = 0; (trainingData = br.readLine()) != null; nDatasets++) {
					ann.trainNeuralNetwork(getDataset(trainingData, columns),
							debug);
				}
				br.close();
				fr.close();
			} catch (IOException ioe) {
				System.err.println("IOException: " + ioe.getMessage());
			}
		}
		return nDatasets;
	}

	public static int trainANNBatch(ArtificialNeuralNetwork ann, String file,
			int[] columns, boolean debug) {
		FileReader fr = null;
		BufferedReader br = null;
		String trainingData = "";
		int nDatasets = -1;

		try {
			fr = new FileReader(file);
		} catch (FileNotFoundException fnfe) {
			System.err.println("FileNotFoundException: " + fnfe.getMessage());
		}
		if (fr != null) {
			br = new BufferedReader(fr);
			try {
				ArrayList<double[]> trainingDatasets = new ArrayList<double[]>();
				for (nDatasets = 0; (trainingData = br.readLine()) != null; nDatasets++) {
					trainingDatasets.add(getDataset(trainingData, columns));
				}
				br.close();
				fr.close();
				ann.trainNeuralNetwork(
						trainingDatasets.toArray(new double[0][]), debug);
			} catch (IOException ioe) {
				System.err.println("IOException: " + ioe.getMessage());
			}
		}
		return nDatasets;
	}

	public static int trainANNAsync(PipelinedNeuralNetwork ann, String file,
			int[] columns, boolean debug) {
		FileReader fr = null;
		BufferedReader br = null;
		String trainingData = "";
		int nDatasets = -1;

		try {
			fr = new FileReader(file);
		} catch (FileNotFoundException fnfe) {
			System.err.println("FileNotFoundException: " + fnfe.getMessage());
		}
		if (fr != null) {
			br = new BufferedReader(fr);
			try {
				for (nDatasets = 0; (trainingData = br.readLine()) != null; nDatasets++) {
					ann.trainNeuralNetworkAsync(
							getDataset(trainingData, columns), debug).get();
				}
				br.close();
				fr.close();
			} catch (IOException ioe) {
				System.err.println("IOException: " + ioe.getMessage());
			} catch (InterruptedException ie) {
				System.err.println("InterruptedException: " + ie.getMessage());
			} catch (ExecutionException ee) {
				System.err.println("ExecutionException: " + ee.getMessage());
			}
		}
		return nDatasets;
	}

	public static int trainANNAsyncBatch(PipelinedNeuralNetwork ann,
			String file, int[] columns, boolean debug) {
		FileReader fr = null;
		BufferedReader br = null;
		String trainingData = "";
		int nDatasets = -1;

		try {
			fr = new FileReader(file);
		} catch (FileNotFoundException fnfe) {
			System.err.println("FileNotFoundException: " + fnfe.getMessage());
		}
		if (fr != null) {
			br = new BufferedReader(fr);
			ArrayList<double[]> trainingDatasets = new ArrayList<double[]>();
			try {
				for (nDatasets = 0; (trainingData = br.readLine()) != null; nDatasets++) {
					trainingDatasets.add(getDataset(trainingData, columns));
				}
				br.close();
				fr.close();
			} catch (IOException ioe) {
				System.err.println("IOException: " + ioe.getMessage());
			}
			try {
				for (Future<Double> trainingResult : ann
						.trainNeuralNetworkAsync(
								trainingDatasets.toArray(new double[0][]),
								debug))
					trainingResult.get();
			} catch (InterruptedException ie) {
				System.err.println("InterruptedException: " + ie.getMessage());
			} catch (ExecutionException ee) {
				System.err.println("ExecutionException: " + ee.getMessage());
			}
		}
		return nDatasets;
	}

	public static int testANN(ArtificialNeuralNetwork ann, String file,
			int[] columns, boolean debug) {
		FileReader fr = null;
		BufferedReader br = null;
		String testData = "";
		int nDatasets = -1;
		ArrayList<Double> absoluteError, relativeError;
		double[] result;
		double output, absoluteErrorValue, relativeErrorValue;

		try {
			fr = new FileReader(file);
		} catch (FileNotFoundException fnfe) {
			System.err.println("FileNotFoundException: " + fnfe.getMessage());
		}
		if (fr != null) {
			br = new BufferedReader(fr);
			try {
				absoluteError = new ArrayList<Double>();
				relativeError = new ArrayList<Double>();
				for (nDatasets = 0; (testData = br.readLine()) != null; nDatasets++) {
					result = getDataset(testData, columns);
					output = ann.testNeuralNetwork(result, debug);
					absoluteError.add(new Double(Math
							.abs(result[result.length - 1] - output)));
					relativeError.add(absoluteError.get(nDatasets)
							/ result[result.length - 1]);
				}
				br.close();
				fr.close();
				absoluteErrorValue = absoluteError.get(0);
				relativeErrorValue = relativeError.get(0);
				for (int n = 1; n < nDatasets; n++) {
					absoluteErrorValue *= absoluteError.get(n);
					relativeErrorValue *= relativeError.get(n);
				}
				absoluteErrorValue = Math.pow(absoluteErrorValue,
						1. / (int) nDatasets);
				relativeErrorValue = Math.pow(relativeErrorValue,
						1. / (int) nDatasets);
				System.out
						.print("Absolute error in Neural Network testing process:  ");
				System.out.printf(Locale.US, "%.4f\n", absoluteErrorValue);
				System.out
						.print("Relative error in Neural Network testing process:  ");
				System.out.printf(Locale.US, "%.4f", relativeErrorValue);
				System.out.print("%\n");
			} catch (IOException ioe) {
				System.err.println("IOException: " + ioe.getMessage());
			}
		}
		return nDatasets;
	}

	public static int testANNBatch(ArtificialNeuralNetwork ann, String file,
			int[] columns, boolean debug) {
		FileReader fr = null;
		BufferedReader br = null;
		String testData = "";
		int nDatasets = -1;
		ArrayList<double[]> result;
		double absoluteErrorValue, relativeErrorValue;
		double[] output, absoluteError, relativeError;

		try {
			fr = new FileReader(file);
		} catch (FileNotFoundException fnfe) {
			System.err.println("FileNotFoundException: " + fnfe.getMessage());
		}
		if (fr != null) {
			br = new BufferedReader(fr);
			try {
				result = new ArrayList<double[]>();
				for (nDatasets = 0; (testData = br.readLine()) != null; nDatasets++) {
					result.add(getDataset(testData, columns));
				}
				br.close();
				fr.close();
				output = ann.testNeuralNetwork(result.toArray(new double[0][]),
						debug);
				absoluteError = new double[output.length];
				relativeError = new double[output.length];
				for (int n = 0; n < output.length; n++) {
					absoluteError[n] = Math
							.abs(result.get(n)[result.get(n).length - 1]
									- output[n]);
					relativeError[n] = absoluteError[n]
							/ result.get(n)[result.get(n).length - 1];
				}
				absoluteErrorValue = absoluteError[0];
				relativeErrorValue = relativeError[0];
				for (int n = 1; n < nDatasets; n++) {
					absoluteErrorValue *= absoluteError[n];
					relativeErrorValue *= relativeError[n];
				}
				absoluteErrorValue = Math.pow(absoluteErrorValue,
						1. / (int) nDatasets);
				relativeErrorValue = Math.pow(relativeErrorValue,
						1. / (int) nDatasets);
				System.out
						.print("Absolute error in Neural Network testing process:  ");
				System.out.printf(Locale.US, "%.4f\n", absoluteErrorValue);
				System.out
						.print("Relative error in Neural Network testing process:  ");
				System.out.printf(Locale.US, "%.4f", relativeErrorValue);
				System.out.print("%\n");
			} catch (IOException ioe) {
				System.err.println("IOException: " + ioe.getMessage());
			}
		}
		return nDatasets;
	}

	public static int testANNAsync(PipelinedNeuralNetwork ann, String file,
			int[] columns, boolean debug) {
		FileReader fr = null;
		BufferedReader br = null;
		String testData = "";
		int nDatasets = -1;
		ArrayList<Double> absoluteError, relativeError;
		double[] result;
		double output, absoluteErrorValue, relativeErrorValue;

		try {
			fr = new FileReader(file);
		} catch (FileNotFoundException fnfe) {
			System.err.println("FileNotFoundException: " + fnfe.getMessage());
		}
		if (fr != null) {
			br = new BufferedReader(fr);
			try {
				absoluteError = new ArrayList<Double>();
				relativeError = new ArrayList<Double>();
				for (nDatasets = 0; (testData = br.readLine()) != null; nDatasets++) {
					result = getDataset(testData, columns);
					output = ann.testNeuralNetworkAsync(result, debug).get();
					absoluteError.add(new Double(Math
							.abs(result[result.length - 1] - output)));
					relativeError.add(absoluteError.get(nDatasets)
							/ result[result.length - 1]);
				}
				br.close();
				fr.close();
				absoluteErrorValue = absoluteError.get(0);
				relativeErrorValue = relativeError.get(0);
				for (int n = 1; n < nDatasets; n++) {
					absoluteErrorValue *= absoluteError.get(n);
					relativeErrorValue *= relativeError.get(n);
				}
				absoluteErrorValue = Math.pow(absoluteErrorValue,
						1. / (int) nDatasets);
				relativeErrorValue = Math.pow(relativeErrorValue,
						1. / (int) nDatasets);
				System.out
						.print("Absolute error in Neural Network testing process:  ");
				System.out.printf(Locale.US, "%.4f\n", absoluteErrorValue);
				System.out
						.print("Relative error in Neural Network testing process:  ");
				System.out.printf(Locale.US, "%.4f", relativeErrorValue);
				System.out.print("%\n");
			} catch (IOException ioe) {
				System.err.println("IOException: " + ioe.getMessage());
			} catch (InterruptedException ie) {
				System.err.println("InterruptedException: " + ie.getMessage());
			} catch (ExecutionException ee) {
				System.err.println("ExecutionException: " + ee.getMessage());
			}
		}
		return nDatasets;
	}

	public static int testANNAsyncBatch(PipelinedNeuralNetwork ann,
			String file, int[] columns, boolean debug) {
		FileReader fr = null;
		BufferedReader br = null;
		String testData = "";
		int nDatasets = -1;
		ArrayList<double[]> result;
		double absoluteErrorValue, relativeErrorValue;
		double[] output, absoluteError, relativeError;

		try {
			fr = new FileReader(file);
		} catch (FileNotFoundException fnfe) {
			System.err.println("FileNotFoundException: " + fnfe.getMessage());
		}
		if (fr != null) {
			br = new BufferedReader(fr);
			try {
				result = new ArrayList<double[]>();
				for (nDatasets = 0; (testData = br.readLine()) != null; nDatasets++) {
					result.add(getDataset(testData, columns));
				}
				br.close();
				fr.close();
				output = new double[nDatasets];
				Future<Double>[] testResultList = ann.testNeuralNetworkAsync(
						result.toArray(new double[0][]), debug);
				for (int n = 0; n < testResultList.length; n++)
					output[n] = testResultList[n].get();
				absoluteError = new double[output.length];
				relativeError = new double[output.length];
				for (int n = 0; n < output.length; n++) {
					absoluteError[n] = Math
							.abs(result.get(n)[result.get(n).length - 1]
									- output[n]);
					relativeError[n] = absoluteError[n]
							/ result.get(n)[result.get(n).length - 1];
				}
				absoluteErrorValue = absoluteError[0];
				relativeErrorValue = relativeError[0];
				for (int n = 1; n < nDatasets; n++) {
					absoluteErrorValue *= absoluteError[n];
					relativeErrorValue *= relativeError[n];
				}
				absoluteErrorValue = Math.pow(absoluteErrorValue,
						1. / (int) nDatasets);
				relativeErrorValue = Math.pow(relativeErrorValue,
						1. / (int) nDatasets);
				System.out
						.print("Absolute error in Neural Network testing process:  ");
				System.out.printf(Locale.US, "%.4f\n", absoluteErrorValue);
				System.out
						.print("Relative error in Neural Network testing process:  ");
				System.out.printf(Locale.US, "%.4f", relativeErrorValue);
				System.out.print("%\n");
			} catch (IOException ioe) {
				System.err.println("IOException: " + ioe.getMessage());
			} catch (InterruptedException ie) {
				System.err.println("InterruptedException: " + ie.getMessage());
			} catch (ExecutionException ee) {
				System.err.println("ExecutionException: " + ee.getMessage());
			}
		}
		return nDatasets;
	}

	public static double[] getDataset(String data, int[] columns) {
		String[] allFeatures;
		double[] selectedFeatures;
		data = data.substring(2);
		allFeatures = data.split("  ");
		selectedFeatures = new double[columns.length];
		for (int n = 0; n < columns.length; n++) {
			if (columns[n] < allFeatures.length) {
				selectedFeatures[n] = Double.valueOf(allFeatures[columns[n]]
						.trim());
			}
		}
		return selectedFeatures;
	}
}
