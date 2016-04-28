package com.example.hwoh.runningapp;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import com.example.hwoh.runningapp.service.ServiceMonitor;
/**
 * 초기 액티비티
 */
public class MainActivity extends Activity {
    private ServiceMonitor serviceMonitor = ServiceMonitor.getInstance();
    private static final int MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {                                //롤리팝이전 버전은 ACTION_USAGE_ACCESS와 다른방식으로 감지
            if (!hasPermission()) {                                                                 //앱 모니터링을 위한 권한 체크
                startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS),           //권한 설정화면으로 이동
                        MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
            }
        }


            serviceMonitor.setInterval(60000 * 30);                                                 //서비스 모니터링(am) 서비스 체크 주기
            serviceMonitor.startMonitoring(getApplicationContext());                                //서비스 모니터링(am) 시작

        finish();
    }
    /**
     * 앱모니터링 권한이 없다면 권한 설정화면 유도
     * UsageStatsManager을 사용하기 위한 권한
     * 실행중인 앱 검색
     */
    private boolean hasPermission() {
        AppOpsManager appOps = (AppOpsManager)
                getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }
    /**
     * 설정창으로 이동 후 다시 액티비티로 왔을때
     * 권한이 여전히 없다면 안내메시지 활성화 후 다시 권한설정화면 유도
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS:
                if (!hasPermission())
                    requestPermission();
                break;
        }
    }

    /**
     * 안내메시지 활성화 후 권한설정화면 유도ㄴ
     */
    private void requestPermission() {
        Toast.makeText(this, "Need to request permission", Toast.LENGTH_SHORT).show();
        startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), MY_PERMISSIONS_REQUEST_PACKAGE_USAGE_STATS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
