package eu.merscher.lbsvolleyball.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;


public class Spieler implements Parcelable, Comparable<Spieler> {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Spieler createFromParcel(Parcel in) {
            return new Spieler(in);
        }

        public Spieler[] newArray(int size) {
            return new Spieler[size];
        }
    };
    private long u_id;
    private String vname;
    private String name;
    private String bdate;
    private int teilnahmen;
    private String foto;
    private String mail;
    private String hat_buchung_mm;


    public Spieler(long u_id, String name, String vname, String bdate, int teilnahmen, String foto, String mail, String hat_buchung_mm) {
        this.u_id = u_id;
        this.name = name;
        this.vname = vname;
        this.bdate = bdate;
        this.teilnahmen = teilnahmen;
        this.foto = foto;
        this.mail = mail;
        this.hat_buchung_mm = hat_buchung_mm;

    }

    public Spieler(Parcel in) {
        this.u_id = in.readLong();
        this.name = in.readString();
        this.vname = in.readString();
        this.bdate = in.readString();
        this.mail = in.readString();
        this.foto = in.readString();
        this.teilnahmen = in.readInt();
        this.hat_buchung_mm = in.readString();
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

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    @Override
    public int compareTo(Spieler spieler) {
        return this.name.compareTo(spieler.getName());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.u_id);
        dest.writeString(this.name);
        dest.writeString(this.vname);
        dest.writeString(this.bdate);
        dest.writeString(this.mail);
        dest.writeString(this.foto);
        dest.writeInt(this.teilnahmen);
        dest.writeString(this.hat_buchung_mm);
    }

    @Override
    public String toString() {
        return vname + " " + name;
    }

    public class FirstNameSorter implements Comparator<Spieler> {

        @Override
        public int compare(Spieler o1, Spieler o2) {
            return o1.getVname().compareTo(o2.getVname());
        }
    }
}

