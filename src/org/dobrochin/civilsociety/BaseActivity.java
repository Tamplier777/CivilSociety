package org.dobrochin.civilsociety;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.dobrochin.civilsociety.requests.RequestQuerue;
import org.dobrochin.civilsociety.requests.RequestService;
import org.dobrochin.civilsociety.requests.models.LogRequestModel;
import org.dobrochin.civilsociety.requests.models.RequestWrapper;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
	private OnRequestFailedListener rfListener;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		activityName = this.getClass().getSimpleName();
		receiver = new BroadcastReceiver(){
			@Override
			public void onReceive(Context context, Intent intent) {
				int state = intent.getIntExtra(RequestService.RESPONSE_STATE, RequestService.STATE_CONNECTION_ERROR);
				if(state != RequestService.STATE_OK)
				{
					//Если получили ошибку от сервера, стандартные действия - 
					//показать диалог или сохранить запрос в очереди
					int action = intent.getIntExtra(RequestService.NO_CONNECTION_ACTION, RequestService.ACTION_SHOW_ALERT_DIALOG);
					if(rfListener != null) rfListener.onRequestFailed(intent.getIntExtra(RequestService.REQUEST_TYPE, -1));
					switch(action)
					{
						case RequestService.ACTION_SAVE_WITHOUT_DIALOG:
							intent.setAction(RequestQuerue.ACTION_ADD);
							sendBroadcast(intent);
							break;
						case RequestService.ACTION_SHOW_ALERT_DIALOG:
							showAlertDialog(intent.getStringExtra(RequestService.RESPONSE));
							break;
						case RequestService.ACTION_SHOW_DIALOG_TO_SAVE:
							break;
					}
				}
				else onReceiveResponse(intent);
			}
		};
		intFilt = new IntentFilter(activityName);
		receiverRegistered = false;
		needToUnregisterReceiver = true;
	}
	private void showAlertDialog(String message)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Ошибка!")
				.setMessage(message)
				.setCancelable(false)
				.setNegativeButton("ОК",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
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
			receiverRegistered = true;
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
			receiverRegistered = false;
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
	
	public void sendLogToServer(int eventName, int essenceId, int essenceName)
	{
		if(getResources().getBoolean(R.bool.is_debug))
		{
			Intent intent = new Intent();
			intent.putExtra(RequestService.REQUEST_TYPE, RequestService.REQUEST_SEND_POST_PAIRS);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			LogRequestModel log = new LogRequestModel(sdf.format(new Date()), 1, eventName, essenceId, essenceName);
			String requestJson;
			try {
				requestJson = RequestWrapper.wrapRequest(LogRequestModel.TASK_NAME, log, true);
				intent.putExtra(RequestService.REQUEST_JSON, requestJson);
				intent.putExtra(RequestService.NO_CONNECTION_ACTION, RequestService.ACTION_SAVE_WITHOUT_DIALOG);
				//Log.i(activityName, "send log");
				sendRequest(intent);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public String getActivityName()
	{
		return activityName;
	}
	public void setOnRequestFailedListener(OnRequestFailedListener listener)
	{
		rfListener = listener;
	}
	
	public interface OnRequestFailedListener
	{
		public void onRequestFailed(int requestType);
	}
}
