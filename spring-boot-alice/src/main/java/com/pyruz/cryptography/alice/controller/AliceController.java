package com.pyruz.cryptography.alice.controller;

import com.pyruz.cryptography.alice.model.Asset;
import com.pyruz.cryptography.alice.utility.CryptoUtilities;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

import static org.apache.http.protocol.HTTP.USER_AGENT;

@RequestMapping("/api")
@RestController
public class AliceController {


    @GetMapping("/v1/exchange")
    public ResponseEntity<String> exchangeKeys(HttpServletRequest request) throws Exception {
        CryptoUtilities.getInstance().generateKeys();
        String aliceEncodedPublicKey = Base64.getEncoder().encodeToString(Asset.publicKey.getEncoded());
        System.out.println("Alice encodedPublicKey --> " + aliceEncodedPublicKey);
        if (request.getHeader("key") != null) {
            Asset.receivedPublicKey = (CryptoUtilities.getInstance().getExchangedPublicKey(Base64.getDecoder().decode(request.getHeader("key"))));
        } else {
            Asset.receivedPublicKey = CryptoUtilities.getInstance().getExchangedPublicKey(
                    Base64.getDecoder().decode(
                            sendToBob("http://localhost:8082/api/v1/exchange", aliceEncodedPublicKey, null)
                    )
            );
        }
        return new ResponseEntity<>(aliceEncodedPublicKey, HttpStatus.OK);
    }


    @GetMapping("/v1/encrypt")
    public ResponseEntity<String[]> encrypt(@RequestParam String plainText) throws Exception {
        //-> DES
        CryptoUtilities.getInstance().generateCommonSecretKeyDES();
        //-> AES
        CryptoUtilities.getInstance().generateCommonSecretKeyAES();
        //-> ENCRYPT
        String encrypted = CryptoUtilities.getInstance().encrypt(plainText);
        System.out.println("encrypted --> " + encrypted);
        String[] result = new String[]{
                "PlainText -> " + plainText,
                "Encrypted -> " + encrypted,
                "Decrypted -> " +sendToBob("http://localhost:8082/api/v1/decrypt", null, encrypted)
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


    public static String sendToBob(String targetUrl, String key, String text) throws Exception {
        URL obj = new URL(targetUrl);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        //-> optional default is GET
        con.setRequestMethod("GET");
        //-> add request header
        con.setRequestProperty("User-Agent", USER_AGENT);
        if (key != null)
            con.setRequestProperty("key", key);
        if (text != null)
            con.setRequestProperty("text", text);
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
