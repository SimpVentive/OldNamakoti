package com.namakoti.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.namakoti.base.MyApplication;
import com.namakoti.beans.GodNamesBean;
import com.namakoti.beans.KarmicOthersBean;
import com.namakoti.beans.KarmicSelfBean;
import com.namakoti.beans.LoginBean;
import com.namakoti.beans.SaveChantsBean;
import com.namakoti.beans.SyncChantsBean;
import com.namakoti.utils.CalendarUtils;
import com.namakoti.utils.Constants;


public class DatabaseHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "Namakoti.db";
    public static String NORMAL_LOCAL_COUNT_TABLE_NAME = "local_count_table";
    public static String NORMAL_SERVER_COUNT_TABLE_NAME = "server_count_table";
    public static String NORMAL_NAMAS_TABLE_NAME = "namas_table";
    public static String NORMAL_GOD_IMAGES_TABLE_NAME = "god_images_table";
    public static String CHANT_FOR_CAUSE_NAMAS_TABLE_NAME = "chant_for_cause_namas_table";
    public static String CHANT_FOR_CAUSE__LOCAL_COUNT_TABLE_NAME = "chant_for_cause_local_count_table";
    public static String CHANT_FOR_CAUSE__SERVER_COUNT_TABLE_NAME = "chant_for_cause_server_count_table";
    public static String KARMIC_SELF_LOCAL_COUNT_TABLE_NAME = "karmic_self_local_count_table";
    public static String KARMIC_SELF_SERVER_COUNT_TABLE_NAME = "karmic_self_server_count_table";
    public static String KARMIC_SELF_NAMAS_TABLE_NAME = "karmic_self_namas_table";
    public static String KARMIC_OTHERS_LOCAL_COUNT_TABLE_NAME = "karmic_others_local_count_table";
    public static String KARMIC_OTHERS_SERVER_COUNT_TABLE_NAME = "karmic_others_server_count_table";
    public static String KARMIC_OTHERS_NAMAS_TABLE_NAME = "karmic_others_namas_table";

    // local_count_table Table Columns
    public final static class TABLE_LOCAL_COUNT {
        public static String COL_USER_ID = "user_id";
        public static String COL_NAMAKOTI_ID = "user_namakoti_id";
        public static String COL_RUNNING_TOTAL = "total_count";
        public static final String COL_NO_TOTAL_COUNT = "no_count";
    }

    public final static class TABLE_SERVER_COUNT {
        public static String COL_USER_ID = "user_id";
        public static String COL_NAMAKOTI_ID = "user_namakoti_id";
        public static String COL_TOTAL_COUNT = "total_count";
        public static String COL_RUNNING_COUNT = "running_count";
        public static final String COL_NO_TOTAL_COUNT = "no_count";
    }

    public final static class TABLE_NAMAS {
        public static final String COL_PRINT_USERNAME = "print_username";
        public static final String COL_MUSIC = "music";
        public static String COL_USER_ID = "user_id";
        public static final String COL_NO_TOTAL_COUNT = "no_count";
        public static final String COL_NAMA_TOTAL_COUNT = "nama_total_count";
        public static final String COL_NAMA_RUNNING_COUNT = "nama_running_count";
        public static final String COL_NAMA_PRINTED_COUNT = "nama_printed_count";
        public static final String COL_PRINTING_SERVICE = "printing_service";
        public static final String COL_THEME_NAME = "theme_name";
        public static final String COL_SUB_THEME_NAME = "sub_theme_name";
        public static final String COL_USER_NAMAKOTI_ID = "user_namakoti_id";
        public static final String COL_USER_THEME_ID = "user_theme_id";
        public static final String COL_USER_SUB_THEME_ID = "user_sub_theme_id";
        public static final String COL_USER_LANGUAGE_ID = "user_language_id";
        public static final String COL_NAMA_END_DATE = "end_date";
        public static final String COL_NAMA_STATUS = "status";
        public static final String COL_GKC_SETUP_ID = "setupId";
    }

    public final static class TABLE_GOD_IMAGES {
        public static String COL_THEME_ID = "theme_id";
        public static String COL_THEME_NAME = "theme_name";
        public static String COL_PRINTING_SERVICE = "printing_service";
        public static String COL_PHOTO = "photo";
    }


    public static int DATABASE_VERSION = 1;
    public static DatabaseHelper instance = null;

    public DatabaseHelper(Context context, String name, CursorFactory factory,
                          int version) {
        super(context, DATABASE_NAME, factory, version);
        // TODO Auto-generated constructor stub
    }

    private static SQLiteDatabase myDataBase;

    private static Context myContext;

    /**
     * Constructor Takes and keeps a reference of the passed context in order to
     * access to the application assets and resources.
     *
     * @param context
     */
    public DatabaseHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;
    }

    public DatabaseHelper() {
        super(MyApplication.getInstance().getApplicationContext(), DATABASE_NAME,
                null, DATABASE_VERSION);
    }

    public static DatabaseHelper getInstance() {
        if (instance != null) {
            return instance;
        } else {
            return new DatabaseHelper();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

        //create a counting table..
        String sql = "CREATE TABLE IF NOT EXISTS " + NORMAL_LOCAL_COUNT_TABLE_NAME + "("
                + TABLE_LOCAL_COUNT.COL_USER_ID + " LONG,"
                + TABLE_LOCAL_COUNT.COL_NAMAKOTI_ID + " TEXT,"
                + TABLE_LOCAL_COUNT.COL_NO_TOTAL_COUNT + " LONG,"
                + TABLE_LOCAL_COUNT.COL_RUNNING_TOTAL + " LONG NOT NULL);";
        db.execSQL(sql);

        sql = "CREATE TABLE IF NOT EXISTS " + NORMAL_SERVER_COUNT_TABLE_NAME + "("
                + TABLE_SERVER_COUNT.COL_USER_ID + " LONG,"
                + TABLE_SERVER_COUNT.COL_NAMAKOTI_ID + " TEXT,"
                + TABLE_SERVER_COUNT.COL_RUNNING_COUNT + " LONG,"
                + TABLE_SERVER_COUNT.COL_NO_TOTAL_COUNT + " LONG,"
                + TABLE_SERVER_COUNT.COL_TOTAL_COUNT + " LONG NOT NULL);";
        db.execSQL(sql);

        sql = "CREATE TABLE IF NOT EXISTS " + NORMAL_NAMAS_TABLE_NAME + " ("
                + TABLE_NAMAS.COL_PRINT_USERNAME + " TEXT, "
                + TABLE_NAMAS.COL_USER_NAMAKOTI_ID + " TEXT,"
                + TABLE_NAMAS.COL_USER_ID + " LONG,"
                + TABLE_NAMAS.COL_NO_TOTAL_COUNT + " LONG,"
                + TABLE_NAMAS.COL_USER_THEME_ID + " TEXT,"
                + TABLE_NAMAS.COL_USER_SUB_THEME_ID + " TEXT,"
                + TABLE_NAMAS.COL_USER_LANGUAGE_ID + " TEXT,"
                + TABLE_NAMAS.COL_MUSIC + " TEXT,"
                + TABLE_NAMAS.COL_NAMA_TOTAL_COUNT + " TEXT,"
                + TABLE_NAMAS.COL_NAMA_RUNNING_COUNT + " TEXT,"
                + TABLE_NAMAS.COL_NAMA_PRINTED_COUNT + " TEXT,"
                + TABLE_NAMAS.COL_PRINTING_SERVICE + " TEXT,"
                + TABLE_NAMAS.COL_THEME_NAME + " TEXT,"
                + TABLE_NAMAS.COL_NAMA_STATUS + " TEXT,"
                + TABLE_NAMAS.COL_NAMA_END_DATE + " TEXT,"
                + TABLE_NAMAS.COL_SUB_THEME_NAME + " TEXT,"
                + TABLE_NAMAS.COL_GKC_SETUP_ID + " TEXT )"
        /*"UNIQUE ( "+TABLE_NAMAS.COL_USER_NAMAKOTI_ID+" ) "+ " )"*/;
        db.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS " + NORMAL_GOD_IMAGES_TABLE_NAME + " ("
                + TABLE_GOD_IMAGES.COL_THEME_ID + " INTEGER,"
                + TABLE_GOD_IMAGES.COL_THEME_NAME + " TEXT,"
                + TABLE_GOD_IMAGES.COL_PRINTING_SERVICE + " INTEGER,"
                + TABLE_GOD_IMAGES.COL_PHOTO + " TEXT );";
        db.execSQL(sql);

        sql = "CREATE TABLE IF NOT EXISTS " + CHANT_FOR_CAUSE_NAMAS_TABLE_NAME + " ("
                + TABLE_NAMAS.COL_PRINT_USERNAME + " TEXT, "
                + TABLE_NAMAS.COL_USER_NAMAKOTI_ID + " TEXT,"
                + TABLE_NAMAS.COL_USER_THEME_ID + " TEXT,"
                + TABLE_NAMAS.COL_USER_ID + " LONG,"
                + TABLE_NAMAS.COL_NO_TOTAL_COUNT + " LONG,"
                + TABLE_NAMAS.COL_USER_SUB_THEME_ID + " TEXT,"
                + TABLE_NAMAS.COL_USER_LANGUAGE_ID + " TEXT,"
                + TABLE_NAMAS.COL_MUSIC + " TEXT,"
                + TABLE_NAMAS.COL_NAMA_TOTAL_COUNT + " TEXT,"
                + TABLE_NAMAS.COL_NAMA_RUNNING_COUNT + " TEXT,"
                + TABLE_NAMAS.COL_NAMA_PRINTED_COUNT + " TEXT,"
                + TABLE_NAMAS.COL_PRINTING_SERVICE + " TEXT,"
                + TABLE_NAMAS.COL_THEME_NAME + " TEXT,"
                + TABLE_NAMAS.COL_NAMA_STATUS + " TEXT,"
                + TABLE_NAMAS.COL_NAMA_END_DATE + " TEXT,"
                + TABLE_NAMAS.COL_GKC_SETUP_ID + " TEXT,"
                + TABLE_NAMAS.COL_SUB_THEME_NAME + " TEXT )";
        db.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS " + CHANT_FOR_CAUSE__LOCAL_COUNT_TABLE_NAME + "("
                + TABLE_LOCAL_COUNT.COL_USER_ID + " LONG,"
                + TABLE_LOCAL_COUNT.COL_NAMAKOTI_ID + " TEXT,"
                + TABLE_LOCAL_COUNT.COL_NO_TOTAL_COUNT + " LONG,"
                + TABLE_LOCAL_COUNT.COL_RUNNING_TOTAL + " LONG NOT NULL);";
        db.execSQL(sql);

        sql = "CREATE TABLE IF NOT EXISTS " + CHANT_FOR_CAUSE__SERVER_COUNT_TABLE_NAME + "("
                + TABLE_SERVER_COUNT.COL_USER_ID + " LONG,"
                + TABLE_SERVER_COUNT.COL_NAMAKOTI_ID + " TEXT,"
                + TABLE_SERVER_COUNT.COL_RUNNING_COUNT + " LONG,"
                + TABLE_SERVER_COUNT.COL_NO_TOTAL_COUNT + " LONG,"
                + TABLE_SERVER_COUNT.COL_TOTAL_COUNT + " LONG NOT NULL);";
        db.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS " + KARMIC_SELF_NAMAS_TABLE_NAME + " ("
                + TABLE_NAMAS.COL_PRINT_USERNAME + " TEXT, "
                + TABLE_NAMAS.COL_USER_NAMAKOTI_ID + " TEXT,"
                + TABLE_NAMAS.COL_USER_THEME_ID + " TEXT,"
                + TABLE_NAMAS.COL_USER_ID + " LONG,"
                + TABLE_NAMAS.COL_NO_TOTAL_COUNT + " LONG,"
                + TABLE_NAMAS.COL_USER_SUB_THEME_ID + " TEXT,"
                + TABLE_NAMAS.COL_USER_LANGUAGE_ID + " TEXT,"
                + TABLE_NAMAS.COL_MUSIC + " TEXT,"
                + TABLE_NAMAS.COL_NAMA_TOTAL_COUNT + " TEXT,"
                + TABLE_NAMAS.COL_NAMA_RUNNING_COUNT + " TEXT,"
                + TABLE_NAMAS.COL_NAMA_PRINTED_COUNT + " TEXT,"
                + TABLE_NAMAS.COL_PRINTING_SERVICE + " TEXT,"
                + TABLE_NAMAS.COL_THEME_NAME + " TEXT,"
                + TABLE_NAMAS.COL_NAMA_STATUS + " TEXT,"
                + TABLE_NAMAS.COL_NAMA_END_DATE + " TEXT,"
                + TABLE_NAMAS.COL_GKC_SETUP_ID + " TEXT,"
                + TABLE_NAMAS.COL_SUB_THEME_NAME + " TEXT )";
        db.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS " + KARMIC_SELF_LOCAL_COUNT_TABLE_NAME + "("
                + TABLE_LOCAL_COUNT.COL_USER_ID + " LONG,"
                + TABLE_LOCAL_COUNT.COL_NAMAKOTI_ID + " TEXT,"
                + TABLE_LOCAL_COUNT.COL_RUNNING_TOTAL + " LONG NOT NULL);";
        db.execSQL(sql);

        sql = "CREATE TABLE IF NOT EXISTS " + KARMIC_SELF_SERVER_COUNT_TABLE_NAME + "("
                + TABLE_SERVER_COUNT.COL_USER_ID + " LONG,"
                + TABLE_SERVER_COUNT.COL_NAMAKOTI_ID + " TEXT,"
                + TABLE_SERVER_COUNT.COL_RUNNING_COUNT + " LONG,"
                + TABLE_SERVER_COUNT.COL_TOTAL_COUNT + " LONG NOT NULL);";
        db.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS " + KARMIC_OTHERS_NAMAS_TABLE_NAME + " ("
                + TABLE_NAMAS.COL_PRINT_USERNAME + " TEXT, "
                + TABLE_NAMAS.COL_USER_NAMAKOTI_ID + " TEXT,"
                + TABLE_NAMAS.COL_USER_THEME_ID + " TEXT,"
                + TABLE_NAMAS.COL_USER_ID + " LONG,"
                + TABLE_NAMAS.COL_NO_TOTAL_COUNT + " LONG,"
                + TABLE_NAMAS.COL_USER_SUB_THEME_ID + " TEXT,"
                + TABLE_NAMAS.COL_USER_LANGUAGE_ID + " TEXT,"
                + TABLE_NAMAS.COL_MUSIC + " TEXT,"
                + TABLE_NAMAS.COL_NAMA_TOTAL_COUNT + " TEXT,"
                + TABLE_NAMAS.COL_NAMA_RUNNING_COUNT + " TEXT,"
                + TABLE_NAMAS.COL_NAMA_PRINTED_COUNT + " TEXT,"
                + TABLE_NAMAS.COL_PRINTING_SERVICE + " TEXT,"
                + TABLE_NAMAS.COL_THEME_NAME + " TEXT,"
                + TABLE_NAMAS.COL_NAMA_STATUS + " TEXT,"
                + TABLE_NAMAS.COL_NAMA_END_DATE + " TEXT,"
                + TABLE_NAMAS.COL_GKC_SETUP_ID + " TEXT,"
                + TABLE_NAMAS.COL_SUB_THEME_NAME + " TEXT )";
        db.execSQL(sql);
        sql = "CREATE TABLE IF NOT EXISTS " + KARMIC_OTHERS_LOCAL_COUNT_TABLE_NAME + "("
                + TABLE_LOCAL_COUNT.COL_USER_ID + " LONG,"
                + TABLE_LOCAL_COUNT.COL_NAMAKOTI_ID + " TEXT,"
                + TABLE_LOCAL_COUNT.COL_RUNNING_TOTAL + " LONG NOT NULL);";
        db.execSQL(sql);

        sql = "CREATE TABLE IF NOT EXISTS " + KARMIC_OTHERS_SERVER_COUNT_TABLE_NAME + "("
                + TABLE_SERVER_COUNT.COL_USER_ID + " LONG,"
                + TABLE_SERVER_COUNT.COL_NAMAKOTI_ID + " TEXT,"
                + TABLE_SERVER_COUNT.COL_RUNNING_COUNT + " LONG,"
                + TABLE_SERVER_COUNT.COL_TOTAL_COUNT + " LONG NOT NULL);";
        db.execSQL(sql);

    }

    public ContentValues localChantCountQuery(long userId, String namakotiId, long runningTotal, String type, long no_count) {
        ContentValues cv = new ContentValues();
        cv.put(TABLE_LOCAL_COUNT.COL_USER_ID, userId);
        cv.put(TABLE_LOCAL_COUNT.COL_NAMAKOTI_ID, namakotiId);
        if (type.equalsIgnoreCase(Constants.KEY_SELF_CHANT) || type.equalsIgnoreCase(Constants.KEY_CHANT_FOR_CAUSE))
            cv.put(TABLE_LOCAL_COUNT.COL_NO_TOTAL_COUNT, no_count);
        cv.put(TABLE_LOCAL_COUNT.COL_RUNNING_TOTAL, runningTotal);
        return cv;
    }

    public ContentValues serverChantCountQuery(long userId, String namakotiId, long total, long running, String type, long no_count) {
        ContentValues cv = new ContentValues();
        cv.put(TABLE_SERVER_COUNT.COL_USER_ID, userId);
        cv.put(TABLE_SERVER_COUNT.COL_NAMAKOTI_ID, namakotiId);
        cv.put(TABLE_SERVER_COUNT.COL_RUNNING_COUNT, running);
        if (type.equalsIgnoreCase(Constants.KEY_SELF_CHANT) || type.equalsIgnoreCase(Constants.KEY_CHANT_FOR_CAUSE))
            cv.put(TABLE_SERVER_COUNT.COL_NO_TOTAL_COUNT, no_count);
        cv.put(TABLE_SERVER_COUNT.COL_TOTAL_COUNT, total);
        return cv;
    }

    public ContentValues getNamasQuery(LoginBean.NamasDetails namasDetails, long userid) {
        ContentValues cv = new ContentValues();
        cv.put(TABLE_NAMAS.COL_USER_ID, userid);
        cv.put(TABLE_NAMAS.COL_PRINT_USERNAME, namasDetails.getPrint_username());
        cv.put(TABLE_NAMAS.COL_MUSIC, namasDetails.getMusic());
        cv.put(TABLE_NAMAS.COL_NO_TOTAL_COUNT, namasDetails.getNo_chants());
        cv.put(TABLE_NAMAS.COL_NAMA_TOTAL_COUNT, namasDetails.getNama_total_count());
        cv.put(TABLE_NAMAS.COL_NAMA_RUNNING_COUNT, namasDetails.getNama_running_count());
        cv.put(TABLE_NAMAS.COL_NAMA_PRINTED_COUNT, namasDetails.getNama_printed_count());
        cv.put(TABLE_NAMAS.COL_PRINTING_SERVICE, namasDetails.getPrinting_service());
        cv.put(TABLE_NAMAS.COL_THEME_NAME, namasDetails.getTheme_name());
        cv.put(TABLE_NAMAS.COL_SUB_THEME_NAME, namasDetails.getSub_theme_name());
        cv.put(TABLE_NAMAS.COL_USER_NAMAKOTI_ID, namasDetails.getUser_namakoti_id());
        cv.put(TABLE_NAMAS.COL_USER_THEME_ID, namasDetails.getUser_theme_id());
        cv.put(TABLE_NAMAS.COL_USER_SUB_THEME_ID, namasDetails.getUser_sub_theme_id());
        cv.put(TABLE_NAMAS.COL_USER_LANGUAGE_ID, namasDetails.getUser_language_id());
        cv.put(TABLE_NAMAS.COL_NAMA_STATUS, "");
        cv.put(TABLE_NAMAS.COL_NAMA_END_DATE, "");
        cv.put(TABLE_NAMAS.COL_GKC_SETUP_ID, "");

        return cv;
    }

    public ContentValues getSelfNamasQuery(KarmicSelfBean.SelfGkcDetails namasDetails, long userid) {
        ContentValues cv = new ContentValues();
        cv.put(TABLE_NAMAS.COL_USER_ID, userid);
        cv.put(TABLE_NAMAS.COL_PRINT_USERNAME, "");
        cv.put(TABLE_NAMAS.COL_MUSIC, namasDetails.music);
        cv.put(TABLE_NAMAS.COL_NO_TOTAL_COUNT, "");
        cv.put(TABLE_NAMAS.COL_NAMA_TOTAL_COUNT, namasDetails.chants_count_will_do);
        cv.put(TABLE_NAMAS.COL_NAMA_RUNNING_COUNT, namasDetails.orginal_chants_count);
        cv.put(TABLE_NAMAS.COL_NAMA_PRINTED_COUNT, 0);
        cv.put(TABLE_NAMAS.COL_PRINTING_SERVICE, namasDetails.printing_service);
        cv.put(TABLE_NAMAS.COL_THEME_NAME, namasDetails.theme_name);
        cv.put(TABLE_NAMAS.COL_SUB_THEME_NAME, namasDetails.sub_theme_name);
        cv.put(TABLE_NAMAS.COL_USER_NAMAKOTI_ID, namasDetails.participant_id);
        cv.put(TABLE_NAMAS.COL_USER_THEME_ID, namasDetails.theme_id);
        cv.put(TABLE_NAMAS.COL_USER_SUB_THEME_ID, namasDetails.sub_theme_id);
        cv.put(TABLE_NAMAS.COL_USER_LANGUAGE_ID, namasDetails.language_id);
        cv.put(TABLE_NAMAS.COL_NAMA_STATUS, "");
        cv.put(TABLE_NAMAS.COL_GKC_SETUP_ID, namasDetails.gkc_setup_id);
        cv.put(TABLE_NAMAS.COL_NAMA_END_DATE, namasDetails.end_datetime);

        return cv;
    }

    public ContentValues getHelpingOthersCV(KarmicOthersBean.RequestGkcDetails namasDetails, long userid) {
        ContentValues cv = new ContentValues();
        cv.put(TABLE_NAMAS.COL_USER_ID, userid);
        cv.put(TABLE_NAMAS.COL_PRINT_USERNAME, "");
        cv.put(TABLE_NAMAS.COL_MUSIC, namasDetails.music);
        cv.put(TABLE_NAMAS.COL_NO_TOTAL_COUNT, "");
        cv.put(TABLE_NAMAS.COL_NAMA_TOTAL_COUNT, namasDetails.chants_count_will_do);
        cv.put(TABLE_NAMAS.COL_NAMA_RUNNING_COUNT, namasDetails.orginal_chants_count);
        cv.put(TABLE_NAMAS.COL_NAMA_PRINTED_COUNT, 0);
        cv.put(TABLE_NAMAS.COL_PRINTING_SERVICE, namasDetails.printing_service);
        cv.put(TABLE_NAMAS.COL_THEME_NAME, namasDetails.theme_name);
        cv.put(TABLE_NAMAS.COL_SUB_THEME_NAME, namasDetails.sub_theme_name);
        cv.put(TABLE_NAMAS.COL_USER_NAMAKOTI_ID, namasDetails.participant_id);
        cv.put(TABLE_NAMAS.COL_USER_THEME_ID, namasDetails.theme_id);
        cv.put(TABLE_NAMAS.COL_USER_SUB_THEME_ID, namasDetails.sub_theme_id);
        cv.put(TABLE_NAMAS.COL_USER_LANGUAGE_ID, namasDetails.language_id);
        cv.put(TABLE_NAMAS.COL_NAMA_STATUS, namasDetails.status);
        cv.put(TABLE_NAMAS.COL_GKC_SETUP_ID, namasDetails.gkc_setup_id);
        cv.put(TABLE_NAMAS.COL_NAMA_END_DATE, namasDetails.end_datetime);
        return cv;
    }

    public ContentValues getSavedNamasQuery(SaveChantsBean namasDetails, long userid) {
        ContentValues cv = getNamasContentValues(userid, namasDetails.getPrint_username(), namasDetails.getMusic(), namasDetails.getNo_chants(), namasDetails.getNama_total_count(),
                namasDetails.getNama_running_count(), namasDetails.getNama_printed_count(), namasDetails.getPrinting_service(), namasDetails.getTheme_name(), namasDetails.getSub_theme_name(),
                namasDetails.getUser_namakoti_id(), namasDetails.getUser_theme_id(), namasDetails.getUser_sub_theme_id(), namasDetails.getUser_language_id());
        return cv;
    }

    public ContentValues getNormalSyncNamasContentValues(SyncChantsBean.SaveNamas namasDetails, long userid) {
        ContentValues cv = getNamasContentValues(userid, namasDetails.print_username, namasDetails.music, namasDetails.no_chants, namasDetails.nama_total_count,
                namasDetails.nama_running_count, namasDetails.nama_printed_count, namasDetails.printing_service, namasDetails.theme_name, namasDetails.sub_theme_name,
                namasDetails.user_namakoti_id, namasDetails.user_theme_id, namasDetails.user_sub_theme_id, namasDetails.user_language_id);
        return cv;
    }

    public Cursor updateKarmicSyncNamasQuery(SQLiteDatabase mDatabase, String mFromScreen, long total, long running, long userid, int namakotiID) {
        String name = "";
        if (mFromScreen.equalsIgnoreCase(Constants.KEY_KARMIC_BEING)) {
            name = DatabaseHelper.KARMIC_SELF_NAMAS_TABLE_NAME;
        } else if (mFromScreen.equalsIgnoreCase(Constants.KEY_KARMIC_OTHERS)) {
            name = DatabaseHelper.KARMIC_OTHERS_NAMAS_TABLE_NAME;
        }
        String namasQuery = "UPDATE " + name + " SET "
                + TABLE_NAMAS.COL_NAMA_TOTAL_COUNT + " = " + "" + total + " and "
                + TABLE_NAMAS.COL_NAMA_RUNNING_COUNT + " = " + "" + running
                + " WHERE " + TABLE_NAMAS.COL_USER_NAMAKOTI_ID + "=" + namakotiID
                + " AND " + TABLE_NAMAS.COL_USER_ID + "=" + userid;
        Cursor cursor = mDatabase.rawQuery(namasQuery, null);

        return cursor;
    }


    public Cursor updateSyncServerQuery(SQLiteDatabase mDatabase, String mFromScreen, int total, int running, String userid, int namakotiID) {
        String name = "";
        if (mFromScreen.equalsIgnoreCase(Constants.KEY_KARMIC_BEING)) {
            name = DatabaseHelper.KARMIC_SELF_SERVER_COUNT_TABLE_NAME;
        } else if (mFromScreen.equalsIgnoreCase(Constants.KEY_KARMIC_OTHERS)) {
            name = DatabaseHelper.KARMIC_OTHERS_SERVER_COUNT_TABLE_NAME;
        }

        String serverQuery = "UPDATE " + name + " SET "
                + TABLE_SERVER_COUNT.COL_TOTAL_COUNT + " = " + "" + total + " and "
                + TABLE_SERVER_COUNT.COL_RUNNING_COUNT + " = " + "" + running
                + " WHERE " + TABLE_SERVER_COUNT.COL_NAMAKOTI_ID + "=" + namakotiID
                + " AND " + TABLE_SERVER_COUNT.COL_USER_ID + "=" + userid;
        Cursor cursor = mDatabase.rawQuery(serverQuery, null);

        return cursor;
    }

    public Cursor getLocalTotalCount(SQLiteDatabase mDatabase, long userid, String namaotiid) {
        String sqlQuery = "SELECT " + TABLE_LOCAL_COUNT.COL_RUNNING_TOTAL + " FROM "
                + KARMIC_SELF_LOCAL_COUNT_TABLE_NAME + " "
                + " WHERE " + TABLE_LOCAL_COUNT.COL_NAMAKOTI_ID + "=" + namaotiid
                + " AND " + TABLE_LOCAL_COUNT.COL_USER_ID + "=" + userid;
        Cursor cursor = mDatabase.rawQuery(sqlQuery, null);
        return cursor;
    }


    public String getSetupId(SQLiteDatabase database, int user_namakoti_id, String fromScreen, long userID) {
        String name = "";
        if (fromScreen.equalsIgnoreCase(Constants.KEY_KARMIC_BEING)) {
            name = DatabaseHelper.KARMIC_SELF_NAMAS_TABLE_NAME;
        } else if (fromScreen.equalsIgnoreCase(Constants.KEY_KARMIC_OTHERS)) {
            name = DatabaseHelper.KARMIC_OTHERS_NAMAS_TABLE_NAME;
        }

        String sqlQuery = "SELECT " + TABLE_NAMAS.COL_GKC_SETUP_ID + " FROM "
                + name + " " + " WHERE " + TABLE_NAMAS.COL_USER_NAMAKOTI_ID + "=" + user_namakoti_id
                + " AND " + TABLE_NAMAS.COL_USER_ID + "=" + userID;;
        Cursor cursor = database.rawQuery(sqlQuery, null);
        String count = "";
        if (cursor != null) {
            cursor.moveToFirst();
            count = cursor.getString(cursor.getColumnIndex(TABLE_NAMAS.COL_GKC_SETUP_ID));
        }
        return count;
    }

    private ContentValues getNamasContentValues(long userid, String print_username, String music, long no_count, long nama_total_count, long nama_running_count, long nama_printed_count, String printing_service, String theme_name, String sub_theme_name, int user_namakoti_id, String user_theme_id, String user_sub_theme_id, String user_language_id) {
        ContentValues cv = new ContentValues();
        cv.put(TABLE_NAMAS.COL_USER_ID, userid);
        cv.put(TABLE_NAMAS.COL_PRINT_USERNAME, print_username);
        cv.put(TABLE_NAMAS.COL_MUSIC, music);
        cv.put(TABLE_NAMAS.COL_NO_TOTAL_COUNT, no_count);
        cv.put(TABLE_NAMAS.COL_NAMA_TOTAL_COUNT, nama_total_count);
        cv.put(TABLE_NAMAS.COL_NAMA_RUNNING_COUNT, nama_running_count);
        cv.put(TABLE_NAMAS.COL_NAMA_PRINTED_COUNT, nama_printed_count);
        cv.put(TABLE_NAMAS.COL_PRINTING_SERVICE, printing_service);
        cv.put(TABLE_NAMAS.COL_THEME_NAME, theme_name);
        cv.put(TABLE_NAMAS.COL_SUB_THEME_NAME, sub_theme_name);
        cv.put(TABLE_NAMAS.COL_USER_NAMAKOTI_ID, user_namakoti_id);
        cv.put(TABLE_NAMAS.COL_USER_THEME_ID, user_theme_id);
        cv.put(TABLE_NAMAS.COL_USER_SUB_THEME_ID, user_sub_theme_id);
        cv.put(TABLE_NAMAS.COL_USER_LANGUAGE_ID, user_language_id);
        return cv;
    }

    public ContentValues getInsertGodQuery(GodNamesBean godBean) {
        ContentValues cv = new ContentValues();
        cv.put(TABLE_GOD_IMAGES.COL_THEME_ID, Integer.parseInt(godBean.getTheme_id()));
        cv.put(TABLE_GOD_IMAGES.COL_THEME_NAME, godBean.getTheme_name());
        cv.put(TABLE_GOD_IMAGES.COL_PRINTING_SERVICE, Integer.parseInt(godBean.getPrinting_service()));
        cv.put(TABLE_GOD_IMAGES.COL_PHOTO, godBean.getPhoto());

        return cv;
    }

    public String getInsertQuery(LoginBean.NamasDetails namasDetails) {

        String sql = "INSERT OR IGNORE INTO " + DatabaseHelper.NORMAL_NAMAS_TABLE_NAME + " ( "
                + TABLE_NAMAS.COL_PRINT_USERNAME + "," +
                TABLE_NAMAS.COL_MUSIC + "," + TABLE_NAMAS.COL_NAMA_TOTAL_COUNT + "," +
                TABLE_NAMAS.COL_NAMA_RUNNING_COUNT + "," + TABLE_NAMAS.COL_NAMA_PRINTED_COUNT + "," +
                TABLE_NAMAS.COL_PRINTING_SERVICE + "," + TABLE_NAMAS.COL_THEME_NAME + "," +
                TABLE_NAMAS.COL_SUB_THEME_NAME + "," + TABLE_NAMAS.COL_USER_NAMAKOTI_ID + "," +
                TABLE_NAMAS.COL_USER_THEME_ID + "," + TABLE_NAMAS.COL_USER_SUB_THEME_ID + ","
                + TABLE_NAMAS.COL_USER_LANGUAGE_ID +
                " ) VALUES ( " + namasDetails.getPrint_username() + "," + namasDetails.getMusic()
                + "," + namasDetails.getNama_total_count() + "," + namasDetails.getNama_running_count()
                + "," + namasDetails.getNama_printed_count() + "," + namasDetails.getPrinting_service()
                + "," + namasDetails.getTheme_name() + "," + namasDetails.getSub_theme_name()
                + "," + namasDetails.getUser_namakoti_id() + "," + namasDetails.getUser_theme_id()
                + "," + namasDetails.getUser_sub_theme_id() + "," + namasDetails.getUser_language_id() + " );";
        return sql;
    }

    public static Cursor getGodImages(SQLiteDatabase mDatabase) {
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + DatabaseHelper.NORMAL_GOD_IMAGES_TABLE_NAME, null);
        return cursor;
    }

    public static Cursor getKarmicOthersNamasCursor(SQLiteDatabase mDatabase, long mUserID, String participant_id, String karmicOthersNamasTableName) {
        String sqlQuery = "select " + " * from " + karmicOthersNamasTableName + " where "
                + TABLE_NAMAS.COL_USER_ID + " = " + mUserID + " and "
                + TABLE_NAMAS.COL_USER_NAMAKOTI_ID+ "=" + participant_id;
        Cursor cursor = mDatabase.rawQuery(sqlQuery, null);

//		Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + karmicOthersNamasTableName
//				+ " WHERE " + DatabaseHelper.TABLE_NAMAS.COL_USER_ID + " = " + mUserIdString
//				+ " AND " + TABLE_NAMAS.COL_USER_NAMAKOTI_ID+ " = " + participant_id, null);
        return cursor;

    }


    /*public static Cursor getTotalCount(String typeOfClass, String userId) {
        String name = "";
        SQLiteDatabase database = MyApplication.getInstance().getDataDatabase();
        if (typeOfClass.equalsIgnoreCase(Constants.KEY_SELF_CHANT)) {
            name = DatabaseHelper.NORMAL_NAMAS_TABLE_NAME;
        } else if (typeOfClass.equalsIgnoreCase(Constants.KEY_CHANT_FOR_CAUSE)) {
            name = DatabaseHelper.CHANT_FOR_CAUSE_NAMAS_TABLE_NAME;
        } else if (typeOfClass.equalsIgnoreCase(Constants.KEY_KARMIC_BEING)) {
            name = DatabaseHelper.KARMIC_SELF_NAMAS_TABLE_NAME;
        } else if (typeOfClass.equalsIgnoreCase(Constants.KEY_KARMIC_OTHERS)) {
            name = DatabaseHelper.KARMIC_OTHERS_NAMAS_TABLE_NAME;
        }
        String sqlQuery = "select " + " * from " + name + " where "
                + DatabaseHelper.TABLE_NAMAS.COL_USER_ID + " = " + userId;
        Cursor cursor = database.rawQuery(sqlQuery, null);

        return cursor;
    }*/

    public static Cursor getNamasListCursor(SQLiteDatabase mDatabase, long mUserID, String tableName) {
        Cursor cursor = mDatabase.rawQuery("SELECT * FROM " + tableName
                + " WHERE " + DatabaseHelper.TABLE_NAMAS.COL_USER_ID + " = " + mUserID, null);
        return cursor;
    }

    public static Cursor getNamasListCursorForGod(SQLiteDatabase mDatabase, long mUserID, String tableName, String fromScreen) {
        Cursor cursor = null;
        String now = CalendarUtils.getTodayString(Constants.DATE_TIME_FORMAT);
        if (fromScreen.equalsIgnoreCase(Constants.KEY_CHANT_FOR_CAUSE) ||
                fromScreen.equalsIgnoreCase(Constants.KEY_SELF_CHANT)) {
            cursor = mDatabase.rawQuery("SELECT * FROM " + tableName
                    + " WHERE " + DatabaseHelper.TABLE_NAMAS.COL_USER_ID + " = " + mUserID, null);
        } else if (fromScreen.equalsIgnoreCase(Constants.KEY_KARMIC_BEING)) {
            cursor = mDatabase.rawQuery("SELECT * FROM " + tableName
                    + " WHERE " + DatabaseHelper.TABLE_NAMAS.COL_USER_ID + " = " + mUserID +
                    " AND " + TABLE_NAMAS.COL_NAMA_END_DATE + " >= \'" + now + "\'", null);
        } else if (fromScreen.equalsIgnoreCase(Constants.KEY_KARMIC_OTHERS)) {
            cursor = mDatabase.rawQuery("SELECT * FROM " + tableName
                    + " WHERE " + TABLE_NAMAS.COL_NAMA_END_DATE + " >= \'" + now + "\'"
                    + " AND ( " + TABLE_NAMAS.COL_NAMA_STATUS + " >= \'" + Constants.GKCA + "\'" +
                    " OR " + TABLE_NAMAS.COL_NAMA_STATUS + " >= \'" + Constants.GKCP + "\'" +
                    " OR " + TABLE_NAMAS.COL_NAMA_STATUS + " >= \'" + Constants.GKCD + "\'" +
                    " OR " + TABLE_NAMAS.COL_USER_ID + " = " + mUserID +" )", null);
        }
        return cursor;
    }

    public static long getSavedLocalCount(SQLiteDatabase mDatabase, int namakotiId, String localCountTableName, long userId) {
        String sqlQuery = "select " + DatabaseHelper.TABLE_LOCAL_COUNT.COL_RUNNING_TOTAL + " from " + localCountTableName + " where "
                + TABLE_LOCAL_COUNT.COL_NAMAKOTI_ID + "=" + namakotiId + " AND "
                + TABLE_LOCAL_COUNT.COL_USER_ID + "=" + userId;
        Cursor countCursor = mDatabase.rawQuery(sqlQuery, null);
        countCursor.moveToFirst();

        long count = countCursor.getLong(countCursor
                .getColumnIndex(DatabaseHelper.TABLE_LOCAL_COUNT.COL_RUNNING_TOTAL));
        return count;
    }

    public static Cursor getSavedServerCountCursor(SQLiteDatabase mDatabase, String serverCountTableName, int namakotiId, long mUserID) {
        String sqlQuery = "select * from " + serverCountTableName + " where "
                + TABLE_SERVER_COUNT.COL_NAMAKOTI_ID + "=" + namakotiId +
                " AND " + TABLE_SERVER_COUNT.COL_USER_ID + "=" + mUserID;
        Cursor countCursor = mDatabase.rawQuery(sqlQuery, null);
        return countCursor;
    }

    public SQLiteDatabase open() {
        try {
            myDataBase = this.getWritableDatabase();

        } catch (SQLException ex) {

        }
        return myDataBase;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

}
