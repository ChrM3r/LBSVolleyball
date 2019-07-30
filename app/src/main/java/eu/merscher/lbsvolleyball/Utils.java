package eu.merscher.lbsvolleyball;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;

import java.util.Comparator;

class Utils {


    public class SortName implements Comparator<Spieler> {
        @Override
        public int compare(Spieler s1, Spieler s2) {
            return s1.getName().compareTo(s2.getName());
        }
    }

    public class SortVname implements Comparator<Spieler> {
        @Override
        public int compare(Spieler s1, Spieler s2) {
            return s1.getVname().compareTo(s2.getVname());
        }
    }

    public class SortTeilnahmen implements Comparator<Spieler> {
        @Override
        public int compare(Spieler s1, Spieler s2) {
            return s1.getTeilnahmen() - s2.getTeilnahmen();
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
}
