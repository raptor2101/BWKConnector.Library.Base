package de.raptor2101.BattleWorldsKronos.Connector.Task;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import de.raptor2101.BattleWorldsKronos.Connector.JSON.GameListing;
import de.raptor2101.BattleWorldsKronos.Connector.JSON.ServerConnection;

public class GameListingLoaderTask extends AsyncTask<Void,Void,GameListing>  {
  public interface ResultListener {

    void handleResult(GameListing result);
  }
  private AndroidHttpClient mHttpClient;
  private ResultListener mListener;
  private String mEMail;
  private String mPassword;
  
  public GameListingLoaderTask(AndroidHttpClient httpClient,String eMail, String password, ResultListener listener){
    mHttpClient = httpClient;
    mListener = listener;
    mEMail = eMail;
    mPassword = password;
  }
  
  @Override
  protected GameListing doInBackground(Void... params) {
    ServerConnection connection = new ServerConnection(mHttpClient);
    try {
      if(connection.login(mEMail, mPassword)){
        return connection.getGameListing();
      } 
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }

  @Override
  protected void onPostExecute(GameListing result) {
    mListener.handleResult(result);
    mHttpClient.close();
  }
}
