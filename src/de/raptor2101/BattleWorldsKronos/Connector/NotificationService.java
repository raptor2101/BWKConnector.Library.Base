package de.raptor2101.BattleWorldsKronos.Connector;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import de.raptor2101.BattleWorldsKronos.Connector.Gui.R;
import de.raptor2101.BattleWorldsKronos.Connector.Task.GamesLoaderTask;
import de.raptor2101.BattleWorldsKronos.Connector.Task.LoaderTask.ResultListener;

public class NotificationService extends Service implements ResultListener<GamesLoaderTask.Result> {
  private static final String ServiceTag = "BWK:Connector-Service";
  private static final int NotificationIdPendingGames = 1;

  private static Class<? extends Activity> GameListingActivity;
  private static Class<? extends Activity> ShowMessageActivity;
  private WakeLock mWakeLock;
  private GamesLoaderTask task;

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }

  @Override
  public void onStart(Intent intent, int startId) {
    handleIntent(intent);
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    handleIntent(intent);
    return START_STICKY;
  }

  public void onDestroy() {
    super.onDestroy();
    mWakeLock.release();
  }

  @Override
  public void handleResult(GamesLoaderTask.Result result) {
    if (result == null) {
      return;
    }

    
    if (result.getUnnotifiedPendingGames() > 0) {
      generatePendingGamesNotification(result.getPendingGames());
    }
  }

  private void handleIntent(Intent intent) {
    // obtain the wake lock
    PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
    mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, ServiceTag);
    mWakeLock.acquire();
    ApplicationSettings settings = new ApplicationSettings(this);
    // check the global background data setting
    ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
    if (!cm.getBackgroundDataSetting() || (!settings.isNotifyOnGamesEnabled() && !settings.isNotifyOnMessagesEnabled())) {
      stopSelf();
      return;
    }
  
    task = new GamesLoaderTask(this, settings.getEmail(), settings.getPassword(), this);
    task.execute(new Boolean[]{true});
  }

  private void generatePendingGamesNotification(int pendingGames) {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_launcher).setContentTitle(this.getString(R.string.app_name))
        .setContentText(String.format(this.getString(R.string.message_games_pending), pendingGames)).setAutoCancel(true)
        .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.FLAG_SHOW_LIGHTS).setPriority(NotificationCompat.PRIORITY_HIGH);

    Intent resultIntent = new Intent(this, GameListingActivity);

    TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
    stackBuilder.addParentStack(GameListingActivity);
    stackBuilder.addNextIntent(resultIntent);
    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    builder.setContentIntent(resultPendingIntent);
    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    // die 1 sollte ich mir noch sinnvoll ermittlen
    notificationManager.notify(NotificationIdPendingGames, builder.build());
  }

  private static boolean isNotificationServiceNeeded(ApplicationSettings settings) {

    return settings.isNotifyOnGamesEnabled() || settings.isNotifyOnMessagesEnabled();
  }

  public static void setResponseActivities(Class<? extends Activity> gameListingActivity, Class<? extends Activity> showMessageActivity) {
    GameListingActivity = gameListingActivity;
    ShowMessageActivity = showMessageActivity;
  }

  public static void reset(Context context) {
    ApplicationSettings settings = new ApplicationSettings(context);
    
    if (!isNotificationServiceNeeded(settings)) {
      return;
    }
    // Reset notifications
    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.cancel(NotificationIdPendingGames);

    // Reset and reregister the service
    int timeSpan = settings.getRefreshCylce();
    AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
    Intent intent = new Intent(context, NotificationService.class);
    PendingIntent pi = PendingIntent.getService(context, 0, intent, 0);
    am.cancel(pi);
    am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, timeSpan, timeSpan, pi);
  }
}
