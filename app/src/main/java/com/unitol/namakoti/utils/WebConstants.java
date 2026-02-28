package com.unitol.namakoti.utils;

public class WebConstants {

    /*Base URL*/
//	public static final String BASE_URL = "http://118.67.249.179/namakoti/webservice";
//	public static final String BASE_URL = "http://183.82.101.133/namakoti/webservice";
//	public static final String BASE_URL = "http://www.namakoti.com/webservice";
    public static final String BASE_URL = com.unitol.namakoti.BuildConfig.BASE_URL;
    public static final String UPGRADE_URL = BASE_URL + "/mobileupgrade";
    public static final String PRINT_CHANT_URL = BASE_URL + "/printchant";
    public static final String GOD_IMAGE_BASE_URL = BASE_URL.replace("/webservice","") + "/";
    /*Methods */
    public static final String INSET_USER = BASE_URL + "/insertuser";
    public static final String SIGN_IN = BASE_URL + "/login";
    public static final String SIGN_IN_NEW = BASE_URL + "/newlogin";
    public static final String CHECK_VERIFY_SMS = BASE_URL + "/verifysms";
    public static final String CHECK_USER = BASE_URL + "/checkuser";
    public static final String CHECK_PHONE = BASE_URL + "/checkphone";
    public static final String FORGOT_PASSWORD = BASE_URL + "/forgotpassword";
    public static final String PROFILE_DETAILS = BASE_URL + "/profiledetails";
    public static final String EDIT_PROFILE_DETAILS = BASE_URL + "/editdetails";
    public static final String UPDATE_PROFILE = BASE_URL + "/updateuser";
    public static final String GODS = BASE_URL + "/gods";
    public static final String UPLOAD_IMAGE = BASE_URL + "/uploadimage";
    public static final String LANGUAGES = BASE_URL + "/languages";
    public static final String CHANTS = BASE_URL + "/chants";
    public static final String CREATE_NAMA = BASE_URL + "/createnama";//
    public static final String SYNC_CHANT = BASE_URL + "/syncchants";
    public static final String CHANGE_PASSWORD = BASE_URL + "/changepassword";
    public static final String UPDRATE = BASE_URL + "/upgrade";
    public static final String DISPATCHS = BASE_URL + "/dispatchs";
    public static final String TEMPLE_PRINTS = BASE_URL + "/templeprints";
    public static final String TRANSACTIONS = BASE_URL + "/transactions";
    public static final String COUNTRY_ISD_CODE = BASE_URL + "/isdcodedetails";
    public static final String COUNTRY_CODES = BASE_URL + "/countryisd";
    public static final String PRINT_LIST = BASE_URL + "/printlist";
    public static final String DELETE_ACCOUNT = BASE_URL + "/deleteuser";
    public static final int LOGGED_IN = 1;

}
