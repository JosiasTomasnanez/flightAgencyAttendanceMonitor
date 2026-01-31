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

    // Realiza el calculo del nuevo marcado en funcion de la ecuacion fundamental
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

    // Vector de sensibilizado estructural
    public List<Integer> getSensibilizadas() {
        List<Integer> sensibilizadas = new ArrayList<>();
        for (int t = 0; t < matrizIncidencia[0].length; t++) {
            if (sensibilizado(t)) {
                sensibilizadas.add(t);
            }
        }
        // System.out.println("Transiciones sensibilizadas: " + sensibilizadas);
        return sensibilizadas;
    }

    public boolean dispararTransicion(int t) {
        // Simulación T11 especial
        if (t == 11) {
            simT11++;
            secuencia += "T" + t; // Asumiendo que las transiciones se numeran desde T1
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
            return false;
        }
        secuencia += "T" + t; // Asumiendo que las transiciones se numeran desde T1
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

    public int verificarConflicto() {

        List<Integer> S = getSensibilizadas();
        for (int i = 0; i < S.size(); i++) {
            for (int j = i + 1; j < S.size(); j++) {

                int t1 = S.get(i);
                int t2 = S.get(j);

                if (compartenLugaresDeEntrada(t1, t2)) {
                    return getPolitica().llamadaApolitica(t1, t2);
                }
            }
        }
        return -1;
    }
}