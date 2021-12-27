package me.weilinfox.pkgsearch.searcher.mirrorSearcher;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import me.weilinfox.pkgsearch.R;
import me.weilinfox.pkgsearch.searchResult.SearchResult;
import me.weilinfox.pkgsearch.utils.NetworkUtils;

public class LoongnixSearcher extends MirrorSearcher {
    private static final String TAG = "LoongnixSearcher";
    ArrayList<SearchResult> searchResults = null;
    private String mOption;
    private static String sOption = null;

    public LoongnixSearcher(@NotNull Context context, @NotNull Handler handler) {
        super(context, handler);
        this.mOption = context.getResources().getString(R.string.search_loongnix);
        LoongnixSearcher.sOption = this.mOption;
    }

    @Override
    public void search(String keyword) {
        searchResults = super.searchCache(this.mOption, keyword);
    }

    @Override
    public ArrayList<SearchResult> parse() {
        String info = mOption + " " + NetworkUtils.loongnixDistro;
        for (SearchResult r : searchResults) {
            r.setInfo(info);
        }
        return searchResults;
    }

    public static boolean updateMirror(Context context) {
        if (sOption == null) {
            sOption = context.getResources().getString(R.string.search_loongnix);
        }

        ArrayList<PackageClass> searchResults = new ArrayList<>();
        HashMap<String, String> relMap = new HashMap<>();

        // 获取 Release
        String releaseUrl = NetworkUtils.loongnixBaseUrl + "/dists/" + NetworkUtils.loongnixDistro + "/Release";
        String release = getUrl(releaseUrl);
        HashMap<String, String> releaseMap = new HashMap<>();
        if (release == null) return false;

        ArrayList<String> texts = new ArrayList<>(Arrays.asList(release.split("\n")));
        for (int i = texts.size()-1; i > 0; i--) {
            if (texts.get(i).length() > 0 && texts.get(i).charAt(0) == ' ') {
                texts.set(i-1, texts.get(i-1) + "\n" + texts.get(i));
                texts.remove(i);
            }
        }
        for (String s : texts) {
            if (s.contains(":")) {
                String[] ss = s.split(":", 2);
                releaseMap.put(ss[0].trim(), ss[1].trim());
            } else {
                return false;
            }
        }
        String codename = releaseMap.get("Codename");
        String date = releaseMap.get("Date");
        String md5s = releaseMap.get("MD5Sum");
        if (codename == null || date == null || md5s == null)
            return false;
        relMap.put(codename, date);
        if (testReleaseDate(context, sOption, codename, date))
            return true;

        // 获取所有 Packages
        String pkgBaseUrl = NetworkUtils.loongnixBaseUrl + "/dists/" + NetworkUtils.loongnixDistro + "/";
        ArrayList<String> packages = new ArrayList<>();
        String[] pMd5s = md5s.trim().split("\n");
        String[] pInfos;
        String[] pPath;
        for (String p : pMd5s) {
            p = p.trim();
            pInfos = p.split(" ");
            if (pInfos.length != 3) continue;
            pInfos[0] = pInfos[0].trim();
            pInfos[1] = pInfos[1].trim();
            pInfos[2] = pInfos[2].trim();
            pPath = pInfos[2].split("/");
            // 只要 gz
            if (!pPath[pPath.length-1].trim().equals("Packages.gz"))
                continue;
            packages.add(pkgBaseUrl+pInfos[2]);
            packages.add(pInfos[0]);
        }

        // 获取所有软件包信息
        String fname;
        ByteArrayOutputStream out;
        pkgBaseUrl = NetworkUtils.loongnixBaseUrl + "/";
        for (int i = 0; i < packages.size(); i += 2) {
            out = getUrlBytes(packages.get(i));
            fname = gzipUncompress(context, out.toByteArray(), packages.get(i+1));
            out.reset();
            if (fname == null)
                continue;
            // 解析
            try {
                FileInputStream fin = context.openFileInput(fname);
                BufferedReader reader = new BufferedReader(new InputStreamReader(fin, StandardCharsets.UTF_8));

                String line = null;
                String pkg = null, version = null, arch = null, file = null, desc = null;
                PackageClass mPackage;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().length() == 0) {
                        if (pkg == null || version == null) continue;
                        if (pkg.length() ==  0 || version.length() == 0) continue;
                        mPackage = new PackageClass(pkg, version, sOption);
                        mPackage.setDescription(desc);
                        mPackage.setArchitecture(arch);
                        mPackage.setUrl(pkgBaseUrl+file);
                        searchResults.add(mPackage);
                        pkg = version = arch = file = desc = null;
                    } else {
                        if (line.charAt(0) == ' ') continue;
                        if (! line.contains(":")) continue;
                        String[] ln = line.split(":", 2);
                        if (ln[0].trim().equals("Package")) {
                            pkg = ln[1].trim();
                        } else if (ln[0].trim().equals("Version")) {
                            version = ln[1].trim();
                        } else if (ln[0].trim().equals("Architecture")) {
                            arch = ln[1].trim();
                        } else if (ln[0].trim().equals("Filename")) {
                            file = ln[1].trim();
                        } else if (ln[0].trim().equals("Description")) {
                            desc = ln[1].trim();
                        }
                    }
                }

                context.deleteFile(fname);
                if (i == 0) {
                    writeCache(context, sOption, searchResults, relMap);
                } else {
                    appendCache(context, sOption, searchResults, relMap);
                }
            } catch (Exception e) {
                Log.e(TAG, "updateMirror: " + e.getStackTrace());
                continue;
            }
            searchResults.clear();
        }

        return true;
    }
}
