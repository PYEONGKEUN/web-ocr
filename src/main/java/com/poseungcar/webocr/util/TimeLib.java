package com.poseungcar.webocr.util;

import java.text.SimpleDateFormat;
import java.util.Date;



/**
 * TimeLib
 * 현재 시간을 정해진 형식에 맞게 리턴
 */
public class TimeLib {

    public TimeLib(){

    }

    public static String getCurrDateTime(){
        Date dt = new java.util.Date();

        SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String currentTime = sdf.format(dt);

        return currentTime;
        
    }
    public static void main(String[] args) {
        System.out.println(TimeLib.getCurrDateTime());
    }
    


}