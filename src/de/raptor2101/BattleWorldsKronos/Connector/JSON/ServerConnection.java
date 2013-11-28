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
import de.raptor2101.BattleWorldsKronos.Connector.Data.Entities.Game.State;
import de.raptor2101.BattleWorldsKronos.Connector.Data.Entities.Player;

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
  
  private class GameObjectIdenifiers{
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
  private class PlayerObjectIdenifiers{
    public static final String PLAYER_ID = "playerId";
    public static final String USER_ID = "userId";
    public static final String TEAM = "team";
    public static final String NAME = "name";
    public static final String COLOR = "color";
    public static final String STATE = "state";
    public static final String LAST_MESSAGE = "last_message";
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
    // TODO: woher bekomm ich sinnvoll die werte her?
    LOGIN(1, "login", serverUrl+"/api/userservice.php"), GETGAMES(5, "getGames", serverUrl+"/api/gameservice.php");

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
  private static String GAME_VERSION = "1.0.11";

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
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    
    
    
    
    return false;
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

  private Game decodeGameData(JSONObject jsonObject) throws JSONException, ParseException{
    String stringState = jsonObject.getString(GameObjectIdenifiers.STATE);
    String stringCreateDate = jsonObject.getString(GameObjectIdenifiers.CREATED);
    String stringUpdateDate = jsonObject.getString(GameObjectIdenifiers.UPDATED);
    
    Game game = new Game();
    game.setGameId(jsonObject.getInt(GameObjectIdenifiers.GAME_ID));
    game.setMapId(jsonObject.getInt(GameObjectIdenifiers.MAP_ID));
    game.setGameName(jsonObject.getString(GameObjectIdenifiers.NAME));
    game.setOwnerId(jsonObject.getInt(GameObjectIdenifiers.OWNER_ID));
    game.setCurrentRound(jsonObject.getInt(GameObjectIdenifiers.CURRENT_ROUND));
    game.setCurrentTurn(jsonObject.getInt(GameObjectIdenifiers.CURRENT_TURN));
    game.setNextPlayerId(jsonObject.getInt(GameObjectIdenifiers.NEXT_PLAYER_ID));
    game.setCreateDate(ServerConnection.DateFormat.parse(stringCreateDate));
    game.setUpdateDate(ServerConnection.DateFormat.parse(stringUpdateDate));
    
    JSONArray playerArray = jsonObject.getJSONArray(GameObjectIdenifiers.PLAYERS);
    decodePlayerData(game, playerArray);
    
    game.setState(decodeGameState(game, stringState));
    
    return game;
  }
  private void decodePlayerData(Game game, JSONArray playerArray) throws JSONException{
    List<Player> players = new ArrayList<Player>(playerArray.length());
    for (int i = 0; i < playerArray.length(); i++) {
      JSONObject jsonObject = playerArray.getJSONObject(i);
      
      Player player = new Player();
      player.setPlayerId(jsonObject.getInt(PlayerObjectIdenifiers.PLAYER_ID));
      int userId = jsonObject.getInt(PlayerObjectIdenifiers.USER_ID);
      
      if(userId == 0){
        continue;
      }
          
      player.setUserId(userId);
      player.setPlayerName(jsonObject.getString(PlayerObjectIdenifiers.NAME));
      player.setTeam(jsonObject.getInt(PlayerObjectIdenifiers.TEAM));
      player.setColor(jsonObject.getString(PlayerObjectIdenifiers.COLOR));
      player.setLastMessage(jsonObject.getString(PlayerObjectIdenifiers.LAST_MESSAGE));
      
      String state = jsonObject.getString(PlayerObjectIdenifiers.STATE);
      player.setState(PLAYER_STATES.get(state));
      
      //if (player.getUserId() == userId) {
      //  playerInfoAssignedToUser = playerInfo;
      //}
      
      if (player.getState() == Player.State.WON) {
        game.setWinner(player);
      }
      
      players.add(player);
    }
    game.setPlayers(players);
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
    } else if (state == State.RUNNING) {
      Player nextPlayer = game.getNextPlayer();
      if (nextPlayer!= null && nextPlayer.getUserId() == mUserId) {
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
