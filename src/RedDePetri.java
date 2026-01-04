import java.util.ArrayList;
import java.util.List;

public class RedDePetri {
    private int[][] matrizIncidencia; // Matriz de incidencia de la red de Petri
    private int[] marcado; // Marcado de la red de Petri
    private String secuencia = ""; // Secuencia de transiciones disparadas

    // simula la transicion 11, llevando registro, pero sin cambiar de estado (Se
    // pueden borrar si se
    // cambia de red)
    private int simT11 = 0; // numero de transiciones T11 disparadas
    private int maxClient; // Cantidad de clientes por atender
    private boolean termino = false; // comprobar si todos los clientes terminaron

    public RedDePetri(int[][] matrizIncidencia, int[] marcadoInicial) {
        this.matrizIncidencia = matrizIncidencia;
        this.marcado = marcadoInicial;
        maxClient = this.marcado[0];
    }

    public int[] getMarcado() {
        return marcado;
    }

    public boolean isTermino() {
        return termino;
    }

    public int[][] getMatrizIncidencia() {
        return matrizIncidencia;
    }

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

    public boolean compartenLugaresDeEntrada(int t1, int t2) {
        for (int[] fila : matrizIncidencia) {
            if (fila[t1] < 0 && fila[t2] < 0)
                return true;
        }
        return false;
    }

    public List<Integer> getSensibilizadas() {
        List<Integer> sensibilizadas = new ArrayList<>();
        for (int t = 0; t < matrizIncidencia[0].length; t++) {
            if (sensibilizado(t)) {
                sensibilizadas.add(t);
            }
        }
        return sensibilizadas;
    }

    public boolean dispararTransicion(int t) {
        // Simulación T11 especial
        secuencia += "T" + t; // Asumiendo que las transiciones se numeran desde T1
        if (t == 11) {
            simT11++;
            PantallaCarga.incrementarPorcentaje(simT11, maxClient);

            if (simT11 == maxClient || comprobarTermino()) {
                termino = true;
                PantallaCarga.cerrar();
                return false;
            }
            return true;
        }
        // Verificar si la transición está sensibilizada
        if (!sensibilizado(t)) {
            throw new IllegalStateException("La transición " + t + " no está sensibilizada.");
        }
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
}