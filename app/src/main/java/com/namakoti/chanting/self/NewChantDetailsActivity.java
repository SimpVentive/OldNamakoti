package com.namakoti.chanting.self;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GestureDetectorCompat;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;

import android.text.Spanned;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.NetworkImageView;
import com.namakoti.R;
import com.namakoti.base.BaseActivity;
import com.namakoti.beans.GodNamesBean;
import com.namakoti.beans.SaveChantsBean;
import com.namakoti.beans.SyncChantKarmicBean;
import com.namakoti.beans.SyncChantsBean;
import com.namakoti.beans.UserInfoBean;
import com.namakoti.components.GestureSwipeListener;
import com.namakoti.components.MyGestureDetector;
import com.namakoti.database.DatabaseHelper;
import com.namakoti.karmic.PangchangFragment;
import com.namakoti.karmic.TopListFragment;
import com.namakoti.karmic.beingHelped.ParticipantsListActivity;
import com.namakoti.network.services.DownloadService;
import com.namakoti.settings.PaymentHistoryFragment;
import com.namakoti.settings.PrintFragment;
import com.namakoti.settings.SelectChantGodActivity;
import com.namakoti.utils.Constants;
import com.namakoti.utils.ServiceMethod;
import com.namakoti.utils.Utils;
import com.namakoti.utils.VolleyResponseListener;
import com.namakoti.utils.VolleyUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.os.Build.VERSION.SDK_INT;
import static com.namakoti.base.BaseFragment.getToolbarLogoIcon;
import static com.namakoti.utils.Utils.convertLongToInt;

/**
 * Created by anusha on 7/7/2018.
 */

public class NewChantDetailsActivity extends BaseActivity implements VolleyResponseListener, View.OnClickListener,
        View.OnTouchListener, View.OnDragListener, RadioGroup.OnCheckedChangeListener,
        GestureSwipeListener {

    public static final String EXTRA_IS_SYNC = "sync_updated_key";
    public static final String BUNDLE_IS_FROM_CAUSE = "is_from_chant_cause";
    public static final String BUNDLE_NAMAS_BEAN_KEY = "BUNDLE_NAMAS_BEAN_KEY";
    public static final String KEY_TOTAL_COUNT = "KEY_TOTAL_COUNT";
    public static final String GKC_SETUPID = "GKC_SETUPID";
    private static final String TAG = ChantingDetailsActivity.class.getName();
    private static final String VOICE_OVER_KEY = "voice_key";
    private static final String IMAGEVIEW_TAG = "Feather Logo";
    @BindView(R.id.includeDescription)
    CardView includeDescription;
    @BindView(R.id.god_image)
    NetworkImageView mGodImageView;
    @BindView(R.id.godNameTv)
    TextView mGodForChatSpinner;
    @BindView(R.id.selectLL)
    LinearLayout mSelectGodLL;
    @BindView(R.id.volumeIv)
    ImageView mVoiceIv;
    @BindView(R.id.totalNumberOfChantsTV)
    TextView mTotalNumberOfChantsTV;
    @BindView(R.id.chantProgressBar)
    ProgressBar mChantProgressBar;
    @BindView(R.id.chantTotalCountTV)
    TextView mChantTotalCountTV;
    @BindView(R.id.chantRunningCountTv)
    TextView mChantRunningCountTv;
    @BindView(R.id.printTotalCountTV)
    TextView mPrintTotalCountTV;
    @BindView(R.id.printRunningCountTv)
    TextView mPrintRunningCountTv;
    @BindView(R.id.printProgressBar)
    ProgressBar mPrintProgressBar;
    @BindView(R.id.includeChantImage)
    LinearLayout includeChantImage;
    @BindView(R.id.includePrintImage)
    LinearLayout includePrintImage;
    @BindView(R.id.includePanchangImage)
    LinearLayout includePanchangImage;
    @BindView(R.id.includeServicesImage)
    LinearLayout includeServicesImage;
    @BindView(R.id.includeTopListImage)
    LinearLayout includeTopListImage;
    @BindView(R.id.topListFragment)
    FrameLayout mTopListFL;

    @BindView(R.id.includeDragLayout)
    LinearLayout includeDragLayout;
    @BindView(R.id.includePrintLayout)
    RelativeLayout includePrintLayout;
    @BindView(R.id.includePanchangLayout)
    LinearLayout includePanchangLayout;
    @BindView(R.id.includeServiceLayout)
    LinearLayout includeServicesLayout;
    @BindView(R.id.feather_layout)
    LinearLayout feather_layout;
    @BindView(R.id.image_feather)
    ImageView mHandImage;
    @BindView(R.id.japa_mala_img_id)
    ImageView japa_mala_img;
    @BindView(R.id.namam_text)
    TextView mDragEnterNamamTv;

    @BindView(R.id.totalChantProgressRL)
    RelativeLayout mTotalChantProgressRL;
    @BindView(R.id.firstProgressPintTitleTv)
    TextView mFirstProgressPintTitleTv;

    private int SELECTED_GOD_NAMA_CODE = 100;
    private NewChantDetailsActivity mActivity;
    private TextView mDescriptionTv;
    private TextView mDateTv;
    private TextView mByNameTv;
    private ImageView mChantIv;
    private TextView mChantTypeTv;
    private TextView mChantSeparatorTv;
    private ImageView mPrintIv;
    private TextView mPrintTypeTv;
    private TextView mPrintSeparatorTv;
    private ImageView mPanchangIv;
    private TextView mPanchangTypeTv;
    private TextView mPanchangSeparatorTv;
    private ImageView mServiceIv;
    private TextView mServiceTypeTv;
    private TextView mServiceSeparatorTv;
    private TextView mChanTitleTv;
    private ImageView mRightArrowIv;
    private ImageView mLeftArrowIv;
    private View mDragAndDropLayout;
    private View mKeyTouchLayout;
    private View mJapaMalaLayout;
    private ArrayList<GodNamesBean> mGodList;
    private List<TextView> mNamasBtns = new ArrayList<>();

    private String mFromScreen;
    private String mNamasTableName;
    private String mLocalCountTableName;
    private String mServerCountTableName;
    private int mNamakotiId;
    private long mLocalCount;
    private long mMyChantServerTotalCount;
    private long mMyChantServerRunningCount;
    private long mAllChantServerTotalCount;
    private long mAllChantServerRunningCount;
    private SaveChantsBean mSelectedBean;
    private AnimationDrawable theDogAnimation;
    private boolean actionDrop = false;
    private MediaPlayer mMediaPlayer;
    private PrintFragment mPrintFragment;
    private PaymentHistoryFragment mPayHistoryFragment;
    private RadioGroup mPayHistoryRg;
    private RadioButton mPrintChantRb;
    private RadioButton mHistoryRb;
    private FrameLayout mPrintFL;
    private FrameLayout mHistoryFL;
    //    private int mTotalChantCount;
    private PangchangFragment mPangchangFragment;
    private String mGkcSetupId;
    private TopListFragment mTopListFragment;
    private ImageView mTopListIv;
    private TextView mTopListTypeTv;
    private TextView mTopListSeparatorTv;
    private String mCurrentView;
    private String mCreatedBy;

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_chant_details_layout);
        ButterKnife.bind(this);
        mActivity = this;

        Cursor mCursorGod = DatabaseHelper.getGodImages(mDatabase);
        mGodList = getGodListFromDb(mCursorGod);
        getBundle();
        toolbar();
        initUI();
        initListener();

        getSavedServerCount();
        mLocalCount = DatabaseHelper.getSavedLocalCount(mDatabase, mNamakotiId, mLocalCountTableName, mUserIdLong);

        ViewTreeObserver observer = mGodForChatSpinner.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                int h = japa_mala_img.getHeight();
                int w = (int) (0.34f * h);
                int total_w = h + w;
                mDragEnterNamamTv.setVisibility(View.VISIBLE);
                updateNamamImage();
                setTheCountValuesWithUserNamas(true, "default", true);
                if (SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    mGodForChatSpinner.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mGodForChatSpinner.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
        // Sets the tag
        mHandImage.setTag(IMAGEVIEW_TAG);

        mPangchangFragment = new PangchangFragment();
        FragmentManager manager = getSupportFragmentManager();//create an instance of fragment manager
        FragmentTransaction transaction = manager.beginTransaction();//create an instance of Fragment-transaction
        transaction.add(R.id.pangchangFragment, mPangchangFragment, PangchangFragment.class.getName());
        if (mFromScreen.equalsIgnoreCase(Constants.KEY_KARMIC_BEING) || mFromScreen.equalsIgnoreCase(Constants.KEY_KARMIC_OTHERS)) {
            mTopListFragment = new TopListFragment();
            transaction.add(R.id.topListFragment, mTopListFragment, TopListFragment.class.getName());
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    registerReceiver(brd_receiver, new IntentFilter(Constants.UPDATE_CHANT_COUNT_INTENT_ACTION), Context.RECEIVER_EXPORTED);
                } else {
                    registerReceiver(brd_receiver, new IntentFilter(Constants.UPDATE_CHANT_COUNT_INTENT_ACTION));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mPrintFragment = new PrintFragment();
            mPayHistoryFragment = new PaymentHistoryFragment();
            transaction.add(R.id.payFragment, mPrintFragment, PrintFragment.class.getName());
            transaction.add(R.id.payHistoryFragment, mPayHistoryFragment, PaymentHistoryFragment.class.getName());
        }
        transaction.commit();
        setFragmentsVisibility();
        setChantTypeVisibility(true, false, false);
        setEnableView(mRightArrowIv);
        setDisableView(mLeftArrowIv);
        mCurrentView = getString(R.string.draganddrop);
    }

    private void setFragmentsVisibility() {
        if (mFromScreen != null) {
            if (mFromScreen.equalsIgnoreCase(Constants.KEY_KARMIC_BEING) || mFromScreen.equalsIgnoreCase(Constants.KEY_KARMIC_OTHERS)) {
                includePrintImage.setVisibility(View.GONE);
                mPrintFL.setVisibility(View.GONE);
                mTopListFL.setVisibility(View.VISIBLE);
                includeTopListImage.setVisibility(View.VISIBLE);
                if (mTopListFragment != null) {
                    mTopListFragment.callGetTopList(mGkcSetupId, mNamakotiId);
                }
            } else {
                mTopListFL.setVisibility(View.GONE);
                includeTopListImage.setVisibility(View.GONE);
                includePrintImage.setVisibility(View.VISIBLE);
                mPrintFL.setVisibility(View.VISIBLE);
                mPrintFragment.updateUI(mNamakotiId, mSelectedBean, mFromScreen, mLocalCount);
                mPayHistoryFragment.updateUI(mNamakotiId, mSelectedBean, mFromScreen);
            }
        }
    }

    public BroadcastReceiver brd_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateTopListChantCount(intent);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(brd_receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTopListChantCount(Intent intent) {
        if (intent != null) {
            if (intent.getAction().equals(Constants.UPDATE_CHANT_COUNT_INTENT_ACTION)) {
                mAllChantServerTotalCount = intent.getIntExtra(TopListFragment.CHANT_TOTAL_COUNT, 0);
                mAllChantServerRunningCount = intent.getIntExtra(TopListFragment.CHANT_PRESENT_COUNT, 0);
                mMyChantServerTotalCount = intent.getIntExtra(TopListFragment.MY_CHANT_TOTAL_COUNT, 0);
                mMyChantServerRunningCount = intent.getIntExtra(TopListFragment.MY_CHANT_PRESENT_COUNT, 0);

                DatabaseHelper.getInstance().updateKarmicSyncNamasQuery(mDatabase, mFromScreen, mMyChantServerTotalCount, mMyChantServerRunningCount, mUserIdLong, mNamakotiId);
                updateServerCountInDb(mNamakotiId, mMyChantServerTotalCount, mMyChantServerRunningCount, 0);
                getSavedServerCount();
                mLocalCount = DatabaseHelper.getSavedLocalCount(mDatabase, mNamakotiId, mLocalCountTableName, mUserIdLong);

                setTheCountValuesWithUserNamas(true, "default", false);
            }
        }
    }

    private void setSeparatorColor(boolean first, boolean sec, boolean third, boolean fourth, boolean fifth) {
        mChantSeparatorTv.setBackgroundColor(ResourcesCompat.getColor(getResources(), android.R.color.white, null));
        mPrintSeparatorTv.setBackgroundColor(ResourcesCompat.getColor(getResources(), android.R.color.white, null));
        mPanchangSeparatorTv.setBackgroundColor(ResourcesCompat.getColor(getResources(), android.R.color.white, null));
        mServiceSeparatorTv.setBackgroundColor(ResourcesCompat.getColor(getResources(), android.R.color.white, null));
        mTopListSeparatorTv.setBackgroundColor(ResourcesCompat.getColor(getResources(), android.R.color.white, null));

        if (first) {
            mChantSeparatorTv.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
        } else if (sec) {
            mPrintSeparatorTv.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
        } else if (third) {
            mPanchangSeparatorTv.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
        } else if (fourth) {
            mServiceSeparatorTv.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
        } else if (fifth) {
            mTopListSeparatorTv.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
        }

    }

    private void initUI() {
        mDescriptionTv = includeDescription.findViewById(R.id.descriptionTv);
        mByNameTv = includeDescription.findViewById(R.id.byNameTv);
        mDateTv = includeDescription.findViewById(R.id.dateTv);

        mChantIv = includeChantImage.findViewById(R.id.chantIv);
        mChantTypeTv = includeChantImage.findViewById(R.id.categoryTv);
        mChantSeparatorTv = includeChantImage.findViewById(R.id.separatorTv);
        mPrintIv = includePrintImage.findViewById(R.id.chantIv);
        mPrintTypeTv = includePrintImage.findViewById(R.id.categoryTv);
        mPrintSeparatorTv = includePrintImage.findViewById(R.id.separatorTv);
        mPanchangIv = includePanchangImage.findViewById(R.id.chantIv);
        mPanchangTypeTv = includePanchangImage.findViewById(R.id.categoryTv);
        mPanchangSeparatorTv = includePanchangImage.findViewById(R.id.separatorTv);
        mServiceIv = includeServicesImage.findViewById(R.id.chantIv);
        mServiceTypeTv = includeServicesImage.findViewById(R.id.categoryTv);
        mServiceSeparatorTv = includeServicesImage.findViewById(R.id.separatorTv);
        mTopListIv = includeTopListImage.findViewById(R.id.chantIv);
        mTopListTypeTv = includeTopListImage.findViewById(R.id.categoryTv);
        mTopListSeparatorTv = includeTopListImage.findViewById(R.id.separatorTv);

        mChanTitleTv = includeDragLayout.findViewById(R.id.chantTitleTv);
        mLeftArrowIv = includeDragLayout.findViewById(R.id.leftArrowIv);
        mRightArrowIv = includeDragLayout.findViewById(R.id.rightArrowIv);
        mDragAndDropLayout = includeDragLayout.findViewById(R.id.drag_and_drop_layout);
        mKeyTouchLayout = includeDragLayout.findViewById(R.id.key_touch_layout);
        mJapaMalaLayout = includeDragLayout.findViewById(R.id.japa_mala_layout);

        mPayHistoryRg = includePrintLayout.findViewById(R.id.payHistoryRg);
        mPrintChantRb = includePrintLayout.findViewById(R.id.printChantRb);
        mHistoryRb = includePrintLayout.findViewById(R.id.historyRb);
        mPrintFL = includePrintLayout.findViewById(R.id.payFragment);
        mHistoryFL = includePrintLayout.findViewById(R.id.payHistoryFragment);

        mDescriptionTv.setVisibility(View.GONE);
        mChanTitleTv.setText(R.string.draganddrop);
        mChantIv.setImageResource(R.drawable.chanting_smallhand_ic);
        mChantTypeTv.setText(getResources().getString(R.string.chanting));
        mChantSeparatorTv.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
        mPrintIv.setImageResource(R.drawable.chanting_print_ic);
        mPrintTypeTv.setText(getResources().getString(R.string.print));
        mPrintSeparatorTv.setBackgroundColor(ResourcesCompat.getColor(getResources(), android.R.color.white, null));
        mPanchangIv.setImageResource(R.drawable.chanting_panchang_ic);
        mPanchangTypeTv.setText(getResources().getString(R.string.panchang));
        mPanchangSeparatorTv.setBackgroundColor(ResourcesCompat.getColor(getResources(), android.R.color.white, null));
        mServiceIv.setImageResource(R.drawable.chanting_services_ic);
        mServiceTypeTv.setText(getResources().getString(R.string.service));
        mServiceSeparatorTv.setBackgroundColor(ResourcesCompat.getColor(getResources(), android.R.color.white, null));
        mTopListIv.setImageResource(R.drawable.chanting_panchang_ic);
        mTopListTypeTv.setText("TopList");
        mTopListSeparatorTv.setBackgroundColor(ResourcesCompat.getColor(getResources(), android.R.color.white, null));

        mTotalNumberOfChantsTV.setText("" + mSelectedBean.getNama_total_count());
    }

    private void getBundle() {
        Intent intent = getIntent();
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                mSelectedBean = (SaveChantsBean) bundle.getSerializable(BUNDLE_NAMAS_BEAN_KEY);
                mCreatedBy = intent.getStringExtra(ParticipantsListActivity.CREATED_BY_KEY);

//                mTotalChantCount = bundle.getInt(KEY_TOTAL_COUNT);
                mNamakotiId = mSelectedBean.getUser_namakoti_id();
                mGkcSetupId = bundle.getString(GKC_SETUPID);
                mFromScreen = bundle.getString(BUNDLE_IS_FROM_CAUSE);
                toolbar();
                mTotalChantProgressRL.setVisibility(View.VISIBLE);

                mUserIdLong = UserInfoBean.getInstance().getUserId(mActivity);
                if (mFromScreen.equalsIgnoreCase(Constants.KEY_CHANT_FOR_CAUSE)) {
                    mNamasTableName = DatabaseHelper.CHANT_FOR_CAUSE_NAMAS_TABLE_NAME;
                    mLocalCountTableName = DatabaseHelper.CHANT_FOR_CAUSE__LOCAL_COUNT_TABLE_NAME;
                    mServerCountTableName = DatabaseHelper.CHANT_FOR_CAUSE__SERVER_COUNT_TABLE_NAME;
                    setToolbarTitle(getString(R.string.chant_for_cause));
                    mFirstProgressPintTitleTv.setText("Print Chants");
                } else if (mFromScreen.equalsIgnoreCase(Constants.KEY_SELF_CHANT)) {
                    mNamasTableName = DatabaseHelper.NORMAL_NAMAS_TABLE_NAME;
                    mLocalCountTableName = DatabaseHelper.NORMAL_LOCAL_COUNT_TABLE_NAME;
                    mServerCountTableName = DatabaseHelper.NORMAL_SERVER_COUNT_TABLE_NAME;
                    setToolbarTitle(getString(R.string.normal_chanting));
                    mFirstProgressPintTitleTv.setText("Print Chants");
                } else if (mFromScreen.equalsIgnoreCase(Constants.KEY_KARMIC_BEING)) {
                    mNamasTableName = DatabaseHelper.KARMIC_SELF_NAMAS_TABLE_NAME;
                    mLocalCountTableName = DatabaseHelper.KARMIC_SELF_LOCAL_COUNT_TABLE_NAME;
                    mServerCountTableName = DatabaseHelper.KARMIC_SELF_SERVER_COUNT_TABLE_NAME;
                    setToolbarTitle(getString(R.string.being_helped));
                } else if (mFromScreen.equalsIgnoreCase(Constants.KEY_KARMIC_OTHERS)) {
                    mNamasTableName = DatabaseHelper.KARMIC_OTHERS_NAMAS_TABLE_NAME;
                    mLocalCountTableName = DatabaseHelper.KARMIC_OTHERS_LOCAL_COUNT_TABLE_NAME;
                    mServerCountTableName = DatabaseHelper.KARMIC_OTHERS_SERVER_COUNT_TABLE_NAME;
                    setToolbarTitle(getString(R.string.helping_others));
                    mUserIdLong = UserInfoBean.getInstance().getUserName(mActivity);
                }
                mUserIdString = String.valueOf(mUserIdLong);
            }
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
    }

    private void updateNamamImage() {
        Spanned namam = fromHtml(mSelectedBean.getTheme_name());
        Spanned namaOfGod = fromHtml(mSelectedBean.getSub_theme_name());
        mGodForChatSpinner.setText(namaOfGod.toString());

        String name = (mSelectedBean.getTheme_name().replace(" ", "")).toLowerCase() + getResources().getString(R.string.save_nama_aditional_key);
        Log.e("", "name =====> " + name);
        setImageLoader(mGodList, mGodImageView, namam.toString());
        prepareKeyTouch(namaOfGod.toString());
    }

    private void initListener() {
        mVoiceIv.setOnClickListener(this);
        mSelectGodLL.setOnClickListener(this);
        includeChantImage.setOnClickListener(this);
        includePrintImage.setOnClickListener(this);
        includePanchangImage.setOnClickListener(this);
        includeServicesImage.setOnClickListener(this);
        includeTopListImage.setOnClickListener(this);

        includeDragLayout.setOnClickListener(this);
        includePrintLayout.setOnClickListener(this);
        includePanchangLayout.setOnClickListener(this);
        includeServicesLayout.setOnClickListener(this);
        mLeftArrowIv.setOnClickListener(this);
        mRightArrowIv.setOnClickListener(this);

        mPayHistoryRg.setOnCheckedChangeListener(this);
        Utils.saveBooleanInSP(mActivity, VOICE_OVER_KEY, true);
        boolean isVoiceEnabled = Utils.getBooleanFromSP(mActivity, VOICE_OVER_KEY, false);
        if (isVoiceEnabled) {
            mVoiceIv.setContentDescription(getResources().getString(R.string.on_voice));
            mVoiceIv.setImageResource(R.drawable.chanting_volume_ic);
        } else {
            mVoiceIv.setContentDescription(getResources().getString(R.string.off_voice));
            mVoiceIv.setImageResource(R.drawable.chanting_novolume_ic);
        }
        GestureDetectorCompat mGestureDetector = new GestureDetectorCompat(mActivity, new MyGestureDetector(this));
        japa_mala_img.setOnTouchListener(new JapaMalaTouchListener(mGestureDetector));
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

    private void getSavedServerCount() {
        Cursor countCursor = DatabaseHelper.getSavedServerCountCursor(mDatabase, mServerCountTableName, mNamakotiId, mUserIdLong);
        if (countCursor != null) {
            if (countCursor.getCount() != 0) {
                countCursor.moveToFirst();
                mMyChantServerTotalCount = countCursor.getInt(countCursor.getColumnIndexOrThrow(DatabaseHelper.TABLE_SERVER_COUNT.COL_TOTAL_COUNT));
                mMyChantServerRunningCount = countCursor.getInt(countCursor.getColumnIndexOrThrow(DatabaseHelper.TABLE_SERVER_COUNT.COL_RUNNING_COUNT));
                if (mFromScreen.equalsIgnoreCase(Constants.KEY_CHANT_FOR_CAUSE) || mFromScreen.equalsIgnoreCase(Constants.KEY_SELF_CHANT)) {
                    mMyChantServerTotalCount = countCursor.getInt(countCursor.getColumnIndexOrThrow(DatabaseHelper.TABLE_SERVER_COUNT.COL_NO_TOTAL_COUNT));
                    mMyChantServerRunningCount = countCursor.getInt(countCursor.getColumnIndexOrThrow(DatabaseHelper.TABLE_SERVER_COUNT.COL_TOTAL_COUNT));
                }
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checked) {
        if (checked == R.id.historyRb) {
            mPrintFL.setVisibility(View.GONE);
            mHistoryFL.setVisibility(View.VISIBLE);
        } else if (checked == R.id.printChantRb) {
            mPrintFL.setVisibility(View.VISIBLE);
            mHistoryFL.setVisibility(View.GONE);
        }
    }

    @Override
    public void successResponse(ServiceMethod serviceMethod, Object object) {
        showProgress(false, mActivity);
        if (object != null && object instanceof SyncChantKarmicBean) {
            SyncChantKarmicBean bean = (SyncChantKarmicBean) object;
            if (bean != null) {
                updateDb(null, 0, bean);
                updateAndSetResult();
            }
        } else if (object != null && object instanceof SyncChantsBean) {
            SyncChantsBean bean = (SyncChantsBean) object;

            if (bean != null) {
                if (mFromScreen.equalsIgnoreCase(Constants.KEY_CHANT_FOR_CAUSE)) {
                    if (bean.causenamas != null && bean.causenamas.size() > 0) {
                        for (int i = 0; i < bean.causenamas.size(); i++) {
                            updateDb(bean.causenamas, i, null);
                        }
                    }
                } else if (mFromScreen.equalsIgnoreCase(Constants.KEY_SELF_CHANT)) {
                    if (bean.namas != null && bean.namas.size() > 0) {
                        for (int i = 0; i < bean.namas.size(); i++) {
                            updateDb(bean.namas, i, null);
                        }
                    }
                }
                updateAndSetResult();
            }
        }
    }

    private void updateAndSetResult() {
        getSavedServerCount();
        mLocalCount = DatabaseHelper.getSavedLocalCount(mDatabase, mNamakotiId, mLocalCountTableName, mUserIdLong);
        setTheCountValuesWithUserNamas(true, "default", false);
        Intent intent = new Intent();

        intent.putExtra(EXTRA_IS_SYNC, true);
        setResult(RESULT_OK, intent);
    }

    private void updateDb(ArrayList<SyncChantsBean.SaveNamas> namas, int i, SyncChantKarmicBean bean) {
        long result = -1;
        int namakotiID = 0;
        long totalCount = 0;
        long runningCount = 0;
        long noCount = 0;
        ContentValues updatenamasCv = null;
        if (namas != null) {
            SyncChantsBean.SaveNamas chantBean = namas.get(i);
            namakotiID = chantBean.user_namakoti_id;
            totalCount = chantBean.nama_total_count;
            noCount = chantBean.no_chants;
            runningCount = chantBean.nama_running_count;
            updatenamasCv = DatabaseHelper.getInstance().getNormalSyncNamasContentValues(chantBean, UserInfoBean.getInstance().getUserId(mActivity));
            result = mDatabase.update(mNamasTableName, updatenamasCv,
                    DatabaseHelper.TABLE_NAMAS.COL_USER_NAMAKOTI_ID + "=" + namakotiID, null);
            if (result != -1 && result == 0)
                mDatabase.insert(mNamasTableName, "firstName", updatenamasCv);

        } else if (bean != null) {
            if (bean.mychants != null) {
                namakotiID = Integer.parseInt(bean.mychants.participant_id);
                totalCount = bean.mychants.chants_count_will_do;
                runningCount = bean.mychants.orginal_chants_count;
                DatabaseHelper.getInstance().updateKarmicSyncNamasQuery(mDatabase, mFromScreen, mMyChantServerTotalCount, mMyChantServerRunningCount, mUserIdLong, mNamakotiId);
            }
            if (bean.chantsdetails != null) {
                mAllChantServerRunningCount = bean.chantsdetails.presentchants;
                mAllChantServerTotalCount = bean.chantsdetails.totalchants;
            }
            if (mTopListFragment != null)
                mTopListFragment.updateUi(bean, true);
        }
        updateLocalCountInDb(namakotiID, 0);
        updateServerCountInDb(namakotiID, totalCount, runningCount, noCount);
    }

    /**
     * updated only my chant count in servercount table
     *
     * @param namakotiID
     * @param total
     * @param running
     */
    private void updateServerCountInDb(int namakotiID, long total, long running, long noCount) {
//        long noCount = 0;
//        if (mFromScreen.equalsIgnoreCase(Constants.KEY_SELF_CHANT) || mFromScreen.equalsIgnoreCase(Constants.KEY_CHANT_FOR_CAUSE))
//            noCount = mSelectedBean.getNo_chants();
        long userId = (mFromScreen.equalsIgnoreCase(Constants.KEY_KARMIC_OTHERS)) ? UserInfoBean.getInstance().getUserName(mActivity) : mUserIdLong;

        ContentValues serverCv = DatabaseHelper.getInstance().serverChantCountQuery(userId, "" + namakotiID, total, running, mFromScreen, noCount);
        updateServerCountTable(mDatabase, serverCv, namakotiID, mServerCountTableName, userId);
    }

    private void updateLocalCountInDb(int namakotiID, long running) {
        long noCount = 0;
        if (mFromScreen.equalsIgnoreCase(Constants.KEY_SELF_CHANT) || mFromScreen.equalsIgnoreCase(Constants.KEY_CHANT_FOR_CAUSE))
            noCount = mSelectedBean.getNo_chants();

        long userId = (mFromScreen.equalsIgnoreCase(Constants.KEY_KARMIC_OTHERS)) ? UserInfoBean.getInstance().getUserName(mActivity) : mUserIdLong;

        ContentValues localCv = DatabaseHelper.getInstance().localChantCountQuery(userId, "" + namakotiID, running, mFromScreen, noCount);
        updateLocalCountTable(mDatabase, localCv, namakotiID, mLocalCountTableName, userId);
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

    @Override
    public boolean onDrag(final View view, DragEvent event) {

        // Handles each of the expected events
        switch (event.getAction()) {
            //signal for the start of a drag and drop operation.
            case DragEvent.ACTION_DRAG_STARTED:
//                mDragEnterNamamTv.setVisibility(View.GONE);
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
//                    mDragEnterNamamTv.setVisibility(View.GONE);
                    mHandImage.setVisibility(View.GONE);
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
                                JSONObject namaObject = (JSONObject) mGodForChatSpinner.getTag();
                                setTheCountValuesWithUserNamas(false, "bookNama", false);
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

    private void prepareKeyTouch(String namaOfGod) {
        Log.i(TAG, "namam: " + namaOfGod);
        String[] namas = namaOfGod.split(" ");
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(30, 0, 10, 0);

        LinearLayout layout = (LinearLayout) findViewById(R.id.nama_btns_ll);
        layout.setOrientation(LinearLayout.VERTICAL);
        mNamasBtns.clear();//Can also be done in xml by android:orientation="vertical"
        layout.removeAllViewsInLayout();
        TableRow row;
        final int screenWidth = Utils.dpToPx(mActivity, getResources().getConfiguration().screenWidthDp) - 100;
        if (namas != null && namas.length > 0) {

            int i = 0;
            int j = 0;
            int count = 0;

            int width = (screenWidth - (namas.length) * 4) / namas.length;

            while (i < namas.length) {
                j = i + 3;
                row = new TableRow(mActivity);
                TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(lp);
                row.setGravity(Gravity.LEFT | Gravity.CENTER);
                row.setWeightSum(3.3f);
                row.setPadding(0, 5, 0, 5);

                while ((i < j) && (i < namas.length)) {
                    LinearLayout mainLayout = new LinearLayout(mActivity);
                    mainLayout.setGravity(Gravity.CENTER);
                    mainLayout.setOrientation(LinearLayout.HORIZONTAL);
//                    LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                    lp1.setMargins(5, 3, 5, 3);
//                    mainLayout.setLayoutParams(lp1);

                    TextView nama_btn = new TextView(mActivity);
//                    Typeface myTypeface = Utils.getTypeFace(mActivity, "regular");
//                    titletv.setTypeface(myTypeface);
                    nama_btn.setGravity(Gravity.CENTER);
                    LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    nama_btn.setLayoutParams(layoutParams1);
                    nama_btn.setPadding(5, 3, 5, 3);
                    nama_btn.setText(namas[i]);
                    nama_btn.setId(count);
                    nama_btn.setTag(namas[i]);

                    nama_btn.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.white, null));
                    nama_btn.setOnClickListener(new BtnNamaOnCLick());
                    if (count == 0) {
                        mainLayout.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selector_4_key_touch, null));
                        nama_btn.setEnabled(true);
                    } else {
                        mainLayout.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.buton_unselect, null));
                        nama_btn.setEnabled(false);
                    }

                    mainLayout.addView(nama_btn);
                    row.addView(mainLayout, 3 + i - j);

                    mNamasBtns.add(nama_btn);
                    i++;
                    count++;
                }
                layout.addView(row);
            }
        }
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
        setTheCountValuesWithUserNamas(false, "japamalaNama", false);
    }

    public void setTheCountValuesWithUserNamas(boolean fromOnItemSelected, final String chantingType, boolean isFronInitial) {
        String isUpgraded = UserInfoBean.getInstance().getUpgrade(mActivity);
        mDragEnterNamamTv.setText(fromHtml(mSelectedBean.getSub_theme_name()));


        if (!isFronInitial && !chantingType.equalsIgnoreCase("default")) {
            if (mPrintRunningCountTv != null && mPrintRunningCountTv != null) {
                int tc = Integer.parseInt(mPrintTotalCountTV.getText().toString());
                int rc = Integer.parseInt(mPrintRunningCountTv.getText().toString());
                if (rc >= tc) {
                    Utils.showAlertDialog(mActivity, "Error", "You reached total chanting count", null, null, false, true);
                    return;
                }
            }
        }
        mLocalCount = DatabaseHelper.getSavedLocalCount(mDatabase, mNamakotiId, mLocalCountTableName, mUserIdLong);

        long addTRC;
        if (fromOnItemSelected) {
            addTRC = mLocalCount;
        } else {//It calls for every chant by dragAndDrop/key_touch/japamala
            addTRC = mLocalCount + 1;//add one to total running count
            Log.i(TAG, "it is not fromOnItemSelected");
            boolean isVoiceEnabled = Utils.getBooleanFromSP(mActivity, VOICE_OVER_KEY, false);
            if (isVoiceEnabled) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        playMusic(chantingType);
                    }
                }, 10);

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
                }, 1500);
            }
        }
        updateLocalCountInDb(mNamakotiId, addTRC);

        long myChantTrc = mMyChantServerRunningCount + addTRC /*+ mSelectedBean.getNama_printed_count()*/;//running + print = total count + local count
        mTotalNumberOfChantsTV.setText("" + myChantTrc);
        mPrintTotalCountTV.setText("" + mMyChantServerTotalCount);
        mPrintRunningCountTv.setText("" + myChantTrc);//27+0
        if (mFromScreen.equalsIgnoreCase(Constants.KEY_KARMIC_BEING) || mFromScreen.equalsIgnoreCase(Constants.KEY_KARMIC_OTHERS)) {
            long allChantTrc = mAllChantServerRunningCount + addTRC;
            mChantTotalCountTV.setText("" + mAllChantServerTotalCount);
            mChantRunningCountTv.setText("" + allChantTrc);
            setProgressValue(convertLongToInt(allChantTrc), convertLongToInt(mAllChantServerTotalCount), false);

            mTotalNumberOfChantsTV.setText("" + (mAllChantServerRunningCount + addTRC));
            if ((UserInfoBean.getInstance().getUserName(mActivity) != Long.parseLong(mCreatedBy)) &&
                    mMyChantServerTotalCount == myChantTrc) {
                Utils.showAlertDialog(mActivity, "", "Thank you for chanting", null, null, false, true);
            }
        } else {
            mChantTotalCountTV.setText("" + mSelectedBean.getNo_chants());
            mChantRunningCountTv.setText("" + mSelectedBean.getNama_printed_count());
            setProgressValue(convertLongToInt(mSelectedBean.getNama_printed_count()), convertLongToInt(mSelectedBean.getNo_chants()), false);
        }

        setProgressTextColor(mChantRunningCountTv, mChantTotalCountTV);
        setProgressTextColor(mPrintRunningCountTv, mPrintTotalCountTV);


        setProgressValue(convertLongToInt(myChantTrc), convertLongToInt(mMyChantServerTotalCount), true);
        if (isCountMultipleOf100(addTRC)) {
            syncCount("" + mNamakotiId);
        }

    }

    private void setProgressTextColor(TextView runningCountTv, TextView totalTv) {
        int cc = Integer.parseInt(totalTv.getText().toString());
        /*if ((cc)>(Integer.parseInt(runningCountTv.getText().toString())*0.5))
            runningCountTv.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        else{
            runningCountTv.setTextColor(ContextCompat.getColor(this, R.color.chant_gray));
        }*/

        if ((cc * (0.1)) < Integer.parseInt(runningCountTv.getText().toString()))
            runningCountTv.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        else {
            runningCountTv.setTextColor(ContextCompat.getColor(this, R.color.chant_gray));
        }
    }

    private void setProgressValue(int runningCount, int totalCount, boolean myChant) {
        TextView tc;
        TextView rc;
        ProgressBar progress;
        if (myChant) {
            tc = mPrintTotalCountTV;
            rc = mPrintRunningCountTv;
            progress = mPrintProgressBar;
            progress.setMinimumHeight(mPrintTotalCountTV.getHeight());
        } else {
            tc = mChantTotalCountTV;
            rc = mChantRunningCountTv;
            progress = mChantProgressBar;
            progress.setMinimumHeight(mPrintTotalCountTV.getHeight());
        }
//        rc.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.white, null));
        progress.setMax(totalCount);

        LayerDrawable progressBarDrawable = (LayerDrawable) progress.getProgressDrawable();
        Drawable backgroundDrawable = progressBarDrawable.getDrawable(0);
        Drawable progressDrawable = progressBarDrawable.getDrawable(1);
        if (myChant)
            backgroundDrawable.setColorFilter(ContextCompat.getColor(this, R.color.light_green), PorterDuff.Mode.SRC_IN);
        else
            backgroundDrawable.setColorFilter(ContextCompat.getColor(this, R.color.light_pink), PorterDuff.Mode.SRC_IN);


        if (runningCount == 0 || runningCount == totalCount) {
            progress.setVisibility(View.VISIBLE);
//            progress.setMax(totalCount);
            progress.setSecondaryProgress(totalCount);
            if (runningCount == 0) {
                rc.setTextColor(ResourcesCompat.getColor(getResources(), R.color.chant_gray, null));
                progress.setProgress(0);
            } else if (runningCount == totalCount) {
                progress.setProgress(totalCount);
            }

        } else if (runningCount < progress.getMax()) {
            progress.setProgress(runningCount);
            progress.setSecondaryProgress(runningCount);
        } else {
            progress.setProgress(0);
            progress.setSecondaryProgress(totalCount);
        }

        rc.setText("" + runningCount);
        tc.setText("" + totalCount);
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

    private void
    playMusic(final String chantingType) {
        String music = null;
        music = mSelectedBean.getMusic();
        music = music.substring(0, (music.length() - 4)).toLowerCase();
        String namamName = "namam";
        String[] name = music.split("/");
        if (name != null && name.length > 0) {
            namamName = name[name.length - 1];
        }
        play(music, namamName/*, true*/);
    }

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void play(String music, String namam) {
        stop();

        mMediaPlayer = new MediaPlayer();
        FileDescriptor pathDataSource = null;
        String urlSource = null;
        FileInputStream fisAudio = null;
        boolean isExist = isFilePresent(namam);
        boolean connectedInternet = Utils.checkConnection(mActivity);
        if (isExist) {
            String mp3FilePath = getCacheDir().getAbsolutePath() + "/mycache/" + namam + ".mp3";
            File file = new File(mp3FilePath);
            file.setReadable(true, false);
            try {
                fisAudio = new FileInputStream(file);
                pathDataSource = fisAudio.getFD();
            } catch (IOException ioe) {
                Log.e("START PLAYING", ioe.getMessage());
            }
        } else {
            urlSource = String.valueOf(Uri.parse(Constants.GOD_IMAGE_BASE_URL + music) + ".mp3");
            if (connectedInternet) {
                Intent startIntent = new Intent(mActivity, DownloadService.class);
                startIntent.putExtra("url", urlSource);
                startIntent.putExtra("namam", namam);
                mActivity.startService(startIntent);
            }
        }
        if (pathDataSource != null || (urlSource != null && connectedInternet)) {
            try {
                if (pathDataSource != null)
                    mMediaPlayer.setDataSource(pathDataSource);
                else if (urlSource != null)
                    mMediaPlayer.setDataSource(urlSource);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } finally {
                if (fisAudio != null) {
                    try {
                        fisAudio.close();
                    } catch (IOException ignore) {
                    }
                }
            }
        }
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
            }
        });
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
        if (cursor.moveToFirst()) {
            do {
                // Passing values
                String userId = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TABLE_LOCAL_COUNT.COL_USER_ID));
                String namakotiId = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TABLE_LOCAL_COUNT.COL_NAMAKOTI_ID));
                int count = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TABLE_LOCAL_COUNT.COL_RUNNING_TOTAL));

                userNamakotiIDs.add(namakotiId);
                userNamakotiCounts.add(count);
                // Do something Here with values
            } while (cursor.moveToNext());
        }
        cursor.close();

        Map<String, String> params = new HashMap<String, String>();
        String url;
        ServiceMethod serviceMethod;

        if (mFromScreen.equalsIgnoreCase(Constants.KEY_KARMIC_BEING) || mFromScreen.equalsIgnoreCase(Constants.KEY_KARMIC_OTHERS)) {
            params.put("participant_id", "" + mNamakotiId);
            params.put("gkccount", "" + mPrintRunningCountTv.getText().toString());
            params.put("gkc_setup_id", mGkcSetupId);
            url = Constants.KARMIC_SYNC_URL;
            serviceMethod = ServiceMethod.SYNC_KARMIC_CHANT;
        } else {
            params.put("user_id", mUserIdString);
            params.put("user_namakoti_id", userNamakotiIDs.toString());
            params.put("count", userNamakotiCounts.toString());
            url = Constants.SYNC_CHANT;
            serviceMethod = ServiceMethod.SYNC_CHANT;
        }

        VolleyUtil.getInstance().volleyStringRequest(this, url, params, serviceMethod, Request.Method.POST, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (menu != null)
            menu.clear();
        getMenuInflater().inflate(R.menu.sync_menu, menu);
        return true;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sync:
                syncCount("" + mNamakotiId);
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.volumeIv) {
            String cd = (String) mVoiceIv.getContentDescription();
            String on = getResources().getString(R.string.on_voice);
            String off = getResources().getString(R.string.off_voice);
            if (cd.equals(on)) {
                Utils.saveBooleanInSP(mActivity, VOICE_OVER_KEY, false);
//                    MyApplication.mEditor.putBoolean(VOICE_OVER_KEY, false).commit();
                mVoiceIv.setImageResource(R.drawable.chanting_novolume_ic);
                mVoiceIv.setContentDescription(off);
            } else if (cd.equals(off)) {
                Utils.saveBooleanInSP(mActivity, VOICE_OVER_KEY, true);
//                    MyApplication.mEditor.putBoolean(VOICE_OVER_KEY, true).commit();
                mVoiceIv.setImageResource(R.drawable.chanting_volume_ic);
                mVoiceIv.setContentDescription(on);
            }
        } else if (id == R.id.selectLL) {
            Intent intent = new Intent(mActivity, SelectChantGodActivity.class);
            intent.putExtra(SelectChantGodActivity.BUNDLE_IS_FROM_CAUSE, mFromScreen);
            startActivityForResult(intent, SELECTED_GOD_NAMA_CODE);
        } else if (id == R.id.includeChantImage) {
            setChantLayoutVisibility(true, false, false, false, false);
            mChanTitleTv.setText(mCurrentView);
            setSeparatorColor(true, false, false, false, false);

            if (mCurrentView.equalsIgnoreCase(getString(R.string.draganddrop))) {
                setChantTypeVisibility(true, false, false);
            } else if (mKeyTouchLayout.getVisibility() == View.VISIBLE) {
                setChantTypeVisibility(false, true, false);
            } else if (mJapaMalaLayout.getVisibility() == View.VISIBLE) {
                setChantTypeVisibility(false, false, true);
            }
        } else if (id == R.id.includePrintImage) {
            setSeparatorColor(false, true, false, false, false);
            setChantLayoutVisibility(false, true, false, false, false);
        } else if (id == R.id.includePanchangImage) {
            setSeparatorColor(false, false, true, false, false);
            setChantLayoutVisibility(false, false, true, false, false);
        } else if (id == R.id.includeServicesImage) {
            setSeparatorColor(false, false, false, true, false);
            setChantLayoutVisibility(false, false, false, true, false);
        } else if (id == R.id.includeTopListImage) {
            setSeparatorColor(false, false, false, false, true);
            setChantLayoutVisibility(false, false, false, false, true);
        } else if (id == R.id.rightArrowIv) {
            if (mDragAndDropLayout.getVisibility() == View.VISIBLE) {
                setChantTypeVisibility(false, true, false);
                setEnableView(mRightArrowIv);
                setEnableView(mLeftArrowIv);
                mCurrentView = getString(R.string.key_touch);
            } else if (mKeyTouchLayout.getVisibility() == View.VISIBLE) {
                setChantTypeVisibility(false, false, true);
                setDisableView(mRightArrowIv);
                setEnableView(mLeftArrowIv);
                mCurrentView = getString(R.string.japamala);
            } else if (mJapaMalaLayout.getVisibility() == View.VISIBLE) {
                setDisableView(mRightArrowIv);
                setEnableView(mLeftArrowIv);
                mCurrentView = getString(R.string.japamala);
            }
        } else if (id == R.id.leftArrowIv) {
            if (mDragAndDropLayout.getVisibility() == View.VISIBLE) {
                setEnableView(mRightArrowIv);
                setDisableView(mLeftArrowIv);
                mCurrentView = getString(R.string.draganddrop);
            } else if (mKeyTouchLayout.getVisibility() == View.VISIBLE) {
                setChantTypeVisibility(true, false, false);
                setEnableView(mRightArrowIv);
                setDisableView(mLeftArrowIv);
                mCurrentView = getString(R.string.draganddrop);
            } else if (mJapaMalaLayout.getVisibility() == View.VISIBLE) {
                setChantTypeVisibility(false, true, false);
                setEnableView(mRightArrowIv);
                setEnableView(mLeftArrowIv);
                mCurrentView = getString(R.string.key_touch);
            }
        }
    }

    private void setDisableView(ImageView imageView) {
        imageView.setEnabled(false);
        imageView.setAlpha((float) 125 / 255);
    }

    private void setEnableView(ImageView imageView) {
        imageView.setEnabled(true);
        imageView.setAlpha((float) 255 / 255);
    }

    private void setChantTypeVisibility(boolean drag, boolean click, boolean japamala) {
        mDragAndDropLayout.setVisibility(View.GONE);
        mJapaMalaLayout.setVisibility(View.GONE);
        mKeyTouchLayout.setVisibility(View.GONE);

        if (drag) {
            mDragAndDropLayout.setVisibility(View.VISIBLE);
            mChanTitleTv.setText(R.string.draganddrop);
        } else if (click) {
            mChanTitleTv.setText(R.string.key_touch);
            mKeyTouchLayout.setVisibility(View.VISIBLE);
        } else if (japamala) {
            mChanTitleTv.setText(R.string.japamala);
            mJapaMalaLayout.setVisibility(View.VISIBLE);
        }
    }

    private void setChantLayoutVisibility(boolean first, boolean sec, boolean third,
                                          boolean fourth, boolean fifth) {
        includeDragLayout.setVisibility(View.GONE);
        includePrintLayout.setVisibility(View.GONE);
        includePanchangLayout.setVisibility(View.GONE);
        includeServicesLayout.setVisibility(View.GONE);
        mTopListFL.setVisibility(View.GONE);
        if (first)
            includeDragLayout.setVisibility(View.VISIBLE);
        else if (sec)
            includePrintLayout.setVisibility(View.VISIBLE);
        else if (third)
            includePanchangLayout.setVisibility(View.VISIBLE);
        else if (fourth)
            includeServicesLayout.setVisibility(View.VISIBLE);
        else if (fifth)
            mTopListFL.setVisibility(View.VISIBLE);
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
                if (mFromScreen.equalsIgnoreCase(Constants.KEY_KARMIC_BEING) || mFromScreen.equalsIgnoreCase(Constants.KEY_KARMIC_OTHERS)) {
                    mGkcSetupId = DatabaseHelper.getInstance().getSetupId(mDatabase, mSelectedBean.getUser_namakoti_id(), mFromScreen, mUserIdLong);
                } else {
                    getSavedServerCount();
                    mLocalCount = DatabaseHelper.getSavedLocalCount(mDatabase, mNamakotiId, mLocalCountTableName, mUserIdLong);
                }
                setFragmentsVisibility();
                updateNamamImage();
                if (mFromScreen.equalsIgnoreCase(Constants.KEY_SELF_CHANT) || mFromScreen.equalsIgnoreCase(Constants.KEY_CHANT_FOR_CAUSE))
                    setTheCountValuesWithUserNamas(true, "default", false);
            }
        }
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
            TextView selectedNamaBtn = mNamasBtns.get(id);
            selectedNamaBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.buton_unselect, null));
            selectedNamaBtn.setEnabled(false);

            if (id == mNamasBtns.size() - 1) {
                mNamasBtns.get(0).setEnabled(true);
                mNamasBtns.get(0).setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selector_4_key_touch, null));
                setTheCountValuesWithUserNamas(false, "buttonNama", false);
            } else {
                Log.i(TAG, "id: " + id);
                mNamasBtns.get(id + 1).setEnabled(true);
                mNamasBtns.get(id + 1).setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.selector_4_key_touch, null));
            }
        }
    }

    public boolean isFilePresent(String fileName) {
        String path = getCacheDir().getAbsolutePath() + "/mycache/" + fileName + ".mp3";
        File file = new File(path);
        return file.exists();
    }
}