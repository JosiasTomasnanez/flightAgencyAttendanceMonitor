/**
 * Clase que simula un proceso de cancelación en una red de Petri, implementando
 * la interfaz {@link Runnable}. El proceso de cancelación dispara las
 * transiciones T7, T8 y T11 de manera secuencial
 * hasta que el monitor indique que el proceso ha terminado. Este proceso se
 * ejecuta en un hilo independiente.
 */
public class Cancelacion implements Runnable {

  private final MonitorInterface monitor; // Instancia del monitor utilizado para disparar las transiciones en la red

  /**
   * Constructor de la clase {@link Cancelacion}.
   *
   * @param monitor el monitor asignado a esta clase
   */
  public Cancelacion(MonitorInterface monitor) {
    this.monitor = monitor;
  }

  /**
   * Metodo que ejecuta el proceso de cancelación, disparando una serie de
   * transiciones en la red de
   * Petri. El proceso continúa ejecutándose en un ciclo hasta que {@link
   * MonitorInterface#isFinish()} indique que el proceso ha terminado. En cada
   * ciclo, el metodo
   * dispara las transiciones T7, T8 y T11 de forma secuencial, con un breve
   * período de espera entre
   * ellas para simular la duración del proceso.
   *
   * @throws RuntimeException si ocurre una interrupción durante la espera del
   *                          proceso.
   */
  @Override
  public void run() {
    while (true) {
      if (!monitor.fireTransition(7)) {
        return; // Disparo de T7
      }
      try {
        Thread.sleep(90); // Duracion del proceso
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      if (!monitor.fireTransition(8)) {
        return; 
      }
      if (!monitor.fireTransition(11)) {
        return; 
      }
    }
  }

  /**
   * Representación en cadena de texto del proceso de cancelación.
   *
   * @return una cadena que describe el proceso de cancelación.
   */
  @Override
  public String toString() {
    return "Proceso de Cancelacion";
  }
}
