package myutil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ReadFromFile {
	
	public final static String DEFAULT_BOOK_PATH = "D:/Eclipse/MyWorkSpace/ch4/WebContent/Zhangyue/Book/";
	
	/**
     * 以字节为单位读取文件，常用于读二进制文件，如图片、声音、影像等文件。
     */
    public static void readFileByBytes(String fileName, boolean isbytes) {
        File file = new File(fileName);
        InputStream in = null;
        if (!isbytes) {
	        try {
	            System.out.println("以字节为单位读取文件内容，一次读一个字节：");
	            // 一次读一个字节
	            in = new FileInputStream(file);
	            int tempbyte;
	            while ((tempbyte = in.read()) != -1) {
	                System.out.write(tempbyte);
	            }
	            in.close();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
        } else {
        	try {
	            System.out.println("以字节为单位读取文件内容，一次读多个字节：");
	            // 一次读多个字节
	            byte[] tempbytes = new byte[100];
	            int byteread = 0;
	            in = new FileInputStream(fileName);
	            ReadFromFile.showAvailableBytes(in);
	            // 读入多个字节到字节数组中，byteread为一次读入的字节数
	            while ((byteread = in.read(tempbytes)) != -1) {
	                System.out.write(tempbytes, 0, byteread);
	            }
	        } catch (Exception e1) {
	            e1.printStackTrace();
	        } finally {
	            if (in != null) {
	                try {
	                    in.close();
	                } catch (IOException e1) {
	                }
	            }
	        }
        }
    }
    
    /**
     * 以字符为单位读取文件，常用于读文本，数字等类型的文件
     */
    public static void readFileByChars(String fileName) {
        File file = new File(fileName);
        Reader reader = null;
        try {
            System.out.println("以字符为单位读取文件内容，一次读一个字节：");
            // 一次读一个字符
            reader = new InputStreamReader(new FileInputStream(file));
            int tempchar;
            while ((tempchar = reader.read()) != -1) {
                // 对于windows下，\r\n这两个字符在一起时，表示一个换行。
                // 但如果这两个字符分开显示时，会换两次行。
                // 因此，屏蔽掉\r，或者屏蔽\n。否则，将会多出很多空行。
                if (((char) tempchar) != '\r') {
                    System.out.print((char) tempchar);
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            System.out.println("以字符为单位读取文件内容，一次读多个字节：");
            // 一次读多个字符
            char[] tempchars = new char[30];
            int charread = 0;
            reader = new InputStreamReader(new FileInputStream(fileName));
            // 读入多个字符到字符数组中，charread为一次读取字符数
            while ((charread = reader.read(tempchars)) != -1) {
                // 同样屏蔽掉\r不显示
                if ((charread == tempchars.length)
                        && (tempchars[tempchars.length - 1] != '\r')) {
                    System.out.print(tempchars);
                } else {
                    for (int i = 0; i < charread; i++) {
                        if (tempchars[i] == '\r') {
                            continue;
                        } else {
                            System.out.print(tempchars[i]);
                        }
                    }
                }
            }

        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }
    
    /** 读取某个文件下下每个txt文件的首行 对文件格式要求高chapter+数字 一定要是 */
    public static List<String> getFirstLines (String path) {
    	List<String> content = new ArrayList<>();
    	File file = new File(path);
    	if (!file.isDirectory())
    		return content;
    	File[] files = file.listFiles(new FilenameFilter () {
    		@Override
            public boolean accept(File file, String s) {
                return s.endsWith(".txt");
            }
    	});
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
            }
        });
        for (File f : fileList) {
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
    
    
    /** 以行为单位读取文件，常用于读面向行的格式化文件 */
    public static String readFileByLines(String fileName) {
        File file = new File(fileName);
        // 如果查找文件不存在或者是个路径就返回空字符串
        if (!file.exists() || file.isDirectory())
        	return "";
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
            	sb.append(tempString).append("\n");
                // 显示行号
                System.out.println("line " + line + ": " + tempString);
                line++;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return sb.toString();
    }
    
    /**
     * 随机读取文件内容
     */
    public static void readFileByRandomAccess(String fileName) {
        RandomAccessFile randomFile = null;
        try {
            System.out.println("随机读取一段文件内容：");
            // 打开一个随机访问文件流，按只读方式
            randomFile = new RandomAccessFile(fileName, "r");
            // 文件长度，字节数
            long fileLength = randomFile.length();
            // 读文件的起始位置
            int beginIndex = (fileLength > 4) ? 4 : 0;
            // 将读文件的开始位置移到beginIndex位置。
            randomFile.seek(beginIndex);
            byte[] bytes = new byte[10];
            int byteread = 0;
            // 一次读10个字节，如果文件内容不足10个字节，则读剩下的字节。
            // 将一次读取的字节数赋给byteread
            while ((byteread = randomFile.read(bytes)) != -1) {
                System.out.write(bytes, 0, byteread);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (randomFile != null) {
                try {
                    randomFile.close();
                } catch (IOException e1) {
                }
            }
        }
    }
    
    /**
     * 显示输入流中还剩的字节数
     */
    private static void showAvailableBytes(InputStream in) {
        try {
            System.out.println("当前字节输入流中的字节数为:" + in.available());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        String fileName = "D:/Eclipse/MyWorkSpace/ch4/WebContent/test.xml";
        ReadFromFile.readFileByBytes(fileName, true);
       // rf = ReadFromFile.readFileByBytes(fileName, true);
       // System.out.println(rf);
       // ReadFromFile.readFileByChars(fileName);
        String rf = ReadFromFile.readFileByLines(fileName);
        CountCharacter car = new CountCharacter();
        car.count(rf);
        //System.out.println("Result:\n" + rf);
       // ReadFromFile.readFileByRandomAccess(fileName);
    }
}
