package de.raptor2101.BattleWorldsKronos.Connector.JSON;

import java.text.ParseException;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.raptor2101.BattleWorldsKronos.Connector.Gui.R;

import android.content.Context;
import android.util.SparseArray;

public class GameInfo implements Comparable<GameInfo> {
  public int getMapId() {
    return mMapId;
  }

  public String getGameName() {
    return mGameName;
  }

  public int getOwnerId() {
    return mOwnerId;
  }

  public int getCurrentRound() {
    return mCurrentRound;
  }

  public int getCurrentTurn() {
    return mCurrentTurn;
  }

  public int getNextPlayerId() {
    return mNextPlayerId;
  }

  public SparseArray<PlayerInfo> getPlayers() {
    return mPlayers;
  }

  public State getState() {
    return mState;
  }

  public Date getCreateDate() {
    return mCreateDate;
  }

  public Date getUpdateDate() {
    return mUpdateDate;
  }

  public int getGameId() {
    return mGameId;
  }

  public enum State {
    RUNNING("running", R.string.game_info_state_running, R.color.game_state_running), ENDED("ended", R.string.game_info_state_ended, R.color.game_state_ended), LOST("lost",
        R.string.game_info_state_lost, R.color.game_state_lost), WON("won", R.string.game_info_state_won, R.color.game_state_won), PENDING("pending", R.string.game_info_state_pending,
        R.color.game_state_pending), WAITING("waiting", R.string.game_info_state_waiting, R.color.game_state_waiting), OPEN("open", R.string.game_info_state_open, R.color.game_state_open), ABORTED(
        "aborted", R.string.game_info_state_aborted, R.color.game_state_aborted), UNKNOWN("unknown", R.string.game_info_state_unknown, R.color.game_state_unknown);

    private final String mValue;
    private final int mResourceId;
    private final int mColorId;

    private State(final String value, final int resourceId, final int colorId) {
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

    @Override
    public String toString() {
      return mValue;
    }
  }

  private static final String JSON_IDENTIFIER_GAME_ID = "gameId";
  private static final String JSON_IDENTIFIER_MAP_ID = "mapId";
  private static final String JSON_IDENTIFIER_NAME = "name";
  private static final String JSON_IDENTIFIER_OWNER_ID = "ownerId";
  private static final String JSON_IDENTIFIER_CURRENT_ROUND = "currentRound";
  private static final String JSON_IDENTIFIER_CURRENT_TURN = "currentTurn";
  private static final String JSON_IDENTIFIER_NEXT_PLAYER_ID = "nextplayerId";
  private static final String JSON_IDENTIFIER_PLAYERS = "players";
  private static final String JSON_IDENTIFIER_STATE = "state";
  private static final String JSON_IDENTIFIER_CREATED = "created";
  private static final String JSON_IDENTIFIER_UPDATED = "updated";

  private int mGameId;
  private int mMapId;
  private String mGameName;
  private int mOwnerId;
  private int mCurrentRound;
  private int mCurrentTurn;
  private int mNextPlayerId;
  private SparseArray<PlayerInfo> mPlayers;
  private State mState;
  private Date mCreateDate;
  private Date mUpdateDate;

  public GameInfo(JSONObject jsonObject, int userId) throws JSONException, ParseException {
    mGameId = jsonObject.getInt(JSON_IDENTIFIER_GAME_ID);
    mMapId = jsonObject.getInt(JSON_IDENTIFIER_MAP_ID);
    mGameName = jsonObject.getString(JSON_IDENTIFIER_NAME);
    mOwnerId = jsonObject.getInt(JSON_IDENTIFIER_OWNER_ID);
    mCurrentRound = jsonObject.getInt(JSON_IDENTIFIER_CURRENT_ROUND);
    mCurrentTurn = jsonObject.getInt(JSON_IDENTIFIER_CURRENT_TURN);
    mNextPlayerId = jsonObject.getInt(JSON_IDENTIFIER_NEXT_PLAYER_ID);
    String state = jsonObject.getString(JSON_IDENTIFIER_STATE);
    String dateString = jsonObject.getString(JSON_IDENTIFIER_CREATED);
    mCreateDate = ServerConnection.DateFormat.parse(dateString);
    dateString = jsonObject.getString(JSON_IDENTIFIER_UPDATED);
    mUpdateDate = ServerConnection.DateFormat.parse(dateString);
    System.out.println(String.format("%d %s %d %s", mGameId, mGameName, mNextPlayerId, state));

    JSONArray playerArray = jsonObject.getJSONArray(JSON_IDENTIFIER_PLAYERS);

    PlayerInfo playerInfoAssignedToUser = null;
    PlayerInfo playerInfoAssignedToWinner = null;
    mPlayers = new SparseArray<PlayerInfo>(playerArray.length());
    for (int i = 0; i < playerArray.length(); i++) {
      PlayerInfo playerInfo = new PlayerInfo(playerArray.getJSONObject(i));
      mPlayers.put(playerInfo.getPlayerId(), playerInfo);
      if (playerInfo.getUserId() == userId) {
        playerInfoAssignedToUser = playerInfo;
      }
      if (playerInfo.getState() == PlayerInfo.State.WON) {
        playerInfoAssignedToWinner = playerInfo;
      }
    }
    mState = State.UNKNOWN;

    if (state.equals(State.ENDED.toString())) {
      if (playerInfoAssignedToUser != null) {
        if (playerInfoAssignedToUser.getState() == PlayerInfo.State.LOST) {
          mState = State.LOST;
        } else if (playerInfoAssignedToUser.getState() == PlayerInfo.State.WON) {
          mState = State.WON;
        } else if (playerInfoAssignedToWinner != null && !playerInfoAssignedToWinner.equals(playerInfoAssignedToUser)) {
          mState = State.LOST;
        }
      }
      if (mState == State.UNKNOWN) {
        mState = State.ENDED;
      }
    } else if (state.equals(State.ABORTED.toString())) {
      mState = State.ABORTED;
    } else if (state.equals(State.RUNNING.toString())) {
      if (mNextPlayerId == playerInfoAssignedToUser.getPlayerId()) {
        mState = State.PENDING;
      } else {
        mState = State.WAITING;
      }
    } else if (state.equals(State.OPEN.toString())) {
      mState = State.OPEN;
    }

  }

  @Override
  public int compareTo(GameInfo another) {
    return mUpdateDate.compareTo(another.mUpdateDate);
  }

  @Override
  public int hashCode() {
    return mGameId;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    GameInfo other = (GameInfo) obj;
    return mGameId == other.mGameId;
  }
}
