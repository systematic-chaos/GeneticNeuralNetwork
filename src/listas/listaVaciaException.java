/*
 * Created on 04-dic-2003
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package listas;

/**
 * @author jgiralt
 *
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class listaVaciaException extends RuntimeException {

    public listaVaciaException() {
        super("Error. La lista está vacía");
    }

    public listaVaciaException(String s) {
        super(s);
    }

    private static final long serialVersionUID = 8155666463241719342L;
}
