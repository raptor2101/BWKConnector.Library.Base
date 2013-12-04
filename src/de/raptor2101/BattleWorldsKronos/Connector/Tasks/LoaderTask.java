package de.raptor2101.BattleWorldsKronos.Connector.Tasks;

import de.raptor2101.BattleWorldsKronos.Connector.AbstractConnectorApp;
import de.raptor2101.BattleWorldsKronos.Connector.Data.Database;

public abstract class LoaderTask<TResultType> extends ServerConnectionTask<Boolean, TResultType>{
  
  private Database mDatabase;
  
  public LoaderTask(AbstractConnectorApp app, ResultListener<TResultType> resultListener) {
    super(app, resultListener);
    mDatabase = app.getDatabase();
  }
  
  protected Database getDatabase(){
    return mDatabase;
  }
}
