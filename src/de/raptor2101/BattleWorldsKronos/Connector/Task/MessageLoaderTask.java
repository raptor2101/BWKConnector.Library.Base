package de.raptor2101.BattleWorldsKronos.Connector.Task;

import java.util.List;

import de.raptor2101.BattleWorldsKronos.Connector.Data.Database;
import de.raptor2101.BattleWorldsKronos.Connector.Data.Entities.Message;
import de.raptor2101.BattleWorldsKronos.Connector.JSON.ServerConnection;
import android.content.Context;

public class MessageLoaderTask extends LoaderTask<MessageLoaderTask.Result> {
  public MessageLoaderTask(Context context, String eMail, String password, ResultListener<Result> resultListener) {
    super(context, eMail, password, resultListener);
  }

  public class Result{
    private final List<Message> mMessages;
    private final int mUnnotifiedMessages;
    public List<Message> getMessages() {
      return mMessages;
    }

    public int getUnnotifiedMessages() {
      return mUnnotifiedMessages;
    }
   
    public Result(List<Message> messages, int unnotfiedMessages){
      mMessages = messages;
      mUnnotifiedMessages = unnotfiedMessages;
    }
  }

  @Override
  protected Result doInBackground(Boolean... params) {
    try{
      Database database = getDatabase();
      boolean forceUpdate = params.length > 0 && params[0]; 
      
      List<Message> messages;
      if(forceUpdate){
        ServerConnection connection = getConnection();
        if (connection != null) {
          messages = connection.getMessages();
          database.persistMessage(messages);
        }
      }
      
      
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return null;
  }
}
