package com.yxy.util.encypt.algor;

public class ConstantGuard {
	
	/**
     * 高位置
     * @param str
     * @return
     */
    public static String encode(String str){
    	if(str == null){
    		return null;
    	}
    	byte[] k = str.getBytes();
    	for(int i = 0; i < k.length; i++){ 
			k[i] = (byte) (k[i] ^ 0xFF); 
		}
    	return Hex.byte2hex(k);
    }
    
    /**
     * 还原高位置
     * @param str
     * @return
     */
    public static String decode(String str){
    	byte[] x = Hex.hex2byte(str); 
		for(int i = 0; i < x.length; i++){
			x[i] = (byte) (~x[i]);
		} 
		return new String(x);
    }
}
