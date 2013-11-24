package de.raptor2101.BattleWorldsKronos.Connector.JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.string;

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

  public GameListing getGameListing() throws JSONException, ClientProtocolException, IOException, ParseException {
    String responseText = performMethod(JsonMethod.GETGAMES);
    JSONObject jsonObject = new JSONObject(responseText);
    jsonObject = jsonObject.getJSONObject(JSON_IDENTIFIER_RESULT);
    
    JSONArray jsonMyGames = jsonObject.getJSONArray("myGames");
    JSONArray jsonOpenGames = jsonObject.getJSONArray("openGames");
    
    ArrayList<GameInfo> myGames = new ArrayList<GameInfo>(jsonMyGames.length());
    ArrayList<GameInfo> openGames = new ArrayList<GameInfo>(jsonOpenGames.length());
    
    for(int i=0;i<jsonMyGames.length();i++){
      myGames.add(new GameInfo(jsonMyGames.getJSONObject(i), mUserId));
    }
    Collections.sort(myGames);
    Collections.reverse(myGames);
    
    for(int i=0;i<jsonOpenGames.length();i++){
      openGames.add(new GameInfo(jsonOpenGames.getJSONObject(i), mUserId));
    }
    
    Collections.sort(openGames);
    
    
    return new GameListing(myGames, openGames);
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
