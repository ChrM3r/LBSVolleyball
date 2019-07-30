package eu.merscher.lbsvolleyball;

import java.io.Serializable;

public class Spieler implements Serializable {


    private long u_id;
    private String vname;
    private String name;
    private String bdate;
    private int teilnahmen;
    private String foto;
    private String hat_buchung_mm;


    public Spieler(long u_id, String name, String vname, String bdate, int teilnahmen, String foto, String hat_buchung_mm) {
        this.u_id = u_id;
        this.name = name;
        this.vname = vname;
        this.bdate = bdate;
        this.teilnahmen = teilnahmen;
        this.foto = foto;
        this.hat_buchung_mm = hat_buchung_mm;

    }

    public long getU_id() {
        return u_id;
    }

    public void setU_id(long u_id) {
        this.u_id = u_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVname() {
        return vname;
    }

    public void setVname(String vname) {
        this.vname = vname;
    }

    public String getBdate() {
        return bdate;
    }

    public void setBdate(String bdate) {
        this.bdate = bdate;
    }

    public int getTeilnahmen() {
        return teilnahmen;
    }

    public void setTeilnahmen(int teilnahmen) {
        this.teilnahmen = teilnahmen;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getHat_buchung_mm() {
        return hat_buchung_mm;
    }

    public void setHat_buchung_mm(String hat_buchung_mm) {
        this.hat_buchung_mm = hat_buchung_mm;
    }

    @Override
    public String toString() {
        return vname + " " + name;
    }
}

