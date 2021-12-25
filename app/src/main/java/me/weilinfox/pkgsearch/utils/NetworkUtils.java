package me.weilinfox.pkgsearch.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.IDN;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class NetworkUtils {
    private static final String TAG = "NetworkUtils";

    /**
     * 构造 url 参数
     * @param domain url
     * @param params 参数
     * @return 构造后的 String
     */
    public static String urlBuild(String domain, HashMap<String, String> params) {
        String ansUrl = "";
        URI uri;

        for (Map.Entry<String, String> entry : params.entrySet()) {
            ansUrl += "&" + entry.getKey() + "=" + entry.getValue();
        }
        ansUrl = domain + "?" + ansUrl.substring(1);

        try {
            URL url = new URL(ansUrl);
            uri = new URI(url.getProtocol(), url.getUserInfo(), IDN.toASCII(url.getHost()), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
        } catch (MalformedURLException | URISyntaxException e) {
            return null;
        }

        ansUrl = uri.toASCIIString();
        Log.d(TAG, "urlBuild: get new url " + ansUrl);

        return ansUrl;
    }

    public static String archlinuxBaseUrl = "https://archlinux.org/";
    public static String archlinuxSearchUrl = "/packages/";
    public static String debianBaseUrl = "https://packages.debian.org/";
    public static String debianSearchUrl = "/search";
    public static String ubuntuBaseUrl = "https://packages.ubuntu.com/";
    public static String ubuntuSearchUrl = "/search";
}
