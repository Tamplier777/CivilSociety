package org.dobrochin.civilsociety;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.dobrochin.civilsociety.requests.RequestService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

//Класс, собирающий в себе весь функционал, присутствующий во всех активностях приложения
//-Способность отправлять и получать запросы.
//-Возможность кэшировать отображаемые данные и загружать их.
public abstract class BaseActivity extends Activity {
	private static final String CACHE_FILE = "cache";
	private String activityName; //Выступает в роли action для регистрации broadcast receiver и в роли имени для кэша
	private IntentFilter intFilt;
	private BroadcastReceiver receiver;
	private boolean receiverRegistered;
	private boolean needToUnregisterReceiver;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		activityName = this.getClass().getSimpleName();
		//Log.i("wtf", activityName);
		receiver = new BroadcastReceiver(){
			@Override
			public void onReceive(Context context, Intent intent) {
				onReceiveResponse(intent);
			}
		};
		intFilt = new IntentFilter(activityName);
		receiverRegistered = false;
		needToUnregisterReceiver = true;
	}
	//Переопределяем этот метод  в дочерних классах для получения ответа на запрос
	protected abstract void onReceiveResponse(Intent intent);
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(!receiverRegistered)
		{
			registerReceiver(receiver, intFilt);
		}
		checkCache();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(receiverRegistered && (needToUnregisterReceiver || isFinishing()))
		{
			unregisterReceiver(receiver);
		}
		saveCache();
	}
	//Дописываем в intent поля, необходимые для запуска сервиса и для корректного возврата результатов 
	public void sendRequest(Intent intent)
	{
		intent.putExtra(RequestService.BACK_ADDRESS, activityName);
		intent.setClass(this, RequestService.class);
		startService(intent);
	}
	//Проверяем, есть ли сохраненный кэш для данной активности
	private void checkCache()
	{
		SharedPreferences cache = getSharedPreferences(CACHE_FILE, MODE_PRIVATE);
		String cacheString = cache.getString(activityName, null);
		if(cacheString != null) cacheLoaded(cacheString);
	}
	//Сохраняем кэш, если есть
	private void saveCache()
	{
		String cache = getCacheToSave();
		if(cache != null) 
		{
			SharedPreferences settings = getSharedPreferences(CACHE_FILE, MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(activityName, cache);
		    editor.commit();
		}
	}
	protected abstract void cacheLoaded(String cache); //Переопределяется в дочерних классах для определения действий при загрузке кэша
	protected abstract String getCacheToSave(); //Переопределяется в дочерних классах для формирования кэша текущей активности
	
	public static final String REQUEST_LOG_TIME = "dts";
	public static final String REQUEST_LOG_USERID = "uid";
	public static final String REQUEST_LOG_EVENTNAME = "prid";
	public static final String REQUEST_LOG_ESSENCEID = "sid";
	public static final String REQUEST_LOG_ESSENCENAME = "snid";
	public void sendLogToServer(int eventName, int essenceId, int essenceName)
	{
		Intent intent = new Intent();
		intent.putExtra(RequestService.REQUEST_TYPE, RequestService.REQUEST_SEND_POST_PAIRS);
		
		//Надо бы тут тест соединения проводить, и забирать список задач
		JSONObject logRequest = new JSONObject();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			logRequest.put(REQUEST_LOG_TIME, sdf.format(new Date()));
			logRequest.put(REQUEST_LOG_USERID, 1);
			logRequest.put(REQUEST_LOG_EVENTNAME, eventName);
			logRequest.put(REQUEST_LOG_ESSENCEID, essenceId);
			logRequest.put(REQUEST_LOG_ESSENCENAME, essenceName);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		intent.putExtra(RequestService.REQUEST_JSON, logRequest.toString());
		sendRequest(intent);
	}
}
