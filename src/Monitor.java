import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

/**
 * Clase Monitor que controla la ejecución de una red de Petri. Implementa la
 * lógica para disparar
 * transiciones y manejar la exclusión mutua utilizando semáforos y
 * sincronización entre hilos.
 */
public class Monitor implements MonitorInterface {
    private final ReentrantLock mutex = new ReentrantLock(true);
    private static Monitor m; // Instancia unica del monitor
    private final HashMap<Integer, Condition> condiciones = new HashMap<>(); // Mapa de llaves para sincronización
    private RedDePetri redDePetri;
    private ArrayList<AlfaYBeta> alfaYBetas;
    private int transicionAdespertar; // -1 , se permiten todos

    /**
     * Constructor privado de la clase Monitor.
     * 
     * @throws IllegalArgumentException Si los parámetros son inválidos.
     */
    private Monitor(RedDePetri redDePetri) {
        if (redDePetri == null) {
            throw new IllegalArgumentException("La red de Petri no puede ser nula.");
        }
        this.redDePetri = redDePetri;
        this.alfaYBetas = redDePetri.getAlfayBeta();
        transicionAdespertar = -1;

    }

    /**
     * Obtiene la instancia única del monitor.
     *
     * @return Instancia del monitor.
     * @throws IllegalArgumentException Si la instancia no ha sido inicializada.
     */
    public static Monitor getInstance() { // Para usarse este metodo se tiene que haber creado ya el monitor
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
     * @param politica   Política para manejo de conflictos.
     * @return Instancia única del monitor.
     */
    public static Monitor getInstance(RedDePetri redDePetri) {
        if (m == null) {
            m = new Monitor(redDePetri);
        }
        return m;
    }

    /**
     * Obtiene o crea la llave asociada a una transición.
     *
     * @param transition Identificador de la transición.
     * @return Objeto llave asociada a la transición.
     */
    private Condition getCondition(int transition) { // automatiza la creacion de llaves (una para cada transicion)
                                                     // también se
        // usa para obtener llave específica
        if (!condiciones.containsKey(transition)) {
            condiciones.put(transition, mutex.newCondition());
        }
        return condiciones.get(transition);
    }

    // MÉTODO PRINCIPAL: fireTransition
    public boolean fireTransition(int t) {

        if (redDePetri.isTermino()) {
            return false;
        }
        // Se toma el mutex
        mutex.lock();
        try {

            outer: while (true) {

                // Se verifica si es una transicion temporal
                switch (alfaYBetas.get(t).verificar()) {

                    case ALFA -> {
                        long inicio = alfaYBetas.get(t).getInicio();
                        long transcurrido = System.currentTimeMillis() - inicio;
                        long faltante = alfaYBetas.get(t).getAlfa() - transcurrido;

                        if (faltante < 1) {
                            faltante = 1;
                        }

                        getCondition(t).await(faltante, TimeUnit.MILLISECONDS);

                        continue; // volver a verificar alfa/beta
                        // luego de esperar, vuelve a verificar alfa/beta/sensibilizado
                    }

                    case BETA -> {
                        break outer; // NO bloquea → continúa
                    }

                    case OK -> {
                        break outer; // sale del while
                    }
                }
            }

            while (!politicaAdmite(t) || !redDePetri.dispararTransicion(t)) {
                
                    if (redDePetri.isTermino()) {
                    notificarATodos();
                    transicionAdespertar = -1;
                    return false;
                }
                getCondition(t).await();
            }

            actualizarAlfaYBeta(t);

            // Actualizar quién puede seguir
            despertarHilos();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            mutex.unlock();
        }
        return true;
    }

    private void actualizarAlfaYBeta(int transicionDisparada) {
        alfaYBetas.get(transicionDisparada).setInicio(0);
        for (int t = 0; redDePetri.getMatrizIncidencia()[0].length > t; t++) {
            if (redDePetri.sensibilizado(t) && alfaYBetas.get(t).getInicio() <= 0)
                alfaYBetas.get(t).iniciar();
        }
    }


    private boolean politicaAdmite(int t){
        return transicionAdespertar == -1 || transicionAdespertar == t;
    }

    private void notificarATodos() {
        for (Condition c : condiciones.values()) {
            c.signalAll();
        }
    }

    // despertar hilos según política
    private void despertarHilos() {
        //"and" entre cola de condicion y sensibilizadas
        List<Integer> candidatos = new ArrayList<>();
        int[] Vs = redDePetri.getSensibilizadas();
        int[] Vc = getHilosEnColas(); 

        for (int t=0 ; t < redDePetri.getCantidadDeTransiciones() ; t++ ) {
            if (Vs[t] > 0  && Vc[t] > 0) candidatos.add(t);
        }

        //Le pido a la red de petri que consulte por su politica
        transicionAdespertar = redDePetri.consultarPolitica(candidatos);
        if (transicionAdespertar >= 0) {
            notificar(transicionAdespertar);
            return;
        }
        for (int t : candidatos) {
            notificar(t);
        }
    }

    private void notificar(int t) {
        condiciones.get(t).signal();
    }

    private int[] getHilosEnColas() {
        int cantidadTransiciones = redDePetri.getCantidadDeTransiciones(); // Cantidad de transiciones
        // Vector que nos dice que transiciones tienen hilos esperando
        int[] vectorEsperando = new int[cantidadTransiciones];
        for (int t = 0; t < cantidadTransiciones; t++) {
            Condition c = condiciones.get(t); // Obtenemos la variable de conidicion
            if (c != null) {
                // nos dice cuantos hilos estan esperando en una condicion
                vectorEsperando[t] = mutex.getWaitQueueLength(c);
            } else {
                vectorEsperando[t] = 0;
            }
        }
        return vectorEsperando;
    }
}
