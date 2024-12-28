/**
 * Clase que implementa la interfaz {@link Politica} para manejar la selección de transiciones en un
 * modelo de red de Petri, con dos políticas posibles: balanceada y priorizada.
 */
public class PoliticaAgenciaVuelo implements Politica {

  /** Enumeración que define las políticas disponibles junto con su código y descripción. */
  private enum Politicas {
    POLITICA_1(1, "Politica balanceada"),
    POLITICA_2(2, "Politica priorizada");

    private final int codigo;
    private final String descripcion;

    /**
     * Constructor para inicializar los valores de la enumeración.
     *
     * @param codigo el código asociado a la política.
     * @param descripcion la descripción de la política.
     */
    Politicas(int codigo, String descripcion) {
      this.codigo = codigo;
      this.descripcion = descripcion;
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
     * Obtiene la descripción de la política.
     *
     * @return la descripción de la política.
     */
    public String getDescripcion() {
      return descripcion;
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
  private static final String[] TRANSICIONES = {
    "T2", "T3", "T6", "T7"
  }; // Arreglo con las transiciones que queremos identificar y contar
  private int countT2, countT3, countT6, countT7; // Contadores para cada transición

  /**
   * Constructor de la clase que inicializa los contadores y establece la política.
   *
   * @param numeroPolitica el código de la política a aplicar.
   * @throws PoliticaInexistenteException si el código de la política es inválido.
   */
  public PoliticaAgenciaVuelo(int numeroPolitica) throws PoliticaInexistenteException {
    resetCounters();
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
  public int llamadaApolitica(int i, int j, String secuencia) {
    actualizarSecuencias(secuencia);
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
    if (i == 2 || i == 3) {
      double porcentajeActualT2 =
          (double) countT2 / (countT2 + countT3 + 1); // Evita división por 0
      return porcentajeActualT2 < 0.75 ? i : j; // 75% de prioridad para T2
    } else if (i == 6 || i == 7) {
      double porcentajeActualT6 =
          (double) countT6 / (countT6 + countT7 + 1); // Evita división por 0
      return porcentajeActualT6 < 0.80 ? i : j; // 80% de prioridad para T6
    }
    return i; // Por defecto, retorna i
  }

  /**
   * Aplica la política balanceada para decidir entre dos transiciones.
   *
   * @param i la primera transición.
   * @param j la segunda transición.
   * @return la transición seleccionada con menor cantidad de disparos acumulados.
   */
  private int politicaBalanceada(int i, int j) {
    if (i == 2) {
      return countT2 < countT3 ? i : j;
    } else {
      return countT6 < countT7 ? i : j;
    }
  }

  /**
   * Actualiza los contadores de las transiciones analizando la secuencia.
   *
   * @param secuencia la secuencia de transiciones disparadas.
   */
  public void actualizarSecuencias(String secuencia) {
    resetCounters(); // Reiniciamos contadores antes de procesar la secuencia

    for (int i = 0; i < secuencia.length(); i++) {
      for (String transicion : TRANSICIONES) {
        if (secuencia.startsWith(transicion, i)) {
          incrementarContador(transicion);
          i += transicion.length() - 1; // Saltamos la longitud de la transición
          break;
        }
      }
    }
  }

  /**
   * Incrementa el contador asociado a una transición.
   *
   * @param transicion la transición que se desea contar.
   */
  private void incrementarContador(String transicion) {
    switch (transicion) {
      case "T2" -> countT2++;
      case "T3" -> countT3++;
      case "T6" -> countT6++;
      case "T7" -> countT7++;
    }
  }

  /** Reinicia los contadores de todas las transiciones. */
  private void resetCounters() {
    countT2 = 0;
    countT3 = 0;
    countT6 = 0;
    countT7 = 0;
  }
}
