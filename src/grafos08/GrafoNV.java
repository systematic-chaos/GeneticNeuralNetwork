/*
 * GrafoNV.java
 */
package grafos08;

import listas.*;

public class GrafoNV<X extends Comparable<X>> extends Grafo<X> {

    class Vertice<V extends Comparable<V>> implements Comparable<Vertice<V>>{

        private V valor;

        private Lista<Vertice<V>> adyacentes;//

        public Vertice(V v) {
            valor = v;
            adyacentes = new Lista<Vertice<V>>();
        }

        public V valor() {
            return valor;
        }

        public Lista<Vertice<V>> adyacentes() {
            return adyacentes;
        }

        public void insAdyacente(Vertice<V> a) {
            if (!adyacentes.pertenece(a)) {
                adyacentes.insertarFinal(a);
            }
        }

        public void eliminaAdyacente(Vertice<V> a) {
            adyacentes.elimina(a);
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object o) {
            return o instanceof Vertice && valor.equals(((Vertice<V>) o).valor);
        }

        @Override
        public String toString() {
            return valor.toString();
        }

        @Override
        public int compareTo(GrafoNV<X>.Vertice<V> other) {
            return valor.compareTo(other.valor);
        }
    }// vertice

    private Lista<Vertice<X>> vertices;// lista de vertices

    public GrafoNV() {
        this(false);
    }

    public GrafoNV(boolean b) {
        super(b);
        vertices = new Lista<Vertice<X>>();
    }

    public boolean esValorado() {
        return false;
    }

    protected Lista<Vertice<X>> Vertices() {
        return vertices;
    }

    public Lista<X> vertices() {
        Lista<X> vert = new Lista<X>();
        Lista<Vertice<X>> aux = vertices;
        while (!aux.esVacia()) {
            vert.insertarFinal(aux.primero().valor());
            aux = aux.resto();
        }
        return vert;
    }

    public void insertarVertice(X v) {
        if (!perteneceVertice(v)) {
            vertices.insertarFinal(new Vertice<X>(v));
        }
    }

    protected Vertice<X> vertice(X v) { // para que lo puedan utilizar sus
                                        // herederas
        Vertice<X> vale = null;
        int pos = vertices().posicion(v);
        if (pos > 0) {
            vale = vertices.elementoN(pos);
        }
        return vale;
    }

    public Lista<Arco<X>> arcos() {
        Lista<Arco<X>> arcos = new Lista<Arco<X>>();
        Lista<Vertice<X>> verts = vertices;
        while (!verts.esVacia()) {
            Vertice<X> origen = verts.primero();
            Lista<Vertice<X>> adyorig = origen.adyacentes();
            while (!adyorig.esVacia()) {
                Vertice<X> destino = adyorig.primero();
                Arco<X> arco = new Arco<X>(origen.valor(), destino.valor());
                arcos.insertarFinal(arco);
                adyorig = adyorig.resto();
            }
            verts = verts.resto();
        }
        return arcos;
    }

    public boolean perteneceArco(X or, X dest) {
        return arcos().pertenece(new Arco<X>(or, dest));
    }

    public void insertarArco(X origen, X destino) {
        Vertice<X> orig = vertice(origen);
        Vertice<X> dest = vertice(destino);
        if (orig != null && dest != null) {
            if (!orig.adyacentes().pertenece(dest)) {
                orig.insAdyacente(dest);
                if (!dirigido()) {
                    dest.insAdyacente(orig);
                }
            }// if
        }// if
    }

    public void insertarArco(Arco<X> a) {
        insertarArco(a.origen(), a.destino());
    }

    public Lista<X> adyacentes(X v) {
        Lista<X> ady = new Lista<X>();
        if (perteneceVertice(v)) {
            Lista<Vertice<X>> vAdy = vertice(v).adyacentes();
            while (!vAdy.esVacia()) {
                ady.insertarFinal(vAdy.primero().valor());
                vAdy = vAdy.resto();
            }
        }
        return ady;
    }

    public Lista<X> incidentes(X v) {
        Lista<X> incidentes = new Lista<X>();
        Vertice<X> aux = vertice(v);
        if (aux != null) {
            Lista<Arco<X>> arc = arcos();
            while (!arc.esVacia()) {
                Arco<X> a = arc.primero();
                if (a.destino().equals(v)) {
                    incidentes.insertarFinal(a.origen());
                }
                arc = arc.resto();
            }
        }
        return incidentes;
    }// incidentes

    public void eliminaArco(Arco<X> a) {
        Vertice<X> auxO = vertice(a.origen());
        Vertice<X> auxD = vertice(a.destino());
        if (auxO != null && auxD != null) {
            auxO.eliminaAdyacente(auxD);
            if (!dirigido()) {
                auxD.eliminaAdyacente(auxO);
            }
        }
    }

    public void eliminaArco(X origen, X destino) {
        eliminaArco(new Arco<X>(origen, destino));
    }

    public void eliminaVertice(X v) {
        Vertice<X> vl = vertice(v);
        Lista<Vertice<X>> verts = vertices;
        while (!verts.esVacia()) {
            Vertice<X> primero = verts.primero();
            primero.eliminaAdyacente(vl);
            verts = verts.resto();
        }
        vertices.elimina(vl);
    }
}// grafoNV

