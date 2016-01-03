/*
 * Grafo.java
 *
 * Created on 19 de abril de 2006, 17:21
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */
package grafos08;

import listas.*;

/**
 * @author jgiralt, clacave
 *
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class Grafo<V extends Comparable<V>> {
    // Clase que representa grafos
    // V representa los vértices

    private boolean dirigido; // para que puedan acceder a él sus herederas

    public Grafo() {
        dirigido = false;
    }

    public Grafo(boolean b) {
        dirigido = b;
    }

    public boolean dirigido() {
        return dirigido;
    }

    public boolean esVacio() {
        return vertices().esVacia();
    }

    public abstract boolean esValorado();

    public abstract void insertarVertice(V v);

    public abstract void insertarArco(Arco<V> a);

    public void insertarArco(V origen, V destino) {
        insertarArco(new Arco<V>(origen, destino));
    }

    public boolean perteneceVertice(V v) {
        return vertices().pertenece(v);
    }

    public boolean perteneceArco(Arco<V> a) {
        return perteneceArco(a.origen(), a.destino());
    }

    public abstract boolean perteneceArco(V origen, V destino);

    public abstract Lista<V> adyacentes(V v);

    public abstract Lista<V> incidentes(V v);

    public abstract Lista<V> vertices();

    public abstract Lista<Arco<V>> arcos();

    public int numVertices() {
        return vertices().longitud();
    }

    public int numArcos() {
        return arcos().longitud();
    }

    @Override
    public String toString() {
        return "vertices..." + vertices().toString() + "\n arcos..." + arcos().toString();
    }

    public abstract void eliminaVertice(V v);

    public abstract void eliminaArco(Arco<V> a);

    public abstract void eliminaArco(V origen, V destino);

    public Lista<V> recorrido() {
        Lista<V> visitados = new Lista<V>();
        for (int n = 1; n <= vertices().longitud(); n++) {
            if (!visitados.pertenece(vertices().elementoN(n))) {
                rep(visitados, vertices().elementoN(n));
            }
        }
        return visitados;
    }

    private void rep(Lista<V> visitados, V v) {
        visitados.insertarFinal(v);
        for (int n = 1; n <= adyacentes(v).longitud(); n++) {
            if (!visitados.pertenece(adyacentes(v).elementoN(n))) {
                rep(visitados, adyacentes(v).elementoN(n));
            }
        }
    }
}
