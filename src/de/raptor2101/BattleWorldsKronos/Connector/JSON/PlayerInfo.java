package de.raptor2101.BattleWorldsKronos.Connector.JSON;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import de.raptor2101.BattleWorldsKronos.Connector.Gui.R;

public class PlayerInfo {
  public int getPlayerId() {
    return mPlayerId;
  }

  public int getUserId() {
    return mUserId;
  }

  public String getPlayerName() {
    return mPlayerName;
  }

  public String getColor() {
    return mColor;
  }

  public State getState() {
    return mState;
  }

  public int getTeam() {
    return mTeam;
  }

  public String getLastMessage() {
    return mLastMessage;
  }

  public enum State {
    PLAYING("playing", R.string.player_info_state_playing, R.color.player_state_playing),
    LOST("lost", R.string.player_info_state_lost, R.color.player_state_lost),
    WON("won", R.string.player_info_state_won, R.color.player_state_won),
    ABORTED("aborted", R.string.player_info_state_aborted, R.color.player_state_aborted),
    UNKNOWN("unknown", R.string.player_info_state_unknown, R.color.player_state_unknown),
    TIMEOUT("timeout", R.string.player_info_state_timeout, R.color.player_state_timeout);

    private final String mValue;
    private final int mResourceId;
    private final int mColorId;

    private State(final String value, final int resourceId, final int colorId) {
      mValue = value;
      mResourceId = resourceId;
      mColorId = colorId;
    }

    public int getColorId() {
      return mColorId;
    }

    public int getColor(Context context){
      return context.getResources().getColor(mColorId);
    }
    
    public int getResourceId() {
      return mResourceId;
    }

    @Override
    public String toString() {
      return mValue;
    }
  }

  private static final String JSON_IDENTIFIER_PLAYER_ID = "playerId";
  private static final String JSON_IDENTIFIER_USER_ID = "userId";
  //private static final String JSON_IDENTIFIER_TEAM = "team";
  private static final String JSON_IDENTIFIER_NAME = "name";
  private static final String JSON_IDENTIFIER_COLOR = "color";
  private static final String JSON_IDENTIFIER_STATE = "state";
  private static final String JSON_IDENTIFIER_LAST_MESSAGE = "last_message";

  public PlayerInfo(JSONObject jsonObject) throws JSONException {

    mPlayerId = jsonObject.getInt(JSON_IDENTIFIER_PLAYER_ID);
    mUserId = jsonObject.getInt(JSON_IDENTIFIER_USER_ID);
    mPlayerName = jsonObject.getString(JSON_IDENTIFIER_NAME);
    mColor = jsonObject.getString(JSON_IDENTIFIER_COLOR);
    String state = jsonObject.getString(JSON_IDENTIFIER_STATE);
    mLastMessage = jsonObject.getString(JSON_IDENTIFIER_LAST_MESSAGE);

    if (state.equals(State.PLAYING.toString())) {
      mState = State.PLAYING;
    } else if (state.equals(State.LOST.toString())) {
      mState = State.LOST;
    } else if (state.equals(State.WON.toString())) {
      mState = State.WON;
    } else if (state.equals(State.ABORTED.toString())) {
      mState = State.ABORTED;
    } else if (state.equals(State.TIMEOUT.toString())) {
      mState = State.TIMEOUT;
    } else {
      mState = State.UNKNOWN;
    }

    System.out.println(String.format("%d %s %s", mPlayerId, mPlayerName, state));
  }

  private int mPlayerId;
  private int mUserId;
  private String mPlayerName;
  private String mColor;
  private State mState;
  private int mTeam;
  private String mLastMessage;
}