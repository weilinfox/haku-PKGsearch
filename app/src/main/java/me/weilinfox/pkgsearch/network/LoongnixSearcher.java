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

public class LoongnixSearcher extends NetworkSearcher {
    private static final String TAG = "loongnixSearcher";
    private String _option;

    public LoongnixSearcher(@NotNull Context context, @NotNull Handler handler) {
        super(context, handler);
        this._option = _context.getResources().getString(R.string.search_loongnix);
    }

    public static class PackageClass extends SearchResult {
        public PackageClass(@NotNull String name, @NotNull String version, @NotNull String option) {
            super(name, version, option);
        }
    }

    @Override
    public void search(String keyword) {
        super.searchCache(this._option);
    }

    @Override
    public ArrayList<SearchResult> getResults() {
        ArrayList<SearchResult> searchResults = new ArrayList<>();

        return searchResults;
    }
}
