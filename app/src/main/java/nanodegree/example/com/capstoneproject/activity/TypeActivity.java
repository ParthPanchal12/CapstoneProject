package nanodegree.example.com.capstoneproject.activity;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.File;
import java.io.FileOutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import nanodegree.example.com.capstoneproject.AnalyticsApplication;
import nanodegree.example.com.capstoneproject.BuildConfig;
import nanodegree.example.com.capstoneproject.R;
import nanodegree.example.com.capstoneproject.database.MyContentProvider;

public class TypeActivity extends AppCompatActivity {
    public static final String EXTRA_TELETEXT = "nanodegree.example.com.capstoneproject.TELETEXT";
    @BindView(R.id.teletext)
    EditText mTeletext;
    @BindView(R.id.teletext_title)
    EditText mTitle;
    @BindView(R.id.toolbar_type)
    Toolbar toolbar;
    @BindView(R.id.nested_scroll_view)
    NestedScrollView nestedScrollView;
    private String documentTitle;
    private Tracker mTracker;
    private boolean isnew = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.type_activity);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        nestedScrollView.setFillViewport(true);
        documentTitle = getIntent().getStringExtra(MainActivity.EXTRA_DOCUMENT_TITLE);
        mTitle.setText(documentTitle);
        mTeletext.setText(getIntent().getStringExtra(MainActivity.EXTRA_DOCUMENT));
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.setScreenName(getResources().getString(R.string.type_activity));
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Share")
                .build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != R.id.settings) {
            return super.onOptionsItemSelected(item);
        }
        startActivity(new Intent(this, SettingsActivity.class));
        return true;
    }

    public void startPrompter(View view) {
        String teletext = mTeletext.getText().toString();
        if (!teletext.matches(BuildConfig.FLAVOR)) {
            String title = mTitle.getText().toString();
            if (!title.equals(documentTitle)) {
                new File(getFilesDir(), documentTitle).delete();
                getContentResolver().delete(MyContentProvider.Contracts.fileInfo.CONTENT_URI,
                        MyContentProvider.Contracts.fileInfo.FILE_NAME + " = ?", new String[]{documentTitle});
                documentTitle = title;
                isnew = true;
            }
            if (!title.matches(BuildConfig.FLAVOR)) {
                new AsyncTask<String, Void, Void>() {
                    @Override
                    protected Void doInBackground(String... params) {
                        try {
                            FileOutputStream outputStream = openFileOutput(params[0], 0);
                            outputStream.write(params[1].getBytes());
                            outputStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (isnew) {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(MyContentProvider.Contracts.fileInfo.FILE_NAME, params[0]);
                            contentValues.put(MyContentProvider.Contracts.fileInfo.FILE_PATH, params[0]);
                            getContentResolver().insert(MyContentProvider.Contracts.fileInfo.CONTENT_URI, contentValues);
                        }
                        return null;
                    }
                }.execute(title, teletext);

                Intent intent = new Intent(this, TeleprompterActivity.class);
                intent.putExtra(EXTRA_TELETEXT, teletext);
                startActivity(intent);
            }

        }
    }

    public void deleteDocument(View view) {
        showDialog(1);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case 1:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getResources().getString(R.string.dialog_message));
                builder.setCancelable(true);
                builder.setPositiveButton(getResources().getString(R.string.delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new File(getFilesDir(), documentTitle).delete();
                        getContentResolver().delete(MyContentProvider.Contracts.fileInfo.CONTENT_URI,
                                MyContentProvider.Contracts.fileInfo.FILE_NAME + " = ?", new String[]{documentTitle});
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.toast), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();
                break;
        }
        return super.onCreateDialog(id);
    }

}
