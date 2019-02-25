package com.example.asus.zhangyue.myutil;

import android.os.Environment;
import android.support.annotation.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 文件输入输出工具
 */

public class FileOperation {

    /** 默认书籍文件存放路径 */
    public final static String DEFAULT_BOOK_SAVE_PATH = "/Zhangyue/User/Books/";

    /** 将未下载到本地的书籍章节内容保存下来 */
    public static void writeBook (final String content, final String bookId, final int chapterId) {
        // 开线程下载
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    write(content, DEFAULT_BOOK_SAVE_PATH + bookId + "/chapter" + chapterId + ".txt");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    static class ContentFilenameFilter implements FilenameFilter {
        @Override
        public boolean accept(File file, String s) {
            return s.contains("chapter") && s.endsWith(".txt");
        }
    }
    /** 读取某个书籍文件夹下所有的txt类型文件里内容的第一行作为目录 */
    public static List<String> loadBookContent (String bookId, @Nullable List<String> fileNames) {
        String path = Environment.getExternalStorageDirectory()+ DEFAULT_BOOK_SAVE_PATH + bookId + "/";
        File file = new File(path);
        List<String> content = new ArrayList<>();
        // 不存在则创建路径
        if (!file.exists()) {
            file.mkdirs();
            return content;
        }
        if (!file.isDirectory())
            return content;
        File[] files = file.listFiles(new ContentFilenameFilter());
        if (files == null)
            return content;
        // 按文件名排序
        List<File> fileList = Arrays.asList(files);
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                if (o1.isDirectory() && o2.isFile())
                    return -1;
                if (o1.isFile() && o2.isDirectory())
                    return 1;
                int num1 = Integer.parseInt(o1.getName().replaceAll("chapter|\\.txt", ""));
                int num2 = Integer.parseInt(o2.getName().replaceAll("chapter|\\.txt", ""));
                int res = num1 - num2;
                if (res > 0)
                    return 1;
                else if (res < 0)
                    return -1;
                else
                    return 0;
                //return o1.getName().compareTo(o2.getName());
            }
        });

        for (File f : fileList) {
            fileNames.add(f.getName().replace(".txt", ""));
            // 开始读取每一个文件的首行
            try {
                //创建一个带缓冲区的输入流
                BufferedReader reader = new BufferedReader(new FileReader(f));
                String tempString = null;
                // 读一行如果不为空就结束
                while ((tempString = reader.readLine()) != null) {
                    if (!tempString.trim().equals(""))
                        break;
                }
                reader.close();
                // 如果一行也没读出来
                if (tempString != null)
                    content.add(tempString);
                else
                    content.add("");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content;
    }

    /** 查看是否存在某本书 */
    public static boolean isExistsBook (String bookId) {
        String path = Environment.getExternalStorageDirectory()+ DEFAULT_BOOK_SAVE_PATH + bookId + "/";
        File file = new File(path);
        return file.exists();
    }

    /** 读取已下载的某本书的某一个章节 如果不存在则返回空字符串 */
    public static String loadBook (String bookId, String chapterId) {
        String content = "";
        String path = DEFAULT_BOOK_SAVE_PATH + bookId + "/" + chapterId + ".txt";
        File file = new File(Environment.getExternalStorageDirectory() + path);
        if (file.exists()) {
            try {
                content = read(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return content;
    }

    /** 判断SDCard是否存在 [当没有外挂SD卡时，内置ROM也被识别为存在sd卡] */
    public static boolean isSdCardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /** 获取SD卡根目录路径 不存在返回null */
    public static String getSdCardPath() {
        boolean exist = isSdCardExist();
        String sdpath = null;
        if (exist) {
            sdpath = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return sdpath;
    }

    /** 获取默认的文件路径 不存在返回null
     * @param fileName
     * */
    public static String getDefaultFilePath(String fileName) {
        String filepath = null;
        File file = new File(Environment.getExternalStorageDirectory(), fileName);
        if (file.exists()) {
            filepath = file.getAbsolutePath();
        }
        return filepath;
    }

    /** 判断路径是否存在 如果不存在则创建路径
     * @param path 表示已经在sd卡下的路径
     * */
    public static boolean isFolderExists (String path) {
        File file = new File(Environment.getExternalStorageDirectory() + path);
        if (!file.exists()) {
            return file.mkdirs();
        }
        return true;
    }

    /** 从SD根目录下读取文件
     * @param flieName 文件名
     * */
    public static String read(String flieName) throws IOException {
        // 创建一个带缓冲区的输出流
        String state = Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED)) {
            File SDPath = Environment.getExternalStorageDirectory(); // SD根目录
            File file = new File(SDPath, flieName);
            if (!file.exists() || file.isDirectory()) {
                return "";
            }
            //创建一个带缓冲区的输入流
            FileInputStream bis = new FileInputStream(file);
            // 判断格式
            String type;
            //java.io.InputStream ios = new FileInputStream(file);
            //byte[] b = new byte[3];
            //ios.read(b);
            //ios.close();
            //if(b[0] == -17 && b[1] == -69 && b[2] == -65)
                type = "utf-8";
            //else
             //   type = "gbk";
            InputStreamReader reader = new InputStreamReader(bis, type);
            int len;
            char[] buffer = new char[bis.available()];
            while ((len = reader.read()) != -1) {
                reader.read(buffer);
            }
            reader.close();
            bis.close();
            return new String(buffer);
        }
        return "";
    }

    /** 将文件写入SD卡根目录的某个文件 覆盖写入
     * @param content 需要存写的内容
     * @param fileName 文件名
     * */
    public static boolean write(String content,String fileName) throws IOException {
        //创建一个带缓冲区的输出流
        String state = Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED)){
            File SDPath = Environment.getExternalStorageDirectory();//SD根目录
            File file = new File(SDPath, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter writer = new OutputStreamWriter(fos,"utf-8");
            writer.write(content);
            writer.close();
            fos.close();
            return true;
        }
        return false;
    }

}
