/**
 * Excepción personalizada que se lanza cuando se intenta utilizar una política que no existe.
 * Extiende la clase {@link Exception}, lo que la convierte en una excepción verificable. Esta
 * excepción se utiliza para manejar casos en los que se ingresa un valor inválido para una
 * política, indicando que la política solicitada no está definida o no es válida.
 */
public class PoliticaInexistenteException extends Exception {

  /**
   * Crea una nueva instancia de {@code PoliticaInexistenteException} sin un mensaje de error
   * adicional.
   */
  public PoliticaInexistenteException() {
    super();
  }
}
