package com.example.domain;


import org.apache.commons.codec.DecoderException;
import org.apache.commons.io.IOUtils;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

public class FileEncryption {
    final int AES_Key_Size = 256;
    Cipher pkCipher, aesCipher;
    SecretKeySpec aeskeySpec;
    byte[] aesKey;

    public FileEncryption() throws GeneralSecurityException, DecoderException {
        pkCipher = Cipher.getInstance("RSA");
        aesCipher = Cipher.getInstance("AES");
    }

    public byte[] encryptImage(InputStream is) throws IOException, GeneralSecurityException{
        makeKey();
        aesCipher.init(Cipher.ENCRYPT_MODE, aeskeySpec);
        byte[] bytes = IOUtils.toByteArray(is);
        return aesCipher.doFinal(bytes);
    }

    public CipherInputStream decryptImage(InputStream is) throws IOException, GeneralSecurityException{
        aesCipher.init(Cipher.DECRYPT_MODE, aeskeySpec);
        CipherInputStream decryptedImage = new CipherInputStream(is, aesCipher);
        return  decryptedImage;
    }

    public void makeKey() throws NoSuchAlgorithmException {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        kgen.init(AES_Key_Size);
        SecretKey key = kgen.generateKey();
        aesKey = key.getEncoded();
        aeskeySpec = new SecretKeySpec(aesKey, "AES");
    }

    public byte[] encryptKey() throws IOException, GeneralSecurityException{
        File publicKeyFile = new File("/home/bcc/Documents/public.der");

        byte[] encodedKey = new byte[(int)publicKeyFile.length()];
        FileInputStream fis = new FileInputStream(publicKeyFile);
        fis.read(encodedKey);

        // create public key
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey pk = kf.generatePublic(publicKeySpec);

        // write AES key
        pkCipher.init(Cipher.ENCRYPT_MODE, pk);
        byte[] encryptedKey = pkCipher.doFinal(aesKey);

        fis.close();
        return encryptedKey;
    }

    public void decryptKey(byte[] key, InputStream inputStream) throws GeneralSecurityException, IOException {
//        File privateKeyFile = new File("/home/bcc/Documents/private.der");

        byte[] privateKeyFile = IOUtils.toByteArray(inputStream);

        // read private key to be used to decrypt the AES key
//        byte[] encodedKey = new byte[(int)privateKeyFile.length()];
//        new FileInputStream(privateKeyFile).read(encodedKey);

        // create private key
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyFile);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PrivateKey pk = kf.generatePrivate(privateKeySpec);

        // read AES key
        pkCipher.init(Cipher.DECRYPT_MODE, pk);
        aesKey = pkCipher.doFinal(key);
        aeskeySpec = new SecretKeySpec(aesKey, "AES");
    }

}
