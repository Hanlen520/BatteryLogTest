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

	//static AndroidBatteryLogSPActivity mAndroidBatteryLogSPActivity;
	private static final String TAG = "BatteryElectricUtils";

    
    static public int getElectricFile()//
    {    
        int BatteryE = 0;       
    	FileInputStream fin = null;
		Log.v(TAG,"getSupplyOnline() = " + BatteryStatusUtils.getSupplyOnline());
    	try {            
            
    		if(BatteryStatusUtils.getSupplyOnline() == "USB"
			   ||BatteryStatusUtils.getSupplyOnline() == "AC")
    		{
				File fileName = new File("/sys/class/power_supply/sprdfgu/fgu_current"); //SP
    			//File fileName = new File("/sys/class/power_supply/battery/BatteryAverageCurrent");//MTK
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
                            //EC.setText("Charging Electric:   " + BatteryE + "mA");
                        
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
                	
                	BatteryE = 0;
                }
    		}
    		else if(BatteryStatusUtils.getSupplyOnline() == "DisConnect")
    		{
				File fileName = new File("/sys/hw_info/bat_out");//MTK   		
				if(fileName.exists()) {
					Log.v(TAG,"FileName exists!");
					try{
							fin = new FileInputStream(fileName);   
							int length = fin.available();   
							byte [] buffer = new byte[length];   
							fin.read(buffer); 
							Log.v(TAG, "Buffer:   " + buffer.toString());
							String default_ec = new String(buffer, "UTF-8"); 
							//Read the number of battery current
							String regEx="[^0-9]";   
							Pattern p = Pattern.compile(regEx);   
							Matcher m = p.matcher(default_ec);   
							
							BatteryE = Integer.parseInt(m.replaceAll("").trim());
							Log.v(TAG, "BatteryE:   " + BatteryE);
							//EC.setText("Discharging Electric:   " + BatteryE + "mA");
						
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
					
					BatteryE = 0;
				}
    		}
    		
        } catch(Exception e) {
            e.printStackTrace();
        }
    	return BatteryE; 
    	
    }
    
    public static void WriteTxtFile(String strContent, String strFilePath)
    {
      
      //String strContent = "0x1";
      try {
           File file = new File(strFilePath);
           if (!file.exists()) {
            Log.d("TestFile", "Create the file:" + strFilePath);
            file.createNewFile();
           }
           RandomAccessFile raf = new RandomAccessFile(file, "rw");
           raf.seek(file.length());
           raf.write(strContent.getBytes());
           raf.close();
      } catch (Exception e) {
           Log.e("TestFile", "Error on write File.");
          }
    }
}