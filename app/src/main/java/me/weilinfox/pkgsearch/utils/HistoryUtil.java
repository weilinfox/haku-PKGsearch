package me.weilinfox.pkgsearch.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import me.weilinfox.pkgsearch.R;
import me.weilinfox.pkgsearch.database.DatabaseHelper;
import me.weilinfox.pkgsearch.searchHistory.SearchHistory;

public final class HistoryUtil {
    private static ArrayList<SearchHistory> searchHistories = null;
    private static String fileName = null;
    private static final String SELECT_HISTORY = "SELECT * " +
                                                    "FROM history " +
                                                    "ORDER BY time DESC;";
    private static final String UPDATE_HISTORY = "UPDATE history " +
                                                    "SET time=? " +
                                                    "WHERE name=? AND option=?;";
    private static final String INSERT_HISTORY = "INSERT INTO history(name, option, time) " +
                                                    "VALUES(?, ?, ?);";
    private static final String REMOVE_HISTORY = "DELETE FROM history " +
                                                    "WHERE name=? AND option=?;";

    public static void initDatabase(Context context) {
        if (fileName == null) fileName = context.getString(R.string.database_file);
    }

    /**
     * 读取数据库缓存
     * @param context 上下文
     * @return ArrayList<SearchHistory>
     */
    public static ArrayList<SearchHistory> readDatabase(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context, fileName, null);
        // 从数据库读取并缓存
        if (searchHistories == null) {
            searchHistories = new ArrayList<>();
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery(SELECT_HISTORY, null);
            if (cursor.moveToFirst()) {
                int pos;
                String name, option;
                long time;
                do {
                    /*
                    android studio 一直红线提示要 >= 0 我就只好判断一下了
                    by weilinfox
                     */
                    pos = cursor.getColumnIndex("name");
                    if (pos < 0) continue;
                    name = cursor.getString(pos);
                    pos = cursor.getColumnIndex("option");
                    if (pos < 0) continue;
                    option = cursor.getString(pos);
                    pos = cursor.getColumnIndex("time");
                    if (pos < 0) continue;
                    time = cursor.getLong(pos);
                    searchHistories.add(new SearchHistory(name, option, time));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return searchHistories;
    }

    /**
     * 更新搜索时间
     * @param context 上下文
     * @param item SearchHistory
     */
    public static void onUpdate(Context context, SearchHistory item) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context, fileName, null);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        database.execSQL(UPDATE_HISTORY, new String[]{Long.toString(item.getDate().getTime()), item.getKeyword(), item.getOption()});
    }

    /**
     * 插入
     * @param context 上下文
     * @param item SearchHistory
     */
    public static void onInsert(Context context, SearchHistory item) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context, fileName, null);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        database.execSQL(INSERT_HISTORY, new String[]{item.getKeyword(), item.getOption(), Long.toString(item.getDate().getTime())});
    }

    /**
     * 删除记录
     * @param context 上下文
     * @param item SearchHistory
     */
    public static void onRemove(Context context, SearchHistory item) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context, fileName, null);
        SQLiteDatabase database = databaseHelper.getWritableDatabase();
        database.execSQL(REMOVE_HISTORY, new String[] {item.getKeyword(), item.getOption()});
    }
}
