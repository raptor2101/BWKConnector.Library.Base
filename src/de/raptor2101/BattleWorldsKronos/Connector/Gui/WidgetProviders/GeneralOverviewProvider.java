package de.raptor2101.BattleWorldsKronos.Connector.Gui.WidgetProviders;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.raptor2101.BattleWorldsKronos.Connector.AbstractConnectorApp;
import de.raptor2101.BattleWorldsKronos.Connector.ApplicationSettings;
import de.raptor2101.BattleWorldsKronos.Connector.Gui.R;
import de.raptor2101.BattleWorldsKronos.Connector.Tasks.GamesLoaderTask;
import de.raptor2101.BattleWorldsKronos.Connector.Tasks.GamesLoaderTask.Result;
import de.raptor2101.BattleWorldsKronos.Connector.Tasks.ServerConnectionTask.ResultListener;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.widget.RemoteViews;

public class GeneralOverviewProvider extends AppWidgetProvider implements ResultListener<Result> {
  int[] mAppWidgetIds;
  AppWidgetManager mAppWidgetManager;
  Context mContext;

  public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    mContext = context;
    mAppWidgetManager = appWidgetManager;
    mAppWidgetIds = appWidgetIds;

    ApplicationSettings settings = new ApplicationSettings(context);
    AbstractConnectorApp app = (AbstractConnectorApp) context.getApplicationContext();
    
    GamesLoaderTask task = new GamesLoaderTask(app, this);
    long lastLoad = app.getDatabase().getTimestampLastGameUpdate();
    
    task.execute(new Boolean[] { SystemClock.elapsedRealtime()-lastLoad>settings.getRefreshCylce() });
  }

  @Override
  public void handleResult(Result result) {
    final int N = mAppWidgetIds.length;
    for (int i = 0; i < N; i++) {
      int appWidgetId = mAppWidgetIds[i];
      SimpleDateFormat sdf = new SimpleDateFormat(mContext.getResources().getString(R.string.date_format_string), mContext.getResources().getConfiguration().locale);
      
      RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.general_overview_widget);
      views.setTextViewText(R.id.general_overview_text_pending_games, String.format("%d", result.getPendingGamesCount()));
      views.setTextViewText(R.id.general_overview_text_running_games, String.format("%d", result.getRunningGames()));
      views.setTextViewText(R.id.general_overview_text_open_games, String.format("%d", result.getOpenGames()));
      views.setTextViewText(R.id.general_overview_text_last_update, sdf.format(new Date()));
      
      mAppWidgetManager.updateAppWidget(appWidgetId, views);
    }    
  }
  
  public static void Update(Context context){
    AppWidgetManager manager = AppWidgetManager.getInstance(context);
    int[] widgetIds = manager.getAppWidgetIds(new ComponentName(context.getApplicationContext(), GeneralOverviewProvider.class));
    
    Intent intent = new Intent();
    intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
    intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds);
    context.sendBroadcast(intent);
  }
}
