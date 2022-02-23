package me.weilinfox.pkgsearch.ui.search;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import me.weilinfox.pkgsearch.R;
import me.weilinfox.pkgsearch.databinding.FragmentSearchBinding;
import me.weilinfox.pkgsearch.searchHistory.SearchHistory;
import me.weilinfox.pkgsearch.searchHistory.SearchHistoryAdapter;
import me.weilinfox.pkgsearch.searchHistory.SearchHistoryFactory;
import me.weilinfox.pkgsearch.searchResult.SearchResult;
import me.weilinfox.pkgsearch.searchResult.SearchResultFactory;
import me.weilinfox.pkgsearch.searchResult.SearchResultActivity;
import me.weilinfox.pkgsearch.utils.Constraints;
import me.weilinfox.pkgsearch.utils.NavigationViewUtil;

/**
 * 搜索页面
 */
public class SearchFragment extends Fragment {
    private static final String TAG = "SearchFragment";
    private SearchViewModel searchViewModel;
    private ProgressDialog progressDialog;
    private String searchOption;
    private String searchKeyword;
    private int searchLock = 0;
    private FragmentSearchBinding binding;
    private SearchResultFactory searchResultFactory;
    private SearchHistoryFactory searchHistoryFactory;
    /**
     * 消息传递
     */
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what) {
                case Constraints.messageToast:
                    onToast((String) message.obj);
                    break;
                case Constraints.searchStart:
                    progressDialog.setProgress(10);
                    break;
                case Constraints.searchError:
                    progressDialog.dismiss();
                    progressDialog = null;
                    AlertDialog.Builder dialogE = new AlertDialog.Builder(getActivity());
                    dialogE.setTitle(getResources().getString(R.string.search_search));
                    dialogE.setMessage(getResources().getString(R.string.network_error));
                    dialogE.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            onSearchRelease();
                        }
                    });
                    dialogE.setNeutralButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            onSearchRelease();
                        }
                    });
                    dialogE.show();
                    break;
                case Constraints.searchFinished:
                    // 解析搜索结果
                    searchResultFactory.parsePackages();
                    progressDialog.setProgress(80);
                    break;
                case Constraints.parseFinished:
                    // 解析结束 关闭进度条
                    progressDialog.dismiss();
                    progressDialog = null;
                    Boolean flag = (Boolean) message.obj;
                    if (flag) {
                        Log.i(TAG, "handleMessage: show search page.");
                        ArrayList<SearchResult> res = searchResultFactory.getSearchResults();
                        if (res.size() == 0) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                            dialog.setTitle(getResources().getString(R.string.search_search));
                            dialog.setMessage(getResources().getString(R.string.search_noresult));
                            dialog.setNeutralButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });
                            dialog.show();
                        } else {
                            // 显示结果页面
                            SearchResultActivity.actionStart(getContext(), searchResultFactory.getSearchResults(), searchOption);
                            Log.i(TAG, "handleMessage: show search results.");
                        }
                    } else {
                        onToast(getResources().getString(R.string.search_error));
                        Log.e(TAG, "handleMessage: parse search result failed.");
                    }
                    searchHistoryFactory.addSearchHistory(new SearchHistory(searchKeyword, searchOption));
                    ((SearchHistoryAdapter) binding.searchHistory.getAdapter()).update();
                    onSearchRelease();
                    break;
                default:
                    // 进度条刷新
                    if (message.what >= 0 && message.what <= 100) {
                        progressDialog.setProgress(message.what);
                    }
                    break;
            }

            return false;
        }
    });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        searchViewModel =
                new ViewModelProvider(this).get(SearchViewModel.class);
        binding = FragmentSearchBinding.inflate(inflater, container, false);

        searchResultFactory = new SearchResultFactory(this.getContext(), handler);

        searchHistoryFactory = searchViewModel.getSearchHistory(this.getContext());
        ArrayList<SearchHistory> searchHistories = searchHistoryFactory.getSearchHistories();
        View root = binding.getRoot();
        SearchView searchView = binding.searchBar;
        ListView historyView = binding.searchHistory;
        Context context = this.getContext();

        // 默认展开搜索框
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Spinner spinner = binding.searchOption;
                String option = spinner.getSelectedItem().toString();

                // 开始搜索
                onSearch(s, option);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        // 历史为空则插入无历史标签
        if (searchHistories.size() == 0) {
            /*
            如果 option 为 "" ，那么会被认为需要显示无历史记录的标签
            这个记录会在插入新历史记录时被删除 by weilinfox
             */
            searchHistories.add(new SearchHistory("", ""));
        }
        // 设置 ListView 适配器
        SearchHistoryAdapter adapter = new SearchHistoryAdapter(context, R.layout.history_item, searchHistoryFactory);
        historyView.setAdapter(adapter);
        historyView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SearchHistory searchHistory = searchHistories.get(i);
                String keyword = searchHistory.getKeyword();
                String option = searchHistory.getOption();

                if (keyword != null && option != null) {
                    onSearch(keyword, option);
                }
            }
        });
        historyView.setLongClickable(true);
        historyView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                // 复制关键字
                ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                String key = searchHistories.get(i).getKeyword();
                ClipData mClipData = ClipData.newPlainText(null, key);
                clipboardManager.setPrimaryClip(mClipData);
                Toast.makeText(getContext(), getResources().getString(R.string.clipboard_keyword), Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        TextView spaceHolder = binding.searchHold;
        spaceHolder.setHeight(NavigationViewUtil.height);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * 调用 searchResultFactory 进行搜索，搜索过程通过 this.handler 传递
     * @param keyword 关键字
     * @param option 搜索选项
     */
    private void onSearch(String keyword, String option) {
        SearchView searchView = binding.searchBar;
        this.searchOption = option;
        this.searchKeyword = keyword;

        // 锁
        if (!onSearchLock()) {
            Toast.makeText(this.getContext(), getResources().getString(R.string.search_fast), Toast.LENGTH_SHORT).show();
            return;
        }
        if (progressDialog != null) {
            onSearchRelease();
            return;
        }

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        //progressDialog.setTitle(getResources().getString(R.string.search_progress));
        progressDialog.setMax(100);
        progressDialog.setMessage(getResources().getString(R.string.search_progress));
        progressDialog.setProgress(0);
        progressDialog.show();

        // searchView.clearFocus();
        // TODO: 取消焦点
        Log.d(TAG, "Search: " + keyword + " on " + option);

        progressDialog.setProgress(5);

        if (! searchResultFactory.searchPackages(keyword, option)) {
            onToast(getResources().getString(R.string.search_error));
            Log.e(TAG, "onSearch: search init error with keyword " + keyword + " on option " + option);
            progressDialog.dismiss();
            progressDialog = null;
            onSearchRelease();
        }
    }

    private void onToast(@NotNull String msg) {
        Toast.makeText(this.getContext(), msg, Toast.LENGTH_LONG).show();
    }

    private boolean onSearchLock() {
        this.searchLock++;
        if (this.searchLock > 1) {
            this.searchLock--;
            return false;
        } else {
            return true;
        }
    }

    private void onSearchRelease() {
        if (this.searchLock > 0)
            this.searchLock--;
    }
}