package com.backend.secureshare.logic;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.backend.secureshare.exception.CryptoException;
 
public class CryptoAES {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5PADDING";
 
    public static void encrypt(String key,String salt, File inputFile, File outputFile)
            throws CryptoException, InvalidKeySpecException, InvalidAlgorithmParameterException {
        doCrypto(Cipher.ENCRYPT_MODE, key, salt, inputFile, outputFile);
    }
 
    public static void decrypt(String key, String salt, File inputFile, File outputFile)
            throws CryptoException, InvalidKeySpecException, InvalidAlgorithmParameterException {
        doCrypto(Cipher.DECRYPT_MODE, key, salt, inputFile, outputFile);
    }
 
    private static void doCrypto(int cipherMode, String key,String salt, File inputFile,
            File outputFile) throws CryptoException, InvalidKeySpecException, InvalidAlgorithmParameterException {
        try {
        	byte[] iv = new byte[16];
        	IvParameterSpec ivSpec = new IvParameterSpec(iv);
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes("UTF-8"), ALGORITHM);
            
//            new SecureRandom().nextBytes(iv);
//            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKey, ivSpec);
             
            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);
             
            byte[] outputBytes = cipher.doFinal(inputBytes);
             
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);
            inputStream.close();
            outputStream.close();
             
        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                | InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException | IOException ex) {
            throw new CryptoException("Error encrypting/decrypting file", ex);
        }
    }
}

