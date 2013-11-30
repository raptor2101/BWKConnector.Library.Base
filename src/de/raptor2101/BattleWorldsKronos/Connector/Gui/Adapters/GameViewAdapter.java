package de.raptor2101.BattleWorldsKronos.Connector.Gui.Adapters;

import java.util.ArrayList;
import java.util.List;

import de.raptor2101.BattleWorldsKronos.Connector.Data.Entities.Game;
import de.raptor2101.BattleWorldsKronos.Connector.Gui.Views.GameView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class GameViewAdapter extends BaseAdapter {
  
  private List<Game> mGames;
  private Context mContext;
  
  public GameViewAdapter(Context context){
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
    GameView view;
    if(currentView != null){
      view = (GameView) currentView;
      
    }
    else {
      view = new GameView(mContext);
    }
    view.setGame(mGames.get(position));
    return (View)view;
  }
}
