package eu.merscher.lbsvolleyball.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;


public class Spieler implements Parcelable, Comparable<Spieler> {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Spieler createFromParcel(Parcel in) {
            return new Spieler(in);
        }

        public Spieler[] newArray(int size) {
            return new Spieler[size];
        }
    };
    private long s_id;
    private String vname;
    private String name;
    private String bdate;
    private int teilnahmen;
    private String foto;
    private String mail;
    private String hat_buchung_mm;


    public Spieler(long s_id, String name, String vname, String bdate, int teilnahmen, String foto, String mail, String hat_buchung_mm) {
        this.s_id = s_id;
        this.name = name;
        this.vname = vname;
        this.bdate = bdate;
        this.teilnahmen = teilnahmen;
        this.foto = foto;
        this.mail = mail;
        this.hat_buchung_mm = hat_buchung_mm;

    }

    public Spieler(Parcel in) {
        this.s_id = in.readLong();
        this.name = in.readString();
        this.vname = in.readString();
        this.bdate = in.readString();
        this.mail = in.readString();
        this.foto = in.readString();
        this.teilnahmen = in.readInt();
        this.hat_buchung_mm = in.readString();
    }

    public long getS_id() {
        return s_id;
    }

    public String getName() {
        return name;
    }

    public String getVname() {
        return vname;
    }

    public String getBdate() {
        return bdate;
    }

    public int getTeilnahmen() {
        return teilnahmen;
    }

    public String getFoto() {
        return foto;
    }

    public String getHat_buchung_mm() {
        return hat_buchung_mm;
    }

    public String getMail() {
        return mail;
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
        dest.writeLong(this.s_id);
        dest.writeString(this.name);
        dest.writeString(this.vname);
        dest.writeString(this.bdate);
        dest.writeString(this.mail);
        dest.writeString(this.foto);
        dest.writeInt(this.teilnahmen);
        dest.writeString(this.hat_buchung_mm);
    }

    @NotNull
    @Override
    public String toString() {
        return vname + " " + name;
    }

}

