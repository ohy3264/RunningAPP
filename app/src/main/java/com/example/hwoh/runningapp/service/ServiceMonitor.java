package com.example.hwoh.runningapp.service;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.example.hwoh.runningapp.util.ServiceUtil;

import java.util.List;

/**
 * 서비스를 주기적으로 작동시키는 클래스
 */
public class ServiceMonitor {
    private static final String TAG = "ServiceMonitor";
    private static ServiceMonitor instance;
    private AlarmManager am;                                                                        //BR 주기적으로 호출할 AlarmManager
    private Intent intent;                                                                          //BR intent
    private PendingIntent sender;                                                                   //BR 등록할 PendingIntent
    private long interval = 5000;                                                                   //default interval

    private ServiceMonitor() {}
    public static synchronized ServiceMonitor getInstance() {
        if (instance == null) {
            instance = new ServiceMonitor();
        }
        return instance;
    }

    /**
     * 서비스를 체크하고 작동시키는 BroadcastReceiver
     */
    public static class MonitorBR extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive : MonitorBR");
            if (ServiceUtil.isRunningService(context, ForegroundService.class) == false) {           //모니터링 서비스 작동안함
                context.startService(new Intent(context, ForegroundService.class));                  //모니터링 서비스 시작
            }
        }
    }

    /**
     * 주기적으로 체크할 시간 간격
     */
    public void setInterval(long interval) {
        this.interval = interval;
    }

    /**
     * 모니터링 알람매니저 시작
     */
    public void startMonitoring(Context context) {
        am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(context, MonitorBR.class);
        sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), interval, sender);
    }

    /**
     * 모니터링 알람매니저 종료
     */
    public void stopMonitoring(Context context) {
        am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(context, MonitorBR.class);
        sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.cancel(sender);
        am = null;
        sender = null;
    }

}
