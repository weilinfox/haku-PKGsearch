package me.weilinfox.pkgsearch.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

import me.weilinfox.pkgsearch.R;
import me.weilinfox.pkgsearch.database.DatabaseHelper;
import me.weilinfox.pkgsearch.searchResult.SearchResult;

public final class StarList {
    private static String fileName = null;
    private static final String SELECT_ALL = "SELECT * FROM star;";
    private static final String FIND_ITEM = "SELECT COUNT(*) FROM star " +
                                                "WHERE name=? AND option=? AND version=? AND url=?;";
    private static final String INSERT_ITEM = "INSERT INTO star(name, option, version, url, arch) " +
                                                "VALUES(?, ?, ?, ?, ?);";
    private static final String DELETE_ITEM = "DELETE FROM star " +
                                                "WHERE name=? AND option=? AND version=? AND url=? AND arch=?;";
    private static final String SELECT_BY_PREFIX = "SELECT * FROM star WHERE option='";
    private static final String SELECT_BY_SUFFIX = "';";
    private static String[] SELECT_BY_ITEM = null;

    /**
     * 初始化收藏列表数据库
     * @param context 上下文
     */
    public static void initDatabase(@NotNull Context context) {
        fileName = context.getString(R.string.database_file);

        if (SELECT_BY_ITEM == null) {
            TypedArray typedArray = context.getResources().obtainTypedArray(R.array.fav_option);
            int len = typedArray.length();
            ArrayList<String> favList = new ArrayList<>();
            int rid;
            for (int i = 0; i < len; i++) {
                rid = typedArray.getResourceId(i, -1);
                if (rid != -1) {
                    favList.add(context.getResources().getString(rid));
                }
            }
            SELECT_BY_ITEM = new String[favList.size()];
            len = favList.size();
            for (int i = 1; i < len; i++) {
                SELECT_BY_ITEM[i] = SELECT_BY_PREFIX + favList.get(i) + SELECT_BY_SUFFIX;
            }
            SELECT_BY_ITEM[0] = SELECT_ALL;
        }
    }

    /**
     * 存在该项
     * @param context 上下文
     * @param item 项目
     * @return boolean 是否存在
     */
    public static boolean hasStar(@NotNull Context context, @NotNull SearchResult item) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context, fileName, null);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(FIND_ITEM, new String[]{item.getName(), item.getOption(),
                                                item.getVersion(), item.getUrl()});
        cursor.moveToFirst();
        Long cnt = cursor.getLong(0);
        cursor.close();
        return cnt > 0;
    }

    /**
     * 添加项目
     * @param context 上下文
     * @param item 项目
     */
    public static void addStar(@NotNull Context context, @NotNull SearchResult item) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context, fileName, null);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.execSQL(INSERT_ITEM, new String[]{item.getName(), item.getOption(), item.getVersion(),
                                                item.getUrl(), item.getArchitecture()});
        Toast.makeText(context, R.string.search_stared, Toast.LENGTH_SHORT).show();
    }

    /**
     * 删除项目
     * @param context 上下文
     * @param item 项目
     */
    public static void deleteStar(@NotNull Context context, @NotNull SearchResult item) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context, fileName, null);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.execSQL(DELETE_ITEM, new String[]{item.getName(), item.getOption(), item.getVersion(),
                                                item.getUrl(), item.getArchitecture()});
        Toast.makeText(context, R.string.search_unstared, Toast.LENGTH_SHORT).show();
    }

    /**
     * 获取项目
     * @param context 上下文
     * @param item 项目
     * @return ArrayList<SearchResult>
     */
    public static ArrayList<SearchResult> getStarByOption(@NotNull Context context, int item) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context, fileName, null);
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(SELECT_BY_ITEM[item], new String[]{});
        ArrayList<SearchResult> results = new ArrayList<>();
        if (cursor.moveToFirst()) {
            String name, option, version, url, arch;
            SearchResult searchResult;
            int pos;
            do {
                pos = cursor.getColumnIndex("name");
                if (pos < 0) continue;
                name = cursor.getString(pos);
                pos = cursor.getColumnIndex("option");
                if (pos < 0) continue;
                option = cursor.getString(pos);
                pos = cursor.getColumnIndex("version");
                if (pos < 0) continue;
                version = cursor.getString(pos);
                pos = cursor.getColumnIndex("url");
                if (pos < 0) continue;
                url = cursor.getString(pos);
                pos = cursor.getColumnIndex("arch");
                if (pos < 0) continue;
                arch = cursor.getString(pos);
                searchResult = new SearchResult(name, version, option);
                searchResult.setUrl(url);
                searchResult.setArchitecture(arch);
                results.add(searchResult);
            } while (cursor.moveToNext());
            // 数据库中最后插入的在前
            Collections.reverse(results);
        }
        return results;
    }
}
