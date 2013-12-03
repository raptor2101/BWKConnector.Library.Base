package de.raptor2101.BattleWorldsKronos.Connector.Gui.Activities;

import android.app.Activity;
import android.os.Bundle;
import de.raptor2101.BattleWorldsKronos.Connector.Gui.R;

public abstract class AbstractWriteMessageActivity extends Activity{
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.write_message_activity);
  }
}
