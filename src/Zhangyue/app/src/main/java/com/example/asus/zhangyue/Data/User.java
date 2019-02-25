package com.example.asus.zhangyue.Data;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;

import com.example.asus.zhangyue.R;
import com.example.asus.zhangyue.myutil.HttpUtil;
import com.example.asus.zhangyue.pageflip.PageRender;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * 用户 处理是否是游客或者是已登录的用户的数据
 */
public class User {
    /** IP地址 */
    //public final static String IP_ADD = "http://192.168.43.177:8080/work/Zhangyue/"; // 热点
    public final static String IP_ADD = "http://10.13.183.224:8080/work/Zhangyue/"; // 闪讯 EDU
    /** 获得书本内容的地址 */
    public final static String IP_ADD_LOAD_BOOK = IP_ADD + "LoadBook.jsp";
    /** 获得书本目录的地址 */
    public final static String IP_ADD_LOAD_BOOK_CONTENT = IP_ADD + "LoadBookContent.jsp";
    /** 获得书籍列表的地址 */
    public final static String IP_ADD_GET_BOOKS = IP_ADD + "GetBooks.jsp";
    /** 登录地址 */
    public final static String IP_ADD_LOGIN = IP_ADD + "Login.jsp";
    /** 注册地址 */
    public final static String IP_ADD_REGISTER = IP_ADD + "Register.jsp";
    /** 处理书签 */
    public final static String IP_ADD_DEAL_BOOKMARKS = IP_ADD + "DealBookMarks.jsp";
    /** 处理阅读记录 */
    public final static String IP_ADD_DEAL_READRECORD = IP_ADD + "DealReadRecord.jsp";

    /** 单例 */
    public static User instance;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    /** 登录状态 */
    private boolean isLoaded = false;
    /** 账号 */
    private String account;
    /** 用户名 */
    private String userName;
    /** 密码 */
    private String password;
    /** 经验 */
    private int userExp;

    /** 上一次阅读某本书的位置 */
    private List<BookMark> historyList = new ArrayList<>();
    /** 当前书签列表 */
    private List<BookMark> bookMarkList = new ArrayList<>();
    /** 书id和书名的哈希表 */
    private HashMap<String, Book> bookLibrary = new HashMap<>();

    /** 获取单例 */
    public static User get() {
        if (instance == null)
            instance = new User();
        return instance;
    }

    /** 是否登录 */
    public boolean isLoaded () {
        return isLoaded;
    }

    /** 登录 */
    public void Login (String content) {
        parseJSONWithJSONObject(content);
        isLoaded = true;
    }

    /** 解析json */
    private void parseJSONWithJSONObject (String jsonData) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                account = jsonObject.getString("account");
                password = jsonObject.getString("password");
                userName = jsonObject.getString("name");
                userExp = Integer.parseInt(jsonObject.getString("exp"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 退出登录 */
    public void Logout () {
        isLoaded = false;
    }

    /** 获取当前用户名字 */
    public String getUserName () {
        if (isLoaded) {
            return userName;
        } else {
            return "点击登录";
        }
    }

    /** 查找书籍 */
    public List<Book> searchBooks (String value) {
        List<Book> bList = new ArrayList<>();
        Iterator iter = bookLibrary.entrySet().iterator();
        while (iter.hasNext()) {
            HashMap.Entry entry = (HashMap.Entry)iter.next();
            Book book = (Book)entry.getValue();
            String bookName = book.getBookName();
            // 比较
            if (bookName.contains(value)) {
                bList.add(book);
            }
        }
        return bList;
    }

    /** 获得用户书签 */
    public List<BookMark> getBookMarkList() {
        return bookMarkList;
    }

    /** 获取某本书的阅读历史 如果没有读过则返回null */
    public BookMark getBookHistory (String bookId) {
        for (BookMark bm : historyList) {
            if (bm.bookId.equals(bookId)) {
                return bm;
            }
        }
        return null;
    }

    /** 获取所有的阅读记录 */
    public List<BookMark> getReadRecord () {
        return historyList;
    }
    
    /** 添加某本书的阅读记录 */
    public void addReadHistory (String bookId, int chapterId, float process) {
        BookMark bm;
        // 没有这本书的记录
        if ((bm = getBookHistory(bookId)) == null) {
            bm = new BookMark(bookId, chapterId, "", process, "");
            historyList.add(bm);
        } else {
            bm.chapterId = chapterId;
            bm.process = process;
        }
    }

    /** 添加书签 */
    public void addBookMark (String bookId, int chapterId, String firstLine, float process, String date) {
        bookMarkList.add(new BookMark(bookId, chapterId, firstLine, process, date));
    }

    /** 删除书签 */
    public void removeBookMark (int index) {
        if (index <0 || index >= bookMarkList.size())
            return;
        bookMarkList.remove(index);
    }

    /** 删除多书签 */
    public void removeBookMarks (List<BookMark> list) {
        if (bookMarkList.removeAll(list)) {
        }
    }

    /** 书签 */
    public static class BookMark {
        public String account;
        /** 书本id */
        public String bookId;
        /** 章节号 */
        public int chapterId;
        /** 显示的文字 */
        public String firstLine;
        /** 进度 主要用于跳转到某一页的计算 */
        public float process;
        /** 日期时间 */
        public String date;

        public BookMark(String bookId, int chapterId, String firstLine, float process, String date) {
            this.bookId = bookId;
            this.chapterId = chapterId;
            this.firstLine = firstLine;
            this.process = process;
            this.date = date;
        }
        /** Gson解析获得书签列表 */
        public static List<BookMark> getBookMarkWithGSON (String jsonData) {
            Gson gson = new Gson();
            List<BookMark> bookList = gson.fromJson(jsonData, new TypeToken<List<BookMark>>(){}.getType());
            return  bookList;
        }
        // region Get Set
        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getBookId() {
            return bookId;
        }

        public void setBookId(String bookId) {
            this.bookId = bookId;
        }

        public int getChapterId() {
            return chapterId;
        }

        public void setChapterId(int chapterId) {
            this.chapterId = chapterId;
        }

        public String getFirstLine() {
            return firstLine;
        }

        public void setFirstLine(String firstLine) {
            this.firstLine = firstLine;
        }

        public float getProcess() {
            return process;
        }

        public void setProcess(float process) {
            this.process = process;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
        // endregion
    }

    /** 清空书签 */
    public void clearBookMark () {
        bookMarkList.clear();
        // 清空本地数据
        if (editor == null)
            return;
        editor.putInt("userHistoryListSize", 0);
        editor.apply();
        // 如果登录
        if (isLoaded) {

        }
    }

    /** 下载所有书的书名 */
    public void getBooks () {
        RequestBody requestBody = new FormBody.Builder().build();
        HttpUtil.sendOkHttpRequest(User.IP_ADD_GET_BOOKS, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {}

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String content = response.body().string().trim();
                content = content.replaceAll("\n|\r| ", "");
                //System.out.println("----" + bookName + "----");
                if (content.equals(""))
                    return;
                List<Book> bookList = Book.getBookListWithGSON(content);
                if (bookLibrary == null)
                    bookLibrary = new HashMap<String, Book>();
                else
                    bookLibrary.clear();
                for (Book b : bookList)
                    bookLibrary.put(b.getBookId(), b);
            }
        }, requestBody);
    }

    /** 根据id获得某本书的书名 */
    public String getBookName (String bookId) {
        if (bookLibrary == null)
            return "未知";
        Book book = (Book)bookLibrary.get(bookId);
        if (book == null)
            return "未知";
        String result = book.getBookName();
        return result;
    }

    /** 根据id获得书的作者 */
    public String getBookAuthor (String bookId) {
        if (bookLibrary == null)
            return "匿名";
        Book book = (Book)bookLibrary.get(bookId);
        if (book == null)
            return "匿名";
        String result = book.getBookAuthor();
        return result;
    }

    // region save&load
    /** 保存设置到本地 */
    public void saveSetLocal () {
        if (editor == null)
            return;
        // 保存夜间模式
        editor.putBoolean("NightMode", PageRender.IS_NIGHT_MODE);
        // 保存字体大小
        editor.putInt("FontSize", PageRender.FONT_SIZE);
        // 保存字体颜色
        editor.putInt("FontSize", PageRender.FONT_COLOR);
        // 保存背景id
        editor.putInt("Background", PageRender.BACKGROUND_ID);
        editor.apply();
    }

    /** 加载设置到本地 */
    public void loadSetLocal () {
        if (pref == null)
            return;
        PageRender.FONT_SIZE = pref.getInt("FontSize", 20);
        PageRender.FONT_COLOR = pref.getInt("FontSize", Color.BLACK);
        PageRender.BACKGROUND_ID = pref.getInt("Background", 1);
        PageRender.IS_NIGHT_MODE = pref.getBoolean("NightMode", false);
    }

    /** 加载用户阅读情况的数据 */
    public void loadUserData (Context context) {
        // 如果未登录则加载本地数据
        // 如果登录则加载数据库数据
        // 有条件的话询问用户是否同步本地数据
        if (!isLoaded) {
            pref = PreferenceManager.getDefaultSharedPreferences(context);
            loadBookMarkLocal();
            loadReadHistoryLocal();

        } else  {
            loadBookMark();
            loadReadHistory();
        }
        loadSetLocal ();
    }

    /** 保存用户阅读情况的数据 */
    public void saveUserData (Context context) {
        System.out.println("saveUserData");
        // 如果是登录状态 则保存到数据库
        // 否则只保存到本地
        if (isLoaded) {
            saveBookMark();
            saveReadHistory();
        }
        saveBookMarkLocal();
        saveReadHistoryLocal();
        saveSetLocal ();
    }


    /** 保存用户书签到本地 */
    public void saveBookMarkLocal () {
        if (editor == null)
            return;
        int size = bookMarkList.size();
        editor.putInt("userBookMarkSize", size);
        for (int i = 0; i < size; i++) {
            BookMark bm = bookMarkList.get(i);
            editor.putString("bookId_" + i, bm.bookId);
            editor.putInt("chapterId_" + i, bm.chapterId);
            editor.putString("firstLine_" + i, bm.firstLine);
            editor.putFloat("process_" + i, bm.process);
            editor.putString("date_" + i, bm.date);
        }
        editor.apply();
    }

    /** 保存用户书签到数据库 */
    public void saveBookMark () {
        int delSize = bookMarkList.size();
        if (delSize == 0)
            return;
        FormBody.Builder build = new FormBody.Builder();
        build.add("mode", "0").add("size", "" + delSize).add("account", account);
        for (int i = 0; i < delSize; i++) {
            BookMark bm = bookMarkList.get(i);
            build.add("bookId_"     + i, bm.bookId)
                 .add("chapterId_"  + i, "" + bm.chapterId)
                 .add("firstLine_"  + i, bm.firstLine)
                 .add("process_"    + i, String.format("%.2f", bm.process))
                 .add("date_"       + i, bm.date);
        }
        RequestBody requestBody = build.build();
        HttpUtil.sendOkHttpRequest(User.IP_ADD_DEAL_BOOKMARKS, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.out.println("saveBookMark onFailure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("saveBookMark onResponse");
            }
        }, requestBody);
    }

    /** 加载用户本地书签 */
    public void loadBookMarkLocal () {
        if (pref == null)
            return;
        if (bookMarkList == null)
            bookMarkList = new ArrayList<>();
        else
            bookMarkList.clear();
        int size = pref.getInt("userBookMarkSize", 0);
        for (int i = 0; i < size; i++) {
            BookMark bm = new BookMark(
                    pref.getString("bookId_" + i, ""),
                    pref.getInt("chapterId_" + i, 0),
                    pref.getString("firstLine_" + i, ""),
                    pref.getFloat("process_" + i, 0f),
                    pref.getString("date_" + i, "")
                    );
            bookMarkList.add(bm);
        }
    }

    /** 加载用户数据库书签 */
    public void loadBookMark () {
        System.out.println("account----" + account + "----");
        RequestBody requestBody = new FormBody.Builder().add("mode", "getBookMarks").add("account", account).build();
        HttpUtil.sendOkHttpRequest(User.IP_ADD_DEAL_BOOKMARKS, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {}

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String content = response.body().string().trim();
                content = content.replaceAll("\n|\r| ", "");
                //System.out.println("----" + bookName + "----");
                if (content.equals(""))
                    return;
                bookMarkList = BookMark.getBookMarkWithGSON(content);
            }
        }, requestBody);
    }

    /** 保存阅读记录到本地 */
    public void saveReadHistoryLocal () {
        if (editor == null)
            return;
        int size = historyList.size();
        editor.putInt("userHistoryListSize", size);
        for (int i = 0; i < size; i++) {
            BookMark bm = historyList.get(i);
            editor.putString("bookId_" + i, bm.bookId);
            editor.putInt("chapterId_" + i, bm.chapterId);
            editor.putString("firstLine_" + i, bm.firstLine);
            editor.putFloat("process_" + i, bm.process);
            editor.putString("date_" + i, bm.date);
        }
        editor.apply();
    }

    /** 保存阅读记录到数据库 */
    public void saveReadHistory () {
        int delSize = historyList.size();
        if (delSize == 0)
            return;
        FormBody.Builder build = new FormBody.Builder();
        build.add("mode", "0").add("size", "" + delSize).add("account", account);
        for (int i = 0; i < delSize; i++) {
            BookMark bm = historyList.get(i);
            build.add("bookId_"     + i, bm.bookId)
                    .add("chapterId_"  + i, "" + bm.chapterId)
                    .add("process_"    + i, String.format("%.2f", bm.process));
        }
        RequestBody requestBody = build.build();
        HttpUtil.sendOkHttpRequest(User.IP_ADD_DEAL_READRECORD, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {}

            @Override
            public void onResponse(Call call, Response response) throws IOException {
            }
        }, requestBody);
    }

    /** 加载用户本地阅读记录 */
    public void loadReadHistoryLocal () {
        if (pref == null)
            return;
        if (historyList == null)
            historyList = new ArrayList<>();
        else
            historyList.clear();
        int size = pref.getInt("userHistoryListSize", 0);
        for (int i = 0; i < size; i++) {
            BookMark bm = new BookMark(
                    pref.getString("bookId_" + i, ""),
                    pref.getInt("chapterId_" + i, 0),
                    pref.getString("firstLine_" + i, ""),
                    pref.getFloat("process_" + i, 0f),
                    pref.getString("date_" + i, "")
            );
            historyList.add(bm);
        }
    }

    /** 加载用户数据库阅读记录 */
    public void loadReadHistory () {
        System.out.println("account----" + account + "----");
        RequestBody requestBody = new FormBody.Builder().add("mode", "getReadRecord").add("account", account).build();
        HttpUtil.sendOkHttpRequest(User.IP_ADD_DEAL_READRECORD, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {}

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String content = response.body().string().trim();
                content = content.replaceAll("\n|\r| ", "");
                //System.out.println("----" + bookName + "----");
                if (content.equals(""))
                    return;
                historyList = BookMark.getBookMarkWithGSON(content);
            }
        }, requestBody);
    }
    // endregion

}
