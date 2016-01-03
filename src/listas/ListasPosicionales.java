/*
 * Created on 02-dic-2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package listas;

/**
 * @author jgiralt
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
//Para que todos tengamos estas operaciones como mínimo
public interface ListasPosicionales<Elemento> {

    public boolean esVacia();

    public Elemento primero();

    public ListasPosicionales<Elemento> resto();

    public void insertarPrincipio(Elemento ob);

    public void eliminaPrimero();

    public void elimina(Elemento e);

    public int longitud();

    public Elemento ultimo();

    public Elemento elementoN(int n);

    public boolean pertenece(Elemento ob);

    public int posicion(Elemento ob);

    public void insertarFinal(Elemento ob);

    public void insertarN(Elemento ob, int n);

    public void eliminaUltimo();

    public void eliminaN(int n);

    public void modificaN(Elemento ob, int n);

    public void modificaObjeto(Elemento actual, Elemento nuevo);

    public void modificaPrimero(Elemento ob);
}
