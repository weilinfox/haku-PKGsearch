package me.weilinfox.pkgsearch.searchHistory;

import java.util.ArrayList;

public class SearchHistoryFactory {
    private ArrayList<SearchHistory> searchHistories;

    public SearchHistoryFactory() {
        this.searchHistories = new ArrayList<SearchHistory>();
    }

    /**
     * 从数据库中读取搜索历史
     */
    public void readSearchHistory() {
        searchHistories.add(new SearchHistory("linux", "Archlinux"));
        searchHistories.add(new SearchHistory("qemu", "Archlinux"));
        searchHistories.add(new SearchHistory("qemu", "Debian"));
        searchHistories.add(new SearchHistory("linux", "Debian"));
        searchHistories.add(new SearchHistory("qemu", "Ubuntu"));
        searchHistories.add(new SearchHistory("linux", "Ubuntu"));
        searchHistories.add(new SearchHistory("qemu", "Loongnix"));
        searchHistories.add(new SearchHistory("linux", "Loongnix"));
        searchHistories.add(new SearchHistory("qemu", "Archlinu"));
        searchHistories.add(new SearchHistory("qemu", "Archlinu"));
        searchHistories.add(new SearchHistory("qemu", "Archlinux"));
        searchHistories.add(new SearchHistory("qemu", "Archlinux"));

        // TODO: 从缓存读取
    }

    /**
     * 添加搜索历史，添加同时直接同步到数据库中
     * @param item
     */
    public void addSearchHistory(SearchHistory item) {
        this.searchHistories.remove(item);
        this.searchHistories.add(0, item);

        // TODO: 添加项目到数据库
    }

    /**
     * 删除搜索历史，直接同步到数据库中
     * @param item
     */
    public void deleteSearchHistory(SearchHistory item) {
        this.searchHistories.remove(item);

        // TODO: 从数据库删除项目
    }

    public ArrayList<SearchHistory> getSearchHistories() {
        return searchHistories;
    }
}
