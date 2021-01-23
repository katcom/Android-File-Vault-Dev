package com.androidprivacy.demo1;

import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;

public interface Cryptography {
    byte[] encrypt(SecretKey key, byte[] fileData);

    byte[] decrypt(SecretKey key, byte[] fileData);

    CipherInputStream decryptFileOnFly(SecretKey key, String filepath,byte[] IV);
    CipherOutputStream encryptFileOnFly(SecretKey key,String filepath);
    
}
