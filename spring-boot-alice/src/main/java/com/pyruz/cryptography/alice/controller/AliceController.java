package com.pyruz.cryptography.alice.controller;

import com.pyruz.cryptography.alice.model.Asset;
import com.pyruz.cryptography.alice.utility.CryptoUtilities;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

import static org.apache.http.protocol.HTTP.USER_AGENT;

@RequestMapping("/api/**")
@RestController
public class AliceController {

    @RequestMapping(method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public String index(HttpServletRequest request) throws Exception {
        Asset asset = new Asset();
        if (request.getHeader("key") != null) {
            asset.setReceivedPublicKey(CryptoUtilities.getInstance().getExchangedPublicKey(Base64.getDecoder().decode(request.getHeader("key"))));
            CryptoUtilities.getInstance().generateCommonSecretKey(asset);
            CryptoUtilities.getInstance().encryptAndSendMessage("test", asset);
        }
        Asset keys = CryptoUtilities.getInstance().generateKeys();
        asset.setPrivateKey(keys.getPrivateKey());
        asset.setPublicKey(keys.getPublicKey());
        String myEncodedPublicKey = Base64.getEncoder().encodeToString(asset.getPublicKey().getEncoded());
        System.out.println("myEncodedPublicKey --> " + myEncodedPublicKey);
        sendByGetMethod("http://localhost:8082/api", myEncodedPublicKey);
        return myEncodedPublicKey;
    }


    public static String sendByGetMethod(String targetUrl, String key) throws Exception {
        URL obj = new URL(targetUrl);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        //-> optional default is GET
        con.setRequestMethod("GET");
        //-> add request header
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("key", key);
        int responseCode = con.getResponseCode();
        System.out.println("Sending 'GET' request to URL : " + targetUrl);
        System.out.println("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        //-> print result
        System.out.println(response.toString());
        return response.toString();
    }


}
