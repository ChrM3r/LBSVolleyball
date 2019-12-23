package eu.merscher.lbsvolleyball.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Trainingsort implements Parcelable, Comparable<Trainingsort> {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {

        public Trainingsort createFromParcel(Parcel in) {
            return new Trainingsort(in);
        }

        public Trainingsort[] newArray(int size) {
            return new Trainingsort[size];
        }
    };

    private long to_id;
    private String name;
    private String strasse;
    private String plz;
    private String ort;
    private String foto;
    private double latitude;
    private double longitude;
    private int besuche;


    public Trainingsort(long to_id, String name, String strasse, String plz, String ort, String foto, double latitude, double longitude, int besuche) {
        this.to_id = to_id;
        this.name = name;
        this.strasse = strasse;
        this.plz = plz;
        this.ort = ort;
        this.foto = foto;
        this.latitude = latitude;
        this.longitude = longitude;
        this.besuche = besuche;
    }

    public Trainingsort(Parcel in) {
        this.to_id = in.readLong();
        this.name = in.readString();
        this.strasse = in.readString();
        this.plz = in.readString();
        this.ort = in.readString();
        this.foto = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.besuche = in.readInt();
    }

    public long getTo_id() {
        return to_id;
    }

    public String getName() {
        return name;
    }

    public String getStrasse() {
        return strasse;
    }

    public String getPlz() {
        return plz;
    }

    public String getOrt() {
        return ort;
    }

    public String getFoto() {
        return foto;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getBesuche() {
        return besuche;
    }

    @Override
    public int compareTo(Trainingsort trainingsort) {
        return this.name.compareTo(trainingsort.getName());
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.to_id);
        dest.writeString(this.name);
        dest.writeString(this.strasse);
        dest.writeString(this.plz);
        dest.writeString(this.ort);
        dest.writeString(this.foto);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeInt(this.besuche);
    }
}
