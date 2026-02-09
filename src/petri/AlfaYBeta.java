package petri;

public class AlfaYBeta {

    public static enum Estado {
        OK, // puede dispararse
        ALFA, // no alcanzó alfa → bloquear
        BETA // excedió beta → loguear y permitir
    }

    private boolean iniciado = false;
    private long alfa, beta;
    private long inicio;
    private boolean sinRestriccion = false;

    public AlfaYBeta(long alfa, long beta) {
        this.alfa = alfa;
        this.beta = beta;
        this.sinRestriccion = false;
        this.inicio = 0;
    }

    public AlfaYBeta() {
        this.sinRestriccion = true;
        this.inicio = 1;
    }

    public void setAlfaYBeta(long alfa, long beta) {
        this.alfa = alfa;
        this.beta = beta;
        this.sinRestriccion = false;
        this.inicio = 0;
    }

    public void setInicio(long inicio) {
        this.inicio = inicio;
    }

    public long getInicio() {
        return inicio;
    }

    public long getBeta() {
        return beta;
    }

    public long getAlfa() {
        return alfa;
    }

    public void iniciar() {
        this.inicio = System.currentTimeMillis();
        this.iniciado = true;
    }

    public Estado verificarVentana() {
        if (sinRestriccion)
            return Estado.OK;

        // EVITAR errores en el primer uso
        if (!iniciado) {
            return Estado.OK; // permite el primer disparo
        }

        long elapsed = System.currentTimeMillis() - inicio;

        if (elapsed < alfa)
            return Estado.ALFA;

        if (elapsed > beta)
            return Estado.BETA;

        return Estado.OK;
    }

    public long getTiempoExcedido() {
        return System.currentTimeMillis() - inicio - beta;
    }
}
