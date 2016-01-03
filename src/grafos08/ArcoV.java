/*
 * Arco.java
 */
package grafos08;

/**
 *
 * @author jgiralt, clacave
 */
public class ArcoV<X, C> extends Arco<X> {//Arcos valorados

    private C peso = null;

    public ArcoV(X or, X dest, C caracteristicas) {
        super(or, dest);
        peso = caracteristicas;
    }

    public ArcoV(X or, X dest) {
        this(or, dest, null);
    }

    @Override
    public String toString() {
        String s = "(" + super.origen().toString() + "," + super.destino().toString();
        if (peso != null) {
            s = s + "<" + peso + ">";
        }
        s = s + ")";
        return s;
    }

    public C peso() {
        return peso;
    }

    public void nuevoPeso(C p) {
        peso = p;
    }
}//arco
