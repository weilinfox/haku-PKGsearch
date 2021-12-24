package me.weilinfox.pkgsearch.ui.search;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import me.weilinfox.pkgsearch.searchHistory.SearchHistory;
import me.weilinfox.pkgsearch.searchHistory.SearchHistoryFactory;

public class SearchViewModel extends ViewModel {

    private SearchHistoryFactory searchHistoryFactory;

    public SearchViewModel() {
        searchHistoryFactory = new SearchHistoryFactory();
        searchHistoryFactory.readSearchHistory();
    }

    public SearchHistoryFactory getSearchHistory() {
        return searchHistoryFactory;
    }
}