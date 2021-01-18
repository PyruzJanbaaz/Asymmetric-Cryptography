
package com.pyruz.cryptography.bob.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.crypto.spec.SecretKeySpec;
import java.security.*;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Asset {
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private PublicKey receivedPublicKey;
    private byte[] sharedKey;
    private String secretMessage;
    private SecretKeySpec secretKey;
}
