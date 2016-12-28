package nanodegree.example.com.capstoneproject.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ScrollView;

import butterknife.BindView;
import butterknife.ButterKnife;
import nanodegree.example.com.capstoneproject.R;


public class TeleprompterActivity extends AppCompatActivity {
    @BindView(R.id.text_teleprompter)
    MirroredTextView mirroredTextView;
    @BindView(R.id.scrollview_teleprompter)
    ScrollView scrollView;
    private Handler customHandler;
    private Runnable scroll;
    private int scrollSpeed;
    private boolean scrollText;
    private int time;
    private int timer;

    public TeleprompterActivity() {
        scroll = new Runnable() {
            @Override
            public void run() {
                if (scrollText) {
                    if (timer > 0) {
                        timer = timer - 1;
                    } else {
                        scrollView.scrollTo(0, scrollView.getScrollY() + 1);
                        timer = time;
                    }
                    customHandler.post(scroll);
                }
            }
        };

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teleprompter);
        ButterKnife.bind(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        Intent intent = getIntent();
        String message = intent.getStringExtra(nanodegree.example.com.capstoneproject.activity.TypeActivity.EXTRA_TELETEXT);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        scrollSpeed = 101 - prefs.getInt("pref_speed", 50);
        MirroredTextView.mirror = prefs.getBoolean("pref_mirror", true);
        mirroredTextView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/" + prefs.getString("pref_font", "DroidSans") + ".ttf"), 1);
        mirroredTextView.setTextSize((float)prefs.getInt("pref_fontsize", 24));
        mirroredTextView.setText(message);
        mirroredTextView.setTextColor(Color.parseColor(prefs.getString("pref_txtcolor", "#FFFFFF")));
        mirroredTextView.setBackgroundColor(Color.parseColor(prefs.getString("pref_bgcolor", "#000000")));
        scrollView.setBackgroundColor(Color.parseColor(prefs.getString("pref_bgcolor", "#000000")));
        time = this.scrollSpeed * 100;
        scrollText = false;
        scrollView.getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tap();
            }
        });
        customHandler = new Handler();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            tap();
        }
        return super.dispatchKeyEvent(event);
    }

    private void tap() {
            scrollText = !scrollText;
            customHandler.post(scroll);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (customHandler != null) {
            customHandler.removeCallbacksAndMessages(null);
        }
        scrollText = false;
    }
}

