import java.util.ArrayList;
import java.util.HashMap;
//import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Clase Monitor que controla la ejecución de una red de Petri. Implementa la
 * lógica para disparar
 * transiciones y manejar la exclusión mutua utilizando semáforos y
 * sincronización entre hilos.
 */
public class Monitor implements MonitorInterface {
    private final ReentrantLock mutex = new ReentrantLock();
    private static Monitor m; // Instancia unica del monitor
    // private static final Semaphore mutex = new Semaphore(1); // Semaforo para
    // exclusion mutua
    private final HashMap<Integer, Object> llaves = new HashMap<>(); // Mapa de llaves para sincronización
    private RedDePetri redDePetri;
    private ArrayList<AlfaYBeta> alfaYBetas;

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
    private Object getLlave(int transition) { // automatiza la creacion de llaves (una para cada transicion) también se
        // usa para obtener llave específica
        if (!llaves.containsKey(transition)) {
            llaves.put(transition, new Object());
        }
        return llaves.get(transition);
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

                        mutex.unlock(); // Liberar el lock antes de esperar
                        try {
                            getLlave(t).wait(faltante);
                        } finally {
                            mutex.lock(); // Re-adquirir el lock después de esperar
                        }
                        continue; // volver a verificar alfa/beta
                        // luego de esperar, vuelve a verificar alfa/beta/sensibilizado
                    }

                    case BETA -> {
                        break outer; //  NO bloquea → continúa
                    }

                    case OK -> {
                        break outer; // sale del while
                    }
                }
            }

            while (!redDePetri.dispararTransicion(t)) {
                if (redDePetri.isTermino()) {
                    notificarATodos();
                    return false;
                }
                dormirHilo(t);
                mutex.lock();
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

    // dormir hilo en su cola de condicion
    private void dormirHilo(int t) throws InterruptedException {
        synchronized (getLlave(t)) {
            mutex.unlock();
            getLlave(t).wait();
        }
    }

    private void actualizarAlfaYBeta(int transicionDisparada) {
        alfaYBetas.get(transicionDisparada).setInicio(0);
        for (int t = 0; redDePetri.getMatrizIncidencia()[0].length > t; t++) {
            if (redDePetri.sensibilizado(t) && alfaYBetas.get(t).getInicio() <= 0)
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

    // despertar hilos según política
    private void despertarHilos() {
        int transicionAdespertar = redDePetri.verificarConflicto();
        if (transicionAdespertar > 0) {
            notificar(transicionAdespertar);
            return;
        }
        for (int t : redDePetri.getSensibilizadas()) {
            notificar(t);
        }
    }

    private void notificar(int t) {
        synchronized (getLlave(t)) {
            getLlave(t).notify();
        }
    }

}
