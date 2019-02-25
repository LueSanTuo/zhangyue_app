package com.example.asus.zhangyue.Data;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * 书籍
 */

public class Book {

    private String bookId;
    private String bookName;
    private String bookAuthor;

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookId='" + bookId + '\'' +
                ", bookName='" + bookName + '\'' +
                ", bookAuthor='" + bookAuthor + '\'' +
                '}';
    }

    /** Gson解析获得书籍列表 */
    public static List<Book> getBookListWithGSON (String jsonData) {
        Gson gson = new Gson();
        List<Book> bookList = gson.fromJson(jsonData, new TypeToken<List<Book>>(){}.getType());
        return  bookList;
    }
}
