package me.weilinfox.pkgsearch.searchResult;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import me.weilinfox.pkgsearch.R;
import me.weilinfox.pkgsearch.utils.StarUtil;

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
        String info = searchResult.getInfo();
        viewHolder.title.setText(searchResult.getName());
        viewHolder.version.setText(version);
        viewHolder.info.setText(info);
        viewHolder.star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchResult.isStared()) {
                    // ????????????
                    Log.d(TAG, "onClick: unstar " + position);
                    searchResult.setStared(false);
                    viewHolder.star.setImageDrawable(SearchResultAdapter.star);
                    StarUtil.deleteStar(getContext(), searchResult);
                } else {
                    // ??????
                    Log.d(TAG, "onClick: star " + position);
                    searchResult.setStared(true);
                    viewHolder.star.setImageDrawable(SearchResultAdapter.yellowStar);
                    StarUtil.addStar(getContext(), searchResult);
                }
            }
        });


        return view;
    }
}
