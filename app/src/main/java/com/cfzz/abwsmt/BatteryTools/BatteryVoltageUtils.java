package com.cfzz.abwsmt.BatteryTools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

//import org.apache.http.util.EncodingUtils;

//import com.cfzz.ablsp.AndroidBatteryLogSPActivity;

import android.util.Log;

public class BatteryVoltageUtils 
{

	//static AndroidBatteryLogSPActivity mAndroidBatteryLogSPActivity;


    static public int getVoltageFile()//
    {    
        int BatteryV = 0;
        FileInputStream fin = null;
    	try {

                File fileName = new File("/sys/class/power_supply/sprdfgu/fgu_vol"); //SP
    			//File fileName = new File("/sys/class/power_supply/battery/batt_vol");//MTK
                if(fileName.exists()) {
                	Log.v("TAG","FileName exists!");
                    try{
                            fin = new FileInputStream(fileName);   
                            int length = fin.available();   
                            byte [] buffer = new byte[length];   
                            fin.read(buffer); 
                            Log.v("TAG", "Buffer:   " + buffer.toString());
                            String default_ve = new String(buffer, "UTF-8");   
                            BatteryV = Integer.parseInt(default_ve.trim());
							BatteryV = BatteryV/10;//for Android O
                            Log.v("TAG", "BatteryV:   " + BatteryV);
                            //EC.setText("Charging Electric:   " + BatteryT + "鈩�);
                        
                    } catch(FileNotFoundException e) {
                        //LogSprd.e("/sys/class/rtc/rtc0/default_time.");
                    } catch(NumberFormatException e) {
                        //LogSprd.e("!!!!!!!number format error.!!!!!!!!");
                        e.printStackTrace();
                    }
                    finally {
                    	fin.close();
                    }
                    
                } 
                else{
                	
                	BatteryV = 0;
                }
    		    		
        } catch(Exception e) {
            e.printStackTrace();
        }
    	return BatteryV;
    }
}