package me.weilinfox.pkgsearch.searchHistory;

import androidx.annotation.Nullable;

import java.util.Date;

/**
 * 搜索历史
 */
public class SearchHistory {
    private String keyword;
    private String option;
    private Date date;

    public SearchHistory(String keyword, String option, Date date) {
        this.keyword = keyword;
        this.date = date;
        this.option = option;
    }

    public SearchHistory(String keyword, String option) {
        this.keyword = keyword;
        this.option = option;
        this.date = new Date();
    }

    public String getKeyword() {
        return keyword;
    }

    public String getOption() {
        return option;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof SearchHistory) {
            SearchHistory his = (SearchHistory) obj;
            return this.keyword.equals(his.keyword) && this.option.equals(his.option);
        }
        return false;
    }
}
