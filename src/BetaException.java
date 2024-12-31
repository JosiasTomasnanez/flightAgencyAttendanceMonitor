/**
 * La clase {@code BetaException} es una excepción personalizada que se lanza cuando el tiempo
 * transcurrido supera el límite máximo permitido ({@code beta}).
 *
 * <p>Esta excepción se utiliza en el contexto de la clase {@code AlfaYBeta} para indicar que el
 * tiempo registrado excede el valor máximo permitido.
 */
public class BetaException extends Exception {

  /** Crea una nueva instancia de {@code BetaException} sin un mensaje de error adicional. */
  public BetaException() {
    super();
  }

  /**
   * Crea una nueva instancia de {@code BetaException} con un mensaje que indica el tiempo excedido
   * en milisegundos.
   *
   * @param tiempoExcedido el tiempo excedido (en milisegundos) que causó la excepción.
   */
  public BetaException(Long tiempoExcedido) {
    super(tiempoExcedido.toString());
  }
}
