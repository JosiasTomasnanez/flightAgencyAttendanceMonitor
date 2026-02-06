import java.util.ArrayList;
import java.util.List;

public class RedDePetri {

    private int[] marcado; // Marcado de la red de Petri
    private String secuencia = ""; // Secuencia de transiciones disparadas
    private ArrayList<AlfaYBeta> alfaybetas;
    private int[][] matrizIncidencia;
    // simula la transicion 11, llevando registro, pero sin cambiar de estado (Se
    // pueden borrar si se cambia de red)
    private int maxClient; // Cantidad de clientes por atender
    private boolean termino = false; // comprobar si todos los clientes terminaron
    private Politica politica;
    private int clientesSalientes =0;

    public RedDePetri(int[][] matrizIncidencia, int[] marcado, Politica politica, ArrayList<AlfaYBeta> alfaYbetas) {

        this.politica = politica;
        this.matrizIncidencia = matrizIncidencia;
        this.marcado = marcado;
        maxClient = this.marcado[0];
        this.alfaybetas = alfaYbetas;

    }

    public int getClientesSalientes(){
        return clientesSalientes;
    }
    public Politica getPolitica() {
        return politica;
    }

    public int[] getMarcado() {
        return marcado;
    }

    public ArrayList<AlfaYBeta> getAlfayBeta() {
        return alfaybetas;
    }

    public boolean isTermino() {
        return termino;
    }

    public int[][] getMatrizIncidencia() {
        return matrizIncidencia;
    }

    // Comprueba si no hay valores negativos en el nuevo estado
    public boolean sensibilizado(int t) {
        int[] nuevo = nuevoMarcado(t);
        for (int x : nuevo)
            if (x < 0)
                return false;
        return true;
    }

    private int[] nuevoMarcado(int t) {
        int[] S = new int[matrizIncidencia[0].length];
        S[t] = 1;

        int[] result = new int[matrizIncidencia.length];

        for (int i = 0; i < matrizIncidencia.length; i++) {
            int suma = 0;
            for (int j = 0; j < matrizIncidencia[0].length; j++) {
                suma += matrizIncidencia[i][j] * S[j];
            }
            result[i] = marcado[i] + suma;
        }

        return result;
    }


  // Vector de sensibilizado estructural (0 = no sensibilizada, 1 = sensibilizada)
    public int[] getSensibilizadas() {
        int[] sensibilizadas = new int[matrizIncidencia[0].length];

        for (int t = 0; t < matrizIncidencia[0].length; t++)
            sensibilizadas[t] = sensibilizado(t) ? 1 : 0;
        
        return sensibilizadas;
    }

    public int getCantidadDeTransiciones(){
        return matrizIncidencia[0].length;
    }


    public boolean dispararTransicion(int t) {
    // Verificar si la transición está sensibilizada
    if (!sensibilizado(t)) return false;
    
    if (t == 11) { // Simulación T11 especial
        clientesSalientes++;
        PantallaCarga.incrementarPorcentaje(maxClient);
    }
    // Transiciones normales
    secuencia += "T" + t; // registrar la transición
    marcado = nuevoMarcado(t);
    // Comprobar si terminamos
    if (comprobarTermino()) {
        termino = true;
        PantallaCarga.cerrar();
        return false;
    }
    return true;
    }


    public String getSecuencia() {
        return secuencia;
    }

    private boolean comprobarTermino() {
        for (int t = 0; t < matrizIncidencia[0].length; t++) {
            if (sensibilizado(t))
                return false;
        }
        return true;
    }

    public int consultarPolitica(List<Integer> candidatos) {
        //si no hay conflicto o no hay hilos candidatos, se manda -1 para que compitan por el mutex
        if (candidatos.isEmpty() || !compartenLugaresDeEntrada(candidatos))  return -1; // No hay candidatos activos

        return politica.llamadaApolitica(candidatos);
    }

private boolean compartenLugaresDeEntrada(List<Integer> candidatos) {
    for (int i = 0; i < candidatos.size(); i++) {
        for (int j = i + 1; j < candidatos.size(); j++) {
            if (compartenLugares(candidatos.get(i), candidatos.get(j))) return true;
        }
    }
    return false;
}

/**
 * Verifica si dos transiciones comparten al menos un lugar de entrada.
 */
private boolean compartenLugares(int t1, int t2) {
    for (int i = 0; i < matrizIncidencia.length; i++) {
        if (matrizIncidencia[i][t1] < 0 && matrizIncidencia[i][t2] < 0) return true;
    }
    return false;
}


}