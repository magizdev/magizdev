package com.magizdev.easytask.util;

import com.magizdev.easytask.AlarmReceiver;
import com.magizdev.easytask.viewmodel.EasyTaskInfo;
import com.magizdev.easytask.viewmodel.EasyTaskUtil;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class AlarmUtil {
	public static void updateAlarm(Context context) {
		EasyTaskUtil util = new EasyTaskUtil(context);
		EasyTaskInfo nextEasyTaskInfo = util.getNextTask();
		if (nextEasyTaskInfo != null) {
			Intent intent = new Intent(context, AlarmReceiver.class);
			intent.putExtra("easyTaskId", nextEasyTaskInfo.Id);
			PendingIntent pIntent = PendingIntent.getBroadcast(context, 0,
					intent, PendingIntent.FLAG_CANCEL_CURRENT);
			AlarmManager alarmManager = (AlarmManager) context
					.getSystemService(Activity.ALARM_SERVICE);
			alarmManager.set(AlarmManager.RTC_WAKEUP,
					nextEasyTaskInfo.StartDate.getTime(), pIntent);
		}
	}
}
