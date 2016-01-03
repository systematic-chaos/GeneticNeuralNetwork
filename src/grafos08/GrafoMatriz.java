/*
 * GrafoMatriz.java
 */
package grafos08;

import java.util.*;

import listas.*;

/**
 * @author jgiralt, clacave
 */
public class GrafoMatriz<X extends Comparable<X>, C> extends Grafo<X> {

    protected int max = 256;

    protected ArrayList<X> vertices;// matriz de vertices

    protected C[][] arcos;

    // matriz de adyacencia para los arcos con pesos C. Para los no valorados,
    // C es un Boolean

    public GrafoMatriz() {
        this(false);
    }

    @SuppressWarnings("unchecked")
    public GrafoMatriz(boolean dirigido) {
        super(dirigido);
        vertices = new ArrayList<X>(max);
        arcos = (C[][]) new Object[max][max];
    }

    public boolean esVacio() {
        return vertices.isEmpty();
    }

    public boolean esValorado() {
        return true;
    }

    protected int getMax() {
        return max;
    }

    protected int fila(X v) {
        int f = -1;
        if (vertices.contains(v)) {
            f = vertices.indexOf(v);
        }
        return f;
    }

    public Lista<X> vertices() {
        Lista<X> v = new Lista<X>();
        for (int n = 0; n < vertices.size(); n++) {
            v.insertarFinal(vertices.get(n));
        }
        return v;
    }

    public void insertarVertice(X v) {
        if (!vertices.contains(v) && vertices.size() < max) {
            vertices.add(v);
        }
    }

    public X vertice(X v) {// es el localiza
        X vet = null;
        if (fila(v) != -1) {
            vet = vertices.get(fila(v));
        }
        return vet;
    }

    public ArcoV<X, C> arco(X orig, X dest) {// es el localiza
        ArcoV<X, C> encontrado = null;
        int fo = fila(orig);
        int fd = fila(dest);
        if (fo != -1 && fd != -1)// si existen los vértices
        {
            if (arcos[fo][fd] != null) {
                encontrado = new ArcoV<X, C>(orig, dest, arcos[fo][fd]);
            }
        }
        return encontrado;
    }

    public boolean perteneceArco(X or, X dest) {
        return (fila(or) != -1 && fila(dest) != -1 && arcos[fila(or)][fila(dest)] != null);
    }

    public void insertarArco(X origen, X destino, C caracteristicas) {
        if (perteneceVertice(origen) && perteneceVertice(destino)) {
            arcos[fila(origen)][fila(destino)] = caracteristicas;
            if (!dirigido()) {
                arcos[fila(destino)][fila(origen)] = caracteristicas;
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void insertarArco(Arco<X> n) {
        if (n instanceof ArcoV) {
            insertarArco(n.origen(), n.destino(), ((ArcoV<X, C>) n).peso());
        } else {
            insertarArco(n.origen(), n.destino());
        }
    }

    public Lista<Arco<X>> arcos() {
        Lista<Arco<X>> listaRes = new Lista<Arco<X>>();
        for (int n = 0; n < vertices.size(); n++) {
            X v = vertices.get(n);
            for (int c = 0; c <= vertices.size(); c++) {
                if (arcos[fila(v)][c] != null) {
                    listaRes.insertarFinal(new ArcoV<X, C>(v, vertices.get(c),
                            arcos[fila(v)][fila(vertices.get(c))]));
                }
            }
        }
        return listaRes;
    }

    public Lista<X> adyacentes(X v) {
        Lista<X> ady = new Lista<X>();
        if (perteneceVertice(v)) {
            for (int c = 0; c <= vertices.size(); c++) {
                if (arcos[fila(v)][c] != null) {
                    ady.insertarFinal(vertices.get(c));
                }
            }
        }
        return ady;
    }

    public Lista<Arco<X>> arcosAdyacentes(X or) {
        Lista<Arco<X>> LA = new Lista<Arco<X>>();
        X v = vertice(or);
        if (v != null) {
            for (int c = 0; c <= vertices.size(); c++) {
                if (arcos[fila(v)][c] != null) {
                    LA.insertarFinal(new ArcoV<X, C>(v, vertices.get(c),
                            arcos[fila(v)][fila(vertices.get(c))]));
                }
            }
        }
        return LA;
    }

    public Lista<X> incidentes(X v) {
        Lista<X> incidentes = new Lista<X>();
        int posV = fila(v);
        if (posV != -1) {
            for (int c = 0; c <= vertices.size(); c++) {
                if (arcos[c][posV] != null) {
                    incidentes.insertarFinal(vertices.get(c));
                }
            }
        }
        return incidentes;
    }

    public Lista<Arco<X>> arcosIncidentes(X v) {
        Lista<Arco<X>> incidentes = new Lista<Arco<X>>();
        int posV = fila(v);
        if (posV != -1) {
            for (int c = 0; c <= vertices.size(); c++) {
                if (arcos[c][posV] != null) {
                    incidentes.insertarFinal(new ArcoV<X, C>(vertices.get(c), v, arcos[c][posV]));
                }
            }
        }
        return incidentes;
    }

    public void eliminaArco(X origen, X destino) {
        int posOr = fila(origen);
        int posDe = fila(destino);
        if (posOr != -1 && posDe != -1) {
            arcos[posOr][posDe] = null;
            if (!(dirigido())) {
                arcos[posDe][posOr] = null;
            }
        }
    }

    public void eliminaArco(Arco<X> a) {
        eliminaArco(a.origen(), a.destino());
    }

    public void eliminaVertice(X v) {
        int pos = fila(v);
        if (pos != -1) {
            quitarFilaCol(arcos, pos);
            vertices.remove(pos);
        }
    }

    private void quitarFilaCol(C[][] m, int p) {
        for (int f = p; f < max - 1; f++) {
            for (int c = 0; c < max; c++) {
                m[f][c] = m[f + 1][c];
            }
        }
        for (int col = p; col < max - 1; col++) {
            for (int fil = 0; fil < max; fil++) {
                m[fil][col] = m[fil][col + 1];
            }
        }
    }

    public void setPeso(X or, X dest, C p) {
        int f = fila(or);
        int c = fila(dest);
        if (f != -1 && c != -1) {
            arcos[f][c] = p;
            if (!dirigido()) {
                arcos[c][f] = p;
            }
        }
    }

    public C getPeso(X or, X dest) {
        C valor = null;
        int f = fila(or);
        int c = fila(dest);
        if (f != -1 && c != -1 && arcos[f][c] != null) {
            valor = arcos[f][c];
        }
        return valor;
    }

    // Cambiar peso a un arco
    public void setPeso(Arco<X> a, C p) {
        setPeso(a.origen(), a.destino(), p);
    }

    public C getPeso(Arco<X> a) {
        return getPeso(a.origen(), a.destino());
    }
    // ver peso de un arco
}// grafosListas

