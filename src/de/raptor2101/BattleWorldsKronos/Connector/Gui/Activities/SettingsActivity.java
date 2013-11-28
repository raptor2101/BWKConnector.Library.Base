package de.raptor2101.BattleWorldsKronos.Connector.Gui.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import de.raptor2101.BattleWorldsKronos.Connector.ApplicationSettings;
import de.raptor2101.BattleWorldsKronos.Connector.Gui.R;

public class SettingsActivity extends Activity implements OnClickListener {

  private static final int TimeSpan_10Minutes = 10 * 60 * 1000;
  private static final int TimeSpan_20Minutes = 20 * 60 * 1000;
  private static final int TimeSpan_30Minutes = 30 * 60 * 1000;
  private static final int TimeSpan_1Hour = 60 * 60 * 1000;
  private static final int TimeSpan_2Hours = 2 * 60 * 60 * 1000;
  private static final int TimeSpan_3Hours = 3 * 60 * 60 * 1000;
  private static final int TimeSpan_6Hours = 6 * 60 * 60 * 1000;
  private static final int TimeSpan_12Hours = 12 * 60 * 60 * 1000;
  private static final int TimeSpan_24Hours = 24 * 60 * 60 * 1000;

  private ApplicationSettings mSettings;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    super.onCreate(savedInstanceState);
    setContentView(R.layout.settings_activity);
    mSettings = new ApplicationSettings(this);

    EditText textBox = (EditText) findViewById(R.id.editEmail);
    textBox.setText(mSettings.getEmail());

    textBox = (EditText) findViewById(R.id.editPassword);
    textBox.setText(mSettings.getPassword());

    CheckBox checkBox = (CheckBox) findViewById(R.id.checkBoxPendingGames);
    checkBox.setChecked(mSettings.isNotifyOnGamesEnabled());

    checkBox = (CheckBox) findViewById(R.id.checkBoxNewMessages);
    checkBox.setChecked(mSettings.isNotifyOnMessagesEnabled());

    Button button = (Button) findViewById(R.id.button_save);
    button.setOnClickListener(this);
    
    button = (Button) findViewById(R.id.button_discard);
    button.setOnClickListener(this);

    Spinner spinner = (Spinner) findViewById(R.id.spinnerUpdateCycle);
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.setting_spinner_updatecycle, android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinner.setAdapter(adapter);
    switch (mSettings.getRefreshCylce()) {
    case TimeSpan_10Minutes:
      spinner.setSelection(0);
      break;
    case TimeSpan_20Minutes:
      spinner.setSelection(1);
      break;
    case TimeSpan_30Minutes:
      spinner.setSelection(2);
      break;
    case TimeSpan_1Hour:
      spinner.setSelection(3);
      break;
    case TimeSpan_2Hours:
      spinner.setSelection(4);
      break;
    case TimeSpan_3Hours:
      spinner.setSelection(5);
      break;
    case TimeSpan_6Hours:
      spinner.setSelection(6);
      break;
    case TimeSpan_12Hours:
      spinner.setSelection(7);
      break;
    case TimeSpan_24Hours:
      spinner.setSelection(8);
      break;
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    return true;
  }

  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.button_save) {
      EditText textBox = (EditText) findViewById(R.id.editEmail);
      String email = textBox.getText().toString();

      textBox = (EditText) findViewById(R.id.editPassword);
      String password = textBox.getText().toString();

      CheckBox checkBox = (CheckBox) findViewById(R.id.checkBoxPendingGames);
      boolean isNotifyOnGamesEnabled = checkBox.isChecked();

      checkBox = (CheckBox) findViewById(R.id.checkBoxNewMessages);
      boolean isNotifyOnMessagesEnabled = checkBox.isChecked();

      int refreshIntervall = 0;
      Spinner spinner = (Spinner) findViewById(R.id.spinnerUpdateCycle);
      switch (spinner.getSelectedItemPosition()) {
      case 0:
        refreshIntervall = TimeSpan_10Minutes;
        break;
      case 1:
        refreshIntervall = TimeSpan_20Minutes;
        break;
      case 2:
        refreshIntervall = TimeSpan_30Minutes;
        break;
      case 3:
        refreshIntervall = TimeSpan_1Hour;
        break;
      case 4:
        refreshIntervall = TimeSpan_2Hours;
        break;
      case 5:
        refreshIntervall = TimeSpan_3Hours;
        break;
      case 6:
        refreshIntervall = TimeSpan_6Hours;
        break;
      case 7:
        refreshIntervall = TimeSpan_12Hours;
        break;
      case 8:
        refreshIntervall = TimeSpan_24Hours;
        break;
      }

      mSettings.Save(email, password, isNotifyOnGamesEnabled, isNotifyOnMessagesEnabled, refreshIntervall);
    }

    this.finish();
  }

}
