package me.weilinfox.pkgsearch.network;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

import me.weilinfox.pkgsearch.R;
import me.weilinfox.pkgsearch.searchResult.SearchResult;
import me.weilinfox.pkgsearch.utils.Constraints;
import me.weilinfox.pkgsearch.utils.NetworkUtils;

public abstract class NetworkSearcher {
    private static final String TAG = "NetworkSearcher";
    protected String _content = null;
    protected Context _context = null;
    protected Handler _handler = null;

    public NetworkSearcher(@NotNull Context context, @NotNull Handler handler) {
        this._context = context;
        this._handler = handler;
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
     * 在子线程中发送 get 请求，结果保存在 this._content ，handler 支持搜索进度条
     * @param domain url
     * @param param 参数
     */
    protected final void sendRequest(String domain, HashMap<String, String> param) {
        // 连接线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = Message.obtain();
                message.what = Constraints.searchStart;
                _handler.sendMessage(message);

                try {
                    String url = NetworkUtils.urlBuild(domain, param);
                    URLConnection urlConnection = new URL(url).openConnection();
                    HttpURLConnection connection = (HttpURLConnection) urlConnection;
                    connection.setRequestMethod("GET");
                    // 不压缩网页，防止 getContentLength() 长度异常
                    connection.setRequestProperty("Accept-Encoding", "identity");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(10000);

                    connection.connect();

                    message = Message.obtain();
                    message.what = 20;
                    _handler.sendMessage(message);

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
                                message = Message.obtain();
                                message.what = last = now;
                                _handler.sendMessage(message);
                            }
                        }
                        _content = bs.toString();
                    }
                    connection.disconnect();
                } catch (ProtocolException e) {
                    Log.e(TAG, "sendRequest: " + e.toString());
                    _content = null;
                } catch (IOException e) {
                    Log.e(TAG, "sendRequest: " + e.toString());
                    _content = null;
                } catch (SecurityException e) {
                    Log.e(TAG, "sendRequest: " + e.toString());
                    _content = null;
                }

                message = Message.obtain();
                message.what = 80;
                _handler.sendMessage(message);
                checkResult();
            }
        }).start();
    }

    /**
     * 检查搜索结果并发送对应的消息
     */
    protected final void checkResult() {
        Message message = Message.obtain();
        if (_content == null) {
            Log.d(TAG, "showResult: search error.");
            message.what = Constraints.searchError;
        } else {
            Log.d(TAG, "showResult: search finished.");
            message.what = Constraints.searchFinished;
        }
        _handler.sendMessage(message);
    }

    protected final void searchCache(@NotNull String option) {
        Message message = Message.obtain();
        message.what = Constraints.searchStart;
        _handler.sendMessage(message);

        message = Message.obtain();
        message.what = Constraints.searchFinished;
        _handler.sendMessage(message);
    }
}
