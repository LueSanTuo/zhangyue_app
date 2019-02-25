package myutil;

public class CountCharacter {
	/**�����ַ� */
    private int chCharacter = 0;
    
    /**Ӣ���ַ� */
    private int enCharacter = 0;
    
    /**�ո� */
    private int spaceCharacter = 0;
    
    /**���� */
    private int numberCharacter = 0;
    
    /**�����ַ� */
    private int otherCharacter = 0;
    
    /***
     * ͳ���ַ��������ģ�Ӣ�ģ����֣��ո���ַ�����
     * @param str ��Ҫͳ�Ƶ��ַ���
     */
    public void count(String str) {
        if (null == str || str.equals("")) {
            System.out.println("�ַ���Ϊ��");
            return;
        }
        chCharacter = 0;
        enCharacter = 0;
        spaceCharacter = 0;
        numberCharacter = 0;
        otherCharacter = 0;
        
        for (int i = 0; i < str.length(); i++) {
            char tmp = str.charAt(i);
            if ((tmp >= 'A' && tmp <= 'Z') || (tmp >= 'a' && tmp <= 'z')) {
                enCharacter++;
            } else if ((tmp >= '0') && (tmp <= '9')) {
                numberCharacter++;
            } else if (tmp ==' ') {
                spaceCharacter++;
            } else if (isChinese(tmp)) {
                chCharacter++;
            } else {
                otherCharacter++;
            }
        }/*
        System.out.println("�ַ���:" + str + "");
        System.out.println("�����ַ���:" + chCharacter);
        System.out.println("Ӣ���ַ���:" + enCharacter);
        System.out.println("������:" + numberCharacter);
        System.out.println("�ո���:" + spaceCharacter);
        System.out.println("�����ַ���:" + otherCharacter);*/
    }
    
    /***
     * �ж��ַ��Ƿ�Ϊ����
     * @param ch ��Ҫ�жϵ��ַ�
     * @return ���ķ���true�������ķ���false
     */
    private boolean isChinese(char ch) {
        //��ȡ���ַ���UniCodeBlock
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(ch);
        //  GENERAL_PUNCTUATION �ж����ĵġ���  
        //  CJK_SYMBOLS_AND_PUNCTUATION �ж����ĵġ���  
        //  HALFWIDTH_AND_FULLWIDTH_FORMS �ж����ĵģ��� 
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS 
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B 
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS 
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION) {
            //System.out.println(ch + " ������");
            return true;
        }
        return false;
    }
    
    public static void main(String[] args) {
        String str = "adbs13��z��12���~3!a @x # $�� ��zs12 szsgss  1234@#���f�f�ۤ� ��������%����&*��������{}����";
        CountCharacter countCharacter = new CountCharacter();
        countCharacter.count(str);
        
    }
}
