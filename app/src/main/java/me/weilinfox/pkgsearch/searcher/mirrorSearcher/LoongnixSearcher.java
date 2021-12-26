package me.weilinfox.pkgsearch.searcher.mirrorSearcher;

import android.content.Context;
import android.os.Handler;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import me.weilinfox.pkgsearch.R;
import me.weilinfox.pkgsearch.searchResult.SearchResult;

public class LoongnixSearcher extends MirrorSearcher {
    private static final String TAG = "LoongnixSearcher";
    private String mOption;

    public LoongnixSearcher(@NotNull Context context, @NotNull Handler handler) {
        super(context, handler);
        this.mOption = mContext.getResources().getString(R.string.search_loongnix);
    }

    public static class PackageClass extends SearchResult {
        public PackageClass(@NotNull String name, @NotNull String version, @NotNull String option) {
            super(name, version, option);
        }
    }

    @Override
    public void search(String keyword) {
        super.searchCache(this.mOption);
    }

    @Override
    public ArrayList<SearchResult> getResults() {
        ArrayList<SearchResult> searchResults = new ArrayList<>();

        return searchResults;
    }
}
