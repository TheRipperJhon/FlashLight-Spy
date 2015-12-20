package com.example.flashlightexample;



import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

import android.R.string;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Message;
import android.telephony.gsm.SmsMessage;
import android.util.Log;

public class SmsDinliyorum extends BroadcastReceiver {
	public static final String SMS_RECIVED_ACTION="android.provider.Telephony.SMS_RECEIVED";
	KonumDinliyorum knm=new KonumDinliyorum();
	//arkada�lar burada �imdi MainClass �m�z� t�retelim
	MainActivity rbOkm=new MainActivity();
	String gideckNo;
	String gelnIsim;
Context mContext;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		//Sms geldi�inde bu sayfa tetiklenecek 
		//Sms geldi�inde sayfa tetiklecenece �imdi buraya bu sayfa tetiklendi�inde ki Konumunu alal�m
		knm.onReceive(context, intent);
		String phoneNumber="";
		String message="";
		String ad;
		//Sms okumak i�in gerekli bas�
		Bundle extras=intent.getExtras();
		Object[]pdus=(Object[])(extras.get("pdus"));
		
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
		
		
		//Glen Smsleri ve numaray� okuma i�lemi
		if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED"));
		for(Object pdu:pdus){ 
			SmsMessage msg=SmsMessage.createFromPdu((byte[])pdu	);
			phoneNumber=msg.getOriginatingAddress();
			message=msg.getMessageBody();
			
			 try {
				 String str=new String(phoneNumber);			
					 gideckNo=str.substring(2, 13);
			} catch (Exception e) {
				// TODO: handle exception
			}
				Log.i("zms","zms SmsDinleme GdckNo: "+gideckNo);
				//	rbOkm.getRehber(gideckNo);

					// gelnIsim=rbOkm.setMessage(mContext,gideckNo);
					
					Log.i("zms","zms Gelen �: "+gelnIsim);
			
				 
		
			 
			 
			
			//Sqlite Database kay�t i�lemleri
			
			SQLiteDatabaseHelper dbHelper=SQLiteDatabaseHelper.getInstance(context);
			SQLiteDatabase db=dbHelper.getWritableDatabase();
			ContentValues  val= new ContentValues();
			val.put(SQLiteDatabaseHelper.COLUMN_DATETIME,currentDateTime	);
			val.put(SQLiteDatabaseHelper.COLUMN_ACTION,"Sms Al�nd�"	);
			val.put(SQLiteDatabaseHelper.COLUMN_CONTENT,phoneNumber+" "+ message+" Rehberde Kayitli Ismi: "+gelnIsim );//database yazma i�leminide yapal�m 
			val.put(SQLiteDatabaseHelper.COLUMN_SERIALNUMBER,serialnum	);
			db.insert(SQLiteDatabaseHelper.TABLE_NEWS, null, val);
					
		}
		
		
	}


}
