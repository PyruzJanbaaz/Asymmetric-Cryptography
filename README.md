# Asymmetric cryptography
There are two sides in an encrypted communication: the sender, who encrypts the data, and the recipient, who decrypts it. As the name implies, asymmetric encryption is different on each side; the sender and the recipient use two different keys. Asymmetric encryption, also known as public key encryption, uses a public key-private key pairing: data encrypted with the private key can only be decrypted with the public key, and vice versa.

I create a multi module Maven project for show how to implement asymmetric cryptography by Java and spring boot.You need the following tools and technologies to develop the same.

- Spring-Boot 2.3.7.RELEASE
- Lombok 1.18.6
- Maven 3.3.9

# How it works...
In asymmetric (public key) cryptography, both communicating parties (i.e. both Alice and Bob) have two keys of their own — just to be clear, that's four keys total. Each party has their own public key, which they share with the world, and their own private key which they ... well, which they keep private, of course but, more than that, which they keep as a closely guarded secret. The magic of public key cryptography is that a message encrypted with the public key can only be decrypted with the private key. Alice will encrypt her message with Bob's public key, and even though Eve knows she used Bob's public key, and even though Eve knows Bob's public key herself, she is unable to decrypt the message. Only Bob, using his secret key, can decrypt the message ... assuming he's kept it secret, of course.

![alt text](https://sectigostore.com/blog/wp-content/uploads/2020/04/types-of-encryption-asymmetric-encryption.png)

# Usage
Run both of the projects
- Call http://localhost:8081/api/v1/exchange for exchange the public keys between Alice and Bob
- Call http://localhost:8081/api/v1/encrypt plus a string as a parameter (plainText) for testing the encryption by Alice's private key and decryption of the encrypted text by Bob with Alice's public key.

