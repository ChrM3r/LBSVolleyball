package eu.merscher.lbsvolleyball.model;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

public class Buchung implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Buchung createFromParcel(Parcel in) {
            return new Buchung(in);
        }

        public Buchung[] newArray(int size) {
            return new Buchung[size];
        }
    };
    private long bu_id;
    private long s_id;
    private double bu_btr;
    private double kto_saldo_alt;
    private double kto_saldo_neu;
    private String bu_date;
    private String ist_training_mm;
    private long training_id;
    private String ist_manuell_mm;
    private String ist_tunier_mm;
    private long tunier_id;
    private String ist_geloeschter_spieler_mm;
    private long geloeschter_s_id;

    public Buchung(long bu_nr, long s_id, double bu_btr, double kto_saldo_alt, double kto_saldo_neu, String bu_date, String ist_training_mm, long training_id, String ist_manuell_mm, String ist_tunier_mm, long tunier_id, String ist_geloeschter_spieler_mm, long geloeschter_s_id) {
        this.bu_id = bu_nr;
        this.s_id = s_id;
        this.bu_btr = bu_btr;
        this.kto_saldo_alt = kto_saldo_alt;
        this.kto_saldo_neu = kto_saldo_neu;
        this.bu_date = bu_date;
        this.ist_training_mm = ist_training_mm;
        this.training_id = training_id;
        this.ist_manuell_mm = ist_manuell_mm;
        this.ist_tunier_mm = ist_tunier_mm;
        this.tunier_id = tunier_id;
        this.ist_geloeschter_spieler_mm = ist_geloeschter_spieler_mm;
        this.geloeschter_s_id = geloeschter_s_id;
    }

    public Buchung(Parcel in) {
        this.bu_id = in.readLong();
        this.s_id = in.readLong();
        this.bu_btr = in.readDouble();
        this.kto_saldo_alt = in.readDouble();
        this.kto_saldo_neu = in.readDouble();
        this.bu_date = in.readString();
        this.ist_training_mm = in.readString();
        this.training_id = in.readLong();
        this.ist_manuell_mm = in.readString();
        this.ist_tunier_mm = in.readString();
        this.tunier_id = in.readLong();


    }

    public long getBu_id() {
        return bu_id;
    }

    public long getS_id() {
        return s_id;
    }

    public Double getBu_btr() {
        return bu_btr;
    }

    public Double getKto_saldo_alt() {
        return kto_saldo_alt;
    }

    public Double getKto_saldo_neu() {
        return kto_saldo_neu;
    }

    public String getBu_date() {
        return bu_date;
    }

    public String getIst_training_mm() {
        return ist_training_mm;
    }

    public long getTraining_id() {
        return training_id;
    }

    public String getIst_manuell_mm() {
        return ist_manuell_mm;
    }

    public String getIst_tunier_mm() {
        return ist_tunier_mm;
    }

    public long getTunier_id() {
        return tunier_id;
    }

    public String getIst_geloeschter_spieler_mm() {
        return ist_geloeschter_spieler_mm;
    }

    public Long getGeloeschter_s_id() {
        return geloeschter_s_id;
    }

    @SuppressLint("DefaultLocale")
    @NotNull
    public String toString() {
        return String.format("%s          %.2f€ %.2f€", this.bu_date, this.bu_btr, this.kto_saldo_neu);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.bu_id);
        dest.writeLong(this.s_id);
        dest.writeDouble(this.bu_btr);
        dest.writeDouble(this.kto_saldo_alt);
        dest.writeDouble(this.kto_saldo_neu);
        dest.writeString(this.bu_date);
        dest.writeString(this.ist_training_mm);
        dest.writeLong(this.training_id);
        dest.writeString(this.ist_manuell_mm);
        dest.writeString(this.ist_tunier_mm);
        dest.writeLong(this.tunier_id);

    }
}
