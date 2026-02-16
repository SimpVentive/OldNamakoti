package com.namakoti.utils;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.collection.LruCache;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by anusha on 12/5/2017.
 */
public class VolleyUtil {

    private static final String TAG = "VolleyUtil";
    private static VolleyUtil mVolleyUtil;
    private static HurlStack hurlStack;

    private VolleyUtil() {

    }
    private static SSLSocketFactory createSslSocketFactory() {
        TrustManager[] byPassTrustManagers = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }
        }};

        SSLContext sslContext = null;
        SSLSocketFactory sslSocketFactory = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, byPassTrustManagers, new SecureRandom());
            sslSocketFactory = sslContext.getSocketFactory();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Log.e(TAG, "@@", e);
        } catch (KeyManagementException e) {
            Log.e(TAG, "@@", e);
        }

        return sslSocketFactory;
    }



    public static VolleyUtil getInstance() {

        if (mVolleyUtil == null)
            mVolleyUtil = new VolleyUtil();

        return mVolleyUtil;
    }
    /**
     * This is used to get the  response from Volley request as JSonObject
     *
     * @param url           - Dts url
     * @param serviceMethod - seperation of the enum
     * @param method        - either GET/POST
     * @param listener      - VolleyResponseListener
     */
    public void volleyJsonObjectRequest(final Context context, String url, JSONObject requestJsonObj, final ServiceMethod serviceMethod, int
            method, final VolleyResponseListener listener) {

        if(Constants.LOG) Utils.writeLogs("-----------------------------");
        if(Constants.LOG) Utils.writeLogs("URL " + url);


        if (requestJsonObj != null){
            if(Constants.LOG) Utils.writeLogs("-----------------------------");
            if(Constants.LOG) Utils.writeLogs("Request " + requestJsonObj.toString());
            if(Constants.LOG) Log.e("req" + ": ", "URL : " + url);
            if(Constants.LOG) Log.d("req" + ": ", "RequestParams : " + requestJsonObj.toString());
        }


        RequestQueue requestQueue = Volley.newRequestQueue(context, new HurlStack());

        JsonObjectRequest request = new JsonObjectRequest(method, url, requestJsonObj,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Object object = null;
                        if(Constants.LOG)
                        Log.d("req" + ": ", "some Request Response : " + response.toString());

                        if(Constants.LOG) Utils.writeLogs("-----------------------------");
                        if(Constants.LOG) Utils.writeLogs("Service Response: " + response.toString());
                        if (response != null && response.toString() != null) {
                            try {
                                //Parse the details
                                object = GsonUtil.parseResponse(context, serviceMethod, response.toString(), null);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            if(listener != null)
                                listener.successResponse(serviceMethod, object);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(Constants.LOG)
                        Log.d("" + ": ", "Error Response code: " + error.getMessage());

                        if(Constants.LOG) Utils.writeLogs("-----------------------------");
                        if(Constants.LOG) Utils.writeLogs("Service Error " + error.getMessage());

                        if(listener != null)
                            listener.errorResponse(serviceMethod, error);
                    }
                });

        request.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy
                .DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }


    /**
     * This is used to get the  response from Volley request as JSonObject
     *
     * @param url           - Dts url
     * @param serviceMethod - seperation of the enum
     * @param method        - either GET/POST
     * @param listener      - VolleyResponseListener
     */
    public static void mapsVolleyJsonObjectRequest(final Context context, String url, JSONObject requestJsonObj, final
    ServiceMethod  serviceMethod, int  method, final VolleyResponseListener listener) {

        RequestQueue requestQueue = Volley.newRequestQueue(context, new HurlStack());

        if (requestJsonObj != null)
            if(Constants.LOG)
            Log.d("req" + ": ", "RequestParams : " + requestJsonObj.toString());

        JsonObjectRequest request = new JsonObjectRequest(method, url, requestJsonObj,

                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Object object = null;
                        if(Constants.LOG)
                        Log.d("req" + ": ", "some Request Response : " + response.toString());
                        if (response != null && response.toString() != null) {
                            try {

                                //Parse the details
                                object = GsonUtil.parseResponse(context, serviceMethod, response.toString(), listener
                                        .getParamsMap(serviceMethod));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            listener.successResponse(serviceMethod, object);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(Constants.LOG)
                        Log.d("" + ": ", "Error Response code: " + error.getMessage());
                        listener.errorResponse(serviceMethod, error);
                    }
                });

        requestQueue.add(request);
    }

    /**
     * This is used to get the  response from Volley request as String
     *
     * @param url           - Dts url
     * @param serviceMethod - seperation of the enum
     * @param method        - either GET/POST
     * @param listener      - VolleyResponseListener
     */

    public void volleyStringRequest(final Context context, String url, final Map<String, String> params, final
    ServiceMethod  serviceMethod, int  method, final VolleyResponseListener listener) {

        RequestQueue requestQueue = Volley.newRequestQueue(context, new HurlStack());

        if(Constants.LOG){
            if (params != null){
                Log.d(VolleyLog.TAG, serviceMethod + " Request : " + url);
                Log.d(VolleyLog.TAG, "params " + params.toString());
            }

        }

        StringRequest strReq = new StringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(Constants.LOG)
                Log.d(TAG, "Response String : " + response.toString());

                if (null != response.toString()) {
                    Object object = null;
                    try {
//                        Document doc = Jsoup.parse(response);
//                        String parsedText = doc.body().text();
                        //Parse the details
                        object = GsonUtil.parseResponse(context, serviceMethod, response.toString(), listener
                                .getParamsMap(serviceMethod));
                        if(listener != null){
                            if (serviceMethod == ServiceMethod.TRANSACTIONS)
                                listener.successResponse(serviceMethod, response.toString());
                            else
                                listener.successResponse(serviceMethod, object);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if(Constants.LOG)
                    Log.d(TAG, "Response obj: " + object);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                /*try {
                    String responseBody = new String( volleyError.networkResponse.data, "utf-8" );
                    JSONObject jsonObject = new JSONObject( responseBody );
                } catch ( JSONException e ) {
                    //Handle a malformed json response
                } catch (UnsupportedEncodingException error){
                }*/
                if (volleyError != null){
                    if (volleyError.getClass().equals(TimeoutError.class)) {
                        // Show timeout error message
//                        Toast.makeText(getContext(),
//                                "Oops. Timeout error!",
//                                Toast.LENGTH_LONG).show();
                    }
                    if(Constants.LOG){
                        Log.e(TAG + ": ", "Error Response code: " + volleyError.getMessage());
                        VolleyLog.d(TAG, "Error: " + volleyError.getMessage());
                    }
                    listener.errorResponse(serviceMethod, volleyError);
                }
            }
        }) {

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }
            /*@Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }*/

            //Get the parameters
            @Override
            protected Map<String, String> getParams() {
//                mParamsMap = listener.getParamsMap(serviceMethod);
//                if(Constants.LOG)
//                Log.d(TAG + ": ", "GetParams: " + mParamsMap);
//                return mParamsMap;
                return params;
            }

        };
        requestQueue.add(strReq);
    }

    private static TrustManager[] getWrappedTrustManagers(TrustManager[] trustManagers) {
        final X509TrustManager originalTrustManager = (X509TrustManager) trustManagers[0];
        return new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return originalTrustManager.getAcceptedIssuers();
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        try {
                            if (certs != null && certs.length > 0) {
                                certs[0].checkValidity();
                            } else {
                                originalTrustManager.checkClientTrusted(certs, authType);
                            }
                        } catch (CertificateException e) {
                            Log.w("checkClientTrusted", e.toString());
                        }
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        try {
                            if (certs != null && certs.length > 0) {
                                certs[0].checkValidity();
                            } else {
                                originalTrustManager.checkServerTrusted(certs, authType);
                            }
                        } catch (CertificateException e) {
                            Log.w("checkServerTrusted", e.toString());
                        }
                    }
                }
        };
    }

    public ImageLoader imageLoader(Context context, final int size) {
        RequestQueue mRequestQueue = VolleySingleton.getInstance().getRequestQueue();
//        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        ImageLoader mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(size);
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });
        return mImageLoader;
    }

/*
    public static SSLSocketFactory getSSLSocketFactory(Context context)
            throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException,
            KeyManagementException {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream caInput = context.getResources().openRawResource(R.raw.www_wekaotimprod_com); // this cert file stored in
        // \app\src\main\res\raw folder path

        Certificate ca = cf.generateCertificate(caInput);
        caInput.close();

        KeyStore keyStore = KeyStore.getInstance("BKS");
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        TrustManager[] wrappedTrustManagers = getWrappedTrustManagers(tmf.getTrustManagers());

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, wrappedTrustManagers, null);

        return sslContext.getSocketFactory();
    }
*/

}


