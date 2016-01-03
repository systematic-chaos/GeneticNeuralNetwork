package listas;

import java.util.Arrays;

//ListasRecursivas
public class Lista<Elemento extends Comparable<Elemento>> implements Cloneable,
		ListasPosicionales<Elemento> {

	private Elemento dato;

	private Lista<Elemento> resto;

	public Lista() {
		dato = null;
		resto = null;
	}

	public Lista(Elemento ob, Lista<Elemento> l) {
		this();
		if (ob != null) {
			dato = ob;
			resto = l;
		}
	}

	public Lista(Elemento ob) {
		this(ob, new Lista<Elemento>());// dato=ob; resto=new Lista();
	}

	public boolean esVacia() {
		return dato == null;
	}

	public Elemento primero() {
		if (esVacia()) {
			return null;
		} else {
			return dato;
		}
	}

	protected void setPrimero(Elemento ob) {
		// La utilzaré de forma auxiliar.Interesa no controlar si esVacia().
		// Para eso se hace protected
		dato = ob;// no se controla si esVacia() porque quiero poder hacer esto
	}

	protected void setResto(Lista<Elemento> l) {
		// la utilizaré de forma auxiliar. No interesa ver si esVacia()
		resto = l;
	}

	public Lista<Elemento> resto() {// PROTECTED?
		if (esVacia()) {
			return this;// return new Lista<Elemento>();
		} else {
			return resto;
		}
	}

	public void insertarPrincipio(Elemento ob) {
		if (esVacia()) {
			setResto(new Lista<Elemento>());// resto=new Lista();
		} else {
			setResto(new Lista<Elemento>(primero(), resto()));
		}
		setPrimero(ob);// dato=ob;
	}

	public void eliminaPrimero() {
		if (!esVacia()) {
			setPrimero(resto().primero());
			setResto(resto().resto());
		}
	}

	public void elimina(Elemento ob) {
		if (!esVacia()) {
			if (primero().equals(ob)) {
				eliminaPrimero();
			} else {
				resto().elimina(ob);
			}
		}
	}

	@Override
	public Lista<Elemento> clone() throws CloneNotSupportedException {
		Lista<Elemento> copia = new Lista<Elemento>();
		if (!esVacia()) {
			copia = resto().clone();
			copia.insertarPrincipio(primero());
		}
		return copia;
	}

	@Override
	public String toString() {
		String s = " ";
		if (!esVacia()) {
			s = s + primero().toString() + resto().toString();
		}
		return s;
	}

	@Override
	public boolean equals(Object lista) {
		boolean iguales = false;
		if (lista instanceof Lista) {
			@SuppressWarnings("unchecked")
			Lista<Elemento> L = (Lista<Elemento>) lista;
			if ((esVacia() && L.esVacia()) || this == L) {
				iguales = true;
			} else if ((!esVacia() && L.esVacia())
					|| (esVacia() && !L.esVacia())) {
				iguales = false;
			} else {
				iguales = primero().equals(L.primero())
						&& resto().equals(L.resto());
			}
		}
		return iguales;
	}

	public Object[] toArray() {
		Object[] array = new Object[longitud()];
		for (int n = 0; n < array.length; n++) {
			array[n] = elementoN(n + 1);
		}
		return array;
	}

	@SuppressWarnings("unchecked")
	public <T> T[] toArray(T[] a) {
		Object[] data = toArray();
		if (a.length < data.length)
			// Make a new array of a's runtime Type, but my contents
			return (T[]) Arrays.copyOf(data, data.length, a.getClass());
		System.arraycopy(data, 0, a, 0, data.length);
		if (a.length > data.length)
			a[data.length] = null;
		return a;
	}

	// OPERACIONES POSICIONALES
	public int longitud() {
		int n = 0;
		if (!esVacia()) {
			n = 1 + resto().longitud();
		}
		return n;
	}

	public Elemento ultimo() throws listaVaciaException {
		if (esVacia()) {
			return null;
		} // if (esVacia()) throw new listaVaciaException();
		else if (resto().esVacia()) {
			return primero();
		} else {
			return resto().ultimo();
		}
	}

	public Elemento elementoN(int n) {
		// if (esVacia()||n<1) return null;
		if (esVacia() || n < 1) {
			throw new listaVaciaException("la lista no tiene tantos elementos");
		} else if (n == 1) {
			return primero();
		} else {
			return resto().elementoN(n - 1);
		}
	}

	public boolean pertenece(Elemento ob) {
		return posicion(ob) > 0;
	}

	public int posicion(Elemento ob) {// uno iterartivo para variar
		int p = 1;
		Lista<Elemento> aux = this;
		while (!aux.esVacia() && !ob.equals(aux.primero())) {
			p++;
			aux = aux.resto();
		}
		if (aux.esVacia()) {
			p = 0;
		}
		return p;
	}

	public void insertarFinal(Elemento ob) {
		if (esVacia()) {
			insertarPrincipio(ob);
		} else {
			resto().insertarFinal(ob);
		}
	}

	public void insertarN(Elemento ob, int n) {
		if (n > 0 && !esVacia()) {
			if (n == 1) {
				insertarPrincipio(ob);
			} else {
				resto().insertarN(ob, n - 1);
			}
		}
	}

	public void concatenar(Lista<Elemento> l) {
		if (!l.esVacia() && l != this) {
			if (!pertenece(l.primero())) {
				insertarFinal(l.primero());
			}
			concatenar(l.resto());
		}
	}

	public void eliminaUltimo() {
		if (!esVacia()) {
			if (resto().esVacia()) {
				eliminaPrimero();
			} else {
				resto().eliminaUltimo();
			}
		}
	}

	public void eliminaN(int n) {
		if (!esVacia() && n > 0) {
			if (n == 1) {
				eliminaPrimero();
			} else {
				resto().eliminaN(n - 1);
			}
		}
	}

	public void modificaN(Elemento ob, int n) {// cambia el elemento n por el ob
		insertarN(ob, n);
		eliminaN(n + 1);
	}

	public void modificaPrimero(Elemento ob) {
		modificaN(ob, 1);
	}

	public void modificaObjeto(Elemento actual, Elemento nuevo) {
		if (pertenece(actual)) {
			modificaN(nuevo, posicion(actual));
		}
	}

	private void coloca(Elemento ob) {
		// para ordenar
		if (esVacia() || primero().compareTo(ob) > 0) {
			insertarPrincipio(ob);
		} else {
			resto().coloca(ob);
		}
	}

	public Lista<Elemento> ordena() {
		Lista<Elemento> ordenada = new Lista<Elemento>();
		if (!esVacia()) {
			ordenada = resto().ordena();
			ordenada.coloca(primero());
		}
		return ordenada;
	}
}// Lista

