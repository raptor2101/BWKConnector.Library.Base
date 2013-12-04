package de.raptor2101.BattleWorldsKronos.Connector.Tasks;

import java.util.List;

import de.raptor2101.BattleWorldsKronos.Connector.AbstractConnectorApp;
import de.raptor2101.BattleWorldsKronos.Connector.Data.Database;
import de.raptor2101.BattleWorldsKronos.Connector.Data.Entities.Game;
import de.raptor2101.BattleWorldsKronos.Connector.JSON.ServerConnection;

public class GamesLoaderTask extends LoaderTask<GamesLoaderTask.Result> {
  
  public GamesLoaderTask(AbstractConnectorApp app,ResultListener<Result> resultListener) {
    super(app, resultListener);
  }

  public class Result{
    private final List<Game> mGames;
    private final int mUnnotifiedPendingGames;
    public List<Game> getGames() {
      return mGames;
    }

    public int getUnnotifiedPendingGames() {
      return mUnnotifiedPendingGames;
    }

    public int getPendingGamesCount() {
      return mPendingGamesCount;
    }

    public int getUnnotifiedOpenGames() {
      return mUnnotifiedOpenGames;
    }

    public int getOpenGames() {
      return mOpenGames;
    }

    private final int mPendingGamesCount;
    private final int mUnnotifiedOpenGames;
    private final int mOpenGames;
    
    public Result(List<Game> games, int unnotfiedPendingGames, int pendingGamesCount, int unnotifiedOpenGames, int openGames){
      mGames = games;
      mUnnotifiedPendingGames = unnotfiedPendingGames;
      mUnnotifiedOpenGames = unnotifiedOpenGames;
      mPendingGamesCount = pendingGamesCount;
      mOpenGames = openGames;
    }
  }

  @Override
  protected Result doInBackground(Boolean... params) {
    try {
      Database database = getDatabase();
      List<Game> games = null;
      
      boolean forceUpdate = params.length > 0 && params[0]; 
      if(forceUpdate){
        ServerConnection connection = getConnection();
        if (connection != null) {
          games = connection.getGames();
          database.persistGames(games);
        }
      }
      
      games = database.getGames();
      
      int unnotifiedPendingGames = database.getUnnotfiedPendingGamesCount();
      int pendingGames = database.getPendingGamesCount();
      
      database.setAllGamesNotified();
      
      return new Result(games, unnotifiedPendingGames, pendingGames, 0, 0);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }
}
