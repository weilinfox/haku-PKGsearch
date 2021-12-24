package me.weilinfox.pkgsearch.searchResult;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import me.weilinfox.pkgsearch.R;
import me.weilinfox.pkgsearch.network.ArchlinuxSearcher;
import me.weilinfox.pkgsearch.network.DebianSearcher;
import me.weilinfox.pkgsearch.network.UbuntuSearcher;
import me.weilinfox.pkgsearch.searchHistory.SearchHistory;
import me.weilinfox.pkgsearch.searchHistory.SearchHistoryAdapter;
import me.weilinfox.pkgsearch.searchHistory.SearchHistoryFactory;
import me.weilinfox.pkgsearch.utils.StarList;

public class SearchResultAdapter extends ArrayAdapter<SearchResult> {
    private final int resourceId;
    private static final String TAG = "SearchResultAdapter";
    private static Drawable star = null;
    private static Drawable yellowStar = null;
    private String option;

    public SearchResultAdapter(@NonNull Context context, int resource, @NonNull ArrayList<SearchResult> objects, @NotNull String option) {
        super(context, resource, objects);
        this.resourceId  = resource;
        this.option = option;
        if (SearchResultAdapter.star == null) {
            SearchResultAdapter.star = getContext().getDrawable(R.drawable.ic_baseline_star_24);
        }
        if (SearchResultAdapter.yellowStar == null) {
            SearchResultAdapter.yellowStar = getContext().getDrawable(R.drawable.ic_baseline_star_yellow_24);
        }
    }

    class ViewHolder {
        public TextView title;
        public TextView version;
        public TextView info;
        public ImageView star;
        public String option;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        SearchResult searchResult = getItem(position);
        View view = convertView;
        ViewHolder viewHolder;

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.title = view.findViewById(R.id.result_name);
            viewHolder.version = view.findViewById(R.id.result_version);
            viewHolder.info = view.findViewById(R.id.result_info);
            viewHolder.star = view.findViewById(R.id.result_star);
            viewHolder.option = this.option;
            if (searchResult.isStared()) {
                viewHolder.star.setImageDrawable(SearchResultAdapter.yellowStar);
            } else {
                viewHolder.star.setImageDrawable(SearchResultAdapter.star);
            }
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
            if (searchResult.isStared()) {
                viewHolder.star.setImageDrawable(SearchResultAdapter.yellowStar);
            } else {
                viewHolder.star.setImageDrawable(SearchResultAdapter.star);
            }
        }

        String version = searchResult.getVersion() + ": " + searchResult.getArchitecture();
        String info = "";
        if (searchResult instanceof ArchlinuxSearcher.PackageClass) {
            info = ((ArchlinuxSearcher.PackageClass) searchResult).getRepo();
        } else if (searchResult instanceof DebianSearcher.PackageClass) {
            info = ((DebianSearcher.PackageClass) searchResult).getDestribution();
        }
        viewHolder.title.setText(searchResult.getName());
        viewHolder.version.setText(version);
        viewHolder.info.setText(info);
        viewHolder.star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchResult.isStared()) {
                    // 取消收藏
                    Log.d(TAG, "onClick: unstar " + position);
                    searchResult.setStared(false);
                    viewHolder.star.setImageDrawable(SearchResultAdapter.star);
                    StarList.deleteStar(searchResult, getContext());
                } else {
                    // 收藏
                    Log.d(TAG, "onClick: star " + position);
                    searchResult.setStared(true);
                    viewHolder.star.setImageDrawable(SearchResultAdapter.yellowStar);
                    StarList.addStar(searchResult, getContext());
                }
            }
        });


        return view;
    }
}
