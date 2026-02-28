package com.unitol.namakoti.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.unitol.namakoti.BaseFragment;
import com.unitol.namakoti.MyApplication;
import com.unitol.namakoti.NamasDialogActivity;
import com.unitol.namakoti.PaymentDialogActivity;
import com.unitol.namakoti.R;
import com.unitol.namakoti.util.Debug;
import com.unitol.namakoti.utils.ConstantsManager;
import com.unitol.namakoti.utils.WebConstants;
import com.unitol.namakoti.web.TransactionsAsync;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PaymentFragment extends BaseFragment implements OnClickListener {


    private static final String TAG = "PaymentFragment";
    private Button godForChatSpinner;
    private ImageView mGodImage;
    private ListView listView;
    private TextView no_details;
    private String user_namakoti_id;
    private View footerView;
    private static int pages;
    private static PaymentFragment instance;
    private List<JSONObject> transactionsList;

    public static PaymentFragment getInstance() {
        return instance;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.payment_details, container, false);
        mActivity.onFragmentTabChange(View.VISIBLE, R.drawable.payment_details_hdr, 0, "", View.GONE);
        instance = this;
        transactionsList = new ArrayList<JSONObject>();
        String userNamasAvailable = MyApplication.mPref.getString(getString(R.string.pref_user_namas_key), null);
//		Log.i(TAG, "userNamasAvailable: "+userNamasAvailable);

        godForChatSpinner = (Button) v.findViewById(R.id.gods_spinner);
        no_details = (TextView) v.findViewById(R.id.no_details);
        final List<JSONObject> namasList = parseUserNamas(userNamasAvailable);
        godForChatSpinner.setTag(namasList);

        mGodImage = (ImageView) v.findViewById(R.id.god_image);

//		final TextView payments = (TextView) v.findViewById(R.id.payments);
//		final TextView delivery = (TextView) v.findViewById(R.id.delivery);
        listView = (ListView) v.findViewById(R.id.payment_and_delivery_listview);

        //add the footer before adding the adapter, else the footer will not load!
        footerView = ((LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.listfooter, null, false);
        listView.addFooterView(footerView);

        footerView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Log.i(TAG, "footerView");
                int tag = (Integer) footerView.getTag();

                if (tag < pages) {
                    int increaseTag = tag + 1;
                    footerView.setTag(increaseTag);
                    TransactionsAsync transactionsAsync = new TransactionsAsync(mActivity, listView, transactionsList, increaseTag);
                    transactionsAsync.execute(user_namakoti_id);
                } else {
                    footerView.setVisibility(View.GONE);
                    Toast.makeText(mActivity, "Transactions are completed", Toast.LENGTH_SHORT).show();
                }
            }

        });

        ViewTreeObserver observer = godForChatSpinner.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {

//				Log.i(TAG, "namasList: ======="+namasList.size());
                if (namasList.size() != 0) {
                    godForChatSpinner.setOnClickListener(PaymentFragment.this);
                    onNamamSelection(namasList.get(0).toString());
                } else {
                    no_details.setVisibility(View.VISIBLE);
                }

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    godForChatSpinner.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    godForChatSpinner.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
                JSONObject namaObject = (JSONObject) view.getTag();
                Intent i = new Intent(mActivity, PaymentDialogActivity.class);
                i.putExtra(ConstantsManager.PAYMENT_JSON, namaObject.toString());
                startActivity(i);
            }
        });

        return v;
    }

    private void setBackground(View vi, int drawable) {

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT >= 16) {
            vi.setBackground(getResources().getDrawable(drawable));
        } else {
            vi.setBackgroundDrawable(getResources().getDrawable(drawable));
        }
    }

    /**
     * Transaction is done or not
     */
    public void onTransactions(boolean done) {
        if (done) {
            no_details.setVisibility(View.GONE);
        } else {
            no_details.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.gods_spinner:
                String userNamasAvailable = MyApplication.mPref.getString(getString(R.string.pref_user_namas_key), null);
                Intent i = new Intent(mActivity, NamasDialogActivity.class);
                i.putExtra(ConstantsManager.USERNAMAS_AVAILBLE, userNamasAvailable);
                startActivityForResult(i, ConstantsManager.NAMAS_CODE);
                break;

            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult: " + resultCode);
        switch (requestCode) {
            case ConstantsManager.NAMAS_CODE:
                if (data != null) {
                    String selectedItem = data.getExtras().getString(ConstantsManager.NAMAS_JSON);
                    onNamamSelection(selectedItem);
                }
                break;

            default:
                break;
        }
    }

    private void onNamamSelection(String selectedItem) {
        Log.i(TAG, "selectedItem: " + selectedItem);
        transactionsList.clear();
        footerView.setVisibility(View.VISIBLE);
        JSONObject namaObject = null;
        try {
            namaObject = new JSONObject(selectedItem);
            String namam = namaObject.getString("theme_name");
            user_namakoti_id = namaObject.getString("user_namakoti_id");
            String namaOfGod = namaObject.getString("sub_theme_name");
            namaOfGod = "" + Html.fromHtml(namaOfGod);
            godForChatSpinner.setText(namaOfGod);

            // String name = (namam.replace(" ", "")).toLowerCase() + getResources().getString(R.string.save_nama_aditional_key);
            // int idDrawable = getResources().getIdentifier(name, "drawable", mActivity.getPackageName());
            // mGodImage.setImageResource(idDrawable);

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

            TransactionsAsync transactionsAsync = new TransactionsAsync(mActivity, listView, transactionsList, 1);
            transactionsAsync.execute(user_namakoti_id);
            footerView.setTag(1);//onSelection set default value is 1
        } catch (JSONException e) {
            e.printStackTrace();
        }
        godForChatSpinner.setTag(namaObject); //It is very important to prepare key


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

    public void totalTransactions(int p, int visibility) {
        pages = p;
        footerView.setVisibility(visibility);
        if (p <= 1) {
            footerView.setVisibility(View.GONE);
        }
    }
}
