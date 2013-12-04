package de.raptor2101.BattleWorldsKronos.Connector.Tasks;

import de.raptor2101.BattleWorldsKronos.Connector.AbstractConnectorApp;
import de.raptor2101.BattleWorldsKronos.Connector.JSON.ServerConnection;

public class SendMessageTask extends ServerConnectionTask<SendMessageTask.Message,SendMessageTask.Result>{
  public SendMessageTask(AbstractConnectorApp app, ResultListener<Result> resultListener) {
    super(app, resultListener);
  }

  public static class Message{
    private String mText;
    private String mReceiver;
    private int mLastMessageId;
    
    public String getText() {
      return mText;
    }
    public void setText(String text) {
      this.mText = text;
    }
    public String getReceiver() {
      return mReceiver;
    }
    public void setReceiver(String receiver) {
      this.mReceiver = receiver;
    }
    public int getLastMessageId() {
      return mLastMessageId;
    }
    public void setLastMessageId(int lastMessageId) {
      this.mLastMessageId = lastMessageId;
    }
  }
  
  public class Result{
    private boolean mAllMessagesSendedSuccessFully;
    
    public Result(boolean allMessagesSendedSuccessFully){
      mAllMessagesSendedSuccessFully = allMessagesSendedSuccessFully;
    }
    
    public boolean areAllMessagesSuccesfullySend(){
      return mAllMessagesSendedSuccessFully;
    }
  }

  @Override
  protected Result doInBackground(Message... messages) {
    try {
      ServerConnection connection = getConnection();
      boolean allMessagesSendedSuccessFully = false;
      
      for(Message message:messages){
        allMessagesSendedSuccessFully = connection.sendMessage(message.getReceiver(), message.getText(), message.getLastMessageId());
        if(allMessagesSendedSuccessFully!= true){
          break;
        }
      }
      return new Result(allMessagesSendedSuccessFully);
    } catch (Exception e) {
      return new Result(false);
    }
  }
}
