package me.weilinfox.pkgsearch.network;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.weilinfox.pkgsearch.R;
import me.weilinfox.pkgsearch.searchResult.SearchResult;
import me.weilinfox.pkgsearch.utils.Constraints;

public class DebianSearcher extends NetworkSearcher {

    private static String TAG = "debianSearcher";
    private String mOption;
    private String baseUrl = Constraints.debianBaseUrl;
    private String searchUrl = Constraints.debianSearchUrl;
    private HashMap<String, String> param = new HashMap<String, String>() {
        {
            put("suite", "default");
            put("section", "all");
            put("arch", "any");
            put("searchon", "names");
        }
    };

    public DebianSearcher(@NotNull Context context, @NotNull Handler handler) {
        super(context, handler);
        this.mOption = mContext.getResources().getString(R.string.search_debian);
    }

    public static class PackageClass extends SearchResult {
        private String distribution;
        private String description;

        public PackageClass(@NotNull String name, @NotNull String version, @NotNull String option) {
            super(name, version, option);
            distribution = description = "";
        }

        public String getDestribution() {
            return distribution;
        }

        public String getDescription() {
            return description;
        }

        public void setDestribution(String destribution) {
            this.distribution = destribution;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    @Override
    public void search(String keyword) {
        param.put("keywords", keyword.trim());

        super.sendRequest(this.baseUrl+this.searchUrl, param);
    }

    @Override
    public ArrayList<SearchResult> getResults() {
        ArrayList<SearchResult> searchResults = new ArrayList<>();

        if (this.mContent == null) {
            return searchResults;
        }
        //Log.d(TAG, this.mContent);

        this.mContent = this.mContent.replace("\n", "").replace("\t", " ");
        String pat = "<h3>.*?</ul>";
        String disPat = "<li.*?</li>";
        String[] itmPat = new String[]{
                "<h3>(.*?)</h3>", "href=\"(.*?)\"", "<a.*?>(.*?)</a>", "</a>(.*?)<br>", "<br>(.*?)</li>"};
        String[] itmStr = new String[6];
        Pattern pattern = Pattern.compile(pat);
        Matcher matcher = pattern.matcher(this.mContent);
        ArrayList<String> itms = new ArrayList<>();
        PackageClass packageClass;

        while (matcher.find()) {
            String itmString = matcher.group();
            Pattern itmPattern;
            Matcher itmMatcher;
            Pattern innerPattern;
            Matcher innerMatcher;

            try {
                boolean flag = true;
                // 获得包名
                itmPattern = Pattern.compile(itmPat[0]);
                itmMatcher = itmPattern.matcher(itmString);
                if (flag = itmMatcher.find()) {
                    itmStr[0] = itmMatcher.group(1).trim();
                    itmStr[0] = itmStr[0].split(" ", 2)[1];
                } else {
                    continue;
                }
                // 遍历版本
                itmPattern = Pattern.compile(disPat);
                itmMatcher = itmPattern.matcher(itmString);
                while (itmMatcher.find()) {
                    String verStr = itmMatcher.group();
                    flag = true;
                    for (int i = 1; i < 4; i++) {
                        innerPattern = Pattern.compile(itmPat[i]);
                        innerMatcher = innerPattern.matcher(verStr);
                        if (innerMatcher.find()) {
                            itmStr[i] = innerMatcher.group(1).replaceAll("<.*?>", "").trim();
                        } else {
                            flag = false;
                            break;
                        }
                    }
                    if (!flag)
                        continue;
                    // 遍历体系结构
                    innerPattern = Pattern.compile(itmPat[4]);
                    innerMatcher = innerPattern.matcher(verStr);
                    if (innerMatcher.find()) {
                        String archMat = innerMatcher.group(1);
                        String[] archs = archMat.split("<br>");
                        String[] strings;
                        for (String s : archs) {
                            s = s.replaceAll("<.*?>", "");
                            if (s.contains(":")) {
                                strings = s.split(":", 2);
                                itmStr[4] = strings[0].trim();
                                itmStr[5] = strings[1].trim();

                                // 单个包
                                packageClass = new PackageClass(itmStr[0], itmStr[4], this.mOption);
                                packageClass.setUrl(this.baseUrl+itmStr[1]);
                                packageClass.setDestribution(itmStr[2]);
                                packageClass.setDescription(itmStr[3]);
                                packageClass.setArchitecture(itmStr[5]);
                                searchResults.add(packageClass);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "getResults: " + e.getStackTrace());
            }

        }


        // 清空
        this.mContent = null;
        return searchResults;
    }

    public void setOption(String option) {
        this.mOption = option;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setSearchUrl(String searchUrl) {
        this.searchUrl = searchUrl;
    }

    public void setParam(HashMap<String, String> param) {
        this.param = param;
    }

    public static void setTAG(String TAG) {
        DebianSearcher.TAG = TAG;
    }
}
