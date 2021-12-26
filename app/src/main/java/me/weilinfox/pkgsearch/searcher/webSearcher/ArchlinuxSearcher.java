package me.weilinfox.pkgsearch.searcher.webSearcher;

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
import me.weilinfox.pkgsearch.utils.NetworkUtils;

public class ArchlinuxSearcher extends WebSearcher {

    private static final String TAG = "archlinuxSearcher";
    private String mOption;
    private HashMap<String, String> param = new HashMap<String, String>() {
        {
            //put("sort", "pkgname");
            put("sort", "");
            put("maintainer", "");
            put("flagged", "");
        }
    };

    public ArchlinuxSearcher(@NotNull Context context, @NotNull Handler handler) {
        super(context, handler);
        this.mOption = mContext.getResources().getString(R.string.search_archlinux);
    }

    public static class PackageClass extends SearchResult {
        private String repo;
        private String description;
        private String lastUpDate;
        private String flagDate;

        public PackageClass(@NotNull String name, @NotNull String version, @NotNull String option) {
            super(name, version, option);
            repo = description = lastUpDate = flagDate = "";
        }

        public String getRepo() {
            return repo;
        }

        public String getDescription() {
            return description;
        }

        public String getLastUpDate() {
            return lastUpDate;
        }

        public String getFlagDate() {
            return flagDate;
        }

        public void setRepo(String repo) {
            this.repo = repo;
            this.setInfo(repo);
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void setLastUpDate(String lastUpDate) {
            this.lastUpDate = lastUpDate;
        }

        public void setFlagDate(String flagDate) {
            this.flagDate = flagDate;
        }
    }

    @Override
    public void search(String keyword) {
        param.put("q", keyword.trim());

        super.sendRequest(NetworkUtils.archlinuxBaseUrl+NetworkUtils.archlinuxSearchUrl, param);
    }

    @Override
    public ArrayList<SearchResult> parse() {
        ArrayList<SearchResult> searchResults = new ArrayList<>();

        if (this.mContent == null) {
            return searchResults;
        }
        //Log.d(TAG, this.mContent);

        this.mContent = this.mContent.replace("\n", "");
        String pat = "<table class=\"results\">(.*?)</table>";
        String subPat = "<tr>(.*?)</tr>";
        String itmsPat = "<td.*?</td>";
        String[] itmPat = {"<td>(.*?)</td>", "<td>(.*?)</td>", "\">(.*?)</a></td>", "<td>(.*?)</td>",
                            "\">(.*?)</td>", "<td>(.*?)</td>", "<td>(.*?)</td>", "href=\"(.*?)\""};
        Pattern pattern = Pattern.compile(pat);
        Pattern subPattern = Pattern.compile(subPat);
        Pattern itmsPattern = Pattern.compile(itmsPat);
        Pattern itmPattern;
        Matcher matcher = pattern.matcher(this.mContent);
        ArrayList<String> itms = new ArrayList<>();
        String[] result = new String[8];

        while (matcher.find()) {
            String mat = matcher.group(1);
            Matcher subMat = subPattern.matcher(mat);
            while (subMat.find()) {
                boolean flag = true;
                String item = subMat.group(1);
                Matcher itmsMat = itmsPattern.matcher(item);
                itms.clear();
                while (itmsMat.find()) {
                    itms.add(itmsMat.group());
                }
                try {
                    if (itms.size() == 7) {
                        itms.add(itms.get(2));
                        for (int i = 0; i < 8; i++) {
                            itmPattern = Pattern.compile(itmPat[i]);
                            itmsMat = itmPattern.matcher(itms.get(i));
                            flag &= itmsMat.find();
                            if (flag) {
                                result[i] = itmsMat.group(1).replaceAll("<.*?>", "").trim();
                            } else {
                                break;
                            }
                        }
                    } else {
                        flag = false;
                    }

                    if (flag) {
                        PackageClass pkg = new PackageClass(result[2], result[3], this.mOption);
                        pkg.setArchitecture(result[0]);
                        pkg.setRepo(result[1]);
                        pkg.setUrl(NetworkUtils.archlinuxBaseUrl + result[7]);
                        pkg.setDescription(result[4]);
                        pkg.setLastUpDate(result[5]);
                        pkg.setFlagDate(result[6]);
                        searchResults.add(pkg);
                    }
                } catch (Exception e) {
                    Log.w(TAG, "getResults: " + e.getStackTrace());
                }
            }
        }

        // 清空
        this.mContent = null;
        return searchResults;
    }
}
