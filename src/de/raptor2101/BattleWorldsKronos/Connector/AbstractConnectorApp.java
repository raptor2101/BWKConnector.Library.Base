package de.raptor2101.BattleWorldsKronos.Connector;

import de.raptor2101.BattleWorldsKronos.Connector.Data.Database;
import de.raptor2101.BattleWorldsKronos.Connector.Gui.R;
import android.app.Activity;
import android.app.Application;
import android.net.http.AndroidHttpClient;


public abstract class AbstractConnectorApp extends Application {
  private Database mDatabase;
  
  @Override
  public void onCreate() {
    super.onCreate();
    mDatabase = new Database(this);
    mDatabase.open();
  }
  
  @Override
  public void onTerminate() {
    super.onTerminate();
    mDatabase.close();
  }
  
  public Database getDatabase(){
    return mDatabase;
  }
  
  public AndroidHttpClient getHttpClient(){
    return AndroidHttpClient.newInstance(this.getString(R.string.app_name));
  }
  
  public abstract Class<? extends Activity> getGameListingActivityClass();
  public abstract Class<? extends Activity> getMessageListingActivityClass();
  
}
