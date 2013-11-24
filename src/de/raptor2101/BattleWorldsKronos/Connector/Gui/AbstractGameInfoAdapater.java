package de.raptor2101.BattleWorldsKronos.Connector.Gui;

import java.util.ArrayList;
import java.util.List;

import de.raptor2101.BattleWorldsKronos.Connector.JSON.GameInfo;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class AbstractGameInfoAdapater extends BaseAdapter {
  
  private List<GameInfo> mGames;
  private Context mContext;
  
  protected AbstractGameInfoAdapater(Context context){
    mGames = new ArrayList<GameInfo>(0);
    mContext =context;
  }
  
  public void setGameInfos(List<GameInfo> games){
    mGames = games;
    this.notifyDataSetChanged();
  }
  
  @Override
  public int getCount() {
return mGames.size();
  }

  @Override
  public Object getItem(int position) {
    
    return mGames.get(position);
  }

  @Override
  public long getItemId(int position) {
    return mGames.get(position).getGameId();
  }

  @Override
  public View getView(int position, View currentView, ViewGroup parent) {
    IGameInfoView view;
    if(currentView != null){
      view = (IGameInfoView) currentView;
      
    }
    else {
      view = createGameInfoView(mContext);
    }
    view.setGameInfo(mGames.get(position));
    return (View)view;
  }

  protected abstract IGameInfoView createGameInfoView(Context context);
}
