package com.cfzz.abwsmt.BatteryTools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
//import org.apache.http.util.EncodingUtils;
import android.util.Log;
//import com.cfzz.ablsp.R;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BatteryElectricUtils 
{
	private static final String TAG = "BatteryElectricUtils";
    static public int getElectricFile()//
    {    
        int BatteryE = 0;       
    	FileInputStream fin = null;
		Log.v(TAG,"getSupplyOnline() = " + BatteryStatusUtils.getSupplyOnline());
    	try {            
            //充电状态判断
    		if(BatteryStatusUtils.getSupplyOnline() == "USB"
			   ||BatteryStatusUtils.getSupplyOnline() == "AC")
    		{
				//File fileName = new File("/sys/class/power_supply/sprdfgu/fgu_current"); //SP展讯平台
    			File fileName = new File("/sys/class/power_supply/battery/BatteryAverageCurrent");//MTK联发科平台当前电流读取节点，AndroidO平台读取电流不准确
                if(fileName.exists()) {
                	Log.v(TAG,"FileName exists!");
                    try{
                            fin = new FileInputStream(fileName);   
                            int length = fin.available();   
                            byte [] buffer = new byte[length];   
                            fin.read(buffer); 
                            Log.v(TAG, "Buffer:   " + buffer.toString());
                            String default_ec = new String(buffer, "UTF-8");   
                            BatteryE = Integer.parseInt(default_ec.trim());
                            Log.v(TAG, "BatteryE:   " + BatteryE);
                    } catch(FileNotFoundException e) {
						Log.e(TAG, "/sys/class/power_supply/battery/BatteryAverageCurrent FileNotFoundException : " + e.getMessage());
                    } catch(NumberFormatException e) {
						Log.e(TAG, "NumberFormatException : " + e.getMessage());
                        e.printStackTrace();
                    }
                    finally {
                    	fin.close();
                    }
                } 
                else{
                	BatteryE = 0;
                }
    		}
    		//放电状态判断
    		else if(BatteryStatusUtils.getSupplyOnline() == "DisConnect")
    		{
    			/*关于电流流出配置可参考6737n common-driver分支ebb72f4f6a33f6e7fe7c941eeb3b62ef38b3c117记录*/
				File fileName = new File("/sys/hw_info/bat_out");//MTK平台6737上有做，其他平台需要驱动同事参考开发配置
				if(fileName.exists()) {
					Log.v(TAG,"FileName exists!");
					try{
							fin = new FileInputStream(fileName);   
							int length = fin.available();   
							byte [] buffer = new byte[length];   
							fin.read(buffer); 
							Log.v(TAG, "Buffer:   " + buffer.toString());
							String default_ec = new String(buffer, "UTF-8");
							String regEx="[^0-9]";   
							Pattern p = Pattern.compile(regEx);   
							Matcher m = p.matcher(default_ec);
							BatteryE = Integer.parseInt(m.replaceAll("").trim());
							Log.v(TAG, "BatteryE:   " + BatteryE);
					} catch(FileNotFoundException e) {
						Log.e(TAG, "/sys/hw_info/bat_out FileNotFoundException : " + e.getMessage());
					} catch(NumberFormatException e) {
						Log.e(TAG, "NumberFormatException : " + e.getMessage());
					}
					finally {
						fin.close();
					}
				}
				else{
					BatteryE = 0;
				}
    		}
        } catch(Exception e) {
            e.printStackTrace();
        }
    	return BatteryE;
    }
}