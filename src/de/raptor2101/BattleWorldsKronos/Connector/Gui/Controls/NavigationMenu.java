package de.raptor2101.BattleWorldsKronos.Connector.Gui.Controls;

import de.raptor2101.BattleWorldsKronos.Connector.AbstractConnectorApp;
import de.raptor2101.BattleWorldsKronos.Connector.Gui.R;
import de.raptor2101.BattleWorldsKronos.Connector.Gui.Adapters.NavigationButtonAdapter;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class NavigationMenu extends ListView implements android.widget.AdapterView.OnItemClickListener {
  private NavigationButtonAdapter mAdapter;
  private Context mContext;
  
  public NavigationMenu(Context context) {
    super(context);
    setupNavigationMenu(context);
  }
  
  public NavigationMenu(Context context, AttributeSet attrs){
    super(context,attrs);
    setupNavigationMenu(context);
  }
  
  public NavigationMenu(Context context, AttributeSet attrs, int defStyle){
    super(context,attrs,defStyle);
    setupNavigationMenu(context);
  }
  
  private void setupNavigationMenu(Context context){
    mContext = context;
    mAdapter = new NavigationButtonAdapter(context, R.menu.navigation_menu);
    this.setAdapter(mAdapter);
    
    this.setOnItemClickListener(this);
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    
    AbstractConnectorApp app = (AbstractConnectorApp) mContext.getApplicationContext();
        
    if(id == R.id.navigation_messages){
      Intent intent = new Intent(mContext, app.getMessageListingActivityClass());
      mContext.startActivity(intent);
    } else if (id == R.id.navigation_games){
      Intent intent = new Intent(mContext, app.getGameListingActivityClass());
      mContext.startActivity(intent);
    }
  }

  
  
}
