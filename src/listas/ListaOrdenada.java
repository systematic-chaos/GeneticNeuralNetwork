package listas;

public class ListaOrdenada<Elemento extends Comparable<Elemento>> extends Lista<Elemento> {

    public ListaOrdenada() {
        super();
    }

    public ListaOrdenada(Elemento ob, ListaOrdenada<Elemento> lo) {
        super(ob, lo);// ESTO SOLO ESTA MAL, no lo ordena
        eliminaPrimero();// elimina ob
        insertarOrdenado(ob);// ahora lo coloca bien
    }

    public ListaOrdenada(Elemento ob) {
        this(ob, new ListaOrdenada<Elemento>());
    }

    @Override
    public ListaOrdenada<Elemento> resto() {
        // me ahorro los cambios de tipo
        if (esVacia()) {
            return this;// new ListaOrdenada<Elemento>();
        } else {
            return (ListaOrdenada<Elemento>) super.resto();
        }
    }

    @Override
    public void insertarPrincipio(Elemento ob) {
        insertarOrdenado(ob);
    }

    public void insertarOrdenado(Elemento ob) throws ClassCastException {
        if (esVacia()) {
            super.setPrimero(ob);
            super.setResto(new ListaOrdenada<Elemento>());
        } else if (primero().compareTo(ob) > 0) {// no hace falta el Comparable
                                                 // porque Elemento hereda de
                                                 // Comparable
            super.setResto(new ListaOrdenada<Elemento>(primero(), resto()));
            super.setPrimero(ob);// dato=ob;
        } else {
            resto().insertarOrdenado(ob);
        }
    }

    @Override
    public void insertarFinal(Elemento ob) {
        insertarOrdenado(ob);
    }

    @Override
    public void insertarN(Elemento ob, int n) {
        if (n <= longitud()) {
            insertarOrdenado(ob);
        }
    }

    @Override
    public void concatenar(Lista<Elemento> l) {
        if (!l.esVacia() && l != this) {
            insertarOrdenado(l.primero());
            concatenar(l.resto());
        }
    }

    @Override
    public void modificaN(Elemento ob, int n) {
        if (n <= longitud()) {
            eliminaN(n);
            insertarOrdenado(ob);
        }
    }

    @Override
    public void modificaPrimero(Elemento ob) {
        modificaN(ob, 1);
    }

    @Override
    public void modificaObjeto(Elemento actual, Elemento nuevo) {
        if (pertenece(actual)) {
            modificaN(nuevo, posicion(actual));
        }
    }

    @Override
    public ListaOrdenada<Elemento> clone() {
        ListaOrdenada<Elemento> copia = new ListaOrdenada<Elemento>();
        if (!esVacia()) {
            copia = resto().clone();
            copia.insertarOrdenado(primero());
        }
        return copia;
    }

    @Override
    public boolean pertenece(Elemento ob) {// mejora el de lista
        return posicion(ob) > 0;
    }

    @Override
    public int posicion(Elemento ob) {// mejora el de lista
        int p = 1;
        ListaOrdenada<Elemento> aux = this;
        while (!aux.esVacia() && (aux.primero()).compareTo(ob) < 0) {
            p++;
            aux = aux.resto();
        }
        // if (aux.esVacia()||(aux.primero()).compareTo(ob)!=0) p=0;
        if (aux.esVacia() || !aux.primero().equals(ob)) {
            p = 0;
        }
        return p;
    }

    @Override
    public void elimina(Elemento ob) {
        int pos = posicion(ob);
        if (pos > 0) {
            eliminaN(pos);
        }
    }
}// ListaOrdenada
