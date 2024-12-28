/**
 * Clase que simula el proceso de entrada de clientes a una agencia, implementando la interfaz
 * {@link Runnable}. Este proceso dispara las transiciones T0 y T1 de la red de Petri de manera
 * secuencial en cada ciclo, hasta que el monitor indique que el proceso ha terminado. El proceso se
 * ejecuta en un hilo independiente.
 */
public class EntradaDeClientes implements Runnable {

  private final MonitorInterface
      monitor; // Instancia del monitor utilizado para disparar las transiciones en la red de Petri.

  /**
   * Constructor de la clase {@link EntradaDeClientes}.
   *
   * @param monitor el monitor asignado a esta clase
   */
  public EntradaDeClientes(MonitorInterface monitor) {
    this.monitor = monitor;
  }

  /**
   * Metodo que ejecuta el proceso de entrada de clientes a la agencia, disparando las transiciones
   * T0 y T1. El proceso continúa ejecutándose en un ciclo hasta que {@link
   * MonitorInterface#isFinish()} indique que el proceso ha terminado. En cada ciclo, el metodo
   * dispara las transiciones T0 (entrada del cliente) y T1 (proceso posterior) de manera
   * secuencial, con un período de espera para simular la duración del proceso de entrada del
   * cliente.
   *
   * @throws RuntimeException si ocurre una interrupción durante la espera del proceso.
   */
  @Override
  public void run() {
    while (!monitor.isFinish()) {
      monitor.fireTransition(0); // Disparo de T0
      try {
        Thread.sleep(200); // Duracion del Proceso
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
      monitor.fireTransition(1); // Disparo de T1
    }
  }

  /**
   * Representación en cadena de texto del proceso de entrada de clientes a la agencia.
   *
   * @return una cadena que describe el proceso de entrada de clientes.
   */
  @Override
  public String toString() {
    return "Proceso de entrar a la agencia";
  }
}
