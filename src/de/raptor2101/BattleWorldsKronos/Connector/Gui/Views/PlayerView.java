package de.raptor2101.BattleWorldsKronos.Connector.Gui.Views;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.raptor2101.BattleWorldsKronos.Connector.Data.Entities.Player.State;
import de.raptor2101.BattleWorldsKronos.Connector.Data.Entities.Player;
import de.raptor2101.BattleWorldsKronos.Connector.Gui.R;

public class PlayerView extends LinearLayout {
  
  public PlayerView(Context context) {
    super(context);
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.player_view, this);

  }

  public void setPlayerInfo(Player player) {
    State state = player.getState();
    LinearLayout playerLine = (LinearLayout) findViewById(R.id.label_player_info_view_PlayerLine);
    playerLine.setBackgroundColor(state.getColor(this.getContext()));
    
    TextView textView = (TextView) findViewById(R.id.text_player_info_view_PlayerName);
    textView.setText(player.getPlayerName());

    textView = (TextView) findViewById(R.id.text_player_info_view_PlayerState);
    textView.setText(state.getResourceId());
  }
}
