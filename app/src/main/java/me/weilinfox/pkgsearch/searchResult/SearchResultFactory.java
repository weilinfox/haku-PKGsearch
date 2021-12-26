package me.weilinfox.pkgsearch.searchResult;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import me.weilinfox.pkgsearch.R;
import me.weilinfox.pkgsearch.searcher.HandleMessageSearcher;
import me.weilinfox.pkgsearch.searcher.webSearcher.ArchlinuxSearcher;
import me.weilinfox.pkgsearch.searcher.webSearcher.DebianSearcher;
import me.weilinfox.pkgsearch.searcher.mirrorSearcher.LoongnixSearcher;
import me.weilinfox.pkgsearch.searcher.webSearcher.UbuntuSearcher;

/**
 * 搜索结果
 */
public class SearchResultFactory {
    private static final String TAG = "SearchResultFactory";
    private ArrayList<SearchResult> searchResults;
    private HandleMessageSearcher searcher;
    /**
     * 上下文
     */
    private Context mContext;
    private Handler handler;
    /**
     * 选项与搜索类映射
     */
    private HashMap<String, Class<?>> searchClasses;

    /**
     * 通过上下文构造对象
     * @param context 上下文
     */
    public SearchResultFactory(Context context, Handler handler) {
        searchResults = new ArrayList<SearchResult>();
        this.mContext = context;
        this.handler = handler;
        this.searchClasses = new HashMap<String, Class<?>>() {
            {
                put(mContext.getResources().getString(R.string.search_archlinux), ArchlinuxSearcher.class);
                put(mContext.getResources().getString(R.string.search_debian), DebianSearcher.class);
                put(mContext.getResources().getString(R.string.search_ubuntu), UbuntuSearcher.class);
                put(mContext.getResources().getString(R.string.search_loongnix), LoongnixSearcher.class);
            }
        };
    }

    public ArrayList<SearchResult> getSearchResults() {
        return searchResults;
    }

    /**
     * 通过关键词和搜索选项（发行版）搜索
     * @param keyword 关键字
     * @param option 搜索选项（发行版）
     * @return boolean 请求是否成功we
     */
    public boolean searchPackages(String keyword, String option) {
        if (keyword == null || option == null) return false;
        searchResults = null;
        Class<?> searchClass = searchClasses.get(option);

        try {
            if (searchClass == null) return false;
            else {
                Constructor constructor = searchClass.getDeclaredConstructor(new Class[]{Context.class, Handler.class});
                constructor.setAccessible(true);
                HandleMessageSearcher handleMessageSearcher = (HandleMessageSearcher) constructor.newInstance(mContext, handler);
                this.searcher = handleMessageSearcher;
                handleMessageSearcher.search(keyword);
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            Log.e(TAG, "searchPackages: " + e.getStackTrace());
            Toast.makeText(this.mContext, this.mContext.getString(R.string.search_error), Toast.LENGTH_LONG).show();

            return false;
        }

        return true;
    }

    public void parsePackages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Boolean flag = true;
                try {
                    searchResults = searcher.parse();
                } catch (Exception e) {
                    Log.e(TAG, "parsePackages: " + e.getStackTrace());
                    flag = false;
                }
                searcher.sendParseFinishedMessage(flag);
            }
        }).start();
    }
}
