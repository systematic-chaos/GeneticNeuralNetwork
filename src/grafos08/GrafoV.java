/*
 * GrafoV.java
 */
package grafos08;

import listas.*;

/**
 *
 * @author jgiralt, clacave
 */
public class GrafoV<X extends Comparable<X>, C> extends GrafoNV<X> {

    @SuppressWarnings("hiding")
    class adyacente<X extends Comparable<X>, C> extends GrafoNV<X>.Vertice<X> {

        private C peso;

        public adyacente(X a, C p) {
            super(a);
            peso = p;
        }

        public adyacente(GrafoNV<X>.Vertice<X> a, C p) {
            super(a.valor());
            peso = p;
        }

        public C getPeso() {
            return peso;
        }

        public void setPeso(C peso) {
            this.peso = peso;
        }

        @Override
        public String toString() {
            return "[" + super.toString() + ": " + peso.toString() + "]";
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object obj) {
            return obj instanceof Vertice && valor().equals(((Vertice<X>) obj).valor());
        }
    }

    public GrafoV() {
        this(false);
    }

    public GrafoV(boolean b) {
        super(b);
    }

    @Override
    public boolean esValorado() {
        return true;
    }

    public void insertarArco(X origen, X destino, C peso) {
        GrafoNV<X>.Vertice<X> orig = vertice(origen);
        GrafoNV<X>.Vertice<X> dest = vertice(destino);
        adyacente<X, C> adyorig = new adyacente<X, C>(dest, peso);
        if (orig != null && dest != null) {
            if (!orig.adyacentes().pertenece(adyorig)) {
                orig.insAdyacente(adyorig);
                if (!dirigido()) {
                    dest.insAdyacente(new adyacente<X, C>(orig, peso));
                }
            }// if
        }// if
    }

    public void insertarArco(ArcoV<X, C> arco) {
        insertarArco(arco.origen(), arco.destino(), arco.peso());
    }

    @Override
    public void insertarArco(X origen, X destino) {
        insertarArco(origen, destino, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Lista<Arco<X>> arcos() {
        Lista<Arco<X>> arcos = new Lista<Arco<X>>();
        Lista<Vertice<X>> verts = Vertices();
        while (!verts.esVacia()) {
            Vertice<X> origen = verts.primero();
            Lista<Vertice<X>> adyorig = origen.adyacentes();
            while (!adyorig.esVacia()) {
                Vertice<X> destino = adyorig.primero();
                C pesodest = ((adyacente<X, C>) adyorig.primero()).getPeso();
                ArcoV<X, C> arco = new ArcoV<X, C>(origen.valor(), destino.valor(), pesodest);
                arcos.insertarFinal(arco);
                adyorig = adyorig.resto();
            }
            verts = verts.resto();
        }
        return arcos;
    }

    // Cambiar peso a un arco

    @SuppressWarnings("unchecked")
    public void setPeso(X origen, X destino, C p) {
        GrafoNV<X>.Vertice<X> orig = vertice(origen);
        GrafoNV<X>.Vertice<X> dest = vertice(destino);
        if (orig != null && dest != null) {
            int pos = orig.adyacentes().posicion(dest);
            if (pos > 0) {
                ((adyacente<X, C>) orig.adyacentes().elementoN(pos)).setPeso(p);
                if (!dirigido()) {
                    ((adyacente<X, C>) dest.adyacentes()
                            .elementoN(dest.adyacentes().posicion(orig))).setPeso(p);
                }
            }// if
        }// if
    }

    public void setPeso(Arco<X> a, C p) {
        setPeso(a.origen(), a.destino(), p);
    }

    @SuppressWarnings("unchecked")
    public C getPeso(X origen, X destino) {
        GrafoNV<X>.Vertice<X> orig = vertice(origen);
        GrafoNV<X>.Vertice<X> dest = vertice(destino);
        C peso = null;
        if (orig != null && dest != null) {
            int pos = orig.adyacentes().posicion(dest);
            if (pos > 0) {
                peso = ((adyacente<X, C>) orig.adyacentes().elementoN(pos)).getPeso();
            }
        }// if
        return peso;
    }

    public C getPeso(Arco<X> a) {
        return getPeso(a.origen(), a.destino());
    }
    // ver peso de un arco
}
