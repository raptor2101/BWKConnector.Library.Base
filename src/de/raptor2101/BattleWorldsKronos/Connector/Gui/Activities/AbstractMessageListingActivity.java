package de.raptor2101.BattleWorldsKronos.Connector.Gui.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import de.raptor2101.BattleWorldsKronos.Connector.AbstractConnectorApp;
import de.raptor2101.BattleWorldsKronos.Connector.ApplicationSettings;
import de.raptor2101.BattleWorldsKronos.Connector.NotificationService;
import de.raptor2101.BattleWorldsKronos.Connector.Data.Entities.Message;
import de.raptor2101.BattleWorldsKronos.Connector.Gui.R;
import de.raptor2101.BattleWorldsKronos.Connector.Gui.Adapters.MessageViewAdapter;
import de.raptor2101.BattleWorldsKronos.Connector.Tasks.DeleteMessageTask;
import de.raptor2101.BattleWorldsKronos.Connector.Tasks.MessageLoaderTask;
import de.raptor2101.BattleWorldsKronos.Connector.Tasks.ServerConnectionTask.ResultListener;

public abstract class AbstractMessageListingActivity extends Activity {
  private MessageViewAdapter mMessageViewAdapater= new MessageViewAdapter(this);
  
  private class MessageLoaderTaskListener implements ResultListener<MessageLoaderTask.Result>{
    
    AbstractMessageListingActivity mActivity;
    
    public MessageLoaderTaskListener(AbstractMessageListingActivity activity){
      mActivity = activity;
    }
    
    @Override
    public void handleResult(MessageLoaderTask.Result result) {
      ProgressBar progressBar = mActivity.getProgressBar();
      progressBar.setVisibility(View.GONE);
      
      if(result != null){
        mMessageViewAdapater.setMessages(result.getMessages());
        NotificationService.resetUnreadMessages(mActivity);
      }
    }
  }
  
private class DeleteMessageTaskListener implements ResultListener<Void>{
    
    AbstractMessageListingActivity mActivity;
    
    public DeleteMessageTaskListener(AbstractMessageListingActivity activity){
      mActivity = activity;
    }
    
    @Override
    public void handleResult(Void result) {
      mActivity.loadMessages(true);
    }
  }
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    setContentView(R.layout.message_listing_activity);
    
    
    AbsListView listView = (AbsListView) findViewById(R.id.message_listing);
    listView.setAdapter(mMessageViewAdapater);    
    listView.setClickable(true);
    registerForContextMenu(listView);
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
  
  protected abstract void startWriteMessageActivity(Message Message);
  
  private void loadMessages(boolean forceReload){
    
    ProgressBar progressBar = getProgressBar();
    progressBar.setVisibility(View.VISIBLE);
    
    MessageLoaderTask loaderTask = new MessageLoaderTask((AbstractConnectorApp) this.getApplication(), new MessageLoaderTaskListener(this));
    loaderTask.execute(new Boolean[]{true});
  }
  
  
  
  @Override
  public abstract boolean onCreateOptionsMenu(Menu menu);

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    getMenuInflater().inflate(R.menu.message_context_menu, menu);
    super.onCreateContextMenu(menu, v, menuInfo);
  }

  @Override
  public boolean onMenuItemSelected(int featureId, MenuItem item) {
    if(item.getItemId() == R.id.action_settings){
      startSettingsActivity();
    } else if (item.getItemId() == R.id.action_refresh){
      loadMessages(true);
    } else if (item.getItemId() == R.id.action_write_message){
      startWriteMessageActivity(null);
    }
    
    return super.onMenuItemSelected(featureId, item);
  }
  
  @Override
  public boolean onContextItemSelected(MenuItem item) {
    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
    Message message = (Message) mMessageViewAdapater.getItem(info.position);
    
    if(item.getItemId() == R.id.context_menu_message_answer){
      startWriteMessageActivity(message);
    } else if(item.getItemId() == R.id.context_menu_message_delete){
      DeleteMessageTask task = new DeleteMessageTask((AbstractConnectorApp) this.getApplication(), new DeleteMessageTaskListener(this));
      task.execute(message.getMessageId());
    }
    return true;
  };
  
  protected abstract ProgressBar getProgressBar();
}
