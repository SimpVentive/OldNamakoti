package com.namakoti.chanting.self;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.IdRes;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GestureDetectorCompat;
import androidx.appcompat.widget.Toolbar;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.NetworkImageView;
import com.namakoti.R;
import com.namakoti.base.BaseActivity;
import com.namakoti.base.MyApplication;
import com.namakoti.beans.GodNamesBean;
import com.namakoti.beans.SaveChantsBean;
import com.namakoti.beans.SyncChantsBean;
import com.namakoti.beans.UserInfoBean;
import com.namakoti.components.GestureSwipeListener;
import com.namakoti.components.MyGestureDetector;
import com.namakoti.database.DatabaseHelper;
import com.namakoti.settings.SelectChantGodActivity;
import com.namakoti.utils.Constants;
import com.namakoti.utils.ServiceMethod;
import com.namakoti.utils.Utils;
import com.namakoti.utils.VolleyResponseListener;
import com.namakoti.utils.VolleyUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.os.Build.VERSION.SDK_INT;
import static com.namakoti.base.BaseFragment.getToolbarLogoIcon;

/**
 * Created by anusha on 12/23/2017.
 */

public class ChantingDetailsActivity extends BaseActivity implements VolleyResponseListener,View.OnClickListener,
        View.OnTouchListener, View.OnDragListener, RadioGroup.OnCheckedChangeListener,
        GestureSwipeListener {

    private static final String TAG = ChantingDetailsActivity.class.getName();
    private static final String VOICE_OVER_KEY = "voice_key";
    private static final String IMAGEVIEW_TAG = "Feather Logo";
    public static final String EXTRA_IS_SYNC = "sync_updated_key";
    public static final String BUNDLE_IS_FROM_CAUSE = "is_from_chant_cause";
    public static final String BUNDLE_NAMAS_BEAN_KEY = "BUNDLE_NAMAS_BEAN_KEY";

    @BindView(R.id.total_count_tv)
    TextView mTotalCountTv;

    @BindView(R.id.running_count_tv)
    TextView mRunningCountTv;

    @BindView(R.id.printed_count_tv)
    TextView mPrintedCountTv;

    @BindView(R.id.drag_and_drop_rb)
    RadioButton drag_and_drop_rb;

    @BindView(R.id.key_touch_rb)
    RadioButton key_touch_rb;
    @BindView(R.id.japa_mala_rb)
    RadioButton japa_mala_rb;

    @BindView(R.id.namam_text)
    TextView mDragEnterNamamTv;

    @BindView(R.id.nama_type_selection)
    TextView mNamamDesTv;

    @BindView(R.id.feather_layout)
    LinearLayout feather_layout;

    @BindView(R.id.drag_and_drop_layout)
    View drag_and_drop_layout;

    @BindView(R.id.key_touch_layout)
    View key_touch_layout;

    @BindView(R.id.japa_mala_layout)
    View japa_mala_layout;

    @BindView(R.id.gods_spinner)
    TextView godForChatSpinner;

    @BindView(R.id.voice)
    ImageView voice;

    @BindView(R.id.image_feather)
    ImageView mHandImage;

    @BindView(R.id.radioGroup1)
    RadioGroup mRadioGroup;

    @BindView(R.id.japa_mala_img_id)
    ImageView japa_mala_img;

    @BindView(R.id.god_image)
    NetworkImageView god_image;

    @BindView(R.id.button_stop)
    Button mStopBtn;

    List<Button> namasBtns = new ArrayList<Button>();
    private SaveChantsBean mSelectedBean;
    private ChantingDetailsActivity mActivity;
    private AnimationDrawable theDogAnimation;
    private boolean actionDrop = false;
    private int mServerTotalCount;
    private int mServerRuunningCount;
    private long mLocalCount;
    private int SELECTED_GOD_NAMA_CODE = 100;
    private String mFromScreen;
    private String mNamasTableName;
    private String mLocalCountTableName;
    private String mServerCountTableName;
    private ArrayList<GodNamesBean> mGodList;
    private int mNamakotiId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chant_details_layout);
        ButterKnife.bind(this);
        mActivity = this;
        Cursor mCursorGod = DatabaseHelper.getGodImages(mDatabase);
        mGodList = getGodListFromDb(mCursorGod);
        getBundle();
        toolbar();
        initListener();

        getSavedServerCount();
        mLocalCount = DatabaseHelper.getSavedLocalCount(mDatabase,mNamakotiId,mLocalCountTableName,mUserIdLong);

        ViewTreeObserver observer = godForChatSpinner.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                int h = japa_mala_img.getHeight();
                int w = (int) (0.34f * h);
                int total_w = h + w;
                mDragEnterNamamTv.setVisibility(View.VISIBLE);
                onNamamSelection();
                if (SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    godForChatSpinner.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    godForChatSpinner.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
        // Sets the tag
        mHandImage.setTag(IMAGEVIEW_TAG);
    }

    private void initListener() {
        voice.setOnClickListener(this);
        godForChatSpinner.setOnClickListener(this);
        mStopBtn.setOnClickListener(this);

        boolean isVoiceEnabled = Utils.getBooleanFromSP(mActivity, VOICE_OVER_KEY, true);
        if (isVoiceEnabled) {
            voice.setContentDescription(getResources().getString(R.string.on_voice));
            voice.setImageResource(R.drawable.chanting_volume_ic);
        } else {
            voice.setContentDescription(getResources().getString(R.string.off_voice));
            voice.setImageResource(R.drawable.chanting_novolume_ic);
        }
        GestureDetectorCompat mGestureDetector = new GestureDetectorCompat(mActivity, new MyGestureDetector(this));
        japa_mala_img.setOnTouchListener(new JapaMalaTouchListener(mGestureDetector));
        mRadioGroup.setOnCheckedChangeListener(this);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT >= 11) {
            mHandImage.setOnTouchListener(this);
            findViewById(R.id.toplinear).setOnDragListener(this);
            findViewById(R.id.bottomlinear).setOnDragListener(this);
        } else {
            Toast.makeText(mActivity, "Handwritten animation won't work for this version, You should have minimum of version Honey Comb", Toast.LENGTH_SHORT).show();
        }
    }

    public void reSetTheView(View v) {
        ((ViewGroup) v).removeView(mHandImage);
        feather_layout.addView(mHandImage);
        mHandImage.setVisibility(View.VISIBLE);
    }

    public void reSetTheViewOnDragExit(View v) {
        feather_layout.removeView(mHandImage);
        feather_layout.addView(mHandImage);
        mHandImage.setVisibility(View.VISIBLE);
    }

    private void onNamamSelection() {
        Spanned namam = fromHtml(mSelectedBean.getTheme_name());
        Spanned namaOfGod = fromHtml(mSelectedBean.getSub_theme_name());
        godForChatSpinner.setText(namaOfGod.toString());

        String name = (mSelectedBean.getTheme_name().replace(" ", "")).toLowerCase() + getResources().getString(R.string.save_nama_aditional_key);
        Log.e("", "name =====> " + name);
        setImageLoader(mGodList,god_image,namam.toString());

        prepareKeyTouch(namaOfGod.toString());
        setTheCountValuesWithUserNamas(true, mTotalCountTv, mPrintedCountTv, mRunningCountTv, "default");
    }

    private void getBundle() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        mSelectedBean = (SaveChantsBean) bundle.getSerializable(BUNDLE_NAMAS_BEAN_KEY);
        mNamakotiId = mSelectedBean.getUser_namakoti_id();

        mFromScreen = bundle.getString(BUNDLE_IS_FROM_CAUSE);
        toolbar();

        if (mFromScreen.equalsIgnoreCase(Constants.KEY_CHANT_FOR_CAUSE)){
            mNamasTableName = DatabaseHelper.CHANT_FOR_CAUSE_NAMAS_TABLE_NAME;
            mLocalCountTableName = DatabaseHelper.CHANT_FOR_CAUSE__LOCAL_COUNT_TABLE_NAME;
            mServerCountTableName = DatabaseHelper.CHANT_FOR_CAUSE__SERVER_COUNT_TABLE_NAME;
            setToolbarTitle(getString(R.string.chant_for_cause));
        }
        else if (mFromScreen.equalsIgnoreCase(Constants.KEY_SELF_CHANT)){
            mNamasTableName = DatabaseHelper.NORMAL_NAMAS_TABLE_NAME;
            mLocalCountTableName = DatabaseHelper.NORMAL_LOCAL_COUNT_TABLE_NAME;
            mServerCountTableName = DatabaseHelper.NORMAL_SERVER_COUNT_TABLE_NAME;
            setToolbarTitle(getString(R.string.normal_chanting));
        }
        else if (mFromScreen.equalsIgnoreCase(Constants.KEY_KARMIC_BEING)){
            mNamasTableName = DatabaseHelper.KARMIC_SELF_NAMAS_TABLE_NAME;
            mLocalCountTableName = DatabaseHelper.KARMIC_SELF_LOCAL_COUNT_TABLE_NAME;
            mServerCountTableName = DatabaseHelper.KARMIC_SELF_SERVER_COUNT_TABLE_NAME;
            setToolbarTitle(getString(R.string.being_helped));
        }
        else if (mFromScreen.equalsIgnoreCase(Constants.KEY_KARMIC_OTHERS)){
            mNamasTableName = DatabaseHelper.KARMIC_OTHERS_NAMAS_TABLE_NAME;
            mLocalCountTableName = DatabaseHelper.KARMIC_OTHERS_LOCAL_COUNT_TABLE_NAME;
            mServerCountTableName = DatabaseHelper.KARMIC_OTHERS_SERVER_COUNT_TABLE_NAME;
            setToolbarTitle(getString(R.string.helping_others));
        }
    }

    private void toolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setLogo(R.drawable.back_ic);
        View logoView = getToolbarLogoIcon(toolbar);
        logoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // add back arrow to toolbar
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = mActivity.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private void prepareKeyTouch(String namaOfGod) {
        Log.i(TAG, "namam: " + namaOfGod);
        String[] namas = namaOfGod.split(" ");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(30, 0, 10, 0);

        LinearLayout layout = (LinearLayout) findViewById(R.id.nama_btns_ll);
        layout.setOrientation(LinearLayout.VERTICAL);
        namasBtns.clear();//Can also be done in xml by android:orientation="vertical"
        layout.removeAllViewsInLayout();
        TableRow row;
        final int screenWidth = dpToPx(getResources().getConfiguration().screenWidthDp);
        if (namas != null && namas.length> 0) {

            int i = 0;
            int j = 0;
            int count = 0;

            int width = (screenWidth - (namas.length)*4)/namas.length;

            while (i<namas.length) {
                j = i + 4;
                row = new TableRow(mActivity);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);
                row.setGravity(Gravity.LEFT);
                row.setWeightSum(4);
                while ((i<j)&&(i<namas.length)) {
                    Button nama_btn = new Button(mActivity);
                    nama_btn.setGravity(Gravity.CENTER_HORIZONTAL);
                    nama_btn.setMinimumWidth(100);//I set 100px for minimunWidth.
                    nama_btn.setWidth(width);
//                    iBtn.setLayoutParams(params);//not working
                    nama_btn.setText(namas[i]);
                    nama_btn.setId(count);
                    nama_btn.setTag(namas[i]);

                    nama_btn.setTextColor(ResourcesCompat.getColor(getResources(),android.R.color.white,null));
                    nama_btn.setOnClickListener(new BtnNamaOnCLick());
                    if (count == 0) {
                        nama_btn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selector_4_key_touch, null));
                        nama_btn.setEnabled(true);
                    } else {
                        nama_btn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.buton_unselect, null));
                        nama_btn.setEnabled(false);
                    }

                    row.addView(nama_btn, 4 + i - j);
                    namasBtns.add(nama_btn);
                    i++;
                    count++;
                }
                layout.addView(row);
            }
        }

        /*LinearLayout nama_btns_ll = (LinearLayout) findViewById(R.id.nama_btns_ll);
        namasBtns.clear();
        nama_btns_ll.removeAllViewsInLayout();

        for (int i = 0; i < namas.length; i++) {
            Button nama_btn = new Button(mActivity);
            nama_btn.setText(namas[i]);
            nama_btn.setId(i);
            nama_btn.setTag(namas[i]);

            nama_btn.setTextColor(ResourcesCompat.getColor(getResources(),android.R.color.white,null));
            nama_btn.setOnClickListener(new BtnNamaOnCLick());
            if (i == 0) {
//				nama_btn.setBackgroundColor(getResources().getColor(R.color.blue));
                nama_btn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selector_4_key_touch, null));
                nama_btn.setEnabled(true);
            } else {
//				nama_btn.setBackgroundColor(getResources().getColor(R.color.white));
                nama_btn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.buton_unselect, null));
                nama_btn.setEnabled(false);
            }
            nama_btns_ll.addView(nama_btn, params);
            namasBtns.add(nama_btn);
        }*/
    }

    @Override
    public void countOnSwipe(boolean bottem, boolean top) {
        int time = 200;
        int totalTime = time * 4;
        if (bottem) {
            japa_mala_img.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.japa_mala_images_down, null));
        } else if (top) {
            japa_mala_img.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.japa_mala_images_up, null));
        }

        theDogAnimation = (AnimationDrawable) japa_mala_img.getBackground();
        setTheCountValuesWithUserNamas(false, mTotalCountTv, mPrintedCountTv, mRunningCountTv, "japamalaNama");
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int checkedId) {
        int pos = mRadioGroup.indexOfChild(findViewById(checkedId));
        int pos1 = mRadioGroup.indexOfChild(findViewById(mRadioGroup.getCheckedRadioButtonId()));
        switch (checkedId) {
            case R.id.drag_and_drop_rb:
                drag_and_drop_layout.setVisibility(View.VISIBLE);
                key_touch_layout.setVisibility(View.GONE);
                japa_mala_layout.setVisibility(View.GONE);
                mNamamDesTv.setText(getResources().getText(R.string.write_nama_description));
                break;
            case R.id.key_touch_rb:
                drag_and_drop_layout.setVisibility(View.GONE);
                key_touch_layout.setVisibility(View.VISIBLE);
                japa_mala_layout.setVisibility(View.GONE);
                mNamamDesTv.setText(getResources().getText(R.string.keytouch_nama_description));
                break;
            case R.id.japa_mala_rb:
                drag_and_drop_layout.setVisibility(View.GONE);
                key_touch_layout.setVisibility(View.GONE);
                japa_mala_layout.setVisibility(View.VISIBLE);
                mNamamDesTv.setText(getResources().getText(R.string.japamala_nama_description));
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onDrag(final View view, DragEvent event) {

        // Handles each of the expected events
        switch (event.getAction()) {
            //signal for the start of a drag and drop operation.
            case DragEvent.ACTION_DRAG_STARTED:
                mDragEnterNamamTv.setVisibility(View.GONE);
                mHandImage.setVisibility(View.INVISIBLE);
                break;
            //the drag point has entered the bounding box of the View
            case DragEvent.ACTION_DRAG_ENTERED:
                //	v.setBackground(targetShape);	//change the shape of the view
                break;
            //the user has moved the drag shadow outside the bounding box of the View
            case DragEvent.ACTION_DRAG_EXITED:
                //	v.setBackground(normalShape);	//change the shape of the view back to normal
                mDragEnterNamamTv.setVisibility(View.VISIBLE);
                reSetTheViewOnDragExit(view);
                break;
            //drag shadow has been released,the drag point is within the bounding box of the View
            case DragEvent.ACTION_DROP:
                // if the view is the bottomlinear, we accept the drag item
                if (view == findViewById(R.id.bottomlinear)) {
                    actionDrop = true;
                    mDragEnterNamamTv.setVisibility(View.GONE);
                    mHandImage.setVisibility(View.INVISIBLE);
                    View localView = (View) event.getLocalState();
                    ViewGroup viewgroup = (ViewGroup) localView.getParent();
                    viewgroup.removeView(localView);
                    //change the text
                    LinearLayout containView = (LinearLayout) view;
                    containView.addView(localView);
                    final ImageView bookImg = (ImageView) findViewById(R.id.book_img);
                    final int SDK_INT = android.os.Build.VERSION.SDK_INT;
                    bookImg.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.hand_written_images, null));
                    int time = 200;
                    int totalTime = time * 4;
                    final AnimationDrawable theDogAnimation = (AnimationDrawable) bookImg.getBackground();
                    Log.i(TAG, "theDogAnimation: " + theDogAnimation);
                    theDogAnimation.start();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (mActivity != null) {
                                reSetTheView(view);
                                theDogAnimation.stop();
                                bookImg.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.book_img, null));
                                mDragEnterNamamTv.setVisibility(View.VISIBLE);
                                JSONObject namaObject = (JSONObject) godForChatSpinner.getTag();
                                setTheCountValuesWithUserNamas(false, mTotalCountTv, mPrintedCountTv, mRunningCountTv, "bookNama");
                                actionDrop = false;
                            }
                        }
                    }, totalTime);
                } else {
                    View v = (View) event.getLocalState();
                    v.setVisibility(View.VISIBLE);
                    break;
                }
                break;

            //the drag and drop operation has concluded.
            case DragEvent.ACTION_DRAG_ENDED:
                if (!actionDrop) {
                    mDragEnterNamamTv.setVisibility(View.VISIBLE);
                    mHandImage.setVisibility(View.VISIBLE);
                }
            default:
                break;
        }
        return true;

    }

    @Override
    public boolean onTouch(View view, MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            // create it from the object's tag
            ClipData.Item item = new ClipData.Item((CharSequence) view.getTag());
            String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};
            ClipData data = new ClipData(view.getTag().toString(), mimeTypes,
                    item);
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);

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

    private void getSavedServerCount() {
        Cursor countCursor = DatabaseHelper.getSavedServerCountCursor(mDatabase,mServerCountTableName, mNamakotiId, mUserIdLong);
        if(countCursor != null){
            if (countCursor.getCount() != 0){
                countCursor.moveToFirst();
                mServerTotalCount = countCursor.getInt(countCursor
                        .getColumnIndex(DatabaseHelper.TABLE_SERVER_COUNT.COL_TOTAL_COUNT));
                mServerRuunningCount = countCursor.getInt(countCursor
                        .getColumnIndex(DatabaseHelper.TABLE_SERVER_COUNT.COL_RUNNING_COUNT));
            }
        }
    }

    public void setTheCountValuesWithUserNamas(boolean fromOnItemSelected, TextView tc, TextView pc, TextView rc, final String chantingType) {
        String isUpgraded = UserInfoBean.getInstance().getUpgrade(mActivity);
        long totalCount = mSelectedBean.getNama_total_count();
        long nama_printed_count = mSelectedBean.getNama_printed_count();
        mLocalCount = DatabaseHelper.getSavedLocalCount(mDatabase,mNamakotiId,mLocalCountTableName,mUserIdLong);
        Log.i(TAG, "totalCount: " + totalCount);
        tc.setText(""+mServerTotalCount);
        rc.setText(""+(mServerRuunningCount+mLocalCount));//27+0
        pc.setText(""+nama_printed_count);
        long addTRC;
        if(fromOnItemSelected){
            addTRC = mLocalCount;
        }
        else{//It calls for every chant by dragAndDrop/key_touch/japamala
            addTRC = mLocalCount + 1;//add one to total running count
            Log.i(TAG, "it is not fromOnItemSelected");
            boolean isVoiceEnabled = Utils.getBooleanFromSP(mActivity, VOICE_OVER_KEY, true);
            if (isVoiceEnabled) {
                    playMusic(chantingType);
            }
            if (chantingType.equalsIgnoreCase("japamalaNama")) {
                theDogAnimation.start();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        theDogAnimation.stop();
//                            japaMalavibe.cancel();
                    }
                }, 1600);
            }
        }
        long noCount = 0;
        if (mFromScreen.equalsIgnoreCase(Constants.KEY_SELF_CHANT) || mFromScreen.equalsIgnoreCase(Constants.KEY_CHANT_FOR_CAUSE))
            noCount = mSelectedBean.getNo_chants();
        //saved count in local db table
        ContentValues cv = DatabaseHelper.getInstance().localChantCountQuery(mUserIdLong,
                ""+mSelectedBean.getUser_namakoti_id(),addTRC,mFromScreen, noCount);
        int count = -1;
        count = mDatabase.update(mLocalCountTableName, cv,
                DatabaseHelper.TABLE_LOCAL_COUNT.COL_NAMAKOTI_ID + "=" +mSelectedBean.getUser_namakoti_id(), null);
        if (count != -1 && count == 0) {
            mDatabase.insert(mLocalCountTableName,"firstName", cv);
        }
        long trc = mServerRuunningCount + addTRC;
        rc.setText("" + trc);

        if (isCountMultipleOf100(addTRC)) {
            syncCount(""+mSelectedBean.getUser_namakoti_id());
        }
    }

    private boolean isCountMultipleOf100(long runningCount) {
        if (runningCount < 100) {
            return false;
        }
        Log.i(TAG, "runningCount: " + runningCount);
        Log.i(TAG, "isCountMultipleOf100: " + runningCount % 100f);
        float reminder = runningCount % 100f;
        return reminder == 0 ? true : false;
    }
    private void playMusic(final String chantingType){

        String music = null;
        music = mSelectedBean.getMusic();

        music = music.substring(0, (music.length() - 4)).toLowerCase();
        Log.i(TAG, "music: === " + music);
        int resID = mActivity.getResources().getIdentifier(music, "raw", mActivity.getPackageName());
        if (resID == 0) {
            Log.i(TAG, "sri_rama default");
            resID = mActivity.getResources().getIdentifier("sri_rama", "raw", mActivity.getPackageName());
        }

        play(mActivity, music,resID);

    }

    private MediaPlayer mMediaPlayer;

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void play(Context c, String music, int resID) {
        stop();

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(String.valueOf(Uri.parse(Constants.GOD_IMAGE_BASE_URL+music)+".mp3"));
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e){
            // Catch the exception
            e.printStackTrace();
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }catch (SecurityException e){
            e.printStackTrace();
        }catch (IllegalStateException e){
            e.printStackTrace();
        }
//        mMediaPlayer = MediaPlayer.create(c, Uri.parse(Constants.GOD_IMAGE_BASE_URL+music)resID);
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stop();
            }
        });
//        mMediaPlayer.start();
    }


    /**
     * This method call async class for syncing the count to server
     *
     * @param user_namakoti_id
     */
    public void syncCount(String user_namakoti_id) {
        showProgress(true, mActivity);
        String sqlQuery = "select * from " + mLocalCountTableName;
        Cursor cursor = mDatabase.rawQuery(sqlQuery, null);
        List<String> userNamakotiIDs = new ArrayList<String>();
        List<Integer> userNamakotiCounts = new ArrayList<Integer>();
        if (cursor.moveToFirst()){
            do {
                // Passing values
                String userId = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TABLE_LOCAL_COUNT.COL_USER_ID));
                String namakotiId = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TABLE_LOCAL_COUNT.COL_NAMAKOTI_ID));
                int count = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TABLE_LOCAL_COUNT.COL_RUNNING_TOTAL));

                userNamakotiIDs.add(namakotiId);
                userNamakotiCounts.add(count);
                // Do something Here with values
            } while(cursor.moveToNext());
        }
        cursor.close();

        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", mUserIdString);

        if (mFromScreen.equalsIgnoreCase(Constants.KEY_KARMIC_BEING) || mFromScreen.equalsIgnoreCase(Constants.KEY_KARMIC_OTHERS)){
            params.put("participant_id", userNamakotiIDs.toString());
            params.put("gkccount", userNamakotiCounts.toString());
        }else{
            params.put("user_namakoti_id", userNamakotiIDs.toString());
            params.put("count", userNamakotiCounts.toString());
        }

        VolleyUtil.getInstance().
                volleyStringRequest(this, Constants.SYNC_CHANT, params, ServiceMethod.SYNC_CHANT, Request.Method.POST, this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sync_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sync:
                syncCount(""+mSelectedBean.getUser_namakoti_id());
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.voice:
                String cd = (String) voice.getContentDescription();
                String on = getResources().getString(R.string.on_voice);
                String off = getResources().getString(R.string.off_voice);
                if (cd.equals(on)) {
                    MyApplication.mEditor.putBoolean(VOICE_OVER_KEY, false).commit();
                    voice.setImageResource(R.drawable.chanting_novolume_ic);
                    voice.setContentDescription(off);
                } else if (cd.equals(off)) {
                    MyApplication.mEditor.putBoolean(VOICE_OVER_KEY, true).commit();
                    voice.setImageResource(R.drawable.chanting_volume_ic);
                    voice.setContentDescription(on);
                }
                break;
            case R.id.gods_spinner:
                Intent intent = new Intent(mActivity, SelectChantGodActivity.class);
                intent.putExtra(SelectChantGodActivity.BUNDLE_IS_FROM_CAUSE, mFromScreen);
                startActivityForResult(intent, SELECTED_GOD_NAMA_CODE);
                break;
            case R.id.button_stop:
                finish();

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECTED_GOD_NAMA_CODE) {
            if (resultCode == RESULT_OK) {
                Intent intent = data;
                Bundle bundle = intent.getExtras();
                mSelectedBean = (SaveChantsBean) bundle.getSerializable(SelectChantGodActivity.SELECTED_NAMAS_BEAN_KEY);
                mNamakotiId = mSelectedBean.getUser_namakoti_id();
                getSavedServerCount();
                mLocalCount = DatabaseHelper.getSavedLocalCount(mDatabase,mNamakotiId,mLocalCountTableName,mUserIdLong);
                onNamamSelection();
            }
        }
    }

    @Override
    public void successResponse(ServiceMethod serviceMethod, Object object) {
        showProgress(false, mActivity);
        if (object != null && object instanceof SyncChantsBean) {
            SyncChantsBean bean = (SyncChantsBean) object;

            if (bean != null) {
                if (mFromScreen.equalsIgnoreCase(Constants.KEY_CHANT_FOR_CAUSE)) {
                    if (bean.causenamas != null && bean.causenamas.size() > 0) {
                        for (int i = 0; i < bean.causenamas.size(); i++) {
                            updateDb(bean.causenamas, i);
                        }
                        updateAndSetResult();
                    }
                } else if (mFromScreen.equalsIgnoreCase(Constants.KEY_SELF_CHANT)) {
                    if (bean.namas != null && bean.namas.size() > 0) {
                        for (int i = 0; i < bean.namas.size(); i++) {
                            updateDb(bean.namas, i);
                        }
                        updateAndSetResult();
                    }
                }else if (mFromScreen.equalsIgnoreCase(Constants.KEY_KARMIC_BEING)) {

                }
                else if (mFromScreen.equalsIgnoreCase(Constants.KEY_KARMIC_OTHERS)) {

                }
            }
        }
    }

    private void updateAndSetResult() {
        setTheCountValuesWithUserNamas(true, mTotalCountTv, mPrintedCountTv, mRunningCountTv, "default");
        Intent intent = new Intent();
        intent.putExtra(EXTRA_IS_SYNC, true);
        setResult(RESULT_OK, intent);
    }

    private void updateDb(ArrayList<SyncChantsBean.SaveNamas> namas, int i) {
        long result = -1;
        SyncChantsBean.SaveNamas chantBean = namas.get(i);
        ContentValues localCv = DatabaseHelper.getInstance().localChantCountQuery(mUserIdLong,
                ""+chantBean.user_namakoti_id,0, mFromScreen, chantBean.no_chants);
        ContentValues serverCv = DatabaseHelper.getInstance().serverChantCountQuery(mUserIdLong,
                ""+chantBean.user_namakoti_id,chantBean.nama_total_count,chantBean.nama_running_count,mFromScreen, chantBean.no_chants);
        updateLocalCountTable(mDatabase,localCv,chantBean.user_namakoti_id, mLocalCountTableName,mUserIdLong);
        updateServerCountTable(mDatabase,serverCv,chantBean.user_namakoti_id, mServerCountTableName,mUserIdLong);
        ContentValues updatenamasCv = DatabaseHelper.getInstance().getNormalSyncNamasContentValues(chantBean,mUserIdLong);
        result = mDatabase.update(mNamasTableName,updatenamasCv,
                DatabaseHelper.TABLE_NAMAS.COL_USER_NAMAKOTI_ID+ "=" +chantBean.user_namakoti_id, null);
        if (result != -1 && result == 0)
            mDatabase.insert(mNamasTableName,"firstName", updatenamasCv);

        getSavedServerCount();
        DatabaseHelper.getSavedLocalCount(mDatabase,mNamakotiId,mLocalCountTableName,mUserIdLong);
    }

    @Override
    public void errorResponse(ServiceMethod serviceMethod, Object object) {
        showProgress(false, mActivity);
        handleErrorMessage(object);
    }


    @Override
    public HashMap<String, String> getParamsMap(ServiceMethod serviceMethod) {
        return null;
    }

    @Override
    public JSONObject getParamsJson(ServiceMethod serviceMethod) {
        return null;
    }

    public class JapaMalaTouchListener implements View.OnTouchListener {

        private GestureDetectorCompat mGestureDetector;

        public JapaMalaTouchListener(GestureDetectorCompat mGestureDetector) {
            this.mGestureDetector = mGestureDetector;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mGestureDetector.onTouchEvent(event);
            return true;
        }

    }

    /**
     * It enables namas's buttons according to sequence
     */
    public class BtnNamaOnCLick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            Button selectedNamaBtn = namasBtns.get(id);
            selectedNamaBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.buton_unselect, null));
            selectedNamaBtn.setEnabled(false);

            if (id == namasBtns.size() - 1) {
                namasBtns.get(0).setEnabled(true);
                namasBtns.get(0).setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selector_4_key_touch, null));
                setTheCountValuesWithUserNamas(false, mTotalCountTv, mPrintedCountTv, mRunningCountTv, "buttonNama");
            } else {
                Log.i(TAG, "id: " + id);
                namasBtns.get(id + 1).setEnabled(true);
                namasBtns.get(id + 1).setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selector_4_key_touch, null));
            }
        }
    }
}
