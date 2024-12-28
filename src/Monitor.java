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
  private final HashMap<Integer, Object> llaves =
      new HashMap<>(); // Mapa de llaves para sincronización
  private final int[][] matrizIncidencia; // Matriz de incidencia de la red de Petri
  private int[] marcado; // Marcado de la red de Petri
  public Politica politica; // Politica para manejo de conflictos entre transiciones
  private String secuencia = ""; // Secuencia de transiciones disparadas
  public boolean termino = false; // comprobar si todos los clientes terminaron

  // simula la transicion 11, llevando registro pero sin cambiar de estado (Se pueden borrar si se
  // cambia de red)
  private int simT11 = 0; // numero de transiciones T11 disparadas
  private final int maxClient; // Cantidad de clientes por atender

  /**
   * Constructor privado de la clase Monitor.
   *
   * @param marcado Marcado inicial de la red.
   * @param matrizIncidencia Matriz de incidencia de la red.
   * @param politica Política para manejo de conflictos.
   * @throws IllegalArgumentException Si los parámetros son inválidos.
   */
  private Monitor(int[] marcado, int[][] matrizIncidencia, Politica politica) {
    if (marcado.length != matrizIncidencia.length) {
      throw new IllegalArgumentException(
          "El tamaño del marcado debe coincidir con el número de filas de la matriz de"
              + " incidencia.");
    }
    if (matrizIncidencia.length == 0 || matrizIncidencia[0].length == 0) {
      throw new IllegalArgumentException(
          "La matriz de incidencia debe ser válida y contener al menos una transición.");
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
  public static Monitor getInstance(int[] marcado, int[][] matrizIncidencia, Politica politica) {
    if (m == null) {
      m = new Monitor(marcado, matrizIncidencia, politica);
    }
    return m;
  }

  /**
   * Obtiene o crea la llave asociada a una transición.
   *
   * @param transition Identificador de la transición.
   * @return Objeto llave asociado a la transición.
   */
  private Object getLlaves(
      int
          transition) { // automatiza la creacion de llaves (una para cada transicion) tambien se
                        // usa para obtener llave especifica
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

  /**
   * Devuelve la matriz de incidencia de la red.
   *
   * @return Matriz de incidencia.
   */
  public int[][] getMatrizIncidencia() {
    return matrizIncidencia;
  }

  @Override
  public boolean fireTransition(int transition) {
    while (!termino) {
      try {
        mutex.acquire(); // Toma el mutex
        if (!sensibilizado(transition)) { // Verifica si no esta sensibilizada
          dormirHilo(transition);
          continue;
        }
        disparar(transition); // Disparo de la transicion
        mutex.release(); // Devuelve el mutex
        return true;
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
    return false;
  }

  /**
   * Hace que un hilo espere hasta que su transición esté sensibilizada.
   *
   * @param transition Número de la transición.
   * @throws InterruptedException Si ocurre una interrupción mientras espera.
   */
  private void dormirHilo(int transition) throws InterruptedException {
    synchronized (getLlaves(transition)) {
      mutex.release();
      getLlaves(transition).wait();
    }
  }

  /**
   * Dispara una transición específica y realiza las acciones correspondientes.
   *
   * @param transition Número de la transición a disparar.
   */
  private void disparar(int transition) {
    secuencia += "T" + transition;
    {
      if (transition == 11) {
        simT11++;
        PantallaCarga.incrementarPorcentaje(simT11, maxClient);
        if (simT11 == maxClient) {
          termino = true;
          PantallaCarga.cerrar();
          for (Object o : llaves.values()) {
            synchronized (o) {
              o.notifyAll();
            }
          }
        }
        return;
      }
    } // Simulacion de disparo T11 y condicion de termino del programa
    marcado = nuevoMarcado(transition);
    despertarHilos(transition);
    comprobarTermino();
  }

  /** Comprueba si el programa debe terminar según el estado actual del marcado. */
  private void comprobarTermino() {
    for (int plaza = 0; plaza < marcado.length; plaza++) {
      if (sensibilizado(
          plaza)) { // Llamar al método que ya tienes para verificar si está sensibilizada
        return; // Salir del bucle si no es necesario continuar verificando
      }
    }
    termino = true;
    for (Object o : llaves.values()) {
      synchronized (o) {
        o.notifyAll();
      }
    }
  }

  /**
   * Despierta los hilos asociados a las transiciones sensibilizadas después de un disparo.
   *
   * @param transition Número de la transición disparada.
   */
  private void despertarHilos(int transition) {
    List<Integer> sensibilizadas = new ArrayList<>();
    for (int t = 0; t < matrizIncidencia[0].length; t++) {
      if (sensibilizado(t)) {
        sensibilizadas.add(t);
      }
    }

    List<Integer> indicesAEliminar = new ArrayList<>();

    for (int i = 0; i < sensibilizadas.size(); i++) {
      for (int j = i + 1; j < sensibilizadas.size(); j++) {
        int t1 = sensibilizadas.get(i);
        int t2 = sensibilizadas.get(j);
        if (compartenLugaresDeEntrada(t1, t2)) {

          Object llaveADespertar = getLlaves(politica.llamadaApolitica(t1, t2, getSecuencia()));
          synchronized (llaveADespertar) {
            llaveADespertar.notifyAll();
          }
          indicesAEliminar.add(i);
          indicesAEliminar.add(j);
        }
      }
    }
    sensibilizadas.removeAll(indicesAEliminar);
    for (int s : sensibilizadas) {
      synchronized (getLlaves(s)) {
        getLlaves(s).notifyAll();
      }
    }
  }

  /**
   * Verifica si dos transiciones comparten lugares de entrada.
   *
   * @param t1 Número de la primera transición.
   * @param t2 Número de la segunda transición.
   * @return {@code true} si las transiciones comparten lugares de entrada; de lo contrario, {@code
   *     false}.
   */
  private boolean compartenLugaresDeEntrada(int t1, int t2) {
    for (int[] ints : matrizIncidencia) {
      if (ints[t1] < 0 && ints[t2] < 0) {
        return true;
      }
    }
    return false;
  }

  /**
   * Calcula el nuevo marcado de la red después de disparar una transición.
   *
   * @param transition Número de la transición disparada.
   * @return Arreglo que representa el nuevo marcado.
   */
  private int[] nuevoMarcado(int transition) {
    int[] S = new int[matrizIncidencia[0].length]; // Vector de disparo
    S[transition] = 1;
    int[] resultado = new int[matrizIncidencia.length]; // Nuevo marcado

    for (int i = 0; i < matrizIncidencia.length; i++) {
      int suma = 0;
      for (int j = 0; j < matrizIncidencia[0].length; j++) {
        suma += matrizIncidencia[i][j] * S[j]; // Producto matricial
      }
      resultado[i] = suma;
    }
    for (int i = 0; i < resultado.length; i++) {
      resultado[i] += marcado[i];
    }
    return resultado;
  }

  /**
   * Verifica si una transición está sensibilizada según el marcado actual.
   *
   * @param transition Número de la transición a verificar.
   * @return {@code true} si la transición está sensibilizada; de lo contrario, {@code false}.
   */
  private boolean sensibilizado(int transition) {
    int[] aux = nuevoMarcado(transition);
    for (int i : aux) {
      if (i < 0) { // Numero negativo
        return false; // No esta sensibilizada
      }
    }
    return true;
  }
}
