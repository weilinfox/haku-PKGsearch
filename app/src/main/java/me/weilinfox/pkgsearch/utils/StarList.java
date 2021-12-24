package me.weilinfox.pkgsearch.utils;

import android.content.Context;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import me.weilinfox.pkgsearch.R;
import me.weilinfox.pkgsearch.searchResult.SearchResult;

public final class StarList {
    private static HashSet<SearchResult> starResults;
    private static String file = null;

    /**
     * 初始化收藏列表数据库，只能初始化一次
     * @param file 数据库名
     */
    public static void initFile(String file) {
        if (StarList.file != null) return;
        StarList.file = file;
        // TODO: 打开数据库
        starResults = new HashSet<>();

        starResults.add(new SearchResult("linux", "1.1", "ArchLinux"));
    }

    /**
     * 存在该项
     * @param searchResult 项目
     * @return boolean 是否存在
     */
    public static boolean hasStar(@NotNull SearchResult searchResult) {
        return starResults.contains(searchResult);
    }

    /**
     * 添加项目
     * @param searchResult 项目
     * @param context 指定则发送吐司
     */
    public static void addStar(@NotNull SearchResult searchResult, Context context) {
        starResults.add(searchResult);
        if (context != null) {
            Toast.makeText(context, R.string.search_stared, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 删除项目
     * @param searchResult 项目
     * @param context 指定则发送吐司
     */
    public static void deleteStar(@NotNull SearchResult searchResult, Context context) {
        starResults.remove(searchResult);
        if (context != null) {
            Toast.makeText(context, R.string.search_unstared, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 获取全部项目
     * @return SearchResult HashSet
     */
    public static HashSet<SearchResult> getStar() {
        return starResults;
    }

    /**
     * 获取全部项目列表
     * @return SearchResult ArrayList
     */
    public static ArrayList<SearchResult> getStarList() {
        return new ArrayList<SearchResult>(starResults);
    }
}
