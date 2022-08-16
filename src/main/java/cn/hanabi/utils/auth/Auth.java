package cn.hanabi.utils.auth;

import cn.hanabi.Client;
import cn.hanabi.utils.CrashUtils;
import cn.hanabi.utils.auth.client.AuthClient;
import cn.hanabi.utils.auth.utils.AES;
import cn.hanabi.utils.auth.utils.DESEncrypt;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import sun.misc.Unsafe;

import java.io.*;
import java.lang.reflect.Field;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;

public class Auth {

    public static String responsePacket;
    public static boolean verify = false;

    public static boolean auth() {
        try {
            Socket s = new Socket("127.0.0.1", 19394);
            OutputStream os = s.getOutputStream();
            PrintWriter bw = new PrintWriter(os);
            String nowTime = new SimpleDateFormat("HH-mm").format(new Date());

            String data;
            String launcherResult;
            String result;
            String userInfo;
            AES aes = new AES(16, "v0xqu%@$6sdr@%a$");
            DESEncrypt des = new DESEncrypt();
            data = des.encrypt(nowTime + "cao", aes.outKey);
            bw.write(data);
            bw.flush();
            InputStream is = s.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            result = br.readLine();
            try {
                launcherResult = aes.decryptData(result);
                if (Objects.equals(launcherResult, "hello")) {
                    data = aes.encryptData(getHWID());
                } else {
                    return false;
                }
            } catch (Exception e) {
                try {
                    Field field = Unsafe.class.getDeclaredField("theUnsafe");
                    field.setAccessible(true);
                    Unsafe unsafe = null;
                    try {
                        unsafe = (Unsafe) field.get(null);
                    } catch (IllegalAccessException i) {
                        i.printStackTrace();
                    }
                    Class<?> cacheClass = null;
                    try {
                        cacheClass = Class.forName("java.lang.Integer$IntegerCache");
                    } catch (ClassNotFoundException i) {
                        i.printStackTrace();
                    }
                    Field cache = cacheClass.getDeclaredField("cache");
                    long offset = unsafe.staticFieldOffset(cache);

                    unsafe.putObject(Integer.getInteger("SkidSense.pub NeverDie"), offset, null);

                } catch (NoSuchFieldException i) {
                    i.printStackTrace();
                }
                return false;
            }
            bw.write(data);
            bw.flush();

            result = br.readLine();
            try {
                userInfo = aes.decryptData(result);
            } catch (Exception e) {
                CrashUtils.doCrash();
                return false;
            }

            JSONObject jsonObj = new JSONObject(userInfo);
            String version = jsonObj.getString("version");
            String hwid = jsonObj.getString("hwid");
            String userName = jsonObj.getString("username");
            String passWord = jsonObj.getString("password");


            if (!Objects.equals(getHWID(), hwid))
                return false;

            AuthClient.Login(userName, passWord, hwid);

            while (!verify) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            JSONObject jsonPakcet = new JSONObject(responsePacket);
            if (jsonPakcet.getString("Type").contains("LoginResponseMessageType")) {
                if (jsonPakcet.getInt("Code") == 200) {
                    new Thread(() -> {
                        String result1;
                        try {
                            bw.write(aes.encryptData("ping"));
                            bw.flush();
                            result1 = br.readLine();
                            if (!Objects.equals(aes.decryptData(result1), "pong")) {
                                bw.close();
                                br.close();
                                CrashUtils.doCrash();
                            }
                        } catch (Exception e) {
                            CrashUtils.doCrash();
                        }
                    }).start();

                    Client.username = userName;
                    Client.rank = version.trim();
                    Client.onDebug = version.equals("admin") || version.equals("beta");
                } else return false;
            } else return false;

        } catch (
                IOException e) {
            return false;
        }

        return true;
    }


    protected static @NotNull String getOriginal() {
        try {
            String toEncrypt = "EmoManIsGay" + System.getProperty("COMPUTERNAME") + System.getenv("PROCESSOR_IDENTIFIER") + System.getenv("PROCESSOR_LEVEL");
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(toEncrypt.getBytes());
            StringBuffer hexString = new StringBuffer();

            byte byteData[] = md.digest();

            for (byte aByteData : byteData) {
                String hex = Integer.toHexString(0xff & aByteData);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error";
        }
    }

    protected static String getHWID() {
        String hwid = null;
        try {
            hwid = g(getOriginal());
        } catch (Exception ignored) {
        }
        return hwid;
    }


    private static String g(String text) throws NoSuchAlgorithmException {
        text = Base64.getUrlEncoder().encodeToString(text.getBytes());
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(text.getBytes(StandardCharsets.UTF_8), 0, text.length());
        text = DigestUtils.sha1Hex(text);
        return text.toUpperCase();
    }

}
