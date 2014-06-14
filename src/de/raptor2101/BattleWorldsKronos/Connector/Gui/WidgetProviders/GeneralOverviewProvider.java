package de.raptor2101.BattleWorldsKronos.Connector.Gui.WidgetProviders;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.raptor2101.BattleWorldsKronos.Connector.AbstractConnectorApp;
import de.raptor2101.BattleWorldsKronos.Connector.ApplicationSettings;
import de.raptor2101.BattleWorldsKronos.Connector.Gui.R;
import de.raptor2101.BattleWorldsKronos.Connector.Gui.R.id;
import de.raptor2101.BattleWorldsKronos.Connector.Tasks.GamesLoaderTask;
import de.raptor2101.BattleWorldsKronos.Connector.Tasks.GamesLoaderTask.Result;
import de.raptor2101.BattleWorldsKronos.Connector.Tasks.ServerConnectionTask.ResultListener;
import android.app.PendingIntent;
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
    Locale locale = mContext.getResources().getConfiguration().locale;
    SimpleDateFormat sdf = new SimpleDateFormat(mContext.getResources().getString(R.string.date_format_string), locale);
    AbstractConnectorApp app = (AbstractConnectorApp)mContext.getApplicationContext();
    
    Intent intent = new Intent(mContext, app.getGameListingActivityClass());
    PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
    
    String pendingGames = String.format(locale,"%d", result.getPendingGamesCount());
    String runningGames = String.format(locale,"%d", result.getRunningGames());
    String openGames = String.format(locale,"%d", result.getOpenGames());
    String timestamp = sdf.format(new Date());
    
    for (int i = 0; i < N; i++) {
      int appWidgetId = mAppWidgetIds[i];
      
      
      
      RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.general_overview_widget);
      views.setTextViewText(R.id.general_overview_text_pending_games, pendingGames);      
      views.setTextViewText(R.id.general_overview_text_running_games, runningGames);
      views.setTextViewText(R.id.general_overview_text_open_games, openGames);
      views.setTextViewText(R.id.general_overview_text_last_update, timestamp);
      views.setOnClickPendingIntent(R.id.general_overview_layout, pendingIntent);
      
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
