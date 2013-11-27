package de.raptor2101.BattleWorldsKronos.Connector.Gui;

import java.util.ArrayList;
import java.util.List;

import de.raptor2101.BattleWorldsKronos.Connector.Data.Entities.Game;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class AbstractGameViewAdapater extends BaseAdapter {
  
  private List<Game> mGames;
  private Context mContext;
  
  protected AbstractGameViewAdapater(Context context){
    mGames = new ArrayList<Game>(0);
    mContext =context;
  }
  
  public void setGames(List<Game> games){
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
    IGameView view;
    if(currentView != null){
      view = (IGameView) currentView;
      
    }
    else {
      view = createGameView(mContext);
    }
    view.setGame(mGames.get(position));
    return (View)view;
  }

  protected abstract IGameView createGameView(Context context);
}
