package multichainClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class RPCClient {

    private static final String COMMAND_LIST_STREAMS = "liststreams";
    private static final String COMMAND_GET_INFO = "getinfo";
    private static final String COMMAND_GET_STREAM_KEY_ITEMS = "liststreamkeyitems";
    private static final String COMMAND_CREATE_STREAM_KEY_ITEMS = "publish";
    private static final String COMMAND_LIST_ADDRESSES = "listaddresses";
    
    public JSONObject invokeRPC(String id, String method, Object[] params) {
        DefaultHttpClient httpclient = new DefaultHttpClient();

        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("method", method);
        if (params != null) {
        	JSONArray list = new JSONArray();
            // Print the objects in a for-loop.
            for (Object e : params) {
            	list.add(e);
            }
            
            json.put("params", list);

        }
        System.out.println(json);
        JSONObject responseJsonObj = null;
        try {
            httpclient.getCredentialsProvider().setCredentials(new AuthScope("52.24.169.158", 6799),
                    new UsernamePasswordCredentials("multichainrpc", "5Hks7oVHY6s9La6tHz2muP8ft9mrbdNEK8G2oRfJgKec"));
            StringEntity myEntity = new StringEntity(json.toJSONString());
            //System.out.println(json.toString());
            HttpPost httppost = new HttpPost("http://multichainrpc:5Hks7oVHY6s9La6tHz2muP8ft9mrbdNEK8G2oRfJgKec@52.24.169.158:6799");
            httppost.setEntity(myEntity);

            //System.out.println("executing request" + httppost.getRequestLine());
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();

            //System.out.println("----------------------------------------");
            //System.out.println(response.getStatusLine());
            if (entity != null) {
                System.out.println("Response content length: " + entity.getContentLength());
            }
            JSONParser parser = new JSONParser();
            responseJsonObj = (JSONObject) parser.parse(EntityUtils.toString(entity));
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return responseJsonObj;
    }

    public JSONObject getstreams(String chainName) {
        JSONObject json = invokeRPC(UUID.randomUUID().toString(), COMMAND_LIST_STREAMS, null);
        return json;
    }
    
    public JSONObject getaddresses(String chainName) {
        JSONObject json = invokeRPC(UUID.randomUUID().toString(), COMMAND_LIST_ADDRESSES, null);
        return json;
    }
    public JSONObject getInfo(String chainName) {
        JSONObject json = invokeRPC(UUID.randomUUID().toString(), COMMAND_GET_INFO, null);
        return (JSONObject)json.get("result");
    }
    
    public JSONObject getStreamKeyItems(String streamName, String keyName) {
        Object[] params = { streamName, keyName};
        JSONObject json = invokeRPC(UUID.randomUUID().toString(), COMMAND_GET_STREAM_KEY_ITEMS, params);
        return json;
    }
    public String getStreamKeyItemsData(String streamName, String keyName) {
    	String data = "", txid = "";
    	Object[] params = { streamName, keyName};
        JSONObject json = invokeRPC(UUID.randomUUID().toString(), COMMAND_GET_STREAM_KEY_ITEMS, params);
        JSONArray result = (JSONArray) json.get("result");
        //JSONArray lineItems = json.getJSONArray("result");
        for (Object o : result) {
            JSONObject jsonLineItem = (JSONObject) o;
            data = jsonLineItem.get("data").toString();
            System.out.println(data);
            //System.out.println(data);
            //txid = jsonLineItem.get("txid").toString();
            //System.out.println(txid);
        }
        try {
        //byte[] bytes = Hex.decodeHex(data.toCharArray());
        //return (new String(bytes, "UTF-8"));
        if (data ==null) return "error";
        else return data;
        }
        catch (Exception e) {
        e.printStackTrace();
        return "Error";
        }
    }    
    public String getLatestStreamKeyItemsData(String streamName, String keyName) {
    	String data = "", txid = "";
    	Object[] params = { streamName, keyName,"false",1,-1};
        JSONObject json = invokeRPC(UUID.randomUUID().toString(), COMMAND_GET_STREAM_KEY_ITEMS, params);
        //System.out.println(json);
        JSONArray result = (JSONArray) json.get("result");
        //System.out.println("|Result| :"+result);
        for (Object o : result) {
            JSONObject jsonLineItem = (JSONObject) o;
            data = jsonLineItem.get("data").toString();
            //System.out.println(data);
        }
        try {
        if ( result.toString().equals("[]")) return "error";
        else return data;
        //else return (new String(Hex.decodeHex(data.toCharArray())));
        }
        catch (Exception e) {
        e.printStackTrace();
        return "Error";
        }
    }

    public String createStreamKeys(String streamName, String keyName, byte[] data) {
    	String txid = "";
    	String data1 = Hex.encodeHexString(data);
    	//String data1 = DatatypeConverter.printHexBinary(data);
    	Object[] params = { streamName, keyName, data1};
    	
        JSONObject json = invokeRPC(UUID.randomUUID().toString(), COMMAND_CREATE_STREAM_KEY_ITEMS, params);
       
        //JSONArray result = (JSONArray) json.get("id");
        //System.out.println("+"+json.get("result"));        
        //System.out.println(json.get("id").toString());
        if (json.get("result") == null) return new String("Unsuccessful");
        else return json.get("result").toString();
        
//        if (result == null) { 
//        	JSONArray error = (JSONArray) result.get("error");
//            for (Object o : error) {
//                JSONObject jsonLineItem = (JSONObject) o;
//                String message = j`sonLineItem.get("message").toString();
//                System.out.println(message);
//            }        	
//        }      
//        return json.toString();
        }
    }      