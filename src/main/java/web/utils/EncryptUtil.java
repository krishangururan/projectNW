package web.utils;

import org.jasypt.util.text.AES256TextEncryptor;

import java.util.Base64;

public class EncryptUtil {
    public static void main(String[] args){
        String base64Pass=base64StringConvert("mypassword");
        String en=Encrypt("",base64Pass);
        System.out.println(en);
        String dec2 = Decrypt("DUnfhjhjkhhlk3ljlkjljk23",base64Pass);
        System.out.println(dec2);

        String dec = Decrypt(en);
        System.out.println(dec);
    }
    public static String Encrypt(String password){
        try{
            AES256TextEncryptor aesEncryptor = new AES256TextEncryptor();
            aesEncryptor.setPassword("myPassword");
            String myEncryptedPassword = aesEncryptor.encrypt(password);
            System.out.println(myEncryptedPassword);
            return myEncryptedPassword;
        }
        catch (Exception e){
            System.out.println("Invalid unlock key");
            return null;
        }
    }
    public static String base64StringConvert(String value){
        return Base64.getEncoder().encodeToString((value).getBytes());
    }
    public static String Encrypt(String password,String encodedKey){
        try{
            byte[] decodedBytes = Base64.getDecoder().decode(encodedKey);
            String decodedString = new String(decodedBytes);
            System.out.println(decodedString);
            AES256TextEncryptor aesEncryptor = new AES256TextEncryptor();
            aesEncryptor.setPassword(decodedString);
            String myEncryptedPassword = aesEncryptor.encrypt(password);
            System.out.println(myEncryptedPassword);
            return myEncryptedPassword;
        }
        catch (Exception e){
            System.out.println("Invalid unlock key");
            return null;
        }
    }
    public static String Decrypt(String passwordFromConfigFile){
        try{
            AES256TextEncryptor aesEncryptor = new AES256TextEncryptor();
            aesEncryptor.setPassword(passwordFromConfigFile);
            return aesEncryptor.decrypt(passwordFromConfigFile);
        }
        catch (Exception e){
            System.out.println("Invalid unlock key");
            return null;
        }
    }
    public static String Decrypt(String passwordFromConfigFile,String encodedKey){
        try{
            byte[] decodedBytes = Base64.getDecoder().decode(encodedKey);
            String decodedString = new String(decodedBytes);
            System.out.println(decodedString);
            AES256TextEncryptor aesEncryptor = new AES256TextEncryptor();
            aesEncryptor.setPassword(passwordFromConfigFile);
            return aesEncryptor.decrypt(passwordFromConfigFile);
        }
        catch (Exception e){
            System.out.println("Invalid unlock key");
            return null;
        }
    }
}
