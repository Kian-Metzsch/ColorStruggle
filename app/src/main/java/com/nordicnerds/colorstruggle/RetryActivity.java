package com.nordicnerds.colorstruggle;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.*;
import android.widget.*;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import static java.lang.Math.toIntExact;

public class RetryActivity extends Activity implements RewardedVideoAdListener
{
    private SharedPreferences PREF_SCORE_KEY;
    private SharedPreferences PREF_SETTINGS_KEY;

    private boolean settings_switch = false;
    private boolean retry_click = false;
    private boolean restoreShieldChargesValue;
    private boolean logoutswitch;

    private int X_ach;
    private int restoreLastScore;
    private int restoreHighScore;
    private int restoreMusicValue;
    private int restoreSoundValue;

    private ImageView imageView_settings;

    private ImageButton imageButton_settings;
    private ImageButton imageButton_achivement;
    private ImageButton imageButton_sound_onOff;
    private ImageButton imageButton_music_onOff;
    private ImageButton imageButton_retry;
    private ImageButton imageButton_logout_login;

    private Button button_shield;

    private TextView textView_highscore;
    private TextView textView_lastscore;

    private View decorView;

    private RewardedVideoAd mRewardedVideoAd_shield;
    private CallbackManager callbackManager;

    private FirebaseAuth mAuth;

    private TextView textView_UserId;

    private ValueEventListener dataListener;

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        try
        {
            @SuppressLint("PackageManagerGetSignatures")
            PackageInfo info = getPackageManager().getPackageInfo("com.nordicnerds.colorstruggle", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures)
            {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                System.out.println("KeyHash: "+ Base64.encodeToString(md.digest(), Base64.DEFAULT));
//                Toast.makeText(this, "KeyHash: "+ Base64.encodeToString(md.digest(), Base64.DEFAULT), Toast.LENGTH_LONG).show();
            }
        }
        catch (PackageManager.NameNotFoundException e)
        {
            System.out.println("KeyHash: "+ e);
        }
        catch (NoSuchAlgorithmException e)
        {
            System.out.println("KeyHash: "+ e);
        }

        decorView = getWindow().getDecorView();
        facebook_login();
        navi_check();
        setContentView(R.layout.activity_retry);

        mAuth = FirebaseAuth.getInstance();

        mRewardedVideoAd_shield = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd_shield.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();

        //------------------------------------------------------------------------------------//
        imageView_settings = findViewById(R.id.imageView_settings);
        button_shield = findViewById(R.id.button_shield);
        imageButton_settings = findViewById(R.id.imageButton_settings);
        imageButton_achivement = findViewById(R.id.imageButton_achivement);
        imageButton_sound_onOff = findViewById(R.id.imageButton_sound_onOff);
        imageButton_music_onOff = findViewById(R.id.imageButton_music_onOff);
        imageButton_retry = findViewById(R.id.imageButton_retry);
        imageButton_logout_login = findViewById(R.id.imageButton_logout_login);

        textView_highscore = findViewById(R.id.textView_highscore);
        textView_lastscore = findViewById(R.id.textView_lastscore);
        textView_UserId = findViewById(R.id.textView_UserId);

        //------------------------------------------------------------------------------------//

        Typeface custom_font = Typeface.createFromAsset(getAssets(), "font/baloobhaijaan_regular.ttf");
        textView_highscore.setTypeface(custom_font);
        textView_lastscore.setTypeface(custom_font);
        textView_UserId.setTypeface(custom_font);

        PREF_SCORE_KEY = getSharedPreferences("PREF_SCORE_KEY", MODE_PRIVATE); // Score
        restoreShieldChargesValue = PREF_SCORE_KEY.getBoolean("shieldIsActive", false);
        restoreLastScore = PREF_SCORE_KEY.getInt("idLastScore", 0);
        restoreHighScore = PREF_SCORE_KEY.getInt("idHighScore", 0);

        PREF_SETTINGS_KEY = getSharedPreferences("PREF_SETTINGS_KEY", MODE_PRIVATE);
        restoreMusicValue = PREF_SETTINGS_KEY.getInt("idMusicSwitch", 0);
        restoreSoundValue = PREF_SETTINGS_KEY.getInt("idSoundSwitch", 0);

        if(restoreShieldChargesValue)
        {
            button_shield.setVisibility(View.INVISIBLE);
        }

        imageView_settings.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @SuppressLint("NewApi")
            @Override
            public void onGlobalLayout()
            {
                imageView_settings.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                Rect rect_ach = new Rect(); imageButton_achivement.getLocalVisibleRect(rect_ach);
                imageButton_achivement.getGlobalVisibleRect(rect_ach);

                int[] location = new int[2];
                imageButton_achivement.getLocationOnScreen(location);
                int x_ach = location[0];

                X_ach = -(x_ach+imageButton_achivement.getMeasuredWidth());

                settings_switch = true;
                settings_move(0);
            }
        });

        textView_lastscore.setText("Last score: "+restoreLastScore);

        if (restoreSoundValue != 0)
        {
            System.out.println("restoreSoundValue = "+restoreSoundValue);
            imageButton_sound_onOff.setBackgroundResource(R.drawable.sound_off_icon);
        }
        if (restoreMusicValue != 0)
        {
            System.out.println("restoreSoundValue = "+restoreSoundValue);
            imageButton_music_onOff.setBackgroundResource(R.drawable.music_off_icon);
        }

        FirebaseAuth.AuthStateListener mAuthListener = firebaseAuth ->
        {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            updateUI(user);
        };
        mAuth.addAuthStateListener(mAuthListener);
    } // ----------------------------------------------------------------------------------------------->

    int scoreChangeTo;
    long scoreChangeToFloat;
    DatabaseReference myRef;
    @SuppressLint("SetTextI18n")
    private void checkHighScore()
    {
        AccessToken accessTokentok = AccessToken.getCurrentAccessToken();
        if (accessTokentok != null)
        {
            myRef = database.getReference(accessTokentok.getUserId());
            dataListener = new ValueEventListener()
            {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    System.out.println("dataSnapshot = "+dataSnapshot.getValue());
                    if (dataSnapshot.getValue() != null)
                    {
                        scoreChangeToFloat = (long) dataSnapshot.getValue();
                        scoreChangeTo = toIntExact(scoreChangeToFloat);
                        if (restoreHighScore > scoreChangeTo)
                        {
                            System.out.println("Local score is higher!");
                            DatabaseReference myRef = database.getReference(accessTokentok.getUserId());
                            myRef.child("score").setValue(restoreHighScore);
                            textView_highscore.setText("High score: "+restoreHighScore);
                        }
                        else
                        {
                            SharedPreferences.Editor editor_Highscore = getSharedPreferences("PREF_SCORE_KEY", MODE_PRIVATE).edit();
                            editor_Highscore.putInt("idHighScore", scoreChangeTo);
                            editor_Highscore.apply();
                            textView_highscore.setText("High score: "+scoreChangeTo);
                        }
                        System.out.println("scoreChangeTo = "+scoreChangeTo);
                    }
                    else
                    {
                        System.out.println("scoreChangeTo = "+scoreChangeTo);
                        textView_highscore.setText("High score: "+PREF_SCORE_KEY.getInt("idHighScore", 0));
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError)
                {
                    System.out.println("loadPost:onCancelled"+ databaseError.toException());
                }
            };
            myRef.child("score").addValueEventListener(dataListener);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart()
    {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (isMobileAvailable(this) && currentUser != null)
        {
            checkHighScore();
        }
        if (currentUser != null)
        {
            imageButton_logout_login.setBackgroundResource(R.drawable.logout_image);
            logoutswitch = true;
        }
        updateUI(currentUser);
        checkHighScore();
    }

    private void updateUI(FirebaseUser user)
    {
        if (user!=null)
        {
            textView_UserId.setText(user.getDisplayName());
            textView_UserId.setVisibility(View.VISIBLE);
        }
        else
        {
            textView_UserId.setVisibility(View.INVISIBLE);
        }
    }

    private void facebook_login() // login first time validation
    {
        Handler facebook_handler = new Handler();
        facebook_handler.postDelayed(()->
        {
            callbackManager = CallbackManager.Factory.create();
            LoginButton loginButton = findViewById(R.id.login_button);
            loginButton.setReadPermissions("public_profile");
            loginButton.setReadPermissions("email");
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>()
            {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onSuccess(LoginResult loginResult)
                {
                    handleFacebookAccessToken(loginResult.getAccessToken());
                }

                @Override
                public void onCancel()
                {
                    System.out.println("facebook:onCancel");
                }

                @Override
                public void onError(FacebookException error)
                {
                    System.out.println("facebook:onError"+ error);
                }
            });
        }, 50);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void handleFacebookAccessToken(AccessToken token)
    {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task ->
        {
            if (task.isSuccessful())
            {
                FirebaseUser user = mAuth.getCurrentUser();
                updateUI(user);
                checkHighScore();

                DatabaseReference myRef = database.getReference(token.getUserId());
                assert user != null;
                myRef.child("name").setValue(Objects.requireNonNull(user.getDisplayName()));
                SharedPreferences.Editor PREF_USER_KEY = getSharedPreferences("PREF_USER_KEY", MODE_PRIVATE).edit();
                PREF_USER_KEY.putString("tokenid", token.getUserId());
                PREF_USER_KEY.apply();

                imageButton_logout_login.setBackgroundResource(R.drawable.logout_image);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("SetTextI18n")
    @Override
    public void onResume()
    {
        super.onResume();
        if (retry_click)
        {
            imageButton_retry.setBackgroundResource(R.drawable.retry_image);
        }

        restoreLastScore = PREF_SCORE_KEY.getInt("idLastScore", 0);
        restoreHighScore = PREF_SCORE_KEY.getInt("idHighScore", 0);
        textView_lastscore.setText("Last score: "+restoreLastScore);
        textView_highscore.setText("High score: "+restoreHighScore);

        restoreShieldChargesValue = PREF_SCORE_KEY.getBoolean("shieldIsActive", false);

        if(restoreShieldChargesValue)
        {
            button_shield.setVisibility(View.INVISIBLE);
        }
        else
        {
            button_shield.setVisibility(View.VISIBLE);
        }
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

    private void click_sound()
    {
        SharedPreferences PREF_SETTINGS_KEY = getSharedPreferences("PREF_SETTINGS_KEY", MODE_PRIVATE); // sound
        restoreSoundValue = PREF_SETTINGS_KEY.getInt("idSoundSwitch", 0);
        if (restoreSoundValue == 0)
        {
            MediaPlayer mp_button_click = MediaPlayer.create(this, R.raw.button_click);
            mp_button_click.start();
            mp_button_click.setOnCompletionListener(MediaPlayer::release);
        }
    }

    public void retry_click(View view)
    {
        click_sound();
        imageButton_retry.startAnimation(AnimationUtils.loadAnimation(this, R.anim.onclick_animation));
        new Thread()
        {
            public void run()
            {
                runOnUiThread(() ->
                {
                    startActivity(new Intent(RetryActivity.this,GameActivity.class));
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    retry_click = true;
                });
            }
        }.start();
    }

    public void menu_click(View view)
    {
        click_sound();
        settings_move(250);
        imageButton_settings.startAnimation(AnimationUtils.loadAnimation(this, R.anim.onclick_animation));
    }

    private void settings_move(long delay)
    {
        if (settings_switch)
        {
            imageButton_achivement.animate().translationX(X_ach).setDuration(delay);
            //imageButton_logout_login.animate().translationX(X_ach).setDuration(delay);
            imageButton_sound_onOff.animate().translationX(X_ach).setDuration(delay);
            imageButton_music_onOff.animate().translationX(X_ach).setDuration(delay);
            imageView_settings.animate().translationX(-imageView_settings.getMeasuredWidth()).setDuration(delay);
            settings_switch = false;
        }
        else
        {
            imageButton_achivement.animate().translationX(0).setDuration(delay);
            //imageButton_logout_login.animate().translationX(0).setDuration(delay);
            imageButton_sound_onOff.animate().translationX(0).setDuration(delay);
            imageButton_music_onOff.animate().translationX(0).setDuration(delay);
            imageView_settings.animate().translationX(0).setDuration(delay);
            settings_switch = true;
        }
    }

    public static Boolean isMobileAvailable(Context appcontext)
    {
        TelephonyManager tel = (TelephonyManager) appcontext.getSystemService(Context.TELEPHONY_SERVICE);
        return (tel.getNetworkOperator() == null || !tel.getNetworkOperator().equals(""));
    }

    public void menu_leaderboards(View view)
    {
        click_sound();
        Intent intent_leaderboards = new Intent(this, LeaderboardsActivity.class);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        System.out.println("Mobile has internet = "+isMobileAvailable(this));
        if (isMobileAvailable(this))
        {
            if (currentUser!=null)
            {
                startActivity(intent_leaderboards);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                imageButton_achivement.startAnimation(AnimationUtils.loadAnimation(this, R.anim.onclick_animation));
            }
            else
            {
                Toast.makeText(this, "Error, not logged in!", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(this, "Error, no internet!", Toast.LENGTH_SHORT).show();
        }
    }

    private void menu_sound_music(int restoreVlaue, ImageButton imgbtn, int drawable, String prefKey)
    {
        if (restoreVlaue == 0)
        {
            imgbtn.setBackgroundResource(drawable);
            restoreVlaue = 1;
            System.out.println("restoreVlaue = "+restoreVlaue);
        }
        else
        {
            imgbtn.setBackgroundResource(drawable);
            restoreVlaue = 0;
            System.out.println("restoreVlaue = "+restoreVlaue);
        }
        imgbtn.startAnimation(AnimationUtils.loadAnimation(this, R.anim.onclick_animation));
        SharedPreferences.Editor editor_Settings = getSharedPreferences("PREF_SETTINGS_KEY", MODE_PRIVATE).edit();
        editor_Settings.putInt(prefKey, restoreVlaue);
        editor_Settings.apply();
        click_sound();
    }

    private void logout()
    {
        myRef.removeEventListener(dataListener);
        LoginManager.getInstance().logOut();
        FirebaseAuth.getInstance().signOut();
        imageButton_logout_login.setBackgroundResource(R.drawable.fb_icon);
        logoutswitch = false;

        SharedPreferences.Editor editor_Highscore = getSharedPreferences("PREF_SCORE_KEY", MODE_PRIVATE).edit();
        editor_Highscore.putInt("idHighScore", 0);
        textView_highscore.setText(getString(R.string.high_score_reset));
        editor_Highscore.putInt("idLastScore", 0);
        textView_lastscore.setText(getString(R.string.last_score_reset));
        editor_Highscore.apply();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void click_logout_login(View view)
    {
        click_sound();
        imageButton_logout_login.startAnimation(AnimationUtils.loadAnimation(this, R.anim.onclick_animation));
        if (isMobileAvailable(this))
        {
            if (logoutswitch)
            {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
                builder.setTitle("Logout?").setMessage("\nAre you sure you want to logout?")
                        .setPositiveButton("OK", (dialog, which) -> logout()) // true is not stable
                        .setNegativeButton("CANCEL", (dialog, which) -> {})
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
            else
            {
                findViewById(R.id.login_button).performClick();
                logoutswitch = true;
            }
        }
        else
        {
            Toast.makeText(this, "Error, no internet!", Toast.LENGTH_SHORT).show();
        }
    }

    public void menu_music(View view)
    {
        int music_Drawable;
        restoreMusicValue = PREF_SETTINGS_KEY.getInt("idMusicSwitch", 0);
        if (restoreMusicValue == 0){music_Drawable = R.drawable.music_off_icon;}
        else {music_Drawable = R.drawable.music_on_icon;}
        menu_sound_music(restoreMusicValue, imageButton_music_onOff, music_Drawable, "idMusicSwitch");

    }

    public void menu_sound(View view)
    {
        int sound_Drawable;
        restoreSoundValue = PREF_SETTINGS_KEY.getInt("idSoundSwitch", 0);
        if (restoreSoundValue == 0){sound_Drawable = R.drawable.sound_off_icon;}
        else {sound_Drawable = R.drawable.sound_on_icon;}
        menu_sound_music(restoreSoundValue, imageButton_sound_onOff, sound_Drawable, "idSoundSwitch");
    }

    private void loadRewardedVideoAd()
    {
        if (!mRewardedVideoAd_shield.isLoaded())
        {
            mRewardedVideoAd_shield.loadAd("ca-app-pub-9467553631695603/2336748566", new AdRequest.Builder().build());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void ad_shield(View view)
    {
        click_sound();
        button_shield.startAnimation(AnimationUtils.loadAnimation(this, R.anim.onclick_animation));
        if (isMobileAvailable(this))
        {
            restoreShieldChargesValue = PREF_SCORE_KEY.getBoolean("shieldIsActive", false);
            System.out.println("restoreShieldChargesValue = "+restoreShieldChargesValue);
            if (!restoreShieldChargesValue)
            {
                loadRewardedVideoAd();
                if (mRewardedVideoAd_shield.isLoaded())
                {
                    mRewardedVideoAd_shield.show();
                }
            }
        }
        else
        {
            Toast.makeText(this, "Error, no internet!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRewardedVideoAdLoaded(){}

    @Override
    public void onRewardedVideoAdOpened(){}

    @Override
    public void onRewardedVideoStarted(){}

    @Override
    public void onRewardedVideoAdClosed()
    {
        loadRewardedVideoAd();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onRewarded(RewardItem rewardItem)
    {
        SharedPreferences.Editor editor_Shield = getSharedPreferences("PREF_SCORE_KEY", MODE_PRIVATE).edit();
        editor_Shield.putBoolean("shieldIsActive", true);
        editor_Shield.apply();
        button_shield.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onRewardedVideoAdLeftApplication(){}

    @Override
    public void onRewardedVideoAdFailedToLoad(int i){}

    @Override
    public void onRewardedVideoCompleted(){}
}