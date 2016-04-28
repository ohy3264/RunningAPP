package com.example.hwoh.runningapp.service;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.hwoh.runningapp.R;
import com.example.hwoh.runningapp.util.ServiceUtil;

import java.util.List;

/**
 * Created by hwoh on 2016-04-28.
 */
public class ForegroundService extends Service {
    private final String TAG = "ForegroundService";
    private BroadcastReceiver mScreenStateReceiver = new ScreenStateReceiver();                     //스크린 감지 리시버
    private final int NOTIFICATION_ID = 1;                                                          //포그라운드 서비스 노티피케이션 ID
    private KeyguardManager keyguardManager;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        IntentFilter localStateFilter = new IntentFilter();                                         //스크린 Intent Filter
        localStateFilter.addAction(Intent.ACTION_SCREEN_ON);                                        //스크린 On Action
        localStateFilter.addAction(Intent.ACTION_SCREEN_OFF);                                       //스크린 Off Action
        localStateFilter.addAction(Intent.ACTION_USER_PRESENT);                                     //LockScreen Off Action
        registerReceiver(mScreenStateReceiver, localStateFilter);

        // check lock screen
        keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);

        if (ServiceUtil.isRunningService(getApplicationContext(), AppDetectService.class) == false) { //모니터링 서비스 작동안함
            startService(new Intent(getApplicationContext(), AppDetectService.class));                //모니터링 서비스 시작
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        Notification.Builder builder = new Notification.Builder(getApplicationContext())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("서비스 알리미")
                .setContentText("포그라운드~!")
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        Notification notification = builder.build();
        startForeground(NOTIFICATION_ID, notification);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        unregisterReceiver(mScreenStateReceiver);                                                   //스크린 BR 등록 해제
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class ScreenStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                Log.i(TAG, "screen off");
                if (ServiceUtil.isRunningService(context, AppDetectService.class) == true) {        //모니터링 서비스 작동중
                    Log.i(TAG, "작동중이면 service off");
                    context.stopService(new Intent(context, AppDetectService.class));               //모니터링 서비스 종료
                }
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                Log.i(TAG, "screen on");
                if(!keyguardManager.inKeyguardRestrictedInputMode()) {                              //Lock 스크린감지
                    if (ServiceUtil.isRunningService(context, AppDetectService.class) == false) {   //모니터링 서비스 작동안함
                        Log.i(TAG, "작동중이 아니면 service on");
                        context.startService(new Intent(context, AppDetectService.class));          //모니터링 서비스 시작
                    }
                }else{
                    Log.i(TAG, "Lock screen 상태라서 Screen on 리시버 패스");
                }
            }else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                Log.i(TAG, "Lock screen 해제됨");
                if (ServiceUtil.isRunningService(context, AppDetectService.class) == false) {       //모니터링 서비스 작동안함
                    Log.i(TAG, "작동중이 아니면 service on");
                    context.startService(new Intent(context, AppDetectService.class));              //모니터링 서비스 시작
                }
            }
        }
    }


}
