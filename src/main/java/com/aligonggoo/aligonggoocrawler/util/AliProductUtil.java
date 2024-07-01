package com.aligonggoo.aligonggoocrawler.util;

import com.aligonggoo.aligonggoocrawler.dto.AliProductInfo;
import com.aligonggoo.aligonggoocrawler.exception.HTTPGETRequestFailException;
import com.aligonggoo.aligonggoocrawler.exception.NotAvailableException;
import com.aligonggoo.aligonggoocrawler.exception.RestartException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Slf4j
@RequiredArgsConstructor
@Component
public class AliProductUtil {

    public AliProductInfo getProductInfo(String url) throws Exception {
        log.info("getProductInfo url: {}", url);
        StringBuilder response = new StringBuilder();
        AliProductInfo aliProductInfo;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/337.36 (KHTML, like Gecko) Chrome/126.0.0.0 Mobile Safari/517.36");
            connection.setRequestProperty("Accept", "text/html;charset=UTF-8");
            connection.connect();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            in.close();
            connection.disconnect();
        } catch (Exception e) {
            throw new HTTPGETRequestFailException();
        }

        if (response.toString().length() < 2000) {
            throw new RestartException();
        }

        try {
            String script = response.toString().split("\"groupItemInfo\":\\[")[1].split("],\"groupStarDTO\"")[0];
            JsonObject jsonObject = JsonParser.parseString(script).getAsJsonObject();
            String imgageUrl = "https:" + jsonObject.get("imageUrl").getAsString();
            String title = jsonObject.get("title").getAsString();
            Integer price = Integer.valueOf(jsonObject.get("localizedMinPriceInfo").getAsString().split("\\|")[1].split("\\|")[0]);
            aliProductInfo = new AliProductInfo(imgageUrl, title, price);
        } catch (Exception e) {
            throw new NotAvailableException();
        }
        return aliProductInfo;
//        try {
//            s = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
//        } catch (Exception e) {
//            throw new IllegalStateException(e.getMessage());
//        }
//
//        JsonObject jsonObject;
//        try {
//            System.out.println(s.getBody());
//            String script = s.getBody().split("\"groupItemInfo\":\\[")[1].split("],\"groupStarDTO\"")[0];
//            jsonObject = JsonParser.parseString(script).getAsJsonObject();
//        } catch (Exception e) {
//            throw new IllegalStateException(e.getMessage());
//        }
//
//        String imgageUrl = "https:" + jsonObject.get("imageUrl").getAsString();
//        String title = jsonObject.get("title").getAsString();
//        Integer price = Integer.valueOf(jsonObject.get("localizedMinPriceInfo").getAsString().split("\\|")[1].split("\\|")[0]);
//        return new AliProductInfo(imgageUrl, title, price);
    }
}

//package com.doramonz.aligonggoo;
//
//import com.google.gson.JsonObject;
//import com.google.gson.JsonParser;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.client.RestTemplate;
//
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Files;
//
//public class Test {
//    public static void main(String[] args) {
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Mobile Safari/537.36");
//        headers.add("Accept", "text/html;charset=UTF-8");
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//        ResponseEntity<String> s = restTemplate.exchange("https://www.aliexpress.com/gcp/300001014/krgroupbuy-share?invitationCode=Yzg1cGl6RENMaGFZa2RIQm90RHprUHA5K3pUNDk2MTdBeEZDTFBWRDdlWUVsR3Zpc0lTQi9WT0s1MU1hdTAyWg&disableNav=YES&_immersiveMode=true&isSmbAutoCall=false&spreadType=ordershare&spreadCode=Yzg1cGl6RENMaGFZa2RIQm90RHprUHA5K3pUNDk2MTdBeEZDTFBWRDdlWUVsR3Zpc0lTQi9WT0s1MU1hdTAyWg&isSmbShow=false&shareGroupCode=SGC_6re466O565Scc3JjZA&srcSns=sns_Copy&pha_manifest=ssr&bizType=sharegroup&social_params=6000117155169&aff_fcid=81b7fdb346a2461c95ddd2aa27a41d6d-1718977305721-01167-_oDra31y&tt=MG&aff_fsk=_oDra31y&aff_platform=default&sk=_oDra31y&aff_trace_key=81b7fdb346a2461c95ddd2aa27a41d6d-1718977305721-01167-_oDra31y&shareId=6000117155169&businessType=sharegroup&platform=AE&terminal_id=f316976369734e3aba990c79a22719b1", HttpMethod.GET,entity, String.class);
//        String script = s.getBody().split("\"groupItemInfo\":\\[")[1].split("],\"groupStarDTO\"")[0];
//        JsonObject jsonObject = JsonParser.parseString(script).getAsJsonObject();
//        String imgageUrl = "https:"+jsonObject.get("imageUrl").getAsString();
//        String title = jsonObject.get("title").getAsString();
//        Integer price = Integer.valueOf(jsonObject.get("localizedMinPriceInfo").getAsString().split("\\|")[1].split("\\|")[0]);
//        System.out.println(imgageUrl);
//        System.out.println(title);
//        System.out.println(price);
//        ResponseEntity<byte[]> entity2 = restTemplate.getForEntity(imgageUrl, byte[].class);
//        try {
//            Files.write(new File("image_"+title+".jpg").toPath(), entity2.getBody());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//    }
//}
