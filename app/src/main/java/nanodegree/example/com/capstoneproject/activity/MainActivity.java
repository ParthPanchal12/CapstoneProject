package nanodegree.example.com.capstoneproject.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import butterknife.BindView;
import butterknife.ButterKnife;
import nanodegree.example.com.capstoneproject.BuildConfig;
import nanodegree.example.com.capstoneproject.R;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {
    public static final String EXTRA_DOCUMENT = "nanodegree.example.com.capstoneproject.IMPORTED";
    public static final String EXTRA_DOCUMENT_TITLE = "nanodegree.example.com.capstoneproject.IMPORTED_TITLE";
    @BindView(R.id.toolbar_main)
    Toolbar mToolbar;
    @BindView(R.id.document_list)
    ListView mListView;
    @BindView(R.id.empty_state_container)
    LinearLayout emptyLayout;
    private String importedText = BuildConfig.FLAVOR;
    private String importedTitle = BuildConfig.FLAVOR;
    private InterstitialAd mInterstitialAd;
    private SimpleCursorAdapter simpleCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mToolbar.setTitle(R.string.app_name);
        setSupportActionBar(mToolbar);
        simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.list_item, null, new String[]{nanodegree.example.com.capstoneproject.database.MyContentProvider.Contracts.fileInfo.FILE_NAME,nanodegree.example.com.capstoneproject.database.MyContentProvider.Contracts.fileInfo.FILE_PATH}, new int[]{R.id.file_name, R.id.hidden_text}, 0);
        mListView.setAdapter(simpleCursorAdapter);
        getSupportLoaderManager().initLoader(1, null, this);
        mListView.setOnItemClickListener(this);
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.interstitial_ad_unit_id));
        requestNewInterstitial();
        Log.e("something","something");
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
        startActivity(new Intent(this, nanodegree.example.com.capstoneproject.activity.SettingsActivity.class));
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, nanodegree.example.com.capstoneproject.database.MyContentProvider.Contracts.fileInfo.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        ((SimpleCursorAdapter) mListView.getAdapter()).swapCursor(data);
        updateEmptyView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        ((SimpleCursorAdapter) mListView.getAdapter()).swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor cursor = (Cursor) simpleCursorAdapter.getItem(position);
        importedTitle = cursor.getString(1);
        read_file(importedTitle);
    }

    public void read_file(String filename) {

        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... params) {
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openFileInput(params[0]), "UTF-8"));
                    StringBuilder sb = new StringBuilder();
                    while (true) {
                        String line = bufferedReader.readLine();
                        if (line == null) {
                            return sb.toString();
                        }
                        sb.append(line).append("\n");
                    }
                } catch (FileNotFoundException e) {
                    return BuildConfig.FLAVOR;
                } catch (UnsupportedEncodingException e2) {
                    return BuildConfig.FLAVOR;
                } catch (IOException e3) {
                    return BuildConfig.FLAVOR;
                }
            }

            @Override
            protected void onPostExecute(String s) {
                importedText = s;
                openDocument();
            }
        }.execute(filename);
    }

    private void openDocument() {
        Intent intent = new Intent(this, nanodegree.example.com.capstoneproject.activity.TypeActivity.class);
        intent.putExtra(EXTRA_DOCUMENT, importedText);
        intent.putExtra(EXTRA_DOCUMENT_TITLE, importedTitle);
        startActivity(intent);
    }

    public void newDocument(View view) {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
        Intent intent = new Intent(this, nanodegree.example.com.capstoneproject.activity.TypeActivity.class);
        intent.putExtra(EXTRA_DOCUMENT, BuildConfig.FLAVOR);
        intent.putExtra(EXTRA_DOCUMENT_TITLE, BuildConfig.FLAVOR);
        startActivity(intent);
    }

    private void updateEmptyView() {
        if (simpleCursorAdapter.getCount() == 0) {
            emptyLayout.setVisibility(View.VISIBLE);
        } else {
            emptyLayout.setVisibility(View.INVISIBLE);
        }
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("YOUR-DEVICE")
                .build();
        mInterstitialAd.loadAd(adRequest);
    }

}
