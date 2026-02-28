package com.unitol.namakoti.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.unitol.namakoti.BaseFragment;
import com.unitol.namakoti.CountryDialogActivity;
import com.unitol.namakoti.DateDialogActivity;
import com.unitol.namakoti.MonthDialogActivity;
import com.unitol.namakoti.MyApplication;
import com.unitol.namakoti.R;
import com.unitol.namakoti.SalutionDialogActivity;
import com.unitol.namakoti.YearDialogActivity;
import com.unitol.namakoti.util.Debug;
import com.unitol.namakoti.utils.ConstantsManager;
import com.unitol.namakoti.utils.NamaKotiUtils;
import com.unitol.namakoti.utils.WebConstants;
import com.unitol.namakoti.web.CountryISDAsync;
import com.unitol.namakoti.web.EditProfileAsync;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class EditProfileFragmentUrlConnection extends BaseFragment implements OnClickListener{
	public static final String TAG = "EditProfileFragment";

	String username, password, re_confirm_pwd, salution, first_name, last_name,
			dob, address1, address2, po_box, postal_code, state, country,
			email, mobile;
	
	String title,date,month,year;
	String gender;
	
	EditText username1, password1, re_confirm_pwd1, salution1, first_name1,
			last_name1, dob1, address11, address21, postal_code1,
			state1, email1, mobile1;
//	EditText date1,month1,year1;
	Button save;

	private static String ISD_CODE;
	private EditText mobileCode;

	private LinearLayout dateSpinner;
	private TextView date_text;
	private TextView month_text;
	private TextView year_text;

	private LinearLayout monthSpinner;

	private LinearLayout yearSpinner;

	private EditText gothram1;
	
	private TextView mobile_country_code;

	private EditText city_profile1;

	private RadioGroup rg;

	private Button salutionProfile;

	private Button country1;

	private RadioButton male_rb;

	private RadioButton female_rb;
	
	String[] arrayMonths = {
	        "January-:-01",
	        "February-:-02",
	        "March-:-03",
	        "April-:-04",
	        "May-:-05",
	        "June-:-06",
	        "July-:-07",
	        "August-:-08",
	        "September-:-09",
	        "October-:-10",
	        "November-:-11",
	        "December-:-12"
	};

	private String allSalutes;

	private String allCountries;
	private int responseCode = 0;

	public View onCreateView(android.view.LayoutInflater inflater, android.view.ViewGroup container, android.os.Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.edit_profile_new, container, false);
		
		mActivity.onFragmentTabChange(View.VISIBLE, R.drawable.edit_profile_hdr, 0, "", View.INVISIBLE);
		NamaKotiUtils.setupUI(v.findViewById(R.id.parent), mActivity);
		
		first_name1 = (EditText) v.findViewById(R.id.first_name_profile);
		last_name1 = (EditText) v.findViewById(R.id.last_name_profile);
		address11 = (EditText) v.findViewById(R.id.address1_profile);
		address21 = (EditText) v.findViewById(R.id.address2_profile);
		postal_code1 = (EditText) v.findViewById(R.id.postal_profile);
		state1 = (EditText) v.findViewById(R.id.state_profile);
		country1 = (Button) v.findViewById(R.id.country_profile);
		email1 = (EditText) v.findViewById(R.id.email_profile);
		mobileCode = (EditText) v.findViewById(R.id.mobile_code_profile);
		mobile1 = (EditText) v.findViewById(R.id.mobile_profile);
		gothram1 = (EditText) v.findViewById(R.id.user_gothram);
		city_profile1 = (EditText) v.findViewById(R.id.city_profile);
		mobile_country_code = (TextView) v.findViewById(R.id.mobile_country_code);
		
        rg = (RadioGroup) v.findViewById(R.id.radioGroupGender);
        rg.setOnCheckedChangeListener(new OnRadioCheckedChangeListener());
        male_rb = (RadioButton) v.findViewById(R.id.male_rb);
        female_rb = (RadioButton) v.findViewById(R.id.female_rb);
		
		salutionProfile = (Button) v.findViewById(R.id.salution_profile);
		salutionProfile.setOnClickListener(this);
		country1.setOnClickListener(this);
		
		/*first_name1.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				String userName = email1.getText().toString().trim();

				if(hasFocus && onFocusChanged(email1, "In valid email")){ //It is valid email CheckEmailAsync async
			 		
					 CheckEmailAsync asyncEmail = new CheckEmailAsync(mActivity);
					 asyncEmail.execute(email1.getText().toString().trim()); }
				 else {
//					 passwordEditText.setCursorVisible(false); 
				 }
			}
		});*/
		
		/*salutionProfile.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});*/
		
		/*country1.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				JSONObject selctedItem = (JSONObject) country1.getSelectedItem();
				Log.i(TAG, "selctedItem: "+selctedItem);
//				selctedItem = {"cc":"IND","cname":"India"}
				
				String cc = null;
				try {
					cc = selctedItem.getString("cc");

					CountryISDAsync countryISDAsync = new CountryISDAsync(mActivity, EditProfileFragment.this);
					countryISDAsync.execute(cc);
					
				} catch (Exception e) {
					// TODO: handle exception
				}
				

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});*/
		
		date_text = (TextView) v.findViewById(R.id.date_text);
		month_text = (TextView) v.findViewById(R.id.month_text);
		year_text = (TextView) v.findViewById(R.id.year_text);
		
		dateSpinner = (LinearLayout) v.findViewById(R.id.date_ll);
		monthSpinner = (LinearLayout) v.findViewById(R.id.month_ll);
		yearSpinner = (LinearLayout) v.findViewById(R.id.year_ll);
		
		dateSpinner.setOnClickListener(this);
		monthSpinner.setOnClickListener(this);
		yearSpinner.setOnClickListener(this);
		
//		DatesAdapter adapter = new DatesAdapter(mActivity, dates);
//		dateSpinner.setAdapter(adapter);
		
//		List<String> months = new ArrayList<String>();
		
		/*for (int i = 1; i <= 12; i++) {
			months.add(""+i);
		}*/
		
//		MonthsAdapter adapterMonths = new MonthsAdapter(mActivity, arrayMonths);
//		monthSpinner.setAdapter(adapterMonths);
		
		save=(Button)v.findViewById(R.id.save_id);
		save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				if (!mActivity.haveNetworkConnection()) {
					NamaKotiUtils.enableWifiSettings(mActivity);
					Log.i(TAG, "We do not have the network connection");
					return;
				} 
				
//				Log.i(TAG, "We have the network connection");
//				Log.i(TAG, "year spinner"+year_text.getText().toString().trim());
//				Log.i(TAG, "month spinner"+month_text.getTag().toString().trim());
//				Log.i(TAG, "date spinner"+date_text.getText().toString().trim());
				
				String yearS = year_text.getText().toString().trim();
				String monthS = month_text.getTag().toString().trim();
				String dateS = date_text.getText().toString().trim();
				
//				Log.e(TAG, "yearS: ======"+yearS);
//				Log.e(TAG, "monthS: ======"+monthS);
//				Log.e(TAG, "dateS: ======"+dateS);
				
				boolean isNotValid = false;
				
				if(!yearS.isEmpty() && !monthS.isEmpty() && !dateS.isEmpty()){
					//Do nothing
					Log.e(TAG, "No one is empty==========");
					if(dateS.equalsIgnoreCase("Day")) {
						Toast.makeText(mActivity, "Please select day", Toast.LENGTH_SHORT).show();
						isNotValid = true;
					}

					if(monthS.isEmpty()){
						Toast.makeText(mActivity, "Please select month", Toast.LENGTH_SHORT).show();
						isNotValid = true;
					}
					
					if(yearS.equalsIgnoreCase("Year")){
						Toast.makeText(mActivity, "Please select year", Toast.LENGTH_SHORT).show();
						isNotValid = true;
					}
						
					if (isNotValid) {
						return;
					}
				} else if((!yearS.isEmpty() && !yearS.equalsIgnoreCase("Year")) || (!monthS.isEmpty()) 
						|| (!dateS.isEmpty() && !dateS.equalsIgnoreCase("Day"))){
					
//					int monthInt = Integer.parseInt(monthS);

					if(dateS.equalsIgnoreCase("Day")) {
						Toast.makeText(mActivity, "Please select day", Toast.LENGTH_SHORT).show();
						isNotValid = true;
					}

					if(monthS.isEmpty()){
						Toast.makeText(mActivity, "Please select month", Toast.LENGTH_SHORT).show();
						isNotValid = true;
					}
					
					if(yearS.equalsIgnoreCase("Year")){
						Toast.makeText(mActivity, "Please select year", Toast.LENGTH_SHORT).show();
						isNotValid = true;
					}
						
					if (isNotValid) {
						return;
					}
				}
				
				String pc = postal_code1.getText().toString();
				if (!pc.isEmpty()) {
					int postalLength = pc.length();
					if (postalLength < 3 || postalLength > 10) {
						Toast.makeText(mActivity, "Postal code must be minimum lenght 3 and maximum of 10", Toast.LENGTH_SHORT).show();
						return;
					}
				}
				
				String mobile = mobile1.getText().toString();
				if (!mobile.isEmpty()) {
					int mobileLength = mobile.length();
					if (mobileLength < 7 || mobileLength > 13) {
						Toast.makeText(mActivity, "Mobile number must be minimum lenght 7 and maximum of 13", Toast.LENGTH_SHORT).show();
						return;
					}
				}
				
				Log.i(TAG, "After all validation =========== ");
				
				if (email1.length() > 0) {
					if(onFocusChanged(email1, "In valid email")){
						ProfileAsync profileAsync = new ProfileAsync();
						profileAsync.execute();
					}
				} else {
					ProfileAsync profileAsync = new ProfileAsync();
					profileAsync.execute();
				}
			}
		});

		first_name1.setText(first_name);
		last_name1.setText(last_name);
		address11.setText(address1);
		address21.setText(address2);
		postal_code1.setText(postal_code);
		state1.setText(state);
//		country1.setText(country);
		email1.setText(email);
		mobile1.setText(mobile);
		
		EditProfileAsync editProfileAsync = new EditProfileAsync(mActivity, this);
		editProfileAsync.execute();
		
		return v;
	};
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.i(TAG, "onActivityResult: " + resultCode);
		
		if (data == null) {
			Log.i(TAG, "data is null");
			return;
		}

        final String selectedItem;
		switch (requestCode) {
		case ConstantsManager.SALUTION_CODE:
            selectedItem = data.getExtras().getString(ConstantsManager.SALUTION_JSON);
            onSalutionSelected(selectedItem);
            break;
		case ConstantsManager.DATE_CODE:
            selectedItem = data.getExtras().getString(ConstantsManager.DATE_JSON_4_ADAPTER);
            onDateSelected(selectedItem);
            break;
		case ConstantsManager.MONTH_CODE:
            selectedItem = data.getExtras().getString(ConstantsManager.MONTH_JSON_4_ADAPTER);
            onMonthSelected(selectedItem);
            break;
		case ConstantsManager.YEAR_CODE:
            selectedItem = data.getExtras().getString(ConstantsManager.YEAR_JSON_4_ADAPTER);
            onYearSelected(selectedItem);
            break;
		case ConstantsManager.COUNTRY_CODE:
            selectedItem = data.getExtras().getString(ConstantsManager.COUNTRY_JSON_4_ADAPTER);
            onCountrySelected(selectedItem);
            break;
		default:
            selectedItem = data.getExtras().getString(ConstantsManager.SALUTION_JSON);
            onSalutionSelected(selectedItem);
			break;
		}
	}
	
	protected void onSalutionSelected(String tag2) {
		Log.i(TAG, "tag2: "+tag2);
		try {
			JSONObject obj = new JSONObject(tag2);
			salutionProfile.setText(obj.getString("name"));
			salutionProfile.setTag(obj);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	protected void onDateSelected(String tag2) {
		Log.i(TAG, "tag2: "+tag2);
		try {
//			JSONObject obj = new JSONObject(tag2);
			date_text.setText(tag2);
//			salutionProfile.setTag(obj);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	protected void onMonthSelected(String tag2) {
		Log.i(TAG, "tag2: "+tag2);
		
		if (tag2 == null || tag2.isEmpty()) {
			return;
		}
		
		try {
			String[] mo = tag2.split("-:-");
//			JSONObject obj = new JSONObject(tag2);
			month_text.setText(mo[0]);
			month_text.setTag(mo[1]);
//			salutionProfile.setTag(obj);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	protected void onYearSelected(String tag2) {
		Log.i(TAG, "tag2: "+tag2);
		if (tag2 == null || tag2.isEmpty()) {
			return;
		}

		try {
//			JSONObject obj = new JSONObject(tag2);
			year_text.setText(tag2);
//			salutionProfile.setTag(obj);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	protected void onCountrySelected(String tag2) {
		Log.i(TAG, "tag2: "+tag2);
		
		try {
			JSONObject selctedItem = new JSONObject(tag2);
			String cc = selctedItem.getString("cc");
			String cname = selctedItem.getString("cname");
			country1.setText(cname);
			country1.setTag(selctedItem);

			if (!mActivity.haveNetworkConnection()) {
				NamaKotiUtils.enableWifiSettings(mActivity);
				Log.i(TAG, "We do not have the network connection");
				return;
			} 
			
			Log.i(TAG, "We have the network connection");
			
			CountryISDAsync countryISDAsync = new CountryISDAsync(mActivity, EditProfileFragmentUrlConnection.this);
			countryISDAsync.execute(cc);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public void updateCountryISD(String objString) {
//		obj: {"isd_code":"+93","ccode":"AFG"}
		try {
			JSONObject obj = new JSONObject(objString);
			String isd_code = obj.getString("isd_code");
			mobile_country_code.setText(isd_code);
			ISD_CODE = obj.getString("ccode");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private class OnRadioCheckedChangeListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			Log.i(TAG, "onCheckedChanged");

			switch (checkedId) {
			case R.id.male_rb:
				Log.i(TAG, "onCheckedChanged change default male");
				gender = "M";
				break;
			case R.id.female_rb:
				gender = "F";
				break;
			default:
				break;
			}
		}
		
	}

	private boolean onFocusChanged(EditText et, String msg) {
		if (et.length() <= 0) {
			return false;
		} else if (!et.getText().toString().matches(ConstantsManager.emailPattern)) {
			NamaKotiUtils.showDialog(mActivity, "Update profile", msg);
			et.clearFocus();
			et.requestFocus();
			return false;
		}
		return true;
	}
	
	public void updateView(@NonNull String response) {
		Log.i(TAG, "response==== "+response);
		if(!response.isEmpty()){
			try {
				JSONObject object = new JSONObject(response);
				String un = object.getString("user_name"); 
				String title =object.getString("title");
				title = title.equals("null") ? "": title;
				String title_name =object.getString("title_name");
				title_name = title_name.equals("null") ? "": title_name;
				try {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("name", title_name);
					jsonObject.put("code", title);
					salutionProfile.setTag(jsonObject);
					Log.i(TAG, "title_name: "+title_name);
					salutionProfile.setText(title_name);
				} catch (Exception e) {
					Log.e(TAG, "e "+e.toString());
				}
				String first_name =object.getString("first_name"); 
				first_name=first_name.equals("null") ? "": first_name;
				first_name1.setText(first_name);
				String last_name =object.getString("last_name");
				last_name=last_name.equals("null") ? "": last_name;
				last_name1.setText(last_name);
				String gender =object.getString("gender");
				
				if (gender.equals("M")) {
			        male_rb.setChecked(true);
			        male_rb.setFocusable(true);
				} else if (gender.equals("F")) {
					female_rb.setChecked(true);
					female_rb.setFocusable(true);
				}
				
				String date_of_birth =object.getString("date_of_birth"); 
				if (!date_of_birth.equals("null") && !date_of_birth.isEmpty()) {
					String[] dob = date_of_birth.split("-");
					String y = dob[0];
					String m = dob[1];
					String d = dob[2];
					date_text.setText(d);
					month_text.setTag(m);
					year_text.setText(y);
				} else {
					month_text.setTag(""); //Added later when null pointer exception is coming
				}
				
				
				String date_of_birth2 =object.getString("date_of_birth2");
				String month2 = "Month";
				if (!date_of_birth2.equals("null")) {
					String[] dob2 = date_of_birth2.split(" ");
					month2 = dob2[1];
				}
				month_text.setText(month2);
				first_name1.setText(first_name);
				String gothram =object.getString("gothram"); 
				gothram=gothram.equals("null") ? "": gothram;
				gothram1.setText(gothram);
				
				String address1 =object.getString("address1");  
				address1=address1.equals("null") ? "": address1; 
				address11.setText(address1);
				String address2 =object.getString("address2");  
				address2=address2.equals("null") ? "": address2;
				address21.setText(address2);
				String zip_code =object.getString("zip_code");  
				zip_code=zip_code.equals("null") ? "": zip_code; 
				zip_code=zip_code.equals("0") ? "": zip_code; 
				postal_code1.setText(zip_code);
				String city =object.getString("city");  
				city=city.equals("null") ? "": city;
				city_profile1.setText(city);
				String state =object.getString("state"); 
				state = state.equals("null") ? "": state; 
				
				state1.setText(state);
//				"country":"AFG","country_name":"Afghanistan"
				String countryCode =object.getString("country"); 
				countryCode = countryCode.equals("null") ? "": countryCode; 
				String country =object.getString("country_name"); 
				country=country.equals("null") ? "": country; 
				country1.setText(country);
				
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("cc", countryCode);
				jsonObject.put("cname", country);
				country1.setTag(jsonObject);
				
				String email =object.getString("email"); 
				email=email.equals("null") ? "": email; 
				email1.setText(email);
				String isd_value =object.getString("isd_value"); 
				isd_value=isd_value.equals("null") ? "": isd_value; 
				mobile_country_code.setText(isd_value);
				String mobile =object.getString("mobile"); 
				mobile=mobile.equals("null") ? "": mobile; 
				mobile1.setText(mobile);
				
				
				allCountries = object.getString("allcountries");

				allSalutes = object.getString("allsalute");
				/*JSONArray allSalutesArray = new JSONArray(allSalutes);
				
				salutionList = new ArrayList<JSONObject>();
				for (int i = 0; i < allSalutesArray.length(); i++) {
					JSONObject saute = (JSONObject) allSalutesArray.get(i);
					salutionList.add(saute);
				}*/
				
//				SalutationAdapter adapterSalution = new SalutationAdapter(mActivity, salutionList);
//				salutionProfile.setAdapter(adapterSalution);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public class ProfileAsync extends AsyncTask<String, Void, String>{

		private ProgressDialog progressDialog;
		private Activity ctx;
    	String userID = MyApplication.mPref.getString(getString(R.string.pref_user_id_key), "");	
		String responseString = "";
		
		public ProfileAsync() {
			createProgresDialog(mActivity);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progressDialog.show();
		}
		
		@Override
		protected String doInBackground(String... params) {
			String result = null;

			
			responseString = null;

			try {

				responseString = useHttpurl(params);
//				Log.e(TAG, "responseCode: "+responseCode);
//				Log.e(TAG, "responseString: "+responseString);
				if (responseCode == 200) {
					
					result = responseString;
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(TAG, "e: "+e.toString());
			}
			
			return responseString;
		}

		private String useHttpurl(String[] inputs) {
			String responseString = "";

			try {
				URL url = new URL(WebConstants.UPDATE_PROFILE);
				Debug.e(TAG, "url:" + WebConstants.UPDATE_PROFILE);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setReadTimeout(20000);
				connection.setConnectTimeout(60000);
				connection.setRequestMethod("POST");
				connection.setUseCaches(false);
				connection.setDoInput(true);
				connection.setDoOutput(true);
				connection.connect();
            /* Passing Name and Telephone Number to HTTP Reqest API */
				responseString = getResponseByWithOutNameValuePair(connection);
				Log.e(TAG, "responseString ====== "+responseString);

				if (responseCode == HttpsURLConnection.HTTP_OK) {
					return responseString;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return responseString;
		}

		/** Gets the response code to the given number. */
		public String getResponseByWithOutNameValuePair(HttpURLConnection connection) {

			String response = "";
			try {
				OutputStream outputStream = connection.getOutputStream();
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
				bufferedWriter.write(getPostDataString(getParamsHashMap()));
				bufferedWriter.flush();
				bufferedWriter.close();
				outputStream.close();
				responseCode = connection.getResponseCode();
				Log.e(TAG, "responseCode ======= " + responseCode);

				String line;
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				while ((line = bufferedReader.readLine()) != null) {
					response += line;
				}
				connection.disconnect();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return response;
		}

		private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
			StringBuilder result = new StringBuilder();
			boolean first = true;
			for (Map.Entry<String, String> entry : params.entrySet()) {
				if (first)
					first = false;
				else
					result.append("&");

				result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
				result.append("=");
				result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
			}

			return result.toString();
		}

		/** Params to send API in HashMap format. */
		private HashMap<String, String> getParamsHashMap() {
			HashMap<String, String> params = new HashMap<String, String>();


			String dob = year_text.getText().toString().trim()+"-"+((String)month_text.getTag()).trim()+ "-"+ date_text.getText().toString().trim();
			try {

				JSONObject saluteObject = (JSONObject) salutionProfile.getTag();
				JSONObject countryObject = (JSONObject) country1.getTag();

				params.put("user_id", userID);
				params.put("password", "1234");
				params.put("title", saluteObject.getString("code"));
				params.put("first_name", first_name1.getText().toString().trim());
				params.put("last_name", last_name1.getText().toString().trim());
				params.put("gender", gender);
				params.put("date_of_birth", dob);
				params.put("gothram", gothram1.getText().toString().trim());
				params.put("address1", address11.getText().toString().trim());
				params.put("address2", address21.getText().toString().trim());
				params.put("zip_code", postal_code1.getText().toString().trim());
				params.put("city", city_profile1.getText().toString().trim());
				params.put("state", state1.getText().toString().trim());
				params.put("country", countryObject.getString("cc"));
				params.put("email", email1.getText().toString().trim());
				params.put("mobile", mobile1.getText().toString().trim());
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return params;
		}
		
		@Override
		protected void onPostExecute(String isSuccess) {
			super.onPostExecute(isSuccess);
			try {
				progressDialog.dismiss();
			} catch (Exception e) {
				Log.e(TAG, "e: "+e.toString());
			}
			
			if(isSuccess != null){// successful login
//				MyApplication.mEditor.putString(userID+getString(R.string.pref_view_profile_key), isSuccess).commit();
				mActivity.onBackPressed();
			} else {
			   
			}
			
		}
		
		private void createProgresDialog(Activity c) {
			progressDialog = new ProgressDialog(c);
			progressDialog.setIndeterminate(true);
			progressDialog.setCancelable(false);
			progressDialog.setMessage("Please wait...");
		}
}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.salution_profile:
			Intent i = new Intent(mActivity, SalutionDialogActivity.class);
			i.putExtra(ConstantsManager.SALUTION_JSON_4_ADAPTER, allSalutes);
			startActivityForResult(i, ConstantsManager.SALUTION_CODE);
			break;
		case R.id.date_ll:
			Intent i1 = new Intent(mActivity, DateDialogActivity.class);
//			i1.putExtra(ConstantsManager.DATE_JSON_4_ADAPTER, allSalutes);
			startActivityForResult(i1, ConstantsManager.DATE_CODE);
			break;
		case R.id.month_ll:
			Intent i2 = new Intent(mActivity, MonthDialogActivity.class);
//			i2.putExtra(ConstantsManager.MONTH_JSON_4_ADAPTER, allSalutes);
			startActivityForResult(i2, ConstantsManager.MONTH_CODE);
			break;
		case R.id.year_ll:
			Intent i3 = new Intent(mActivity, YearDialogActivity.class);
//			i3.putExtra(ConstantsManager.YEAR_JSON_4_ADAPTER, allSalutes);
			startActivityForResult(i3, ConstantsManager.YEAR_CODE);
			break;
		case R.id.country_profile:
			Intent i4 = new Intent(mActivity, CountryDialogActivity.class);
			i4.putExtra(ConstantsManager.COUNTRY_JSON_4_ADAPTER, allCountries);
			startActivityForResult(i4, ConstantsManager.COUNTRY_CODE);
			break;
		default:
			break;
		}
	}
}
