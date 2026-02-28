package com.unitol.namakoti;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.unitol.namakoti.util.Debug;
import com.unitol.namakoti.utils.ConstantsManager;
import com.unitol.namakoti.utils.WebConstants;
import com.unitol.namakoti.web.ChantAsync;
import com.unitol.namakoti.web.LanguagesAsync;
import com.unitol.namakoti.web.SaveNamaAsync;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class SaveNamaFragment extends BaseFragment implements OnClickListener {

    private static final String TAG = "SaveNamaFragment";
    private ImageView mGodImage;
    private String namam;
    private int i, i1;
    private Button language_spinner;
    private Button namam_type_spinner;

    private ArrayAdapter<CharSequence> namaAdapter;
    private String themeID;
    protected String languageID;
    private ArrayList<String> localcountries;
    protected String chantID;
    private String godsThemeID;
    private String godsPhoto;
    private EditText nameOfThePerson;
    private static SaveNamaFragment saveNamaFragment;
//	CreateNama createNama;


    // example for diff langeues...
    //http://snowpard-android.blogspot.in/2013/03/programmatically-change-language-in.html
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.save_nama_new, container, false);
        mActivity.onFragmentTabChange(View.VISIBLE, R.drawable.chant_settings_hdr, 0, "", View.GONE);
        saveNamaFragment = this;

        mGodImage = v.findViewById(R.id.god_image);
        TextView namam_textview = v.findViewById(R.id.namam_textview);

        language_spinner = v.findViewById(R.id.language_spinner);
        language_spinner.setOnClickListener(this);
        namam_type_spinner = v.findViewById(R.id.namam_type_spinner);
        namam_type_spinner.setOnClickListener(this);
        Button saveBtn = v.findViewById(R.id.save_setnama_id);
//		Button upgradeBtn = (Button) v.findViewById(R.id.upgrade_setnama_id);
        String fullName = MyApplication.mPref.getString(getString(R.string.pref_fullname_key), "");
        nameOfThePerson = (EditText) v.findViewById(R.id.preson_edittext);
        nameOfThePerson.setText(fullName);

        readDeviceSupportLanguages();


        saveBtn.setOnClickListener(this);
//		upgradeBtn.setOnClickListener(this);

        Bundle in = getArguments();

        JSONObject jsonObject;
        if (in != null) {
            String god = in.getString(ConstantsManager.GOD_KEY);
            Log.i(TAG, "god: " + god);
            try {
                jsonObject = new JSONObject(god);
                namam = jsonObject.getString("theme_name");
                godsThemeID = jsonObject.getString("theme_id");
                godsPhoto = jsonObject.getString("photo");
                setThemeId(godsThemeID);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (!TextUtils.isEmpty(namam)) {
                namam_textview.setText(namam);
            }
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
        }

//		godsThemeID = theme_id;

//		LanguagesAsync languagesAsync = new LanguagesAsync(mActivity, adapter, language_spinner);
        LanguagesAsync languagesAsync = new LanguagesAsync(mActivity);
        languagesAsync.execute();


		/*language_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Log.i(TAG, "onItemSelected: "+position);
				String selectedItem = language_spinner.getSelectedItem().toString();
				Log.i(TAG, "selectedItem: "+selectedItem);
				String selectedTag = (String) view.getTag();
				Log.i(TAG, ""+selectedItem+" selectedTag: "+selectedTag);
//				1-:-English selectedTag: 1-:-English
				String[] tag = selectedTag.split(ConstantsManager.SPLIT_KEY);
				languageID = tag[0];
				ChantAsync chantAsync = new ChantAsync(mActivity, namam_type_spinner);
				chantAsync.execute(languageID, getThemeID());
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});*/
		
		/*namam_type_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				Log.i(TAG, "onItemSelected: "+position);
				String selectedItem = namam_type_spinner.getSelectedItem().toString();
				Log.i(TAG, "selectedItem: "+selectedItem);
				String selectedTag = (String) view.getTag();
				Log.i(TAG, ""+selectedItem+" selectedTag: "+selectedTag);
				Log.i(TAG, " selectedTag: "+selectedTag);
//				Log.i(TAG, " selectedTag: "+android.text.Html.fromHtml("&#2360;&#2350;&#2379;&#2360;&#2366;"));
				Log.i(TAG, " selectedTag: "+android.text.Html.fromHtml(selectedTag));
				
				String[] tag = selectedTag.split(ConstantsManager.SPLIT_KEY);
				chantID = tag[0];
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});*/

        return v;
    }

    public void onLanguageSelected(@NonNull String selectedTag) {
//		Log.i(TAG, "onItemSelected: "+position);
//		String selectedItem = language_spinner.getSelectedItem().toString();
//		Log.i(TAG, "selectedItem: "+selectedItem);
//		String selectedTag = (String) view.getTag();
        Log.i(TAG, "" + " selectedTag: " + selectedTag);
//		1-:-English selectedTag: 1-:-English
        String[] tag = selectedTag.split(ConstantsManager.SPLIT_KEY);
        languageID = tag[0];
        String language = tag[1];
        String tempLanguage = language;
        Log.i(TAG, "" + " languageID: " + languageID);
        Log.i(TAG, "" + " language: " + language);
        if (tempLanguage.equalsIgnoreCase("Gujarathi")) {
            tempLanguage = "Gujarati";
        }
        if (!localcountries.contains(tempLanguage)) {
            showAlertForChangeLanguage(language, languageID);
        } else {
            language_spinner.setText(language);
            ChantAsync chantAsync = new ChantAsync(mActivity);
            chantAsync.execute(languageID, getThemeID());
        }
    }

    public void onNamamSelected(@NonNull String selectedTag) {
//		String selectedItem = namam_type_spinner.getSelectedItem().toString();
//		Log.i(TAG, "selectedItem: "+selectedItem);
//		String selectedTag = (String) view.getTag();
        Log.i(TAG, " selectedTag: " + selectedTag);

        String[] tag = selectedTag.split(ConstantsManager.SPLIT_KEY);
        chantID = tag[0];
        Log.i(TAG, "===" + tag[1]);
        namam_type_spinner.setText("" + tag[1]);
    }

    public void updateChatsAsync() {
		/*ChantAsync chantAsync = new ChantAsync(mActivity, namam_type_spinner);
		chantAsync.execute();*/
    }

    @Override
    public void onClick(@NonNull View v) {

        switch (v.getId()) {
            case R.id.save_setnama_id:
                String personName = nameOfThePerson.getText().toString();
                if (personName.length() > 0) {
                    String userID = MyApplication.mPref.getString(getResources().getString(R.string.pref_user_id_key), "");

//				String keyToSaveNama = userID+godsThemeID+languageID+chantID+personName;
//				user_id=63&language_id=2&selectedgod=1&selectedchant=3&username=Ga
                    Log.i(TAG, "" + userID + " , " + godsThemeID + " , " + languageID + " , " + chantID + " , " + personName);
                    SaveNamaAsync saveNamaAsync = new SaveNamaAsync(mActivity);
                    saveNamaAsync.execute(userID, languageID, godsThemeID, chantID, personName);

                } else {
                    Toast.makeText(mActivity, getResources().getString(R.string.enter_the_name_of_the_person), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.language_spinner:

                Intent i = new Intent(mActivity, LanguagesDialogActivity.class);
//			i.putExtra(ConstantsManager.USERNAMAS_AVAILBLE, noOfLanguages);
                startActivityForResult(i, ConstantsManager.LANGUAGE_CODE);

                break;
            case R.id.namam_type_spinner:

                Intent i2 = new Intent(mActivity, Namam4SaveNamaDialogActivity.class);
//			i.putExtra(ConstantsManager.USERNAMAS_AVAILBLE, noOfLanguages);
                startActivityForResult(i2, ConstantsManager.NAMAM_4_SAVE);

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
            case ConstantsManager.LANGUAGE_CODE:
                if (data != null) {
                    String selectedItem = Objects.requireNonNull(data.getExtras()).getString(ConstantsManager.LANGUAGES);
                    if (selectedItem != null) {
                        onLanguageSelected(selectedItem);
                    }
                }
                break;
            case ConstantsManager.NAMAM_4_SAVE:
                if (data != null) {
                    String selectedItem = Objects.requireNonNull(data.getExtras()).getString(ConstantsManager.NAMAM_4_SAVE_RES);
                    if (selectedItem != null) {
                        onNamamSelected(selectedItem);
                    }
                }
                break;
            default:
                break;
        }
    }

    public static SaveNamaFragment getInstace() {
        return saveNamaFragment;
    }

    private void setThemeId(String themeID) {
        this.themeID = themeID;
    }

    public String getThemeID() {
        return themeID;
    }

    public void readDeviceSupportLanguages() {
        try {
            Locale[] locales = Locale.getAvailableLocales();
            localcountries = new ArrayList<String>();
            for (Locale l : locales) {
                Log.i(TAG, " locales getDisplayLanguage: " + l.getDisplayLanguage());
                Log.i(TAG, " locales getDisplayName: " + l.getDisplayName());
                localcountries.add(l.getDisplayLanguage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlertForChangeLanguage(final String language, final String languageid) {


        AlertDialog.Builder alertDialog = null;

        alertDialog = new AlertDialog.Builder(getActivity());

        alertDialog.setIcon(R.drawable.alert_dialog_icon);
        alertDialog.setTitle("Select your language");
        alertDialog.setMessage("Your device doesn't support " + language + ".\nPlease select 'OK' to make English as your default language");
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        language_spinner.setText("English");
                        ChantAsync chantAsync = new ChantAsync(mActivity);
                        chantAsync.execute("EN", getThemeID());
                    }
                });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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

}
