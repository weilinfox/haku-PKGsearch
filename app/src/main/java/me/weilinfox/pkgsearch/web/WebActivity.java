package me.weilinfox.pkgsearch.web;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import me.weilinfox.pkgsearch.R;

public class WebActivity extends AppCompatActivity {
    private static final String TAG = "WebActivity";

    /*private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    }*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");

        setContentView(R.layout.activity_web);
        WebView webView = (WebView) findViewById(R.id.web_tab);
        WebSettings webSettings = webView.getSettings();
        TextView title = (TextView) findViewById(R.id.web_title);
        ImageView refresh = (ImageView) findViewById(R.id.web_reload);
        ImageView back = (ImageView) findViewById(R.id.web_back);

        title.setText(R.string.search_detail);
        // 返回
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebActivity.this.finish();
            }
        });
        // 刷新
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.loadUrl(url);
            }
        });
        // 打开网页
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl(url);
    }

    public static void actionStart(@NotNull Context context, @NotNull String url) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra("url", url);
        context.startActivities(new Intent[]{intent});

        Log.d(TAG, "actionStart: open url " + url);
    }
}
