package de.raptor2101.BattleWorldsKronos.Connector.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DbHelper extends SQLiteOpenHelper {
  public static final String EQUALS = "%s = ?";
  public static final String LESS_THAN = "%s < ?";
  public static final String DESC = "%s DESC";
  
  
  static class TableGames {
    public static final String Name = "games";
    public static final String[] ALL_COLUMNS = { Columns.GAME_ID, Columns.MAP_ID, Columns.NAME, Columns.OWNER_ID, Columns.CURRENT_ROUND, Columns.CURRENT_TURN, Columns.NEXT_PLAYER_ID, Columns.STATE, Columns.CREATED,
        Columns.UPDATED , Columns.PERSISTED, Columns.NOTIFIED};

    static class Columns {
      public static final String GAME_ID = "game_id";
      public static final String MAP_ID = "map_id";
      public static final String NAME = "name";
      public static final String OWNER_ID = "owner_id";
      public static final String CURRENT_TURN = "current_turn";
      public static final String CURRENT_ROUND = "current_round";
      public static final String NEXT_PLAYER_ID = "next_player_id";
      public static final String STATE = "state";
      public static final String CREATED = "created";
      public static final String UPDATED = "updated";
      public static final String PERSISTED = "persisted";
      public static final String NOTIFIED = "notified";
    }

    static class SqlCommands {
      public static final String CREATE_TABLE = String.format("CREATE TABLE %s (" + 
                  "%s INTEGER       NOT NULL PRIMARY KEY," + 
                  "%s INTEGER       NOT NULL," + 
                  "%s VARCHAR(250)  NOT NULL," + 
                  "%s INTEGER       NOT NULL," +
                  "%s INTEGER       NOT NULL," + 
                  "%s INTEGER       NOT NULL," + 
                  "%s INTEGER       NOT NULL," + 
                  "%s INTEGER       NOT NULL," + 
                  "%s DATETIME      NOT NULL," + 
                  "%s DATETIME      NOT NULL," +
                  "%s INTEGER       NOT NULL," + 
                  "%s INTEGER       NOT NULL" + 
                  ")",
          Name, Columns.GAME_ID, Columns.MAP_ID, Columns.NAME, Columns.OWNER_ID, Columns.CURRENT_TURN, Columns.CURRENT_ROUND, Columns.NEXT_PLAYER_ID, Columns.STATE, Columns.CREATED, Columns.UPDATED,
          Columns.PERSISTED, Columns.NOTIFIED);
      public static final String DROP_TABLE = String.format("DROP TABLE IF EXISTS %s", Name);
      public static final String COUNT_UNNOTIFIED_GAMES_IN_STATE = String.format("SELECT COUNT(*) FROM %s WHERE %s = 0 AND %s = ?", Name,Columns.NOTIFIED,Columns.STATE);
      public static final String COUNT_GAMES_IN_STATE = String.format("SELECT COUNT(*) FROM %s WHERE %s = ?", Name, Columns.STATE);
      public static final String MIN_PERSIST_TIMESTAMP = String.format("SELECT MIN(%s) FROM %s", Columns.PERSISTED, Name);
    }
  }

  static class TablePlayers{
    public static final String Name = "players";
    public static final String[] ALL_COLUMNS = {Columns.GAME_ID, Columns.PLAYER_ID, Columns.USER_ID, Columns.TEAM, Columns.NAME, Columns.COLOR, Columns.STATE, Columns.LAST_MESSAGE}; 
    
    static class Columns {
      public static final String GAME_ID = "game_id";
      public static final String PLAYER_ID = "player_id";
      public static final String USER_ID = "user_id";
      public static final String NAME = "name";
      public static final String TEAM = "team";
      public static final String COLOR = "color";
      public static final String STATE = "state";
      public static final String LAST_MESSAGE = "last_message";
    }
    
    static class SqlCommands {
      public static final String CREATE_TABLE = String.format("CREATE TABLE %s (" +
          "%s INTEGER       NOT NULL," + 
          "%s INTEGER       NOT NULL," + 
          "%s INTEGER       NOT NULL," + 
          "%s VARCHAR(250)  NOT NULL," + 
          "%s INTEGER       NOT NULL," +
          "%s VARCHAR(7)    NOT NULL," + 
          "%s INTEGER       NOT NULL," + 
          "%s VARCHAR(140)  NOT NULL," +
          "PRIMARY KEY (%s,%s))",
        Name, Columns.GAME_ID, Columns.PLAYER_ID, Columns.USER_ID, Columns.NAME, Columns.TEAM, Columns.COLOR, Columns.STATE, Columns.LAST_MESSAGE, Columns.GAME_ID, Columns.PLAYER_ID);
      public static final String DROP_TABLE = String.format("DROP TABLE IF EXISTS %s", Name);
    }
  }
  
  static class TableMessage{
    public static final String Name = "messages";
    public static final String[] ALL_COLUMNS = new String[]{Columns.MESSAGE_ID, Columns.AUTHOR_ID, Columns.AUTHOR_NAME, Columns.TIMESTAMP, Columns.MESSAGE, Columns.LAST_MESSAGE_ID, Columns.IS_SYSTEM_MESSAGE, Columns.IS_READ, Columns.IS_DISCARDED, Columns.IS_DELETED, Columns.PERSISTED, Columns.NOTIFIED};
    
    static class Columns {
      public static final String MESSAGE_ID = "message_id";
      public static final String AUTHOR_ID = "author_id";
      public static final String AUTHOR_NAME = "author_name";
      public static final String TIMESTAMP = "timestamp";
      public static final String MESSAGE = "message";
      public static final String LAST_MESSAGE_ID = "last_message_id";
      
      public static final String IS_SYSTEM_MESSAGE = "is_system_message";
      public static final String IS_READ = "is_read";
      public static final String IS_DISCARDED = "is_discarded";
      public static final String IS_DELETED = "is_deleted";
      
      public static final String PERSISTED = "persisted";
      public static final String NOTIFIED = "notified";
    }
    
    static class SqlCommands {
      public static final String CREATE_TABLE = String.format("CREATE TABLE %s (" +
          "%s INTEGER       NOT NULL PRIMARY KEY," + 
          "%s INTEGER       NOT NULL," + 
          "%s VARCHAR(250)  NOT NULL," + 
          "%s DATETIME      NOT NULL," + 
          "%s TEXT          NOT NULL," +
          "%s INTEGER       NOT NULL," + 
          "%s INTEGER       NOT NULL," +
          "%s INTEGER       NOT NULL," +
          "%s INTEGER       NOT NULL," +
          "%s INTEGER       NOT NULL," +
          "%s INTEGER       NOT NULL," + 
          "%s INTEGER       NOT NULL" + 
          ")",
        Name, Columns.MESSAGE_ID, Columns.AUTHOR_ID, Columns.AUTHOR_NAME, Columns.TIMESTAMP, Columns.MESSAGE, Columns.LAST_MESSAGE_ID, Columns.IS_SYSTEM_MESSAGE, Columns.IS_READ, Columns.IS_DISCARDED, Columns.IS_DELETED,Columns.PERSISTED,Columns.NOTIFIED);
      public static final String DROP_TABLE = String.format("DROP TABLE IF EXISTS %s", Name);
      public static final String DELETE_OLD_MESSAGES = String.format("DELETE FROM %s WHERE %s = 1 AND %s < ?", Name, Columns.IS_DELETED, Columns.PERSISTED);
      public static final String WHERE_MESSAGES_TO_READ = String.format("%s = 0 AND %s = 0", Columns.IS_DISCARDED,Columns.IS_DELETED);
    }
  }
  
  static class TableLastUpdate{
    public static final String Name = "last_update";
    
    static class Columns {
      public static final String Name = "name";
      public static final String Timestamp = "timestamp";
    }
    
    static class SqlCommands {
      public static final String CREATE_TABLE = String.format("CREATE TABLE %s (" +
          "%s VARCHAR(50)   NOT NULL PRIMARY KEY,"+
          "%s INTEGER       NOT NULL" +
          ")",
          Name,Columns.Name,Columns.Timestamp);
      public static final String DROP_TABLE = String.format("DROP TABLE IF EXISTS %s", Name);
    }
  }
  
  private static final String DATABASE_NAME = "connector.db";
  private static final int DATABASE_VERSION = 4;
  

  public DbHelper(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase database) {
    database.execSQL(TableGames.SqlCommands.CREATE_TABLE);
    database.execSQL(TablePlayers.SqlCommands.CREATE_TABLE);
    database.execSQL(TableMessage.SqlCommands.CREATE_TABLE);
    database.execSQL(TableLastUpdate.SqlCommands.CREATE_TABLE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
    database.execSQL(TableGames.SqlCommands.DROP_TABLE);
    database.execSQL(TablePlayers.SqlCommands.DROP_TABLE);
    database.execSQL(TableMessage.SqlCommands.DROP_TABLE);
    database.execSQL(TableLastUpdate.SqlCommands.DROP_TABLE);
    database.execSQL(TableGames.SqlCommands.CREATE_TABLE);
    database.execSQL(TablePlayers.SqlCommands.CREATE_TABLE);
    database.execSQL(TableMessage.SqlCommands.CREATE_TABLE);
    database.execSQL(TableLastUpdate.SqlCommands.CREATE_TABLE);
  }
  
  
}
