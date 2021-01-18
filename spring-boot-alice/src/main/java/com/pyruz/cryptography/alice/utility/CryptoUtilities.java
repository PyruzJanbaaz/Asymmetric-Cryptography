package com.pyruz.cryptography.alice.utility;

import com.pyruz.cryptography.alice.model.Asset;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class CryptoUtilities {

    public static CryptoUtilities getInstance() {
        return new CryptoUtilities();
    }

    public void encryptAndSendMessage(String message, Asset asset) {
        try {
            final SecretKeySpec keySpec = new SecretKeySpec(asset.getSecretKey(), "DES");
            final Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            final byte[] encryptedMessage = cipher.doFinal(message.getBytes());
            receiveAndDecryptMessage(encryptedMessage, asset);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generateCommonSecretKey(Asset asset) {
        try {
            final KeyAgreement keyAgreement = KeyAgreement.getInstance("DH");
            keyAgreement.init(asset.getPrivateKey());
            keyAgreement.doPhase(asset.getReceivedPublicKey(), true);
            asset.setSecretKey(shortenSecretKey(keyAgreement.generateSecret()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Asset generateKeys() {
        Asset asset = new Asset();
        try {
            final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DH");
            keyPairGenerator.initialize(512);
            final KeyPair keyPair = keyPairGenerator.generateKeyPair();
            asset = Asset.builder()
                    .publicKey(keyPair.getPublic())
                    .privateKey(keyPair.getPrivate())
                    .build();
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

    public void receiveAndDecryptMessage(final byte[] message, Asset asset) {
        try {
            final SecretKeySpec keySpec = new SecretKeySpec(asset.getSecretKey(), "DES");
            final Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            asset.setSecretMessage(new String(cipher.doFinal(message)));
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
}
