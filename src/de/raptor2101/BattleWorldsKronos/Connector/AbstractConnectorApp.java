package de.raptor2101.BattleWorldsKronos.Connector;

import java.util.HashSet;

import android.app.Application;
import android.os.SystemClock;

import de.raptor2101.BattleWorldsKronos.Connector.JSON.GameInfo;
import de.raptor2101.BattleWorldsKronos.Connector.JSON.GameListing;

public abstract class AbstractConnectorApp extends Application {
  private GameListing mStoredResult;
  private long mTimestampResultStored;

  public HashSet<GameInfo> getLastPendingGames() {

    return mStoredResult == null ? null : mStoredResult.getPendingGames();
  }

  public GameListing getResult() {
    return mStoredResult;
  }

  public void storeResult(GameListing result) {
    if(result!=null && result != mStoredResult){
      mStoredResult = result;
      mTimestampResultStored = SystemClock.elapsedRealtime();
    }
  }

  public long getTimestampResultStored() {
    return mTimestampResultStored;
  }
}
