package de.raptor2101.BattleWorldsKronos.Connector.Gui.Views;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.raptor2101.BattleWorldsKronos.Connector.Data.Entities.Message;
import de.raptor2101.BattleWorldsKronos.Connector.Gui.R;

public class MessageView extends LinearLayout {
  private SimpleDateFormat mFormater;
  
  public MessageView(Context context) {
    super(context);
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.message_view, this);
    mFormater = new SimpleDateFormat(context.getString(R.string.date_format_string), Locale.getDefault());
  }

  public void setMessage(Message message) {
    TextView textView = (TextView) findViewById(R.id.text_message_info_view_author);
    textView.setText(message.getAuthorName());
    
    textView = (TextView)findViewById(R.id.text_message_info_view_date);
    textView.setText(mFormater.format(message.getTimestamp()));
    
    textView = (TextView)findViewById(R.id.text_message_info_view_message);
    textView.setText(message.getMessage());
  }
}
