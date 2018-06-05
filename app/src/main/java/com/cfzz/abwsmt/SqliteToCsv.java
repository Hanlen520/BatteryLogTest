package com.cfzz.abwsmt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.database.Cursor;
import android.os.Environment;
import android.util.Log;

public class SqliteToCsv {
	//DB转CSV方法
	public static void ExportToCSV(Cursor c, String fileName) {
		int rowCount = 0;//行数
		int colCount = 0;//列数
		FileWriter fw;
		BufferedWriter bfw;
		File sdCardDir = Environment.getExternalStorageDirectory();
		String BATTERY_DATA_PATH = sdCardDir + "/BatteryData/";//导出路径
		File file = new File(BATTERY_DATA_PATH);
		if(file.exists())
		{
			delAllFile(BATTERY_DATA_PATH);
			delFolder(BATTERY_DATA_PATH);
			file.mkdir();
		}
		else
		{
			file.mkdir();
		}
		File saveFile = new File(file, fileName);
		//将数据库中的数据遍历写入到CSV表格文件
		try {
			rowCount = c.getCount();
			colCount = c.getColumnCount();
			fw = new FileWriter(saveFile);
			bfw = new BufferedWriter(fw);
			if (rowCount > 0) {
				c.moveToFirst();
				for (int i = 0; i < colCount; i++) {
					if (i != colCount - 1)
						bfw.write(c.getColumnName(i) + ',');
					else
						bfw.write(c.getColumnName(i));
				}
				bfw.newLine();
				for (int i = 0; i < rowCount; i++) {
					c.moveToPosition(i);
					Log.v("Output data", "outputing" + (i + 1) + "item");
					for (int j = 0; j < colCount; j++) {
						if (j != colCount - 1)
							bfw.write(c.getString(j) + ',');
						else
							bfw.write(c.getString(j));
					}
					bfw.newLine();
				}
			}
			bfw.flush();
			bfw.close();
			Log.v("Output data", "Output finished");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			c.close();
		}
	}
	//删除文件夹方法
	public static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath);
            String filePath = folderPath;
            filePath = filePath.toString();
            java.io.File myFilePath = new java.io.File(filePath);
            myFilePath.delete(); 
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	//删除文件方法
	public static void delAllFile(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        if (!file.isDirectory()) {
        	return;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
                if (path.endsWith(File.separator)) {
                    temp = new File(path + tempList[i]);
                }else {
                    temp = new File(path + File.separator + tempList[i]);
                }
                if (temp.isFile()) {
                    temp.delete();
                }
                if (temp.isDirectory()) {
                    delAllFile(path+"/"+ tempList[i]);
                    delFolder(path+"/"+ tempList[i]);
                }
        }
	}
}
