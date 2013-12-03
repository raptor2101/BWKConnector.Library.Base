package de.raptor2101.BattleWorldsKronos.Connector.Gui.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import de.raptor2101.BattleWorldsKronos.Connector.AbstractConnectorApp;
import de.raptor2101.BattleWorldsKronos.Connector.ApplicationSettings;
import de.raptor2101.BattleWorldsKronos.Connector.NotificationService;
import de.raptor2101.BattleWorldsKronos.Connector.Gui.R;
import de.raptor2101.BattleWorldsKronos.Connector.Gui.Adapters.GameViewAdapter;
import de.raptor2101.BattleWorldsKronos.Connector.Gui.Views.GameView;
import de.raptor2101.BattleWorldsKronos.Connector.Task.GamesLoaderTask;
import de.raptor2101.BattleWorldsKronos.Connector.Task.LoaderTask.ResultListener;

public abstract class AbstractGameListingActivity extends Activity implements ResultListener<GamesLoaderTask.Result>, OnItemClickListener {
  public final static String TAG_EXPENDABLE = "expendable";
  GameViewAdapter mGameViewAdapater = new GameViewAdapter(this);
  GameView mExpandedView;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);    
    setContentView(R.layout.game_listing_activity);
    
    
    
    AbsListView listView = (AbsListView) findViewById(R.id.game_listing);
    listView.setAdapter(mGameViewAdapater);
    if(TAG_EXPENDABLE.equals(listView.getTag())){
      listView.setOnItemClickListener(this);
      listView.setClickable(true);
    } else {
      listView.setClickable(false);
    }
    
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
    long lastLoad = app.getDatabase().getTimestampLastGameUpdate();
    loadGames(SystemClock.elapsedRealtime()-lastLoad>settings.getRefreshCylce());
  }

  private void loadGames(boolean forceReload) {
    ApplicationSettings settings = new ApplicationSettings(this);
    
    ProgressBar progressBar = GetProgressBar();
    progressBar.setVisibility(View.VISIBLE);
    
    GamesLoaderTask task = new GamesLoaderTask(this, settings.getEmail(), settings.getPassword(), this);
    task.execute(new Boolean[]{forceReload});
    
  }

  @Override
  public abstract boolean onCreateOptionsMenu(Menu menu);

  @Override
  public boolean onMenuItemSelected(int featureId, MenuItem item) {
    if(item.getItemId() == R.id.action_settings){
      startSettingsActivity();
    } else if (item.getItemId() == R.id.action_refresh){
      loadGames(true);
    }
    return super.onMenuItemSelected(featureId, item);
  }
  
  @Override
  public void handleResult(GamesLoaderTask.Result result) {
    ProgressBar progressBar = GetProgressBar();
    progressBar.setVisibility(View.GONE);
    if(result != null){
      mGameViewAdapater.setGames(result.getGames());
      NotificationService.resetPendingGames(this);
    }
  }
  
  private void startSettingsActivity() {
    Intent intent = new Intent(this, SettingsActivity.class);
    startActivity(intent);
  }
  
  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    GameView gameView = (GameView) view;
    if(gameView.isExpanded()){
      gameView.collapse();
      mExpandedView = null;
    } else{
      if(mExpandedView != null){
        mExpandedView.collapse();
      }
      gameView.expand();
      mExpandedView = gameView;
    }
  }
  
  protected abstract ProgressBar GetProgressBar();
}
