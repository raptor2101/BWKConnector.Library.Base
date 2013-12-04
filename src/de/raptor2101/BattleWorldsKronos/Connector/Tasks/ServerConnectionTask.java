package de.raptor2101.BattleWorldsKronos.Connector.Tasks;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import de.raptor2101.BattleWorldsKronos.Connector.AbstractConnectorApp;
import de.raptor2101.BattleWorldsKronos.Connector.ApplicationSettings;
import de.raptor2101.BattleWorldsKronos.Connector.JSON.ServerConnection;

public abstract class ServerConnectionTask<TParemeterType,TResultType> extends AsyncTask<TParemeterType, Void, TResultType>{
  public interface ResultListener<TResultType> {
    void handleResult(TResultType result);
  }
  
  private AndroidHttpClient mHttpClient;
  private String mEMail;
  private String mPassword;
  private ResultListener<TResultType> mListener;

  protected ServerConnectionTask(AbstractConnectorApp app, ResultListener<TResultType> resultListener) {
    ApplicationSettings settings = new ApplicationSettings(app);
    mHttpClient = app.getHttpClient();
    mEMail = settings.getEmail();
    mPassword = settings.getPassword();
    mListener = resultListener;
  }
  
  protected ServerConnection getConnection() throws ClientProtocolException, IOException{
    ServerConnection connection = new ServerConnection(mHttpClient);
    if(connection.login(mEMail, mPassword)){
      return connection;
    }
    
    return null;
  }
  
  @Override
  protected abstract TResultType doInBackground(TParemeterType... params);
  
  @Override
  protected void onPostExecute(TResultType result) {
    mListener.handleResult(result);
    mHttpClient.close();
  }
}
