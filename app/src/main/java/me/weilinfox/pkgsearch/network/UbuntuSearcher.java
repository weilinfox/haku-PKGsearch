package me.weilinfox.pkgsearch.network;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.weilinfox.pkgsearch.R;
import me.weilinfox.pkgsearch.searchResult.SearchResult;
import me.weilinfox.pkgsearch.utils.Constraints;
import me.weilinfox.pkgsearch.network.DebianSearcher.PackageClass;
import me.weilinfox.pkgsearch.utils.NetworkUtils;

public class UbuntuSearcher extends DebianSearcher {
    public UbuntuSearcher(@NotNull Context context, @NotNull Handler handler) {
        super(context, handler);
        super.setOption(mContext.getResources().getString(R.string.search_ubuntu));
        setBaseUrl(NetworkUtils.ubuntuBaseUrl);
        setSearchUrl(NetworkUtils.ubuntuSearchUrl);
        setParam(new HashMap<String, String>() {
            {
                put("suite", "all");
                put("section", "all");
                put("arch", "any");
                put("searchon", "names");
            }
        });
        setTAG("ubuntuSearcher");
    }
}
