/*
 * Arco.java
 */
package grafos08;

/**
 *
 * @author jgiralt, clacave
 */
public class Arco<X> implements Comparable<Arco<X>> {// Arcos no valorados

    private X origen, destino;

    public Arco(X or, X dest) {
        origen = or;
        destino = dest;
    }

    @Override
    public String toString() {
        String s = "(" + origen.toString() + "," + destino.toString() + ")";
        return s;
    }

    public X origen() {
        return origen;
    }

    public X destino() {
        return destino;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object a) {
        return a instanceof Arco && origen().equals(((Arco<X>) a).origen())
                && destino().equals(((Arco<X>) a).destino());
    }

    @Override
    public int compareTo(Arco<X> other) {
        return 0;
    }
}// arco
