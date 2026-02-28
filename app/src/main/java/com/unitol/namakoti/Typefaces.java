package com.unitol.namakoti;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.Hashtable;

public class Typefaces {
    public static String TAG = Typefaces.class.getSimpleName();

    private static final Hashtable<Integer, Typeface> cache = new Hashtable<Integer, Typeface>();

    @Nullable
    public static Typeface get(Context c, int type, String ttfFile) {
        synchronized (cache) {
            if (!cache.containsKey(type)) {
                try {
//				String ttfFile = "fonts/engravers-gothic-bt.ttf";
                    Typeface t = Typeface.createFromAsset(c.getResources().getAssets(), ttfFile);
                    cache.put(type, t);
                } catch (Exception e) {
                    Log.e(TAG, "Could not get typeface '" + c.getAssets() + "' because " + e.getMessage());
                    return null;
                }
            }
            return cache.get(type);
        }
    }

    @Nullable
    public static Typeface getLanguageFont(Context c, String language) {
        try {
            String ttf = "fonts/";
            if (language.equalsIgnoreCase("AS")) {
                ttf = ttf + "f1-Lohit-Assamese.ttf";
            } else if (language.equalsIgnoreCase("GJ")) {
                ttf = ttf + "f2-Lohit-Gujarathi.ttf";
            } else if (language.equalsIgnoreCase("KS")) {
                ttf = ttf + "f3-Lohit-Kashmiri.ttf";
            } else if (language.equalsIgnoreCase("MR")) {
                ttf = ttf + "f4-Lohit-Marathi.ttf";
            } else if (language.equalsIgnoreCase("NE")) {
                ttf = ttf + "f5-Lohit-Nepali.ttf";
            } else if (language.equalsIgnoreCase("OR")) {
                ttf = ttf + "f6-Lohit-Oriya.ttf";
            }  else if (language.equalsIgnoreCase("PA")) {
                ttf = ttf + "f7-Lohit-Punjabi.ttf";
            } else if (language.equalsIgnoreCase("SD")) {
                ttf = ttf + "f8-Lohit-Sindhi.ttf";
            } else if (language.equalsIgnoreCase("TL")) {
                ttf = ttf + "f9-Lohit-Telugu.ttf";
            } else if (language.equalsIgnoreCase("TM")) {
                ttf = ttf + "f10-Lohit-Tamil.ttf";
            } else if (language.equalsIgnoreCase("BN")) {
                ttf = ttf + "f11-Lohit-Bengali.ttf";
            } else if (language.equalsIgnoreCase("KN")) {
                ttf = ttf + "f12-Lohit-Kannada.ttf";
            }else if (language.equalsIgnoreCase("HI")) {
                ttf = ttf + "f13-Lohit-Hindi.ttf";
            } else if (language.equalsIgnoreCase("ML")) {
                ttf = ttf + "f14-Lohit-Malayalam.ttf";
            }else {
                ttf = ttf + "Nokora.ttf";
            }
            return Typeface.createFromAsset(c.getAssets(), ttf);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}