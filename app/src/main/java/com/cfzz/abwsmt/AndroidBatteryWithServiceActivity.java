package com.cfzz.abwsmt;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import android.os.SystemClock;

//import com.cfzz.abwsmt.R;
import com.cfzz.abwsmt.BatteryTools.BatteryElectricUtils;
import com.cfzz.abwsmt.BatteryTools.BatteryQuantityUtils;
import com.cfzz.abwsmt.BatteryTools.BatteryStatusUtils;
import com.cfzz.abwsmt.BatteryTools.BatteryTemperatureUtils;
import com.cfzz.abwsmt.BatteryTools.BatteryVoltageUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class AndroidBatteryWithServiceActivity extends Activity {

	private Timer atimer = null;//电池信息定时更新timer
	private TimerTask atask = null;//电池信息定时更新
	private Handler ahandler;//电池信息定时更新
    private String TAG = "com.cfzz.abl";
	static SQLiteDatabase db;//电池信息数据库
	private Button clearButton;//清空电池信息记录数据
	private Button outputButton;//倒出电池信息记录数据
	private Button screenOnButton;//屏幕常亮开关
	private String fname;//csv文件名
	public TextView TV, BI, TimeIntervalName;//电池记录相关信息显示控件
	public EditText TimeIntervalInput;//电池信息记录时间间隔输入控件
	public static int nti;//电池信息记录时间间隔值
	private int mq, mv, me;//电量、电压、电流
	private double mt;//温度
	private ImageButton state = null;//电池信息数据记录状态拨钮
	public PowerManager.WakeLock wl;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//初始化UI
		setContentView(R.layout.main);
		state = (ImageButton) findViewById(R.id.state);
		state.setOnClickListener(new ocl());
		TimeIntervalName = (TextView) findViewById(R.id.TimeIntervalName);	
		TimeIntervalInput = (EditText) findViewById(R.id.TimeIntervalInput);
		TV = (TextView) findViewById(R.id.TV);//
		BI = (TextView) findViewById(R.id.BI);//
		//创建电池信息数据库
		try {
			//db = SQLiteDatabase.openOrCreateDatabase("/data/data/com.cfzz.abwsmt/databases/BatteryDB.db", null);//
			db = openOrCreateDatabase("BatteryDB", MODE_PRIVATE, null);

		} catch (SQLException e) {
		}
		//电池信息Handler Timer控制，每秒刷新
		ahandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				mq = BatteryQuantityUtils.getQuantityFile();// 
				mv = BatteryVoltageUtils.getVoltageFile();// 
				me = BatteryElectricUtils.getElectricFile();//
				mt = BatteryTemperatureUtils.getTemperatureFile()/10.0;//
				BI.setText("Level:   " + mq + "%" + "\n" +
				           "Voltage:   " + mv + "mV" + "\n" +
						   "Electric:   " + me + "mA" + "\n" +
						   "Temperature:   " + mt + "C");
				TV.setText("Status: " + BatteryStatusUtils.getBatteryTemp() + "---" 
						   + BatteryStatusUtils.getBatteryStatus() + "---" + BatteryStatusUtils.getSupplyOnline());
				super.handleMessage(msg);
			}
		};
		atimer = new Timer();
		atask = new TimerTask()// 
		{
			@Override
			public void run() {
				Message message = new Message();
				message.what = 1;
				ahandler.sendMessage(message);
				//充电到100%自动关闭记录服务
				/*if(BatteryQuantityUtils.getQuantityFile() == 100 && BatteryStatusUtils.getBatteryTemp() == "Full")
				{
					int fme = (me + BatteryElectricUtils.getElectricFile())/2;
					if (is == true && (fme>=0 && fme <=80))
					{
						//CFBatteryThread.interrupted();
						try{
							state.callOnClick();//
						}catch(Exception e)
						{
							AndroidBatteryWithServiceActivity.this.stopService(localIntent);
						}
					}
				}*/
			}
		};
		atimer.schedule(atask, 0, 1000);// 
		
		//电池记录数据表清空
		this.clearButton = (Button) this.findViewById(R.id.my_button);//
		this.clearButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
			    //判断是否正在记录数据
				if (is == true) {
					state.callOnClick();//关闭数据记录开关
					try {
                        //删除原有数据表
						db.execSQL("DROP TABLE IF EXISTS Batterylog");
						//新建数据表
						db.execSQL("Create Table Batterylog(Time text,Quantity text,Voltage text,Electric text,Temperature text,Status text)");
					} catch (Exception e) {
						db.execSQL("Create Table Batterylog(Time text,Quantity text,Voltage text,Electric text,Temperature text,Status text)");//
					}
				} else {
					try {
						db.execSQL("DROP TABLE IF EXISTS Batterylog");
						db.execSQL("Create Table Batterylog(Time text,Quantity text,Voltage text,Electric text,Temperature text,Status text)");//
					} catch (Exception e) {
						db.execSQL("Create Table Batterylog(Time text,Quantity text,Voltage text,Electric text,Temperature text,Status text)");//
					}
				}
			}
		});
		//电池记录信息表格导出
		outputButton = (Button) this.findViewById(R.id.Button01);//
		outputButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (is == true) {
					state.callOnClick();// 
					try {
						outputData();
						
					} catch (Exception e) {
						new AlertDialog.Builder(AndroidBatteryWithServiceActivity.this).setCancelable(false)
						.setPositiveButton("Fail to output", null).show();
					}
				} else {
					try {
						outputData();
					} catch (Exception e) {
						new AlertDialog.Builder(AndroidBatteryWithServiceActivity.this).setCancelable(false)
						.setPositiveButton("Fail to output", null).show();
					}
				}
			}
		});

		//开启屏幕常亮
		screenOnButton = (Button) this.findViewById(R.id.Button02);//
		screenOnButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
						WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
				wl = pm.newWakeLock(
						PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "MyBatteryTest");
				if(screenOnButton.getText().toString().equals("开启屏幕常亮")) {
					//控制常亮
					wl.acquire();//
					screenOnButton.setText("关闭屏幕常亮");
				}else{
					//控制自动延迟灭屏
					wl.release();
					screenOnButton.setText("开启屏幕常亮");
				}
			}
		});
		
	}

	public static boolean is = false;
	Intent localIntent;
	//电池曲线数据记录图形化开关控制
	class ocl implements OnClickListener {
		public void onClick(View v) {
			//置为关闭状态
			if (v.getId() == R.id.state && is == true) {
				state.setImageDrawable(getResources().getDrawable(
						R.drawable.close));
				try{
					//关闭后台曲线数据记录服务
					AndroidBatteryWithServiceActivity.this.stopService(localIntent);
				}catch(Exception e)
				{
					Log.e(TAG, "Service stop fail : " + e.getMessage());
				}
				is = false;
			//置为开启状态
			} else if (v.getId() == R.id.state && is == false) {
				try
                {
                    db.rawQuery("select * from Batterylog", null);//验证Batterylog数据表是否存在
                    state.setImageDrawable(getResources().getDrawable(
    						R.drawable.open));
                    nti = Integer.parseInt(TimeIntervalInput.getText().toString());//获取电池信息记录间隔
					//开启后台曲线数据记录服务
                    AndroidBatteryWithServiceActivity localCFBatteryActivity = AndroidBatteryWithServiceActivity.this;			
    			    localIntent = new Intent(localCFBatteryActivity, com.cfzz.abwsmt.BatteryLogMTKService.class);
    				localCFBatteryActivity.startService(localIntent);
    				Log.v(TAG, "Service start success!");
                }
                catch(Exception e)
                {
                    //提示未创建数据
                    new AlertDialog.Builder(AndroidBatteryWithServiceActivity.this).setCancelable(false)
                    .setPositiveButton("Fail to write data,please clear data", null).show();
                }
				is = true;
			}
		}
	}
	//导出数据方法
	private synchronized void outputData()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault());// 
		fname = sdf.format(new Date()) + ".csv";// 
		
		Cursor c = db.rawQuery("select * from Batterylog", null);//
		Log.v(TAG, "Batterylog: " + c);
		if(c.getCount()!=0)
		{
			SqliteToCsv.ExportToCSV(c, fname); //将数据表写入到CSV文件
			new AlertDialog.Builder(AndroidBatteryWithServiceActivity.this).setCancelable(false)
			.setPositiveButton("Output success", null).show();
		}
		else
		{
			new AlertDialog.Builder(AndroidBatteryWithServiceActivity.this).setCancelable(false)
			.setPositiveButton("Data table empty,no to output", null).show();
		}
	}
	
	@Override	
	public void onDestroy() // 
	{
		super.onDestroy();
		try{
				atimer.cancel();// 
				atimer = null;
				atask.cancel();
				atask = null;
		}catch(Exception e){
				Log.v(TAG, "Test activity has not started!");
		}
		//退出应用关闭电池信息记录服务
		/*if (is == true) {
			try{
					AndroidBatteryWithServiceActivity.this.stopService(localIntent);
				}catch(Exception e)
				{
					Log.v(TAG, "No test Mission!");
				}
		}*/
		try {
			wl.release();
		}catch (Exception e){
			Log.e(TAG, "No screen wakelock on!");
		}

	}
}