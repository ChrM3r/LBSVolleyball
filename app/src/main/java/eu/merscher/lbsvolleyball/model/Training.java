package eu.merscher.lbsvolleyball.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Training implements Parcelable {

    public static final Creator CREATOR = new Creator() {
        public Training createFromParcel(Parcel in) {
            return new Training(in);
        }

        public Training[] newArray(int size) {
            return new Training[size];
        }
    };
    private long db_id;
    private long training_id;
    private String training_dtm;
    private long trainings_ort_id;
    private long trainings_tn;
    private double platzkosten;
    private String ist_kostenlos_mm;


    public Training(long db_id, long training_id, String training_dtm, long trainings_ort_id, long trainings_tn, double platzkosten, String ist_kostenlos_mm) {
        this.db_id = db_id;
        this.training_id = training_id;
        this.training_dtm = training_dtm;
        this.trainings_ort_id = trainings_ort_id;
        this.trainings_tn = trainings_tn;
        this.platzkosten = platzkosten;
        this.ist_kostenlos_mm = ist_kostenlos_mm;

    }

    private Training(Parcel in) {
        this.db_id = in.readLong();
        this.training_id = in.readLong();
        this.training_dtm = in.readString();
        this.trainings_ort_id = in.readLong();
        this.trainings_tn = in.readLong();
        this.platzkosten = in.readDouble();
        this.ist_kostenlos_mm = in.readString();


    }

    public long getDb_id() {
        return db_id;
    }

    public long getTraining_id() {
        return training_id;
    }

    public String getTraining_dtm() {
        return training_dtm;
    }

    public long getTrainings_ort_id() {
        return trainings_ort_id;
    }

    public long getTrainings_tn() {
        return trainings_tn;
    }

    public double getPlatzkosten() {
        return platzkosten;
    }

    public String getIst_kostenlos_mm() {
        return ist_kostenlos_mm;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.db_id);
        dest.writeLong(this.training_id);
        dest.writeString(this.training_dtm);
        dest.writeLong(this.trainings_ort_id);
        dest.writeLong(this.trainings_tn);
        dest.writeDouble(this.platzkosten);
        dest.writeString(this.ist_kostenlos_mm);
    }
}
