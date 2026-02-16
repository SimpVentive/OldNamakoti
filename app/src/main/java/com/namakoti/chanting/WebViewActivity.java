package com.namakoti.chanting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.namakoti.R;
import com.namakoti.base.BaseActivity;
import com.namakoti.base.MyApplication;
import com.namakoti.utils.Constants;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Pattern;


public class WebViewActivity extends BaseActivity {
 
    protected static final String TAG = "WebViewActivity";
	//private Button button;
    private WebView webView;
    
	private ProgressDialog progressDialog;
    final String uploadFilePath = "/mnt/sdcard/Namakoti/";
    private String uploadFileName = "";
    
    public void onCreate(Bundle savedInstanceState) {
         
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view);
        
        Bundle extras = getIntent().getExtras();
        
//        ImageView logo = (ImageView) findViewById(R.id.ic);
//        if (extras != null) {
//            logo.setImageResource(extras.getInt(Constants.WEBVIEW_HDR_IMG));
//		}
//
//        ImageView back = (ImageView) findViewById(R.id.back);
//        back.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				finish();
//			}
//		});
//
        String url = getIntent().getExtras().getString(Constants.WEBVIEW_URL);
         
        //Get webview 
        webView = (WebView) findViewById(R.id.webView1);
        setToolbarWithBack(getString(R.string.payment));
//        String url = WebConstants.UPGRADE_URL;
        startWebView(url);
         
    }
     
    private void startWebView(String url) {
         
        //Create new webview Client to show progress dialog
        //When opening a url or click on link


        webView.setWebViewClient(new WebViewClient() {
            ProgressDialog progressDialog;
          
            //If you will not use this method url links are opeen in new brower not in webview
            public boolean shouldOverrideUrlLoading(WebView view, String url) {              
                view.loadUrl(url);
                return true;
            }
            
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
            	super.onPageStarted(view, url, favicon);
            	Log.i(TAG, "onPageStarted : ");

                showProgress(true, WebViewActivity.this);
            	//anu
                /*if (progressDialog == null) {
                    // in standard case YourActivity.this
                    progressDialog = new ProgressDialog(WebViewActivity.this);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setMessage("Loading...");
                }
                if (!progressDialog.isShowing()) {
                    progressDialog.show();
				}*/
            }
        
            //Show loader on url load
            /*public void onLoadResource (WebView view, String url) {
                if (progressDialog == null) {
                    // in standard case YourActivity.this
                    progressDialog = new ProgressDialog(WebViewActivity.this);
                }
                progressDialog.setMessage("Loading...");
                progressDialog.show();
            }*/
            public void onPageFinished(WebView view, String url) {

            	Log.i(TAG, "onPageFinished url: "+url);
                try{
                    //anu
                    showProgress(false, WebViewActivity.this);
                	/*Log.i(TAG, "progressDialog: "+progressDialog);
	                if (progressDialog != null && progressDialog.isShowing()) {
	                    progressDialog.dismiss();
//	                    progressDialog = null;
	                }*/
	                
	                //It is for print or upgrade_and_print
	                if (url.contains(Constants.WEBVIEW_SUCCES_URL)) {
	                	Toast.makeText(WebViewActivity.this, "Your payment is succes, thank you", Toast.LENGTH_SHORT).show();

	                	final String img = MyApplication.mPref.getString(getResources().getString(R.string.hand_written_image_namakoti), "");
	    		    	MyApplication.mEditor.putInt(getString(R.string.pref_user_upgrade_key), 1).commit();	
	                	String[] splittedString = url.split("=");
//	                	Log.i(TAG, "splittedString: "+splittedString);
//	                	Log.i(TAG, "splittedString length: "+splittedString.length);
	                	if (splittedString.length > 1) {
		                	String releaseID = splittedString[1];
//		                	Log.i(TAG, "releaseID: "+releaseID);
		                	String[] upgradeUrl = releaseID.split(Pattern.quote(","));
//		                	Log.i(TAG, "upgradeUrl: "+upgradeUrl);
//		                	Log.i(TAG, "upgradeUrl length: "+upgradeUrl.length);
		                	if (upgradeUrl.length > 1) {
		                	    //anu
//			                	MainActivityPhone.getInstance().finish();
								finish();
                                //anu
//								Intent i = new Intent(WebViewActivity.this, MainActivityPhone.class);
//								i.putExtra(Constants.UPGRADED_SUCCESFUL, true);
//								startActivity(i);
							} else {
								//It is print url. Write logic for upgrade
			                	uploadImae(releaseID);
//								uploadImg(uploadFilePath+""+img, releaseID);
//								executeMultipartPost(uploadFilePath, img, releaseID);
							}
						} else if (url.contains(Constants.WEBVIEW_SUCCES_URL_4_UPGRADE)) {
							//It is upgrade url. Write logic for upgrade
		    		    	MyApplication.mEditor.putInt(getString(R.string.pref_user_upgrade_key), 1).commit();
                            //anu
//		    		    	MainActivityPhone.getInstance().finish();
		    		    	finish();
						} else {
							Log.i(TAG, "Not a upgrade or print");
						}
	                	
	                	
	                	
//	    		    	MyApplication.mEditor.putInt(getString(R.string.pref_user_upgrade_key), 1).commit();	
//	                	MainActivityPhone.getInstance().finish();
//						finish();
//						startActivity(new Intent(WebViewActivity.this, MainActivityPhone.class));
					} else if (url.contains(Constants.WEBVIEW_SUCCES_URL_4_UPGRADE)) {
						//It is upgrade url. Write logic for upgrade
	    		    	MyApplication.mEditor.putInt(getString(R.string.pref_user_upgrade_key), 1).commit();
                        //anu
//	    		    	MainActivityPhone.getInstance().finish();
	    		    	finish();
                        //anu
//	    		    	startActivity(new Intent(WebViewActivity.this, MainActivityPhone.class));
					} else if(url.contains(Constants.WEBVIEW_FAILURE_URL)){
	                	Toast.makeText(WebViewActivity.this, "Your payment is failure, please try again", Toast.LENGTH_SHORT).show();
					}
                
                }catch(Exception exception){
                    exception.printStackTrace();
                    Log.e(TAG, ""+exception.toString());
                }
            }
             
        }); 
          
         // Javascript inabled on webview  
        webView.getSettings().setJavaScriptEnabled(true); 
         
        // Other webview options
        /*
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);
        webView.getSettings().setBuiltInZoomControls(true);
        */
         
        /*
         String summary = "<html><body>You scored <b>192</b> points.</body></html>";
         webview.loadData(summary, "text/html", null); 
         */
         
        //Load url in webview
        webView.loadUrl(url);
          
          
    }
     
    private void doPrintLogic() {
        //anu
//    	PrintListAsync printListAsync = new PrintListAsync(this);
//    	printListAsync.execute();
	}
    
    
    /*private void doPrintLogic() {
    	
    	PrintListAsync printListAsync = new PrintListAsync(this);
    	printListAsync.execute();
    	
    	MainActivityPhone.getInstance().finish();
		finish();
		Intent i = new Intent(WebViewActivity.this, MainActivityPhone.class);
		i.putExtra(ConstantsManager.PRINT_SUCCESFUL, true);
		startActivity(i);
	}*/
    
    protected void uploadImae(final String releaseID) {

    	Log.i(TAG, "releaseID: "+releaseID);
    	final String img = MyApplication.mPref.getString(getResources().getString(R.string.hand_written_image_namakoti), "");
        if (!img.isEmpty()) {
        	uploadFileName = img;
        	createProgresDialog(this);
            progressDialog.show();
        	
            new Thread(new Runnable() {
                    public void run() {
                         runOnUiThread(new Runnable() {
                                public void run() {
//                                    messageText.setText("uploading started.....");
                                }
                            });                      
                       
                         uploadFile(uploadFilePath + "" + uploadFileName, releaseID);;
                                                  
                    }
                  }).start();
            
            return;
		} else {
			doPrintLogic();
		}
        
	}

	// Open previous opened link from history on webview when back button pressed
     
    @Override
    // Detect when the back button is pressed
    public void onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
        } else {
            // Let the system handle the back button
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return false;
    }

    @SuppressLint("LongLogTag")
    public int uploadFile(String sourceFileUri, String releaseID) {

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
            
        	Log.e(TAG, "Source File not exist :"+sourceFileUri);
        	
        	progressDialog.dismiss(); 
              
             Log.e("uploadFile", "Source File not exist :"
                                 +uploadFilePath + "" + uploadFileName);
              
             runOnUiThread(new Runnable() {
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
                 String urlFormation = Constants.UPLOAD_IMAGE+"?user_id="+userID+"&user_nama_release_id="+releaseID;//+"&file=namakoti_img";
                 Log.i(TAG, "urlFormation: ======"+urlFormation);
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
//                 conn.setRequestProperty("user_id", userID); 
//                 conn.setRequestProperty("user_nama_release_id", releaseID); 
                 conn.setRequestProperty("uploaded_file", fileName); 
                  
                 dos = new DataOutputStream(conn.getOutputStream());
        
                 dos.writeBytes(twoHyphens + boundary + lineEnd); 
                 dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""+ fileName + "\"" + lineEnd);
                 
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
                  
                 if(serverResponseCode == 200){
                      
                     runOnUiThread(new Runnable() {
                          public void run() {
                        	  doPrintLogic();

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
                 
                runOnUiThread(new Runnable() {
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
                 
                runOnUiThread(new Runnable() {
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
    

	private void createProgresDialog(Activity c) {
		progressDialog = new ProgressDialog(c);
		progressDialog.setIndeterminate(true);
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setMessage("Uploading image...");
	}
	
}
