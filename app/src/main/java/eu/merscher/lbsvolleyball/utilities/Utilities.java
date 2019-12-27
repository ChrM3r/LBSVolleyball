package eu.merscher.lbsvolleyball.utilities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.EditText;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import de.siegmar.fastcsv.reader.CsvParser;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import de.siegmar.fastcsv.writer.CsvAppender;
import de.siegmar.fastcsv.writer.CsvWriter;
import eu.merscher.lbsvolleyball.model.Spieler;
import eu.merscher.lbsvolleyball.model.Trainingsort;

public class Utilities {



    public static void formatNumericEditText(EditText editText) {
        String s = null;

        if (!editText.getText().toString().isEmpty())
            s = editText.getText().toString();

        if (s != null) {
            if (s.contains(",") && s.charAt(0) != ',' && s.charAt(s.length() - 1) != ',') {
                String[] split = s.split(",");

                if (split[1].length() == 1)
                    s += "0";
                else if (split[1].length() == 0)
                    s += "00";

            } else if (s.charAt(0) == ',')
                s = "0,00";
            else if (s.charAt(s.length() - 1) == ',')
                s += "00";
            else
                s += ",00";

            editText.setText(s);
        }
    }

    /**
     * This method is responsible for solving the rotation issue if exist. Also scale the images to
     * 2048x2048 resolution
     *
     * @param context       The current context
     * @param selectedImage The Image URI
     * @return Bitmap image results
     */
    public static Bitmap handleSamplingAndRotationBitmap(Context context, Uri selectedImage)
            throws IOException {
        int MAX_HEIGHT = 1536;
        int MAX_WIDTH = 1536;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        assert imageStream != null;
        imageStream.close();

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImage);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        img = rotateImageIfRequired(context, img, selectedImage);
        return img;
    }

    /**
     * Calculate an inSampleSize for use in a {@link BitmapFactory.Options} object when decoding
     * bitmaps using the decode* methods from {@link BitmapFactory}. This implementation calculates
     * the closest inSampleSize that will result in the final decoded bitmap having a width and
     * height equal to or larger than the requested width and height. This implementation does not
     * ensure a power of 2 is returned for inSampleSize which can be faster when decoding but
     * results in a larger bitmap which isn't as useful for caching purposes.
     *
     * @param options   An options object with out* params already populated (run through a decode*
     *                  method with inJustDecodeBounds==true
     * @param reqWidth  The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return The value to be used for inSampleSize
     */
    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    /**
     * Rotate an image if required.
     *
     * @param img           The image bitmap
     * @param selectedImage Image URI
     * @return The resulted Bitmap after manipulation
     */
    private static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {

        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(Objects.requireNonNull(input));
        else
            ei = new ExifInterface(Objects.requireNonNull(selectedImage.getPath()));

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    //Bilder von der Kamera richtig rotieren

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }

    public static String bildSpeichern(Context context, Bitmap bitmap) {

        ContextWrapper cw = new ContextWrapper(context);
        File ordner = cw.getDir("profilbilder", Context.MODE_PRIVATE);

        File pfad = new File(ordner, "temp.png");
        File pfad_klein = new File(ordner, "temp_klein.png");

        FileOutputStream fos;
        FileOutputStream fos_klein;
        try {
            fos = new FileOutputStream(pfad);
            fos_klein = new FileOutputStream(pfad_klein);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            bitmap = scaleToFitWidth(bitmap, 200);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos_klein);
            fos_klein.close();

        } catch (Exception e) {
            Log.d("BILD_SPEICHERN", e.getMessage(), e);
        }
        return pfad.getAbsolutePath();
    }

    public static String bildTrainingsortSpeichern(Context context, Bitmap bitmap) {

        ContextWrapper cw = new ContextWrapper(context);
        File ordner = cw.getDir("trainingsortbilder", Context.MODE_PRIVATE);

        File pfad = new File(ordner, "temp.png");
        File pfad_klein = new File(ordner, "temp_klein.png");

        FileOutputStream fos;
        FileOutputStream fos_klein;
        try {
            fos = new FileOutputStream(pfad);
            fos_klein = new FileOutputStream(pfad_klein);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            bitmap = scaleToFitWidth(bitmap, 200);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos_klein);
            fos_klein.close();

        } catch (Exception e) {
            Log.d("BILD_SPEICHERN", e.getMessage(), e);
        }
        return pfad.getAbsolutePath();
    }

    public static String bildNachSpielerBenennen(Context context, Spieler spieler) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("profilbilder", Context.MODE_PRIVATE);

        File bildAlt = new File(spieler.getFoto());
        File bildAlt_klein = new File(spieler.getFoto().replace(".png", "_klein.png"));

        File bildNeu = new File(directory, spieler.getS_id() + "_" + spieler.getName() + ".png");
        File bildNeu_klein = new File(directory, spieler.getS_id() + "_" + spieler.getName() + "_klein.png");


        boolean umbenannt;
        umbenannt = bildAlt.renameTo(bildNeu);
        Log.d("S Bild gr umbennant:", Boolean.toString(umbenannt));
        umbenannt = bildAlt_klein.renameTo(bildNeu_klein);
        Log.d("S Bild kl umbennant:", Boolean.toString(umbenannt));

        return bildNeu.getAbsolutePath();
    }

    public static String bildNachTrainingsortBenennen(Context context, Trainingsort trainingsort) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("trainingsortbilder", Context.MODE_PRIVATE);

        File bildAlt = new File(trainingsort.getFoto());
        File bildAlt_klein = new File(trainingsort.getFoto().replace(".png", "_klein.png"));

        File bildNeu = new File(directory, trainingsort.getTo_id() + "_" + trainingsort.getName().replace(" ", "_") + ".png");
        File bildNeu_klein = new File(directory, trainingsort.getTo_id() + "_" + trainingsort.getName().replace(" ", "_") + "_klein.png");


        boolean umbenannt;
        umbenannt = bildAlt.renameTo(bildNeu);
        Log.d("TO Bild gr umbennant:", Boolean.toString(umbenannt));
        umbenannt = bildAlt_klein.renameTo(bildNeu_klein);
        Log.d("TO Bild kl umbennant:", Boolean.toString(umbenannt));

        return bildNeu.getAbsolutePath();
    }

    public static String bildNachNamensaenderungBenennen(Context context, Spieler spielerAlt, Spieler spielerNeu) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("profilbilder", Context.MODE_PRIVATE);

        File bildAlt = new File(spielerAlt.getFoto());
        File bildAlt_klein = new File(spielerAlt.getFoto().replace(".png", "_klein.png"));

        File bildNeu = new File(directory, spielerNeu.getS_id() + "_" + spielerNeu.getName() + ".png");
        File bildNeu_klein = new File(directory, spielerNeu.getS_id() + "_" + spielerNeu.getName() + "_klein.png");


        boolean umbenannt;
        umbenannt = bildAlt.renameTo(bildNeu);
        Log.d("S Bild gr umbennant:", Boolean.toString(umbenannt));
        umbenannt = bildAlt_klein.renameTo(bildNeu_klein);
        Log.d("S Bild kl umbennant:", Boolean.toString(umbenannt));

        return bildNeu.getAbsolutePath();
    }

    public static String bildNachTrainingsortaenderungBenennen(Context context, Trainingsort trainingsortAlt, Trainingsort trainingsortNeu) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("trainingsortbilder", Context.MODE_PRIVATE);

        File bildAlt = new File(trainingsortAlt.getFoto());
        File bildAlt_klein = new File(trainingsortAlt.getFoto().replace(".png", "_klein.png"));

        File bildNeu = new File(directory, trainingsortNeu.getTo_id() + "_" + trainingsortNeu.getName().replace(" ", "_") + ".png");
        File bildNeu_klein = new File(directory, trainingsortNeu.getTo_id() + "_" + trainingsortNeu.getName().replace(" ", "_") + "_klein.png");


        boolean umbenannt;
        umbenannt = bildAlt.renameTo(bildNeu);
        Log.d("TO Bild gr umbennant:", Boolean.toString(umbenannt));
        umbenannt = bildAlt_klein.renameTo(bildNeu_klein);
        Log.d("TO Bild kl umbennant:", Boolean.toString(umbenannt));

        return bildNeu.getAbsolutePath();
    }

    public static void csvExport(ArrayList<String> list) {

        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();

        File file = new File(baseDir, "spieler_export.csv");
        CsvWriter csvWriter = new CsvWriter();

        try (CsvAppender csvAppender = csvWriter.append(file, StandardCharsets.UTF_8)) {
            // header
            for (String string : list) {
                csvAppender.appendLine(string);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> csvImport() {

        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();

        File file = new File(baseDir, "spieler_export.csv");

        CsvReader csvReader = new CsvReader();
        ArrayList<String> spielerListString = new ArrayList<>();

        try (CsvParser csvParser = csvReader.parse(file, StandardCharsets.UTF_8)) {
            CsvRow row;
            while ((row = csvParser.nextRow()) != null) {

                List<String> list = row.getFields();
                spielerListString.addAll(list);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return spielerListString;
    }
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


    //https://stackoverflow.com/questions/3574644/how-can-i-find-the-latitude-and-longitude-from-address/27834110#27834110
    public static LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address.size() < 1) {
                p1 = new LatLng(-999, -999);
                return p1;
            } else {

                Address location = address.get(0);
                p1 = new LatLng(location.getLatitude(), location.getLongitude());
            }

        } catch (IOException ex) {

            ex.printStackTrace();
            p1 = new LatLng(-999, -999);
            return p1;
        }

        return p1;
    }

    public static Bitmap getMapBildAusURL(double lat, double lon, int width, int height) {

        Bitmap img;

        try {
            String urlString = "http://maps.google.com/maps/api/staticmap";
            urlString += "?zoom=15&size=" + width + "x" + height;
            urlString += "&maptype=roadmap";
            urlString += "&markers=color:red|label:A|" + lat + "," + lon;
            urlString += "&sensor=true";
            urlString += "&key=AIzaSyBZNYEUwq0cg9s6jxKtKWwENI1wyvF977k";
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream is = connection.getInputStream();
            img = BitmapFactory.decodeStream(is);

        } catch (IOException e) {

            e.printStackTrace();
            img = null;
        }
        return img;
    }

    // Scale and maintain aspect ratio given a desired width
    public static Bitmap scaleToFitWidth(Bitmap b, int width) {
        float factor = width / (float) b.getWidth();
        return Bitmap.createScaledBitmap(b, width, (int) (b.getHeight() * factor), true);
    }


    // Scale and maintain aspect ratio given a desired height
    public static Bitmap scaleToFitHeight(Bitmap b, int height) {
        float factor = height / (float) b.getHeight();
        return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factor), height, true);
    }

    public static void berechtigungenPruefen(Activity context) {

        final WeakReference<Activity> activityReference;
        activityReference = new WeakReference<>(context);
        Activity activity = activityReference.get();


        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (!ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        0);
            }

            if (ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // Should we show an explanation?
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            0);
                }
            }
            if (ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.ACCESS_NETWORK_STATE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // Should we show an explanation?
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        Manifest.permission.ACCESS_NETWORK_STATE)) {

                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                            0);
                }
            }
            if (ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.INTERNET)
                    != PackageManager.PERMISSION_GRANTED) {

                // Permission is not granted
                // Should we show an explanation?
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        Manifest.permission.INTERNET)) {

                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.INTERNET},
                            0);
                }
            }
        }
    }
}



