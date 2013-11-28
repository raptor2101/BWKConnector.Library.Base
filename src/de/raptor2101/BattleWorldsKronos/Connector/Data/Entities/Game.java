package de.raptor2101.BattleWorldsKronos.Connector.Data.Entities;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.util.SparseArray;
import de.raptor2101.BattleWorldsKronos.Connector.Gui.R;

public class Game {
  
  public enum State {
    RUNNING(0, R.string.game_state_running, R.color.game_state_running), 
    ENDED(1, R.string.game_state_ended, R.color.game_state_ended), 
    LOST(2, R.string.game_state_lost, R.color.game_state_lost), 
    WON(3, R.string.game_state_won, R.color.game_state_won), 
    PENDING(4, R.string.game_state_pending, R.color.game_state_pending), 
    WAITING(5, R.string.game_state_waiting, R.color.game_state_waiting), 
    OPEN(6, R.string.game_state_open, R.color.game_state_open), 
    ABORTED(7, R.string.game_state_aborted, R.color.game_state_aborted), 
    UNKNOWN(8, R.string.game_state_unknown, R.color.game_state_unknown);

    private static final SparseArray<State> intToState = new SparseArray<State>();
    
    static {
      intToState.put(RUNNING.getValue(), RUNNING);
      intToState.put(ENDED.getValue(), ENDED);
      intToState.put(LOST.getValue(), LOST);
      intToState.put(WON.getValue(), WON);
      intToState.put(PENDING.getValue(), PENDING);
      intToState.put(WAITING.getValue(), WAITING);
      intToState.put(OPEN.getValue(), OPEN);
      intToState.put(ABORTED.getValue(), ABORTED);
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

    public int getResourceId() {
      return mResourceId;
    }

    public int getColorId() {
      return mColorId;
    }

    public int getColor(Context context) {
      return context.getResources().getColor(mColorId);
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
  
  private Date mCreateDate;
  private int mCurrentRound;
  private int mCurrentTurn;
  private int mGameId;
  private String mGameName;
  private int mMapId;
  private int mActivePlayerId;
  private int mOwnerId;
  private State mState;
  private Date mUpdateDate;
  private List<Player> mPlayers;
  private Player mWinner;
  private Player mActivePlayer;
  
  public Player getWinner() {
    return mWinner;
  }
  
  public void setWinner(Player player) {
    mWinner = player;
  }
  
  public Date getCreateDate() {
    return mCreateDate;
  }

  public int getCurrentRound() {
    return mCurrentRound;
  }

  public int getCurrentTurn() {
    return mCurrentTurn;
  }

  public int getGameId() {
    return mGameId;
  }

  public String getGameName() {
    return mGameName;
  }

  public int getMapId() {
    return mMapId;
  }

  public int getActivePlayerId() {
    return mActivePlayerId;
  }

  public int getOwnerId() {
    return mOwnerId;
  }

  public List<Player> getPlayers() {
    return mPlayers;
  }

  public State getState() {
    return mState;
  }

  public Date getUpdateDate() {
    return mUpdateDate;
  }

  public void setCreateDate(Date createDate) {
    this.mCreateDate = createDate;
  }

  public void setCurrentRound(int currentRound) {
    this.mCurrentRound = currentRound;
  }

  public void setCurrentTurn(int currentTurn) {
    this.mCurrentTurn = currentTurn;
  }

  public void setGameId(int gameId) {
    this.mGameId = gameId;
  }

  public void setGameName(String gameName) {
    this.mGameName = gameName;
  }

  public void setMapId(int mapId) {
    this.mMapId = mapId;
  }

  public void setNextPlayerId(int nextPlayerId) {
    this.mActivePlayer = null;
    this.mActivePlayerId = nextPlayerId;
  }

  public void setOwnerId(int mOwnerId) {
    this.mOwnerId = mOwnerId;
  }

  public void setPlayers(List<Player> players, Player winner, Player nextPlayer) {
    this.mActivePlayer = nextPlayer;
    this.mWinner = winner;
    this.mPlayers = players;
  }

  public void setState(State state) {
    this.mState = state;
  }

  public void setUpdateDate(Date updateDate) {
    this.mUpdateDate = updateDate;
  }

  public Player getActivePlayer() {
    if(mActivePlayer == null && mPlayers != null){
      for(Player player:mPlayers){
        if(mActivePlayerId == player.getPlayerId()){
          mActivePlayer = player;
          break;
        }
      }
    }
    
    return mActivePlayer;
  }
}
