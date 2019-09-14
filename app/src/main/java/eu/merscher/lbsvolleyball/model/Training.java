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
    private String trainings_ort;
    private long trainings_tn;
    private String ist_kostenlos_mm;


    public Training(long db_id, long training_id, String training_dtm, String trainings_ort, long trainings_tn, String ist_kostenlos_mm) {
        this.db_id = db_id;
        this.training_id = training_id;
        this.training_dtm = training_dtm;
        this.trainings_ort = trainings_ort;
        this.trainings_tn = trainings_tn;
        this.ist_kostenlos_mm = ist_kostenlos_mm;

    }

    public Training(Parcel in) {
        this.db_id = in.readLong();
        this.training_id = in.readLong();
        this.training_dtm = in.readString();
        this.trainings_ort = in.readString();
        this.trainings_tn = in.readLong();
        this.ist_kostenlos_mm = in.readString();


    }

    public long getDb_id() {
        return db_id;
    }

    public void setDb_id(long db_id) {
        this.db_id = db_id;
    }

    public long getTraining_id() {
        return training_id;
    }

    public void setTraining_id(long training_id) {
        this.training_id = training_id;
    }

    public String getTraining_dtm() {
        return training_dtm;
    }

    public void setTraining_dtm(String training_dtm) {
        this.training_dtm = training_dtm;
    }

    public String getTrainings_ort() {
        return trainings_ort;
    }

    public void setTrainings_ort(String trainings_ort) {
        this.trainings_ort = trainings_ort;
    }

    public long getTrainings_tn() {
        return trainings_tn;
    }

    public void setTrainings_tn(long trainings_tn) {
        this.trainings_tn = trainings_tn;
    }

    public String getIst_kostenlos_mm() {
        return ist_kostenlos_mm;
    }

    public void setIst_kostenlos_mm(String ist_kostenlos_mm) {
        this.ist_kostenlos_mm = ist_kostenlos_mm;
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
        dest.writeString(this.trainings_ort);
        dest.writeLong(this.trainings_tn);
        dest.writeString(this.ist_kostenlos_mm);
    }
}
