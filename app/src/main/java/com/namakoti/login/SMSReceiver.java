package com.namakoti.login;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;


import androidx.annotation.NonNull;

import com.namakoti.utils.Constants;

public class SMSReceiver extends BroadcastReceiver {

    private final static String SMS_ORIGIN = "IM-UNITOL";
    private static final String OTP_DELIMITER = ":";

    @Override
    public void onReceive(Context context, @NonNull Intent intent) {
        final Bundle bundle = intent.getExtras();
        try {
            if (bundle != null) {
                Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (Object aPdusObj : pdusObj) {
                    SmsMessage currentMessage ;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                        currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj, bundle.getString("SMS_RECEIVED_ACTION"));
                    else{
                        currentMessage = SmsMessage.createFromPdu((byte[]) aPdusObj);
                    }
                    String senderAddress = currentMessage.getDisplayOriginatingAddress();
                    String message = currentMessage.getDisplayMessageBody();

                    if(Constants.LOG)
                        Log.e("", "Received SMS: " + message + ", Sender: " + senderAddress);

                    // if the SMS is not from our gateway, ignore the message
                    if (!senderAddress.toLowerCase().contains(SMS_ORIGIN.toLowerCase())) {
                        return;
                    }

                    // verification code from sms
                    String verificationCode = getVerificationCode(message);

                    if(Constants.LOG)
                        Log.e("", "OTP received: " + verificationCode);

                    Intent otpIntent = new Intent(Constants.OTP_NUMBER_INTENT_FILTER_KEY);
                    otpIntent.putExtra(Constants.OTP_NUMBER_KEY, verificationCode);

                    context.sendBroadcast(otpIntent);

                }
            }
        } catch (Exception e) {
            Log.e("", "Exception: " + e.getMessage());
        }
    }

    /**
     * Getting the OTP from sms message body
     * ':' is the separator of OTP from the message
     *
     * @param message
     * @return
     */
    private String getVerificationCode(String message) {
        String code = null;
        int index = message.indexOf(OTP_DELIMITER);

        if (index != -1) {
            int start = index + 2;
            int length = 6;
            code = message.substring(start, start + length);
            return code;
        }

        return code;
    }
}
