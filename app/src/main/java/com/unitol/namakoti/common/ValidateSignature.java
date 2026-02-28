//package com.unitol.namakoti.common;
//
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.params.BasicHttpParams;
//import org.apache.http.params.HttpParams;
//import org.apache.http.util.EntityUtils;
//import org.json.JSONObject;
//
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.os.AsyncTask;
//import android.util.Log;
//
//import com.unitol.namakoti.utils.WebConstants;
//import com.unitol.namakoti.utils.NamaKotiUtils;
//
//
//public class ValidateSignature extends AsyncTask<String, Void, Integer> {
//
//	private static final String TAG = "ValidateSignature";
//	private ProgressDialog progressDialog;
//	private Context c;
//
//	public ValidateSignature(Context ctx) {
//		c= ctx;
//		progressDialog = new ProgressDialog(c);
//		progressDialog.setIndeterminate(true);
//		progressDialog.setCancelable(false);
//		progressDialog.setMessage("validating purchase...");
//	}
//
//	@Override
//	protected void onPreExecute() {
//		progressDialog.show();
//	}
//
//	@Override
//	protected Integer doInBackground(String... params) {
//		int responseCode = 500;
//		 try {
//			 HttpParams httpParameters = new BasicHttpParams();
//			 JSONObject json = new JSONObject();
//
//			 HttpClient httpclient = new DefaultHttpClient(httpParameters);
//
//			 HttpPost httpPost = null;
//			 HttpResponse response;
//
//			 httpPost = new HttpPost(WebConstants.BASE_URL+WebConstants.SIGN_IN);
//			 Log.i(TAG, "params[0]: "+params[0]+" , params[1]: "+params[1]+" , params[2]: "+params[2]);
//			 json.put("email", params[0]);
//			 json.put("signature", params[1]);
//			 json.put("purchasedData", params[2]);
//
//			 httpPost.addHeader("content-type", "application/json");
//			 httpPost.setEntity(new StringEntity(json.toString()));
//
//			 response = httpclient.execute(httpPost);
//			 responseCode = response.getStatusLine().getStatusCode();
//			 Log.i(TAG,"responseCode: "+ responseCode);
//
//			 String responseString = EntityUtils.toString(response.getEntity());
//			 Log.i(TAG,"responseString: "+ responseString);
//
//			 JSONObject jo = new JSONObject(responseString);
//
//		  } catch (Exception e) {
//			  Log.e(TAG,">>> Exception:+ checkPlanCode(,,)"+e);
//			  e.printStackTrace(); }
//		 return responseCode;
//	}
//
//	@Override
//	protected void onPostExecute(Integer responseCode) {
//		progressDialog.dismiss();
//		if (responseCode == 200) {
////			BalanceRequest bal = new BalanceRequest();
////			bal.execute(currentPin);
////			sendDataToServer();
//		} else if (responseCode == 401) {
//			NamaKotiUtils.showAlertDialog(c, "UnAuthorized data found. Please make a valid purchase");
//		} else if (responseCode == 500) {
//			NamaKotiUtils.showAlertDialog(c, "Internal server error. Please contact Support Team");
//		}else{
//			NamaKotiUtils.showAlertDialog(c, "Unknown error");
//		}
//	}
//}
