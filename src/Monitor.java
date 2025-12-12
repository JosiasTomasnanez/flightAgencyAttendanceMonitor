import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Clase Monitor que controla la ejecución de una red de Petri. Implementa la lógica para disparar
 * transiciones y manejar la exclusión mutua utilizando semáforos y sincronización entre hilos.
 */
public class Monitor implements MonitorInterface {
  private static Monitor m; // Instancia unica del monitor
  private static final Semaphore mutex = new Semaphore(1); // Semaforo para exclusion mutua
  private final HashMap<Integer, Object> llaves = new HashMap<>(); // Mapa de llaves para sincronización
  private final int[][] matrizIncidencia; // Matriz de incidencia de la red de Petri
  private int[] marcado; // Marcado de la red de Petri
  public Politica politica; // Politica para manejo de conflictos entre transiciones
  private String secuencia = ""; // Secuencia de transiciones disparadas
  private boolean termino = false; // comprobar si todos los clientes terminaron
  private final ArrayList<AlfaYBeta> alfaYBetas;
  private String betaErrors = "";
  // simula la transicion 11, llevando registro, pero sin cambiar de estado (Se pueden borrar si se
  // cambia de red)
  private int simT11 = 0; // numero de transiciones T11 disparadas
  private final int maxClient; // Cantidad de clientes por atender
  private final List<Integer> transicionesPermitidas = new ArrayList<>();
  /**
   * Constructor privado de la clase Monitor.
   *
   * @param marcado Marcado inicial de la red.
   * @param matrizIncidencia Matriz de incidencia de la red.
   * @param politica Política para manejo de conflictos.
   * @throws IllegalArgumentException Si los parámetros son inválidos.
   */
  private Monitor(
      int[] marcado, int[][] matrizIncidencia, Politica politica, ArrayList<AlfaYBeta> alfaYBetas) {
    if (marcado.length != matrizIncidencia.length) {
      throw new IllegalArgumentException(
          "El tamaño del marcado debe coincidir con el número de filas de la matriz de"
              + " incidencia.");
    }
    if (matrizIncidencia.length == 0 || matrizIncidencia[0].length == 0) {
      throw new IllegalArgumentException(
          "La matriz de incidencia debe ser válida y contener al menos una transición.");
    }
    if (alfaYBetas.size() < matrizIncidencia[0].length) {
      throw new IllegalArgumentException(
          "La lista de alfas y betas debe contener la misma cantidad de"
              + "elementos que las transiciones en la red de petri");
    }
    if (politica == null) {
      throw new IllegalArgumentException("La política no puede ser nula.");
    }
    for (int ficha : marcado) {
      if (ficha < 0) {
        throw new IllegalArgumentException(
            "El marcado inicial no puede contener valores negativos.");
      }
    }
    this.alfaYBetas = alfaYBetas;
    this.marcado = marcado;
    this.matrizIncidencia = matrizIncidencia;
    this.politica = politica;
    {
      maxClient = marcado[0];
    } // variable para la simulación de clientes
  }

  /**
   * Obtiene la instancia única del monitor.
   *
   * @return Instancia del monitor.
   * @throws IllegalArgumentException Si la instancia no ha sido inicializada.
   */
  public static Monitor
      getInstance() { // Para usarse este metodo se tiene que haber creado ya el monitor
    if (m == null) {
      throw new IllegalArgumentException(
          "Para el uso de este metodo se debe crear la instancia mandando los parametros"
              + " requeridos");
    }
    return m;
  }

  /**
   * Crea o devuelve la instancia única del monitor.
   *
   * @param marcado Marcado inicial de la red.
   * @param matrizIncidencia Matriz de incidencia de la red.
   * @param politica Política para manejo de conflictos.
   * @return Instancia única del monitor.
   */
  public static Monitor getInstance(
      int[] marcado, int[][] matrizIncidencia, Politica politica, ArrayList<AlfaYBeta> alfaYBetas) {
    if (m == null) {
      m = new Monitor(marcado, matrizIncidencia, politica, alfaYBetas);
    }
    return m;
  }

  /**
   * Obtiene o crea la llave asociada a una transición.
   *
   * @param transition Identificador de la transición.
   * @return Objeto llave asociada a la transición.
   */
  private Object getLlave(int transition) { // automatiza la creacion de llaves (una para cada transicion) también se
    // usa para obtener llave específica
    if (!llaves.containsKey(transition)) {
      llaves.put(transition, new Object());
    }
    return llaves.get(transition);
  }

  /**
   * Devuelve el marcado actual de la red.
   *
   * @return Arreglo con el marcado actual.
   */
  public int[] getMarcado() {
    return marcado;
  }

  @Override
  public String getSecuencia() {
    return secuencia;
  }

  @Override
  public boolean isFinish() {
    return termino;
  }

  public String getBetaErrors() {
    return betaErrors;
  }

  /**
   * Devuelve la matriz de incidencia de la red.
   *
   * @return Matriz de incidencia.
   */
  public int[][] getMatrizIncidencia() {
    return matrizIncidencia;
  }

    // --------------------------------------------------------
    // 🔥  MÉTODO PRINCIPAL: fireTransition
    // --------------------------------------------------------
    public boolean fireTransition(int t) {

        if (termino) return false;
        try {
            mutex.acquire();

            while (true) {

            switch (alfaYBetas.get(t).verificar()) {

                case BLOQUEAR -> {
                    long inicio = alfaYBetas.get(t).getInicio();
                    long transcurrido = System.currentTimeMillis() - inicio;
                    long faltante = alfaYBetas.get(t).getAlfa() - transcurrido;

                if (faltante < 1) faltante = 1;

                    mutex.release();
                    synchronized (getLlave(t)) {
                        getLlave(t).wait(faltante);
                    }
                mutex.acquire();
                continue; // volver a verificar alfa/beta
                    // luego de esperar, vuelve a verificar alfa/beta/sensibilizado
                }

                case BETA -> {
                    betaErrors += "\nT" + t + " excedió β por " + alfaYBetas.get(t).getTiempoExcedido() + " ms";
                    // pero NO bloquea → continúa
                    break;
                }

                case OK -> {
                    break; // sale del while
                }
            }

            // Si no es BLOQUEAR, salimos del while
            if (alfaYBetas.get(t).verificar() != AlfaYBeta.Estado.BLOQUEAR)
                break;
        }


            while (!sensibilizado(t) || !politicaPermite(t)) {
                dormirHilo(t);
                mutex.acquire();  // re-adquirir tras despertar
            }

            

            // Disparo real
            disparar(t);
            // Actualizar quién puede seguir
            despertarHilos();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            mutex.release();
        }
        return true;
    }

    // --------------------------------------------------------
    // 🔍 Política: consulta si t pertenece al conjunto permitido
    // --------------------------------------------------------
    private boolean politicaPermite(int t) {
        if (transicionesPermitidas.isEmpty()) return true;
        return transicionesPermitidas.contains(t);
    }

    // --------------------------------------------------------
    // 😴 dormir hilo
    // --------------------------------------------------------
    private void dormirHilo(int t) throws InterruptedException {
        synchronized (getLlave(t)) {
            mutex.release();
            getLlave(t).wait();
        }
    }

    // --------------------------------------------------------
    // 🚀 disparar transición
    // --------------------------------------------------------
    private void disparar(int t) {

        secuencia += "T" + t;

        // Simulación T11 especial
        if (t == 11) {
            simT11++;
            PantallaCarga.incrementarPorcentaje(simT11, maxClient);

            if (simT11 == maxClient) {
                termino = true;
                PantallaCarga.cerrar();
                notificarATodos();
            }
            return;
        }

        // Regular
        alfaYBetas.get(t).setInicio(0);
        marcado = nuevoMarcado(t);
        comprobarTermino();
        actualizarAlfaYBeta();

    }

    private void actualizarAlfaYBeta() {
        for (int t = 0; matrizIncidencia[0].length > t; t++)  {
            if(sensibilizado(t) && alfaYBetas.get(t).getInicio() <= 0)
                alfaYBetas.get(t).iniciar();
        }
    }

    // --------------------------------------------------------
    // 🛑 comprobar fin
    // --------------------------------------------------------
    private void comprobarTermino() {
        for (int t = 0; t < matrizIncidencia[0].length; t++) {
            if (sensibilizado(t)) return;
        }
        termino = true;
        PantallaCarga.cerrar();
        notificarATodos();
    }

    private void notificarATodos() {
        for (Object o : llaves.values()) {
            synchronized (o) {
                o.notifyAll();
            }
        }
    }

    // --------------------------------------------------------
    // 🔔 despertar hilos según política
    // --------------------------------------------------------
   private void despertarHilos() {
    List<Integer> S = new ArrayList<>();

    // 1. obtener sensibilizadas
    for (int t = 0; t < matrizIncidencia[0].length; t++) {
        if (sensibilizado(t)) S.add(t);
    }

    transicionesPermitidas.clear();
    if (S.isEmpty()) return;

    // Primero: TODAS permitidas (después sacamos)
    transicionesPermitidas.addAll(S);

    // 2. detectar conflictos y resolverlos
    for (int i = 0; i < S.size(); i++) {
        for (int j = i + 1; j < S.size(); j++) {

            int t1 = S.get(i);
            int t2 = S.get(j);

            if (compartenLugaresDeEntrada(t1, t2)) {

                int ganadora = politica.llamadaApolitica(t1, t2, secuencia);

                int perdedora = (ganadora == t1) ? t2 : t1;

                //  quitar la perdedora de las permitidas
                transicionesPermitidas.remove((Integer) perdedora);
            }
        }
    }

    // 3. despertar a todas las sensibilizadas
    for (int t : S) {
        notificar(t);
    }
}


    private void notificar(int t) {
        synchronized (getLlave(t)) {
            getLlave(t).notifyAll();
        }
    }

    // --------------------------------------------------------
    // 🔧 utilidades de red de Petri
    // --------------------------------------------------------
    private boolean sensibilizado(int t) {
        int[] nuevo = nuevoMarcado(t);
        for (int x : nuevo) if (x < 0) return false;
        return true;
    }

    private boolean compartenLugaresDeEntrada(int t1, int t2) {
        for (int[] fila : matrizIncidencia) {
            if (fila[t1] < 0 && fila[t2] < 0) return true;
        }
        return false;
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
}
