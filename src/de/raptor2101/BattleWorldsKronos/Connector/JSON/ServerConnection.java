package de.raptor2101.BattleWorldsKronos.Connector.JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.raptor2101.BattleWorldsKronos.Connector.Data.Entities.Game;
import de.raptor2101.BattleWorldsKronos.Connector.Data.Entities.League;
import de.raptor2101.BattleWorldsKronos.Connector.Data.Entities.LeagueMember;
import de.raptor2101.BattleWorldsKronos.Connector.Data.Entities.Message;
import de.raptor2101.BattleWorldsKronos.Connector.Data.Entities.Player;
import de.raptor2101.BattleWorldsKronos.Connector.Data.Entities.User;

public class ServerConnection {

  public static final SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-d kk:mm:ss",Locale.getDefault());
  
  
  
  private static final String serverUrl = "http://54.229.75.183";

  private static final String ENCODING = "UTF-8";

  private static final String JSON_IDENTIFIER_JSON_RPC = "jsonrpc";
  private static final String JSON_IDENTIFIER_METHOD = "method";
  private static final String JSON_IDENTIFIER_PARAMS = "params";
  private static final String JSON_IDENTIFIER_RESULT = "result";
  private static final String JSON_IDENTIFIER_USER_ID = "userId";
  private static final String JSON_IDENTIFIER_ID = "id";
  
  private class GameObjectIdentifiers {
    public static final String GAME_ID = "gameId";
    public static final String MAP_ID = "mapId";
    public static final String NAME = "name";
    public static final String OWNER_ID = "ownerId";
    public static final String CURRENT_ROUND = "currentRound";
    public static final String NEXT_PLAYER_ID = "nextplayerId";
    public static final String PLAYERS = "players";
    public static final String STATE = "state";
    public static final String CREATED = "created";
    public static final String UPDATED = "updated";
    public static final String CURRENT_TURN = "currentTurn";
  }
  
  private class PlayerObjectIdentifiers {
    public static final String PLAYER_ID = "playerId";
    public static final String USER_ID = "userId";
    public static final String TEAM = "team";
    public static final String NAME = "name";
    public static final String COLOR = "color";
    public static final String STATE = "state";
    public static final String LAST_MESSAGE = "last_message";
  }
  
  private class MessageObjectIdentifiers {
    public static final String ID = "id";
    public static final String AUTHOR_ID = "author_id";
    public static final String AUTHOR_NAME = "author_name";
    public static final String TIMESTAMP = "timestamp";
    public static final String MESSAGE = "message";
    public static final String LAST_MESSAGE_ID = "last_message_id";
    public static final String SYSTEM_MESSAGE = "system_message";
    
    public static final String RECEIVER_NAME = "receiverName";
    public static final String LAST_MESSAGE = "lastMessage";
    public static final String USER_ID = "userId";
    
    public static final String READ = "read";
    public static final String DISCARDED = "discarded";
    public static final String DELETED = "deleted";
  }
  
  private class UserObjectIdentifiers {
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String AVATAR_ID = "avatar_id";
    private static final String REGISTERED = "registered";
    private static final String LAST_LOGIN = "last_login";
    private static final String REALNAME = "realname";
    private static final String HOMETOWN = "howetown";
    private static final String LANGUAGES = "languages";
  }
  
  private class LeagueObjectIdentifiers {
    private static final String LEAGUE_ID = "league_id";
    private static final String ID = "id";
    private static final String NAME = "name";
    //private static final String PASSWORD = "password";
    private static final String IMAGE = "image";
    private static final String PLAYER_COUNT ="playerCount";
  }
  
  private class LeagueMemberObjectIdentifiers {
    private static final String USER= "user";
    private static final String PLAYER_MATCHES= "played_matches";
    private static final String FINISHED_MATCHES = "finished_matches";
    private static final String WON_MATCHES = "won_matches";
    private static final String ON_WATCHLIST = "on_watchlist";
    private static final String ON_BLOCKLIST = "on_blocklist";
    private static final String MP_STATS = "mp_stats";
  }
  
  private class LeagueStatisticsObjectIdentifiers {
    private static final String ID= "statsId";
    private static final String USER_ID = "userId";
    private static final String POINTS = "points";
    private static final String RANK = "rank";
    private static final String RATING = "rating";
    private static final String RANKING = "ranking";
    private static final String FINISHED = "finished";
    private static final String WON = "won";
    private static final String HONORABLE = "honorable";
  }
  
  public static final HashMap<String, Player.State> PLAYER_STATES = new HashMap<String, Player.State>();
  public static final HashMap<String, Game.State> GAME_STATES = new HashMap<String, Game.State>();
    
  static {
    PLAYER_STATES.put("playing", Player.State.PLAYING);
    PLAYER_STATES.put("lost", Player.State.LOST);
    PLAYER_STATES.put("won", Player.State.WON);
    PLAYER_STATES.put("aborted", Player.State.ABORTED);
    PLAYER_STATES.put("unknown", Player.State.UNKNOWN);
    PLAYER_STATES.put("timeout", Player.State.TIMEOUT);
    
    GAME_STATES.put("ended", Game.State.ENDED);
    GAME_STATES.put("aborted", Game.State.ABORTED);
    GAME_STATES.put("running", Game.State.RUNNING);
    GAME_STATES.put("open", Game.State.OPEN);
  }

  private enum JsonMethod {
    LOGIN(1, "login", serverUrl+"/api/userservice.php"),
    GETMESSAGES(2, "getMessages", serverUrl+"/api/userservice.php"),
    GETGAMES(5, "getGames", serverUrl+"/api/gameservice.php"),
    SENDMESSAGE(10,"sendMessage",serverUrl+"/api/userservice.php"),
    DELETEMESSAGE(149,"deleteMessage",serverUrl+"/api/userservice.php"),
    GETLEAGUEDETAILS(10,"getLeagueDetails",serverUrl+"/api/leagueservice.php"),
    GETLEAGUES(12,"getLeagues",serverUrl+"/api/leagueservice.php"),
    GETMYLEAGUES(14,"getMyLeagues",serverUrl+"/api/leagueservice.php");
    

    private final int mIntValue;
    private final String mStringValue;
    private final String mUri;
    
    private JsonMethod(final int intValue, final String stringValue, final String uri) {
      mIntValue = intValue;
      mStringValue = stringValue;
      mUri = uri;
    }

    public int getValue() {
      return mIntValue;
    }
    
    public String getUri(){
      return mUri;
    }

    @Override
    public String toString() {
      return mStringValue;
    }
  }

  private static String JSON_RPC = "2.0";
  private static String GAME_VERSION = "1.3.5";

  private HttpClient mHttpClient;

  private String sessionValue;
  
  private int mUserId;

  public ServerConnection(HttpClient httpClient) {
    mHttpClient = httpClient;
  }

  public boolean login(String eMail, String password) throws ClientProtocolException, IOException {
    
    try {
      String responseText = performMethod(JsonMethod.LOGIN,eMail, password, GAME_VERSION);
      
      JSONObject jsonObject = new JSONObject(responseText);
      jsonObject = jsonObject.getJSONObject(JSON_IDENTIFIER_RESULT);
      
      mUserId = jsonObject.getInt(JSON_IDENTIFIER_USER_ID);
      
      return true;
    } catch (JSONException e) {

      e.printStackTrace();
    }
    
    return false;
  }

  public List<League> getLeagues(Boolean loadMyLeaguesOnly) throws JSONException, ClientProtocolException, IOException, ParseException {
    String responseText;
    if(loadMyLeaguesOnly){
      responseText = performMethod(JsonMethod.GETMYLEAGUES);
    }else{
      responseText = performMethod(JsonMethod.GETLEAGUES);
    }
        
    
    JSONObject jsonObject = new JSONObject(responseText);
    jsonObject = jsonObject.getJSONObject(JSON_IDENTIFIER_RESULT);
    
    JSONArray jsonLeagues = jsonObject.getJSONArray("leagues");
    
    List<League> leagues = new ArrayList<League>(jsonLeagues.length());
    for(int i=0;i<jsonLeagues.length();i++){
      leagues.add(decodeLeague(jsonLeagues.getJSONObject(i)));
    }
    
    return null;
  }
  
  public List<LeagueMember> getLeageMembers(int leagueId) throws JSONException, ClientProtocolException, IOException, ParseException{
    JSONObject jsonObject = new JSONObject();
    jsonObject.put(LeagueObjectIdentifiers.LEAGUE_ID, leagueId);
    
    
    String responseText = performMethod(JsonMethod.GETLEAGUEDETAILS,jsonObject);
    jsonObject = new JSONObject(responseText);
    jsonObject = jsonObject.getJSONObject(JSON_IDENTIFIER_RESULT);
    JSONArray jsonMembers = jsonObject.getJSONArray("member");
    
    List<LeagueMember> leagues = new ArrayList<LeagueMember>(jsonMembers.length());
    for(int i=0;i<jsonMembers.length();i++){
      leagues.add(decodeLeagueMember(jsonMembers.getJSONObject(i)));
    }
    
    return leagues;
  }
  
  private League decodeLeague(JSONObject jsonObject) throws JSONException{
    League league = new League();
    
    league.setId(jsonObject.getInt(LeagueObjectIdentifiers.ID));
    league.setName(jsonObject.getString(LeagueObjectIdentifiers.NAME));
    league.setImage(jsonObject.getString(LeagueObjectIdentifiers.IMAGE));
    league.setPlayerCounter(jsonObject.getInt(LeagueObjectIdentifiers.PLAYER_COUNT));
    
    return league;
  }
  
  private User decodeUser(JSONObject jsonObject) throws JSONException, ParseException {
    User user = new User();
    
    user.setId(jsonObject.getInt(UserObjectIdentifiers.ID));
    user.setName(jsonObject.getString(UserObjectIdentifiers.NAME));
    user.setAvatarId(jsonObject.getString(UserObjectIdentifiers.AVATAR_ID));
    
    String dateString = jsonObject.getString(UserObjectIdentifiers.REGISTERED);
    user.setRegistered(DateFormat.parse(dateString));
    
    dateString = jsonObject.getString(UserObjectIdentifiers.LAST_LOGIN);
    user.setLastLogin(DateFormat.parse(dateString));
    
    user.setRealname(jsonObject.getString(UserObjectIdentifiers.REALNAME));
    user.setHometown(jsonObject.getString(UserObjectIdentifiers.HOMETOWN));
    String languagesString = jsonObject.getString(UserObjectIdentifiers.LANGUAGES);
    List<User.Language> languages = new ArrayList<User.Language>();
    user.setLanguages(languages);
    
    return user;
  }
  
  private LeagueMember decodeLeagueMember(JSONObject jsonObject) throws JSONException, ParseException{
    LeagueMember leagueMember = new LeagueMember();
    
    User user = decodeUser(jsonObject.getJSONObject(LeagueMemberObjectIdentifiers.USER));
    leagueMember.setUser(user);
    
    leagueMember.setPlayedMatches(jsonObject.getInt(LeagueMemberObjectIdentifiers.PLAYER_MATCHES));
    leagueMember.setFinishedMatches(jsonObject.getInt(LeagueMemberObjectIdentifiers.FINISHED_MATCHES));
    leagueMember.setWonMatches(jsonObject.getInt(LeagueMemberObjectIdentifiers.WON_MATCHES));
    leagueMember.setOnWatchlist(jsonObject.getBoolean(LeagueMemberObjectIdentifiers.ON_WATCHLIST));
    leagueMember.setOnBlocklist(jsonObject.getBoolean(LeagueMemberObjectIdentifiers.ON_BLOCKLIST));
    
    LeagueMember.Statistic statistic = decodeLeagueMemberStatistic(jsonObject.getJSONObject(LeagueMemberObjectIdentifiers.MP_STATS));
    leagueMember.setStatistic(statistic);
    
    return leagueMember;
  }
  
  
  private LeagueMember.Statistic decodeLeagueMemberStatistic(JSONObject jsonObject) throws JSONException{
    LeagueMember.Statistic statistic = new LeagueMember.Statistic();
    
    statistic.setId(jsonObject.getInt(LeagueStatisticsObjectIdentifiers.ID));
    statistic.setUserId(jsonObject.getInt(LeagueStatisticsObjectIdentifiers.USER_ID));
    statistic.setPoints(jsonObject.getInt(LeagueStatisticsObjectIdentifiers.POINTS));
    statistic.setRank(jsonObject.getInt(LeagueStatisticsObjectIdentifiers.RANK));
    statistic.setRating(jsonObject.getInt(LeagueStatisticsObjectIdentifiers.RATING));
    statistic.setRanking(jsonObject.getInt(LeagueStatisticsObjectIdentifiers.RANKING));
    statistic.setFinished(jsonObject.getInt(LeagueStatisticsObjectIdentifiers.FINISHED));
    statistic.setWon(jsonObject.getInt(LeagueStatisticsObjectIdentifiers.WON));
    statistic.setHonorable(jsonObject.getInt(LeagueStatisticsObjectIdentifiers.HONORABLE));
    
    return statistic;
  }
  
  public List<Game> getGames() throws JSONException, ClientProtocolException, IOException, ParseException {
    String responseText = performMethod(JsonMethod.GETGAMES);
    
    JSONObject jsonObject = new JSONObject(responseText);
    jsonObject = jsonObject.getJSONObject(JSON_IDENTIFIER_RESULT);
    
    JSONArray jsonMyGames = jsonObject.getJSONArray("myGames");
    JSONArray jsonOpenGames = jsonObject.getJSONArray("openGames");
    
    ArrayList<Game> games = new ArrayList<Game>(jsonMyGames.length() + jsonOpenGames.length());
    
    for(int i=0;i<jsonMyGames.length();i++){
      games.add(decodeGameData(jsonMyGames.getJSONObject(i)));
    }
    
    for(int i=0;i<jsonOpenGames.length();i++){
      games.add(decodeGameData(jsonOpenGames.getJSONObject(i)));
    }
    
    
    return games;
  }


  public List<Message> getMessages() throws JSONException, ClientProtocolException, IOException, ParseException {
  
    String responseText = performMethod(JsonMethod.GETMESSAGES, mUserId);
    
    JSONObject jsonObject = new JSONObject(responseText);
    jsonObject = jsonObject.getJSONObject(JSON_IDENTIFIER_RESULT);
    
    JSONArray messageBundle = jsonObject.getJSONArray("messageBundle");
    
    List<Message> messages = new ArrayList<Message>(messageBundle.length());
    for(int i=0;i<messageBundle.length();i++){
      messages.add(decodeMessageData(messageBundle.getJSONObject(i)));
    }
    
    return messages; 
  }
  
  public boolean sendMessage(String receiverName, String message, int lastMessageId) throws JSONException, ClientProtocolException, IOException, ParseException {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put(MessageObjectIdentifiers.USER_ID, mUserId);
    jsonObject.put(MessageObjectIdentifiers.RECEIVER_NAME, receiverName);
    jsonObject.put(MessageObjectIdentifiers.MESSAGE, message);
    jsonObject.put(MessageObjectIdentifiers.LAST_MESSAGE, lastMessageId);
    
    String responseText = performMethod(JsonMethod.SENDMESSAGE, jsonObject);
    jsonObject = new JSONObject(responseText);
    jsonObject = jsonObject.getJSONObject(JSON_IDENTIFIER_RESULT);
    return jsonObject.getBoolean(JSON_IDENTIFIER_RESULT); 
  }
  
  public void deleteMessage(int messageId) throws ClientProtocolException, JSONException, IOException{
    performMethod(JsonMethod.DELETEMESSAGE, mUserId, messageId);
  }
  
  private Message decodeMessageData(JSONObject jsonObject) throws JSONException, ParseException{
    JSONObject messageObject = jsonObject.getJSONObject("message");
    JSONObject infoObject = jsonObject.getJSONObject("info");
    
    Message message = new Message();
    
    message.setMessageId(messageObject.getInt(MessageObjectIdentifiers.ID));
    message.setAuthorId(messageObject.getInt(MessageObjectIdentifiers.AUTHOR_ID));
    message.setAuthorName(messageObject.getString(MessageObjectIdentifiers.AUTHOR_NAME));
    message.setTimestamp(DateFormat.parse(messageObject.getString(MessageObjectIdentifiers.TIMESTAMP)));
    message.setMessageText(messageObject.getString(MessageObjectIdentifiers.MESSAGE));
    message.setLastMessageId(messageObject.getInt(MessageObjectIdentifiers.LAST_MESSAGE_ID));
    message.setSystemMessage(messageObject.getBoolean(MessageObjectIdentifiers.SYSTEM_MESSAGE));
    message.setReaded(infoObject.getBoolean(MessageObjectIdentifiers.READ));
    message.setDiscarded(infoObject.getBoolean(MessageObjectIdentifiers.DISCARDED));
    message.setDeleted(infoObject.getBoolean(MessageObjectIdentifiers.DELETED));
    
    return message;
  }
  
  
  private Game decodeGameData(JSONObject jsonObject) throws JSONException, ParseException{
    String stringState = jsonObject.getString(GameObjectIdentifiers.STATE);
    String stringCreateDate = jsonObject.getString(GameObjectIdentifiers.CREATED);
    String stringUpdateDate = jsonObject.getString(GameObjectIdentifiers.UPDATED);
    
    Game game = new Game();
    game.setGameId(jsonObject.getInt(GameObjectIdentifiers.GAME_ID));
    game.setMapId(jsonObject.getInt(GameObjectIdentifiers.MAP_ID));
    game.setGameName(jsonObject.getString(GameObjectIdentifiers.NAME));
    game.setOwnerId(jsonObject.getInt(GameObjectIdentifiers.OWNER_ID));
    game.setCurrentRound(jsonObject.getInt(GameObjectIdentifiers.CURRENT_ROUND));
    game.setCurrentTurn(jsonObject.getInt(GameObjectIdentifiers.CURRENT_TURN));
    game.setNextPlayerId(jsonObject.getInt(GameObjectIdentifiers.NEXT_PLAYER_ID));
    game.setCreateDate(ServerConnection.DateFormat.parse(stringCreateDate));
    game.setUpdateDate(ServerConnection.DateFormat.parse(stringUpdateDate));
    
    JSONArray playerArray = jsonObject.getJSONArray(GameObjectIdentifiers.PLAYERS);
    decodePlayerData(game, playerArray);
    
    game.setState(decodeGameState(game, stringState));
    
    return game;
  }
  private void decodePlayerData(Game game, JSONArray playerArray) throws JSONException{
    List<Player> players = new ArrayList<Player>(playerArray.length());
    Player winner = null, activePlayer = null;
    int activePlayerId = game.getActivePlayerId(); 
    for (int i = 0; i < playerArray.length(); i++) {
      JSONObject jsonObject = playerArray.getJSONObject(i);
      
      Player player = new Player();
      player.setPlayerId(jsonObject.getInt(PlayerObjectIdentifiers.PLAYER_ID));
      int userId = jsonObject.getInt(PlayerObjectIdentifiers.USER_ID);
      
      if(userId == 0){
        continue;
      }
          
      player.setUserId(userId);
      player.setPlayerName(jsonObject.getString(PlayerObjectIdentifiers.NAME));
      player.setTeam(jsonObject.getInt(PlayerObjectIdentifiers.TEAM));
      player.setColor(jsonObject.getString(PlayerObjectIdentifiers.COLOR));
      player.setLastMessage(jsonObject.getString(PlayerObjectIdentifiers.LAST_MESSAGE));
      
      String stateString = jsonObject.getString(PlayerObjectIdentifiers.STATE);
      Player.State state = PLAYER_STATES.get(stateString);
            
      if (state == Player.State.WON) {
        winner = player;
      }
      
      if(player.getPlayerId() == activePlayerId) {
        activePlayer = player;
        if(state == Player.State.PLAYING){
          state = Player.State.ACTIVE;
        }
      } else {
        if(state == Player.State.PLAYING){
          state = Player.State.WAITING;
        }
      }
        
      
      player.setState(state);
      players.add(player);
    }
    game.setPlayers(players, winner, activePlayer);
  }
  
  private Game.State decodeGameState(Game game, String stateString) {
    Game.State state = GAME_STATES.get(stateString);

    if (state == Game.State.ENDED) {
      Player winner = game.getWinner();
      
      if (winner != null) {
        if (winner.getUserId() == mUserId) {
          state = Game.State.WON;
        } else {
          state = Game.State.LOST;
        }
      }
    } else if (state == Game.State.RUNNING) {
      Player activePlayer = game.getActivePlayer();
      if (activePlayer!= null && activePlayer.getUserId() == mUserId) {
        state = Game.State.PENDING;
      } else {
        state = Game.State.WAITING;
      }
    }
    
    return state;
  }

  private String performMethod(JsonMethod jsonMethod,Object... params) throws JSONException, IOException, ClientProtocolException {
    HttpPost httpRequest = createRequest(jsonMethod.getUri(), JsonMethod.LOGIN.toString());
    JSONObject jsonObject = createJsonObject(jsonMethod, params);

    String responseText = executeJSONRequest(httpRequest, jsonObject);
    return responseText;
  }

  private JSONObject createJsonObject(JsonMethod method, Object... arrayParameters) throws JSONException {
    JSONArray array = new JSONArray();

    for (Object arrayParameter : arrayParameters) {
      array.put(arrayParameter);
    }

    JSONObject jsonObject = new JSONObject();
    jsonObject.put(JSON_IDENTIFIER_JSON_RPC, JSON_RPC);
    jsonObject.put(JSON_IDENTIFIER_METHOD, method.toString());
    jsonObject.put(JSON_IDENTIFIER_PARAMS, array);
    jsonObject.put(JSON_IDENTIFIER_ID, method.getValue());
    return jsonObject;
  }

  private String executeJSONRequest(HttpPost httpRequest, JSONObject jsonObject) throws IOException, ClientProtocolException {
    httpRequest.setEntity(new StringEntity(jsonObject.toString(), ENCODING));
    HttpResponse response = mHttpClient.execute(httpRequest);
    Header[] headers = response.getHeaders("Set-Cookie");
    
    if(headers.length > 0){
      sessionValue = headers[0].getValue();
    }
    
    InputStream inputStream = response.getEntity().getContent();
    InputStreamReader streamReader = new InputStreamReader(inputStream);
    BufferedReader reader = new BufferedReader(streamReader);
    StringBuilder stringBuilder = new StringBuilder();
    try {
      String line = null;

      while ((line = reader.readLine()) != null) {
        stringBuilder.append(line + '\n');
      }
    } finally {
      reader.close();
      inputStream.close();
    }
    return stringBuilder.toString();
  }

  private HttpPost createRequest(String url, String jsonRpc) {
    System.out.println(url);
    HttpPost httpRequest = new HttpPost(url);
    httpRequest.addHeader("Content-Typ", "application/json; charset=utf-8");
    httpRequest.addHeader("X-JSON-RPC", jsonRpc);

    if (sessionValue != null && !sessionValue.isEmpty()) {
      httpRequest.addHeader("Cookie", sessionValue);
    }

    return httpRequest;
  }
}
