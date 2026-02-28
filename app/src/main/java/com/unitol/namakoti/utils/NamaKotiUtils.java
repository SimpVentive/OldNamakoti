package com.unitol.namakoti.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;

import com.unitol.namakoti.MainActivity;
import com.unitol.namakoti.MyApplication;
import com.unitol.namakoti.R;
import com.unitol.namakoti.SaveNamaFragment;
import com.unitol.namakoti.SetNamaFragment;
import com.unitol.namakoti.web.Container;
import com.unitol.namakoti.web.DeleteAccountAsync;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 * @author Gangadhar
 */

public class NamaKotiUtils {

    private static final String TAG = "NamaKotiUtils";

    /**
     * key is formed userID+namaObject.getString("user_namakoti_id")+getResources().getString(R.string.pref_namas_total_count_key);
     * and for running count ===>
     * userID+namaObject.getString("user_namakoti_id")+getResources().getString(R.string.pref_namas_running_count_key);
     */
    public static void setCountForNama(String key, int count) {
        MyApplication.mEditor.putInt(key, count).commit();
    }

    /**
     * key is formed userID+namaObject.getString("user_namakoti_id")+getResources().getString(R.string.pref_namas_total_count_key);
     * and for running count ===>
     * userID+namaObject.getString("user_namakoti_id")+getResources().getString(R.string.pref_namas_running_count_key);
     */
    public static int getCountForNama(String key) {
        return MyApplication.mPref.getInt(key, 0);
    }

    /* Get bitmap with drawable */
    public static Bitmap getBitMapByDrawable(Context ctxt, Integer defaultResource) {
        Bitmap img = null;

        BitmapDrawable drawableBitmap = ((BitmapDrawable) ctxt.getResources().getDrawable(defaultResource));
        if (drawableBitmap != null) {
            img = drawableBitmap.getBitmap();
        }
        return img;
    }


    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }


    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }


    public static void enableWifiSettings(final Context ctx) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        TextView lDialogTextView = new TextView(ctx);
        lDialogTextView.setAutoLinkMask(0x0f/*all*/);
        lDialogTextView.setPadding(10, 10, 10, 10);
        lDialogTextView.setText(ctx.getString(R.string.wifi_text));
        builder.setCustomTitle(lDialogTextView)
                .setCancelable(false)
                .setPositiveButton(ctx.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ctx.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                }).setNeutralButton(ctx.getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }


    /**
     * Show message with dialog
     */
    public static void showDialog(final Activity context, final String title, final String msg) {

        Log.i(TAG, "showDialog: ");
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setIcon(R.drawable.alert_dialog_icon);
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.setNegativeButton("Cancel", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // context.finish();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        if (context != null && !context.isFinishing() && !alert.isShowing()) {
            alert.show();
        }
    }

    /**
     * Show message with dialog
     */
    public static void showUpgradeDialog(final Container context, final String title, final String msg, final String positive, final String negative) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setIcon(R.drawable.alert_dialog_icon);
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setPositiveButton(positive,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //(context).setCurrentTab(MainActivityPhone.UPGRADE_TAB);
                    }
                });

        alertDialog.setNegativeButton(negative, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //(context).setCurrentTab(MainActivityPhone.SETTINGS_TAB);
//					context.pushFragments(ConstantsManager.TAB_3, new HomeFragment(), false, true, null);
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        if (context != null && !context.isFinishing()) {
            alert.show();
        }
    }

    /**
     * Show message with dialog
     */
    public static void showUpgradeDialogForMoreGods(final Activity context, final String title, final String msg, final String positive, final String negative) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setIcon(R.drawable.alert_dialog_icon);
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setPositiveButton(positive,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ((Container) context).manageFragments(ConstantsManager.TAB_5);
                    }
                });

        alertDialog.setNegativeButton(negative, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
//					context.finish();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        if (context != null && !context.isFinishing()) {
            alert.show();
        }
    }

    /**
     * Show message with dialog
     */
    public static void showSetNamaDialog(final Activity context, final String title, final String msg, final String positive, final String negative) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setIcon(R.drawable.alert_dialog_icon);
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setPositiveButton(positive,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //((MainActivityPhone) context).setCurrentTab(MainActivityPhone.SETTINGS_TAB);
                        ((Container) context).pushFragments(ConstantsManager.TAB_3, new SetNamaFragment(), false, true, null);

                    }
                });

        AlertDialog alert = alertDialog.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        if (context != null && !context.isFinishing()) {
            alert.show();
        }
    }

    /**
     * Show message with dialog
     */
    public static void showNoNamaDialog(final Activity context, final String title, final String msg, final String positive, final String negative) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setIcon(R.drawable.alert_dialog_icon);
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setPositiveButton(positive,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alert = alertDialog.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        if (context != null && !context.isFinishing()) {
            alert.show();
        }
    }

    public static int pixelsToDpi(Resources res, int pixels) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) pixels, res.getDisplayMetrics());
    }

    //for cheching device is tab or phone
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    //	custom image rotation
    public static void addAnimationFrameCount(Animation mRotateAnim) {
        mRotateAnim.setDuration(1000);
        mRotateAnim.setInterpolator(new Interpolator() {
            private final int frameCount = 12;

            @Override
            public float getInterpolation(float input) {
                return (float) Math.floor(input * frameCount) / frameCount;
            }
        });
    }

    public static void setSlowAnimationFrameCount(Animation mRotateAnim) {
        mRotateAnim.setDuration(1000);
        mRotateAnim.setInterpolator(new Interpolator() {
            private final int frameCount = 6;

            @Override
            public float getInterpolation(float input) {
                return (float) Math.floor(input * frameCount) / frameCount;
            }
        });
    }

    //play alarm tone
    public static void playAlarmTone(Context context, MediaPlayer mMediaPlayer) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            mMediaPlayer.setDataSource(context, notification);
            final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

            if (audioManager.getStreamVolume(AudioManager.STREAM_RING) != 0 && !mMediaPlayer.isPlaying()) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
                mMediaPlayer.setLooping(true);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }

        } catch (Exception e) {
        }
    }


    //	to get display sizes
    public static DisplayMetrics getDisplayMetrics(Activity act) {
        DisplayMetrics dm = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    public static BitmapDrawable decodeBitmap(Resources res, int resId, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return new BitmapDrawable(res, BitmapFactory.decodeResource(res, resId, options));
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height;
            final int halfWidth = width;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static boolean isLogin(Context context) {
        Log.e(TAG, "isLogin=============================");
        SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(context);
        if (mPref != null) {
            return mPref.getBoolean(context.getResources().getString(R.string.pref_login_flag_key, ""), false);
        }
        return false;
    }


    public static HttpClient getHttpclient() {
        HttpParams httpParameters = new BasicHttpParams();
        int timeoutConnection = 20000;
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
        int timeoutSocket = 20000;
        HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
        return new DefaultHttpClient(httpParameters);
    }
		
		/*// For no wifi, enable WiFi settings
		public static void enableWifiSettings(final Activity ctx) {    	
			AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
			TextView lDialogTextView = new TextView(ctx);
			lDialogTextView.setAutoLinkMask(0x0fall);
			lDialogTextView.setPadding(10, 10, 10, 10);
			lDialogTextView.setText(ctx.getString(R.string.wifi_text));
			builder.setCustomTitle(lDialogTextView).setCancelable(false)
			.setPositiveButton(ctx.getString(R.string.ok), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					ctx.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));	
				}
			}).setNeutralButton(ctx.getString(R.string.no), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
			});
			builder.create().show();
		}*/

    /**
     * Show message with dialog
     *
     * @param lastChangedTab
     */
    public static void enableWifiSettings(final Activity ctx) {
        AlertDialog.Builder alertDialog = null;

        alertDialog = new AlertDialog.Builder(ctx);

        alertDialog.setIcon(R.drawable.alert_dialog_icon);
        alertDialog.setTitle(ctx.getString(R.string.wifi_text));
        alertDialog.setMessage("You are not Connected to the Internet. You need to connect to continue");
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ctx.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                });

        alertDialog.setNegativeButton("Cancel", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    // Take3 custom Dialog
		/*private static void customTake3Dialog(Activity act, boolean isShown){
			final Dialog  dialog = new Dialog(act);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	 		dialog.setContentView(R.layout.dialog);
			dialog.setCanceledOnTouchOutside(false);
			dialog.setCancelable(false);	
			
			Window window = dialog.getWindow();
			window.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
			if(isShown && act != null && !act.isFinishing()){
				dialog.show();
			}
				
			Button okBtn = (Button) dialog.findViewById(R.id.ok_btn);
			okBtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if(dialog != null && dialog.isShowing()){
						dialog.dismiss();
					}
					}
			});
		}*/

    public static void showAlertDialog(Context c, String message) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(c);
        alertDialog.setTitle("Alert");
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.create().show();
    }

    public static void showHelpDialog(Context c, String hdr, String message) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(c);
        alertDialog.setTitle(hdr);
        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.create().show();
    }

    /**
     * Show message with dialog
     *
     * @param lastChangedTab
     */
    public static void showSignOutDialog(final Activity ctx, final String title, final String msg, final int lastChangedTab) {
        AlertDialog.Builder alertDialog = null;

        alertDialog = new AlertDialog.Builder(ctx);

        alertDialog.setIcon(R.drawable.alert_dialog_icon);
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setPositiveButton("Logout",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        MyApplication.mEditor.putBoolean(ctx.getResources().getString(R.string.pref_login_flag_key, ""), false).commit();
                        MyApplication.mEditor.remove(ctx.getResources().getString(R.string.pref_dispatches_key));
                        MyApplication.mEditor.remove(ctx.getResources().getString(R.string.pref_temples_key));
                        MyApplication.mEditor.remove(ctx.getResources().getString(R.string.pref_fullname_key));
                        MyApplication.mEditor.remove(ctx.getResources().getString(R.string.pref_gods_key));
                        MyApplication.mEditor.remove(ctx.getResources().getString(R.string.pref_edit_profile_key));
                        ctx.startActivity(new Intent(ctx, MainActivity.class));
                        ctx.finish();
                    }
                });

        alertDialog.setNegativeButton("Return to home screen", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
					/*if (lastChangedTab != -1) {
						((MainActivityPhone)ctx).setCurrentTab(lastChangedTab);
					}*/
                ctx.finish();
            }
        });

        alertDialog.setNeutralButton("Cancel", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
//                if (lastChangedTab != -1) {
//                    ((MainActivityPhone) ctx).setCurrentTab(lastChangedTab);
//                }
//					ctx.finish();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }


    /**
     * Show message with dialog
     *
     * @param lastChangedTab
     */
    public static void showDeleteAccountDialog(final Activity ctx, final String title, final String msg, final int lastChangedTab) {
        AlertDialog.Builder alertDialog = null;

        alertDialog = new AlertDialog.Builder(ctx);

        alertDialog.setIcon(R.drawable.alert_dialog_icon);
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setPositiveButton("Delete",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        DeleteAccountAsync deleteAccountAsync = new DeleteAccountAsync(ctx);
                        deleteAccountAsync.execute();
                    }
                });

        alertDialog.setNegativeButton("Return to Home", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ctx.finish();
            }
        });

        alertDialog.setNeutralButton("Cancel", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    /**
     * Show message with dialog
     *
     * @param b
     * @param lastChangedTab
     */
    public static void showPrintingServiceDialog(final Activity ctx, final String title, final String msg, final Bundle b) {
        AlertDialog.Builder alertDialog = null;

        alertDialog = new AlertDialog.Builder(ctx);

        alertDialog.setIcon(R.drawable.alert_dialog_icon);
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setPositiveButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
//							((MainActivityPhone) ctx).onBackPressed();
                    }
                });

        alertDialog.setNegativeButton("Continue", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ((Container) ctx).pushFragments(ConstantsManager.TAB_3, new SaveNamaFragment(), false, true, b);
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCancelable(false);
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager conMan;
        boolean isWifi = false;
        boolean is3g = false;

        conMan = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        isWifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        if (conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null) {
            is3g = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        }
        Log.i(TAG, "<======== isNetworkAvailable: ========>");
        Log.i(TAG, "is3g: " + is3g + " , isWifi: " + isWifi);
        if (is3g || isWifi) {
            return true;
        }
        return false;
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     *
     * @return
     */
	    /*private static void generateNotification(Context context, String message) {
	    
	    	Uri uri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.loud);
	    	Log.d(TAG, " Sound URI :" + uri);
	    	
	        NotificationCompat.Builder mBuilder =   new NotificationCompat.Builder(context)
			.setSmallIcon(R.drawable.app_icon_notify) // notification icon
	        .setContentTitle(CAConstants.GCM_TITLE) // title for notification
	        .setContentText(message) // message for notification
	        .setAutoCancel(true); // clear notification after click
			
	        Intent intent = null;
	        if (message.contains(CAConstants.DIALED) || message.contains(CAConstants.SENDED_MESSAGE)) {
	        	intent = new Intent(context, HomeActivity.class);
	    		intent.setAction(CAConstants.ALERT);
	    		intent.putExtra("message", message);
	    		mBuilder.setSound(uri, AudioManager.STREAM_NOTIFICATION);
	        } else {
		    	intent = new Intent(context, MainActivity.class);
		    	mBuilder.setDefaults(Notification.DEFAULT_SOUND);
		    }
	        
			PendingIntent pi = PendingIntent.getActivity(context, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
			mBuilder.setContentIntent(pi);
			NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.notify(0, mBuilder.build());
	    }*/
    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static void setupUI(View view, final Activity activity) {

        //Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {

            view.setOnTouchListener(new OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(activity);
                    return false;
                }

            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

                View innerView = ((ViewGroup) view).getChildAt(i);

                setupUI(innerView, activity);
            }
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        if (activity != null) {
            try {
                InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            } catch (Exception e) {
            }
        }

    }

    /**
     * This will create dialog with OK, cancel buttons and returns the dialog object
     *
     * @param activity       -- required
     * @param msgTitle       -- required
     * @param msg            -- required
     * @param okListener     -- optional, default -- closes dialog
     * @param cancelListener -- optional, default -- closes dialog
     * @param showCancelBtn  -- required
     * @param showDialog     -- default false
     * @return
     */
    @NonNull
    @SuppressWarnings("UnusedReturnValue")
    public static AlertDialog showAlertDialog(Activity activity, String msgTitle, String msg, DialogInterface
            .OnClickListener okListener, DialogInterface.OnClickListener cancelListener, boolean showCancelBtn, boolean
                                                                             showDialog) {

        return showAlertDialog(activity, msgTitle, msg, okListener, cancelListener, showCancelBtn, showDialog, null, null);

    }


    /**
     * This will create dialog with OK, cancel buttons and returns the dialog object
     *
     * @param activity       -- required
     * @param msgTitle       -- required
     * @param msg            -- required
     * @param okListener     -- optional, default -- closes dialog
     * @param cancelListener -- optional, default -- closes dialog
     * @param showCancelBtn  -- required
     * @param showDialog     -- default false
     * @return
     */
    @NonNull
    @SuppressWarnings("UnusedReturnValue")
    public static AlertDialog showAlertDialog(Activity activity, String msgTitle, String msg, DialogInterface
            .OnClickListener okListener, DialogInterface.OnClickListener cancelListener, boolean showCancelBtn, boolean
                                                                             showDialog, String okText, String cancelText) {

      AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        dialogBuilder.setMessage(msg);
        dialogBuilder.setTitle(msgTitle);

        if (TextUtils.isEmpty(okText)) {
            okText = "OK";
        }

        if (TextUtils.isEmpty(cancelText)) {
            cancelText = "Cancel";
        }

        if (okListener != null) {
            dialogBuilder.setPositiveButton(okText, okListener);
        } else {
            dialogBuilder.setPositiveButton(okText, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        }

        if (showCancelBtn) {
            if (cancelListener != null) {
                dialogBuilder.setNegativeButton(cancelText, cancelListener);
            } else {
                dialogBuilder.setNegativeButton(cancelText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }
        }

      AlertDialog dialog = dialogBuilder.create();
        if (showDialog)
            dialog.show();

        return dialog;
    }

    public static void openWebUrlLink(@NonNull Activity activity, @NonNull String web_url) {
        try {
            Intent intentActionView = new Intent(Intent.ACTION_VIEW);
            intentActionView.setData(Uri.parse(web_url));
            Bundle bundle = new Bundle();
            if (!intentActionView.hasExtra(CustomTabsIntent.EXTRA_SESSION)) {
                bundle.putBinder(CustomTabsIntent.EXTRA_SESSION, null);
            }
            bundle.putInt(CustomTabsIntent.EXTRA_TOOLBAR_COLOR, ContextCompat.getColor(activity, R.color.app_color));
            intentActionView.putExtra(CustomTabsIntent.EXTRA_ENABLE_INSTANT_APPS, true);
            intentActionView.putExtra(CustomTabsIntent.EXTRA_SHARE_STATE, 0);
            intentActionView.setPackage("com.android.chrome");
            intentActionView.putExtras(bundle);
            activity.startActivity(intentActionView);
        } catch (Exception e) {
           e.printStackTrace();
            try {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(web_url)));
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        }

    }
}
