package com.backend.secureshare.logic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;

import javax.crypto.Cipher;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class CryptoDES {
	private static final String ALGORITHM = "TripleDES";
    private static final String TRANSFORMATION = "TripleDES/CBC/PKCS5Padding";
	
    public static void encrypt(String key, File inputFile, File outputFile)
            throws Exception {
        doCrypto(Cipher.ENCRYPT_MODE, key, inputFile, outputFile);
    }
    public static void decrypt(String key, File inputFile, File outputFile)
            throws Exception {
        doCrypto(Cipher.DECRYPT_MODE, key, inputFile, outputFile);
    }
    
    private static void doCrypto(int cipherMode, String key, File inputFile, File outputFile) throws Exception {
    	try {
    		IvParameterSpec ivSpec = new IvParameterSpec(key.substring(0,8).getBytes());
    		SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKeySpec, ivSpec);
             
            FileInputStream inputStream = new FileInputStream(inputFile);
            byte[] inputBytes = new byte[(int) inputFile.length()];
            inputStream.read(inputBytes);
             
            int blockSize = cipher.getBlockSize();
            int paddedLength = ((inputBytes.length + blockSize - 1) / blockSize) * blockSize;
            byte[] paddedBytes = Arrays.copyOf(inputBytes, paddedLength);
            for (int i = inputBytes.length; i < paddedBytes.length; i++) {
                paddedBytes[i] = (byte) (paddedBytes.length - inputBytes.length);
            }
            
            byte[] outputBytes = cipher.doFinal(paddedBytes);
            if (cipherMode == Cipher.DECRYPT_MODE) {
                // Remove padding bytes
                int paddingLength = outputBytes[outputBytes.length - 1];
                outputBytes = Arrays.copyOfRange(outputBytes, 0, outputBytes.length - paddingLength);
            }
            FileOutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(outputBytes);
            inputStream.close();
            outputStream.close();
             
        } catch (Exception ex) {
            throw ex;
        }
    }
    
    private static byte[] addPadding(byte[] inputBytes, int blockSize) {
        int paddingLength = blockSize - inputBytes.length % blockSize;
        byte[] paddingBytes = new byte[paddingLength];
        Arrays.fill(paddingBytes, (byte) paddingLength);
        byte[] outputBytes = new byte[inputBytes.length + paddingLength];
        System.arraycopy(inputBytes, 0, outputBytes, 0, inputBytes.length);
        System.arraycopy(paddingBytes, 0, outputBytes, inputBytes.length, paddingLength);
        return outputBytes;
    }

    private static byte[] removePadding(byte[] inputBytes) {
        int paddingLength = inputBytes[inputBytes.length - 1];
        return Arrays.copyOfRange(inputBytes, 0, inputBytes.length - paddingLength);
    }

}
