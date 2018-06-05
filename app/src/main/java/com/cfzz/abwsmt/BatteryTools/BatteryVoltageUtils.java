package com.cfzz.abwsmt.BatteryTools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

//import org.apache.http.util.EncodingUtils;

//import com.cfzz.ablsp.AndroidBatteryLogSPActivity;

import android.util.Log;

public class BatteryVoltageUtils 
{
    private static final String TAG = "BatteryVoltageUtils";
    //获取电池电压信息
    static public int getVoltageFile()//
    {    
        int BatteryV = 0;
        FileInputStream fin = null;
    	try {
                //File fileName = new File("/sys/class/power_supply/sprdfgu/fgu_vol"); //SP平台带负载电压
                /*添加开路电压读取节点可参考7731c平台common_driver分支7aa7bcf72ecfa4670d70f5ee8c5ea232de9d85e1，一般调节曲线按开路电压更为准确*/
                //File fileName = new File("/sys/class/power_supply/sprdfgu/fgu_ocv_vol"); //SP平台开路电压
    			File fileName = new File("/sys/class/power_supply/battery/batt_vol");//MTK平台带负载电压
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
                            Log.v("TAG", "BatteryV:   " + BatteryV);
                    } catch(FileNotFoundException e) {
                        Log.e(TAG, "/sys/class/power_supply/battery/batt_vol FileNotFoundException : " + e.getMessage());
                    } catch(NumberFormatException e) {
                        Log.e(TAG, "NumberFormatException : " + e.getMessage());
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
    	return BatteryV/1000;//Android O平台
    }
}