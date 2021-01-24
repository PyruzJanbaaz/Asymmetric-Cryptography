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

    public void encryptAndSendMessage(final String message) {
        try {
            final SecretKeySpec keySpec = new SecretKeySpec(Asset.sharedKey, "DES");
            final Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            final byte[] encryptedMessage = cipher.doFinal(message.getBytes());
            System.out.println("encrypted message --> " + Base64.getEncoder().encodeToString(encryptedMessage));
            CryptoUtilities.getInstance().receiveAndDecryptMessage(encryptedMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void generateCommonSecretKeyDES() {
        try {
            final KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
            keyAgreement.init(Asset.privateKey);
            keyAgreement.doPhase(Asset.receivedPublicKey, true);
            Asset.sharedKey = (shortenSecretKey(keyAgreement.generateSecret()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generateCommonSecretKeyAES() {
        try {
            final KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
            keyAgreement.init(Asset.privateKey);
            keyAgreement.doPhase(Asset.receivedPublicKey, true);
            Asset.sharedKey = (keyAgreement.generateSecret());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generateKeys() {
        try {
            final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
            keyPairGenerator.initialize(512);
            final KeyPair keyPair = keyPairGenerator.generateKeyPair();
            Asset.privateKey = (keyPair.getPrivate());
            Asset.publicKey = (keyPair.getPublic());
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void receiveAndDecryptMessage(final byte[] message) {
        try {
            final SecretKeySpec keySpec = new SecretKeySpec(Asset.sharedKey, "DES");
            final Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            Asset.secretMessage = (new String(cipher.doFinal(message)));
            System.out.println("decrypted message --> " + Asset.secretMessage);
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

    public void setKey(byte[] key) {
        MessageDigest sha = null;
        try {
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            Asset.secretKey = (new SecretKeySpec(key, "AES"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public String encrypt(String strToEncrypt) {
        try {
            setKey(Asset.sharedKey);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, Asset.secretKey);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes("UTF-8")));
        } catch (Exception e) {
            System.out.println("Error while encrypting: " + e.toString());
        }
        return null;
    }

    public String decrypt(String strToDecrypt) {
        try {
            setKey(Asset.sharedKey);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, Asset.secretKey);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
            System.out.println("Error while decrypting: " + e.toString());
        }
        return null;
    }

}
