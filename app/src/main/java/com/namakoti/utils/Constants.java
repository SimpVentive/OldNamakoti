package com.namakoti.utils;

/**
 * Created by anusha on 12/5/2017.
 */

public class Constants {

    public static final String SHARED_PREFERENCES_NAME = "NAMAKOTI";
    public static final String BASE_PATH = "https://testing.namakoti.com";//"https://namakoti.com";
    public static final String BASE_URL = BASE_PATH + "/webservice/";
    public static final String LOGIN_URL = BASE_URL + "login";
    public static final String REGISTER_URL = BASE_URL + "insertuser";
    public static final String CHECK_USERNAME_URL = BASE_URL + "checkuser";//not working
    public static final String CHECKPHONE_URL = BASE_URL + "checkphone";//not working
    public static final String GET_PROFILE_DETAILS = BASE_URL + "profiledetails";
    public static final String EDIT_PROFILE_DETAILS = BASE_URL + "editdetails";
    public static final String UPDATE_PROFILE_DETAILS = BASE_URL + "updateuser";
    public static final String GET_GOD_NAMES = BASE_URL + "gods";
    public static final String GET_LAUNGUAGES = BASE_URL + "languages";
    public static final String GET_CHANTS_LIST = BASE_URL + "chants";
    public static final String SAVE_CHANT = BASE_URL + "createnama";
    public static final String SYNC_CHANT = BASE_URL + "syncchants";
    public static final String VERIFY_SMS = BASE_URL + "verifysms";
    public static final String TRANSACTIONS = BASE_URL + "transactions";
    public static final String PAYMENT_JSON = "PAYMENT_JSON";
    public static final String CAUSE_OPTIONS = BASE_URL + "causes";
    public static final String CAUSE_GODS_NAMES = BASE_URL + "causegods";
    public static final String CAUSE_CHANT_LIST = BASE_URL + "causechants";
    public static final String CAUSE_SAVE_CHANT_LIST = BASE_URL + "createcausenama";
    public static final String CAUSE_SYNC_CHANT = BASE_URL + "syncchants";

    public static final String GOD_IMAGE_BASE_URL = BASE_PATH + "/";
    public static final String TEMPLE_PRINTS_URL = BASE_URL + "templeprints?user_id=%1$s&theme_id=%2$s";

    //karmic
    public static final String KARMIC_SELF_URL = BASE_URL + "gkcself";
    public static final String GET_KARMIC_CAUSE_FOR_URL = BASE_URL + "causefor";
    public static final String GET_PARTICIPANTS_URL = BASE_URL + "gkcparticipants";
    public static final String ADD_PARTICIPANTS_URL = BASE_URL + "addgkcparticipants";
    public static final String KARMIC_OTHERS_URL = BASE_URL + "gkcrequest?username=%1$s&page=%2$s";
    public static final String KARMIC_REQUESTSTATUS_URL = BASE_URL + "requeststatus";
    public static final String CREATE_KARMIC_CHANT_URL = BASE_URL + "creategkc";
    public static final String CANCEL_REQUEST_URL = BASE_URL + "cancelrequest";
    public static final String SEND_REMINDER_URL = BASE_URL + "sendreminder";
    public static final String GKC_REQUEST_SUBMIT_URL = BASE_URL + "gkcrequestsubmit";
    public static final String PUSH_TOKEN_URL = BASE_URL + "updatetoken";

    public static final String GET_KARMIC_DETAILS_URL = BASE_URL + "getkarmicdetails";
    public static final String PANGCHANG_URL = BASE_URL + "pangchang";
    public static final String KARMIC_SYNC_URL = BASE_URL + "karmicsync";
    public static final String UPLOAD_IMAGE = BASE_URL + "/uploadimage";
    public static final String PRINT_CHANT_URL = BASE_URL + "/printchant";


    public static final boolean LOG = true;
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String SELF_DATE_FORMAT = "dd MMM";
    public static final String SELF_TIME_FORMAT = "EEEE hh:mm a";
    public static final String TIME_FORMAT = "HH:mm:ss";
    public static final String FCM_DEVICE_TOKEN_KEY = "FCM_DEVICE_TOKEN_KEY";
    public static final String PUSH_NOTIFICATION_ACTION = "com.from.notification";
    public static final String PUSH_MESSAGE_KEY = "message";
    public static final String PUSH_TITLE_KEY = "title";
    public static String DATE_FORMAT = "yyyy-MM-dd";

    public static final String PARTICNTS_LIST_KEY = "PARTICNTS_LIST_KEY";
    public static final String PARTICNTS_GKC_SETUP_KEY = "PARTICNTS_GKC_SETUP_KEY";
    public static final String ADD_PARTICIPANT_INTENT_ACTION = "ADD_RECEIVE_ACTION";
    public static final String CREATE_KARMIC_CHANT_INTENT_ACTION = "CREATE_KARMIC_CHANT_INTENT_ACTION";
    public static final String UPDATE_CHANT_COUNT_INTENT_ACTION = "UPDATE_CHANT_COUNT_INTENT_ACTION";
    public static final String INTENT_FILE_DOWNLOADED_ACTION = "FILE_DOWNLOADED_ACTION";
    public static boolean IS_EMPTY_EMAIL = false;

    public static boolean POSTMARTEMREPORTLOG = true;
    public static String CURRENT_USER_PHONE_NUMBER;

    public static final String LORD_HANUMAN = "Lord Hanuman";
    public static final String LORD_RAMA = "Lord Rama";
    public static final String LORD_SHIVA = "Lord Shiva";
    public static final String LORD_SAIBABA = "Lord Sai Baba";
    public static final String LORD_DURGA = "Goddess Durga";
    public static final String LORD_GANESH = "Lord Ganesh";
    public static final String LORD_LAKSHMI = "Goddess Lakshmi";
    public static final String LORD_VENKATESHWARA = "Lord Venkateshwara";
    public static final String LORD_AYYAPPA = "Lord Ayyappa";


    /*FireBase Starting*/
    public static final String TOPIC_GLOBAL = "global";
    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";
    public static final String SHARED_PREF = "ah_firebase";
    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;
    /*FireBase Ending*/

    public static final String I_ACCEPT = "GKCA";
    public static final String I_DO_CHANT = "GKCP";
    public static final String I_WILL_NOT_HELP = "GKCD";
    public static final String I_WILL_NOT_KNOW_OF = "GKCDP";
    public static final String GKCA = "GKCA";
    public static final String GKCP = "GKCP";
    public static final String GKCD = "GKCD";
    public static final String GKCDP = "GKCDP";

    public static final String KEY_KARMIC_OTHERS = "helpingOthers";
    public static final String KEY_KARMIC_BEING = "beingHelped";
    public static final String KEY_SELF_CHANT = "self";
    public static final String KEY_CHANT_FOR_CAUSE = "chantForCause";

    public static final int REQUEST_GALLERY_PERMISSION = 1;
    public static final int REQUEST_CAMERA_PERMISSION = 2;
    public static final int REQUEST_MULTIPLE_PERMISSION = 3;
    public static final int REQUEST_CONTACT_PERMISSION = 4;

    public static final String OTP_NUMBER_INTENT_FILTER_KEY = "com.namakoti.otp.OTP_ACTION";
    public static final String OTP_NUMBER_KEY = "OTP_NUMBER_KEY";
    public static String[] sChantType = {"Self", "Chant for Cause"};
    public static final String WEBVIEW_URL = "WEBVIEW_URL";
    public static final String WEBVIEW_HDR_IMG = "WEBVIEW_HDR_IMG";
    public static final String WEBVIEW_SUCCES_URL_4_UPGRADE = "pay_success";
    public static final String WEBVIEW_SUCCES_URL = "pay_mobileprintsuccess";
    public static final String UPGRADED_SUCCESFUL = "UPGRADED_SUCCESFUL";
    public static final String WEBVIEW_FAILURE_URL = "pay_failure";
}
