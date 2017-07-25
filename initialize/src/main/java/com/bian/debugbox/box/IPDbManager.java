package com.bian.debugbox.box;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static com.bian.debugbox.box.InitializeUtil.LOG_TAG;

/**
 * author 边凌
 * date 2017/4/1 14:31
 * desc ${TODO}
 */

class IPDbManager {
    private final static String COLUMN_ID = "id";
    private final static String COLUMN_HOST = "host";
    private final static String COLUMN_PORT = "port";
    private final static String COLUMN_SELECTED = "selected";
    private final static String COLUMN_CREATED_DATE = "createDate";
    private final static String COLUMN_KEY = "key";
    private static IPDbManager sInstance;
    private IPDbHelper ipDbHelper;

    private IPDbManager(Context context) {
        ipDbHelper = new IPDbHelper(context);
    }

    static IPDbManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (IPDbManager.class) {
                if (sInstance == null) {
                    sInstance = new IPDbManager(context);
                }
            }
        }
        return sInstance;
    }

    void updateIp(IPEntity ipEntity) {
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_HOST, ipEntity.getHost());
        cv.put(COLUMN_PORT, ipEntity.getPort());
        cv.put(COLUMN_KEY, ipEntity.getKey());
        cv.put(COLUMN_SELECTED, ipEntity.isSelected() ? 1 : 0);
        cv.put(COLUMN_CREATED_DATE, ipEntity.getCreateDate());
        Long id = ipEntity.getId();
        String whereClause = "id=?";
        String[] whereArgs = {String.valueOf(id)};
        SQLiteDatabase sqLiteDatabase;
        sqLiteDatabase = ipDbHelper.getWritableDatabase();
        try {
            sqLiteDatabase.update(IPDbHelper.TABLE_NAME, cv, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sqLiteDatabase != null) {
                sqLiteDatabase.close();
            }
        }
    }

    void insertIP(String clientName, String host, String port) {
        if (TextUtils.isEmpty(host)) {
            return;
        }
        Log.d(LOG_TAG, "insertIp:" + host + ":" + port);
        IPEntity ipEntity1 = queryIp(host, port);
        if (ipEntity1 == null) {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_HOST, host);
            cv.put(COLUMN_PORT, port);
            cv.put(COLUMN_SELECTED, 1);
            cv.put(COLUMN_KEY, clientName);
            cv.put(COLUMN_CREATED_DATE, System.currentTimeMillis());
            SQLiteDatabase sqLiteDatabase = null;
            try {
                sqLiteDatabase = ipDbHelper.getWritableDatabase();
                long insert = sqLiteDatabase.insert(IPDbHelper.TABLE_NAME, null, cv);
                if (insert == 1) {
                    sqLiteDatabase.insert(IPDbHelper.TABLE_NAME, null, cv);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (sqLiteDatabase != null) {
                    sqLiteDatabase.close();
                }
            }
        } else {
            updateIp(ipEntity1);
        }

    }

    private IPEntity queryIp(String host, String port) {
        String whereClause = COLUMN_HOST + "=? and " + COLUMN_PORT + "=?";
        String[] selectionArgs = {host, port};
        SQLiteDatabase sqLiteDatabase = null;
        try {
            sqLiteDatabase = ipDbHelper.getWritableDatabase();
            Cursor query = sqLiteDatabase.query(IPDbHelper.TABLE_NAME, null, whereClause, selectionArgs, null, null, null, null);
            List<IPEntity> ipEntities = new ArrayList<>();
            if (query.moveToFirst()) {
                while (query.moveToNext()) {
                    IPEntity ipEntity = getIpEntityFromCursor(query);
                    ipEntities.add(ipEntity);
                }
                if (ipEntities.size() == 1) {
                    return ipEntities.get(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sqLiteDatabase != null) {
                sqLiteDatabase.close();
            }
        }
        return null;
    }

    void delete(IPEntity ipEntity) {
        if (ipEntity == null || ipEntity.getId() == null) {
            return;
        }
        Log.d(LOG_TAG, "deleteIp:" + ipEntity.getIp());
        String whereClause = "id=?";
        String[] whereArgs = {String.valueOf(ipEntity.getId())};
        SQLiteDatabase sqLiteDatabase = null;
        try {
            sqLiteDatabase = ipDbHelper.getWritableDatabase();
            sqLiteDatabase.delete(IPDbHelper.TABLE_NAME, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sqLiteDatabase != null) {
                sqLiteDatabase.close();
            }
        }
    }

    List<IPEntity> queryListAll(String key){
        List<IPEntity> ipEntities = queryListAll();
        List<IPEntity> results=new ArrayList<>();
        if (ipEntities != null) {
            for (IPEntity ipEntity : ipEntities) {
                if (TextUtils.equals(ipEntity.getKey(),key)){
                    results.add(ipEntity);
                }
            }
        }
        return results;
    }

    private List<IPEntity> queryListAll() {
        List<IPEntity> list = new ArrayList<>();
        SQLiteDatabase sqLiteDatabase = null;
        try {
            sqLiteDatabase = ipDbHelper.getWritableDatabase();
            Cursor cursor = sqLiteDatabase.query(IPDbHelper.TABLE_NAME, null, null, null, null, null, null);
            if (cursor.moveToFirst()) {
                while (cursor.moveToNext()) {
                    IPEntity ipEntity = getIpEntityFromCursor(cursor);
                    if (ipEntity != null) {
                        list.add(ipEntity);
                    }
                }
            }
            cursor.close();
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sqLiteDatabase != null) {
                sqLiteDatabase.close();
            }
        }
        return null;
    }

    @Nullable
    IPEntity querySelected(String key) {
        for (IPEntity ipEntity : queryListAll(key)) {
            if (ipEntity.isSelected()) {
                return ipEntity;
            }
        }
        return null;
    }

    @Nullable
    private IPEntity getIpEntityFromCursor(Cursor cursor) {
        try {
            long id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
            String host = cursor.getString(cursor.getColumnIndex(COLUMN_HOST));
            String port = cursor.getString(cursor.getColumnIndex(COLUMN_PORT));
            int selectedInt = cursor.getInt(cursor.getColumnIndex(COLUMN_SELECTED));
            boolean selected = (selectedInt == 1);
            long createdDate = cursor.getLong(cursor.getColumnIndex(COLUMN_CREATED_DATE));
            String key = cursor.getString(cursor.getColumnIndex(COLUMN_KEY));
            IPEntity ipEntity = new IPEntity();
            ipEntity.setId(id);
            ipEntity.setHost(host);
            ipEntity.setPort(port);
            ipEntity.setKey(key);
            ipEntity.setSelected(selected);
            ipEntity.setCreateDate(createdDate);
            return ipEntity;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    void setDefaultIp(String ipSettingName, String host, String port) {
        insertIP(ipSettingName, host, port);
    }

    /**
     * author 边凌
     * date 2017/3/28 17:26
     * desc ${TODO}
     */
    static class IPEntity {
        private long id;
        private String host;
        private String port;
        private long createDate;
        private boolean selected;
        private String key;

        IPEntity() {
        }

        String getIp() {
            if (TextUtils.isEmpty(port)) {
                return host;
            } else {
                return host + ":" + port;
            }
        }

        public Long getId() {
            return this.id;
        }

        public void setId(long id) {
            this.id = id;
        }


        String getHost() {
            return this.host;
        }

        void setHost(String host) {
            this.host = host;
        }

        String getPort() {
            return this.port;
        }

        void setPort(String port) {
            this.port = TextUtils.isEmpty(port) ? "" : port;
        }

        long getCreateDate() {
            return this.createDate;
        }

        void setCreateDate(long createDate) {
            this.createDate = createDate;
        }

        boolean isSelected() {
            return this.selected;
        }

        void setSelected(boolean selected) {
            this.selected = selected;
        }

        String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }
    }

    /**
     * author 边凌
     * date 2017/4/1 14:21
     * desc ${TODO}
     */

    private static class IPDbHelper extends SQLiteOpenHelper {

        static final String TABLE_NAME = "IpInfo";
        private static final String DATABASE_NAME = "IPDebug.db";
        private static final int DATABASE_VERSION = 1;
        private static final String CREATE_USERINFO_SQL = "CREATE TABLE "
                + TABLE_NAME
                + " (id Integer primary key autoincrement,"
                + " createDate integer,"
                + " host text,"
                + " key text,"
                + " port text,"
                + " selected integer);";

        IPDbHelper(Context context) {
            this(context, DATABASE_NAME, null, DATABASE_VERSION);

        }

        private IPDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
            Log.d(LOG_TAG, "Constructor of IPDbHelper");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_USERINFO_SQL);
            Log.d(LOG_TAG, "onCreate of IPDbHelper");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (newVersion > oldVersion) {
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
                onCreate(db);
            }
            Log.d(LOG_TAG, "onUpgrade of IPDbHelper");
        }
    }
}
