import java.util.ArrayList;
import java.util.List;

public class RedDePetri {

    private int[] marcado; // Marcado de la red de Petri
    private String secuencia = ""; // Secuencia de transiciones disparadas
    private ArrayList<AlfaYBeta> alfaybetas;
    private int[][] matrizIncidencia;
    // simula la transicion 11, llevando registro, pero sin cambiar de estado (Se
    // pueden borrar si se cambia de red)
    private int simT11 = 0; // numero de transiciones T11 disparadas
    private int maxClient; // Cantidad de clientes por atender
    private boolean termino = false; // comprobar si todos los clientes terminaron
    private Politica politica;
    private int tockensT11=0;
    private int auxT11=0;

    public RedDePetri(int[][] matrizIncidencia, int[] marcado, Politica politica, ArrayList<AlfaYBeta> alfaYbetas) {

        this.politica = politica;
        this.matrizIncidencia = matrizIncidencia;
        this.marcado = marcado;
        maxClient = this.marcado[0];
        this.alfaybetas = alfaYbetas;

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

    // Realiza el calculo del nuevo marcado en funcion de la ecuacion fundamental, con una sola transicion.
   private int[] nuevoMarcado(int t) {
    int[] result = new int[matrizIncidencia.length];
    for (int i = 0; i < matrizIncidencia.length; i++) {
        result[i] = marcado[i] + matrizIncidencia[i][t]; // usar solo la columna t debido a que es una unica transicion a la vez
    }
    return result;
}


    // Vector de sensibilizado estructural
   public int[] getSensibilizadas() {
    int[] temp = new int[matrizIncidencia[0].length];
    int count = 0;
    for (int t = 0; t < matrizIncidencia[0].length; t++) {
        if (sensibilizado(t)) {
            temp[count++] = t;
        }
    }
    int[] sensibilizadas = new int[count];
    System.arraycopy(temp, 0, sensibilizadas, 0, count);
    return sensibilizadas;
}


    public boolean dispararTransicion(int t) {
    // Verificar si la transición está sensibilizada
    if (!sensibilizado(t)) {
        return false;
    }
    if (t == 11) { // Simulación T11 especial
        // Calculamos cuántos tokens nuevos hay en la plaza 14 desde la última observación
        int diferencia = marcado[14] - auxT11;
        if (diferencia > 0) {
            tockensT11 += diferencia;
            auxT11 = marcado[14]; // actualizamos la referencia
        }
        // Si no hay tokens ficticios, no podemos "disparar"
        if (tockensT11 < 1) {
            return false;
        }
        // Disparamos T11 de manera simulada
        tockensT11--;
        simT11++;
        secuencia += "T" + t; // registrar la transición
        // Actualizar pantalla
        PantallaCarga.incrementarPorcentaje(simT11, maxClient);
        // Comprobar si terminamos
        if (simT11 == maxClient || comprobarTermino()) {
            termino = true;
            PantallaCarga.cerrar();
            return false;
        }
        return true;
    }
    // Transiciones normales
    secuencia += "T" + t; // registrar la transición
    marcado = nuevoMarcado(t);
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