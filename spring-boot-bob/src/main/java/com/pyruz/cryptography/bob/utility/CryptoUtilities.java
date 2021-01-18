package com.pyruz.cryptography.bob.utility;


import com.pyruz.cryptography.bob.model.Asset;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

public class CryptoUtilities {

    public static CryptoUtilities getInstance() {
        return new CryptoUtilities();
    }

    public void encryptAndSendMessage(final String message, Asset asset) {
        try {
            final SecretKeySpec keySpec = new SecretKeySpec(asset.getSharedKey(), "DES");
            final Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            final byte[] encryptedMessage = cipher.doFinal(message.getBytes());
            System.out.println("encrypted message --> " + Base64.getEncoder().encodeToString(encryptedMessage));
            CryptoUtilities.getInstance().receiveAndDecryptMessage(encryptedMessage, asset);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Asset generateCommonSecretKeyDES(Asset asset) {
        try {
            final KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
            keyAgreement.init(asset.getPrivateKey());
            keyAgreement.doPhase(asset.getReceivedPublicKey(), true);
            asset.setSharedKey(shortenSecretKey(keyAgreement.generateSecret()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return asset;
    }

    public Asset generateCommonSecretKeyAES(Asset asset) {
        try {
            final KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
            keyAgreement.init(asset.getPrivateKey());
            keyAgreement.doPhase(asset.getReceivedPublicKey(), true);
            asset.setSharedKey(keyAgreement.generateSecret());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return asset;
    }

    public Asset generateKeys() {
        Asset asset = new Asset();
        try {
            final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
            keyPairGenerator.initialize(512);
            final KeyPair keyPair = keyPairGenerator.generateKeyPair();
            asset.setPrivateKey(keyPair.getPrivate());
            asset.setPublicKey(keyPair.getPublic());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return asset;
    }

    public PublicKey getExchangedPublicKey(byte[] publicKeyBytes) throws InvalidKeySpecException, NoSuchAlgorithmException {
        KeyFactory keyFactory = KeyFactory.getInstance("DH");
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        return keyFactory.generatePublic(publicKeySpec);
    }

    public PrivateKey getExchangedPrivateKey(byte[] privateKeyBytes) throws InvalidKeySpecException, NoSuchAlgorithmException {
        KeyFactory keyFactory = KeyFactory.getInstance("DH");
        EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        return keyFactory.generatePrivate(privateKeySpec);
    }

    public void receiveAndDecryptMessage(final byte[] message, Asset asset) {
        try {
            final SecretKeySpec keySpec = new SecretKeySpec(asset.getSharedKey(), "DES");
            final Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            asset.setSecretMessage(new String(cipher.doFinal(message)));
            System.out.println("decrypted message --> " + asset.getSecretMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private byte[] shortenSecretKey(final byte[] longKey) {

        try {
            final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            final DESKeySpec desSpec = new DESKeySpec(longKey);
            return keyFactory.generateSecret(desSpec).getEncoded();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setKey(byte[] key, Asset asset) {
        MessageDigest sha = null;
        try {
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            asset.setSecretKey(new SecretKeySpec(key, "AES"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public String encrypt(String strToEncrypt, Asset asset) {
        try {
            setKey(asset.getSharedKey(), asset);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, asset.getSecretKey());
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public String decrypt(String strToDecrypt, Asset asset) {
        try {
            setKey(asset.getSharedKey(), asset);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, asset.getSecretKey());
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }

}
