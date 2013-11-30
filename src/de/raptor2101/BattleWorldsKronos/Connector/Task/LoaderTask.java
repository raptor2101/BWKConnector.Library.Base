package de.raptor2101.BattleWorldsKronos.Connector.Task;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import de.raptor2101.BattleWorldsKronos.Connector.AbstractConnectorApp;
import de.raptor2101.BattleWorldsKronos.Connector.Data.Database;
import de.raptor2101.BattleWorldsKronos.Connector.JSON.ServerConnection;
import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;

public abstract class LoaderTask<TResultType> extends AsyncTask<Boolean, Void, TResultType>{
  public interface ResultListener<TResultType> {
    void handleResult(TResultType result);
  }
  
  private AndroidHttpClient mHttpClient;
  private Database mDatabase;
  private String mEMail;
  private String mPassword;
  private ResultListener<TResultType> mListener;

  public LoaderTask(Context context, String eMail, String password, ResultListener<TResultType> resultListener) {
    AbstractConnectorApp app = (AbstractConnectorApp) context.getApplicationContext();
    mHttpClient = app.getHttpClient();
    mDatabase = app.getDatabase();
    mEMail = eMail;
    mPassword = password;
    mListener = resultListener;
  }
  
  protected ServerConnection getConnection() throws ClientProtocolException, IOException{
    ServerConnection connection = new ServerConnection(mHttpClient);
    if(connection.login(mEMail, mPassword)){
      return connection;
    }
    
    return null;
  }
  
  
  protected Database getDatabase(){
    return mDatabase;
  }
  
  @Override
  protected abstract TResultType doInBackground(Boolean... params);
  
  @Override
  protected void onPostExecute(TResultType result) {
    mListener.handleResult(result);
    mHttpClient.close();
  }
}
