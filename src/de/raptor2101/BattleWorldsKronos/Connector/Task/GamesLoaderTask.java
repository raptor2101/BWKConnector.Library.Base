package de.raptor2101.BattleWorldsKronos.Connector.Task;

import java.util.List;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import de.raptor2101.BattleWorldsKronos.Connector.AbstractConnectorApp;
import de.raptor2101.BattleWorldsKronos.Connector.Data.Database;
import de.raptor2101.BattleWorldsKronos.Connector.Data.Entities.Game;
import de.raptor2101.BattleWorldsKronos.Connector.JSON.ServerConnection;

public class GamesLoaderTask extends AsyncTask<Boolean, Void, GamesLoaderTask.Result> {
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
  public interface ResultListener {
    void handleResult(Result result);
  }

  private AndroidHttpClient mHttpClient;
  private Database mDatabase;
  private ResultListener mListener;
  private String mEMail;
  private String mPassword;
  

  public GamesLoaderTask(Context context, String eMail, String password, ResultListener listener) {
    AbstractConnectorApp app = (AbstractConnectorApp) context.getApplicationContext();
    mHttpClient = app.getHttpClient();
    mDatabase = app.getDatabase();
    mListener = listener;
    mEMail = eMail;
    mPassword = password;
  }

  @Override
  protected Result doInBackground(Boolean... params) {
    ServerConnection connection = new ServerConnection(mHttpClient);
    try {
      List<Game> games = null;
      boolean forceUpdate = params.length > 0 && params[0]; 
      if(forceUpdate){
        if (connection.login(mEMail, mPassword)) {
          games = connection.getGames();
          mDatabase.persistGames(games);
        }
      }
      
      games = mDatabase.getGames();
      
      int unnotifiedPendingGames = mDatabase.getUnnotfiedPendingGamesCount();
      int pendingGames = mDatabase.getPendingGamesCount();
      
      mDatabase.setAllGamesNotified();
      
      return new Result(games, unnotifiedPendingGames, pendingGames, 0, 0);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

  @Override
  protected void onPostExecute(Result result) {
    mListener.handleResult(result);
    mHttpClient.close();
  }
}
