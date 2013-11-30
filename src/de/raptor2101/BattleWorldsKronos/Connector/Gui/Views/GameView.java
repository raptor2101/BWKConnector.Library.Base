package de.raptor2101.BattleWorldsKronos.Connector.Gui.Views;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.raptor2101.BattleWorldsKronos.Connector.Data.Entities.Game;
import de.raptor2101.BattleWorldsKronos.Connector.Data.Entities.Player;
import de.raptor2101.BattleWorldsKronos.Connector.Gui.R;

public class GameView extends LinearLayout {

  private SimpleDateFormat mFormater;

  public GameView(Context context) {
    super(context);
    mFormater = new SimpleDateFormat(context.getString(R.string.date_format_string), Locale.getDefault());
    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    inflater.inflate(R.layout.game_view, this);
  }

  public void setGame(Game game) {
    Game.State gameState = game.getState();

    TextView textView = (TextView) this.findViewById(R.id.text_game_info_view_Title);
    textView.setText(game.getGameName());

    textView = (TextView) this.findViewById(R.id.text_game_info_view_State);
    textView.setText(gameState.getResourceId());
    textView.setTextColor(gameState.getColor(this.getContext()));

    textView = (TextView) this.findViewById(R.id.text_game_info_view_Round);
    textView.setText(String.format("%d", game.getCurrentRound()));

    textView = (TextView) this.findViewById(R.id.text_game_info_view_PlayerCount);
    textView.setText(String.format("%d", game.getPlayers().size()));

    textView = (TextView) this.findViewById(R.id.text_game_info_view_Date);
    textView.setText(String.format("%d", game.getPlayers().size()));

    textView = (TextView) this.findViewById(R.id.text_game_info_view_Date);
    textView.setText(mFormater.format(game.getUpdateDate()));

    LinearLayout layout = (LinearLayout) findViewById(R.id.layout_game_info_view_PlayerInfo);
    layout.removeAllViews();
    List<Player> players = game.getPlayers();

    textView = (TextView) this.findViewById(R.id.text_game_info_view_PlayerCount);
    textView.setText(String.format("%d", players.size()));

    for (int i = 0; i < players.size(); i++) {
      PlayerView view = new PlayerView(getContext());
      view.setPlayerInfo(players.get(i));
      layout.addView(view);
    }
  }

  public boolean isExpanded() {
    LinearLayout layout = (LinearLayout) findViewById(R.id.layout_game_info_view_PlayerBox);
    return layout.getVisibility() == View.VISIBLE;
  }

  public void collapse() {
    LinearLayout layout = (LinearLayout) findViewById(R.id.layout_game_info_view_PlayerBox);
    layout.setVisibility(View.GONE);
  }

  public void expand() {
    LinearLayout layout = (LinearLayout) findViewById(R.id.layout_game_info_view_PlayerBox);
    layout.setVisibility(View.VISIBLE);
  }

}
