package me.weilinfox.pkgsearch.ui.favourite;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;

import me.weilinfox.pkgsearch.R;
import me.weilinfox.pkgsearch.databinding.FragmentFavouriteBinding;
import me.weilinfox.pkgsearch.searchResult.SearchResult;
import me.weilinfox.pkgsearch.utils.NavigationViewUtil;
import me.weilinfox.pkgsearch.utils.StarList;
import me.weilinfox.pkgsearch.web.WebActivity;

public class FavouriteFragment extends Fragment {

    private static final String TAG = "FavouriteFragment";
    private FavouriteViewModel favouriteViewModel;
    private FragmentFavouriteBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        favouriteViewModel =
                new ViewModelProvider(this).get(FavouriteViewModel.class);

        binding = FragmentFavouriteBinding.inflate(inflater, container, false);
        Spinner spinner = binding.favOption;
        View root = binding.getRoot();

        setupListView(0);

        // 设置选择后刷新 ListView
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setupListView(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        TextView favHold = binding.favHold;
        favHold.setHeight(NavigationViewUtil.height);
        
        return root;
    }

    private void setupListView(int index) {
        // 设置 ListView 适配器
        Log.d(TAG, "setupListView: on option index " + index);
        ListView favView = binding.favList;
        ArrayList<SearchResult> searchResults = StarList.getStarByOption(getContext(), index);
        FavouriteAdapter adapter = new FavouriteAdapter(getContext(), R.layout.fav_item, searchResults, null);
        favView.setAdapter(adapter);
        favView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SearchResult searchResult = searchResults.get(i);
                String url = searchResult.getUrl();

                WebActivity.actionStart(getContext(), url);
            }
        });
        favView.setLongClickable(true);
        favView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                // 复制链接
                ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                String url = searchResults.get(i).getUrl();
                ClipData mClipData = ClipData.newPlainText(null, url);
                clipboardManager.setPrimaryClip(mClipData);
                Toast.makeText(getContext(), getResources().getString(R.string.clipboard_url), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}