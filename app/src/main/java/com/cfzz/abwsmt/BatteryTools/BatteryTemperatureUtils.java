package com.cfzz.abwsmt.BatteryTools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

//import org.apache.http.util.EncodingUtils;

//import com.cfzz.ablsp.AndroidBatteryLogSPActivity;

import android.util.Log;

public class BatteryTemperatureUtils 
{

	//static AndroidBatteryLogSPActivity mAndroidBatteryLogSPActivity;


    static public double getTemperatureFile()//
    {    
        double BatteryT = -2732;
        FileInputStream fin = null;
    	try {            
               		  			
        		File fileName = new File("/sys/class/power_supply/battery/temp"); //SP
    			//File fileName = new File("/sys/class/power_supply/battery/batt_temp");//MTK
                if(fileName.exists()) {
                	Log.v("TAG","FileName exists!");
                    try{
                            fin = new FileInputStream(fileName);   
                            int length = fin.available();   
                            byte [] buffer = new byte[length];   
                            fin.read(buffer); 
                            Log.v("TAG", "Buffer:   " + buffer.toString());
                            String default_tp = new String(buffer, "UTF-8");   
                            BatteryT = Double.parseDouble(default_tp.trim());
                            Log.v("TAG", "BatteryT:   " + BatteryT);
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
                	
                	BatteryT = -2732;
                }
    		    		
        } catch(Exception e) {
            e.printStackTrace();
        }
    	return BatteryT;
    }
}