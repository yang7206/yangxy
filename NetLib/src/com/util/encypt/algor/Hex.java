package com.util.encypt.algor;

/**
 * 16�������ֽ�ת��
 * @author Administrator
 *
 */
public class Hex {

	private static char[] hexChar = { '0', '1', '2', '3', '4', '5', '6', '7',   
        '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	   
    
    /**
     * 16����תΪ�ֽ�
     * @param hex
     * @return
     * @throws IllegalArgumentException
     */
    public static byte[] hex2byte(String hex) throws IllegalArgumentException {
    	char[] arr = hex.toCharArray();
        if (arr.length % 2 != 0) {   
            throw new IllegalArgumentException();   
        }   
        byte[] b = new byte[arr.length / 2];   
        for (int n = 0; n < arr.length; n += 2) {
        	String value = new String(arr, n, 2);
        	b[n/2] = (byte) Integer.parseInt(value, 16);
        }
        return b;   
    }
     
	
    /**
	 * �ֽ�תΪ16����
	 * @param b
	 * @return
	 */
    public static String byte2hex(byte[] b) {   
        StringBuilder sb = new StringBuilder(b.length * 2);   
        for (int i = 0; i < b.length; i++) {   
            sb.append(hexChar[(b[i] & 0xf0) >>> 4]);   
            sb.append(hexChar[b[i] & 0x0f]);   
        }   
        return sb.toString();   
    }
}
