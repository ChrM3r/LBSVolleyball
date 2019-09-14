package eu.merscher.lbsvolleyball.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import de.siegmar.fastcsv.reader.CsvParser;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;
import de.siegmar.fastcsv.writer.CsvAppender;
import de.siegmar.fastcsv.writer.CsvWriter;
import eu.merscher.lbsvolleyball.model.Spieler;

public class Utilities {


    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }

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
     * @throws IOException
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
            ei = new ExifInterface(input);
        else
            ei = new ExifInterface(selectedImage.getPath());

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

        FileOutputStream fos = null;
        FileOutputStream fos_klein = null;
        try {
            fos = new FileOutputStream(pfad);
            fos_klein = new FileOutputStream(pfad_klein);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            bitmap = BitmapScaler.scaleToFitWidth(bitmap, 200);
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

        File bildNeu = new File(directory, spieler.getU_id() + "_" + spieler.getName() + ".png");
        File bildNeu_klein = new File(directory, spieler.getU_id() + "_" + spieler.getName() + "_klein.png");


        boolean umbenannt = bildAlt.renameTo(bildNeu);
        boolean umbenannt_klein = bildAlt_klein.renameTo(bildNeu_klein);
        System.out.println(umbenannt);
        System.out.println(umbenannt_klein);

        return bildNeu.getAbsolutePath();
    }

    public static String bildNachNamensaenderungBenennen(Context context, Spieler spielerAlt, Spieler spielerNeu) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("profilbilder", Context.MODE_PRIVATE);

        File bildAlt = new File(spielerAlt.getFoto());
        File bildAlt_klein = new File(spielerAlt.getFoto().replace(".png", "_klein.png"));

        File bildNeu = new File(directory, spielerNeu.getU_id() + "_" + spielerNeu.getName() + ".png");
        File bildNeu_klein = new File(directory, spielerNeu.getU_id() + "_" + spielerNeu.getName() + "_klein.png");


        boolean umbenannt = bildAlt.renameTo(bildNeu);
        boolean umbenannt_klein = bildAlt_klein.renameTo(bildNeu_klein);

        return bildNeu.getAbsolutePath();
    }

    public static void csvExport(ArrayList<String> list, Context context) {

        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("export", Context.MODE_PRIVATE);
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

    public static ArrayList<String> csvImport(Context context) {

        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("export", Context.MODE_PRIVATE);
        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();

        File file = new File(baseDir, "spieler_export.csv");

        CsvReader csvReader = new CsvReader();
        ArrayList<String> spielerListString = new ArrayList<>();

        try (CsvParser csvParser = csvReader.parse(file, StandardCharsets.UTF_8)) {
            CsvRow row;
            while ((row = csvParser.nextRow()) != null) {

                List<String> list = row.getFields();
                for (String s : list) {
                    spielerListString.add(s);
                }
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

}
