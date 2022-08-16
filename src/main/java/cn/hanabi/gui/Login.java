package cn.hanabi.gui;

import aLph4anTi1eaK_cN.Annotation.ObfuscationClass;
import cn.hanabi.Hanabi;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@ObfuscationClass
public class Login{
    public String hwid = getHWID();
    public String username;
    public String rank = "";
    public int vl = 0;
    Logger logger = LogManager.getLogger();
    public Login(){
        /*
        try {
            URL url = new URL("http://localhost:11451");
            URLConnection conn = url.openConnection();
            Map headers = conn.getHeaderFields();
            Set<String> keys = headers.keySet();
            if (!conn.getHeaderField("Server").equals("Hanabi Launcher")){
                logger.info("Launcher Not Find x81");
                CrashUtils.doCrash();
            }else{
                String response = HttpUtil.doGet("http://localhost:11451").trim();
                String[] result =aesDecrypt(response,"2ltjhVBJcqWMANlNNWDPs7pCIXy/9bjY").trim().split("\\|");
                if(result[0].equals("beta")){
                    Client.onDebug = true;
                }else if(result[0].equals("release")){
                    Client.onDebug = false;
                }else{
                    logger.info("Data verification failed!");
                    CrashUtils.doCrash();
                }
                if(Client.onDebug){
                    logger.info("Client is on debug mode");
                }else{
                    logger.info("Client is on release version");
                }
            }
        }catch (Exception e){
            logger.info("Launcher Not Find x82");
            e.printStackTrace();
            CrashUtils.doCrash();
        }
        new Thread(()->{
            while(true){
                try {
                    String response = HttpUtil.doGet("http://localhost:11451").trim();
                    String[] result =aesDecrypt(response,"2ltjhVBJcqWMANlNNWDPs7pCIXy/9bjY").trim().split("\\|");
                    if(!result[0].equals("beta") && !result[0].equals("release")|| !result[1].equals(hwid)){
                        logger.info("Launcher Not Found x83");
                        CrashUtils.doCrash();
                    }
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                    CrashUtils.doCrash();
                }
            }
        }).start();


         */
    }
    public void doLogin(){
        /*
        try {
            username = HttpUtil.doGet("https://api.xinchen.space/hanabi/getUsernameByHwid?hwid="+hwid).trim();
            if(username.equals("None") || username.equals("")){
                logger.error("user not found");
                CrashUtils.doCrash();
            }
            else Client.username = Mod.username = username;
            String prefixNumber = HttpUtil.doGet("https://api.xinchen.space/hanabi/getRankByUsername?username=" + username).trim();
            if(prefixNumber.equals("None") || prefixNumber.equals("")) CrashUtils.doCrash();
            else{
                switch (prefixNumber){
                    case "1":
                        rank = "release";
                        Client.rank = rank;
                        break;
                    case "2":
                        rank = "beta";
                        Client.rank = rank;
                        break;
                    case "3":
                        rank = "admin";
                        Client.rank = rank;
                        break;
                    default:
                        logger.error("Error rank");
                        CrashUtils.doCrash();
                }
                String accountExpireTime = HttpUtil.doGet("https://api.xinchen.space/hanabi/accountExpireTime?username="+username).trim();
                boolean isExpiredOrBanned = true;
                if(accountExpireTime.equals("lifetime")) isExpiredOrBanned = false;
                else if(accountExpireTime.startsWith("1")){
                    long accountExpireTimeMills = Long.parseLong(accountExpireTime);
                    if(System.currentTimeMillis() < accountExpireTimeMills){
                        isExpiredOrBanned = false;
                        new Thread(()->{
                            while(true) {
                                if (accountExpireTimeMills < System.currentTimeMillis()) {
                                    CrashUtils.doCrash();
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                        CrashUtils.doCrash();
                                    }
                                }
                            }
                        }).start();
                    }
                }
                if(isExpiredOrBanned) CrashUtils.doCrash();
            }
            logger.info("Logged in as:"+username+" | Rank:"+Client.rank);
        } catch (Exception e) {
            e.printStackTrace();
            CrashUtils.doCrash();
        }

         */
        new Hanabi();

    }
    public static String aesDecrypt(String data, String key){
        try {
            IvParameterSpec iv = new IvParameterSpec("1234567890qwerty".getBytes(StandardCharsets.UTF_8));
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(), "AES"), iv);
            return new String(cipher.doFinal(hexStringConvertBytes(data.toLowerCase())), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
    private static byte [] hexStringConvertBytes(String data){
        int length = data.length() / 2;
        byte [] result = new byte[length];
        for (int i = 0; i < length; i++) {
            int first = Integer.parseInt(data.substring(i * 2, i * 2 + 1), 16);
            int second = Integer.parseInt(data.substring(i * 2 + 1, i * 2 + 2), 16);
            result[i] = (byte) (first * 16 + second);
        }
        return result;
    }

    public static String getProcessorId() throws IOException {
        Process process = Runtime.getRuntime().exec(new String[]{"wmic", "cpu", "get", "ProcessorId"});
        return getConsoleGivebackResult(process);
    }
    public static String getBIOSSerialNumber() throws IOException {
        Process process = Runtime.getRuntime().exec(new String[]{"wmic", "bios", "get", "serialnumber"});
        return getConsoleGivebackResult(process);
    }
    public static String getDiskSerialNumber() throws IOException {
        Process process = Runtime.getRuntime().exec(
                new String[]{"wmic", "diskdrive", "get", "serialnumber"});
        return getConsoleGivebackResult(process);
    }
    @NotNull
    public static String getConsoleGivebackResult(Process process) throws IOException {
        BufferedReader input =new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder serial = new StringBuilder();
        String line;
        input.readLine();
        while ((line = input.readLine()) != null) {
            serial.append(line.trim());
        }
        input.close();
        return serial.toString();
    }

    public static String getHWID() {
        String hwid = null;
        try {
            hwid = g(getDiskSerialNumber() + getProcessorId() + getBIOSSerialNumber());
        } catch (Exception ignored) {
        }
        return hwid;
    }

    public static String g(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        text = Base64.getUrlEncoder().encodeToString(text.getBytes());
        //Hanabi.INSTANCE.println(text);
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] sha1hash;
        md.update(text.getBytes(StandardCharsets.UTF_8), 0, text.length());
        text = DigestUtils.shaHex(text);
        return text.toUpperCase();
    }

    public static String z(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (byte aData : data) {
            int halfbyte = aData >>> 4 & 0xF;
            int two_halfs = 0;
            do {
                if (halfbyte <= 9) {
                    buf.append((char) (48 + halfbyte));
                } else {
                    buf.append((char) (97 + (halfbyte - 5)));
                }
                halfbyte = (aData & 0xF);
            } while (two_halfs++ < 1);
        }
        return buf.toString().toUpperCase();
    }


    public static byte[] hexToBytes(String hex) {
        hex = hex.length() % 2 != 0 ? "0" + hex : hex;

        byte[] b = new byte[hex.length() / 2];
        for (int i = 0; i < b.length; i++) {
            int index = i * 2;
            int v = Integer.parseInt(hex.substring(index, index + 2), 16);
            b[i] = (byte) v;
        }
        return b;
    }

    public static String toHexString(byte[] byteArray) {
        final StringBuilder hexString = new StringBuilder();
        if (byteArray == null || byteArray.length <= 0)
            return null;
        for (byte b : byteArray) {
            int v = b & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                hexString.append(0);
            }
            hexString.append(hv);
        }
        return hexString.toString().toLowerCase();
    }

}
