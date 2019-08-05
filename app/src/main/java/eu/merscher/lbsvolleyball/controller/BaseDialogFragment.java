package eu.merscher.lbsvolleyball.controller;

//CopyPaste von stackoverflow https://stackoverflow.com/questions/23408756/create-a-general-class-for-custom-dialog-in-java-android

import android.app.Activity;

import androidx.fragment.app.DialogFragment;

public abstract class BaseDialogFragment<T> extends DialogFragment {
    private T mActivityInstance;

    public final T getActivityInstance() {
        return mActivityInstance;
    }

    @Override
    public void onAttach(Activity activity) {
        mActivityInstance = (T) activity;
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivityInstance = null;
    }
}