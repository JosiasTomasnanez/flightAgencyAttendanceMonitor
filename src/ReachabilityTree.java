import java.util.*;

/**
 * Clase que representa un árbol de alcanzabilidad para un sistema de red de Petri. Dado un sistema
 * definido por una matriz de incidencia y una marcación inicial, esta clase calcula todos los
 * estados alcanzables a partir de la marcación inicial aplicando las transiciones de la red de
 * Petri.
 */
public class ReachabilityTree {
  private final int[][]
      incidenceMatrix; // Matriz de incidencia que define las transiciones y sus efectos
  private final int[] initialMarking; // Marcación inicial del sistema
  private final Set<String> visitedStates; // Conjunto de estados visitados para evitar ciclos
  private final Queue<int[]> queue; // Cola para almacenar los estados a procesar
  private final List<int[]> reachabilityTree; // Lista que almacena todos los estados alcanzables

  /**
   * Constructor que inicializa el árbol de alcanzabilidad con la matriz de incidencia y la
   * marcación inicial.
   *
   * @param incidenceMatrix Matriz de incidencia que describe las transiciones del sistema
   * @param initialMarking Marcación inicial que representa el estado de inicio del sistema
   */
  public ReachabilityTree(int[][] incidenceMatrix, int[] initialMarking) {
    this.incidenceMatrix = incidenceMatrix;
    this.initialMarking = initialMarking;
    this.visitedStates = new HashSet<>();
    this.queue = new LinkedList<>();
    this.reachabilityTree = new ArrayList<>();
  }

  /**
   * Metodo que construye el árbol de alcanzabilidad. Genera todos los estados alcanzables desde la
   * marcación inicial aplicando las transiciones del sistema. Utiliza una cola para procesar cada
   * estado y evita ciclos utilizando un conjunto de estados visitados.
   */
  public void buildReachabilityTree() {
    // Start with the initial marking
    queue.add(initialMarking);
    visitedStates.add(Arrays.toString(initialMarking));
    reachabilityTree.add(initialMarking.clone());

    while (!queue.isEmpty()) {
      int[] currentMarking = queue.poll();

      // Generate next states
      for (int transition = 0; transition < incidenceMatrix[0].length; transition++) {
        int[] nextMarking = applyTransition(currentMarking, transition);
        if (nextMarking != null && !visitedStates.contains(Arrays.toString(nextMarking))) {
          visitedStates.add(Arrays.toString(nextMarking));
          queue.add(nextMarking);
          reachabilityTree.add(nextMarking.clone());
        }
      }
    }
  }

  /**
   * Aplica una transición a una marcación dada. Calcula la nueva marcación después de aplicar la
   * transición, teniendo en cuenta los efectos definidos por la matriz de incidencia. Si la
   * transición no es habilitada, devuelve null.
   *
   * @param marking Marcación actual sobre la que se aplicará la transición
   * @param transition Índice de la transición a aplicar
   * @return Nueva marcación resultante de aplicar la transición, o null si no es habilitada
   */
  private int[] applyTransition(int[] marking, int transition) {
    int[] nextMarking = marking.clone();
    for (int i = 0; i < marking.length; i++) {
      nextMarking[i] += incidenceMatrix[i][transition];
      if (nextMarking[i] < 0) {
        return null; // Transition is not enabled
      }
    }
    return nextMarking;
  }

  /**
   * Calcula la suma de los valores de ciertos lugares de la red de Petri en los estados
   * alcanzables. Esta suma se utiliza para encontrar el valor máximo entre los estados alcanzables.
   *
   * @return El valor máximo de la suma de los lugares específicos en el árbol de alcanzabilidad
   */
  public int getSumaMarcados() {
    int[] resultado = new int[reachabilityTree.size()];
    int index = 0;

    for (int[] marcado : reachabilityTree) {
      int suma = 0;
      for (int j = 0; j < marcado.length; j++) {
        if (j == 2 || j == 3 || j == 5 || j == 8 || j == 9 || j == 11 || j == 12 || j == 13
            || j == 14) {
          suma += marcado[j];
        }
      }
      resultado[index] = suma;
      index++;
    }
    int maximo = resultado[0];
    for (int i : resultado) {
      if (i > maximo) {
        maximo = i;
      }
    }
    return maximo;
  }

  /**
   * Devuelve el árbol de alcanzabilidad, que es una lista de todos los estados alcanzables.
   *
   * @return Lista de arrays representando los estados alcanzables
   */
  public List<int[]> getReachabilityTree() {
    return reachabilityTree;
  }
}
