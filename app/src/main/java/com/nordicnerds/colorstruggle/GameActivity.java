package com.nordicnerds.colorstruggle;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.telephony.TelephonyManager;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.*;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class GameActivity extends Activity
{
    private View view_onTouch_Disapear;
    private View decorView;

    private boolean game_switch = false;
    private boolean restoreShieldChargesValue;

    private int pace = 2250;
    private int pace_up = 0;
    private int score = 0;

    private float lastValX = 0;
    private float valix = 0;
    private float newValX;

    private int
            lastrand,
            restoreSoundValue,
            restoreMusicValue;

    private Random randsoundint = new Random();
    private MediaPlayer dropSound;
    private int[] sounds= {R.raw.drop0, R.raw.drop1, R.raw.drop2, R.raw.drop3};

    private ImageView
            imageView_splashsprite_2d34c6,
            imageView_splashsprite_2dbec6,
            imageView_splashsprite_2dc63c,
            imageView_splashsprite_800080,
            imageView_splashsprite_e1e100,
            imageView_splashsprite_ff00f0,
            imageView_splashsprite_ff0000,
            imageView_splashsprite_ff8000,
            imageView_colorpaddern,
            imageView_drop_animation,
            imageView_onTouch_Disapear,
            imageView_deathsprite,
            imageView_shieldIsActive;

    private TextView
            textView_countDown,
            textView_score;

    private MediaPlayer mp_Music;

    private String tokenid;

    private validate validatation;

    private FirebaseDatabase database;
    private FirebaseAuth mAuth;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        decorView = getWindow().getDecorView();
        navi_check();
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_game);

        //------------------------------------------------------------------------------------//
        imageView_splashsprite_2d34c6 = findViewById(R.id.imageView_splashsprite_2d34c6);
        imageView_splashsprite_2dbec6 = findViewById(R.id.imageView_splashsprite_2dbec6);
        imageView_splashsprite_2dc63c = findViewById(R.id.imageView_splashsprite_2dc63c);
        imageView_splashsprite_800080 = findViewById(R.id.imageView_splashsprite_800080);
        imageView_splashsprite_e1e100 = findViewById(R.id.imageView_splashsprite_e1e100);
        imageView_splashsprite_ff00f0 = findViewById(R.id.imageView_splashsprite_ff00f0);
        imageView_splashsprite_ff0000 = findViewById(R.id.imageView_splashsprite_ff0000);
        imageView_splashsprite_ff8000 = findViewById(R.id.imageView_splashsprite_ff8000);
        imageView_colorpaddern = findViewById(R.id.imageView_colorpaddern);
        imageView_drop_animation = findViewById(R.id.imageView_drop_animation);
        imageView_deathsprite = findViewById(R.id.imageView_deathsprite);
        imageView_shieldIsActive = findViewById(R.id.imageView_shieldIsActive);

        textView_score = findViewById(R.id.textView_score);
        textView_countDown = findViewById(R.id.textView_countDown);

        view_onTouch_Disapear = findViewById(R.id.view_onTouch_Disapear);
        imageView_onTouch_Disapear = findViewById(R.id.imageView_onTouch_Disapear);
        //------------------------------------------------------------------------------------//
        database = FirebaseDatabase.getInstance();

        validatation = new validate();

        mp_Music = MediaPlayer.create(this, R.raw.home);
        mp_Music.setLooping(true);

        Typeface custom_font = Typeface.createFromAsset(getAssets(), "font/baloobhaijaan_regular.ttf");
        textView_score.setTypeface(custom_font);

        SharedPreferences PREF_USER_KEY = getSharedPreferences("PREF_USER_KEY", MODE_PRIVATE);
        tokenid = PREF_USER_KEY.getString("tokenid", "");
        System.out.println("tokenid = "+tokenid);

        SharedPreferences PREF_SCORE_KEY = getSharedPreferences("PREF_SCORE_KEY", MODE_PRIVATE);
        restoreShieldChargesValue = PREF_SCORE_KEY.getBoolean("shieldIsActive", false);

        if (restoreShieldChargesValue)
        {
            imageView_shieldIsActive.setVisibility(View.VISIBLE);
        }
        SharedPreferences PREF_SETTINGS_KEY = getSharedPreferences("PREF_SETTINGS_KEY", MODE_PRIVATE); // sound
        restoreSoundValue = PREF_SETTINGS_KEY.getInt("idSoundSwitch", 0);
        restoreMusicValue = PREF_SETTINGS_KEY.getInt("idMusicSwitch", 0);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onStart()
    {
        super.onStart();
        new Thread()
        {
            public void run()
            {
                runOnUiThread(()->{
                    Animation alpha_out = AnimationUtils.loadAnimation(GameActivity.this, R.anim.alpha_out);
                    alpha_out.setAnimationListener(new AnimationListener()
                    {
                        @Override
                        public void onAnimationStart(Animation animation){}
                        @Override
                        public void onAnimationEnd(Animation animation)
                        {
                            view_onTouch_Disapear.setVisibility(View.INVISIBLE);
                            imageView_onTouch_Disapear.setVisibility(View.INVISIBLE);
                        }
                        @Override
                        public void onAnimationRepeat(Animation animation){}
                    });

                    View view_wheelTurner = findViewById(R.id.view_wheelTurner);
                    view_wheelTurner.setOnTouchListener((v, event) ->
                    {
                        switch (event.getAction())
                        {
                            case MotionEvent.ACTION_DOWN:
                                System.out.println("ACTION_DOWN - "+String.valueOf(event.getX()));
                                new Thread()
                                {
                                    public void run()
                                    {
                                        runOnUiThread(()->{
                                            if (!game_switch)
                                            {
                                                view_onTouch_Disapear.startAnimation(alpha_out);
                                                imageView_onTouch_Disapear.startAnimation(alpha_out);
                                                System.out.println("Start game!");
                                                countdown();
                                                game_switch = true;
                                            }
//                              System.out.println("Start = "+valix);
                                        });
                                    }
                                }.start();
                                valix = event.getX(0)/2;
                                return true;

                            case MotionEvent.ACTION_MOVE:
                                newValX = lastValX+valix - (event.getX(0)/2);
                                if (newValX < 0.0f)
                                    newValX = newValX + 360.0f;
                                else if (newValX > 360.0f)
                                    newValX = newValX - 360.0f;
//                                      System.out.println("valix = "+valix+"\tevent.getX() = "+event.getX()+"\tnewValX = "+newValX);
//                                      System.out.println("Move = "+newValX);
                                RotateAnimation rotate = new RotateAnimation(newValX, newValX,
                                        Animation.RELATIVE_TO_SELF,0.5f,
                                        Animation.RELATIVE_TO_SELF, 0.5f);
                                rotate.setDuration(100);
                                rotate.setFillEnabled(true);
                                rotate.setFillAfter(true);
                                imageView_colorpaddern.startAnimation(rotate);
                                return true;

                            case MotionEvent.ACTION_UP:
                                lastValX = newValX;
                                System.out.println("lastValX = "+lastValX);
                                return true;
                        }
                        return false;
                    });
                });
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
    public void onResume()
    {
        super.onResume();
        if (!game_switch)
        {
            repeat_anim();
        }
        if (restoreMusicValue == 0)
        {
            mp_Music.start();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (restoreMusicValue == 0)
        {
            mp_Music.pause();
        }
    }

    @Override
    public void onBackPressed()
    {
        System.out.println("Puha!");
    }

    private boolean tstbol = true;
    private void repeat_anim()
    {
        new Thread()
        {
            public void run()
            {
                while(!game_switch)
                    while (tstbol)
                    {
                        tstbol = false;
                        runOnUiThread(()-> imageView_onTouch_Disapear.animate().translationX(100).setDuration(600).setListener(new AnimatorListenerAdapter()
                        {
                            @Override
                            public void onAnimationEnd(Animator animation)
                            {
                                super.onAnimationEnd(animation);
                                runOnUiThread(()-> imageView_onTouch_Disapear.animate().translationX(-100).setDuration(600).setListener(new AnimatorListenerAdapter()
                                {
                                    @Override
                                    public void onAnimationEnd(Animator animation)
                                    {
                                        super.onAnimationEnd(animation);
                                        tstbol = true;
                                    }
                                }));
                            }
                        }));
                    }
            }
        }.start();
    }

    private void countdown()
    {
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f); fadeIn.setDuration(500); fadeIn.setFillAfter(true);
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f); fadeOut.setDuration(500); fadeOut.setFillAfter(true);
        new Thread()
        {
            public void run()
            {
                runOnUiThread(() -> new CountDownTimer(3500, 1000)
                {
                    @SuppressLint("SetTextI18n")
                    public void onTick(long millisUntilFinished)
                    {
//                        System.out.println("millisUntilFinished = "+millisUntilFinished);
                        textView_countDown.setText(""+millisUntilFinished / 1000);
                        textView_countDown.startAnimation(fadeIn);
                        textView_countDown.startAnimation(fadeOut);
                    }

                    @SuppressLint("SetTextI18n")
                    public void onFinish()
                    {
                        textView_countDown.setText("GO!");
                        fadeIn.setDuration(100); textView_countDown.startAnimation(fadeIn);
                        fadeOut.setDuration(1000); textView_countDown.startAnimation(fadeOut);
                        startgame();
                        textView_score.setVisibility(View.VISIBLE);
                        fadeIn.setDuration(1000);
                        textView_score.startAnimation(fadeIn);
                    }
                }.start());
            }
        }.start();
    }

    private void startgame()
    {
        new Thread()
        {
            public void run()
            {
                pace_up++;
                if (pace_up == 2)
                {
                    if (pace <= 1000)
                    {
                        System.out.println("Max pace reached!");
                        random_integer();
                        return;
                    }
                    pace = pace - 25;
                    pace_up = 0;
                } // Faster!!
                random_integer();
            }
        }.start();
    }

    private int rnd_int_color_last;
    private void random_integer()
    {
        Random ran = new Random();

        int rnd_int_color = ran.nextInt(8);
        if (rnd_int_color == rnd_int_color_last)
        {
            rnd_int_color = ran.nextInt(8); // anti duplicate
        }
        rnd_int_color_last = rnd_int_color;
        dropAnimation(rnd_int_color, pace);
    }

    @SuppressLint("SetTextI18n")
    private void update_score(int score_int)
    {
        textView_score.setText(""+score_int);
    }

    private void dropAnimation(int color_int, long pace)
    {
        new Thread()
        {
            public void run()
            {
                runOnUiThread(() ->
                {
                    switch (color_int)
                    {
                        case 0:
                            imageView_drop_animation.setImageResource(R.drawable.splashsprite_2d34c6_drop);
                            break;
                        case 1:
                            imageView_drop_animation.setImageResource(R.drawable.splashsprite_2dbec6_drop);
                            break;
                        case 2:
                            imageView_drop_animation.setImageResource(R.drawable.splashsprite_2dc63c_drop);
                            break;
                        case 3:
                            imageView_drop_animation.setImageResource(R.drawable.splashsprite_800080_drop);
                            break;
                        case 4:
                            imageView_drop_animation.setImageResource(R.drawable.splashsprite_e1e100_drop);
                            break;
                        case 5:
                            imageView_drop_animation.setImageResource(R.drawable.splashsprite_ff00f0_drop);
                            break;
                        case 6:
                            imageView_drop_animation.setImageResource(R.drawable.splashsprite_ff0000_drop);
                            break;
                        case 7:
                            imageView_drop_animation.setImageResource(R.drawable.splashsprite_ff8000_drop);
                            break;
                    }
                });
            }
        }.start();

        int imageView_colorpaddern_view_top = imageView_colorpaddern.getTop()-imageView_drop_animation.getMeasuredHeight()+7;
        System.out.println(imageView_colorpaddern.getTop()+" - "+imageView_drop_animation.getMeasuredHeight());
        new Thread()
        {
            public void run()
            {
                runOnUiThread(() ->
                        imageView_drop_animation.animate().setInterpolator(new AccelerateInterpolator()).translationY(imageView_colorpaddern_view_top).setDuration(pace)
                                .setListener(new AnimatorListenerAdapter()
                                {
                                    @Override
                                    public void onAnimationEnd(Animator animation)
                                    {
                                        super.onAnimationEnd(animation);
                                        validateScore(color_int);
                                    }
                                }));
            }
        }.start();
    }

    void shield_lost()
    {
        imageView_shieldIsActive.setVisibility(View.INVISIBLE);
        SharedPreferences.Editor editor_Shield = getSharedPreferences("PREF_SCORE_KEY", MODE_PRIVATE).edit();
        editor_Shield.putBoolean("shieldIsActive", false);
        editor_Shield.apply();
        SharedPreferences PREF_SCORE_KEY = getSharedPreferences("PREF_SCORE_KEY", MODE_PRIVATE);
        restoreShieldChargesValue = PREF_SCORE_KEY.getBoolean("shieldIsActive", false);
    }

    private void validateScore(int color_int) // (int progressChangedValue, int color_int)
    {
        new Thread()
        {
            public void run()
            {
                runOnUiThread(()->{

                    validatation.validateScore(color_int, newValX, restoreShieldChargesValue);
                    switch (validatation.validateScore(color_int, newValX, restoreShieldChargesValue))
                    {
                        case 0:
                            splashAnimation(color_int);
                            break;
                        case 1:
                            shield_lost();
                            splashAnimation(color_int);
                            break;
                        case 2:
                            game_death_leaveActivity();
                            break;
                    }
                });
            }
        }.start();
    }

    void splashAnimation(int color_int)
    {
        new Thread()
        {
            public void run()
            {
                runOnUiThread(() ->
                {
                    score++;
                    switch (color_int)
                    {
                        case 0: // 2d34c6
                            update_score(score);
                            animation_shortned(imageView_splashsprite_2d34c6);
                            break;

                        case 1: // 2dbec6
                            update_score(score);
                            animation_shortned(imageView_splashsprite_2dbec6);
                            break;

                        case 2: // 2dc63c
                            update_score(score);
                            animation_shortned(imageView_splashsprite_2dc63c);
                            break;

                        case 3: // 800080
                            update_score(score);
                            animation_shortned(imageView_splashsprite_800080);
                            break;

                        case 4: // e1e100
                            update_score(score);
                            animation_shortned(imageView_splashsprite_e1e100);
                            break;

                        case 5: // ff00f0
                            update_score(score);
                            animation_shortned(imageView_splashsprite_ff00f0);
                            break;

                        case 6: // ff0000
                            update_score(score);
                            animation_shortned(imageView_splashsprite_ff0000);
                            break;

                        case 7: // ff8000
                            update_score(score);
                            animation_shortned(imageView_splashsprite_ff8000);
                            break;
                    }

                    new Thread()
                    {
                        public void run()
                        {
                            try
                            {
                                if (restoreSoundValue == 0)
                                {
                                    int rndm = randsoundint.nextInt(4);
                                    if (rndm == lastrand)
                                    {
                                        rndm = randsoundint.nextInt(4);
                                    }
                                    lastrand = rndm;
                                    System.out.println("rndm = "+rndm);
                                    dropSound = MediaPlayer.create(getApplicationContext(), sounds[rndm]);
                                    dropSound.start();
                                    dropSound.setOnCompletionListener(MediaPlayer::release);
                                }
                                runOnUiThread(() -> imageView_drop_animation.animate().translationY(0).setDuration(0).setListener(null));
                                Thread.sleep(100);
                                startgame();
                            }
                            catch (InterruptedException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                });
            }
        }.start();
    }

    private void animation_shortned(ImageView imageView_splashsprite_)
    {
        new Thread()
        {
            public void run()
            {
                runOnUiThread(() ->
                {
                    AnimationDrawable animation_imageView_splashsprite_ = (AnimationDrawable) imageView_splashsprite_.getDrawable();
                    animation_imageView_splashsprite_.stop();
                    imageView_splashsprite_.clearAnimation();
                    animation_imageView_splashsprite_.start();
                });
            }
        }.start();
    }

    private void finish_animation()
    {
        AnimationDrawable imageView_deathsprite_anim = (AnimationDrawable) imageView_deathsprite.getDrawable();
        imageView_deathsprite_anim.stop();
        imageView_deathsprite.clearAnimation();
        if (restoreSoundValue == 0)
        {
            MediaPlayer vapeSound = MediaPlayer.create(this, R.raw.vape_sound);
            vapeSound.start();
            vapeSound.setOnCompletionListener(MediaPlayer::release);
        }
        imageView_deathsprite_anim.start();
    }

    private void finish_this()
    {
        imageView_drop_animation.animate().translationY(0).setDuration(0).setListener(null);
        finish_animation();
        Handler handler = new Handler();
        handler.postDelayed(()->{
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }, 1000);
    }

    public static Boolean isMobileAvailable(Context appcontext)
    {
        TelephonyManager tel = (TelephonyManager) appcontext.getSystemService(Context.TELEPHONY_SERVICE);
        return (tel.getNetworkOperator() == null || !tel.getNetworkOperator().equals(""));
    }

    private void game_death_leaveActivity()
    {
        DatabaseReference myRef = database.getReference(tokenid);
        System.out.println("tokenid = "+tokenid);
        SharedPreferences PREF_SCORE_KEY = getSharedPreferences("PREF_SCORE_KEY", MODE_PRIVATE);
        int restoreHighScore = PREF_SCORE_KEY.getInt("idHighScore", 0);
        if (restoreHighScore < score)
        {
            new Thread()
            {
                public void run()
                {
                    SharedPreferences.Editor editor_Highscore = getSharedPreferences("PREF_SCORE_KEY", MODE_PRIVATE).edit();
                    editor_Highscore.putInt("idHighScore", score);
                    editor_Highscore.apply();

                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (isMobileAvailable(GameActivity.this) && currentUser != null)
                    {
                        myRef.child("score").setValue(score);
                    }
                }
            }.start();
        }
        SharedPreferences.Editor editor_LastScore = getSharedPreferences("PREF_SCORE_KEY", MODE_PRIVATE).edit();
        editor_LastScore.putInt("idLastScore", score);
        editor_LastScore.apply();
        finish_this();
    }
}