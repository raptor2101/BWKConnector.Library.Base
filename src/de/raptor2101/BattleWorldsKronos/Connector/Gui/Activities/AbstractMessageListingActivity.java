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
import de.raptor2101.BattleWorldsKronos.Connector.Task.LoaderTask.ResultListener;
import de.raptor2101.BattleWorldsKronos.Connector.Task.MessageLoaderTask;
import de.raptor2101.BattleWorldsKronos.Connector.Task.MessageLoaderTask.Result;

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
  
  private void loadMessages(boolean forceReload){
    ApplicationSettings settings = new ApplicationSettings(this);
    
    ProgressBar progressBar = GetProgressBar();
    progressBar.setVisibility(View.VISIBLE);
    
    MessageLoaderTask loaderTask = new MessageLoaderTask(this, settings.getEmail(), settings.getPassword(), this);
    loaderTask.execute(new Boolean[]{true});
  }
  
  @Override
  public void handleResult(Result result) {
    ProgressBar progressBar = GetProgressBar();
    progressBar.setVisibility(View.GONE);
    
    if(result != null){
      mMessageViewAdapater.setMessages(result.getMessages());
      NotificationService.resetUnreadMessages(this);
    }
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu, menu);
    return true;
  }

  @Override
  public boolean onMenuItemSelected(int featureId, MenuItem item) {
    if(item.getItemId() == R.id.action_settings){
      startSettingsActivity();
    } else if (item.getItemId() == R.id.action_refresh){
      loadMessages(true);
    }
    return super.onMenuItemSelected(featureId, item);
  }
  
  protected abstract ProgressBar GetProgressBar();
}
