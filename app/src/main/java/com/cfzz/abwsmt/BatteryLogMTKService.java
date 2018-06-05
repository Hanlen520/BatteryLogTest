package com.cfzz.abwsmt;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.cfzz.abwsmt.BatteryTools.BatteryElectricUtils;
import com.cfzz.abwsmt.BatteryTools.BatteryQuantityUtils;
import com.cfzz.abwsmt.BatteryTools.BatteryStatusUtils;
import com.cfzz.abwsmt.BatteryTools.BatteryTemperatureUtils;
import com.cfzz.abwsmt.BatteryTools.BatteryVoltageUtils;

public class BatteryLogMTKService extends Service
{
  public static final String CF_ACTION_BATTERY_INFORMATION = "com.cfzz.abwsmt";
  private static long mCurrentTime;
  //private final String TAG = "BatteryLogMTKService";
  private AlarmManager mAlarmManager = null;
  private Context mContext = null;
  private Handler handler;
  private PendingIntent mPendingIntent = null;
  private CFBatteryThread mThread = null;
  private PowerManager.WakeLock mWakeLock = null;
  private String ct = null;
  private int mq, mv, me;
  private double mt;
  private int nti;

  private void acquireWakeLock()
  {
    if (this.mWakeLock == null)
    {
      Log.v("BatteryLogMTKService", "Acquire wake lock");
      this.mWakeLock = ((PowerManager)this.mContext.getSystemService("power")).newWakeLock(1, getClass().getCanonicalName());
      this.mWakeLock.acquire();
    }
  }

  private void releaseWakeLock()
  {
    if ((this.mWakeLock != null) && (this.mWakeLock.isHeld()))
    {
      Log.d("BatteryLogMTKService", "Release wake lock");
      this.mWakeLock.release();
      this.mWakeLock = null;
    }
  }



  public IBinder onBind(Intent paramIntent)
  {
    return null;
  }

  public void onCreate()
  {
    super.onCreate();
    Log.v("BatteryLogMTKService", "BatteryLogMTKService->onCreate");
    this.mContext = getApplicationContext();
    //this.mFile = createFile();
    Intent localIntent = new Intent("com.cfzz.abws");
    this.mPendingIntent = PendingIntent.getBroadcast(this.mContext, 0, localIntent, 134217728);
    this.mAlarmManager = ((AlarmManager)this.mContext.getSystemService("alarm"));
    MTKBatteryReceiver localMTKBatteryReceiver = new MTKBatteryReceiver();
    IntentFilter localIntentFilter = new IntentFilter("com.cfzz.abws");
    this.mContext.registerReceiver(localMTKBatteryReceiver, localIntentFilter);
    acquireWakeLock();
    //Log.v("BatteryLogMTKService", "File Name:" + this.mFile.getName());
	
	
	
	handler = new Handler() {
			
			@Override
			public void handleMessage(Message msg) {
	
				// 
				// String Time = hour:minute:sec;			
			
				SimpleDateFormat hms = new SimpleDateFormat("HH:mm:ss",Locale.getDefault());// 
				ct = hms.format(new Date());// 
				mq = BatteryQuantityUtils.getQuantityFile();// 
				mv = BatteryVoltageUtils.getVoltageFile();// 
				me = BatteryElectricUtils.getElectricFile();//
				mt = BatteryTemperatureUtils.getTemperatureFile()/10.0;//
				String ms = BatteryStatusUtils.getBatteryTemp() + "-" + BatteryStatusUtils.getSupplyOnline();//
				if(me<80 && mq==99 && (BatteryStatusUtils.getSupplyOnline() != "DisConnect"))
				{
					ms = "Charging" + "-" + BatteryStatusUtils.getSupplyOnline();
				}

				ContentValues values = new ContentValues();
				//values.put("Time", hour + ":" + minute + ":" + sec);// 
				values.put("Time", ct);// 
				values.put("Quantity", mq);// 
				values.put("Voltage", mv);// 
				values.put("Electric", me);//
				values.put("Temperature", mt);// 
				values.put("Status", ms);//
				AndroidBatteryWithServiceActivity.db.insert("Temp", null, values);
				
				/*if(BatteryQuantityUtils.getQuantityFile() == 100 && BatteryStatusUtils.getBatteryTemp() == "Full")
				{
					int fme = (me + BatteryElectricUtils.getElectricFile())/2;
					if (AndroidBatteryWithServiceActivity.is == true && (fme>=0 && fme <=80))
					{
						//CFBatteryThread.interrupted();
						//state.callOnClick();//
						if(mThread != null)
						{
						   mThread.interrupt();
						   mThread = null;
						}
					}
				}*/
				
				/*try
				{
					db.rawQuery("select * from Temp", null);
				}
				catch(Exception e)
				{
					state.callOnClick();// 
					new AlertDialog.Builder(AndroidBatteryWithServiceActivity.this).setCancelable(false)
					.setPositiveButton("Fail to write data,please clear data", null).show();
				}*/
				
				super.handleMessage(msg);

			}
		};
  }

  public void onDestroy()
  {
    Log.v("BatteryLogMTKService", "BatteryLogMTKService->onDestroy");
    //String str = this.mFile.getName() + " is kept in sdcard.";
    //Toast.makeText(getApplication(), str, 1).show();
    this.mAlarmManager.cancel(this.mPendingIntent);
    this.mAlarmManager = null;
    if (this.mThread != null)
    {
    	this.mThread.interrupt();
        this.mThread = null;
    }
    releaseWakeLock();
    //
    super.onDestroy();
  }

  public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2)
  {
    Log.v("BatteryLogMTKService", "BatteryLogMTKService->onStartCommand");
    new BatteryLogMTKService();
    nti = AndroidBatteryWithServiceActivity.nti;
    if (this.mThread == null)
    {
      this.mThread = new CFBatteryThread();
      this.mThread.start();
    }
    return super.onStartCommand(paramIntent, paramInt1, paramInt2);
  }

  public boolean onUnbind(Intent paramIntent)
  {
    return super.onUnbind(paramIntent);
  }

  private class MTKBatteryReceiver extends BroadcastReceiver
  {
    private MTKBatteryReceiver()
    {
    }

    public void onReceive(Context paramContext, Intent paramIntent)
    {
      Log.v("BatteryLogMTKService", "onReceive time=" + System.currentTimeMillis());
      paramIntent.getAction().equals("com.cfzz.abws");
    }
  }

  public class CFBatteryThread extends Thread
  {
    private CFBatteryThread()
    {
    }

    public void run()
    {
      Log.v("BatteryLogMTKService", "thread run: time=" + BatteryLogMTKService.mCurrentTime);
      while (true)
        try
        {
          
		  Message message = new Message();
						message.what = 1;
						handler.sendMessage(message);
          BatteryLogMTKService.mCurrentTime = 1000*nti + BatteryLogMTKService.mCurrentTime;
          Thread.sleep(1000*nti);
        }
        catch (Exception localException)
        {
        }
    }
    
    /*public void onDestroy()
    {
    	//AndroidBatteryWithServiceActivity.db.close();
    	super.destroy();
    	
    }*/
  }
}