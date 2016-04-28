package com.example.hwoh.runningapp.service;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.example.hwoh.runningapp.lockscreen.LockActivity;

import java.util.Date;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 전면활성화 앱 감지
 */
public class AppDetectService extends Service {
    private final String TAG = "AppDetectService";
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service onCreate");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");                                                               //무한루프해제
        mAppDetectRefHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        //서비스 상태 플래그
        Message msg = mAppDetectRefHandler.obtainMessage();
        msg.obj = "";
        mAppDetectRefHandler.sendMessage(msg);
        return START_STICKY;
    }

    /**
     * @author hwoh
     * @version 2016.04.22 / 1.0
     * <p/>
     * mAppDetectRefHandler 이전의 앱을 탐색합니다.
     */

    Handler mAppDetectRefHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Message reflectionMsg = obtainMessage();                                                       //기존 안내 Msg
            String packageName = "";
            packageName = getProcessName();
            Log.d(TAG, "== Application : " + packageName);
            String[] home = getHomeLauncher();
            for (int i = 0; i < home.length; i++) {
                Log.v(TAG, "home launcher = " + home[i]);
                if (home[i].equals(packageName)) {                                                  // 현재 포그라운드가 홈런처 일경우
                    if (packageName.equals(msg.obj.toString())) {
                        Log.d(TAG, "== pre App, recent App is same App");
                    } else {
                        Log.d(TAG, "== Application is catched HOME!!: " + packageName);
                        if (msg.obj.toString().equals("com.kakao.talk")) {
                            Log.d(TAG, "이전앱은 카카오!");
                            Intent dialogIntent = new Intent(getApplicationContext(), LockActivity.class);
                            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(dialogIntent);
                            Log.d(TAG, "이전앱은 카카오!!!!!");
                          //  stopSelf();
                        }
                    }
                }
            }
            reflectionMsg.obj = packageName;
            mAppDetectRefHandler.sendMessageDelayed(reflectionMsg, 500);
        }
    };

    /**
     * 현재화면이 활성화된 화면 감지
     */
    private String getProcessName() {
        String foregroundProcess = "";
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {                                //롤리팝 이후
            UsageStatsManager mUsageStatsManager = (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> stats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, time - 1000 * 10, time); //현재시간으로부터 10초전 전면활성화된 앱
            Date start = new Date(time);
            Date end = new Date(time - 1000 * 10);
            Log.d(TAG, "시작 : " + start.toString() + " 종료 : " + end.toString());
            // Sort the stats by the last time used
            if (stats != null) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : stats) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    String topPackageName = mySortedMap.get(mySortedMap.lastKey()).getPackageName();//활성화된 앱 중 마지막으로 활성화된 앱
                    foregroundProcess = topPackageName;
                }
            }
        } else {                                                                                    //롤리팝 이전
            @SuppressWarnings("deprecation")
            ActivityManager.RunningTaskInfo foregroundTaskInfo = activityManager.getRunningTasks(1).get(0);
            foregroundProcess = foregroundTaskInfo.topActivity.getPackageName();
        }
        return foregroundProcess;
    }

    /**
     * 현재화면이 홈인지 감지
     *
     * @return 홈런처
     */
    private String[] getHomeLauncher() {
        String[] HomeLauncher;
        PackageManager pm = getPackageManager();
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);

        List<ResolveInfo> homeApps = pm.queryIntentActivities(homeIntent, PackageManager.GET_ACTIVITIES);
        HomeLauncher = new String[homeApps.size()];
        for (int i = 0; i < homeApps.size(); i++) {
            ResolveInfo info = homeApps.get(i);
            HomeLauncher[i] = info.activityInfo.packageName;
        }
        return HomeLauncher;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}