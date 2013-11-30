package de.raptor2101.BattleWorldsKronos.Connector.Task;

import java.util.List;

import android.content.Context;
import de.raptor2101.BattleWorldsKronos.Connector.Data.Database;
import de.raptor2101.BattleWorldsKronos.Connector.Data.Entities.Game;
import de.raptor2101.BattleWorldsKronos.Connector.JSON.ServerConnection;

public class GamesLoaderTask extends LoaderTask<GamesLoaderTask.Result> {
  
  public GamesLoaderTask(Context context, String eMail, String password, ResultListener<Result> resultListener) {
    super(context, eMail, password, resultListener);
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

    public int getPendingGames() {
      return mPendingGames;
    }

    public int getUnnotifiedOpenGames() {
      return mUnnotifiedOpenGames;
    }

    public int getOpenGames() {
      return mOpenGames;
    }

    private final int mPendingGames;
    private final int mUnnotifiedOpenGames;
    private final int mOpenGames;
    
    public Result(List<Game> games, int unnotfiedPendingGames, int pendingGames, int unnotifiedOpenGames, int openGames){
      mGames = games;
      mUnnotifiedPendingGames = unnotfiedPendingGames;
      mUnnotifiedOpenGames = unnotifiedOpenGames;
      mPendingGames = pendingGames;
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
