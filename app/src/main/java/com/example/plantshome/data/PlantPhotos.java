package com.example.plantshome.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Photos the user adds: one per plant (its "Photo Album") and one profile photo.
 * Stored per account in the app's private files, so other accounts on the phone can't see them.
 */
public final class PlantPhotos {

    private static final String FOLDER = "plant_photos";

    private PlantPhotos() {
    }

    /** The saved photo for a plant (may not exist yet). */
    @NonNull
    public static File photo(@NonNull Context context, @NonNull String uid, int plantIndex) {
        return new File(folder(context, uid), "plant_" + plantIndex + ".jpg");
    }

    /** Where the camera app writes; only replaces the saved photo if the shot succeeds. */
    @NonNull
    public static File pending(@NonNull Context context, @NonNull String uid, int plantIndex) {
        return new File(folder(context, uid), "plant_" + plantIndex + ".pending.jpg");
    }

    @NonNull
    public static File profilePhoto(@NonNull Context context, @NonNull String uid) {
        return new File(folder(context, uid), "profile.jpg");
    }

    @NonNull
    public static Uri shareableUri(@NonNull Context context, @NonNull File file) {
        return FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
    }

    /** Keeps a successful shot as the plant's photo. */
    public static boolean commit(@NonNull Context context, @NonNull String uid, int plantIndex) {
        File pending = pending(context, uid, plantIndex);
        if (!pending.exists() || pending.length() == 0) {
            return false;
        }
        File photo = photo(context, uid, plantIndex);
        if (photo.exists() && !photo.delete()) {
            return false;
        }
        return pending.renameTo(photo);
    }

    public static void discardPending(@NonNull Context context, @NonNull String uid, int plantIndex) {
        File pending = pending(context, uid, plantIndex);
        if (pending.exists()) {
            //noinspection ResultOfMethodCallIgnored
            pending.delete();
        }
    }

    /** Copies a picked image (e.g. from the photo picker) into {@code target}. */
    @WorkerThread
    public static boolean copy(@NonNull Context context, @NonNull Uri source, @NonNull File target) {
        File temp = new File(target.getPath() + ".tmp");
        try (InputStream in = context.getContentResolver().openInputStream(source);
             OutputStream out = new FileOutputStream(temp)) {
            if (in == null) {
                return false;
            }
            byte[] buffer = new byte[16 * 1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        } catch (IOException | SecurityException e) {
            //noinspection ResultOfMethodCallIgnored
            temp.delete();
            return false;
        }
        if (target.exists() && !target.delete()) {
            return false;
        }
        return temp.renameTo(target);
    }

    /** Removes every photo of one account (used when the account is deleted). */
    public static void deleteAll(@NonNull Context context, @NonNull String uid) {
        File folder = folder(context, uid);
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
        }
        //noinspection ResultOfMethodCallIgnored
        folder.delete();
    }

    /**
     * Decodes a photo scaled down to about {@code targetWidth} pixels and turned upright.
     *
     * @return the bitmap, or null if the file is missing or unreadable
     */
    @WorkerThread
    @Nullable
    public static Bitmap load(@NonNull File file, int targetWidth) {
        if (!file.exists()) {
            return null;
        }
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getPath(), bounds);
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize(Math.max(bounds.outWidth, bounds.outHeight), Math.max(targetWidth, 1));
        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath(), options);
        if (bitmap == null) {
            return null;
        }
        int degrees = rotationOf(file);
        if (degrees == 0) {
            return bitmap;
        }
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        Bitmap upright = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (upright != bitmap) {
            bitmap.recycle();
        }
        return upright;
    }

    /** Largest power of two that keeps the image at least {@code target} pixels wide. */
    static int sampleSize(int longestSide, int target) {
        int sample = 1;
        while (longestSide / (sample * 2) >= target) {
            sample *= 2;
        }
        return sample;
    }

    private static int rotationOf(File file) {
        try {
            int orientation = new ExifInterface(file.getPath()).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
                default:
                    return 0;
            }
        } catch (IOException e) {
            return 0;
        }
    }

    private static File folder(Context context, String uid) {
        // The uid is a Firebase id (letters and digits); keep only safe characters anyway.
        String safe = uid.replaceAll("[^A-Za-z0-9_-]", "_");
        File folder = new File(new File(context.getFilesDir(), FOLDER), safe);
        if (!folder.exists()) {
            //noinspection ResultOfMethodCallIgnored
            folder.mkdirs();
        }
        return folder;
    }
}
