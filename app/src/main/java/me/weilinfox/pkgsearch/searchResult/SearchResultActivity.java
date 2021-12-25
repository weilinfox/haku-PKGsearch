package me.weilinfox.pkgsearch.searchResult;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import me.weilinfox.pkgsearch.R;
import me.weilinfox.pkgsearch.utils.StarList;
import me.weilinfox.pkgsearch.web.WebActivity;

public class SearchResultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");

        ArrayList<SearchResult> searchResults = (ArrayList<SearchResult>) bundle.getSerializable("data");

        // 查询是否在数据库中
        for (SearchResult r : searchResults) {
            r.setStared(StarList.hasStar(this, r));
        }

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
        listView.setLongClickable(true);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                // 复制链接
                ClipboardManager clipboardManager =
                        (ClipboardManager) SearchResultActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                String url = searchResults.get(i).getUrl();
                ClipData mClipData = ClipData.newPlainText(null, url);
                clipboardManager.setPrimaryClip(mClipData);
                Toast.makeText(SearchResultActivity.this, getResources().getString(R.string.clipboard_url), Toast.LENGTH_SHORT).show();
                return true;
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
