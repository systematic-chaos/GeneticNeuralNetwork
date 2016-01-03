/*
 * GrafoMatrizNV.java
 */
package grafos08;

/**
 *
 * @author jgiralt, clacave
 */
public class GrafoMatrizNV<X extends Comparable<X>> extends GrafoMatriz<X, Boolean> {

    public GrafoMatrizNV() {
        this(false);
    }

    public GrafoMatrizNV(boolean b) {
        super(b);
        arcos = new Boolean[max][max];
    }

    @Override
    public boolean esValorado() {
        return false;
    }

    @Override
    public void insertarArco(X origen, X destino) {
        if (perteneceVertice(origen) && perteneceVertice(destino)) {
            int posOr = fila(origen);
            int posDes = fila(destino);
            arcos[posOr][posDes] = true;
            if (!dirigido()) {
                arcos[posDes][posOr] = true;
            }
        }
    }

    @Override
    public void eliminaArco(X origen, X destino) {
        int posOr = fila(origen);
        int posDes = fila(destino);
        if (posOr != -1 && posDes != -1) {
            // lo ponemos a null en lugar de false para no tener que
            // sobreescribir los métodos arcos, perteneceArco, arco, etc.
            arcos[posOr][posDes] = null;
            if (!dirigido()) {
                arcos[posDes][posOr] = null;
            }
        }
    }
}
