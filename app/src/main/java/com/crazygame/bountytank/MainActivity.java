package com.crazygame.bountytank;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {
    private final Point size = new Point();
    private GameView gameView = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(checkOpenGL()) {
            setFullScreen();
            gameView = new GameView(this, size);
            setContentView(gameView);
        } else {
            Toast.makeText(this, "This device doesn't support ES2.0",
                    Toast.LENGTH_LONG).show();
            return;
        }
    }

    private boolean checkOpenGL() {
        final ActivityManager activityManager =
                (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo =
                activityManager.getDeviceConfigurationInfo();
        final boolean supportsES2 = configurationInfo.reqGlEsVersion >= 0x20000;

        return supportsES2;
    }

    private void setFullScreen() {
        final View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE);

        final Display display = getWindowManager().getDefaultDisplay();
        display.getSize(size);

        final Resources resources = getResources();
        int navigationBarHeight;
        int resourceId = getResources().getIdentifier("navigation_bar_height",
                "dimen", "android");
        if (resourceId > 0) {
            navigationBarHeight = resources.getDimensionPixelSize(resourceId);
            size.x += (float)navigationBarHeight;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(gameView != null) {
            gameView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(gameView != null) {
            gameView.onPause();
        }
    }
}
