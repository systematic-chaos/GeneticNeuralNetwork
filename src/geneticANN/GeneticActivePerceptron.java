/************************************************************************
 * Mälardalen University - Learning Systems                             *
 * Lab Assignment 1 - Construction of artificial neural network         *
 * Students: Fco. Javier Fernández-Bravo Peñuela & Alicia García Sastre *
 * File: geneticANN.GeneticActivePerceptron.java                        *
 ************************************************************************/
package geneticANN;

import java.util.TreeSet;
import listas.Lista;
import java.util.Iterator;
import java.util.Random;

public class GeneticActivePerceptron extends ActivePerceptron {

    private TreeSet<Chromosome> matingPool;
    private final int populationSize = 6;
    private Chromosome weight;
    private double maxError;

    public GeneticActivePerceptron() {
        super();
        matingPool = new TreeSet<Chromosome>();
    }

    @Override
    public void setInputConnections(Lista<Synapse> ic) {
        inputConnections = ic;
        inputValues = new double[inputConnections.longitud()];
        inputWeights = new double[inputConnections.longitud()];
        createInitialPopulation();
    }

    @Override
    protected void updateWeights() {
        updatePopulation();

        select();
        crossover();
        mutation();

        updateConnections();
    }

    private void createInitialPopulation() {
        double[] individual;
        Random rand = new Random();

        for (int i = 0; i < populationSize; i++) {
            individual = new double[inputConnections.longitud()];
            for (int j = 0; j < individual.length; j++) {
                individual[j] = rand.nextDouble();
                if (rand.nextBoolean()) {
                    individual[j] /= 2.;
                } else {
                    individual[j] /= -2.;
                }
            }
            matingPool.add(new Chromosome(i, individual));
        }
    }

    private void updatePopulation() {
        Iterator<Chromosome> mpIterator = matingPool.iterator();
        Lista<Chromosome> lAux = new Lista<Chromosome>();
        double[] input = new double[inputWeights.length];

        System.arraycopy(inputWeights, 0, input, 0, inputWeights.length);
        weight = new Chromosome(populationSize, input);
        maxError = weight.updateError(inputValues, resultValue);

        while (mpIterator.hasNext()) {
            lAux.insertarPrincipio(mpIterator.next());
        }

        for (int n = 1; n <= lAux.longitud(); n++) {
            if (lAux.elementoN(n).updateError(inputValues, resultValue) > maxError) {
                maxError = lAux.elementoN(n).getError();
            }
        }

        weight.updateFitness(maxError);
        for (int n = 1; n <= lAux.longitud(); n++) {
            matingPool.remove(lAux.elementoN(n));
            lAux.elementoN(n).updateFitness(maxError);
            matingPool.add(lAux.elementoN(n));
        }

        if (!matingPool.contains(weight) && matingPool.lower(weight) != null) {
            matingPool.remove(matingPool.first());
            matingPool.add(weight);
        }
    }

    private void select() {
        if (matingPool.size() > 1) {
            matingPool.remove(matingPool.first());
            matingPool.add((Chromosome) (matingPool.last().clone()));
        }
    }

    private void crossover() {
        if (matingPool.size() >= 4) {
            Lista<Chromosome> mpList = new Lista<Chromosome>();
            Chromosome[] parents = new Chromosome[2];
            Chromosome[] offspring = new Chromosome[2];
            Chromosome[] cAuxParents = new Chromosome[4];
            Chromosome[] cAuxOffspring = new Chromosome[4];
            Iterator<Chromosome> mpIterator = matingPool.descendingIterator();

            while (mpIterator.hasNext()) {
                mpList.insertarFinal(mpIterator.next());
            }
            for (int n = 0; n < 4; n++) {
                cAuxParents[n] = mpList.elementoN(n + 1);
            }
            parents[0] = cAuxParents[0];
            parents[1] = cAuxParents[2];
            recombine(parents, offspring);
            System.arraycopy(offspring, 0, cAuxOffspring, 0, offspring.length);

            parents[0] = cAuxParents[1];
            parents[1] = cAuxParents[3];
            recombine(parents, offspring);
            System.arraycopy(offspring, 0, cAuxOffspring, 2, offspring.length);

            for (int n = 0; n < cAuxParents.length; n++) {
                matingPool.remove(cAuxParents[n]);
                matingPool.add(cAuxOffspring[n]);
            }

            for (int i = 5; i <= mpList.longitud(); i += 2) {
                parents[0] = mpList.elementoN(i);
                if (i + 1 <= mpList.longitud()) {
                    parents[1] = mpList.elementoN(i + 1);
                } else {
                    parents[1] = mpList.primero();
                }
                recombine(parents, offspring);

                for (int j = 0; j < parents.length; j++) {
                    matingPool.remove(parents[j]);
                    matingPool.add(offspring[j]);
                }
            }
        }
    }

    private void recombine(Chromosome[] parents, Chromosome[] offspring) {
        Random rand = new Random();
        double[][] weights = new double[4][parents[0].getWeights().length];
        double[] a = new double[2];

        for (int n = 0; n < 2; n++) {
            weights[n] = parents[n].getWeights();
            a[n] = rand.nextDouble();
        }

        for (int n = 0; n < parents[0].getWeights().length; n++) {
            weights[2][n] = a[0] * weights[0][n] + (1. - a[0]) * weights[1][n];
            weights[3][n] = a[1] * weights[0][n] + (1. - a[1]) * weights[1][n];
        }

        for (int n = 0; n < 2; n++) {
            offspring[n] = new Chromosome(parents[n].getId(), weights[n + 2]);
            offspring[n].updateError(inputValues, resultValue);
            offspring[n].updateFitness(maxError);
        }
    }

    private void mutation() {
        Iterator<Chromosome> mpIterator = matingPool.iterator();
        Lista<Chromosome> mpList = new Lista<Chromosome>();
        double[] wAux;
        Random rand = new Random();
        double alfa;

        while (mpIterator.hasNext()) {
            mpList.insertarPrincipio(mpIterator.next());
        }
        for (int i = 1; i <= mpList.longitud(); i++) {
            wAux = mpList.elementoN(i).getWeights();
            alfa = rand.nextDouble();
            if (rand.nextBoolean()) {
                alfa /= 20.;
            } else {
                alfa /= -20.;
            }
            for (int j = 0; j < wAux.length; j++) {
                wAux[j] += alfa * wAux[j];
            }
        }
    }

    private void updateConnections() {
        weight = matingPool.last();
        System.arraycopy(weight.getWeights(), 0, inputWeights, 0, inputWeights.length);
        for (int n = 0; n < inputConnections.longitud(); n++) {
            inputConnections.elementoN(n + 1).setWeight(inputWeights[n]);
        }
    }
}
