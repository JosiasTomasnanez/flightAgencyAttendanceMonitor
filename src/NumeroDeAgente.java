/**
 * Enumeración que representa los diferentes números de agentes en el sistema. Esta enumeración
 * define dos agentes: AGENTE1 y AGENTE2, cada uno asociado a un número de agente único. La clase
 * {@code NumeroDeAgente} proporciona una forma de representar los números de agentes dentro del
 * sistema, permitiendo su uso en diferentes procesos o acciones que requieren identificar a los
 * agentes de manera sencilla y clara.
 */
public enum NumeroDeAgente {
  AGENTE1(1), // Representa al Agente 1, identificado con el número "1".
  AGENTE2(2); // Representa al Agente 2, identificado con el número "2".

  private final int numeroDeAgente; // El número de agente asociado a la instancia.

  /**
   * Constructor privado para inicializar el número de agente.
   *
   * @param numeroDeAgente el número asociado al agente, como una cadena de texto.
   */
  private NumeroDeAgente(int numeroDeAgente) {
    this.numeroDeAgente = numeroDeAgente;
  }

  /**
   * Obtiene el número de agente asociado a la instancia.
   *
   * @return el número de agente como una cadena de texto.
   */
  public int getnumeroDeAgente() {
    return numeroDeAgente;
  }
}
