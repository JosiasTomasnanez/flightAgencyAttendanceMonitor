import java.util.ArrayList;

public class ConfiguracionInicial {
private int numero_politica;
private Politica politica;
private int [] marcado;
private int [][] matrizIncidencia;
private RedDePetri redDePetri;
private ArrayList <AlfaYBeta> alfaYBetas;

public ConfiguracionInicial(){
    setupMatriz();
    setupPolitica();
    setupAlfaYBeta();
    setupRedDePetri();
    setupMonitor();
}


public RedDePetri getRedDePetri(){
    return redDePetri;
}

private void setupMatriz() {
marcado = new int[] { 186, 1, 0, 0, 5, 0, 1, 1, 0, 0, 1, 0, 0, 0, 0 };

matrizIncidencia = new int[][] {
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
}

    private void setupPolitica() {
        numero_politica = 2;
        try {
                politica = new PoliticaAgenciaVuelo(numero_politica);
            } catch (PoliticaInexistenteException pie) {
                System.out.println("Politica Invalida");
            }
    }

    private void setupRedDePetri() {
        redDePetri = new RedDePetri(matrizIncidencia, marcado, politica, alfaYBetas);
    }

    private void setupMonitor() {
        Monitor.getInstance(redDePetri);
    }

    private void setupAlfaYBeta() {

        alfaYBetas= new ArrayList<>();
            for (int i = 0; matrizIncidencia[0].length> i; i++) {
                alfaYBetas.add(new AlfaYBeta());
            }
        alfaYBetas.get(1).setAlfaYBeta(8, 151);
        alfaYBetas.get(4).setAlfaYBeta(24, 141);
        alfaYBetas.get(5).setAlfaYBeta(24, 141);
        alfaYBetas.get(8).setAlfaYBeta(16, 90);
        alfaYBetas.get(9).setAlfaYBeta(20, 40);
        alfaYBetas.get(10).setAlfaYBeta(24, 70);
    }
}