package me.weilinfox.pkgsearch.searchResult;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

/**
 * 所有收藏、搜索的目标类
 */
public class SearchResult implements Serializable {
    private String url;
    private String name;
    private String option;
    private String version;
    private String architecture;
    private String info;
    private boolean stared;
    private boolean canView;

    public SearchResult(@NotNull String name, @NotNull String version, @NotNull String option) {
        this.option = option;
        this.name = name;
        this.version = version;
        this.url = "";
        this.architecture = "";
        this.stared = false;
        this.canView = true;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setArchitecture(String architecture) {
        this.architecture = architecture;
    }

    public void setStared(boolean stared) {
        this.stared = stared;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setCanView(boolean canView) {
        this.canView = canView;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public String getOption() {
        return option;
    }

    public String getVersion() {
        return version;
    }

    public String getArchitecture() {
        return architecture;
    }

    public String getInfo() {
        return info;
    }

    public boolean isStared() {
        return stared;
    }

    public boolean isCanView() {
        return canView;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof SearchResult) {
            SearchResult res = (SearchResult) obj;
            return this.name.equals(res.name) && this.option.equals(res.option);
        }
        return false;
    }
}
