import java.awt.*;
import javax.swing.*;

/**
 * Clase que representa una pantalla de carga gráfica que muestra un progreso de ejecución
 * utilizando una barra de progreso basada en cuadros. Se utiliza para mostrar el avance durante la
 * ejecución de un proceso, como la realización de transiciones en un sistema. Esta clase extiende
 * {@link JFrame} para crear una ventana gráfica en la interfaz de usuario.
 */
public class PantallaCarga extends JFrame {
  private static JPanel panel; // Panel donde se dibuja la barra de progreso.
  private static int porcentaje = 0; // Porcentaje de avance de la carga.

  /**
   * Constructor que configura la ventana de la pantalla de carga. Inicializa la ventana con un
   * título, tamaño fijo y comportamiento al cerrar. Configura el panel donde se dibuja el progreso.
   */
  public PantallaCarga() {
    setTitle("Ejecutando Transiciones");
    setSize(400, 60);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLocationRelativeTo(null);

    panel =
        new JPanel() {
          @Override
          protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int totalCuadros = 100;
            int cuadrosLlenos = (int) Math.round(porcentaje * (totalCuadros / 100.0));
            int cuadroAncho = (getWidth() + 17) / totalCuadros;
            for (int i = 0; i < cuadrosLlenos; i++) {
              g.setColor(Color.GREEN);
              g.fillRect(i * cuadroAncho, 0, cuadroAncho, getHeight());
            }
          }
        };
    add(panel);
  }

  /**
   * Incrementa el porcentaje de avance en la pantalla de carga, basado en el número de clientes que
   * han salido del sistema comparado con el total de clientes. Si el porcentaje aumenta, actualiza
   * la pantalla de carga.
   *
   * @param clientesSalientes Número de clientes que han salido
   * @param clientesMax Número total de clientes
   */
  public static void incrementarPorcentaje(int clientesSalientes, int clientesMax) {
    int nuevoPorcentaje = (clientesSalientes * 100) / clientesMax;
    if (nuevoPorcentaje > porcentaje) {
      porcentaje = nuevoPorcentaje;
      SwingUtilities.invokeLater(() -> panel.repaint());
    }
    if (porcentaje >= 100) {
      panel.setVisible(false);
    }
  }

  /**
   * Cierra la pantalla de carga, liberando los recursos y cerrando la ventana. Se invoca en el hilo
   * de eventos de Swing para asegurar que la operación se haga de manera segura.
   */
  public static void cerrar() {
    SwingUtilities.invokeLater(
        () -> {
          JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(panel);
          if (topFrame != null) {
            topFrame.dispose();
          }
        });
  }
}
