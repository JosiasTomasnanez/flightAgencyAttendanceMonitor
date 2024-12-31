/**
 * La clase {@code AlfaYBeta} gestiona un sistema de control de tiempos basado en dos límites:
 * un tiempo mínimo ({@code alfa}) y un tiempo máximo ({@code beta}).
 *
 * La clase permite comprobar si un tiempo transcurrido cumple con los valores de {@code alfa} y {@code beta},
 * lanzando excepciones específicas en caso de incumplimiento.
 */
public class AlfaYBeta {
    private long tiempoActual = 0; // Tiempo actual en milisegundos desde un momento inicial registrado.
    private final long alfa, beta; // Limite superior e inferior permitido en milisegundos
    private boolean habilitada=true; // Estado de habilitación para comprobaciones de tiempo.

    /**
     * Constructor de la clase {@code AlfaYBeta}.
     *
     * @param alfa el tiempo mínimo permitido (en milisegundos).
     * @param beta el tiempo máximo permitido (en milisegundos).
     */
    public AlfaYBeta(int alfa, int beta){
        this.alfa=alfa;
        this.beta=beta;
    }

    /**
     * Verifica si el estado está habilitado.
     *
     * @return {@code true} si está habilitada, {@code false} en caso contrario.
     */
    public boolean isHabilitada(){
        return habilitada;
    }

    /**
     * Establece el tiempo actual y deshabilita el estado.
     *
     * @param tiempoActual el tiempo actual (en milisegundos).
     */
    public void setTiempoActual(long tiempoActual){
        this.tiempoActual=tiempoActual;
        habilitada=false;
    }

    /**
     * Obtiene el valor de {@code alfa}.
     *
     * @return el tiempo mínimo permitido (en milisegundos).
     */
    public long getAlfa() {
        return alfa;
    }

    /**
     * Obtiene el valor de {@code beta}.
     *
     * @return el tiempo máximo permitido (en milisegundos).
     */
    public long getBeta() {
        return beta;
    }

    /**
     * Obtiene el tiempo actual.
     *
     * @return el tiempo actual registrado (en milisegundos).
     */
    public long getTiempoActual() {
        return tiempoActual;
    }

    /**
     * Comprueba si el tiempo transcurrido desde el momento registrado cumple con los límites de {@code alfa} y {@code beta}.
     *
     * @throws AlfaException si el tiempo transcurrido es menor que {@code alfa}.
     * @throws BetaException si el tiempo transcurrido es mayor que {@code beta}.
     */
    public void comprobarAlfaYBeta() throws AlfaException , BetaException{
        long tiempo = System.currentTimeMillis()-tiempoActual;
        if (tiempo< alfa)
            throw new AlfaException();
        habilitada=true;
        if (tiempo > beta)
            throw new BetaException(tiempo);
    }
}
