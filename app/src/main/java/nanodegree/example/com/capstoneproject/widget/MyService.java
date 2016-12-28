package nanodegree.example.com.capstoneproject.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import nanodegree.example.com.capstoneproject.R;
import nanodegree.example.com.capstoneproject.database.MyContentProvider;

public class MyService extends RemoteViewsService {
    public MyService() {
    }


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new MYRemoteViewFactory(getApplicationContext(), intent);

    }

    class MYRemoteViewFactory implements RemoteViewsService.RemoteViewsFactory {

        private Context context;
        private Cursor cursor;

        public MYRemoteViewFactory(Context context, Intent intent) {
            this.context = context;
        }

        @Override
        public void onCreate() {
            cursor = context.getContentResolver().query(
                    MyContentProvider.Contracts.fileInfo.CONTENT_URI,
                    new String[]{MyContentProvider.Contracts.fileInfo.FILE_NAME, MyContentProvider.Contracts.fileInfo.FILE_PATH},
                    null, null, null
            );
        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return cursor.getCount();
        }

        @Override
        public RemoteViews getViewAt(int i) {
            cursor.moveToPosition(i);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.list_item);
            remoteViews.setTextColor(R.id.file_name, getResources().getColor(R.color.material_light_black));
            remoteViews.setTextViewText(R.id.file_name, cursor.getString(cursor.getColumnIndex(MyContentProvider.Contracts.fileInfo.FILE_NAME)));
            Intent intent = new Intent();
            intent.putExtra("content_add", cursor.getString(cursor.getColumnIndex(MyContentProvider.Contracts.fileInfo.FILE_PATH)));
            remoteViews.setOnClickFillInIntent(R.id.file_name, intent);
            return remoteViews;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
