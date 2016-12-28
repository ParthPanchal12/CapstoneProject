package nanodegree.example.com.capstoneproject.activity;


import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import nanodegree.example.com.capstoneproject.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment {


    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout v = (LinearLayout) super.onCreateView(inflater, container, savedInstanceState);

        Button previewBtn = new Button(getActivity().getApplicationContext());
        previewBtn.setText(R.string.button_preview);
        previewBtn.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        previewBtn.setTextColor(getResources().getColor(R.color.material_light_white));
        previewBtn.setPadding(100,10,100,10);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(60, 30, 60, 30);
        layoutParams.gravity= Gravity.CENTER;
        previewBtn.setLayoutParams(layoutParams);
        if(v!=null)
            v.addView(previewBtn);

        previewBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), nanodegree.example.com.capstoneproject.activity.TeleprompterActivity.class);
                intent.putExtra(nanodegree.example.com.capstoneproject.activity.TypeActivity.EXTRA_TELETEXT, getString(R.string.blind_text));
                startActivity(intent);
            }
        });

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
