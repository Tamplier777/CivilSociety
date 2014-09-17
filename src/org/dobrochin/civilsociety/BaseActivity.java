package org.dobrochin.civilsociety;

import org.dobrochin.civilsociety.requests.RequestService;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

//Класс, собирающий в себе весь функционал, присутствующий во всех активностях приложения
//-Способность отправлять и получать запросы.
//-Возможность кэшировать отображаемые данные и загружать их.
public abstract class BaseActivity extends Activity {
	private String activityName; //Выступает в роли action для регистрации broadcast receiver и в роли имени для кэша
	private IntentFilter intFilt;
	private BroadcastReceiver receiver;
	private boolean receiverRegistered;
	private boolean needToUnregisterReceiver;
	private RequestService requestService;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		activityName = this.getClass().getSimpleName();
		receiver = new BroadcastReceiver(){
			@Override
			public void onReceive(Context context, Intent intent) {
				onReceiveResponse(intent);
			}
		};
		intFilt = new IntentFilter(activityName);
		receiverRegistered = false;
		needToUnregisterReceiver = true;
		//requestService = new RequestService();
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
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(receiverRegistered && (needToUnregisterReceiver || isFinishing()))
		{
			unregisterReceiver(receiver);
		}
	}
	//Отсылаем задачу в другой поток
	public void sendRequest(Intent intent)
	{
		intent.putExtra(RequestService.BACK_ADDRESS, activityName);
		//requestService.startService(intent);
		startService(intent);
	}
}
