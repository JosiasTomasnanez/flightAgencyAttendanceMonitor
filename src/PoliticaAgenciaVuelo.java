import java.util.List;

/**
 * Clase que implementa la interfaz {@link Politica} para manejar la selección
 * de transiciones en un
 * modelo de red de Petri, con dos políticas posibles: balanceada y priorizada.
 */
public class PoliticaAgenciaVuelo implements Politica {

  private int numeroPolitica; // Política elegida

  public PoliticaAgenciaVuelo(int numeroPolitica) throws PoliticaInexistenteException {
    setPolitica(numeroPolitica);
  }

  @Override
  public void setPolitica(int numeroPolitica) throws PoliticaInexistenteException {
    if (numeroPolitica < 1 || numeroPolitica > 2) {
      throw new PoliticaInexistenteException();
    }
    this.numeroPolitica = numeroPolitica;
  }

  @Override
  public int llamadaApolitica(List<Integer> candidatos) {
    switch (numeroPolitica) {
      case 1:
        if (candidatos.contains(2) && candidatos.contains(3))
          return politicaBalanceada(2, 3);
        return politicaBalanceada(6, 7);

      case 2:
        if (candidatos.contains(2) && candidatos.contains(3))
          return politicaPriorizada(2, 3);
        return politicaPriorizada(6, 7);
      default:
        return -1;
    }
  }

  /**
   * Aplica la política priorizada para decidir entre dos transiciones.
   *
   * @param i la primera transición.
   * @param j la segunda transición.
   * @return la transición seleccionada según la prioridad.
   */
  private int politicaPriorizada(int i, int j) {
    if ((i == 2 && j == 3) || (i == 3 && j == 2)) {
      int resultado = Math.random() <= 0.75 ? i : j;
      return resultado;
    } else {
      int resultado = Math.random() <= 0.8 ? i : j;
      return resultado;
    }
  }

  /**
   * Aplica la política balanceada para decidir entre dos transiciones.
   *
   * @param i la primera transición.
   * @param j la segunda transición.
   * @return la transición seleccionada con menor cantidad de disparos acumulados.
   */
  private int politicaBalanceada(int i, int j) {
    return Math.random() <= 0.5 ? i : j;
  }
}
