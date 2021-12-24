package me.weilinfox.pkgsearch.searchResult;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import me.weilinfox.pkgsearch.R;
import me.weilinfox.pkgsearch.network.ArchlinuxSearcher;
import me.weilinfox.pkgsearch.network.DebianSearcher;
import me.weilinfox.pkgsearch.network.LoongnixSearcher;
import me.weilinfox.pkgsearch.network.NetworkSearcher;
import me.weilinfox.pkgsearch.network.UbuntuSearcher;
import me.weilinfox.pkgsearch.utils.Constraints;

/**
 * 搜索结果
 */
public class SearchResultFactory {
    private static final String TAG = "SearchResultFactory";
    private ArrayList<SearchResult> searchResults;
    private NetworkSearcher networkSearcher;
    /**
     * 上下文
     */
    private Context context;
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
        this.context = context;
        this.handler = handler;
        this.searchClasses = new HashMap<String, Class<?>>() {
            {
                put(context.getResources().getString(R.string.search_archlinux), ArchlinuxSearcher.class);
                put(context.getResources().getString(R.string.search_debian), DebianSearcher.class);
                put(context.getResources().getString(R.string.search_ubuntu), UbuntuSearcher.class);
                put(context.getResources().getString(R.string.search_loongnix), LoongnixSearcher.class);
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
                NetworkSearcher networkSearcher = (NetworkSearcher) constructor.newInstance(context, handler);
                this.networkSearcher = networkSearcher;
                networkSearcher.search(keyword);
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
            Log.e(TAG, "searchPackages: " + e.getStackTrace());
            Toast.makeText(this.context, context.getString(R.string.search_error), Toast.LENGTH_LONG).show();

            return false;
        }

        return true;
    }

    public void parsePackages() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = Message.obtain();
                Boolean flag = true;
                message.what = Constraints.parseFinished;
                try {
                    searchResults = networkSearcher.getResults();
                } catch (Exception e) {
                    Log.e(TAG, "parsePackages: " + e.getStackTrace());
                    flag = false;
                }
                message.obj = flag;
                handler.sendMessage(message);
            }
        }).start();
    }
}
