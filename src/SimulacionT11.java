public class SimulacionT11 implements Runnable{
    private final MonitorInterface monitor;

    public SimulacionT11(MonitorInterface monitor){
        this.monitor=monitor;
    }

    @Override
    public void run() {
        while (true) {
             if (!monitor.fireTransition(11)) {
                return; // Disparo de T11
            }
        }
    }
    
}
