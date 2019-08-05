package eu.merscher.lbsvolleyball.model;

public class Grunddaten {

    private Spieler spieler;
    private double kto_saldo_neu;
    private int teilnahmen;

    public Grunddaten(Spieler spieler, double kto_saldo_neu, int teilnahmen) {
        this.spieler = spieler;
        this.kto_saldo_neu = kto_saldo_neu;
        this.teilnahmen = teilnahmen;
    }

    public Spieler getSpieler() {
        return spieler;
    }

    public void setSpieler(Spieler spieler) {
        this.spieler = spieler;
    }

    public double getKto_saldo_neu() {
        return kto_saldo_neu;
    }

    public void setKto_saldo_neu(double kto_saldo_neu) {
        this.kto_saldo_neu = kto_saldo_neu;
    }

    public int getTeilnahmen() {
        return teilnahmen;
    }

    public void setTeilnahmen(int teilnahmen) {
        this.teilnahmen = teilnahmen;
    }
}
