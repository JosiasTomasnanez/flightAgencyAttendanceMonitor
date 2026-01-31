import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Clase que implementa la interfaz {@link ThreadFactory} para crear nuevos
 * hilos personalizados.
 * Cada hilo creado lleva un identificador único basado en un contador atómico y
 * se le asigna un
 * nombre que incluye información sobre el hilo y la tarea que ejecutará.
 * Además, mantiene un
 * registro de los hilos creados y su fecha de creación.
 */
public class OurThreadFactory implements ThreadFactory {

  /**
   * Contador atómico que lleva el seguimiento de la cantidad de hilos creados. Se
   * utiliza para
   * generar un identificador único para cada hilo.
   */
  private static final AtomicInteger counterThreads = new AtomicInteger(0);

  /**
   * Lista sincronizada que almacena la información sobre los hilos creados. Esta
   * lista contiene
   * cadenas que describen cada hilo, incluyendo su nombre y la fecha de creación.
   */
  private final List<String> stats;

  /**
   * Constructor que inicializa la lista de estadísticas de hilos. La lista es
   * sincronizada para
   * garantizar la seguridad en entornos multihilo.
   */
  public OurThreadFactory() {
    stats = Collections.synchronizedList(new ArrayList<>());
  }

  /**
   * Metodo sobrescrito de la interfaz {@link ThreadFactory} para crear un nuevo
   * hilo. Se asigna un
   * nombre único al hilo que incluye un identificador basado en el contador
   * atómico, así como
   * información sobre la tarea que ejecutará. Además, se registra la fecha y hora
   * de la creación
   * del hilo en la lista stats.
   *
   * @param r la tarea que el hilo ejecutará.
   * @return un nuevo hilo con un nombre único.
   */
  @Override
  public Thread newThread(Runnable r) {
    int threadId = counterThreads.incrementAndGet();
    Thread t = new Thread(r, "Thread-" + threadId + " para " + r.toString());
    stats.add("Hilo " + t.getName() + " fue creado en el momento: " + new Date());
    return t;
  }

  /**
   * Metodo que devuelve la lista de estadísticas de hilos creados. Cada entrada
   * en la lista
   * describe un hilo, incluyendo su nombre y la fecha en que fue creado.
   *
   * @return una lista sincronizada con las estadísticas de los hilos.
   */
  public List<String> getStats() {
    return stats;
  }
}
