package org.dragonet.common.utilities;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.UUID;

public class HybridAuth {

    public static String url;
    public static String key;

    public static void setUrl(String url) {
        HybridAuth.url = url;
    }

    public static void setKey(String key) {
        HybridAuth.key = key;
    }

    private static boolean hasError(JSONObject value) {
        if(value.has("error")) {
            System.out.println("HybridAuth Error: " + value.getString("error"));
            return true;
        }
        return false;
    }

    public static JSONObject getMappingForBedrock(String xuid) {
        try {
            String total_url = url + "/api/mapping/get/bedrockXUID/" + URLEncoder.encode(xuid, "UTF-8") + "?key=" + key;;
            String data = HTTP.performGetRequest(total_url);
            if(data == null) {
                System.out.println("HybridAuth: get mapping for MCJE failed! ");
                return null;
            }
            JSONObject j = new JSONObject(data);
            if(hasError(j)) return null;
            return j;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject getMappingForJava(UUID originalUUID) {
        try {
            String total_url = url + "/api/mapping/get/javaUUID/" + URLEncoder.encode(originalUUID.toString(), "UTF-8") + "?key=" + key;;
            String data = HTTP.performGetRequest(total_url);
            if(data == null) {
                System.out.println("HybridAuth: get mapping for MCJE failed! ");
                return null;
            }
            JSONObject j = new JSONObject(data);
            if(hasError(j)) return null;
            return j;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * generate a key for MCJE players to merge with their account
     * @param originalUUID
     * @return
     */
    public static String generateMergeKeyForBedrock(UUID originalUUID) {
        try {
            String total_url = url + "/api/key/generate/javaUUID/" + URLEncoder.encode(originalUUID.toString(), "UTF-8") + "?key=" + key;;
            String data = HTTP.performGetRequest(total_url);
            if(data == null) {
                System.out.println("HybridAuth: get mapping for MCJE failed! ");
                return null;
            }
            JSONObject j = new JSONObject(data);
            if(hasError(j)) return null;
            return j.getString("key");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * generate a key for MCBE players tp merge with their MCJE account
     * @param xuid
     * @return
     */
    public static String generateMergeKeyForJava(String xuid) {
        try {
            String total_url = url + "/api/key/generate/bedrockXUID/" + URLEncoder.encode(xuid, "UTF-8") + "?key=" + key;;
            String data = HTTP.performGetRequest(total_url);
            if(data == null) {
                System.out.println("HybridAuth: get mapping for MCJE failed! ");
                return null;
            }
            JSONObject j = new JSONObject(data);
            if(hasError(j)) return null;
            return j.getString("key");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
