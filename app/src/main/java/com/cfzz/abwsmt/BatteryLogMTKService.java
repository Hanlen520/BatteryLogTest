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
  //public static final String CF_ACTION_BATTERY_INFORMATION = "com.cfzz.abwsmt";
  private static long mCurrentTime;
  private final String TAG = "BatteryLogMTKService";
  private AlarmManager mAlarmManager = null;//alarm定时器
  private Context mContext = null;
  private Handler handler;//电池信息数据库写入Handler
  private PendingIntent mPendingIntent = null;
  private CFBatteryThread mThread = null;///电池信息数据库写入线程
  private PowerManager.WakeLock mWakeLock = null;
  private String ct = null;//当前时间信息
  private int mq, mv, me;//电量、电压、电流
  private double mt;//温度
  private int nti;//记录间隔
    //获取唤醒锁
  private void acquireWakeLock()
  {
    if (this.mWakeLock == null)
    {
      Log.v("BatteryLogMTKService", "Acquire wake lock");
      this.mWakeLock = ((PowerManager)this.mContext.getSystemService(POWER_SERVICE))
              .newWakeLock(1, getClass().getCanonicalName());
      this.mWakeLock.acquire();
    }
  }
  //释放唤醒锁
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
    //设定闹钟定时状态接收器
    Intent localIntent = new Intent("com.cfzz.abws");
    this.mPendingIntent = PendingIntent.getBroadcast(this.mContext, 0, localIntent, Intent.FILL_IN_ACTION);
    this.mAlarmManager = ((AlarmManager)this.mContext.getSystemService(ALARM_SERVICE));
    MTKBatteryReceiver localMTKBatteryReceiver = new MTKBatteryReceiver();
    IntentFilter localIntentFilter = new IntentFilter("com.cfzz.abws");
    this.mContext.registerReceiver(localMTKBatteryReceiver, localIntentFilter);
    acquireWakeLock();
    //Log.v("BatteryLogMTKService", "File Name:" + this.mFile.getName());

	handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				SimpleDateFormat hms = new SimpleDateFormat("HH:mm:ss",Locale.getDefault());//获取当前时间
				ct = hms.format(new Date());//格式化时间
				mq = BatteryQuantityUtils.getQuantityFile();//获取电量数据
				mv = BatteryVoltageUtils.getVoltageFile();//获取电压数据
				me = BatteryElectricUtils.getElectricFile();//获取电流数据
				mt = BatteryTemperatureUtils.getTemperatureFile()/10.0;//获取温度数据
				String ms = BatteryStatusUtils.getBatteryTemp() + "-" + BatteryStatusUtils.getSupplyOnline();//获取电池状态
                //调整特殊充电状态显示，针对部分机型会出现99%电量时便不再充电的情况
				if(me<80 && mq==99 && (BatteryStatusUtils.getSupplyOnline() != "DisConnect"))
				{
					ms = "Charging" + "-" + BatteryStatusUtils.getSupplyOnline();
				}
                //将以上获取到的信息写入数据表
				ContentValues values = new ContentValues();
				values.put("Time", ct);// 
				values.put("Quantity", mq);// 
				values.put("Voltage", mv);// 
				values.put("Electric", me);//
				values.put("Temperature", mt);// 
				values.put("Status", ms);//
				AndroidBatteryWithServiceActivity.db.insert("Batterylog", null, values);
				super.handleMessage(msg);
			}
		};
  }

  public void onDestroy()
  {
      super.onDestroy();
      stopSelf();
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
  }

  public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2)
  {
      Log.v("BatteryLogMTKService", "BatteryLogMTKService->onStartCommand");
      new BatteryLogMTKService();
      nti = AndroidBatteryWithServiceActivity.nti;//获取记录间隔
      //开启记录线程
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
     private MTKBatteryReceiver(){}
     public void onReceive(Context paramContext, Intent paramIntent)
     {
       Log.v("BatteryLogMTKService", "onReceive time=" + System.currentTimeMillis());
       paramIntent.getAction().equals("com.cfzz.abws");
     }
  }
  //定时记录线程
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
             Log.e(TAG, "Battery info handler run fail : " + localException.getMessage());
         }
    }
  }
}