
package main;
 
import java.util.Arrays;


public class Simbolo {
    private String tiposNumericos[]={"BYTE","WORD","ENTERO","DECIMAL","REAL"};
    private String tipo;//tupodelidentificador
    private String ident;//propionobredelidentificador
    private String valor;
    private String varconst;//guardarcomoconstanteovariable
 
    public Simbolo(String tipo, String ident, String valor, String varconst) {
        this.tipo = tipo;
        this.ident = ident;
        this.valor = valor;
        this.varconst = varconst;
    }
    // Constructor vacío (por defecto)
    public Simbolo() {
    }
 
    public String getTipo() {
        return tipo;
    }
 
    public String getIdent() {
        return ident;
    }
 
    public String getValor() {
        return valor;
    }
 
    public String getVarconst() {
        return varconst;
    }
 
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
 
    public void setIdent(String ident) {
        this.ident = ident;
    }
 
    public void setValor(String valor) {
        this.valor = valor;
    }
 
    public void setVarconst(String varconst) {
        this.varconst = varconst;
    }
    public boolean isTipoNumeric(){
        return Arrays.asList(tiposNumericos).contains(tipo.toUpperCase());
    }
    
}
