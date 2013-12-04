package de.raptor2101.BattleWorldsKronos.Connector.Gui.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ProgressBar;
import de.raptor2101.BattleWorldsKronos.Connector.AbstractConnectorApp;
import de.raptor2101.BattleWorldsKronos.Connector.ApplicationSettings;
import de.raptor2101.BattleWorldsKronos.Connector.NotificationService;
import de.raptor2101.BattleWorldsKronos.Connector.Gui.R;
import de.raptor2101.BattleWorldsKronos.Connector.Gui.Adapters.MessageViewAdapter;
import de.raptor2101.BattleWorldsKronos.Connector.Tasks.MessageLoaderTask;
import de.raptor2101.BattleWorldsKronos.Connector.Tasks.MessageLoaderTask.Result;
import de.raptor2101.BattleWorldsKronos.Connector.Tasks.ServerConnectionTask.ResultListener;

public abstract class AbstractMessageListingActivity extends Activity implements ResultListener<MessageLoaderTask.Result>{
  private MessageViewAdapter mMessageViewAdapater= new MessageViewAdapter(this);
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    setContentView(R.layout.message_listing_activity);
    
    
    AbsListView listView = (AbsListView) findViewById(R.id.message_listing);
    listView.setAdapter(mMessageViewAdapater);    
    listView.setClickable(false);
  }

  @Override
  protected void onResume() {
    super.onResume();
    
    ApplicationSettings settings = new ApplicationSettings(this);
    
    if(settings.getEmail().equals(ApplicationSettings.EmptyResult)){
      startSettingsActivity();
      return;
    }
    
    AbstractConnectorApp app = (AbstractConnectorApp) getApplication();
    //TODO das last load ist noch falsch...
    long lastLoad = app.getDatabase().getTimestampMessagesUpdate();
    loadMessages(SystemClock.elapsedRealtime()-lastLoad>settings.getRefreshCylce());
  }
  
  private void startSettingsActivity() {
    Intent intent = new Intent(this, SettingsActivity.class);
    startActivity(intent);
  }
  
  protected abstract void startWriteMessageActivity();
  
  private void loadMessages(boolean forceReload){
    
    ProgressBar progressBar = getProgressBar();
    progressBar.setVisibility(View.VISIBLE);
    
    MessageLoaderTask loaderTask = new MessageLoaderTask((AbstractConnectorApp) this.getApplication(), this);
    loaderTask.execute(new Boolean[]{true});
  }
  
  @Override
  public void handleResult(Result result) {
    ProgressBar progressBar = getProgressBar();
    progressBar.setVisibility(View.GONE);
    
    if(result != null){
      mMessageViewAdapater.setMessages(result.getMessages());
      NotificationService.resetUnreadMessages(this);
    }
  }
  
  @Override
  public abstract boolean onCreateOptionsMenu(Menu menu);

  @Override
  public boolean onMenuItemSelected(int featureId, MenuItem item) {
    if(item.getItemId() == R.id.action_settings){
      startSettingsActivity();
    } else if (item.getItemId() == R.id.action_refresh){
      loadMessages(true);
    } else if (item.getItemId() == R.id.action_write_message){
      startWriteMessageActivity();
    }
    
    return super.onMenuItemSelected(featureId, item);
  }
  
  protected abstract ProgressBar getProgressBar();
}
