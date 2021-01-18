package com.pyruz.cryptography.bob.controller;

import com.pyruz.cryptography.bob.model.Asset;
import com.pyruz.cryptography.bob.utility.CryptoUtilities;
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
public class BobController {

    @RequestMapping(method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public String index(HttpServletRequest request) throws Exception {
        Asset asset = new Asset();
        asset=  CryptoUtilities.getInstance().generateKeys();
        if (request.getHeader("key") != null) {
            asset.setReceivedPublicKey(CryptoUtilities.getInstance().getExchangedPublicKey(Base64.getDecoder().decode(request.getHeader("key"))));
            //==================== DES ===================\\
            CryptoUtilities.getInstance().generateCommonSecretKeyDES(asset);
            CryptoUtilities.getInstance().encryptAndSendMessage("test DES", asset);
            //==================== AES ===================\\
            CryptoUtilities.getInstance().generateCommonSecretKeyAES(asset);
            String encrypted = CryptoUtilities.getInstance().encrypt("test AES" , asset);
            System.out.println("encrypted --> " + encrypted);
            String decrypted = CryptoUtilities.getInstance().decrypt(encrypted, asset);
            System.out.println("decrypted --> " + decrypted);
        }
        return "OK";
    }

    public static String sendByGetMethod(String targetUrl, String key) throws Exception {
        String url = targetUrl;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("key", key);
        int responseCode = con.getResponseCode();
        System.out.println("Sending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        System.out.println(response.toString());
        return response.toString();
    }

}
