
package com.pyruz.cryptography.alice.model;

import javax.crypto.spec.SecretKeySpec;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Asset {
    public static PrivateKey privateKey;
    public static PublicKey publicKey;
    public static PublicKey receivedPublicKey;
    public static byte[] sharedKey;
    public static String secretMessage;
    public static SecretKeySpec secretKey;
}
