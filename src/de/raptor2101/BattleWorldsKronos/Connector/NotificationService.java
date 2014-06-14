package de.raptor2101.BattleWorldsKronos.Connector;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask.Status;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import de.raptor2101.BattleWorldsKronos.Connector.Gui.R;
import de.raptor2101.BattleWorldsKronos.Connector.Gui.WidgetProviders.GeneralOverviewProvider;
import de.raptor2101.BattleWorldsKronos.Connector.Tasks.GamesLoaderTask;
import de.raptor2101.BattleWorldsKronos.Connector.Tasks.MessageLoaderTask;
import de.raptor2101.BattleWorldsKronos.Connector.Tasks.ServerConnectionTask.ResultListener;

public class NotificationService extends Service {
  private static final String ServiceTag = "BWK:Connector-Service";
  private static final int NotificationIdPendingGames = 1;
  private static final int NotificationIdUnreadMessages = 2;

  private static Class<? extends Activity> GameListingActivity;
  private static Class<? extends Activity> MessageListingActivity;
  private WakeLock mWakeLock;
  private GamesLoaderTask mGameLoaderTask;
  private MessageLoaderTask mMessageLoaderTask;
  
  private class MessageLoaderResultListener implements ResultListener<MessageLoaderTask.Result>{
    NotificationService mService;
    
    public MessageLoaderResultListener(NotificationService service){
      mService = service;
    }
    
    @Override
    public void handleResult(MessageLoaderTask.Result result) {
      
      if (result != null && result.getUnnotifiedMessages()>0) {
        mService.generateUnreadMessageNotification(result.getMessages().size());
      }
      
      if(mService.mGameLoaderTask != null && mService.mGameLoaderTask.getStatus() ==Status.FINISHED){
        mService.stopSelf();
      }

    }
  }
  
  private class GamesLoaderResultListener implements ResultListener<GamesLoaderTask.Result>{
    NotificationService mService;
    
    public GamesLoaderResultListener(NotificationService service){
      mService = service;
    }
    
    @Override
    public void handleResult(GamesLoaderTask.Result result) {
      if (result != null && result.getUnnotifiedPendingGames() > 0) {
        mService.generatePendingGamesNotification(result.getPendingGamesCount());
        
        Intent intent = new Intent(mService, GeneralOverviewProvider.class);
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
        // since it seems the onUpdate() is only fired on that:
        int[] ids = {R.xml.general_overview_widgetinfo};
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        sendBroadcast(intent);
      }
      
      if(mService.mMessageLoaderTask != null && mService.mMessageLoaderTask.getStatus() ==Status.FINISHED){
        mService.stopSelf();
      }
    }
  }
  
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
  
    AbstractConnectorApp app = (AbstractConnectorApp) this.getApplication();
    if(settings.isNotifyOnGamesEnabled()){
      mGameLoaderTask = new GamesLoaderTask(app, new GamesLoaderResultListener(this));
      mGameLoaderTask.execute(new Boolean[]{true});
    }
    
    if(settings.isNotifyOnMessagesEnabled()){
      mMessageLoaderTask = new MessageLoaderTask(app, new MessageLoaderResultListener(this));
      mMessageLoaderTask.execute(new Boolean[]{true});
    }
    
  }

  private void generatePendingGamesNotification(int pendingGames) {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_launcher).setContentTitle(this.getString(R.string.app_name))
        .setContentText(String.format(this.getString(R.string.notification_games_pending), pendingGames)).setAutoCancel(true)
        .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.FLAG_SHOW_LIGHTS).setPriority(NotificationCompat.PRIORITY_HIGH);

    Intent resultIntent = new Intent(this, GameListingActivity);

    TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
    stackBuilder.addParentStack(GameListingActivity);
    stackBuilder.addNextIntent(resultIntent);
    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    builder.setContentIntent(resultPendingIntent);
    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    notificationManager.notify(NotificationIdPendingGames, builder.build());
    
    GeneralOverviewProvider.Update(this);
  }
  
  private void generateUnreadMessageNotification(int unreadMessages) {
    NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_launcher).setContentTitle(this.getString(R.string.app_name))
        .setContentText(String.format(this.getString(R.string.notification_messages_unread), unreadMessages)).setAutoCancel(true)
        .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND | Notification.FLAG_SHOW_LIGHTS).setPriority(NotificationCompat.PRIORITY_HIGH);

    Intent resultIntent = new Intent(this, MessageListingActivity);

    TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
    stackBuilder.addParentStack(MessageListingActivity);
    stackBuilder.addNextIntent(resultIntent);
    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    builder.setContentIntent(resultPendingIntent);
    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

    notificationManager.notify(NotificationIdUnreadMessages, builder.build());
  }

  private static boolean isNotificationServiceNeeded(ApplicationSettings settings) {

    return settings.isNotifyOnGamesEnabled() || settings.isNotifyOnMessagesEnabled();
  }

  public static void setResponseActivities(Class<? extends Activity> gameListingActivity, Class<? extends Activity> showMessageActivity) {
    GameListingActivity = gameListingActivity;
    MessageListingActivity = showMessageActivity;
  }

  public static void resetPendingGames(Context context) {
    ApplicationSettings settings = new ApplicationSettings(context);
    
    if (!isNotificationServiceNeeded(settings)) {
      return;
    }
    // Reset notifications
    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.cancel(NotificationIdPendingGames);

    resetAndRegister(context, settings);
  }
  
  public static void resetUnreadMessages(Context context) {
    ApplicationSettings settings = new ApplicationSettings(context);
    
    if (!isNotificationServiceNeeded(settings)) {
      return;
    }
    // Reset notifications
    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.cancel(NotificationIdUnreadMessages);

    resetAndRegister(context, settings);
  }

  private static void resetAndRegister(Context context, ApplicationSettings settings) {
    // Reset and reregister the service
    int timeSpan = settings.getRefreshCylce();
    AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
    Intent intent = new Intent(context, NotificationService.class);
    PendingIntent pi = PendingIntent.getService(context, 0, intent, 0);
    am.cancel(pi);
    am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, timeSpan, timeSpan, pi);
  }
}
