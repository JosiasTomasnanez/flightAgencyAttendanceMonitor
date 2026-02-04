import java.util.List;

/**
 * Interfaz que define el comportamiento de una política para decidir entre
 * transiciones en un
 * modelo de red de Petri. Las clases que implementen esta interfaz deberán
 * proporcionar una
 * implementación específica para seleccionar transiciones basadas en diferentes
 * criterios.
 */
public interface Politica {

  /**
   * Establece la política a utilizar según el número de políticas especificado.
   *
   * @param numeroDePolitica el código de la política a aplicar.
   * @throws PoliticaInexistenteException si el número de política proporcionado
   *                                      no corresponde a
   *                                      ninguna política válida.
   */
  void setPolitica(int numeroDePolitica) throws PoliticaInexistenteException;

  /**
   * Selecciona una transición entre dos opciones basándose en la política
   * definida.
   *
   * @param i         el código de la primera transición.
   * @param j         el código de la segunda transición.
   * @param secuencia la secuencia de transiciones disparadas hasta el momento,
   *                  que puede ser
   *                  utilizada para la toma de decisiones.
   * @return el código de la transición seleccionada según los criterios de la
   *         política.
   */
  int llamadaApolitica(List<Integer> conflicto);
}
