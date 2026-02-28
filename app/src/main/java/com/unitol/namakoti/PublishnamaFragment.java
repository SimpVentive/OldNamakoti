package com.unitol.namakoti;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.unitol.namakoti.utils.NamaKotiUtils;
import com.unitol.namakoti.utils.WebConstants;
import com.unitol.namakoti.web.TemplesAsync;
import com.unitol.namakoti.web.WebViewActivity;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class PublishnamaFragment extends BaseFragment implements OnClickListener {

    private static final String TAG = "PublishnamaFragment";
    private static int RESULT_LOAD_IMAGE = 1;
    private Uri mImageCaptureUri;
    private ImageView mImageView;
    //	private AlertDialog dialog;
    private ImageView hand_written_img;
    TextView totalcount, printed_count, running_count;

    private final int CROP_FROM_CAMERA = 6;
    private final int PICK_FROM_FILE = 7;
    private RadioGroup rg;
    private Button godForChatSpinner;
    private Button handWrittenChant;
    private LinearLayout hand_written_layout;
    private TextView hand_written_text;
    private Button choose_the_temple;
    private Button print_type;
    private Button noOfNamasToPrint;
    private EditText name_of_person;
    /*private static PublishnamaFragment instance;

    public static PublishnamaFragment getInstance() {
        return instance;
    }*/
    private ProgressDialog progressDialog;
    final String uploadFilePath = "/mnt/sdcard/Namakoti/";
    private String uploadFileName = "";
    private ImageView mGodImage;


    private static PublishnamaFragment intance;

    public static PublishnamaFragment getInstance() {
        return intance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (container == null) {
            return null;
        }

        View v = inflater.inflate(R.layout.releasenama_new, container, false);
        intance = this;
//		instance = this;

        mActivity.onFragmentTabChange(View.VISIBLE, R.drawable.print_and_delivery_hdr, 0, "", View.GONE);
        totalcount = v.findViewById(R.id.total_edit);
        running_count = v.findViewById(R.id.running_count);
        printed_count = v.findViewById(R.id.printed_count);
        name_of_person = v.findViewById(R.id.name_of_person);

        Button pay_btn = v.findViewById(R.id.pay_btn);

        choose_the_temple = v.findViewById(R.id.choose_the_temple);
        print_type = v.findViewById(R.id.print_type);
        noOfNamasToPrint = v.findViewById(R.id.no_of_namas_to_print);

        noOfNamasToPrint.setOnClickListener(this);
        print_type.setOnClickListener(this);
        choose_the_temple.setOnClickListener(this);

        String userNamasAvailable = MyApplication.mPref.getString(getString(R.string.pref_user_namas_key), null);
        Log.i(TAG, "userNamasAvailable: " + userNamasAvailable);
        godForChatSpinner = v.findViewById(R.id.gods_spinner);
        mGodImage = v.findViewById(R.id.god_image);


        godForChatSpinner = (Button) v.findViewById(R.id.gods_spinner);
        godForChatSpinner.setOnClickListener(this);
        final List<JSONObject> namasList = parseUserNamas(userNamasAvailable);
        godForChatSpinner.setTag(namasList);

        ViewTreeObserver observer = godForChatSpinner.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {

                onNamamSelection(namasList.get(0).toString());
                godForChatSpinner.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        LinearLayout tCountLL = v.findViewById(R.id.total_help);
        LinearLayout pCountLL = v.findViewById(R.id.print_help);
        LinearLayout rCountLL = v.findViewById(R.id.running_help);

        tCountLL.setOnClickListener(this);
        pCountLL.setOnClickListener(this);
        rCountLL.setOnClickListener(this);

        hand_written_img = v.findViewById(R.id.hand_written_img);
        hand_written_layout = v.findViewById(R.id.hand_written_layout);
        hand_written_text = v.findViewById(R.id.hand_written_text);
        handWrittenChant = v.findViewById(R.id.handwritten_chant);

        if (handWrittenChant.getText().toString().trim().equalsIgnoreCase("Yes")) {
            hand_written_layout.setVisibility(View.VISIBLE);
            hand_written_text.setVisibility(View.VISIBLE);
        }

        hand_written_img.setOnClickListener(this);

        handWrittenChant.setOnClickListener(this);

        pay_btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
//				Log.i(TAG, "noOfNamasToPrint "+noOfNamasToPrint.getSelectedItem());

                JSONObject selectedNamaObject = (JSONObject) godForChatSpinner.getTag();
                Log.i(TAG, "selectedNamaObject: " + selectedNamaObject);
                try {
                    String printing_service = selectedNamaObject.getString("printing_service");
                    if (printing_service.equalsIgnoreCase("0")) {
                        String theme_name = selectedNamaObject.getString("theme_name");
                        Toast.makeText(mActivity, "Currently, Print & Delivery to temple facility is not available for the " + theme_name, Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }


                String tCount = running_count.getText().toString();
                int tc = Integer.parseInt(tCount);
                Log.i(TAG, "tCount: " + tCount);
                Log.i(TAG, "tc: " + tc);
                if (tc < 10000) {
                    Toast.makeText(mActivity, "You shoud have minimum of 10000 chants(On Going) for Printing", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!mActivity.haveNetworkConnection()) {
                    NamaKotiUtils.enableWifiSettings(mActivity);
                    Log.i(TAG, "We do not have the network connection");
                    return;
                }

                Log.i(TAG, "We have the network connection");

                if (noOfNamasToPrint.getTag() == null) {
                    return;
                }

                String noOfNamas = noOfNamasToPrint.getTag().toString();
                String printType = print_type.getTag().toString();
                String chooseTemple = choose_the_temple.getTag().toString();

                try {
                    JSONObject noOfNamasObject = new JSONObject(noOfNamas);
                    JSONObject printTypeObject = new JSONObject(printType);
                    JSONObject chooseTempleObject = new JSONObject(chooseTemple);

                    String user_namakoti_id = selectedNamaObject.getString("user_namakoti_id");
                    String printingcount = noOfNamasObject.getString("id");
                    String printing_id = printTypeObject.getString("printing_id");
                    String temple_id = chooseTempleObject.getString("temple_id");

                    String logInUserName = MyApplication.mPref.getString(getString(R.string.pref_email_key), "");
                    String pwd = MyApplication.mPref.getString(getString(R.string.pref_pwd_key), "");
                    String userID = MyApplication.mPref.getString(getResources().getString(R.string.pref_user_id_key), "");

                    String nameOfPerson = name_of_person.getText().toString().trim();
                    Log.i(TAG, "noID: " + printingcount);
                    Log.i(TAG, "printing_id: " + printing_id);
                    Log.i(TAG, "temple_id: " + temple_id);
                    Log.i(TAG, "nameOfPerson: " + nameOfPerson);
                    String amount = "200";
                    String printcat = "no";
//					&user_namakoti_id=3&name=srikanth&templename=1&printingcount=10000&printtype=BK&amount=200&printcat=no
//					getDialogForPay(user_namakoti_id, nameOfPerson, temple_id, printingcount, printing_id, amount, printcat).show();


                    String imageFile = (String) hand_written_img.getTag();

//					if (!imageFile.isEmpty()) {
//						
//					}
//					File file = new File(imageFile);


//					http://118.67.249.179/namakoti/webservice/printchant?user_id=19&username=srikanthmath0@gmail.com&password=Asdasd11
//					&user_namakoti_id=3&name=srikanth&templename=1&printingcount=10000&printtype=BK&amount=200&printcat=no
                    String url = WebConstants.PRINT_CHANT_URL + "?user_id=" + userID + "&username=" + logInUserName + "&password=" + pwd
                            + "&user_namakoti_id=" + user_namakoti_id + "&name=" + nameOfPerson + "&templename=" + temple_id + "&printingcount=" + printingcount
                            + "&printtype=" + printing_id + "&amount=" + amount + "&printcat=" + printcat + "&file=" + "";
                    Debug.e(TAG, "url:" + url);

                    Intent i = new Intent(mActivity, WebViewActivity.class);
                    i.putExtra(ConstantsManager.WEBVIEW_URL, url);
                    i.putExtra(ConstantsManager.WEBVIEW_HDR_IMG, R.drawable.print_and_delivery_hdr);
                    startActivity(i);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


        return v;
    }

    public void startPaymentGateWay() {

        String noOfNamas = noOfNamasToPrint.getTag().toString();
        String printType = print_type.getTag().toString();
        String chooseTemple = choose_the_temple.getTag().toString();
        JSONObject selectedNamaObject = (JSONObject) godForChatSpinner.getTag();
        Log.i(TAG, "selectedNamaObject: " + selectedNamaObject);

        String printingcount = "";
        String user_namakoti_id = "";
        String printing_id = "";
        String temple_id = "";
        try {

            JSONObject noOfNamasObject = new JSONObject(noOfNamas);
            JSONObject printTypeObject = new JSONObject(printType);
            JSONObject chooseTempleObject = new JSONObject(chooseTemple);

            user_namakoti_id = selectedNamaObject.getString("user_namakoti_id");
            printingcount = noOfNamasObject.getString("id");
            printing_id = printTypeObject.getString("printing_id");
            temple_id = chooseTempleObject.getString("temple_id");

        } catch (Exception e) {
            Log.e(TAG, "e: " + e);
        }

        String logInUserName = MyApplication.mPref.getString(getString(R.string.pref_email_key), "");
        String pwd = MyApplication.mPref.getString(getString(R.string.pref_pwd_key), "");
        String userID = MyApplication.mPref.getString(getResources().getString(R.string.pref_user_id_key), "");

        String nameOfPerson = name_of_person.getText().toString().trim();
        Log.i(TAG, "noID: " + printingcount);
        Log.i(TAG, "printing_id: " + printing_id);
        Log.i(TAG, "temple_id: " + temple_id);
        Log.i(TAG, "nameOfPerson: " + nameOfPerson);
        String amount = "200";
        String printcat = "no";

        String imageFile = (String) hand_written_img.getTag();

//		if (!imageFile.isEmpty()) {
//
//		}
//		File file = new File(imageFile);


//		http://118.67.249.179/namakoti/webservice/printchant?user_id=19&username=srikanthmath0@gmail.com&password=Asdasd11
//		&user_namakoti_id=3&name=srikanth&templename=1&printingcount=10000&printtype=BK&amount=200&printcat=no
        String url = WebConstants.PRINT_CHANT_URL + "?user_id=" + userID + "&username=" + logInUserName + "&password=" + pwd
                + "&user_namakoti_id=" + user_namakoti_id + "&name=" + nameOfPerson + "&templename=" + temple_id + "&printingcount=" + 10000
                + "&printtype=" + printing_id + "&amount=" + amount + "&printcat=" + printcat + "&file=" + "";
        Debug.e(TAG, "url:" + url);

        Intent i = new Intent(mActivity, WebViewActivity.class);
        i.putExtra(ConstantsManager.WEBVIEW_URL, url);
        i.putExtra(ConstantsManager.WEBVIEW_HDR_IMG, R.drawable.print_and_delivery_hdr);
        startActivity(i);
    }

    private Dialog getDialogForPay(final String user_namakoti_id,
                                   final String nameOfPerson, final String temple_id, String printingcount,
                                   final String printing_id, final String amount, final String printcat) {
        final Dialog dialog = new Dialog(mActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.custom_dlg);

        rg = (RadioGroup) dialog.findViewById(R.id.radioGroup1);
//        rg.setOnCheckedChangeListener(new OnRadioCheckedChangeListener());

        // set the custom dialog components - text, image and button
        RadioButton upgrade_and_print = (RadioButton) dialog.findViewById(R.id.upgrade_and_print);
        RadioButton upgrade_and_chanting = (RadioButton) dialog.findViewById(R.id.upgrade_and_chanting);

        int isUpgraded = MyApplication.mPref.getInt(getResources().getString(R.string.pref_user_upgrade_key), 0);
        if (isUpgraded == 1) {
            upgrade_and_chanting.setVisibility(View.GONE);
            upgrade_and_print.setChecked(true);
        }

        Button dialogButton = (Button) dialog.findViewById(R.id.pay_btn);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String logInUserName = MyApplication.mPref.getString(getString(R.string.pref_email_key), "");
                String pwd = MyApplication.mPref.getString(getString(R.string.pref_pwd_key), "");
                String userID = MyApplication.mPref.getString(getResources().getString(R.string.pref_user_id_key), "");
                String url = "";
                switch (rg.getCheckedRadioButtonId()) {
                    case R.id.upgrade_and_print:
                        String imageFile = (String) hand_written_img.getTag();

//					if (!imageFile.isEmpty()) {
//
//					}
//					File file = new File(imageFile);

//					http://118.67.249.179/namakoti/webservice/printchant?user_id=19&username=srikanthmath0@gmail.com&password=Asdasd11
//					&user_namakoti_id=3&name=srikanth&templename=1&printingcount=10000&printtype=BK&amount=200&printcat=no
                        url = WebConstants.PRINT_CHANT_URL + "?user_id=" + userID + "&username=" + logInUserName + "&password=" + pwd
                                + "&user_namakoti_id=" + user_namakoti_id + "&name=" + nameOfPerson + "&templename=" + temple_id + "&printingcount=" + 10000
                                + "&printtype=" + printing_id + "&amount=" + amount + "&printcat=" + printcat + "&file=" + "";
                        Debug.e(TAG, "url:" + url);
                        break;
                    case R.id.upgrade_and_chanting:
                        url = WebConstants.UPGRADE_URL + "?user_id=" + userID + "&username=" + logInUserName + "&password=" + pwd;
                        Debug.e(TAG, "url:" + url);
                        break;

                    default:
                        break;
                }
                Log.i(TAG, "url: " + url);

                Intent i = new Intent(mActivity, WebViewActivity.class);
                i.putExtra(ConstantsManager.WEBVIEW_URL, url);
                i.putExtra(ConstantsManager.WEBVIEW_HDR_IMG, R.drawable.print_and_delivery_hdr);
                startActivity(i);

                dialog.dismiss();
            }
        });

        return dialog;
    }

	/*private class OnRadioCheckedChangeListener implements OnCheckedChangeListener {

		private int pos;
		private int pos1;

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			pos = rg.indexOfChild(getView().findViewById(checkedId));

			pos1 = rg.indexOfChild(getView().findViewById(rg.getCheckedRadioButtonId()));

			switch (checkedId) {
			case R.id.upgrade_and_print:
				
				break;
			case R.id.upgrade_and_chanting:
				
				break;
			default:
				break;
			}
		}
		
	}*/

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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObjects;
    }

    public void setTheCountValuesWithUserNamas(boolean fromOnItemSelected, JSONObject nama, TextView tc, TextView pc, TextView rc) {

        int isUpgraded = MyApplication.mPref.getInt(mActivity.getResources().getString(R.string.pref_user_upgrade_key), 0);

        String userID = MyApplication.mPref.getString(mActivity.getResources().getString(R.string.pref_user_id_key), "");
        String runningKey = mActivity.getResources().getString(R.string.pref_namas_running_count_key);
//		String keyLocal = getResources().getString(R.string.pref_namas_local_running_count_key);
        /**key for runningCount form nama object*/
        String key;
        try {
//			Log.i(TAG, "namaObject: "+nama);
            String nama_printed_count = nama.getString("nama_printed_count");
            if (!TextUtils.isEmpty(nama_printed_count) && !nama_printed_count.equalsIgnoreCase("null")) {
                pc.setText(nama_printed_count);
            } else {
                pc.setText("0");
            }
            String user_namakoti_id = nama.getString("user_namakoti_id");
//			String localRunningKey = userID+user_namakoti_id+keyLocal;
            key = userID + user_namakoti_id;
            Log.i(TAG, "isUpgraded: " + isUpgraded);

            //When user first entered in this view(EnternamaFragment)

            String totalKey = mActivity.getResources().getString(R.string.pref_namas_total_count_key);

            //Total count
            NamaKotiUtils.setCountForNama((key + totalKey), Integer.parseInt(nama.getString("nama_total_count")));//Need onItemSelected()

            //Running count from server
            int runningCountFromNamasObject = Integer.parseInt(nama.getString("nama_running_count"));//Need onItemSelected()
            NamaKotiUtils.setCountForNama((key + runningKey), runningCountFromNamasObject);

            //Local running count from local memory
//			int runningCountFromLocalMemory = NamaKotiUtils.getCountForNama(localRunningKey);
//			Log.i(TAG, "localRunningKey: "+localRunningKey);
//			Log.i(TAG, "runningCountFromLocalMemory: before save"+runningCountFromLocalMemory);
            Log.i(TAG, "runningCountFromNamasObject: " + runningCountFromNamasObject);

            tc.setText("" + NamaKotiUtils.getCountForNama(key + totalKey));//Need onItemSelected()

            int runningCountToShow = /*runningCountFromLocalMemory +*/ runningCountFromNamasObject;
            Log.i(TAG, "runningCountToShow: " + runningCountToShow);
            rc.setText("" + runningCountToShow);
            String namaOfGod = nama.getString("sub_theme_name");
            namaOfGod = "" + Html.fromHtml(namaOfGod);
            Bundle b = getArguments();
            boolean isItFromPopUp = false;
            if (b != null) {
                isItFromPopUp = b.getBoolean(ConstantsManager.IS_IT_FROM_POP_UP);
            }

            if (isUpgraded == 0 && NamaKotiUtils.getCountForNama(key + runningKey) == ConstantsManager.USER_DEFAULT_NAMA_SIZE) {
                if (!isItFromPopUp) {
                    NamaKotiUtils.showUpgradeDialog(mActivity, mActivity.getResources().getString(R.string.upgrade),
                            mActivity.getResources().getString(R.string.upgrade_text), mActivity.getResources().getString(R.string.upgrade), "Cancel");
                }
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File getOutputImageFileUri(@NotNull Context ctx) {
        File image = null;
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String imageFileName = "IMG_" + timeStamp + "_";
            File storageDir = new File(ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "Camera");
            if (!storageDir.exists()) {
                boolean success = storageDir.mkdirs();
                Debug.e("cacheDir", "success::" + success + ">>" + storageDir.getAbsolutePath() + "<< is success");
            }
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Debug.e("getOutputImageFileUri", "image_path:" + Objects.requireNonNull(image).getAbsolutePath());
        return image;

    }

    public String saveImage(@NotNull Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        try {
            File file = getOutputImageFileUri(requireActivity());
            FileOutputStream fo = new FileOutputStream(file);
            fo.write(bytes.toByteArray());
            fo.close();
            Debug.e(TAG, "File Saved::--->" + file.getAbsolutePath());
            return file.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    private File filePath;

    public void Start_Crop_Image() {
        if (filePath != null && filePath.exists() && filePath.isFile()) {
            Debug.e(TAG, "Start_Crop_Image image_path::" + filePath.getAbsolutePath());
            try {
                Intent intent = new Intent(mActivity, CropperActivity.class);
                intent.putExtra(ConstantsManager.SELECTED_IMAGE_PATH, filePath.getAbsolutePath());
                //noinspection deprecation
                startActivityForResult(intent, CROP_FROM_CAMERA);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult:" + resultCode);
        if (resultCode != Activity.RESULT_OK && resultCode != ConstantsManager.HAND_WRITTEN_CODE && resultCode != ConstantsManager.NAMAS_CODE &&
                resultCode != ConstantsManager.TEMPLES_CODE && resultCode != ConstantsManager.PRINT_TYPE_CODE && resultCode != ConstantsManager.COUNTS_CODE)
            return;

        Log.i(TAG, "data: " + data);
        switch (requestCode) {
            case PICK_FROM_FILE:
                /**
                 * After selecting image from files, save the selected path
                 */
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null && data.getData() != null) {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream;
                        try {
                            imageStream = mActivity.getContentResolver().openInputStream(imageUri);
                            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                            String selectedImagePath = saveImage(selectedImage);
                            filePath = new File(selectedImagePath);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    Start_Crop_Image();
                }
                break;

            case CROP_FROM_CAMERA:
                String photoPath = MyApplication.mPref.getString(getResources().getString(R.string.hand_written_image_namakoti), "");
                Log.i(TAG, "onResume: " + photoPath);
                if (!photoPath.isEmpty()) {
                    String root = requireActivity().getFilesDir().toString();//Environment.getExternalStorageDirectory().toString();
                    photoPath = root + "/Namakoti" + "/" + photoPath;
                    Log.i(TAG, "photoPath: " + photoPath);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);
                    hand_written_img.setImageBitmap(bitmap);
                    hand_written_img.setTag(photoPath);
                    handWrittenChant.setText("Yes");
                    hand_written_layout.setVisibility(View.VISIBLE);
                    hand_written_text.setVisibility(View.VISIBLE);
//			onHandWrittenSelected("Yes");
                } else {
                    onHandWrittenSelected("No");
                }
                break;
            case ConstantsManager.HAND_WRITTEN_CODE:
                if (data != null) {
                    String selectedItem = data.getExtras().getString(ConstantsManager.DATE_JSON_4_ADAPTER);
                    onHandWrittenSelected(selectedItem);
                }
                break;
            case ConstantsManager.NAMAS_CODE:
                Log.i(TAG, "NAMAS_CODE");
                if (data != null) {
                    String selectedItem = Objects.requireNonNull(data.getExtras()).getString(ConstantsManager.NAMAS_JSON);
                    onNamamSelection(selectedItem);
                }
                break;

            case ConstantsManager.TEMPLES_CODE:
                if (data != null) {
                    String selectedItem = Objects.requireNonNull(data.getExtras()).getString(ConstantsManager.TEMPLES_JSON);
                    JSONObject namaObject;
                    try {
                        namaObject = new JSONObject(selectedItem);
                        onTempleSelected(namaObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case ConstantsManager.PRINT_TYPE_CODE:
                if (data != null) {
                    String selectedItem = Objects.requireNonNull(data.getExtras()).getString(ConstantsManager.PRINT_TYPE_JSON);
                    JSONObject namaObject;
                    try {
                        namaObject = new JSONObject(selectedItem);
                        onPrintTypesSelected(namaObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case ConstantsManager.COUNTS_CODE:
                if (data != null) {
                    String selectedItem = Objects.requireNonNull(data.getExtras()).getString(ConstantsManager.COUNT_JSON);
                    JSONObject namaObject;
                    try {
                        namaObject = new JSONObject(selectedItem);
                        onCountSelected(namaObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void onHandWrittenSelected(String selectedItem) {

//		String selectedItem = handWrittenChant.getSelectedItem().toString();
        Log.i(TAG, "onHandWrittenSelected: " + selectedItem);
        handWrittenChant.setText(selectedItem);
        if (selectedItem.equals("Yes")) {

            getHandWrittenImage();

        } else if (selectedItem.equals("No")) {
            hand_written_layout.setVisibility(View.GONE);
            hand_written_text.setVisibility(View.GONE);
        }

    }


    private void getHandWrittenImage() {
        hand_written_layout.setVisibility(View.VISIBLE);
        hand_written_text.setVisibility(View.VISIBLE);

        try {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            //noinspection deprecation
            startActivityForResult(intent, PICK_FROM_FILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(@NonNull View v) {

        switch (v.getId()) {
            case R.id.total_help:
                NamaKotiUtils.showHelpDialog(mActivity, "Total", getResources().getString(R.string.total_count_text));
                break;
            case R.id.print_help:
                NamaKotiUtils.showHelpDialog(mActivity, "Printed", getResources().getString(R.string.printed_count_text));
                break;
            case R.id.running_help:
                NamaKotiUtils.showHelpDialog(mActivity, "On Going", getResources().getString(R.string.on_goingl_count_text));
                break;
            case R.id.handwritten_chant:
                Intent i1 = new Intent(mActivity, HandWrittenImageDialogActivity.class);
                startActivityForResult(i1, ConstantsManager.HAND_WRITTEN_CODE);
                break;

            case R.id.gods_spinner:
                String userNamasAvailable = MyApplication.mPref.getString(getString(R.string.pref_user_namas_key), null);
                Intent i = new Intent(mActivity, NamasDialogActivity.class);
                i.putExtra(ConstantsManager.USERNAMAS_AVAILBLE, userNamasAvailable);
                startActivityForResult(i, ConstantsManager.NAMAS_CODE);
                break;
            case R.id.choose_the_temple:
                Intent i2 = new Intent(mActivity, TemplesDialogActivity.class);
                startActivityForResult(i2, ConstantsManager.TEMPLES_CODE);
                break;
            case R.id.print_type:
                Intent i3 = new Intent(mActivity, PrintTypeDialogActivity.class);
                startActivityForResult(i3, ConstantsManager.PRINT_TYPE_CODE);
                break;
            case R.id.no_of_namas_to_print:
                Intent i4 = new Intent(mActivity, CountDialogActivity.class);
                startActivityForResult(i4, ConstantsManager.COUNTS_CODE);
                break;
            case R.id.hand_written_img:
                getHandWrittenImage();
                break;
        }

    }

    private void onNamamSelection(String selectedItem) {
        Log.i(TAG, "selectedItem: " + selectedItem);
        JSONObject namaObject = null;

        try {
            namaObject = new JSONObject(selectedItem);
            String namam = namaObject.getString("theme_name");
            String namaOfGod = namaObject.getString("sub_theme_name");
            String themeId = namaObject.getString("user_theme_id");
            String print_username = namaObject.getString("print_username");
            name_of_person.setText(print_username);
            namaOfGod = "" + Html.fromHtml(namaOfGod);
            godForChatSpinner.setText(namaOfGod);

            //  String name = (namam.replace(" ", "")).toLowerCase() + mActivity.getResources().getString(R.string.save_nama_aditional_key);
            // int idDrawable = mActivity.getResources().getIdentifier(name, "drawable", mActivity.getPackageName());
            // Log.i(TAG, "id " + idDrawable);
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

            if (!mActivity.haveNetworkConnection()) {
                NamaKotiUtils.enableWifiSettings(mActivity);
                Log.i(TAG, "We do not have the network connection");
                return;
            }

            Log.i(TAG, "We have the network connection");

            TemplesAsync templesAsync = new TemplesAsync(mActivity);
            templesAsync.execute(themeId);

//			prepareKeyTouch(v, namaOfGod);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        setTheCountValuesWithUserNamas(true, namaObject, totalcount, printed_count, running_count);
        godForChatSpinner.setTag(namaObject); //It is very important to prepare key

    }

    @SuppressLint("LongLogTag")
    public int uploadFile(String sourceFileUri) {

        int serverResponseCode = 0;

        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File(sourceFileUri);

        if (!sourceFile.isFile()) {

            Log.e(TAG, "Source File not exist :" + sourceFileUri);

            progressDialog.dismiss();

            Log.e("uploadFile", "Source File not exist :"
                    + uploadFilePath + "" + uploadFileName);

            mActivity.runOnUiThread(new Runnable() {
                public void run() {
//                     messageText.setText("Source File not exist :"
//                             +uploadFilePath + "" + uploadFileName);
                }
            });

            return 0;

        } else {
            try {

                // open a URL connection to the Servlet
                FileInputStream fileInputStream = new FileInputStream(sourceFile);
                String userID = MyApplication.mPref.getString(getResources().getString(R.string.pref_user_id_key), "");

//                 String urlFormation = WebConstants.UPLOAD_IMAGE+"?user_id="+userID+"&user_nama_release_id="+"110";//+"&file=namakoti_img";
                String urlFormation = WebConstants.UPLOAD_IMAGE;//+"&file=namakoti_img";
                Debug.e(TAG, "url:" + WebConstants.UPLOAD_IMAGE);
                URL url = new URL(urlFormation);

                // Open a HTTP  connection to  the URL
                conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                conn.setRequestProperty("user_id", userID);
                conn.setRequestProperty("user_nama_release_id", "110");
                conn.setRequestProperty("uploaded_file", fileName);

                dos = new DataOutputStream(conn.getOutputStream());

                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + fileName + "\"" + lineEnd);

//                 dos.writeBytes("Content-Disposition: form-data; name="+uploaded_file+";"+ fileName + """ + lineEnd);

                dos.writeBytes(lineEnd);

                // create a buffer of  maximum size
                bytesAvailable = fileInputStream.available();

                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];

                // read file and write it into form...
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                while (bytesRead > 0) {

                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                }

                // send multipart form data necesssary after file data...
                dos.writeBytes(lineEnd);
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                // Responses from the server (code and message)
                serverResponseCode = conn.getResponseCode();
                String serverResponseMessage = conn.getResponseMessage();

                Log.i("uploadFile", "HTTP Response is : "
                        + serverResponseMessage + ": " + serverResponseCode);

                if (serverResponseCode == 200) {

                    mActivity.runOnUiThread(new Runnable() {
                        public void run() {

                            Log.e(TAG, "File Upload Completed.\n\n See uploaded file here : \n\n");

//                              String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
//                                            +" http://www.androidexample.com/media/uploads/"
//                                            +uploadFileName;

//                              messageText.setText(msg);
//                              Toast.makeText(PublishnamaFragment.this, "File Upload Complete.", 
//                                           Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                //close the streams //
                fileInputStream.close();
                dos.flush();
                dos.close();

            } catch (MalformedURLException ex) {

                Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
                progressDialog.dismiss();
                ex.printStackTrace();

                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
//                        messageText.setText("MalformedURLException Exception : check script url.");
//                        Toast.makeText(PublishnamaFragment.this, "MalformedURLException", 
//                                                            Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {

                Log.e("Upload file to server Exception", "Exception : "
                        + e.getMessage(), e);
                progressDialog.dismiss();
                e.printStackTrace();

                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        Log.e(TAG, "Got Exception : see logcat ");
//                        messageText.setText("Got Exception : see logcat ");
//                        Toast.makeText(PublishnamaFragment.this, "Got Exception : see logcat ", 
//                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
            progressDialog.dismiss();
            return serverResponseCode;

        } // End else block
    }


    @Override
    public void onDetach() {
        super.onDetach();
        MyApplication.mEditor.putString(getResources().getString(R.string.hand_written_image_namakoti), "").commit();
    }

    private void createProgresDialog(Activity c) {
        progressDialog = new ProgressDialog(c);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading image...");
    }

    public void onTempleSelected(JSONObject namaObject) {

        try {
            if (namaObject != null) {
                Log.i(TAG, "choose_the_temple: " + choose_the_temple);
                choose_the_temple.setText(namaObject.getString("temple_name"));
                choose_the_temple.setTag(namaObject);
            } else {
                choose_the_temple.setText("");
                choose_the_temple.setTag("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onPrintTypesSelected(JSONObject namaObject) {

        try {
            if (namaObject != null) {
                print_type.setText(namaObject.getString("prinitng_name"));
                print_type.setTag(namaObject);
            } else {
                print_type.setText("");
                print_type.setTag("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onCountSelected(JSONObject namaObject) {

        try {
            if (namaObject != null) {
                noOfNamasToPrint.setText(namaObject.getString("no_count"));
                noOfNamasToPrint.setTag(namaObject);
            } else {
                noOfNamasToPrint.setText("");
                noOfNamasToPrint.setTag("");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

