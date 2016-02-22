package barqsoft.footballscores;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link AppWidgetConfigureActivity AppWidgetConfigureActivity}
 */
public class AppWidget
        extends AppWidgetProvider {

    static void updateAppWidget(
            Context context, AppWidgetManager appWidgetManager,
            int appWidgetId) {

        CharSequence widgetText = AppWidgetConfigureActivity.loadTitlePref(context, appWidgetId);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);

        Cursor data = null;
        Uri todayUri = DatabaseContract.scores_table.buildScoreWithDate();
        String strDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
        data = context.getContentResolver().query(todayUri,
                                                 null,
                                                 null,
                                                 new String[]{strDate},
                                                 DatabaseContract.scores_table.DATE_COL + " ASC");
        String result = "";
        assert data != null;
        if (data.moveToFirst()) {
            views.setTextViewText(R.id.tite_text,
                                  "Today "+
                                  data.getString(data.getColumnIndex(DatabaseContract.scores_table.TIME_COL)));
            views.setTextViewText(R.id.home_name_text,
                                  data.getString(data.getColumnIndex(DatabaseContract.scores_table.HOME_COL)));
            views.setTextViewText(R.id.goals_text,
                                  data.getString(data.getColumnIndex(DatabaseContract.scores_table.HOME_GOALS_COL))+
                                    "-" + data.getString(data.getColumnIndex(DatabaseContract.scores_table.AWAY_GOALS_COL))
            );
            views.setTextViewText(R.id.away_name_text,
                                  data.getString(data.getColumnIndex(DatabaseContract.scores_table.AWAY_COL)));
            result +=  data.getString(data.getColumnIndex(DatabaseContract.scores_table.HOME_COL));
            Log.e("widget", "Result: "+result);
        }
        data.close();

        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0,intent,0);
        views.setOnClickPendingIntent(R.id.widget_linear, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            AppWidgetConfigureActivity.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

