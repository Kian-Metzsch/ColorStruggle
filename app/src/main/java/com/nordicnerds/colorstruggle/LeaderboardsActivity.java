package com.nordicnerds.colorstruggle;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.view.Gravity;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.*;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class LeaderboardsActivity extends Activity
{
    private View decorView;

    private Button button_arrow_back;

    private LinearLayout ll;

    private Typeface custom_font;

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        decorView = getWindow().getDecorView();
        navi_check();
        setContentView(R.layout.activity_leaderboards);

        button_arrow_back = findViewById(R.id.button_arrow_back);
        ll = findViewById(R.id.linearLayout_leaderboards);

        TextView textView_into_layout = findViewById(R.id.textView_leaderboards_over);

        custom_font = Typeface.createFromAsset(getAssets(), "font/baloobhaijaan_regular.ttf");
        textView_into_layout.setTypeface(custom_font);
        init_firebase();
    }

    private ValueEventListener dataListener;
    private DatabaseReference myRef;
    private void init_firebase()
    {
        new Thread()
        {
            public void run()
            {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                myRef = database.getReference();
                dataListener = new ValueEventListener()
                {

                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        int ls = 1;
                        int counts = 0;
                        String[][] personalInfo = new String[101][101];

                        for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren())
                        {
                            personalInfo[ls][0] = ""+childDataSnapshot.child("name").getValue();
                            personalInfo[0][ls] = ""+childDataSnapshot.child("score").getValue();

//                            System.out.println(childDataSnapshot.getKey()+"="+childDataSnapshot.getValue());
                            ls++;
                            counts++;
                        }
                        int ii = 1;
                        ll.removeAllViews();
                        for(int i=counts; i>0; i--)
                        {
//                            System.out.println(ii+"# "+personalInfo[i][0]+" = "+personalInfo[0][i]);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                            params.setMargins(20,5,20,5);
                            TextView cb = new TextView(LeaderboardsActivity.this);

                            cb.setText("#"+ii);
                            cb.append("\n"+personalInfo[i][0]);
                            System.out.println("Score = "+personalInfo[0][i]);
                            if (personalInfo[0][i].equals("null"))
                            {
                                cb.append("\n"+0);
                            }
                            else
                            {
                                cb.append("\n"+personalInfo[0][i]);
                            }


                            cb.setLayoutParams(params);
                            cb.setTextColor(Color.WHITE);
                            cb.setGravity(Gravity.CENTER);
                            cb.setBackgroundResource(R.drawable.leaderboards_tabs);
                            cb.setTypeface(custom_font);
                            ll.addView(cb);

                            ii++;
                        }
//                        sort_leaderboards();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {
                        System.out.println("loadPost:onCancelled"+ databaseError.toException());
                    }
                };
                myRef.orderByChild("score").limitToFirst(100).addValueEventListener(dataListener);
            }
        }.start();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)
        {
            navi_check();
            System.out.println("Firebase initiated from focus");
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

    @Override
    public void finish()
    {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void back(View view)
    {
        SharedPreferences PREF_SETTINGS_KEY = getSharedPreferences("PREF_SETTINGS_KEY", MODE_PRIVATE); // sound
        int restoreSoundValue = PREF_SETTINGS_KEY.getInt("idSoundSwitch", 0);
        if (restoreSoundValue == 0)
        {
            MediaPlayer mp_button_click = MediaPlayer.create(this, R.raw.button_click);
            mp_button_click.start();
            mp_button_click.setOnCompletionListener(MediaPlayer::release);
        }
        button_arrow_back.startAnimation(AnimationUtils.loadAnimation(this, R.anim.onclick_animation));
        finish();
    }
}