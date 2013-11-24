package de.raptor2101.BattleWorldsKronos.Connector.JSON;

import java.util.HashSet;
import java.util.List;

import de.raptor2101.BattleWorldsKronos.Connector.JSON.GameInfo.State;

public class GameListing {
  private List<GameInfo> mMyGames;
  private List<GameInfo> mOpenGames;
  
  public GameListing(List<GameInfo> myGames, List<GameInfo> openGames){
    mMyGames = myGames;
    mOpenGames = openGames;
  }
  
  public List<GameInfo> getMyGames(){
    return mMyGames;
  }
  
  public List<GameInfo> getOpenGames(){
    return mOpenGames;
  }
  
  public HashSet<GameInfo> getPendingGames() {
    HashSet<GameInfo> currentPendingGames = new HashSet<GameInfo>(mMyGames.size()/2);
    for(GameInfo gameInfo:mMyGames){
      if(gameInfo.getState() == State.PENDING){
        currentPendingGames.add(gameInfo);
      }
    }
    return currentPendingGames;
  }
}
