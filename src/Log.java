import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * La clase {@code Log} se encarga de registrar información sobre el estado del sistema en un
 * archivo de log. Implementa {@code Runnable} para permitir que se ejecute en un hilo separado y
 * registre periódicamente el estado del sistema, incluyendo estadísticas sobre las transiciones y
 * los clientes.
 *
 * <p>La clase usa un archivo de log denominado {@code log.txt}, y los datos se escriben en dicho
 * archivo de forma periódica con un intervalo de 100 ms, o al finalizar el proceso.
 */
public class Log implements Runnable {
  private final long tiempo; // El tiempo de inicio del proceso de registro, en milisegundos.
  private static final FileWriter file; // El escritor de archivo para guardar los registros.
  private static final PrintWriter
      pw; // El escritor de texto que permite la escritura en el archivo de log.

  static {
    try {
      file = new FileWriter("log.txt");
      pw = new PrintWriter(file, true); // autoflush activado
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /** Constructor de la clase {@code Log}, inicializa el tiempo de inicio del proceso. */
  public Log() {
    tiempo = System.currentTimeMillis();
  }

  /**
   * Cuenta cuántas veces aparece una transición específica en una cadena de secuencia.
   *
   * @param cadena La secuencia de transiciones donde se realizará la búsqueda.
   * @param transicion El identificador de la transición a contar (por ejemplo, "T2").
   * @return El número de veces que aparece la transición en la secuencia.
   * @throws IllegalArgumentException Si el formato de la transición no es válido.
   */
  public int contarTransiciones(String cadena, String transicion) {
    // Asegúrate de que la transición comience con 'T' seguida de un número
    if (!transicion.matches("T\\d+")) {
      throw new IllegalArgumentException("Formato de transición inválido.");
    }
    // Expresión regular para encontrar transiciones válidas
    String regex = transicion + "(?!\\d)"; // `(?<!\\d)` asegura que no haya un dígito después
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(cadena);
    int contador = 0;
    while (matcher.find()) {
      contador++;
    }
    return contador;
  }

  /** Imprime la secuencia de transiciones al archivo de log. */
  private void imprimirTransiciones() {
    pw.println("secuencia de transiciones:\n" + Monitor.getInstance().getSecuencia());
  }

  /**
   * Metodo que ejecuta el hilo y registra información sobre el estado del sistema en el archivo de
   * log. Se ejecuta en un ciclo hasta que {@code Monitor} indique que el proceso ha terminado.
   */
  @Override
  public void run() {
    while (true) {
      if (Monitor.getInstance().isFinish()) {
        pw.println(
            "tiempo en milis: "
                + (System.currentTimeMillis() - tiempo)
                + "\n"
                + "clientes atendidos por el agente 1: "
                + contarTransiciones(Monitor.getInstance().getSecuencia(), "T2")
                + "\n"
                + "clientes atendidos por el agente 2: "
                + contarTransiciones(Monitor.getInstance().getSecuencia(), "T3")
                + "\n"
                + "Cantidad de clientes que confirmaron: "
                + contarTransiciones(Monitor.getInstance().getSecuencia(), "T6")
                + "\n"
                + "Cantidad de clientes que Cancelaron: "
                + contarTransiciones(Monitor.getInstance().getSecuencia(), "T7")
                + "\n"
                + "clientes que salieron en total: "
                + Monitor.getInstance().getMarcado()[14]
                + "\n");
        imprimirTransiciones();
        comprobarSecuencia();
        pw.println(
            "\nErrores de beta(Exceso de tiempo en espera para un disparo):\n"
                + Monitor.getInstance().getBetaErrors());
        return;
      }
      int[] marcado = Monitor.getInstance().getMarcado();
      pw.println(
          "Clientes por entrar: "
              + marcado[0]
              + "\n"
              + "Clientes en puerta: "
              + marcado[2]
              + "\n"
              + "Clientes esperando para reservar: "
              + marcado[3]
              + "\n"
              + "Clientes atendiendose por agente 1: "
              + marcado[5]
              + "\n"
              + "Clientes atendiendose por agente 2: "
              + marcado[8]
              + "\n"
              + "Clientes esperando para cancelar o confirmar reserva: "
              + marcado[9]
              + "\n"
              + "Clientes confirmando: "
              + marcado[11]
              + "\n"
              + "Clientes pagando: "
              + marcado[13]
              + "\n"
              + "Clientes cancelando: "
              + marcado[12]
              + "\n"
              + "Clientes saliente: "
              + marcado[14]
              + "\n");
      try {
        Thread.sleep(100);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void comprobarSecuencia() {
    pw.println();
    try {
      // Detectar dinámicamente el intérprete de Python
      String pythonPath = detectPythonInterpreter();
      if (pythonPath == null) {
        return;
      }

      String scriptPath = "PetriFlightAnalyzer.py";
      String parametro = Monitor.getInstance().getSecuencia();

      // Crear el proceso
      ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, scriptPath, parametro);
      processBuilder.redirectErrorStream(true);
      Process process = processBuilder.start();

      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      String line;
      while ((line = reader.readLine()) != null) {
        pw.println(line);
      }

      process.waitFor();
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException("Error al ejecutar el script de Python.", e);
    }
  }

  private String detectPythonInterpreter() {
    String[] interpreters = {"python3", "python", "python2"};
    for (String interpreter : interpreters) {
      try {
        ProcessBuilder processBuilder = new ProcessBuilder(interpreter, "--version");
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        // Leer la salida para comprobar si es válido
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        if (reader.readLine() != null) {
          return interpreter; // Devolver el primer intérprete válido
        }
      } catch (IOException ignored) {
        // Ignorar y probar el siguiente intérprete
      }
    }
    return null; // No se encontró un intérprete válido
  }
}
