package me.weilinfox.pkgsearch.searcher.mirrorSearcher;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

import me.weilinfox.pkgsearch.searchResult.SearchResult;
import me.weilinfox.pkgsearch.searcher.HandleMessageSearcher;
import me.weilinfox.pkgsearch.utils.Constraints;

public abstract class MirrorSearcher extends HandleMessageSearcher {
    private static final String TAG = "MirrorSearcher";
    protected String mContent = null;
    protected Context mContext = null;

    public MirrorSearcher(@NotNull Context context, @NotNull Handler handler) {
        super(handler);
        this.mContext = context;
    }

    /**
     * 通过关键字搜索
     * @param keyword 关键字
     * @return boolean 是否成功
     */
    public void search(String keyword) {    }

    /**
     * 解析搜索结果网页，转换为 SearchResult 类列表
     * @return ArrayList<SearchResult> 发生异常返回 null
     */
    public ArrayList<SearchResult> getResults() {
        return null;
    }

    /**
     * 在子线程中发送 get 请求，结果保存在 this.mContent ，handler 支持搜索进度条
     * @param domain url
     * @param param 参数
     */
    protected final void sendRequest(String domain, HashMap<String, String> param) {
        // 连接线程
        new Thread(new Runnable() {
            @Override
            public void run() {
            }
        }).start();
    }

    /**
     * 检查搜索结果并发送对应的消息
     */
    protected final void checkResult() {
        Message message = Message.obtain();
        if (mContent == null) {
            Log.d(TAG, "showResult: search error.");
            message.what = Constraints.searchError;
        } else {
            Log.d(TAG, "showResult: search finished.");
            message.what = Constraints.searchFinished;
        }
        mHandler.sendMessage(message);
    }

    protected final void searchCache(@NotNull String option) {
        Message message = Message.obtain();
        message.what = Constraints.searchStart;
        mHandler.sendMessage(message);

        message = Message.obtain();
        message.what = Constraints.searchFinished;
        mHandler.sendMessage(message);
    }
}
