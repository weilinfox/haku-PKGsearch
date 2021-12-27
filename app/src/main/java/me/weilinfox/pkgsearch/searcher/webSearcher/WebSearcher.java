package me.weilinfox.pkgsearch.searcher.webSearcher;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

import me.weilinfox.pkgsearch.searchResult.SearchResult;
import me.weilinfox.pkgsearch.searcher.HandleMessageSearcher;
import me.weilinfox.pkgsearch.utils.NetworkUtils;

public abstract class WebSearcher extends HandleMessageSearcher {
    private static final String TAG = "WebSearcher";
    protected String mContent = null;
    protected Context mContext;

    public WebSearcher(@NotNull Context context, @NotNull Handler handler) {
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
                sendSearchStartMessage();

                try {
                    String url = NetworkUtils.urlBuild(domain, param);
                    URLConnection urlConnection = new URL(url).openConnection();
                    HttpURLConnection connection = (HttpURLConnection) urlConnection;
                    connection.setRequestMethod("GET");
                    // 不压缩网页，防止 getContentLength() 长度异常
                    connection.setRequestProperty("Accept-Encoding", "identity");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);

                    connection.connect();

                    sendSearchProcessMessage(20);

                    int code = connection.getResponseCode();
                    if (code == HttpURLConnection.HTTP_OK) {
                        BufferedReader bufferedReader = new BufferedReader(
                                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                        StringBuilder bs = new StringBuilder();
                        String t;
                        int len = connection.getContentLength();
                        int base = 20, last = 20, now;

                        while ((t = bufferedReader.readLine()) != null) {
                            bs.append(t).append("\n");
                            // 防止 len == -1 的异常情况
                            now = base + bs.length()/len*50;
                            if (now > last) {
                                sendSearchProcessMessage(now);
                                last = now;
                            }
                        }
                        mContent = bs.toString();
                    }
                    connection.disconnect();
                } catch (ProtocolException e) {
                    Log.e(TAG, "sendRequest: " + e.toString());
                    mContent = null;
                } catch (IOException e) {
                    Log.e(TAG, "sendRequest: " + e.toString());
                    mContent = null;
                } catch (SecurityException e) {
                    Log.e(TAG, "sendRequest: " + e.toString());
                    mContent = null;
                }

                sendSearchProcessMessage(80);
                checkResult();
            }
        }).start();
    }

    /**
     * 检查搜索结果并发送对应的消息
     */
    protected final void checkResult() {
        if (mContent == null) {
            Log.e(TAG, "showResult: search error.");
            sendSearchErrorMessage();
        } else {
            Log.i(TAG, "showResult: search finished.");
            sendSearchFinishedMessage();
        }
    }
}
