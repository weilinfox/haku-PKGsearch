package me.weilinfox.pkgsearch.ui.favourite;

import android.content.Context;
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
import me.weilinfox.pkgsearch.searchResult.SearchResult;
import me.weilinfox.pkgsearch.utils.StarList;

public class FavouriteAdapter extends ArrayAdapter<SearchResult> {
    private final int resourceId;
    private static final String TAG = "FavouriteAdapter";
    ArrayList<SearchResult> searchResults;
    private String option;

    public FavouriteAdapter(@NonNull Context context, int resource, @NonNull ArrayList<SearchResult> objects, @NotNull String option) {
        super(context, resource, objects);
        this.resourceId  = resource;
        this.option = option;
        this.searchResults = objects;
    }

    class ViewHolder {
        public TextView title;
        public TextView version;
        public TextView info;
        public ImageView cancel;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        SearchResult searchResult = getItem(position);
        View view = convertView;
        FavouriteAdapter.ViewHolder viewHolder;

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new FavouriteAdapter.ViewHolder();
            viewHolder.title = view.findViewById(R.id.fav_name);
            viewHolder.version = view.findViewById(R.id.fav_version);
            viewHolder.info = view.findViewById(R.id.fav_info);
            viewHolder.cancel = view.findViewById(R.id.fav_delete);
            view.setTag(viewHolder);
        } else {
            viewHolder = (FavouriteAdapter.ViewHolder) view.getTag();
        }

        String version = searchResult.getVersion() + ": " + searchResult.getArchitecture();
        String info = searchResult.getInfo();
        viewHolder.title.setText(searchResult.getName());
        viewHolder.version.setText(version);
        viewHolder.info.setText(info);
        viewHolder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StarList.deleteStar(getContext(), searchResult);
                searchResults.remove(position);
                notifyDataSetChanged();
            }
        });

        return view;
    }
}
