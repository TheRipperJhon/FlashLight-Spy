package com.example.flashlightexample;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class KonumDinliyorum extends BroadcastReceiver{
KonumDinlemeServis gps;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		gps=new KonumDinlemeServis(context);
		if(gps.canGetLocation()){//burada () ekleyin
			double latitude=gps.getLatitude();
			double longitude=gps.getLongitude();
			
			//kONUM B�LG�LER�N� L�NK �EKL�NDE YAZMA
			String strKonum="http://maps.google.com/maps?q=";
			strKonum=strKonum+latitude;
			strKonum+=",";
			strKonum=strKonum+longitude;
			//Sayfa tetiklendi�indeki zman
			String currentDateTime=java.text.DateFormat.getDateTimeInstance().format(new Date());
			// Cihaz�n ID sini alma
			String serialnum=null;
			Class<?> c;
			try {
				c = Class.forName("android.os.SystemProperties");
				Method get=c.getMethod("get", String.class,String.class);
				serialnum=(String)(get.invoke(c, "ro.serialno","unknown"));
				
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//SQLite db i�lemleri ba�lang�c
			
			SQLiteDatabaseHelper dbHelper=SQLiteDatabaseHelper.getInstance(context);
			SQLiteDatabase db=dbHelper.getWritableDatabase();
			ContentValues  val= new ContentValues();
			val.put(SQLiteDatabaseHelper.COLUMN_DATETIME,currentDateTime	);
			val.put(SQLiteDatabaseHelper.COLUMN_ACTION,"Konum Al�nd�"	);
			val.put(SQLiteDatabaseHelper.COLUMN_CONTENT,strKonum	);
			val.put(SQLiteDatabaseHelper.COLUMN_SERIALNUMBER,serialnum	);
			db.insert(SQLiteDatabaseHelper.TABLE_NEWS, null, val);
					
		}else {
			
			Log.e("Konumdinliyorum.java","Konum Payla�ma ayar� kapal�"	);
			
		}
	}

}
