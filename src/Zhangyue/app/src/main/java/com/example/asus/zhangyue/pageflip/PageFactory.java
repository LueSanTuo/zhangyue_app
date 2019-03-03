package com.example.asus.zhangyue.pageflip;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * 书页处理工厂 处理划分内容 匹配段落行等 以及生成新内容形成的书页
 */

public class PageFactory {
    /** 单例 */
    public static PageFactory instance;

    /** 画布宽度 */
    private int canvasWidth;
    /** 画布高度 */
    private int canvasHeight;
    /** 画笔 */
    private Paint mPaint;
    /** 页集 */
    private List<Page> mPages;

    /** 页面显示的最大行数 */
    private int maxLineCount;
    /** 该章节名称 */
    public String chapterName;
    /** 整个章节的内容 */
    public String pageContentStr;
    /** 存放整个章节划分好行的内容 */
    private List<String> pageContent;
    /** 页集当前页数 */
    private int pagesRealSize;

    public static PageFactory get () {
        if (instance == null) {
            instance = new PageFactory();
        }
        return instance;
    }

    /** 设置章节名称 每章首行章节名称来显示 */
    public void setChapterName () {
        Page p = getPage(0);
        if (p == null) {
            chapterName = "";
            return;
        }
        if (p.getLineCount() <= 0)
            return;
        chapterName = getPage(0).getLine(0);
    }

    /** 获得页面显示的最大行数 */
    public int getMaxLineCount() {
        return maxLineCount;
    }

    /** 获取整个章节的行数 */
    public int getWholeLineCount() {
        if (pageContent == null)
            return 0;
        return pageContent.size();
    }

    /** 直接绘制某一页 */
    public void drawPage (Canvas canvas, int index) {
        Page curPage = getPage(index);
        if (curPage == null)
            return;
        int curPageLine = curPage.getLineCount();
        for (int i = 0; i < curPageLine; i++) {
            float y = (mPaint.getTextSize() + PageRender.SPACING_LINE) * i + PageRender.SPACING_UD + mPaint.getTextSize();
            canvas.drawText(curPage.getLine(i), PageRender.SPACING_LR, y, mPaint);
        }
    }

    /** 设置画布 */
    public void setCanvas (int width, int height, Paint paint) {
        canvasWidth = width;
        canvasHeight = height;
        mPaint = paint;
    }

    /** 划分章节 */
    public List<String> divChapter (String chapter) {
        List<String> strList = new ArrayList<>();
        // 根据回车划分段落
        String[] strArr = chapter.split("\n|\r");
        System.out.println("PageFactory : divChapter + " + strArr.length);
        for (int i = 0; i < strArr.length; i++) {
            strList.addAll(divParagraph(strArr[i]));
            //strList.add("\n"); // 添加段落结束符
            System.out.println("PageFactory: divParagraph finish + " + i);
        }
        return strList;
    }

    /** 划分成页 */
    public void divPage () {
        if (pageContentStr == null)
            return;
        divPage(pageContentStr);
        setChapterName ();
    }

    /** 给一章节的内容来 划分成页 */
    public List<Page> divPage (String chapter) {
        System.out.println("PageFactory:Start DivPage");
        if (mPages == null) {
            mPages = new ArrayList<>();
        }
        // 获得该章节的所有行数
        pageContent = divChapter(chapter);
        int total = pageContent.size();
        int textSize = (int)mPaint.getTextSize();
        // 根据页面的上下间距 行间距 设置每个页面内的最大行数
        int visibleHeight = canvasHeight - PageRender.SPACING_UD * 2 - textSize;
        maxLineCount = (int) (visibleHeight / (textSize + PageRender.SPACING_LINE)); // 可显示的行数 = 可显示的高度/(每个字体的高度 + 行距)
        // 如果一行都没有
        if (total <= 0) {
            pagesRealSize = 0;
        } else {
            pagesRealSize = total / maxLineCount + 1;
        }
        int lineFlag = 0;
        System.out.println("PageFactory: total" + total);
        while (lineFlag < total) {
            Page p;
            int index;
            // 如果已经有实例page直接拿来用
            if (mPages.size() > (index = lineFlag / maxLineCount)) {
                p = mPages.get(index);
                p.set(lineFlag, Math.min(maxLineCount + lineFlag - 1, total - 1));
            } else {
                p = new Page(lineFlag, Math.min(maxLineCount + lineFlag - 1, total - 1));
                mPages.add(p);
            }
            lineFlag += maxLineCount;
        }
        System.out.println("PageFactory:total lines: " + total);
        System.out.println("PageFactory:pagesRealSize: " + pagesRealSize);
        System.out.println("PageFactory:End DivPage");
        return mPages;
    }

    /** 获得当前页集 */
    public List<Page> getPages () {
        if (mPages == null) {
            mPages = new ArrayList<>();
            pagesRealSize = 0;
        }
        return mPages;
    }

    /** 获得当前页集的真实页数 */
    public int getPagesRealSize () {
        return pagesRealSize;
    }

    /** 获取当前页集某一页 */
    public Page getPage (int index) {
        Page p = null;
        // 判断下标是否过界
        if (index < pagesRealSize && index >= 0) {
            p = mPages.get(index);
        } else {
            System.out.println("PageFactory:getPage index out of range " + index);
        }
        return p;
    }

    /** 获取当前内容的某行 */
    public String getLine (int index) {
        if (index < pageContent.size() && index >= 0) {
            return pageContent.get(index);
        } else {
            System.out.println("PageFactory:getLine index out of range");
            return "";
        }
    }

    /** 划分段落 */
    public List<String> divParagraph (String paragraph) {
        List<String> strList = new ArrayList<>();
        int visibleWidth = canvasWidth - PageRender.SPACING_LR * 2;
        System.out.println("PageFactory: divParagraph start + " + visibleWidth);
        while (paragraph.length() > 0) {
            int nSize = 1;
            nSize = mPaint.breakText(paragraph, true, visibleWidth, null);// 切割文本, 返回本行长度
            strList.add(paragraph.substring(0, nSize)); // 行集中添加本行
            paragraph = paragraph.substring(nSize); // 去掉已添加的行
        }
        // System.out.println("PageFactory: divParagraph finish + " + paragraph);
        return strList;
    }

}
