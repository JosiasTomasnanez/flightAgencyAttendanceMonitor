/**
 * Interfaz que define el comportamiento para un monitor de red de Petri. Esta
 * interfaz incluye
 * métodos para disparar transiciones, obtener la secuencia de transiciones y
 * verificar si el
 * proceso ha terminado.
 */
public interface MonitorInterface {

  /**
   * Dispara una transición específica en la red de Petri. El metodo devuelve un
   * valor booleano que
   * indica si la transición fue disparada correctamente. Tambien considera los
   * limites de tiempo
   * alfa y beta.
   *
   * @param transicion el código de la transición a disparar.
   * @return {@code true} si la transición se disparó exitosamente; {@code false}
   *         si no fue posible
   *         dispararla.
   */
  boolean fireTransition(int transicion);
}
