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
  private RedDePetri redDePetri;
  public Politica politica; // Politica para manejo de conflictos entre transiciones
  private ArrayList<AlfaYBeta> alfaYBetas;
  private String betaErrors = "";
  private Integer politicaAdmite = -1;
  
  /**
   * Constructor privado de la clase Monitor.
   *
   * @param marcado Marcado inicial de la red.
   * @param matrizIncidencia Matriz de incidencia de la red.
   * @param politica Política para manejo de conflictos.
   * @throws IllegalArgumentException Si los parámetros son inválidos.
   */
  private Monitor(RedDePetri redDePetri, Politica politica, ArrayList<AlfaYBeta> alfaYBetas) {
    if (redDePetri == null) {
      throw new IllegalArgumentException("La red de Petri no puede ser nula.");
    }
    if (politica == null) {
      throw new IllegalArgumentException("La política no puede ser nula.");
    }
     if (alfaYBetas.size() < redDePetri.getMatrizIncidencia()[0].length) {
      throw new IllegalArgumentException(
          "La lista de alfas y betas debe contener la misma cantidad de"
              + "elementos que las transiciones en la red de petri");
    }
    this.alfaYBetas = alfaYBetas;
    this.redDePetri = redDePetri;
    this.politica = politica;
    
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
   * @param redDePetri Red de Petri a monitorear.
   * @param politica Política para manejo de conflictos.
   * @return Instancia única del monitor.
   */
  public static Monitor getInstance(RedDePetri redDePetri, Politica politica, ArrayList<AlfaYBeta> alfaYBetas) {
    if (m == null) {
      m = new Monitor(redDePetri, politica, alfaYBetas);
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


  public String getBetaErrors() {
    return betaErrors;
  }

    // --------------------------------------------------------
    // 🔥  MÉTODO PRINCIPAL: fireTransition
    // --------------------------------------------------------
    public boolean fireTransition(int t) {

        if (redDePetri.isTermino()) return false;
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

            while (redDePetri.sensibilizado(t) == false || !politicaPermite(t)) {
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
        if (politicaAdmite == -1) return true;
        return t == politicaAdmite;
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
        alfaYBetas.get(t).setInicio(0);
        if (!redDePetri.dispararTransicion(t)){
        notificarATodos();
        return; 
        }
        actualizarAlfaYBeta();

    }

    private void actualizarAlfaYBeta() {
        for (int t = 0; redDePetri.getMatrizIncidencia()[0].length > t; t++)  {
            if(redDePetri.sensibilizado(t) && alfaYBetas.get(t).getInicio() <= 0)
                alfaYBetas.get(t).iniciar();
        }
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
    List<Integer> S = redDePetri.getSensibilizadas();
    for (int i = 0; i < S.size(); i++) {
        for (int j = i + 1; j < S.size(); j++) {

            int t1 = S.get(i);
            int t2 = S.get(j);

            if (redDePetri.compartenLugaresDeEntrada(t1, t2)) {

                notificar(politica.llamadaApolitica(t1, t2));
                return; // solo se admite una transicion
            }
        }
    }
    
    for (int t : S) {
        notificar(t);
    }
}


    private void notificar(int t) {
        synchronized (getLlave(t)) {
            getLlave(t).notifyAll();
        }
    }
   
}
