package de.raptor2101.BattleWorldsKronos.Connector;

import de.raptor2101.BattleWorldsKronos.Connector.Gui.R;
import android.content.Context;
import android.content.SharedPreferences;

public class ApplicationSettings {
  private final static String PREFERENCE_NAME_EMAIL ="e-mail";
  private final static String PREFERENCE_NAME_PASSWORD ="password";
  private final static String PREFERENCE_NAME_REFRESH_CYCLE ="refreshCylce";
  private final static String PREFERENCE_NAME_ENABLE_NOTIFY_ON_GAMES ="gameNotification";
  private final static String PREFERENCE_NAME_ENABLE_NOTIFY_ON_MESSAGES ="messageNotification";
  
  public final static String EmptyResult = "";
  
  SharedPreferences mSettings;
  
  public ApplicationSettings(Context context){
    mSettings = context.getSharedPreferences(context.getString(R.string.app_name), Context.MODE_PRIVATE);
  }
  
  public String getEmail(){
    return mSettings.getString(PREFERENCE_NAME_EMAIL, EmptyResult);
  }
  
  public String getPassword(){
    return mSettings.getString(PREFERENCE_NAME_PASSWORD, EmptyResult);
  }
  
  public boolean isNotifyOnGamesEnabled(){
    return mSettings.getInt(PREFERENCE_NAME_ENABLE_NOTIFY_ON_GAMES, 0) == 1;
  }
  public boolean isNotifyOnMessagesEnabled(){
    return mSettings.getInt(PREFERENCE_NAME_ENABLE_NOTIFY_ON_MESSAGES, 0) == 1;
  }
  
  public int getRefreshCylce(){
    return mSettings.getInt(PREFERENCE_NAME_REFRESH_CYCLE, 30*60*1000);
  }
  
  public void Save(String eMail, String password, boolean isNotifyOnGamesEnabled, boolean isNotifyOnMessagesEnabled, int refreshCycle){
    SharedPreferences.Editor editor = mSettings.edit();
    editor.putString(PREFERENCE_NAME_EMAIL, eMail);
    editor.putString(PREFERENCE_NAME_PASSWORD, password);
    
    editor.putInt(PREFERENCE_NAME_ENABLE_NOTIFY_ON_GAMES, isNotifyOnGamesEnabled?1:0);
    editor.putInt(PREFERENCE_NAME_ENABLE_NOTIFY_ON_MESSAGES, isNotifyOnMessagesEnabled?1:0);
    editor.putInt(PREFERENCE_NAME_REFRESH_CYCLE, refreshCycle);
    
    editor.commit();
  }
}
