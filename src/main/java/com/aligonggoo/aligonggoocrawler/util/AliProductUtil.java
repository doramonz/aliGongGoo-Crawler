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
        if (response.toString().length() < 2000 && !response.toString().contains("302 Found")) {
            throw new RestartException();
        }

        try {
            String script = response.toString().split("\"groupItemInfo\":\\[")[1].split("],\"groupStarDTO\"")[0];
            JsonObject jsonObject = JsonParser.parseString(script).getAsJsonObject();
            String imgageUrl = "https:" + jsonObject.get("imageUrl").getAsString();
            String title = jsonObject.get("title").getAsString();
            Integer price = Integer.valueOf(jsonObject.get("localizedMinPriceInfo").getAsString().split("\\|")[1].split("\\|")[0]);
            aliProductInfo = new AliProductInfo(imgageUrl, title, price, url);
        } catch (Exception e) {
            throw new NotAvailableException();
        }
        return aliProductInfo;
    }
}

