package de.raptor2101.BattleWorldsKronos.Connector.Gui.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import de.raptor2101.BattleWorldsKronos.Connector.AbstractConnectorApp;
import de.raptor2101.BattleWorldsKronos.Connector.Data.Entities.Message;
import de.raptor2101.BattleWorldsKronos.Connector.Gui.R;
import de.raptor2101.BattleWorldsKronos.Connector.Tasks.SendMessageTask;
import de.raptor2101.BattleWorldsKronos.Connector.Tasks.SendMessageTask.Result;
import de.raptor2101.BattleWorldsKronos.Connector.Tasks.ServerConnectionTask.ResultListener;

public abstract class AbstractWriteMessageActivity extends Activity implements ResultListener<Result> {
  public final static String INTENT_EXTRA_MESSAGE_RESPOND_TO = "RESPONSE_MESSAGE";
  private SendMessageTask mTask;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.message_write_activity);
    
    View view = (View) findViewById(R.id.text_write_message_respond_box);
    Message message = (Message) getIntent().getExtras().get(INTENT_EXTRA_MESSAGE_RESPOND_TO);
    if(message != null){
      
      EditText editText = (EditText) findViewById(R.id.edit_write_message_receiver);
      editText.setText(message.getAuthorName());
      
      TextView textView = (TextView) findViewById(R.id.text_write_message_respond_text);
      textView.setText(message.getMessageText());
      
      view.setVisibility(View.VISIBLE);
    }
    else{
      view.setVisibility(View.GONE);
    }
  }

  @Override
  public boolean onMenuItemSelected(int featureId, MenuItem item) {
    if (item.getItemId() == R.id.action_send_message) {
      Message messageRespondTo = (Message) getIntent().getExtras().get(INTENT_EXTRA_MESSAGE_RESPOND_TO);
      
      SendMessageTask.Message message = new SendMessageTask.Message();

      EditText editText = (EditText) findViewById(R.id.edit_write_message_receiver);
      if(messageRespondTo == null){
        message.setReceiver(editText.getText().toString());
      }
      else
      {
        message.setReceiver(messageRespondTo.getAuthorName());
        message.setLastMessageId(messageRespondTo.getLastMessageId());
      }

      editText = (EditText) findViewById(R.id.edit_write_message_text);
      message.setText(editText.getText().toString());

      if (mTask != null) {
        mTask.cancel(true);
      }

      mTask = new SendMessageTask((AbstractConnectorApp) this.getApplication(), this);
      mTask.execute(message);

      ProgressBar progressBar = getProgressBar();
      progressBar.setVisibility(View.VISIBLE);
    }
    return super.onMenuItemSelected(featureId, item);
  }

  @Override
  public void handleResult(Result result) {
    mTask = null;
    ProgressBar progressBar = getProgressBar();
    progressBar.setVisibility(View.GONE);
    if (result.areAllMessagesSuccesfullySend()) {
      this.finish();
    } else {
      AlertDialog.Builder builder = new AlertDialog.Builder(this);
      builder.setMessage(R.string.alert_dialog_send_message_error_message);
      builder.setTitle(R.string.alert_dialog_send_message_error_title);
      
      builder.create().show();
    }
  };

  protected abstract ProgressBar getProgressBar();
}
