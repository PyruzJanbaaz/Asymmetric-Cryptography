package com.pyruz.cryptography.bob.controller;

import com.pyruz.cryptography.bob.model.Asset;
import com.pyruz.cryptography.bob.utility.CryptoUtilities;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

import static org.apache.http.protocol.HTTP.USER_AGENT;

@RequestMapping("/api")
@RestController
public class BobController {

    @GetMapping("/v1/encrypt")
    public ResponseEntity<String[]> encrypt(@RequestParam String plainText) throws Exception {
        //-> DES
        CryptoUtilities.getInstance().generateCommonSecretKeyDES();
        //-> AES
        CryptoUtilities.getInstance().generateCommonSecretKeyAES();
        //-> ENCRYPT
        String encrypted = CryptoUtilities.getInstance().encrypt(plainText);
        String[] result = new String[]{
                "PlainText -> " + plainText,
                "Encrypted -> " + encrypted,
                "Decrypted -> " + sendToAlice("http://localhost:8081/api/v1/decrypt", null, encrypted)
        };
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @GetMapping("/v1/decrypt")
    public String decrypt(HttpServletRequest request) {
        if (request.getHeader("text") != null) {
            //-> DES
            CryptoUtilities.getInstance().generateCommonSecretKeyDES();
            //-> AES
            CryptoUtilities.getInstance().generateCommonSecretKeyAES();
            //-> DECRYPT
            String decrypted = CryptoUtilities.getInstance().decrypt(request.getHeader("text"));
            System.out.println("decrypted --> " + decrypted);
            return decrypted;
        } else {
            return "Something is wrong!";
        }
    }


    @GetMapping("/v1/exchange")
    public ResponseEntity<String> exchangeKeys(HttpServletRequest request) throws Exception {
        CryptoUtilities.getInstance().generateKeys();
        String bobEncodedPublicKey = Base64.getEncoder().encodeToString(Asset.publicKey.getEncoded());
        System.out.println("Bob encodedPublicKey --> " + bobEncodedPublicKey);
        if (request.getHeader("key") != null) {
            Asset.receivedPublicKey = (CryptoUtilities.getInstance().getExchangedPublicKey(Base64.getDecoder().decode(request.getHeader("key"))));
        } else {
            Asset.receivedPublicKey = CryptoUtilities.getInstance().getExchangedPublicKey(
                    Base64.getDecoder().decode(
                            sendToAlice("http://localhost:8081/api/v1/exchange", bobEncodedPublicKey, null)
                    )
            );
        }
        return new ResponseEntity<>(bobEncodedPublicKey, HttpStatus.OK);
    }

    public static String sendToAlice(String targetUrl, String key, String text) throws Exception {
        String url = targetUrl;
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);
        if (key != null)
            con.setRequestProperty("key", key);
        if (text != null)
            con.setRequestProperty("text", text);
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
