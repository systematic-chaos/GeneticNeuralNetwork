package geneticANN;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ArrayBlockingQueue;

/* 
 * The neural network cannot be managed to be executed concurrently as a 
 * whole, it needs a mechanism to detect which layers are currently occupied 
 * and perform the computation only in those
 */
public class PipelinedNeuralNetwork extends ArtificialNeuralNetwork implements
		Runnable {

	/*
	 * Data structures for managing the execution of tasks and the processing of
	 * data sets through the pipeline conformed by the neural network
	 */
	private ConcurrentLinkedQueue<AsyncNeuralTask> inputQueue;
	private ArrayBlockingQueue<AsyncNeuralTask> processingQueue;
	private ExecutorCompletionService<Double> executorCompletionService;
	private ExecutorService executorService;

	public PipelinedNeuralNetwork(int ni, int no, int nwh, int lw) {
		super(ni, no, nwh, lw);
		inputQueue = new ConcurrentLinkedQueue<AsyncNeuralTask>();
		processingQueue = new ArrayBlockingQueue<AsyncNeuralTask>(
				nHiddenLayers + 1, true);
		executorCompletionService = new ExecutorCompletionService<Double>(
				executorService = Executors.newCachedThreadPool());
	}

	/*
	 * The neural network features an execution thread of its own, which
	 * operates as a consumer of its input queue
	 */
	@Override
	public void run() {
		ExecutorService masterExecutor = Executors
				.newFixedThreadPool((nHiddenLayers + 1) * layerWidth);
		ArrayList<Callable<Double>> tasks = new ArrayList<Callable<Double>>(
				(nHiddenLayers + 1) * layerWidth);
		AsyncNeuralTask finishedTask;
		double outputData;
		final AsyncNeuralNullTask NULL_TASK = new AsyncNeuralNullTask();
		while (processingQueue.remainingCapacity() > 0)
			processingQueue.add(NULL_TASK);

		/*
		 * Neural network execution loop. Makes use of its processing queue to
		 * discern whether any specific stage of the hidden layers pipeline is
		 * occupied. Every time those layers which are occupied perform their
		 * computation, the contents of the processing queue shift a position
		 * forth
		 */
		while (true) {
			AsyncNeuralTask dataset = inputQueue.poll();
			if (dataset != null)
				setInputData(dataset.dataSet);
			else
				dataset = NULL_TASK;

			try {
				processingQueue.take();
				processingQueue.put(dataset);
			} catch (InterruptedException ie) {
				System.err.println("InterruptedException: " + ie.getMessage());
			}

			List<ActivePerceptron> layer = Arrays.asList(neuralNetwork
					.adyacentes(inputLayer.primero()).toArray(
							new ActivePerceptron[0]));
			List<AsyncNeuralTask> pipelineStatus = Arrays
					.asList(processingQueue.toArray(new AsyncNeuralTask[0]));
			Collections.reverse(pipelineStatus);
			for (AsyncNeuralTask pipelineStage : pipelineStatus) {
				if (pipelineStage instanceof AsyncNeuralTrainingTask) {
					for (ActivePerceptron cell : layer)
						tasks.add(cell.new TrainEntryPoint(
								pipelineStage.dataSet[nInputs]));
				} else if (pipelineStage instanceof AsyncNeuralTestingTask) {
					for (ActivePerceptron cell : layer)
						tasks.add(cell.new TestEntryPoint(
								pipelineStage.dataSet[nInputs]));
				}
				layer = Arrays.asList(neuralNetwork.adyacentes(layer.get(0))
						.toArray(new ActivePerceptron[0]));
			}

			try {
				masterExecutor.invokeAll(tasks);
			} catch (InterruptedException ie) {
				System.err.println("InterruptedException: " + ie.getMessage());
			}
			tasks.clear();

			/*
			 * If the last layer is occupied, set the neural network's output as
			 * the result of the data set being processed in that last layer and
			 * awake the corresponding task
			 */
			if (!((finishedTask = processingQueue.peek()) instanceof AsyncNeuralNullTask)) {
				outputData = getOutputData();
				if (finishedTask.dump) {
					System.out.print("Expected value: "
							+ finishedTask.dataSet[nInputs]);
					System.out.print("\tComputed value: " + outputData);
					System.out.println("\tError: "
							+ Math.abs(finishedTask.dataSet[nInputs]
									- outputData));
				}

				synchronized (finishedTask) {
					finishedTask.result = outputData;
					finishedTask.notify();
				}
			}

			if (Thread.currentThread().isInterrupted()) {
				executorService.shutdown();
				masterExecutor.shutdown();
				break;
			}
		}
	}

	/*
	 * Store these and poll checking for completion until them all finish
	 */
	@Override
	public double trainNeuralNetwork(double[] trainingDataSet, boolean dump) {
		double trainingResult = 0.;
		try {
			trainingResult = trainNeuralNetworkAsync(trainingDataSet, dump)
					.get();
		} catch (InterruptedException ie) {
			System.err.println("InterruptedException: " + ie.getMessage());
		} catch (ExecutionException ee) {
			System.err.println("ExecutionException: " + ee.getMessage());
		}
		return trainingResult;
	}

	/*
	 * Store these and poll checking for completion until them all finish
	 */
	@Override
	public double[] trainNeuralNetwork(double[][] testDataSet, boolean dump) {
		double[] trainingResult = new double[testDataSet.length];
		Future<Double>[] futureResult = trainNeuralNetworkAsync(testDataSet,
				dump);
		try {
			for (int n = 0; n < futureResult.length; n++)
				trainingResult[n] = futureResult[n].get();
		} catch (InterruptedException ie) {
			System.err.println("InterruptedException: " + ie.getMessage());
		} catch (ExecutionException ee) {
			System.err.println("ExecutionException: " + ee.getMessage());
		}
		return trainingResult;
	}

	/*
	 * The tasks submitted to the executor completion service are execution
	 * requests
	 */
	public Future<Double> trainNeuralNetworkAsync(double[] trainingDataSet,
			boolean dump) {
		return trainNeuralNetworkAsync(new AsyncNeuralTrainingTask(
				trainingDataSet, dump));
	}

	/*
	 * The tasks submitted to the executor completion service are execution
	 * requests
	 */
	public Future<Double>[] trainNeuralNetworkAsync(
			double[][] trainingDataSetList, boolean dump) {
		@SuppressWarnings("unchecked")
		Future<Double>[] result = (Future<Double>[]) new Future[trainingDataSetList.length];
		for (int n = 0; n < trainingDataSetList.length; n++)
			result[n] = trainNeuralNetworkAsync(new AsyncNeuralTrainingTask(
					trainingDataSetList[n], dump));
		return result;
	}

	/*
	 * The tasks submitted to the executor completion service are execution
	 * requests
	 */
	private Future<Double> trainNeuralNetworkAsync(
			AsyncNeuralTrainingTask trainingTask) {
		return executorCompletionService.submit(trainingTask);
	}

	/*
	 * Store these and poll checking for completion until them all finish
	 */
	@Override
	public double testNeuralNetwork(double[] testDataSet, boolean dump) {
		double testResult = 0.;
		try {
			testResult = testNeuralNetworkAsync(testDataSet, dump).get();
		} catch (InterruptedException ie) {
			System.err.println("InterruptedException: " + ie.getMessage());
		} catch (ExecutionException ee) {
			System.err.println("ExecutionException: " + ee.getMessage());
		}
		return testResult;
	}

	/*
	 * Store these and poll checking for completion until them all finish
	 */
	@Override
	public double[] testNeuralNetwork(double[][] testDataSet, boolean dump) {
		double[] testResult = new double[testDataSet.length];
		Future<Double>[] futureResult = testNeuralNetworkAsync(testDataSet,
				dump);
		try {
			for (int n = 0; n < futureResult.length; n++)
				testResult[n] = futureResult[n].get();
		} catch (InterruptedException ie) {
			System.err.println("InterruptedException: " + ie.getMessage());
		} catch (ExecutionException ee) {
			System.err.println("ExecutionException: " + ee.getMessage());
		}
		return testResult;
	}

	/*
	 * The tasks submitted to the executor completion service are execution
	 * requests
	 */
	public Future<Double> testNeuralNetworkAsync(double[] testDataSet,
			boolean dump) {
		return testNeuralNetworkAsync(new AsyncNeuralTestingTask(testDataSet,
				dump));
	}

	/*
	 * The tasks submitted to the executor completion service are execution
	 * requests
	 */
	public Future<Double>[] testNeuralNetworkAsync(double[][] testDataSetList,
			boolean dump) {
		@SuppressWarnings("unchecked")
		Future<Double>[] testResult = (Future<Double>[]) new Future[testDataSetList.length];
		for (int n = 0; n < testDataSetList.length; n++)
			testResult[n] = testNeuralNetworkAsync(new AsyncNeuralTestingTask(
					testDataSetList[n], dump));
		return testResult;
	}

	/*
	 * The tasks submitted to the executor completion service are execution
	 * requests
	 */
	private Future<Double> testNeuralNetworkAsync(
			AsyncNeuralTestingTask testTask) {
		return executorCompletionService.submit(testTask);
	}

	@Override
	protected void finalize() throws Throwable {
		executorService.shutdownNow();
	}

	private abstract class AsyncNeuralTask implements Callable<Double> {
		public double[] dataSet;
		public boolean dump;
		public volatile double result;

		public AsyncNeuralTask(double[] dataSet, boolean dump) {
			this.dataSet = dataSet;
			this.dump = dump;
		}

		/*
		 * Insert the data set in the neural network's input queue and get
		 * blocked waiting for the task's completion
		 */
		@Override
		public Double call() {
			inputQueue.offer(this);
			try {
				synchronized (this) {
					wait();
				}
			} catch (InterruptedException ie) {
				System.err.println("InterruptedException: " + ie.getMessage());
			}
			return result;
		}
	}

	private class AsyncNeuralTrainingTask extends AsyncNeuralTask {
		public AsyncNeuralTrainingTask(double[] trainingDataSet, boolean dump) {
			super(trainingDataSet, dump);
		}
	}

	private class AsyncNeuralTestingTask extends AsyncNeuralTask {
		public AsyncNeuralTestingTask(double[] testDataSet, boolean dump) {
			super(testDataSet, dump);
		}
	}

	private class AsyncNeuralNullTask extends AsyncNeuralTask {
		public AsyncNeuralNullTask() {
			super(null, false);
		}
	}
}
