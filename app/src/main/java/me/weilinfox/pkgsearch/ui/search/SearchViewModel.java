package me.weilinfox.pkgsearch.ui.search;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import me.weilinfox.pkgsearch.searchHistory.SearchHistoryFactory;

public class SearchViewModel extends ViewModel {

    private SearchHistoryFactory searchHistoryFactory;
    private Context mContext;

    public SearchViewModel() {
    }

    public SearchHistoryFactory getSearchHistory(Context context) {
        searchHistoryFactory = new SearchHistoryFactory(context);
        mContext = context;
        searchHistoryFactory.readSearchHistory();
        return searchHistoryFactory;
    }
}