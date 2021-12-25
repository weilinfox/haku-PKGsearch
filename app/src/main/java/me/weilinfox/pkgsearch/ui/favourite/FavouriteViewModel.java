package me.weilinfox.pkgsearch.ui.favourite;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import me.weilinfox.pkgsearch.searchResult.SearchResult;
import me.weilinfox.pkgsearch.utils.StarList;

public class FavouriteViewModel extends ViewModel {

    public FavouriteViewModel() {

    }

    public ArrayList<SearchResult> getStarList(@NotNull Context context, int index) {
        return StarList.getStarByOption(context, index);
    }
}