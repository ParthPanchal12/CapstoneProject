package nanodegree.example.com.capstoneproject;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by Parth Panchal on 12/22/2016.
 */

public class SeekBarPreference extends DialogPreference implements SeekBar.OnSeekBarChangeListener {

    private static final String androidns = "http://schemas.android.com/apk/res/android";
    private static final String attributeString = "http://attributeString.com";
    private Context mContext;
    private int mDefault;
    private String mDialogMessage;
    private int mMax;
    private int mMin;
    private SeekBar mSeekBar;
    private TextView mSplashText;
    private String mSuffix;
    private int mValue;
    private TextView mValueText;
    private boolean showSize;

    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mValue = 0;
        mContext = context;
        mDialogMessage = attrs.getAttributeValue(androidns, "dialogMessage");
        mSuffix = attrs.getAttributeValue(androidns, "text");
        mDefault = attrs.getAttributeIntValue(androidns, "defaultValue", 0);
        mMin = attrs.getAttributeIntValue(attributeString, "min", 0);
        mMax = attrs.getAttributeIntValue(androidns, "max", 100) - mMin;
        showSize = attrs.getAttributeBooleanValue(attributeString, "showSize", false);
    }

    @Override
    protected View onCreateDialogView() {
        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(6, 6, 6, 6);
        mSplashText = new TextView(mContext);
        if (mDialogMessage != null) {
            mSplashText.setText(mDialogMessage);
        }
        layout.addView(mSplashText);
        mValueText = new TextView(mContext);
        mValueText.setGravity(View.TEXT_ALIGNMENT_CENTER);
        mValueText.setTextSize(32.0f);
        layout.addView(mValueText, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mSeekBar = new SeekBar(mContext);
        mSeekBar.setOnSeekBarChangeListener(this);
        layout.addView(mSeekBar, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        if (shouldPersist()) {
            mValue = getPersistedInt(mDefault);
        }
        mSeekBar.setMax(mMax);
        mSeekBar.setProgress(mValue);
        return layout;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);
        mSeekBar.setMax(mMax);
        mSeekBar.setProgress(mValue);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        super.onSetInitialValue(restorePersistedValue, defaultValue);
        if (restorePersistedValue) {
            mValue = shouldPersist() ? getPersistedInt(mDefault) : 0;
        } else {
            mValue = (Integer) defaultValue;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        String t = String.valueOf(mMin + progress);
        TextView textView = mValueText;
        if (mSuffix != null) {
            t = t.concat(mSuffix);
        }
        textView.setText(t);
        if (shouldPersist()) {
            persistInt(progress);
        }
        callChangeListener(progress);
        if (showSize) {
            mValueText.setTextSize((float) (mMin + progress));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

}
