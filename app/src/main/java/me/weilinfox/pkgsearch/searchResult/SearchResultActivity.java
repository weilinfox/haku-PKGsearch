package me.weilinfox.pkgsearch.searchResult;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import me.weilinfox.pkgsearch.R;
import me.weilinfox.pkgsearch.web.WebActivity;

public class SearchResultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");

        ArrayList<SearchResult> searchResults = (ArrayList<SearchResult>) bundle.getSerializable("data");
        String option = intent.getStringExtra("option");
        setContentView(R.layout.activity_search);
        ArrayAdapter<SearchResult> searchResultArrayAdapter = new SearchResultAdapter(this, R.layout.search_item, searchResults, option);
        ListView listView = (ListView) findViewById(R.id.result_list);
        TextView textView = (TextView) findViewById(R.id.result_title);
        ImageView imageView = (ImageView) findViewById(R.id.result_back);
        textView.setText(option);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchResultActivity.this.finish();
            }
        });
        listView.setAdapter(searchResultArrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SearchResult searchResult = searchResults.get(i);
                WebActivity.actionStart(SearchResultActivity.this, searchResult.getUrl());
            }
        });
    }

    public static void actionStart(@NotNull Context context, @NotNull ArrayList<SearchResult> searchResults, @NotNull String option) {
        Intent intent = new Intent(context, SearchResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", searchResults);
        intent.putExtra("option", option);
        intent.putExtra("bundle", bundle);
        context.startActivities(new Intent[]{intent});
    }
}
