/**
 * Interfaz que define el comportamiento para un monitor de red de Petri. Esta interfaz incluye
 * métodos para disparar transiciones, obtener la secuencia de transiciones y verificar si el
 * proceso ha terminado.
 */
public interface MonitorInterface {

  /**
   * Dispara una transición específica en la red de Petri. El metodo devuelve un valor booleano que
   * indica si la transición fue disparada correctamente. Tambien considera los limites de tiempo
   * alfa y beta.
   *
   * @param transicion el código de la transición a disparar.
   * @return {@code true} si la transición se disparó exitosamente; {@code false} si no fue posible
   *     dispararla.
   */
  boolean fireTransition(int transicion);

  /**
   * Obtiene la secuencia actual de transiciones disparadas en la red de Petri. Esta secuencia se
   * devuelve como una cadena de texto que refleja el orden en que las transiciones fueron
   * activadas.
   *
   * @return una cadena que representa la secuencia de transiciones disparadas.
   */
  String getSecuencia();

  /**
   * Verifica si el proceso en la red de Petri ha terminado. Este metodo debe determinar si el
   * sistema ha llegado a un estado final, donde no es posible realizar más transiciones.
   *
   * @return {@code true} si el proceso ha terminado; {@code false} si aún puede continuar.
   */
  boolean isFinish();
}
