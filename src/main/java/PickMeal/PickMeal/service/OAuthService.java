package PickMeal.PickMeal.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.codehaus.groovy.tools.shell.IO;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class OAuthService {

    public String getKakaoAccessToken(String code) {
        String access_Token = "";
        String refresh_Token = "";
        String requestURL = "https://kauth.kakao.com/oauth/token";

        try{
            URL url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=5fb770c37c4b02604ef174950a4a1af8");
            sb.append("&redirect_uri=http://localhost:8080/login/oauth2/code/kakao");
            sb.append("&code=" + code);
            sb.append("&client_secret=PnE5EcckkJZIjoF7OMFKddW4n1O2PgoU");
            bw.write(sb.toString());
            bw.flush();

            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            System.out.println("access_token : " + access_Token);
            System.out.println("refresh_token : " + refresh_Token);

            br.close();
            bw.close();


        }catch (IOException e){
            e.printStackTrace();
        }
        return access_Token;
    }

    public void getKakaoUserInfo(String access_Token) {
        String requestURL = "https://kapi.kakao.com/v2/user/me";

        try{

            URL url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + access_Token);

            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            int getId = element.getAsJsonObject().get("id").getAsInt();

            String nickname = element.getAsJsonObject().get("kakao_account").
                    getAsJsonObject().get("profile").getAsJsonObject().get("nickname").toString();
            System.out.println("nickname : " + nickname);


            boolean hasEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();

            if(hasEmail){
                String email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
                System.out.println("email : " + email);
            }

            String id = "Kakao_" + getId;

            br.close();



        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
