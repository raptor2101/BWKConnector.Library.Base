package de.raptor2101.BattleWorldsKronos.Connector.Data.Entities;

import android.content.Context;
import android.graphics.AvoidXfermode;
import android.util.SparseArray;
import de.raptor2101.BattleWorldsKronos.Connector.Gui.R;

public class Player {
  
  public enum State {
    PLAYING(0, R.string.player_state_playing, R.color.player_state_playing),
    LOST(1, R.string.player_state_lost, R.color.player_state_lost),
    WON(2, R.string.player_state_won, R.color.player_state_won),
    ABORTED(3, R.string.player_state_aborted, R.color.player_state_aborted),
    UNKNOWN(4, R.string.player_state_unknown, R.color.player_state_unknown),
    TIMEOUT(5, R.string.player_state_timeout, R.color.player_state_timeout),
    ACTIVE(6, R.string.player_state_active, R.color.player_state_active),
    WAITING(7, R.string.player_state_waiting, R.color.player_state_waiting),;

    private static final SparseArray<State> intToState = new SparseArray<State>();
    
    static {
      intToState.put(PLAYING.getValue(), PLAYING);
      intToState.put(LOST.getValue(), LOST);
      intToState.put(WON.getValue(), WON);
      intToState.put(TIMEOUT.getValue(), TIMEOUT);
      intToState.put(ABORTED.getValue(), ABORTED);
      intToState.put(ACTIVE.getValue(), ACTIVE);
      intToState.put(WAITING.getValue(), WAITING);
      intToState.put(UNKNOWN.getValue(), UNKNOWN);
    }
    
    private final int mValue;
    private final int mResourceId;
    private final int mColorId;

    private State(final int value, final int resourceId, final int colorId) {
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

    public int getValue() {
      return mValue;
    }
    
    public static State FromInt(int value) {
      State returnValue = intToState.get(value);
      
      if(returnValue == null){
        return State.UNKNOWN;
      }
      
      return returnValue; 
    }
  }
  
  private int mPlayerId;
  private int mUserId;
  private String mPlayerName;
  private String mColor;
  private State mState;
  private int mTeam;
  private String mLastMessage;
  
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

  public void setPlayerId(int playerId) {
    this.mPlayerId = playerId;
  }

  public void setUserId(int userId) {
    this.mUserId = userId;
  }

  public void setPlayerName(String playerName) {
    this.mPlayerName = playerName;
  }

  public void setColor(String color) {
    this.mColor = color;
  }

  public void setState(State state) {
    this.mState = state;
  }

  public void setTeam(int team) {
    this.mTeam = team;
  }

  public void setLastMessage(String lastMessage) {
    this.mLastMessage = lastMessage;
  }
  
  
}
