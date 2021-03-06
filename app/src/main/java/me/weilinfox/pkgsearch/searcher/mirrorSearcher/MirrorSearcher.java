package me.weilinfox.pkgsearch.searcher.mirrorSearcher;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import me.weilinfox.pkgsearch.searchResult.SearchResult;
import me.weilinfox.pkgsearch.searcher.HandleMessageSearcher;

public abstract class MirrorSearcher extends HandleMessageSearcher {
    private static final String TAG = "MirrorSearcher";
    protected Context mContext;
    private static final int dbVersion = 3;
    private static final String dbNameSuffix = "-mirror.db";
    private static final String CREATE_MIRROR = "CREATE TABLE IF NOT EXISTS packages " +
                                                "(name text, version text," +
                                                "arch text, info text, url text);";
    private static final String CREATE_RELEASE = "CREATE TABLE IF NOT EXISTS releases(" +
                                                "codename text, date text);";
    private static final String DROP_MIRROR = "DROP TABLE IF EXISTS packages;";
    private static final String DROP_RELEASE = "DROP TABLE IF EXISTS releases;";
    private static final String INSERT_ITEM = "INSERT INTO packages(name, version, arch, info, url) " +
                                                "VALUES(?, ?, ?, ?, ?)";
    private static final String INSERT_REL = "INSERT INTO releases(codename, date) " +
                                                "VALUES(?, ?)";

    public MirrorSearcher(@NotNull Context context, @NotNull Handler handler) {
        super(handler);
        this.mContext = context;
    }

    /**
     * ?????????????????????
     * @param keyword ?????????
     * @return boolean ????????????
     */
    public void search(String keyword) {    }

    /**
     * ???????????????????????????????????? SearchResult ?????????
     * @return ArrayList<SearchResult> ?????????????????? null
     */
    public ArrayList<SearchResult> parse() {
        return null;
    }

    /**
     * MirrorSearcher ?????????????????????
     */
    private static class MirrorHelper extends SQLiteOpenHelper {
        public MirrorHelper(@Nullable Context context, @Nullable String name) {
            super(context, name, null, dbVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_MIRROR);
            sqLiteDatabase.execSQL(CREATE_RELEASE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            switch (oldVersion) {
                case 1:
                    break;
                case 2:
                    String addUrl = "ALTER TABLE packages ADD url text;";
                    sqLiteDatabase.execSQL(addUrl);
                    sqLiteDatabase.execSQL(DROP_RELEASE);
                    break;
                default:
                    sqLiteDatabase.execSQL(DROP_MIRROR);
                    sqLiteDatabase.execSQL(DROP_RELEASE);
                    break;
            }
            onCreate(sqLiteDatabase);
        }
    }

    /**
     * MirrorSearcher ????????? PackageClass
     */
    public static class PackageClass extends SearchResult {
        private String description;

        public PackageClass(@NotNull String name, @NotNull String version, @NotNull String option) {
            super(name, version, option);
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    /**
     * ???????????????????????????
     * @param option ??????
     * @param keyword ?????????
     * @return ArrayList<SearchResult>
     */
    protected final ArrayList<SearchResult> searchCache(@NotNull String option, @NotNull String keyword) {
        final String SELECT_ITEM = "SELECT * FROM packages WHERE name LIKE ? ORDER BY name;";
        sendSearchStartMessage();

        ArrayList<SearchResult> searchResults = new ArrayList<>();
        String dbName = option + dbNameSuffix;
        String key = "%";
        String[] keys = keyword.split("\\s*");
        for (String s : keys) {
            key = key + s + "%";
        }
        MirrorHelper helper = new MirrorHelper(mContext, dbName);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(SELECT_ITEM, new String[]{key});
        if (cursor.moveToFirst()) {
            int pos;
            PackageClass result;
            String name, version, arch, info, url;
            do {
                pos = cursor.getColumnIndex("name");
                if (pos < 0) continue;
                name = cursor.getString(pos);
                pos = cursor.getColumnIndex("version");
                if (pos < 0) continue;
                version = cursor.getString(pos);
                pos = cursor.getColumnIndex("arch");
                if (pos < 0) continue;
                arch = cursor.getString(pos);
                pos = cursor.getColumnIndex("info");
                if (pos < 0) continue;
                info = cursor.getString(pos);
                pos = cursor.getColumnIndex("url");
                if (pos < 0) continue;
                url = cursor.getString(pos);

                result = new PackageClass(name, version, option);
                result.setArchitecture(arch);
                result.setUrl(url);
                result.setCanView(false);
                // ???????????????????????? info ??? Description
                // ???????????? info ?????????????????????????????????
                result.setDescription(info);
                searchResults.add(result);
            } while (cursor.moveToNext());
        }

        cursor.close();

        sendSearchFinishedMessage();

        return searchResults;
    }

    /**
     * ??????????????????????????????????????????????????????
     * @param context ?????????
     * @param option ??????
     * @param searchResults ????????????
     * @param release ?????????????????????
     */
    protected static void writeCache(Context context, String option,
                                     @NotNull ArrayList<PackageClass> searchResults,
                                     @NotNull HashMap<String, String> release) {
        final String DELETE_ITEM = "DELETE FROM packages;";
        final String DELETE_REL = "DELETE FROM releases;";
        String dbName = option + dbNameSuffix;
        MirrorHelper helper = new MirrorHelper(context, dbName);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL(DELETE_ITEM);
            for (PackageClass r : searchResults) {
                // ??????????????????????????? info ?????????????????? description
                db.execSQL(INSERT_ITEM, new String[]{r.getName(), r.getVersion(), r.getArchitecture(), r.getDescription(), r.getUrl()});
            }
            db.execSQL(DELETE_REL);
            for (Map.Entry<String, String> entry : release.entrySet()) {
                db.execSQL(INSERT_REL, new String[]{entry.getKey(), entry.getValue()});
            }
            // ??????
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "writeCache: write database error " + e.getStackTrace());
        } finally {
            db.endTransaction();
        }
    }

    /**
     * ??????????????????????????????
     * @param context ?????????
     * @param option ??????
     * @param searchResults ????????????
     * @param release ?????????????????????
     */
    protected static void appendCache(Context context, String option,
                                     @NotNull ArrayList<PackageClass> searchResults,
                                     @NotNull HashMap<String, String> release) {
        String dbName = option + dbNameSuffix;
        MirrorHelper helper = new MirrorHelper(context, dbName);
        SQLiteDatabase db = helper.getWritableDatabase();
        try {
            for (PackageClass r : searchResults) {
                // ??????????????????????????? info ?????????????????? description
                db.execSQL(INSERT_ITEM, new String[]{r.getName(), r.getVersion(), r.getArchitecture(), r.getDescription(), r.getUrl()});
            }
            for (Map.Entry<String, String> entry : release.entrySet()) {
                db.execSQL(INSERT_REL, new String[]{entry.getKey(), entry.getValue()});
            }
        } catch (Exception e) {
            Log.e(TAG, "writeCache: write database error " + e.getStackTrace());
        }
    }

    /**
     * ????????????????????????????????????
     * @param context ?????????
     * @param option ??????
     * @param codename ?????????
     * @param date ??????????????????
     * @return
     */
    protected static boolean testReleaseDate(Context context, String option, String codename, String date) {
        final String SELECT_REL = "SELECT * FROM releases " +
                "WHERE codename=? AND date=?;";
        String dbName = option + dbNameSuffix;
        MirrorHelper helper = new MirrorHelper(context, dbName);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(SELECT_REL, new String[]{codename.trim(), date.trim()});
        boolean flag = cursor.moveToFirst();
        cursor.close();

        return flag;
    }

    /**
     * ??? url ?????? get ??????????????????????????????
     * @param url url
     * @return String ???????????????
     */
    public static String getUrl(String url) {
        String content = null;
        try {
            URLConnection urlConnection = new URL(url).openConnection();
            HttpURLConnection connection = (HttpURLConnection) urlConnection;
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(60000);

            connection.connect();

            int code = connection.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder bs = new StringBuilder();
                String t;
                while ((t = bufferedReader.readLine()) != null) {
                    bs.append(t).append("\n");
                }
                content = bs.toString();
            }
            connection.disconnect();
        } catch (IOException | SecurityException e) {
            Log.e(TAG, "sendRequest: " + e.toString());
            content = null;
        }
        return content;
    }

    public static ByteArrayOutputStream getUrlBytes(String url) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            URLConnection urlConnection = new URL(url).openConnection();
            HttpURLConnection connection = (HttpURLConnection) urlConnection;
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(60000);

            connection.connect();

            int code = connection.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());
                byte[] buffer = new byte[8192];
                int n;
                while ((n = inputStream.read(buffer)) > 0) {
                    out.write(buffer, 0, n);
                }
            }
            connection.disconnect();
        } catch (IOException | SecurityException e) {
            Log.e(TAG, "sendRequest: " + e.toString());
            out.reset();
        }
        return out;
    }

    /**
     * gzip ???????????????
     * @param gzContent gzip ??????
     * @param md5 md5 ?????????
     * @return ?????????????????????????????????????????? null
     */
    protected static String gzipUncompress(@NotNull Context context, byte[] gzContent, String md5) {
        try {
            // md5 ??????
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(gzContent);
            String hashCode = new BigInteger(1, digest.digest()).toString(16);
            for (int i = md5.length()-hashCode.length(); i > 0; i --) {
                hashCode = "0" + hashCode;
            }
            if (!hashCode.equals(md5))
                return null;
        } catch (Exception e) {
            Log.e(TAG, "gzipVerify: " + e.getStackTrace());
            return null;
        }

        // ??????
        String fileName = "Package_" + new Date().getTime() + ".cache";
        ByteArrayInputStream in = new ByteArrayInputStream(gzContent);
        try {
            int n;
            GZIPInputStream gin = new GZIPInputStream(in);
            FileOutputStream out = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            byte[] buffer = new byte[8192];
            while ((n = gin.read(buffer)) > 0) {
                out.write(buffer, 0, n);
            }
            out.close();

            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "gzipUncompress: " + e.getStackTrace());
        }

        return null;
    }
}
