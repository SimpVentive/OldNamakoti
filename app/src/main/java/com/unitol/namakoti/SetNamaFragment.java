package com.unitol.namakoti;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.unitol.namakoti.adapters.GodsAdapter;
import com.unitol.namakoti.model.GodNamesBean;
import com.unitol.namakoti.utils.ConstantsManager;
import com.unitol.namakoti.utils.NamaKotiUtils;
import com.unitol.namakoti.web.GetNamasAsync;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SetNamaFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "SetNamaFragment";

    private GodsAdapter adapter;
    private List<GodNamesBean> mGodList = new ArrayList<>();

    private GodNamesBean mSelectedTheme;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        View v = inflater.inflate(R.layout.setnama, container, false);
        mActivity.onFragmentTabChange(View.VISIBLE, R.drawable.chant_settings_hdr, 0, "", View.GONE);
        MyApplication.mEditor.putBoolean(getString(R.string.pref_create_nama_key), true).commit();

        mSelectedTheme = null;

        RecyclerView gridView = v.findViewById(R.id.gridview);
        gridView.setLayoutManager(new GridLayoutManager(requireActivity(), 3));

        Button mContinue = v.findViewById(R.id.continue_button);

        View.OnClickListener mListener = this;
        mContinue.setOnClickListener(this);

        mGodList.clear();
        mGodList.addAll(getGodListFromDb());
        adapter = new GodsAdapter(requireActivity(), mGodList, mListener, -1);
        gridView.setAdapter(adapter);
        getGodNames();

        return v;
    }

    private void getGodNames() {
        if (!NamaKotiUtils.isNetworkAvailable(mActivity) && mGodList.isEmpty()) {
            NamaKotiUtils.enableWifiSettings(mActivity);
        } else {
            mSelectedTheme = null;
            GetNamasAsync async = new GetNamasAsync(mActivity, mGodList, adapter);
            async.execute();
        }
    }

    @Override
    public void onClick(@NonNull final View view) {
        int id = view.getId();
        if (id == R.id.continue_button) {
            if (mSelectedTheme == null) {
                NamaKotiUtils.showAlertDialog(requireActivity(), "Error", "Please select god", null, null, false, true);
            } else {
                Bundle b = getGodDetailsBundle();
                mActivity.pushFragments(ConstantsManager.TAB_3, new SaveNamaFragment(), false, true, b);
            }
        } else if (id == R.id.grid_image) {
            updateRow(view);
        } else if (id == R.id.grid_text) {
            updateRow(view);
        }
    }

    private @NonNull
    Bundle getGodDetailsBundle() {
        Bundle b = new Bundle();
        JSONObject godDetails = new JSONObject();
        try {
            godDetails.put("theme_id", mSelectedTheme.getTheme_id());
            godDetails.put("theme_name", mSelectedTheme.getTheme_name());
            godDetails.put("photo", mSelectedTheme.getPhoto());
            godDetails.put("printing_service", mSelectedTheme.getPrinting_service());
            b.putString(ConstantsManager.GOD_KEY, "" + godDetails);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return b;
    }

    private void updateRow(@NonNull View view) {
        int pos = (int) view.getTag(R.id.grid_image);
        Log.e(TAG, "pos: " + pos);
        mSelectedTheme = mGodList.get(pos);
        adapter.updateList(mGodList, pos);
    }


    public ArrayList<GodNamesBean> getGodListFromDb() {
        ArrayList<GodNamesBean> godsWithIDList = new ArrayList<>();
        try {
            String godsAvailable = MyApplication.mPref.getString(getString(R.string.pref_gods_key), null);
            if (godsAvailable != null) {
                JSONArray array = new JSONArray(godsAvailable);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jsonObject = array.getJSONObject(i);
                    String theme_id = "";
                    String theme_name = "";
                    String printing_service = "";
                    String photo = "";
                    if (jsonObject.has("theme_id"))
                        theme_id = jsonObject.getString("theme_id");
                    if (jsonObject.has("theme_name"))
                        theme_name = jsonObject.getString("theme_name");
                    if (jsonObject.has("printing_service"))
                        printing_service = jsonObject.getString("printing_service");
                    if (jsonObject.has("photo"))
                        photo = jsonObject.getString("photo");
                    GodNamesBean godNamesBeanWithID = new GodNamesBean(theme_id, theme_name, printing_service, photo);
                    godsWithIDList.add(godNamesBeanWithID);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return godsWithIDList;
    }

}
