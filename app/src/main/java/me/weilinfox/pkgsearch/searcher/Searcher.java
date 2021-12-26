package me.weilinfox.pkgsearch.searcher;

import java.util.ArrayList;

import me.weilinfox.pkgsearch.searchResult.SearchResult;

public interface Searcher {
    void search(String keyword);
    ArrayList<SearchResult> getResults();
    void sendSearchStartMessage();
    void sendSearchFinishedMessage();
    void sendSearchErrorMessage();
    void sendSearchProcessMessage(int process);
    void sendParseFinishedMessage(Boolean flag);
}
