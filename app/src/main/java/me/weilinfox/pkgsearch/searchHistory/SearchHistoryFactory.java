package me.weilinfox.pkgsearch.searchHistory;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import me.weilinfox.pkgsearch.R;
import me.weilinfox.pkgsearch.database.DatabaseHelper;
import me.weilinfox.pkgsearch.network.DebianSearcher;
import me.weilinfox.pkgsearch.utils.HistoryList;

public class SearchHistoryFactory {
    private static final String TAG = "SearchHistoryFactory";
    private ArrayList<SearchHistory> searchHistories;
    private Context mContext;

    public SearchHistoryFactory(Context context) {
        HistoryList.initDatabase(context);
        this.mContext = context;
    }

    /**
     * 从数据库中读取搜索历史
     */
    public void readSearchHistory() {
        this.searchHistories = HistoryList.readDatabase();
    }

    /**
     * 添加搜索历史，添加同时直接同步到数据库中
     * @param item
     */
    public void addSearchHistory(SearchHistory item) {
        if (this.searchHistories.size() <= 1) {
            this.searchHistories.remove(new SearchHistory("", ""));
        }
        try {
            if (this.searchHistories.remove(item)) {
                HistoryList.onUpdate(item);
            } else {
                HistoryList.onInsert(item);
            }
        } catch (Exception e) {
            Toast.makeText(mContext, R.string.database_error, Toast.LENGTH_LONG).show();
            Log.e(TAG, "addSearchHistory: " + e.getStackTrace());
        }
        this.searchHistories.add(0, item);
    }

    /**
     * 删除搜索历史，直接同步到数据库中
     * @param item
     */
    public void deleteSearchHistory(SearchHistory item) {
        HistoryList.onRemove(item);
        this.searchHistories.remove(item);
    }

    public ArrayList<SearchHistory> getSearchHistories() {
        return searchHistories;
    }
}
