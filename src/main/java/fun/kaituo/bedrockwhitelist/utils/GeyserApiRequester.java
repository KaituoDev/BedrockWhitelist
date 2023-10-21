package fun.kaituo.bedrockwhitelist.utils;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GeyserApiRequester {
    private static final String API_URL = "https://api.geysermc.org/v2/xbox/xuid/";

    public static Long getXuid(String gamerTag) throws IOException, ParseException {
        String responseString = getApiResponse(gamerTag);
        return getXuidFromJson(responseString);
    }

    private static String getApiResponse(String gamerTag) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(API_URL + gamerTag);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        return new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
    }

    private static long getXuidFromJson(String jsonString) throws ParseException {
        JSONObject object = (JSONObject) new JSONParser().parse(jsonString);
        return (long) object.get("xuid");
    }
}
