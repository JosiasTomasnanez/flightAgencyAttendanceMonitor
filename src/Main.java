import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Clase principal que gestiona la ejecución del programa de la agencia de
 * vuelos. Esta clase se
 * encarga de la interacción con el usuario, la selección de la política, la
 * inicialización de la
 * matriz de incidencia, el marcado inicial y la creación y ejecución de los
 * hilos que simulan las
 * diferentes tareas de la agencia.
 */
public class Main {
  public static void main(String[] args) {

    PoliticaAgenciaVuelo politica; // Variable que guarda la instancia de la politica a usar
    // Se pide por pantalla la seleccion de la politica a utilizar
    Scanner scanner = new Scanner(System.in);
    int numero;
    System.out.print(
        """
            \u001B[34mpoliticas:
            1) Politica balanceada
            2) Politica diferenciada
            """);
    while (true) {
      System.out.println("Ingrese el numero correspondiente a la politica: ");
      try {
        numero = scanner.nextInt();
        politica = new PoliticaAgenciaVuelo(numero);
        break;
      } catch (InputMismatchException ime) {
        scanner.next();
        System.out.println(" Solo se puede digitar un numero.");
      } catch (PoliticaInexistenteException pie) {
        System.out.println("El numero ingresado no corresponde a una politica valida");
      }
    }
    scanner.close();
    int[][] matrizIncidencia = new int[][] {
        { -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1 },
        { -1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
        { 1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
        { 0, 1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0 },
        { -1, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 1, 0, 0, -1, 0, 0, 0, 0, 0, 0 },
        { 0, 0, -1, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, -1, 1, 0, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 1, -1, 0, 0, 0, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 1, 1, -1, -1, 0, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 0, -1, -1, 1, 0, 1, 0 },
        { 0, 0, 0, 0, 0, 0, 1, 0, 0, -1, 0, 0 },
        { 0, 0, 0, 0, 0, 0, 0, 1, -1, 0, 0, 0 },
        { 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, -1, 0 },
        { 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, -1 }
    };

    ArrayList<AlfaYBeta> alfaybetas = new ArrayList<>();

    for (int i = 0; matrizIncidencia[0].length > i; i++) {
      alfaybetas.add(new AlfaYBeta());
    }
    alfaybetas.get(1).setAlfaYBeta(8, 41);
    alfaybetas.get(4).setAlfaYBeta(24, 201);
    alfaybetas.get(5).setAlfaYBeta(24, 201);
    alfaybetas.get(8).setAlfaYBeta(16, 81);
    alfaybetas.get(9).setAlfaYBeta(20, 81);
    alfaybetas.get(10).setAlfaYBeta(24, 91);

    int[] marcado = new int[] { 186, 1, 0, 0, 5, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0 };
    RedDePetri redDePetri = new RedDePetri(matrizIncidencia, marcado);
    Monitor.getInstance(redDePetri, politica, alfaybetas);

    {
      PantallaCarga pantalla = new PantallaCarga();
      pantalla.setVisible(true);
      pantalla.setResizable(false);
    } // parte gráfica de pantalla de carga

    OurThreadFactory factory = new OurThreadFactory();
    ArrayList<Thread> hilos = new ArrayList<>();

    // 1 hilo por agente
    for (int i = 0; i < 1; i++)
      hilos.add(
          factory.newThread(new AtencionAgente(NumeroDeAgente.AGENTE1, Monitor.getInstance())));

    for (int i = 0; i < 1; i++)
      hilos.add(
          factory.newThread(new AtencionAgente(NumeroDeAgente.AGENTE2, Monitor.getInstance())));

    // 1 Hilo encargado de la cancelacion
    for (int i = 0; i < 1; i++)
      hilos.add(factory.newThread(new Cancelacion(Monitor.getInstance())));

    // 1 Hilo encargado de la confirmacion y pago
    for (int i = 0; i < 1; i++)
      hilos.add(factory.newThread(new ConfirmacionYPago(Monitor.getInstance())));

    // 5 hilos encargados de la generacion y entrada de clientes
    for (int i = 0; i < 5; i++)
      hilos.add(factory.newThread(new EntradaDeClientes(Monitor.getInstance())));

    // Hilo encargado del Log
    hilos.add(factory.newThread(new Log(redDePetri)));

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
    System.out.println("\u001B[31m" + "Fin de la ejecucion");
  }
}
