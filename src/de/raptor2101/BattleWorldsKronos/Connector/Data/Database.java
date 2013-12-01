package de.raptor2101.BattleWorldsKronos.Connector.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;
import de.raptor2101.BattleWorldsKronos.Connector.Data.Entities.Game;
import de.raptor2101.BattleWorldsKronos.Connector.Data.Entities.Message;
import de.raptor2101.BattleWorldsKronos.Connector.Data.Entities.Player;

public class Database {
  private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
  private DbHelper mDbHelper;
  private SQLiteDatabase mDatabase;

  public Database(Context context) {
    mDbHelper = new DbHelper(context);
  }

  public void open() {
    mDatabase = mDbHelper.getWritableDatabase();
  }

  public void close() {
    mDbHelper.close();
    mDatabase = null;
  }

  public void persistMessage(List<Message> messages){
    long persistTimestamp = SystemClock.elapsedRealtime();
    
    for(Message message:messages){
      int messageId = message.getMessageId();
      ContentValues contentValues = buildContentValues(message, persistTimestamp);
      
      String whereCondition = String.format(DbHelper.EQUALS, DbHelper.TableMessage.Columns.MESSAGE_ID);
      String[] arguments = new String[] { String.valueOf(messageId) };
      int updatedRows = mDatabase.update(DbHelper.TableMessage.Name, contentValues, whereCondition, arguments);
      
      if(updatedRows == 0) {
        contentValues.put(DbHelper.TableMessage.Columns.NOTIFIED, 0);
        mDatabase.insert(DbHelper.TableMessage.Name, null, contentValues);
      }
    }
    
    deleteOldMessages(persistTimestamp);
    setLastUpdate(DbHelper.TableMessage.Name, persistTimestamp);
  }
  
  private void deleteOldMessages(long persistTimestamp) {
    mDatabase.execSQL(DbHelper.TableMessage.SqlCommands.DELETE_OLD_MESSAGES, new String[]{String.valueOf(persistTimestamp)});
  }
  
  public List<Message> getMessages(){
    Cursor cursor = mDatabase.query(DbHelper.TableMessage.Name, DbHelper.TableMessage.ALL_COLUMNS, DbHelper.TableMessage.SqlCommands.WHERE_MESSAGES_TO_READ, null, null, null, DbHelper.TableMessage.Columns.MESSAGE_ID);
    
    ArrayList<Message> messages = new ArrayList<Message>(cursor.getCount());
    
    cursor.moveToFirst();
    while(!cursor.isAfterLast()){
      try {
        Message message = new Message();
        
        message.setMessageId(cursor.getInt(0));
        message.setAuthorId(cursor.getInt(1));
        message.setAuthorName(cursor.getString(2));
        message.setTimestamp(DATE_FORMAT.parse(cursor.getString(3)));
        message.setMessageText(cursor.getString(4));
        message.setLastMessageId(cursor.getInt(5));
        message.setSystemMessage(cursor.getInt(6)==1);
        message.setReaded(cursor.getInt(7)==1);
        message.setDiscarded(cursor.getInt(8)==1);
        message.setDeleted(cursor.getInt(9)==1);
        
        messages.add(message);
      } catch (ParseException e) {
        e.printStackTrace();
      }
      cursor.moveToNext();
    }
    cursor.close();
    
    return messages;
  }

  private ContentValues buildContentValues(Message message, long persistTimestamp) {
    ContentValues contentValues = new ContentValues(DbHelper.TableMessage.ALL_COLUMNS.length);
    contentValues.put(DbHelper.TableMessage.Columns.MESSAGE_ID, message.getMessageId());
    contentValues.put(DbHelper.TableMessage.Columns.AUTHOR_ID, message.getAuthorId());
    contentValues.put(DbHelper.TableMessage.Columns.AUTHOR_NAME, message.getAuthorName());
    contentValues.put(DbHelper.TableMessage.Columns.TIMESTAMP, DATE_FORMAT.format(message.getTimestamp()));
    contentValues.put(DbHelper.TableMessage.Columns.MESSAGE, message.getMessageText());
    contentValues.put(DbHelper.TableMessage.Columns.LAST_MESSAGE_ID, message.getLastMessageId());
    contentValues.put(DbHelper.TableMessage.Columns.IS_SYSTEM_MESSAGE, message.isSystemMessage());
    contentValues.put(DbHelper.TableMessage.Columns.IS_READ, message.isReaded());
    contentValues.put(DbHelper.TableMessage.Columns.IS_DISCARDED, message.isDiscarded());
    contentValues.put(DbHelper.TableMessage.Columns.IS_DELETED, message.isDeleted());
    contentValues.put(DbHelper.TableMessage.Columns.PERSISTED, persistTimestamp);
    
    return contentValues;
  }

  public void persistGames(List<Game> games) {
    long persistTimestamp = SystemClock.elapsedRealtime();
    for (Game game : games) {
      ContentValues contentValues = buildContentValues(game, persistTimestamp);
      int gameId = game.getGameId();
      int modiefied = updateGame(gameId, game.getUpdateDate(), contentValues);
      if (modiefied == 0) {
        contentValues.put(DbHelper.TableGames.Columns.NOTIFIED, 0);
        insertGame(contentValues);

        for (Player player : game.getPlayers()) {
          contentValues = buildContentValues(gameId, player);
          insertPlayer(contentValues);
        }
      } else {
        for (Player player : game.getPlayers()) {
          contentValues = new ContentValues(2);
          contentValues.put(DbHelper.TablePlayers.Columns.STATE, player.getState().getValue());
          contentValues.put(DbHelper.TablePlayers.Columns.LAST_MESSAGE, player.getLastMessage());
          updatePlayer(player.getPlayerId(), contentValues);
        }
      }
    }
    
    deleteOldGames(persistTimestamp);
    setLastUpdate(DbHelper.TableGames.Name, persistTimestamp);
  }

  private void deleteOldGames(long persistTimestamp) {
    Cursor cursor = mDatabase.query(DbHelper.TableGames.Name, new String[]{DbHelper.TableGames.Columns.GAME_ID}, String.format(DbHelper.LESS_THAN, DbHelper.TableGames.Columns.PERSISTED), new String[]{String.valueOf(persistTimestamp)}, null, null, null);
    cursor.moveToFirst();
    while(!cursor.isAfterLast()){
      int gameId = cursor.getInt(0);
      
      mDatabase.delete(DbHelper.TablePlayers.Name, String.format(DbHelper.EQUALS,DbHelper.TablePlayers.Columns.GAME_ID), new String[]{String.valueOf(gameId)});
      mDatabase.delete(DbHelper.TableGames.Name, String.format(DbHelper.EQUALS,DbHelper.TableGames.Columns.GAME_ID), new String[]{String.valueOf(gameId)});
      cursor.moveToNext();
    }
    cursor.close();
  }

  private void insertPlayer(ContentValues contentValues) {
    mDatabase.insert(DbHelper.TablePlayers.Name, null, contentValues);
  }

  private ContentValues buildContentValues(int gameId, Player player) {
    ContentValues contentValues = new ContentValues(DbHelper.TablePlayers.ALL_COLUMNS.length);
    contentValues.put(DbHelper.TablePlayers.Columns.GAME_ID, gameId);
    contentValues.put(DbHelper.TablePlayers.Columns.PLAYER_ID, player.getPlayerId());
    contentValues.put(DbHelper.TablePlayers.Columns.NAME, player.getPlayerName());
    contentValues.put(DbHelper.TablePlayers.Columns.STATE, player.getState().getValue());
    contentValues.put(DbHelper.TablePlayers.Columns.TEAM, player.getTeam());
    contentValues.put(DbHelper.TablePlayers.Columns.USER_ID, player.getUserId());
    contentValues.put(DbHelper.TablePlayers.Columns.COLOR, player.getColor());
    contentValues.put(DbHelper.TablePlayers.Columns.LAST_MESSAGE, player.getLastMessage());
    return contentValues;
  }

  private ContentValues buildContentValues(Game game, long persistTimestamp) {
    ContentValues contentValues = new ContentValues(DbHelper.TableGames.ALL_COLUMNS.length);
    contentValues.put(DbHelper.TableGames.Columns.GAME_ID, game.getGameId());
    contentValues.put(DbHelper.TableGames.Columns.MAP_ID, game.getMapId());
    contentValues.put(DbHelper.TableGames.Columns.NAME, game.getGameName());
    contentValues.put(DbHelper.TableGames.Columns.OWNER_ID, game.getOwnerId());
    contentValues.put(DbHelper.TableGames.Columns.CURRENT_TURN, game.getCurrentTurn());
    contentValues.put(DbHelper.TableGames.Columns.CURRENT_ROUND, game.getCurrentRound());
    contentValues.put(DbHelper.TableGames.Columns.NEXT_PLAYER_ID, game.getActivePlayerId());
    contentValues.put(DbHelper.TableGames.Columns.STATE, game.getState().getValue());
    contentValues.put(DbHelper.TableGames.Columns.CREATED, DATE_FORMAT.format(game.getCreateDate()));
    contentValues.put(DbHelper.TableGames.Columns.UPDATED, DATE_FORMAT.format(game.getUpdateDate()));
    contentValues.put(DbHelper.TableGames.Columns.PERSISTED, persistTimestamp);
    contentValues.put(DbHelper.TableGames.Columns.NOTIFIED, 0);
    return contentValues;
  }

  private void updatePlayer(int playerId, ContentValues contentValues) {
    String whereCondition = String.format(DbHelper.EQUALS, DbHelper.TablePlayers.Columns.PLAYER_ID);
    String[] arguments = new String[] { String.valueOf(playerId) };
    mDatabase.update(DbHelper.TablePlayers.Name, contentValues, whereCondition, arguments);
  }

  private void insertGame(ContentValues contentValues) {
    mDatabase.insert(DbHelper.TableGames.Name, null, contentValues);
  }

  private int updateGame(int gameId, Date updateTimestamp, ContentValues contentValues) {

    Date savedUpdateTimestamp;
    savedUpdateTimestamp = getUpdateTimestamp(gameId);

    if (savedUpdateTimestamp == null) {
      return 0;
    }

    if (!updateTimestamp.after(savedUpdateTimestamp)) {
      long persistTimestamp = contentValues.getAsLong(DbHelper.TableGames.Columns.PERSISTED);
      contentValues = new ContentValues(1);
      contentValues.put(DbHelper.TableGames.Columns.PERSISTED, persistTimestamp);
    }

    String whereCondition = String.format(DbHelper.EQUALS, DbHelper.TableGames.Columns.GAME_ID);
    String[] arguments = new String[] { String.valueOf(gameId) };
    return mDatabase.update(DbHelper.TableGames.Name, contentValues, whereCondition, arguments);
  }

  private Date getUpdateTimestamp(int gameId) {
    Date returnDate = null;
    Cursor cursor = mDatabase.query(DbHelper.TableGames.Name, new String[] { DbHelper.TableGames.Columns.UPDATED }, String.format(DbHelper.EQUALS, DbHelper.TableGames.Columns.GAME_ID),
        new String[] { String.valueOf(gameId) }, null, null, null);
    try {

      cursor.moveToFirst();
      if (!cursor.isAfterLast()) {
        returnDate = DATE_FORMAT.parse(cursor.getString(0));
      }
    } catch (ParseException e) {
    } finally {
      cursor.close();
    }
    return returnDate;
  }

  public int getPendingGamesCount() {
    Cursor cursor = mDatabase.rawQuery(DbHelper.TableGames.SqlCommands.COUNT_GAMES_IN_STATE, new String[]{String.valueOf(Game.State.PENDING.getValue())});
    return getSingleIntFromCursor(cursor);
  }

  public int getUnnotfiedPendingGamesCount() {
    Cursor cursor = mDatabase.rawQuery(DbHelper.TableGames.SqlCommands.COUNT_UNNOTIFIED_GAMES_IN_STATE, new String[]{String.valueOf(Game.State.PENDING.getValue())});
    return getSingleIntFromCursor(cursor);
  }
  
  public void setAllGamesNotified(){
    ContentValues contentValues = new ContentValues(1);
    contentValues.put(DbHelper.TableGames.Columns.NOTIFIED, 1);
    mDatabase.update(DbHelper.TableGames.Name, contentValues, String.format(DbHelper.EQUALS, DbHelper.TableGames.Columns.NOTIFIED), new String[]{"0"});
  }
  
  public long getTimestampLastGameUpdate(){
    return getLastUpdate(DbHelper.TableGames.Name);
  }
  
  public long getTimestampMessagesUpdate(){
    return getLastUpdate(DbHelper.TableMessage.Name);
  }
  
  private long getLastUpdate(String tableName){
    Cursor cursor = mDatabase.query(DbHelper.TableLastUpdate.Name, new String[]{DbHelper.TableLastUpdate.Columns.Timestamp}, String.format(DbHelper.EQUALS,DbHelper.TableLastUpdate.Columns.Name), new String[]{tableName}, null, null, null);
    
    long returnValue = 0;
    cursor.moveToFirst();
    if(!cursor.isAfterLast()){
      returnValue = cursor.getLong(0);
    }
    cursor.close();
    
    return returnValue;
  }
  
  private void setLastUpdate(String tableName, long timestamp){
    ContentValues values = new ContentValues(2);
    values.put(DbHelper.TableLastUpdate.Columns.Name, tableName);
    values.put(DbHelper.TableLastUpdate.Columns.Timestamp, timestamp);
    
    int updatedRows = mDatabase.update(DbHelper.TableLastUpdate.Name, values, String.format(DbHelper.EQUALS,DbHelper.TableLastUpdate.Columns.Name), new String[]{tableName});
    if(updatedRows == 0){
      mDatabase.insert(DbHelper.TableLastUpdate.Name, null, values);
    }
  }
  
  
  private int getSingleIntFromCursor(Cursor cursor){
    int returnValue = 0;
    cursor.moveToFirst();
    if(!cursor.isAfterLast()){
      returnValue = cursor.getInt(0);
    }
    cursor.close();
    
    return returnValue;
  }

  public List<Game> getGames() {
    Cursor cursor = mDatabase.query(DbHelper.TableGames.Name, DbHelper.TableGames.ALL_COLUMNS, null, null, null, null, String.format(DbHelper.DESC, DbHelper.TableGames.Columns.UPDATED));
    
    ArrayList<Game> games = new ArrayList<Game>(cursor.getCount());
        
    cursor.moveToFirst();
    while(!cursor.isAfterLast()){
      try {
        Game game = new Game();
        game.setGameId(cursor.getInt(0));
        game.setMapId(cursor.getInt(1));
        game.setGameName(cursor.getString(2));
        game.setOwnerId(cursor.getInt(3));
        game.setCurrentRound(cursor.getInt(4));
        game.setCurrentTurn(cursor.getInt(5));
        game.setNextPlayerId(cursor.getInt(6));
        game.setState(Game.State.FromInt(cursor.getInt(7)));
        game.setUpdateDate(DATE_FORMAT.parse(cursor.getString(9)));
        game.setCreateDate(DATE_FORMAT.parse(cursor.getString(8)));
        
        games.add(game);
      } catch (ParseException e) {
        e.printStackTrace();
      }
      cursor.moveToNext();
    }
    cursor.close();
    
    for(Game game:games){
      loadPlayers(game);
    }
    
    return games;
  }
  
  private void loadPlayers(Game game) {
    Cursor cursor = mDatabase.query(DbHelper.TablePlayers.Name, DbHelper.TablePlayers.ALL_COLUMNS, String.format(DbHelper.EQUALS,DbHelper.TablePlayers.Columns.GAME_ID), new String[]{String.valueOf(game.getGameId())}, null, null, DbHelper.TablePlayers.Columns.PLAYER_ID);
    List<Player> players = new ArrayList<Player>(cursor.getCount());
    Player activePlayer = null, winner = null;
    
    int activePlayerId = game.getActivePlayerId();
    cursor.moveToFirst();
    while(!cursor.isAfterLast()){
      Player player = new Player();
      player.setPlayerId(cursor.getInt(1));
      player.setUserId(cursor.getInt(2));
      player.setTeam(cursor.getInt(3));
      player.setPlayerName(cursor.getString(4));
      player.setColor(cursor.getString(5));
      player.setState(Player.State.FromInt(cursor.getInt(6)));
      player.setLastMessage(cursor.getString(7));
      
      if(player.getState() == Player.State.WON){
        winner = player;
      }
      
      if(player.getPlayerId() == activePlayerId){
        activePlayer = player;
      }
      
      players.add(player);
      cursor.moveToNext();
    }
    cursor.close();
    
    game.setPlayers(players, winner, activePlayer);
  }
}
