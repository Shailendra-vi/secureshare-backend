package com.backend.secureshare.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.backend.secureshare.exception.CryptoException;

public class CryptoDES {
	private static final String ALGORITHM = "TripleDES";
    private static final String TRANSFORMATION = "TripleDES/CBC/PKCS5Padding";
	
    public static void encrypt(String key, File inputFile, File outputFile)
            throws CryptoException, InvalidAlgorithmParameterException {
        doCrypto(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
    }
    public static void decrypt(String key, File inputFile, File outputFile)
            throws CryptoException, InvalidAlgorithmParameterException {
        doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
    }
    
    private static void doCrypto(int cipherMode, String key, File inputFile, File outputFile) throws CryptoException, InvalidAlgorithmParameterException {
    	try {
    		IvParameterSpec ivSpec = new IvParameterSpec(key.substring(0,8).getBytes());
    		SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKeySpec, ivSpec);
             
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
