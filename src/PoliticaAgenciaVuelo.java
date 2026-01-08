/**
 * Clase que implementa la interfaz {@link Politica} para manejar la selección
 * de transiciones en un
 * modelo de red de Petri, con dos políticas posibles: balanceada y priorizada.
 */
public class PoliticaAgenciaVuelo implements Politica {

  /**
   * Enumeración que define las políticas disponibles junto con su código y
   * descripción.
   */
  private enum Politicas {
    POLITICA_1(1, "Politica balanceada"),
    POLITICA_2(2, "Politica priorizada");

    private final int codigo;

    /**
     * Constructor para inicializar los valores de la enumeración.
     *
     * @param codigo      el código asociado a la política.
     * @param descripcion la descripción de la política.
     */
    Politicas(int codigo, String descripcion) {
      this.codigo = codigo;
    }

    /**
     * Obtiene el código de la política.
     *
     * @return el código de la política.
     */
    public int getCodigo() {
      return codigo;
    }

    /**
     * Verifica si un código corresponde a una política válida.
     *
     * @param codigo el código a verificar.
     * @return true si el código es válido; false en caso contrario.
     */
    public static boolean isCodigoValido(int codigo) {
      for (Politicas politica : values()) {
        if (politica.getCodigo() == codigo) {
          return true;
        }
      }
      return false;
    }
  }

  private int numeroPolitica; // numero identificador de la politica a usar

  /**
   * Constructor de la clase que inicializa los contadores y establece la
   * política.
   *
   * @param numeroPolitica el código de la política a aplicar.
   * @throws PoliticaInexistenteException si el código de la política es inválido.
   */
  public PoliticaAgenciaVuelo(int numeroPolitica) throws PoliticaInexistenteException {
    setPolitica(numeroPolitica);
  }

  @Override
  public void setPolitica(int numeroPolitica) throws PoliticaInexistenteException {
    if (Politicas.isCodigoValido(numeroPolitica)) {
      this.numeroPolitica = numeroPolitica;
    } else {
      throw new PoliticaInexistenteException();
    }
  }

  @Override
  public int llamadaApolitica(int i, int j) {
    if (numeroPolitica == Politicas.POLITICA_1.getCodigo()) {
      return politicaBalanceada(i, j);
    } else if (numeroPolitica == Politicas.POLITICA_2.getCodigo()) {
      return politicaPriorizada(i, j);
    } else {
      throw new IllegalStateException("Política no definida o inválida.");
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
    if (i == 2 || i == 3 || j==2 || j == 3) {
      return Math.random() < 0.75 ? i : j;
    } else {
      return Math.random() < 0.8 ? i : j;
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
    return Math.random() < 0.5 ? i : j;
  }
}
