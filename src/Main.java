import java.util.ArrayList;
/**
 * Clase principal que gestiona la ejecución del programa de la agencia de
 * vuelos. Esta clase se encarga de la interacción con el usuario,
 * la selección de la política, la inicialización de la matriz de incidencia,
 * el marcado inicial y la creación y ejecución de los hilos que simulan las
 * diferentes tareas de la agencia.
 */
public class Main {
  public static final int CANTIDAD_HILOS_AGENTE_1 = 1;
  public static final int CANTIDAD_HILOS_AGENTE_2 = 1;
  public static final int CANTIDAD_HILOS_CANCELACION = 1;
  public static final int CANTIDAD_HILOS_CONFIRMACION = 1;
  public static final int CANTIDAD_HILOS_GEN_CLIENTES = 5;

  public static void main(String[] args) {

    ConfiguracionInicial configuracionInicial = new ConfiguracionInicial();

    {
      PantallaCarga pantalla = new PantallaCarga();
      pantalla.setVisible(true);
      pantalla.setResizable(false);
    } // parte gráfica de pantalla de carga

    OurThreadFactory factory = new OurThreadFactory();
    ArrayList<Thread> hilos = new ArrayList<>();

    // 1 hilo por agente
    for (int i = 0; i < CANTIDAD_HILOS_AGENTE_1; i++) {
      hilos.add(factory.newThread(new AtencionAgente(NumeroDeAgente.AGENTE1, Monitor.getInstance())));
    }

    for (int i = 0; i < CANTIDAD_HILOS_AGENTE_2; i++) {
      hilos.add(factory.newThread(new AtencionAgente(NumeroDeAgente.AGENTE2, Monitor.getInstance())));
    }

    // 1 Hilo encargado de la cancelacion
    for (int i = 0; i < CANTIDAD_HILOS_CANCELACION; i++) {
      hilos.add(factory.newThread(new Cancelacion(Monitor.getInstance())));
    }

    // 1 Hilo encargado de la confirmacion y pago
    for (int i = 0; i < CANTIDAD_HILOS_CONFIRMACION; i++) {
      hilos.add(factory.newThread(new ConfirmacionYPago(Monitor.getInstance())));
    }

    // 5 hilos encargados de la generacion y entrada de clientes
    for (int i = 0; i < CANTIDAD_HILOS_GEN_CLIENTES; i++) {
      hilos.add(factory.newThread(new EntradaDeClientes(Monitor.getInstance())));
    }

    // Hilo encargado del Log
    hilos.add(factory.newThread(new Log(configuracionInicial.getRedDePetri())));

    // Inicializacion de los hilos
    for (Thread h : hilos) {
      h.start();
    }
    for (Thread h : hilos) {
      try {
        h.join();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
    System.out.println("Fin de la ejecucion");
  }

}
