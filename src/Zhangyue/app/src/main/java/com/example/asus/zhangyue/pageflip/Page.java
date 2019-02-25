package com.example.asus.zhangyue.pageflip;

import java.util.List;

/**
 * 书页 用于配置每一页的具体内容
 */

public class Page {
    /** 页面内第一行 */
    public int firstLineIndex;
    /** 页面内最后一行 */
    public int lastLineIndex;

    public Page () {
        firstLineIndex = lastLineIndex = 0;
    }

    public Page (int firstLine, int lastLine) {
        firstLineIndex = (firstLine < 0 ? 0 : firstLine);
        lastLineIndex = (lastLine < firstLine ? firstLine : lastLine);
    }

    public void set (int firstLine, int lastLine) {
        firstLineIndex = (firstLine < 0 ? 0 : firstLine);
        lastLineIndex = (lastLine < firstLine ? firstLine : lastLine);
    }

    /** 页面内的行数*/
    public int getLineCount () {
        return lastLineIndex - firstLineIndex + 1;
    }

    /** 获取某行的内容 */
    public String getLine (int index) {
        PageFactory pf = PageFactory.get();
        return pf.getLine(firstLineIndex + index);
    }

    /** 获取当前页在总数中的百分比 */
    public float getRate () {
        PageFactory pf = PageFactory.get();
        int curPage = firstLineIndex / pf.getMaxLineCount();
        System.out.println("firstLineIndex " + firstLineIndex + " pf.getMaxLineCount()" + pf.getMaxLineCount());
        return curPage * 100f / pf.getPagesRealSize();
    }

    /** 当前行在总数中的百分比 */
    public float getLineRate () {
        PageFactory pf = PageFactory.get();
        return firstLineIndex * 1f / pf.getWholeLineCount();
    }
}
