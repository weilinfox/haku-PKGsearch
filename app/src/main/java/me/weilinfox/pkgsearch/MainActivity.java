package me.weilinfox.pkgsearch;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.ArrayList;

import me.weilinfox.pkgsearch.databinding.ActivityMainBinding;
import me.weilinfox.pkgsearch.searcher.mirrorSearcher.updateService.UpdateService;
import me.weilinfox.pkgsearch.utils.Constraints;
import me.weilinfox.pkgsearch.utils.NavigationViewUtil;
import me.weilinfox.pkgsearch.utils.StarUtil;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    /*private boolean quitStatus = false;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what) {
                case Constraints.quitTimeout:
                    quitStatus = false;
                    break;
            }
            return false;
        }
    });*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 软键盘不顶起布局
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_search, R.id.navigation_favourite, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        // NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        navView.measure(width, height);
        width = navView.getMeasuredWidth();
        height = navView.getMeasuredHeight();
        NavigationViewUtil.height = height;
        Log.d(TAG, "onCreate: NavigationView height " + height);

        // >= api 23
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            if (checkSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED) {
                permissions.add(Manifest.permission.INTERNET);
            }
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (!permissions.isEmpty()) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), Constraints.requestPermissions);
            }
        }

        // 初始化数据库
        StarUtil.initDatabase(this);

        // 通知 channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(
                    Constraints.notificationChannelId, Constraints.notificationChannelId,
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription(Constraints.notificationChannelId);
            channel.enableLights(false);
            channel.setSound(null, null);
            notificationManager.createNotificationChannel(channel);
        }

        // 初始化服务
        UpdateService.startService(this);
        UpdateService.startRepeating(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constraints.requestPermissions:
                boolean flag = true;
                for (int r : grantResults) {
                    if (r == PackageManager.PERMISSION_DENIED) {
                        flag = false;
                        break;
                    }
                }
                if (!flag) {
                    System.exit(0);
                }
                break;
        }
    }

    /*@Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (quitStatus) {
                MainActivity.this.finish();
            } else {
                quitStatus = true;
                handler.sendEmptyMessageDelayed(Constraints.quitTimeout, 2000);
                Toast.makeText(this, R.string.quit_confirm, Toast.LENGTH_SHORT).show();
            }
        }
        return super.onKeyUp(keyCode, event);
    }*/
}
