package me.weilinfox.pkgsearch.ui.search;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import me.weilinfox.pkgsearch.searchHistory.SearchHistory;
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