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
     * ���ֽ�Ϊ��λ��ȡ�ļ��������ڶ��������ļ�����ͼƬ��������Ӱ����ļ���
     */
    public static void readFileByBytes(String fileName, boolean isbytes) {
        File file = new File(fileName);
        InputStream in = null;
        if (!isbytes) {
	        try {
	            System.out.println("���ֽ�Ϊ��λ��ȡ�ļ����ݣ�һ�ζ�һ���ֽڣ�");
	            // һ�ζ�һ���ֽ�
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
	            System.out.println("���ֽ�Ϊ��λ��ȡ�ļ����ݣ�һ�ζ�����ֽڣ�");
	            // һ�ζ�����ֽ�
	            byte[] tempbytes = new byte[100];
	            int byteread = 0;
	            in = new FileInputStream(fileName);
	            ReadFromFile.showAvailableBytes(in);
	            // �������ֽڵ��ֽ������У�bytereadΪһ�ζ�����ֽ���
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
     * ���ַ�Ϊ��λ��ȡ�ļ��������ڶ��ı������ֵ����͵��ļ�
     */
    public static void readFileByChars(String fileName) {
        File file = new File(fileName);
        Reader reader = null;
        try {
            System.out.println("���ַ�Ϊ��λ��ȡ�ļ����ݣ�һ�ζ�һ���ֽڣ�");
            // һ�ζ�һ���ַ�
            reader = new InputStreamReader(new FileInputStream(file));
            int tempchar;
            while ((tempchar = reader.read()) != -1) {
                // ����windows�£�\r\n�������ַ���һ��ʱ����ʾһ�����С�
                // ������������ַ��ֿ���ʾʱ���ỻ�����С�
                // ��ˣ����ε�\r����������\n�����򣬽������ܶ���С�
                if (((char) tempchar) != '\r') {
                    System.out.print((char) tempchar);
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            System.out.println("���ַ�Ϊ��λ��ȡ�ļ����ݣ�һ�ζ�����ֽڣ�");
            // һ�ζ�����ַ�
            char[] tempchars = new char[30];
            int charread = 0;
            reader = new InputStreamReader(new FileInputStream(fileName));
            // �������ַ����ַ������У�charreadΪһ�ζ�ȡ�ַ���
            while ((charread = reader.read(tempchars)) != -1) {
                // ͬ�����ε�\r����ʾ
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
    
    /** ��ȡĳ���ļ�����ÿ��txt�ļ������� ���ļ���ʽҪ���chapter+���� һ��Ҫ�� */
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
    	// ���ļ�������
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
            // ��ʼ��ȡÿһ���ļ�������
            try {
                //����һ������������������
                BufferedReader reader = new BufferedReader(new FileReader(f));
                String tempString = null;
                // ��һ�������Ϊ�վͽ���
                while ((tempString = reader.readLine()) != null) {
                    if (!tempString.trim().equals(""))
                        break;
                }
                reader.close();
                // ���һ��Ҳû������
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
    
    
    /** ����Ϊ��λ��ȡ�ļ��������ڶ������еĸ�ʽ���ļ� */
    public static String readFileByLines(String fileName) {
        File file = new File(fileName);
        // ��������ļ������ڻ����Ǹ�·���ͷ��ؿ��ַ���
        if (!file.exists() || file.isDirectory())
        	return "";
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            System.out.println("����Ϊ��λ��ȡ�ļ����ݣ�һ�ζ�һ���У�");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // һ�ζ���һ�У�ֱ������nullΪ�ļ�����
            while ((tempString = reader.readLine()) != null) {
            	sb.append(tempString).append("\n");
                // ��ʾ�к�
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
     * �����ȡ�ļ�����
     */
    public static void readFileByRandomAccess(String fileName) {
        RandomAccessFile randomFile = null;
        try {
            System.out.println("�����ȡһ���ļ����ݣ�");
            // ��һ����������ļ�������ֻ����ʽ
            randomFile = new RandomAccessFile(fileName, "r");
            // �ļ����ȣ��ֽ���
            long fileLength = randomFile.length();
            // ���ļ�����ʼλ��
            int beginIndex = (fileLength > 4) ? 4 : 0;
            // �����ļ��Ŀ�ʼλ���Ƶ�beginIndexλ�á�
            randomFile.seek(beginIndex);
            byte[] bytes = new byte[10];
            int byteread = 0;
            // һ�ζ�10���ֽڣ�����ļ����ݲ���10���ֽڣ����ʣ�µ��ֽڡ�
            // ��һ�ζ�ȡ���ֽ�������byteread
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
     * ��ʾ�������л�ʣ���ֽ���
     */
    private static void showAvailableBytes(InputStream in) {
        try {
            System.out.println("��ǰ�ֽ��������е��ֽ���Ϊ:" + in.available());
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
