/**
 * La clase {@code AlfaException} es una excepción personalizada que se lanza cuando el tiempo
 * transcurrido no cumple con el límite mínimo establecido ({@code alfa}). Esta excepción se utiliza
 * en el contexto de la clase {@code AlfaYBeta} para indicar que el tiempo registrado es inferior al
 * valor mínimo permitido.
 */
public class AlfaException extends Exception {

  /** Crea una nueva instancia de {@code AlfaException} sin un mensaje de error adicional. */
  public AlfaException() {
    super();
  }

  /**
   * Crea una nueva instancia de {@code AlfaException} con un mensaje de error específico.
   *
   * @param message el mensaje de error que describe la excepción.
   */
  public AlfaException(String message) {
    super(message);
  }
}
