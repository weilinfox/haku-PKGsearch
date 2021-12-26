package me.weilinfox.pkgsearch.searcher;

import android.os.Handler;
import android.os.Message;

import me.weilinfox.pkgsearch.utils.Constraints;

public abstract class HandleMessageSearcher implements Searcher {
    protected Handler mHandler;

    public HandleMessageSearcher(Handler handler) {
        this.mHandler = handler;
    }

    /**
     * 发送开始网络搜索消息
     */
    @Override
    public final void sendSearchStartMessage() {
        Message message = Message.obtain();
        message.what = Constraints.searchStart;
        mHandler.sendMessage(message);
    }

    /**
     * 发送结束网络搜索消息，下一步通常是进行解析
     */
    @Override
    public final void sendSearchFinishedMessage() {
        Message message = Message.obtain();
        message.what = Constraints.searchFinished;
        mHandler.sendMessage(message);
    }

    /**
     * 发送搜索出错消息，同时标志着搜索结束
     */
    @Override
    public final void sendSearchErrorMessage() {
        Message message = Message.obtain();
        message.what = Constraints.searchError;
        mHandler.sendMessage(message);
    }

    /**
     * 发送搜索进度消息
     * @param process
     */
    @Override
    public final void sendSearchProcessMessage(int process) {
        if (process < 0 || process > 100) return;
        Message message = Message.obtain();
        message.what = process;
        mHandler.sendMessage(message);
    }

    /**
     * 发送解析状态消息，同时标志着搜索结束
     * @param flag 成功/失败
     */
    @Override
    public final void sendParseFinishedMessage(Boolean flag) {
        Message message = Message.obtain();
        message.what = Constraints.parseFinished;
        message.obj = flag;
        mHandler.sendMessage(message);
    }
}
