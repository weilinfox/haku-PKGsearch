package me.weilinfox.pkgsearch.searchHistory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import me.weilinfox.pkgsearch.R;

public class SearchHistoryAdapter extends ArrayAdapter<SearchHistory> {
    private final int resourceId;
    private SearchHistoryFactory searchHistoryFactory = null;

    public SearchHistoryAdapter(@NonNull Context context, int resource, @NonNull List<SearchHistory> objects) {
        super(context, resource, objects);
        this.resourceId  = resource;
    }

    public SearchHistoryAdapter(@NonNull Context context, int resource, @NonNull SearchHistoryFactory obj) {
        super(context, resource, obj.getSearchHistories());
        this.searchHistoryFactory = obj;
        this.resourceId  = resource;
    }

    class ViewHolder {
        public TextView title;
        public TextView info;
        public TextView option;
        public TextView time;
        public ImageView button;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        SearchHistory searchHistory = getItem(position);
        View view = convertView;
        ViewHolder viewHolder;

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.title = view.findViewById(R.id.history_title);
            viewHolder.info = view.findViewById(R.id.history_info);
            viewHolder.option = view.findViewById(R.id.history_option);
            viewHolder.time = view.findViewById(R.id.history_time);
            viewHolder.button = view.findViewById(R.id.history_delete);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (searchHistory.getOption() == null || searchHistory.getOption().length() == 0) {
            view.setEnabled(false);
            viewHolder.info.setText(R.string.search_nohistory);
            viewHolder.title.setText("");
            viewHolder.option.setText("");
            viewHolder.time.setText("");
            viewHolder.button.setVisibility(ImageButton.INVISIBLE);
        } else {
            view.setEnabled(true);
            viewHolder.info.setText("");
            viewHolder.title.setText(searchHistory.getKeyword());
            viewHolder.option.setText(searchHistory.getOption());
            viewHolder.time.setText(searchHistory.getDate().toString());
            viewHolder.button.setVisibility(ImageButton.VISIBLE);
            viewHolder.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (searchHistoryFactory != null) {
                        searchHistoryFactory.deleteSearchHistory(searchHistory);
                        if (searchHistoryFactory.getSearchHistories().size() == 0) {
                            searchHistoryFactory.getSearchHistories().add(new SearchHistory("", ""));
                        }

                        SearchHistoryAdapter.super.notifyDataSetChanged();
                    }
                }
            });
        }

        return view;
    }

    public void update() {
        SearchHistoryAdapter.super.notifyDataSetChanged();
    }
}
