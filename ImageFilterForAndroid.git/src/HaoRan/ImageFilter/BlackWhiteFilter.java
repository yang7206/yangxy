/* 
 * HaoRan ImageFilter Classes v0.1
 * Copyright (C) 2012 Zhenjun Dai
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation.
 */

package HaoRan.ImageFilter;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.media.ThumbnailUtils;

/**
 * 
 * @author daizhj
 *
 */
public class BlackWhiteFilter implements IImageFilter {

    //@Override
    public Image process(Image imageIn) {
        int r,g,b,corfinal;
        for (int x = 0; x < imageIn.getWidth(); x++) {
            for (int y = 0; y < imageIn.getHeight(); y++) {
                    r = imageIn.getRComponent(x, y);
                    g = imageIn.getGComponent(x, y);
                    b = imageIn.getBComponent(x, y);
                    corfinal = (int)((r*0.3)+(b*0.59)+(g*0.11));
                    imageIn.setPixelColor(x,y,corfinal,corfinal,corfinal);
            }
        }
        return imageIn;
    }
    
    
    
    /** 
    * 将彩色图转换为黑白图 
    *  
    * @param 位图 
    * @return 返回转换好的位图 
    */  
    public static Bitmap convertToBlackWhite(Bitmap bmp) {  
        int width = bmp.getWidth(); // 获取位图的宽  
        int height = bmp.getHeight(); // 获取位图的高  
        int[] pixels = new int[width * height]; // 通过位图的大小创建像素点数组  
  
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);  
        int alpha = 0xFF << 24;  
        for (int i = 0; i < height; i++) {  
            for (int j = 0; j < width; j++) {  
                int grey = pixels[width * i + j];  
  
                int red = ((grey & 0x00FF0000) >> 16);  
                int green = ((grey & 0x0000FF00) >> 8);  
                int blue = (grey & 0x000000FF);  
  
                grey = (int) (red * 0.3 + green * 0.59 + blue * 0.11);  
                grey = alpha | (grey << 16) | (grey << 8) | grey;  
                pixels[width * i + j] = grey;  
            }  
        }  
        Bitmap newBmp = Bitmap.createBitmap(width, height, Config.RGB_565);  
  
        newBmp.setPixels(pixels, 0, width, 0, 0, width, height);  
  
        Bitmap resizeBmp = ThumbnailUtils.extractThumbnail(newBmp, 380, 460);  
        return resizeBmp;  
    } 

}

