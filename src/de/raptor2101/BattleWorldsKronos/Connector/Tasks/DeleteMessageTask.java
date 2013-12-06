package de.raptor2101.BattleWorldsKronos.Connector.Tasks;

import de.raptor2101.BattleWorldsKronos.Connector.AbstractConnectorApp;
import de.raptor2101.BattleWorldsKronos.Connector.JSON.ServerConnection;

public class DeleteMessageTask extends ServerConnectionTask<Integer, Void>{

  public DeleteMessageTask(AbstractConnectorApp app, ResultListener<Void> resultListener) {
    super(app, resultListener);
  }

  @Override
  protected Void doInBackground(Integer... params) {
    try {
      ServerConnection connection = getConnection();
      
      if(connection == null){
        return null;
      }
      
      for(Integer messageId:params){
       connection.deleteMessage(messageId); 
      }
    } catch (Exception e) {
      
    } 
    
    return null;
  }

}
