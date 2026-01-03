/**
 * Clase que simula la atención de un agente en una red de Petri, implementando
 * la interfaz {@link
 * Runnable}. Dependiendo del número de agente, dispara una serie de
 * transiciones en el monitor de
 * la red de Petri. El proceso de atención se ejecuta en un hilo independiente y
 * continúa hasta que
 * el monitor indique que el proceso ha terminado.
 */
public class AtencionAgente implements Runnable {

  private final NumeroDeAgente agente; // El número de agente que procesa la atención.
  private final MonitorInterface monitor; // Instancia del monitor utilizado para disparar las transiciones en la red de
                                          // Petri.

  /**
   * Constructor de la clase {@link AtencionAgente}.
   *
   * @param agente  el número del agente (1 o 2) que realizará el proceso de
   *                atención.
   * @param monitor el monitor asignado a esta clase
   */
  public AtencionAgente(NumeroDeAgente agente, MonitorInterface monitor) {
    this.agente = agente;
    this.monitor = monitor;
  }

  /**
   * Metodo que ejecuta el proceso de atención del agente. Dependiendo del número
   * de agente (AGENTE1
   * o AGENTE2), el metodo disparará diferentes transiciones en la red de Petri.
   * El proceso continúa
   * ejecutándose en un ciclo hasta que {@link MonitorInterface#isFinish()}
   * indique que el proceso
   * ha terminado. Si el agente es el AGENTE1 (número 1), se disparan las
   * transiciones T2 y T5. Si
   * el agente es el AGENTE2 (número 2), se disparan las transiciones T3 y T4. En
   * cada ciclo, el
   * hilo se duerme brevemente con {@link Thread#sleep(long)} para simular la
   * duración del proceso.
   */
  @Override
  public void run() {
    while (true) {
      if (agente.equals(NumeroDeAgente.AGENTE1)) { // Agente Numero 1 o Superor
        if (!monitor.fireTransition(2))
          return; // Disparo de T2
        try {
          Thread.sleep(200); // Duracion del proceso
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
        if (!monitor.fireTransition(5))
          return; // Disparo de T5
      } else { // Agente Numero 2 o Inferior
        monitor.fireTransition(3); // Disparo de T3
        try {
          Thread.sleep(200); // Duracion del proceso
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
        if (!monitor.fireTransition(4))
          return; // Disparo de T4
      }
    }
  }

  /**
   * Representación en cadena de texto del proceso de atención del agente.
   *
   * @return una cadena que describe el proceso de atención por el agente,
   *         indicando su número de
   *         agente.
   */
  @Override
  public String toString() {
    return "Proceso de Atencion por agente " + agente.getnumeroDeAgente();
  }
}
