package com.androidprivacy.demo1;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.SecretKey;

public class FileManager implements Cryptography {
    private static final String TAG = "Demo1.fileManager";
    private List<MyFile> mEncryptedFiles;
    private List<MyFile> mDecryptedFiles;
    private Context mContext;
    private static FileManager mFileManager;

    private static String encryptFolder = "EncryptedFolder";
    private static String decryptFolder = "DecryptedFolder";

    private FileManager(Context context){
        mContext = context;
        mEncryptedFiles = new ArrayList<>();
        mDecryptedFiles = new ArrayList<>();

        String[] files = {};
        checkAndCreateFolder(mContext.getFilesDir()+"/"+encryptFolder);
        checkAndCreateFolder(mContext.getFilesDir()+"/"+decryptFolder);
        update();
    };

    public static FileManager getInstance(Context context){
        if(mFileManager == null){
            mFileManager = new FileManager(context);
        }
        return mFileManager;
    }

    public int getCount(){
        return mEncryptedFiles.size();
    }

    public void update(){
        String[] files = mContext.getFilesDir().list();
        File encryptedDir = new File(mContext.getFilesDir()+"/"+encryptFolder);
        files = encryptedDir.list();
        mEncryptedFiles = new ArrayList<>();

        for(String filename : files){
            mEncryptedFiles.add(new MyFile(filename, mContext.getFilesDir()+"/" + encryptFolder+"/"+ filename));
        }

        mDecryptedFiles = new ArrayList<>();
        File decryptedDir = new File(mContext.getFilesDir()+"/"+decryptFolder);
        files = decryptedDir.list();

        for(String filename : files){
            mDecryptedFiles.add(new MyFile(filename, mContext.getFilesDir()+"/" + decryptFolder+"/"+ filename));
        }
    }


    public List<MyFile> getEncryptedFiles(){
        return mEncryptedFiles;
    }

    public List<MyFile> getDecryptedFiles(){
        return mDecryptedFiles;
    }


    public void saveDecryptedFile(String filename,byte[] data){
        checkAndCreateFolder(mContext.getFilesDir() +"/" + decryptFolder+"/");

        String location = mContext.getFilesDir() +"/" + decryptFolder+"/"+filename;

        saveAndOverwriteFile(location,data);
    }

    public void saveEncryptedFile(String filename,byte[] data){
        checkAndCreateFolder(mContext.getFilesDir() +"/" + encryptFolder+"/");

        String filepath = mContext.getFilesDir() +"/"+ encryptFolder+"/" + filename;
        saveAndOverwriteFile(filepath,data);
    }

    private void checkAndCreateFolder(String directory) {
        File dir = new File(directory);
        if(!dir.exists()){
            dir.mkdirs();
        }
    }

    /**
     * Encrypt file from Inputstream with the given alias,
     * And save it to the private storage
     * @param keyAlias
     * @param in
     * @param filename
     */
    public void encryptAndSaveInPrivateStorage(String keyAlias, InputStream in, String filename){
        byte[] data= encryptFromInputStream(keyAlias,in);

        mFileManager.saveEncryptedFile(filename,data);
    }

    /**
     * Encrypt file from inputstream with the given key alias
     * @param keyAlias
     * @param in
     * @return
     */
    private byte[] encryptFromInputStream(String keyAlias, InputStream in){
        byte[] data = readDataFromInputStream(in);

        SecretKey key = getSecretKey(keyAlias);
        byte[] encryptedFile = encrypt(key,data);

        return encryptedFile;
    }

    /**
     * Decrypt file with given key alias and the file path
     * @param keyAlias
     * @param filepath
     * @return
     */
    private byte[] decryptFile(String keyAlias, String filepath){
        byte[] data = readDataFromFile(filepath);
        SecretKey key = getSecretKey(keyAlias);
        return decrypt(key,data);
    }

    /**
     * Decrypt file and save the decrypted to the destiny location
     * @param keyAlias
     * @param sourcePath
     */
    public void decryptAndSaveInPrivateStorage(String keyAlias,String filename, String sourcePath){
        byte data[] = decryptFile(keyAlias,sourcePath);
        mFileManager.saveDecryptedFile(filename,data);
    }

    /**
     * Read the data from InputStream as a byte array
     * @param in
     * @return
     */
    private byte[] readDataFromInputStream(InputStream in){
        byte[] data = null;
        try {
            int fileSize = in.available();
            data = new byte[fileSize];
            in.read(data);
        } catch (IOException e) {
            Log.e(TAG,e.toString());
        }
        return data;
    }

    /**
     * Read a file as byte array given the file's location
     * @param filepath
     * @return
     */
    private byte[] readDataFromFile(String filepath) {
        File file = new File(filepath);
        byte[] data = null;
        try{
            FileInputStream in = new FileInputStream(file);

            data = new byte[in.available()];
            in.read(data);

            in.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG,e.toString());
        } catch (IOException e) {
            Log.e(TAG,e.toString());
        }
        return data;
    }
    /**
     * Write the data to a given location
     * @param data
     */
    private void saveAndOverwriteFile(String filepath, byte[] data) {
        File file = new File(filepath);
        try {
            if(file.exists()){
                file.createNewFile();
            }
            FileOutputStream out= new FileOutputStream(file);

            out.write(data);
            out.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG,e.toString());
        } catch (IOException ioe){
            Log.e(TAG,ioe.toString());
        }

    }

    /**
     * Generator a private key in keystore with the alias "MyKey" represented by the variable keyAlias
     */
    protected void generatorKey(){
        // TODO (Dan)
    }

    /**
     * Get the SecretKey object associated with the alias
     * @param keyAlias
     * @return
     */
    private SecretKey getSecretKey(String keyAlias) {
        // TODO (Dan)
        return null;
    }

    /**
     * Encrypt data with given secret key
     * @param key
     * @param fileData
     * @return
     */
    @Override
    public byte[] encrypt(SecretKey key, byte[] fileData) {
        // TODO (Dan)

        // Returning the original data is just placeholder.
        // You should return the encrypted data when you finish the code instead.
        return fileData;
    }

    /**
     * Decrypt data with the given secret key
     * @param key
     * @param fileData
     * @return
     */
    @Override
    public byte[] decrypt(SecretKey key, byte[] fileData) {
        // TODO (Dan)

        // Returning the original data is just placeholder.
        // You should return the decrypted data when you finish the code.
        return fileData;
    }
}
