package com.cfzz.abwsmt.BatteryTools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
//import org.apache.http.util.EncodingUtils;
import android.util.Log;

public class BatteryStatusUtils {
	
	  public static String BatteryStatus; //
	  public static String SupplyOnline = null;//
	  public static String BatteryTemp; //
	  
	  public static String getBatteryStatus()
	  {
		  //int BatteryE = 0;       
	    	FileInputStream fin = null;
	    	try {            
  			
	        		File fileName = new File("/sys/class/power_supply/battery/health"); //SP
	    			//File fileName = new File("/sys/class/power_supply/battery/BatteryAverageCurrent");//MTK 
	                if(fileName.exists()) {
	                	Log.v("TAG","FileName exists!");
	                    try{
	                            fin = new FileInputStream(fileName);   
	                            int length = fin.available();   
	                            byte [] buffer = new byte[length];   
	                            fin.read(buffer); 
	                            Log.v("TAG", "Buffer:   " + buffer.toString());
	                            String default_st = new String(buffer, "UTF-8");   
	                            BatteryStatus = default_st.trim();
	                            
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
	                else
	                {
	                	BatteryStatus = null;
	                }
	    		
	    	}
	    	catch(Exception e){
	    		BatteryStatus = null;
	    	}
	      Log.v("TAG", "BatteryStatus:   " + BatteryStatus);
		  return BatteryStatus;
	  }
	  public static String getSupplyOnline()
	  {
		  int soac = 0, sousb = 0;
		  FileInputStream finac = null, finusb = null;
	    	try {            
			
	        		File fileName = new File("/sys/class/power_supply/ac/online"); //SP&MTK
	        		File fileName1 = new File("/sys/class/power_supply/usb/online"); //SP&MTK       		
	    			
	                
	                	Log.v("TAG","FileName exists!");
	                    try{
	                            finac = new FileInputStream(fileName);   
	                            int length = finac.available();   
	                            byte [] buffer = new byte[length];   
	                            finac.read(buffer); 
	                            Log.v("TAG", "Buffer:   " + buffer.toString());
	                            String default_so = new String(buffer, "UTF-8");   
	                            soac = Integer.parseInt(default_so.trim());
	                            if(soac==1)
	                            {
	                            	SupplyOnline = "AC";
	                            }
	                            
	                            
	                            //EC.setText("Charging Electric:   " + BatteryE + "mA");
	                        
	                    } catch(FileNotFoundException e) {
	                        //LogSprd.e("/sys/class/rtc/rtc0/default_time.");
	                    } catch(NumberFormatException e) {
	                        //LogSprd.e("!!!!!!!number format error.!!!!!!!!");
	                        e.printStackTrace();
	                    }
	                    finally {
	                    	finac.close();
	                    }
	                    
	                 
	               
	                	Log.v("TAG","FileName1 exists!");
	                    try{
	                            finusb = new FileInputStream(fileName1);   
	                            int length = finusb.available();   
	                            byte [] buffer = new byte[length];   
	                            finusb.read(buffer); 
	                            Log.v("TAG", "Buffer:   " + buffer.toString());
	                            String default_so = new String(buffer, "UTF-8");   
	                            sousb = Integer.parseInt(default_so.trim());
	                            if(sousb==1)
	                            {
	                            	SupplyOnline = "USB";
	                            }
	                            
	                            
	                            //EC.setText("Charging Electric:   " + BatteryE + "mA");
	                        
	                    } catch(FileNotFoundException e) {
	                        //LogSprd.e("/sys/class/rtc/rtc0/default_time.");
	                    } catch(NumberFormatException e) {
	                        //LogSprd.e("!!!!!!!number format error.!!!!!!!!");
	                        e.printStackTrace();
	                    }
	                    finally {
	                    	finusb.close();
	                    }
	                
	                
	    		
	    	       
	                if(soac == 0 && sousb == 0)
	                {
	                	SupplyOnline = "DisConnect";
	                }
	                
	    	}
	    	catch(Exception e){
	    		SupplyOnline = null;
	    	}
	      Log.v("TAG", "SupplyOnline:   " + SupplyOnline);
	      //Log.v("TAG", "SupplyOnline:   " + SupplyOnline);
		  return SupplyOnline;
	  }
	  public static String getBatteryTemp()
	  {
		  FileInputStream fin = null;
	    	try {            
			
	        		File fileName = new File("/sys/class/power_supply/battery/status"); //SP&MTK
	    			 
	                if(fileName.exists()) {
	                	Log.v("TAG","FileName exists!");
	                    try{
	                            fin = new FileInputStream(fileName);   
	                            int length = fin.available();   
	                            byte [] buffer = new byte[length];   
	                            fin.read(buffer); 
	                            Log.v("TAG", "Buffer:   " + buffer.toString());
	                            String default_st = new String(buffer, "UTF-8");   
	                            BatteryTemp = default_st.trim();
	                            Log.v("TAG", "BatteryTemp:   " + BatteryTemp);
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
	                else
	                {
	                	BatteryTemp = null;
	                }
	    		
	    	}
	    	catch(Exception e){
	    		BatteryTemp = null;
	    	}
	      Log.v("TAG", "BatteryTemp:   " + BatteryTemp);
		  return BatteryTemp;
	  }

}
