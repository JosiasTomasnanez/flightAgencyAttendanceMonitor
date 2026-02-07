package procesos;

import monitor.MonitorInterface;

/**
 * Clase que simula un proceso de confirmación y pago en una red de Petri,
 * implementando la interfaz
 * {@link Runnable}. El proceso de confirmación y pago dispara las transiciones
 * T6, T9, T10 y T11 de
 * manera secuencial hasta que el monitor indique que el proceso ha terminado.
 * Este proceso se
 * ejecuta en un hilo independiente.
 */
public class ConfirmacionYPago implements Runnable {

  private final MonitorInterface monitor; // Instancia del monitor utilizado para disparar las transiciones en la red

  /**
   * Constructor de la clase {@link ConfirmacionYPago}.
   *
   * @param monitor el monitor asignado a esta clase
   */
  public ConfirmacionYPago(MonitorInterface monitor) {
    this.monitor = monitor;
  }

  /**
   * Metodo que ejecuta el proceso de confirmación y pago, disparando una serie de
   * transiciones en
   * la red de Petri. El proceso continúa ejecutándose en un ciclo hasta que
   * {@link
   * MonitorInterface#isFinish()} indique que el proceso ha terminado. En cada
   * ciclo, el metodo
   * dispara las transiciones T6, T9, T10 y T11 de forma secuencial, con breves
   * períodos de espera
   * entre ellas para simular la duración de cada proceso.
   *
   * @throws RuntimeException si ocurre una interrupción durante la espera de los
   *                          procesos.
   */
  @Override
  public void run() {
    while (true) {
      if (!monitor.fireTransition(6)) {
        return; // Disparo de T6
      }
      try {
        Thread.sleep(40); // Duracion del Proceso Confirmacion
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      if (!monitor.fireTransition(9)) {
        return; // Disparo de T9
      }
      try {
        Thread.sleep(70); // Duracion del Proceso de Pago
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      if (!monitor.fireTransition(10)) {
        return; // Disparo de T10
      }
    }
  }

  /**
   * Representación en cadena de texto del proceso de confirmación y pago.
   *
   * @return una cadena que describe el proceso de confirmación y pago.
   */
  @Override
  public String toString() {
    return "Proceso de Cconfirmacion";
  }
}
