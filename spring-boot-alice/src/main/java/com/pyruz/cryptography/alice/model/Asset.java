
package com.pyruz.cryptography.alice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Asset {
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private PublicKey receivedPublicKey;
    private byte[] secretKey;
    private String secretMessage;
}
