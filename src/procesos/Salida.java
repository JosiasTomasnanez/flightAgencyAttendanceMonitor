package procesos;

import monitor.MonitorInterface;

public class Salida implements Runnable {
    private final MonitorInterface monitor;

    public Salida(MonitorInterface monitor) {
        this.monitor = monitor;
    }

    @Override
    public void run() {
        while (true) {
            if (!monitor.fireTransition(11)) {
                return; // Disparo de T11
            }
            try {
                Thread.sleep(10); // Duracion del proceso
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
