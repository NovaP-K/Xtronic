package com.novaapps.xtronic.helpingclass;

public class Encrypto {

    public Encrypto(){}

    public String Encrypt(String originalText){

        int code;
        String result = "";
        for (int i = 0; i < originalText.length(); i++) {
            code = Math.round((float) Math.random()*8+1);
            result += code + Integer.toHexString( ((int) originalText.charAt(i) ) ^ code )+"-";
        }
        return result.substring(0, result.lastIndexOf("-"));

    }

    public String Decrypt(String encryptedText){
        encryptedText = encryptedText.replace("-", "");
        String result = "";
        for (int i = 0; i < encryptedText.length(); i+=3) {
            String hex =  encryptedText.substring(i+1, i+3);
            result += (char) (Integer.parseInt(hex, 16) ^ (Integer.parseInt(String.valueOf(encryptedText.charAt(i)))));
        }
        return result;
    }

}
