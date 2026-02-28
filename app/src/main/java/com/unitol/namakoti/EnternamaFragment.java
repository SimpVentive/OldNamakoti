package com.unitol.namakoti;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.DragEvent;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GestureDetectorCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.nex3z.flowlayout.FlowLayout;
import com.unitol.namakoti.util.Debug;
import com.unitol.namakoti.utils.ConstantsManager;
import com.unitol.namakoti.utils.NamaKotiUtils;
import com.unitol.namakoti.utils.WebConstants;
import com.unitol.namakoti.web.SyncChantAsync;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EnternamaFragment extends BaseFragment implements OnClickListener, OnTouchListener {
    protected static final String TAG = "EnternamaFragment";
    private ImageView myImage;
    TextView sriram;
    public TextView running_count;
    private TextView totalcount;
    private TextView printed_count;
    //	static int count ;
//	MediaPlayer mPlayer;
//	private ImageView image_ink_bottle;
    private LinearLayout feather_layout;
    private RadioGroup rg;
    private static final String IMAGEVIEW_TAG = "Feather Logo";

    List<Button> namasBtns = new ArrayList<Button>();
    //	private String namaOfGod;
    private View drag_and_drop_layout;
    private View key_touch_layout;
    private View japa_mala_layout;
    private Button godForChatSpinner;
    private ImageView japa_mala_img;
    private ImageView bookImg;
    private ImageView voice;
    private ImageView vibrate;
    private MediaPlayer namaPlayer;
    public String user_namakoti_id;
    private TextView nama_type_selection;
    private ImageView mGodImage;
    private static EnternamaFragment intance;
    //    private Vibrator japaMalavibe;
    private AnimationDrawable bookNamaAnimation;
    private AnimationDrawable japamalaNamaAnimation;

    public static EnternamaFragment getInstance() {
        return intance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, " onCreate() ");
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (container == null) {
            return null;
        }
        Log.i(TAG, " onCreateView() ");

        final View v = inflater.inflate(R.layout.enternama_new, container, false);
        mActivity.onFragmentTabChange(View.VISIBLE, R.drawable.enter_namam, R.drawable.selector_4_sync, "sync", View.VISIBLE);
        intance = this;
        mGodImage = v.findViewById(R.id.god_image);

        if (isKindleFire()) {
            String kindle = null;
            String[] k = kindle.split("");
            Log.i("", "" + k);
        }


        String userNamasAvailable = MyApplication.mPref.getString(getString(R.string.pref_user_namas_key), null);
        Log.i(TAG, "userNamasAvailable: " + userNamasAvailable);

        try {
            if (!TextUtils.isEmpty(userNamasAvailable)) {
                JSONArray array = new JSONArray(userNamasAvailable);
                if (array.length() == 0) { //When no nama set
                    NamaKotiUtils.showSetNamaDialog(mActivity, getResources().getString(R.string.no_nama),
                            getResources().getString(R.string.set_nama_first), getResources().getString(R.string.set_nama), "Cancel");
                    return v;
                }
            }

        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        myImage = v.findViewById(R.id.image_feather);
        voice = v.findViewById(R.id.voice);
        voice.setOnClickListener(this);

        vibrate = v.findViewById(R.id.vibrate);
        vibrate.setOnClickListener(this);

        boolean isVoiceEnabled = MyApplication.mPref.getBoolean(getString(R.string.pref_voice_over_key), true);
        boolean isVibrateEnabled = MyApplication.mPref.getBoolean(getString(R.string.pref_vibrate_over_key), true);

        if (isVoiceEnabled) {
            voice.setContentDescription(getResources().getString(R.string.on_voice));
            voice.setImageResource(R.drawable.voice_on);
        } else {
            voice.setContentDescription(getResources().getString(R.string.off_voice));
            voice.setImageResource(R.drawable.voice_off);
        }

        if (isVibrateEnabled) {
            vibrate.setContentDescription(getResources().getString(R.string.on_vibrate));
            vibrate.setImageResource(R.drawable.vibrate_on);
        } else {
            vibrate.setContentDescription(getResources().getString(R.string.off_vibrate));
            vibrate.setImageResource(R.drawable.vibrate_off);
        }

        japa_mala_img = v.findViewById(R.id.japa_mala_img_id);
        bookImg = v.findViewById(R.id.book_img);
//		japa_mala_img.setonG;

        GestureDetectorCompat mGestureDetector = new GestureDetectorCompat(mActivity, new MyGestureDetector());
        japa_mala_img.setOnTouchListener(new JapaMalaTouchLisner(mGestureDetector));
//		image_ink_bottle = (ImageView)v.findViewById(R.id.image_ink_bottle);
//		image_ink_with_feather = (ImageView)v.findViewById(R.id.image_ink_with_feather);
        sriram = (TextView) v.findViewById(R.id.namam_text);
        nama_type_selection = (TextView) v.findViewById(R.id.nama_type_selection);
        totalcount = (TextView) v.findViewById(R.id.total_edit);
        running_count = (TextView) v.findViewById(R.id.running_count);
        printed_count = (TextView) v.findViewById(R.id.printed_count);
//		totalcount.setText(""+NamaKotiUtils.getCountForNama(namaOfGod));
        feather_layout = (LinearLayout) v.findViewById(R.id.feather_layout);


        drag_and_drop_layout = v.findViewById(R.id.drag_and_drop_layout);
        key_touch_layout = v.findViewById(R.id.key_touch_layout);
        japa_mala_layout = v.findViewById(R.id.japa_mala_layout);

        LinearLayout tCountLL = v.findViewById(R.id.total_help);
        LinearLayout pCountLL = v.findViewById(R.id.print_help);
        LinearLayout rCountLL = v.findViewById(R.id.running_help);

        tCountLL.setOnClickListener(this);
        pCountLL.setOnClickListener(this);
        rCountLL.setOnClickListener(this);

        godForChatSpinner = v.findViewById(R.id.gods_spinner);
        godForChatSpinner.setOnClickListener(this);
        final List<JSONObject> namasList = parseUserNamas(userNamasAvailable);
        godForChatSpinner.setTag(namasList);

        ViewTreeObserver observer = godForChatSpinner.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                int h = japa_mala_img.getHeight();
                int w = (int) (0.34f * h);
                int total_w = h + w;

                sriram.setVisibility(View.VISIBLE);
                //
                String nama = MyApplication.mPref.getString(getString(R.string.pref_nama_on_orientation_change), "");
                int rgId = MyApplication.mPref.getInt(getString(R.string.pref_nama_on_orientation_change_rg_id), 0);
                Log.i(TAG, "nama: ====" + nama);
                if (nama.isEmpty()) {
                    onNamamSelection(namasList.get(0).toString());
                } else {
                    onNamamSelection(nama);
                    Log.i(TAG, "rgId: ====" + rgId);
                    //((RadioButton)v.findViewById(rgId)).setChecked(true);
                    MyApplication.mEditor.putString(getString(R.string.pref_nama_on_orientation_change), "").commit();
                    MyApplication.mEditor.putInt(getString(R.string.pref_nama_on_orientation_change_rg_id), 0);
                }

                godForChatSpinner.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        // Sets the tag
        myImage.setTag(IMAGEVIEW_TAG);

        rg = (RadioGroup) v.findViewById(R.id.radioGroup1);
        rg.setOnCheckedChangeListener(new OnRadioCheckedChangeListener());


        myImage.setOnTouchListener(this);
        v.findViewById(R.id.toplinear).setOnDragListener(new MyDragListener());
        //		LinearLayout bootemLinearLayout = (LinearLayout)v.findViewById(R.id.bottomlinear);
        v.findViewById(R.id.bottomlinear).setOnDragListener(new MyDragListener());
        return v;
    }

    private void prepareKeyTouch(@NonNull View v, @NonNull String namaOfGod) {
        Log.i(TAG, "namam: " + namaOfGod);
        String[] namas = namaOfGod.split(" ");
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(4, 0, 4, 0);

        FlowLayout nama_btns_ll = v.findViewById(R.id.nama_btns_ll);
        namasBtns.clear();
        nama_btns_ll.removeAllViewsInLayout();

        for (int i = 0; i < namas.length; i++) {
            Button nama_btn = new Button(mActivity);
            nama_btn.setText(namas[i]);
            nama_btn.setId(i);
            nama_btn.setTag(namas[i]);
            nama_btn.setOnClickListener(new BtnNamaOnCLick());
            if (i == 0) {
                nama_btn.setBackgroundResource(R.drawable.selector_4_key_touch);
//				nama_btn.setBackgroundColor(getResources().getColor(R.color.blue));
                nama_btn.setEnabled(true);
            } else {
//				nama_btn.setBackgroundColor(getResources().getColor(R.color.white));
                nama_btn.setBackgroundResource(R.drawable.key_un_selected);

                nama_btn.setEnabled(false);
            }
//			nama_btn.
            nama_btns_ll.addView(nama_btn, params);
            namasBtns.add(nama_btn);
        }

    }

    @SuppressLint("SetTextI18n")
    public void setTheCountValuesWithUserNamas(boolean fromOnItemSelected, @NonNull JSONObject nama, @NonNull TextView tc, @NonNull TextView pc, TextView rc, final String chantingType) {

        int isUpgraded = MyApplication.mPref.getInt(mActivity.getResources().getString(R.string.pref_user_upgrade_key), 0);

        String userID = MyApplication.mPref.getString(mActivity.getResources().getString(R.string.pref_user_id_key), "");
        String runningKey = mActivity.getResources().getString(R.string.pref_namas_running_count_key);
        String keyLocal = mActivity.getResources().getString(R.string.pref_namas_local_running_count_key);
        /**key for runningCount form nama object*/
        String key;
        try {
//			Log.i(TAG, "namaObject: "+nama);
            String totalCount = nama.getString("nama_total_count");
            Log.i(TAG, "totalCount: " + totalCount);
            if (!TextUtils.isEmpty(totalCount) && !totalCount.equalsIgnoreCase("null")) {
                tc.setText(totalCount);
            } else {
                tc.setText("0");
            }
//			Log.i(TAG, "tc.getText(): "+tc.getText().toString());

            String nama_printed_count = nama.getString("nama_printed_count");
            if (!TextUtils.isEmpty(nama_printed_count) && !nama_printed_count.equalsIgnoreCase("null")) {
                pc.setText(nama_printed_count);
            } else {
                pc.setText("0");
            }

            user_namakoti_id = nama.getString("user_namakoti_id");
            String localRunningKey = userID + user_namakoti_id + keyLocal;
            key = userID + user_namakoti_id;
            Log.i(TAG, "running count : " + NamaKotiUtils.getCountForNama(localRunningKey));

            //Commented for not to stop chanting after 10,000
			/*int tenThousandCount = NamaKotiUtils.getCountForNama(localRunningKey) + Integer.parseInt(nama.getString("nama_running_count"));
			if (isUpgraded == 0 && tenThousandCount >= ConstantsManager.USER_DEFAULT_NAMA_SIZE) {
				MyApplication.mEditor.putInt(mActivity.getResources().getString(R.string.pref_present_running_count_key), tenThousandCount).commit();
				NamaKotiUtils.showUpgradeDialog(mActivity, mActivity.getResources().getString(R.string.upgrade), mActivity.getResources().getString(R.string.upgrade_text), mActivity.getResources().getString(R.string.upgrade), "Cancel");
				return;
			}*/

            if (!fromOnItemSelected) { //It calls for every chant by dragAndDrop/key_touch/japamala
                Log.i(TAG, "it is not fromOnItemSelected");
                boolean isVoiceEnabled = MyApplication.mPref.getBoolean(mActivity.getString(R.string.pref_voice_over_key), true);
                if (isVoiceEnabled && !chantingType.equalsIgnoreCase("buttonNama")) {
                    playMusic(nama, chantingType);
                }
                boolean isVibrateEnabled = MyApplication.mPref.getBoolean(getString(R.string.pref_vibrate_over_key), true);
                if (isVibrateEnabled && !chantingType.equalsIgnoreCase("buttonNama")) {
                    playVibrate(nama, chantingType);
                }

                //running count from nama object
                int runningCount = NamaKotiUtils.getCountForNama(key + runningKey);

                //Local count
                int runningCountFromLocalMemory = NamaKotiUtils.getCountForNama(localRunningKey);
                int addTRC = runningCountFromLocalMemory + 1;//add one to total running count
//				int addTRC = runningCountFromLocalMemory+10000;//add one to total running count
                NamaKotiUtils.setCountForNama(localRunningKey, addTRC); // Add 1 and save in local memory
                Log.i(TAG, "runningCountFromLocalMemory: " + runningCountFromLocalMemory);

                //total running count
                int addedTRC = NamaKotiUtils.getCountForNama(localRunningKey); //TRC(total running count) after adding
                Log.i(TAG, "localRunningKey: " + localRunningKey);
                int trc = runningCount + addedTRC;
                Log.i(TAG, "trc: " + trc);

                rc.setText("" + trc);

				/*if(isUpgraded == 0){
					MyApplication.mEditor.putInt(mActivity.getResources().getString(R.string.pref_present_running_count_key), trc).commit();
				}*/

                if (isCountMultipleOf100(addedTRC)) {
                    syncCount(user_namakoti_id);
                }
            } else { //When user first entered in this view(EnternamaFragment)

//				String totalKey = mActivity.getResources().getString(R.string.pref_namas_total_count_key);

                //Total count
//				NamaKotiUtils.setCountForNama((key+totalKey), Integer.parseInt(nama.getString("nama_total_count")));//Need onItemSelected()
//				String totaCount = nama.getString("nama_total_count");
//				Log.i(TAG, "totaCount: == "+totaCount);
//				tc.setText(""+totaCount);//Need onItemSelected()

                //Running count from server
                godForChatSpinner.setTag(nama);
                int runningCountFromNamasObject = Integer.parseInt(nama.getString("nama_running_count"));//Need onItemSelected()
                NamaKotiUtils.setCountForNama((key + runningKey), runningCountFromNamasObject);

                //Local running count from local memory
                int runningCountFromLocalMemory = NamaKotiUtils.getCountForNama(localRunningKey);
                Log.i(TAG, "localRunningKey: " + localRunningKey);
                Log.i(TAG, "runningCountFromLocalMemory: before save" + runningCountFromLocalMemory);
                Log.i(TAG, "runningCountFromNamasObject: " + runningCountFromNamasObject);

                int runningCountToShow = runningCountFromLocalMemory + runningCountFromNamasObject;
                Log.i(TAG, "runningCountToShow: " + runningCountToShow);
                rc.setText("" + runningCountToShow);

				/*if(isUpgraded == 0){
					MyApplication.mEditor.putInt(mActivity.getResources().getString(R.string.pref_present_running_count_key), runningCountToShow).commit();
				}*/

                String namaOfGod = nama.getString("sub_theme_name");
                namaOfGod = "" + Html.fromHtml(namaOfGod);
                sriram.setText(namaOfGod);

                /*If count is multiple of 100, do sync with server*/
                if (isCountMultipleOf100(runningCountFromLocalMemory)) {
                    syncCount(user_namakoti_id);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("DiscouragedApi")
    private void playMusic(@NonNull JSONObject namaObject, final String chantingType) {
        Log.i(TAG, "playMusic chantingType: === " + chantingType);
        try {
            String godsMusic = namaObject.getString("music");
            if (!TextUtils.isEmpty(godsMusic) && godsMusic.contains("/")) {
                Debug.e(TAG, "url:" + WebConstants.GOD_IMAGE_BASE_URL + godsMusic);
                String SDCardRoot = requireActivity().getCacheDir().toString();
                String fileName = godsMusic.substring(godsMusic.lastIndexOf('/') + 1);
                String audioFilePath = SDCardRoot + "/MyAudioFolder/" + fileName;
                File audioFile = new File(audioFilePath);
                if (!audioFile.exists()) {
                    new DownloadFile(requireActivity(), WebConstants.GOD_IMAGE_BASE_URL + godsMusic, fileName, SDCardRoot + "/MyAudioFolder").execute();
                } else {
                    play(requireActivity(), audioFile, chantingType);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void playVibrate(@NonNull JSONObject namaObject, final String chantingType) {
        Log.i(TAG, "playMusic chantingType: === " + chantingType);
        try {
            Vibrator v = (Vibrator) requireActivity().getSystemService(Context.VIBRATOR_SERVICE);
            assert v != null;
            // Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500,
                        VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(500);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class DownloadFile extends AsyncTask<String, Integer, String> {
        private Activity ctx;
        private ProgressDialog progressDialog;
        private final String download_file_path;
        private final String fileName;
        private final String pathToSave;
        private File file;
        int downloadedSize = 0;
        int totalSize = 0;

        private void createProgresDialog(Activity c) {
            progressDialog = new ProgressDialog(c);
            progressDialog.setIndeterminate(true);
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Please wait...");
        }

        public DownloadFile(Activity c, String download_file_path, String fileName, String pathToSave) {
            ctx = c;
            this.download_file_path = download_file_path;
            this.fileName = fileName;
            this.pathToSave = pathToSave;
            createProgresDialog(ctx);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(download_file_path);
                HttpURLConnection urlConnection = (HttpURLConnection) url
                        .openConnection();

                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);

                // connect
                urlConnection.connect();

                File myDir = new File(pathToSave);
                if (!myDir.exists()) {
                    boolean makeDir = myDir.mkdirs();
                    Log.i(TAG, "makeDir: === " + makeDir);
                }

                // create a new file, to save the downloaded file

                file = new File(myDir, fileName);

                FileOutputStream fileOutput = new FileOutputStream(file);

                // Stream used for reading the data from the internet
                InputStream inputStream = urlConnection.getInputStream();

                // this is the total size of the file which we are downloading
                totalSize = urlConnection.getContentLength();

                ctx.runOnUiThread(() -> progressDialog.setMax(totalSize));

                // create a buffer...
                byte[] buffer = new byte[1024];
                int bufferLength = 0;

                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    fileOutput.write(buffer, 0, bufferLength);
                    downloadedSize += bufferLength;
                    // update the progressbar //
                    ctx.runOnUiThread(() -> {
                        progressDialog.setProgress(downloadedSize);
                        float per = ((float) downloadedSize / totalSize) * 100;
                        Log.i(TAG, "Downloaded " + downloadedSize + "KB / " + totalSize + "KB (" + (int) per + "%)");

                    });
                }
                // close the output stream when complete //
                fileOutput.close();
                return file.getAbsolutePath();
            } catch (final MalformedURLException e) {
                e.printStackTrace();
            } catch (final IOException e) {
                e.printStackTrace();
            } catch (final Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String audioFilePath) {
            super.onPostExecute(audioFilePath);
            try {
                progressDialog.dismiss();
                if (audioFilePath == null) {
                    boolean deleteFile = file.delete();
                    Log.i(TAG, "deleteFile: === " + deleteFile);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private MediaPlayer mMediaPlayer;

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void play(Context c, @NonNull File rid, final String chantingType) {
        Log.i(TAG, "play: === " + rid.getAbsolutePath());
        try {
            stop();
            mMediaPlayer = MediaPlayer.create(c, Uri.fromFile(rid));
            mMediaPlayer.setOnCompletionListener(mediaPlayer -> {
                stop();
            });
            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                boolean deleteFile = rid.delete();
                Log.i(TAG, "deleteFile: === " + deleteFile);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private boolean isCountMultipleOf100(int runningCount) {
        if (runningCount < 100) {
            return false;
        }
        Log.i(TAG, "runningCount: " + runningCount);
        Log.i(TAG, "isCountMultipleOf100: " + runningCount % 100f);
        float reminder = runningCount % 100f;
        return reminder == 0 ? true : false;
    }

    /**
     * parse the usernamas and returns list of jsonobjects
     */
    @NonNull
    private List<JSONObject> parseUserNamas(String userNamas) {
        //"namas":[{"nama_total_count":"0","nama_running_count":"0","sub_theme_name":"Om Sai Ram","user_namakoti_id":"36","user_theme_id":"3","user_sub_theme_id":"4","user_language_id":"1"}]}
        List<JSONObject> jsonObjects = new ArrayList<JSONObject>();
        try {
            if (!TextUtils.isEmpty(userNamas)) {
                JSONArray namasArray = new JSONArray(userNamas);
                Log.i(TAG, "namasArray size: " + namasArray.length());
                for (int i = 0; i < namasArray.length(); i++) {
                    JSONObject object = (JSONObject) namasArray.get(i);
                    jsonObjects.add(object);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObjects;
    }

    /**
     * It enables namas's buttons according to sequence
     */
    public class BtnNamaOnCLick implements OnClickListener {

        @Override
        public void onClick(@NonNull View v) {
            int id = v.getId();
            Button selectedNamaBtn = namasBtns.get(id);

            selectedNamaBtn.setEnabled(false);
            selectedNamaBtn.setBackgroundResource(R.drawable.key_un_selected);

            if (id == namasBtns.size() - 1) {
                namasBtns.get(0).setEnabled(true);
                namasBtns.get(0).setBackgroundResource(R.drawable.selector_4_key_touch);
                JSONObject namaObject = (JSONObject) godForChatSpinner.getTag();
                setTheCountValuesWithUserNamas(false, namaObject, totalcount, printed_count, running_count, "buttonNama");
            } else {
                boolean isVoiceEnabled = MyApplication.mPref.getBoolean(mActivity.getString(R.string.pref_voice_over_key), true);
                if (isVoiceEnabled && id == 0) {
                    JSONObject namaObject = (JSONObject) godForChatSpinner.getTag();
                    playMusic(namaObject, "buttonNama");
                }

                boolean isVibrateEnabled = MyApplication.mPref.getBoolean(getString(R.string.pref_vibrate_over_key), true);
                if (isVibrateEnabled && id == 0) {
                    JSONObject namaObject = (JSONObject) godForChatSpinner.getTag();
                    playVibrate(namaObject, "buttonNama");
                }
                Log.i(TAG, "id: " + id);
                namasBtns.get(id + 1).setEnabled(true);
                namasBtns.get(id + 1).setBackgroundResource(R.drawable.selector_4_key_touch);
            }
        }

    }

    private class OnRadioCheckedChangeListener implements OnCheckedChangeListener {

        private int pos;
        private int pos1;

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            pos = rg.indexOfChild(getView().findViewById(checkedId));

            pos1 = rg.indexOfChild(getView().findViewById(rg.getCheckedRadioButtonId()));

            switch (checkedId) {
                case R.id.drag_and_drop_rb:
                    drag_and_drop_layout.setVisibility(View.VISIBLE);
                    key_touch_layout.setVisibility(View.GONE);
                    japa_mala_layout.setVisibility(View.GONE);
                    nama_type_selection.setText(getResources().getText(R.string.write_nama_description));
                    break;
                case R.id.key_touch_rb:
                    drag_and_drop_layout.setVisibility(View.GONE);
                    key_touch_layout.setVisibility(View.VISIBLE);
                    japa_mala_layout.setVisibility(View.GONE);
                    nama_type_selection.setText(getResources().getText(R.string.keytouch_nama_description));
                    break;
                case R.id.japa_mala_rb:
                    drag_and_drop_layout.setVisibility(View.GONE);
                    key_touch_layout.setVisibility(View.GONE);
                    japa_mala_layout.setVisibility(View.VISIBLE);
                    nama_type_selection.setText(getResources().getText(R.string.japamala_nama_description));
                    break;
                default:
                    break;
            }
        }

    }

    class MyDragListener implements OnDragListener {

        private boolean actionDrop = false;

        @Override
        public boolean onDrag(final View v, @NonNull DragEvent event) {
            if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                return false;
            }
            if (bookNamaAnimation != null && bookNamaAnimation.isRunning()) {
                return false;
            }
            Log.i(TAG, "getAction: " + "onDrag call");
            // Handles each of the expected events
            switch (event.getAction()) {

                //signal for the start of a drag and drop operation.
                case DragEvent.ACTION_DRAG_STARTED:
                    Log.i(TAG, "getAction: " + "ACTION_DRAG_STARTED");
                    sriram.setVisibility(View.VISIBLE);
                    myImage.setVisibility(View.INVISIBLE);
                    // do nothing
                    break;

                //the drag point has entered the bounding box of the View
                case DragEvent.ACTION_DRAG_ENTERED:
                    Log.i(TAG, "getAction: " + "ACTION_DRAG_ENTERED");
                    //	v.setBackground(targetShape);	//change the shape of the view
                    break;

                //the user has moved the drag shadow outside the bounding box of the View
                case DragEvent.ACTION_DRAG_EXITED:
                    Log.i(TAG, "getAction: " + "ACTION_DRAG_EXITED");
                    //	v.setBackground(normalShape);	//change the shape of the view back to normal
//				Log.e(TAG, "ACTION_DRAG_EXITED===============");
                    sriram.setVisibility(View.VISIBLE);
                    myImage.setVisibility(View.INVISIBLE);
                    reSetTheViewOnDragExit(v);
                    break;

                //drag shadow has been released,the drag point is within the bounding box of the View
                case DragEvent.ACTION_DROP:
                    Log.i(TAG, "getAction: " + "ACTION_DROP");
                    // if the view is the bottomlinear, we accept the drag item
                    if (v == getView().findViewById(R.id.bottomlinear)) {

                        actionDrop = true;
                        sriram.setVisibility(View.VISIBLE);
                        myImage.setVisibility(View.INVISIBLE);
                        View view = (View) event.getLocalState();
                        ViewGroup viewgroup = (ViewGroup) view.getParent();
                        viewgroup.removeView(view);

                        //change the text

//					sriram.setVisibility(View.VISIBLE);
                        LinearLayout containView = (LinearLayout) v;
                        containView.addView(view);
//					view.setVisibility(View.VISIBLE);
//					myImage.setVisibility(View.VISIBLE);
//					reSetTheView(v);
//					NamaKotiUtils.setCountForNama(namaOfGod, 1);
//					count++;
//					String Count = ""+count;
//					totalcount.setText(""+NamaKotiUtils.getCountForNama(namaOfGod));

                        bookImg.setBackgroundResource(R.drawable.hand_written_images);
                        bookNamaAnimation = (AnimationDrawable) bookImg.getBackground();
                        bookNamaAnimation.start();

                        int time = 495;
                        int totalTime = time * 4;

                        Handler handler = new Handler();
                        handler.postDelayed(() -> {
                            if (EnternamaFragment.getInstance() != null) {
                                reSetTheView(v);
                                bookNamaAnimation.stop();
                                bookImg.setBackgroundResource(R.drawable.book_img);
                                sriram.setVisibility(View.VISIBLE);
                            }
                        }, totalTime);
                        JSONObject namaObject = (JSONObject) godForChatSpinner.getTag();
                        setTheCountValuesWithUserNamas(false, namaObject, totalcount, printed_count, running_count, "bookNama");
                        actionDrop = false;
//					mPlayer.start();
//					mPlayer.start();
                    } else {
                        myImage.setVisibility(View.INVISIBLE);
                        View view = (View) event.getLocalState();
                        view.setVisibility(View.VISIBLE);
                        Context context = mActivity;
//					Toast.makeText(context, "Please drop the feather on the book", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    break;

                //the drag and drop operation has concluded.
                case DragEvent.ACTION_DRAG_ENDED:
                    Log.i(TAG, "getAction: " + "ACTION_DRAG_ENDED");
//				Log.e(TAG, "ACTION_DRAG_ENDED===============");
                    //	v.setBackground(normalShape);
                    //go back to normal shape
                    if (!actionDrop) {
                        sriram.setVisibility(View.VISIBLE);
                        myImage.setVisibility(View.VISIBLE);
                    }

                default:
                    break;
            }
            return true;
        }
    }


    @Override
    public void onClick(@NonNull View v) {

        switch (v.getId()) {
		/*case R.id.save_btn:
//			syncCount(user_namakoti_id);
			break;*/
            case R.id.voice:
                String cd = (String) voice.getContentDescription();
                String on = getResources().getString(R.string.on_voice);
                String off = getResources().getString(R.string.off_voice);

                if (cd.equals(on)) {
                    MyApplication.mEditor.putBoolean(getString(R.string.pref_voice_over_key), false).commit();
                    voice.setImageResource(R.drawable.voice_off);
                    voice.setContentDescription(off);
                } else if (cd.equals(off)) {
                    MyApplication.mEditor.putBoolean(getString(R.string.pref_voice_over_key), true).commit();
                    voice.setImageResource(R.drawable.voice_on);
                    voice.setContentDescription(on);
                }
                break;

            case R.id.vibrate:
                String cdvibrate = (String) vibrate.getContentDescription();
                String onvibrate = getResources().getString(R.string.on_vibrate);
                String offvibrate = getResources().getString(R.string.off_vibrate);

                if (cdvibrate.equals(onvibrate)) {
                    MyApplication.mEditor.putBoolean(getString(R.string.pref_vibrate_over_key), false).commit();
                    vibrate.setImageResource(R.drawable.vibrate_off);
                    vibrate.setContentDescription(offvibrate);
                } else if (cdvibrate.equals(offvibrate)) {
                    MyApplication.mEditor.putBoolean(getString(R.string.pref_vibrate_over_key), true).commit();
                    vibrate.setImageResource(R.drawable.vibrate_on);
                    vibrate.setContentDescription(onvibrate);
                }
                break;

            case R.id.gods_spinner:
                String userNamasAvailable = MyApplication.mPref.getString(getString(R.string.pref_user_namas_key), null);
                Intent i = new Intent(mActivity, NamasDialogActivity.class);
                i.putExtra(ConstantsManager.USERNAMAS_AVAILBLE, userNamasAvailable);
                startActivityForResult(i, ConstantsManager.NAMAS_CODE);

                break;
            case R.id.total_help:
                NamaKotiUtils.showHelpDialog(mActivity, "Total", getResources().getString(R.string.total_count_text));
                break;
            case R.id.print_help:
                NamaKotiUtils.showHelpDialog(mActivity, "Printed", getResources().getString(R.string.printed_count_text));
                break;
            case R.id.running_help:
                NamaKotiUtils.showHelpDialog(mActivity, "On Going", getResources().getString(R.string.on_goingl_count_text));
                break;
            default:
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult: " + resultCode);
        if (requestCode == ConstantsManager.NAMAS_CODE) {
            if (data != null) {
                String selectedItem = data.getExtras().getString(ConstantsManager.NAMAS_JSON);
                onNamamSelection(selectedItem);
            }
        }
    }

    private void onNamamSelection(String selectedItem) {
        Log.i(TAG, "selectedItem: " + selectedItem);
        JSONObject namaObject = null;
        try {
            namaObject = new JSONObject(selectedItem);
            String namam = namaObject.getString("theme_name");
            String namaOfGod = namaObject.getString("sub_theme_name");
            namaOfGod = "" + Html.fromHtml(namaOfGod);
            Log.i(TAG, "namaOfGod: " + namaOfGod);
//			godForChatSpinner.setTypeface(Typefaces.get(mActivity, 1, "fonts/f5-Lohit-Gujarati-kikat.ttf"));
            godForChatSpinner.setText(namaOfGod);
//			NamaKotiUtils.onLanguage(mActivity, namaObject.getString("user_language_id"), godForChatSpinner);

            String godsPhoto = namaObject.getString("photo");
            mGodImage.setImageBitmap(null);
            mGodImage.setImageResource(R.drawable.ic_defaultprofile);

            if (!TextUtils.isEmpty(godsPhoto)) {
                Debug.e(TAG, "url:" + WebConstants.GOD_IMAGE_BASE_URL + godsPhoto);
                Glide.with(requireActivity().getApplicationContext())
                        .asBitmap()
                        .load(WebConstants.GOD_IMAGE_BASE_URL + godsPhoto)
                        .transition(BitmapTransitionOptions.withCrossFade())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                mGodImage.setImageBitmap(resource);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }

                            @Override
                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                super.onLoadFailed(errorDrawable);
                            }
                        });
            }

            String godsMusic = namaObject.getString("music");
            if (!TextUtils.isEmpty(godsMusic) && godsMusic.contains("/")) {
                Debug.e(TAG, "url:" + WebConstants.GOD_IMAGE_BASE_URL + godsMusic);
                String SDCardRoot = requireActivity().getCacheDir().toString();
                String fileName = godsMusic.substring(godsMusic.lastIndexOf('/') + 1);
                String audioFilePath = SDCardRoot + "/MyAudioFolder/" + fileName;
                File audioFile = new File(audioFilePath);
                if (!audioFile.exists()) {
                    new DownloadFile(requireActivity(), WebConstants.GOD_IMAGE_BASE_URL + godsMusic, fileName, SDCardRoot + "/MyAudioFolder").execute();
                }
            }
            prepareKeyTouch(getView(), namaOfGod);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (namaObject != null) {
            setTheCountValuesWithUserNamas(true, namaObject, totalcount, printed_count, running_count, "default");
            godForChatSpinner.setTag(namaObject); //It is very important to prepare key
        }

    }

    /**
     * This method call async class for syncing the count to server
     *
     * @param user_namakoti_id
     */
    public void syncCount(String user_namakoti_id) {
        if (NamaKotiUtils.isNetworkAvailable(mActivity)) {
            SyncChantAsync syncChantAsync = new SyncChantAsync(mActivity, user_namakoti_id, true, false);
            syncChantAsync.execute(totalcount, printed_count, running_count);
        } else {
            Log.i(TAG, "No network");
        }
    }

    public void reSetTheView(View v) {
        ((ViewGroup) v).removeView(myImage);
//		feather_layout.removeView(myImage);
//		feather_layout.removeView(image_ink_bottle);
        feather_layout.addView(myImage);
//		feather_layout.addView(image_ink_bottle);
        myImage.setVisibility(View.VISIBLE);
    }

    public void reSetTheViewOnDragExit(View v) {
//		((ViewGroup) v).removeView(myImage);
//		feather_layout.removeView(myImage);
//		feather_layout.removeView(image_ink_bottle);
        feather_layout.removeView(myImage);
        feather_layout.addView(myImage);
//		feather_layout.addView(image_ink_bottle);
        myImage.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onTouch(View view, @NonNull MotionEvent e) {
//		Log.i(TAG, "onTouch 1"+e.getAction());
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
//			Log.i(TAG, "onTouch 2"+e.getAction());
            // create it from the object's tag
            ClipData.Item item = new ClipData.Item((CharSequence) view.getTag());

            String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
            ClipData data = new ClipData(view.getTag().toString(), mimeTypes,
                    item);
            DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);

            view.startDrag(data, // data to be dragged
                    shadowBuilder, // drag shadow
                    view, // local data about the drag and drop operation
                    0 // no needed flags
            );

//			view.setVisibility(View.INVISIBLE);

            return true;
        } else {
            return false;
        }
    }


    private static final int SWIPE_MIN_DISTANCE = 10;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private static final int SWIPE_THRESHOLD = 50;
    private static final int SWIPE_VELOCITY_THRESHOLD = 50;

    class MyGestureDetector extends SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            result = onSwipeRight();
                        } else {
                            result = onSwipeLeft();
                        }
                    }
                } else {
                    if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            result = onSwipeBottom();
                        } else {
                            result = onSwipeTop();
                        }
                    }
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

    public boolean onSwipeRight() {
        Log.i(TAG, "onSwipeRight");
        return false;
    }

    public boolean onSwipeLeft() {
        Log.i(TAG, "onSwipeLeft");
        return false;
    }

    public boolean onSwipeTop() {
        Log.i(TAG, "onSwipeTop");
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            return false;
        }
        if (japamalaNamaAnimation != null && japamalaNamaAnimation.isRunning()) {
            return false;
        }
        countOnSwipe(false, true);
        return false;
    }

    public boolean onSwipeBottom() {
        Log.i(TAG, "onSwipeBottom");
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            return false;
        }
        if (japamalaNamaAnimation != null && japamalaNamaAnimation.isRunning()) {
            return false;
        }
        countOnSwipe(true, false);
        return false;
    }

    public class JapaMalaTouchLisner implements OnTouchListener {

        private GestureDetectorCompat mGestureDetector;

        public JapaMalaTouchLisner(GestureDetectorCompat mGestureDetector) {
            this.mGestureDetector = mGestureDetector;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mGestureDetector.onTouchEvent(event);
//			Log.i(TAG, "onTouch");
            return true;
        }

    }

    private void countOnSwipe(final boolean bottem, final boolean top) {
        if (bottem) {
            japa_mala_img.setBackgroundResource(R.drawable.japa_mala_images_down);
        } else if (top) {
            japa_mala_img.setBackgroundResource(R.drawable.japa_mala_images_up);
        }
        japamalaNamaAnimation = (AnimationDrawable) japa_mala_img.getBackground();
        japamalaNamaAnimation.start();

        int time = 495;
        int totalTime = time * 4;

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (EnternamaFragment.getInstance() != null) {
                japamalaNamaAnimation.stop();
            }
        }, totalTime);

        JSONObject namaObject = (JSONObject) godForChatSpinner.getTag();
        setTheCountValuesWithUserNamas(false, namaObject, totalcount, printed_count, running_count, "japamalaNama");

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onDestroy() {
//		namaPlayer = null;
//		intance = null;
//		Log.i(TAG, " onDestroy() ");
//		Log.i(TAG, " onDestroy() =="+godForChatSpinner.getTag());
//		String tag = godForChatSpinner.getTag().toString();
//		Log.i(TAG, " onDestroy() "+tag);
//		MyApplication.mEditor.putString(getString(R.string.pref_nama_on_orientation_change), tag).commit();
//		MyApplication.mEditor.putInt(getString(R.string.pref_nama_on_orientation_change_rg_id), rg.getCheckedRadioButtonId()).commit();

        super.onDestroy();
    }

    public static boolean isKindleFire() {
        return android.os.Build.MANUFACTURER.equals("Amazon")
                && (android.os.Build.MODEL.equals("Kindle Fire")
                || android.os.Build.MODEL.startsWith("KF"));
    }

}
