package com.nordicnerds.colorstruggle;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;

public class LoadingActivity extends Activity
{
    private View decorView;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        decorView = getWindow().getDecorView();
        navi_check();
        setContentView(R.layout.activity_loading);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Handler handler = new Handler();
        handler.postDelayed(this::validate_this, 2500);
    }

    private void validate_this()
    {
        finish();
        startActivity(new Intent(LoadingActivity.this,RetryActivity.class));
        overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
        {
            navi_check();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void hideSystemUI()
    {
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void navi_check()
    {
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);
        if (!hasBackKey && !hasHomeKey)
        {
            hideSystemUI();
        }
    }
}