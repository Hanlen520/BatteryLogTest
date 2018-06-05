package com.cfzz.abwsmt.BatteryTools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

//import org.apache.http.util.EncodingUtils;

//import com.cfzz.ablsp.AndroidBatteryLogSPActivity;

import android.util.Log;

public class BatteryQuantityUtils 
{
    private static final String TAG = "BatteryQuantityUtils";
    //获取电池电量值方法
    static public int getQuantityFile()//
    {    
        int BatteryQ = -1;
    	FileInputStream fin = null;
    	try {            
               		  			
        		//File fileName = new File("/sys/class/power_supply/battery/capacity"); //SP平台
    			File fileName = new File("/sys/class/power_supply/battery/capacity");//MTK平台
                if(fileName.exists()) {
                	Log.v("TAG","FileName exists!");
                    try{
                            fin = new FileInputStream(fileName);   
                            int length = fin.available();   
                            byte [] buffer = new byte[length];   
                            fin.read(buffer); 
                            Log.v("TAG", "Buffer:   " + buffer.toString());
                            String default_qt = new String(buffer, "UTF-8");   
                            BatteryQ = Integer.parseInt(default_qt.trim());
                            Log.v("TAG", "BatteryQ:   " + BatteryQ);
                    } catch(FileNotFoundException e) {
                        Log.e(TAG, "/sys/class/power_supply/battery/capacity FileNotFoundException : " + e.getMessage());
                    } catch(NumberFormatException e) {
                        Log.e(TAG, "NumberFormatException : " + e.getMessage());
                        e.printStackTrace();
                    }
                    finally {
                    	fin.close();
                    }                    
                } 
                else{
                	BatteryQ = -1;
                }
    		    		
        } catch(Exception e) {
            e.printStackTrace();
        }
		return BatteryQ;
        
    }
}