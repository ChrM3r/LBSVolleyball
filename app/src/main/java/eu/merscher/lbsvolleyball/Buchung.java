package eu.merscher.lbsvolleyball;

import android.os.Parcel;
import android.os.Parcelable;

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
    private long u_id;
    private Double bu_btr;
    private Double kto_saldo_alt;
    private Double kto_saldo_neu;
    private String bu_date;

    public Buchung(long bu_nr, long u_id, Double bu_btr, Double kto_saldo_alt, Double kto_saldo_neu, String bu_date) {
        this.bu_id = bu_nr;
        this.u_id = u_id;
        this.bu_btr = bu_btr;
        this.kto_saldo_alt = kto_saldo_alt;
        this.kto_saldo_neu = kto_saldo_neu;
        this.bu_date = bu_date;
    }

    public Buchung(Parcel in) {
        this.bu_id = in.readLong();
        this.u_id = in.readLong();
        this.bu_btr = in.readDouble();
        this.kto_saldo_alt = in.readDouble();
        this.kto_saldo_neu = in.readDouble();
        this.bu_date = in.readString();
    }

    public long getBu_id() {
        return bu_id;
    }

    public void setBu_id(long bu_id) {
        this.bu_id = bu_id;
    }

    public long getU_id() {
        return u_id;
    }

    public void setU_id(long u_id) {
        this.u_id = u_id;
    }

    public Double getBu_btr() {
        return bu_btr;
    }

    public void setBu_btr(Double bu_btr) {
        this.bu_btr = bu_btr;
    }

    public Double getKto_saldo_alt() {
        return kto_saldo_alt;
    }

    public void setKto_saldo_alt(Double kto_saldo_alt) {
        this.kto_saldo_alt = kto_saldo_alt;
    }

    public Double getKto_saldo_neu() {
        return kto_saldo_neu;
    }

    public void setKto_saldo_neu(Double kto_saldo_neu) {
        this.kto_saldo_neu = kto_saldo_neu;
    }

    public String getBu_date() {
        return bu_date;
    }

    public void setBu_date(String bu_date) {
        this.bu_date = bu_date;
    }

    public String toString(Buchung buchung) {
        return (buchung.getBu_date() + "     " + String.format("%.2f", buchung.getBu_btr()) + "€     " + String.format("%.2f", buchung.getKto_saldo_neu()) + "€");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.bu_id);
        dest.writeLong(this.u_id);
        dest.writeDouble(this.bu_btr);
        dest.writeDouble(this.kto_saldo_alt);
        dest.writeDouble(this.kto_saldo_neu);
        dest.writeString(this.bu_date);
    }

    @Override
    public String toString() {
        return (bu_date + "     " + String.format("%.2f", bu_btr) + "€     " + String.format("%.2f", kto_saldo_neu) + "€");
    }
}
