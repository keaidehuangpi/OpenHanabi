package cn.hanabi.utils.bypass;

import cn.hanabi.Hanabi;
import cn.hanabi.utils.fileSystem.Base58;
import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

public class AESUtil {
    public static void main(String[] args) {
        AESUtil aesUtil = new AESUtil(1);
        String s = aesUtil.AESEncode("1");
        Hanabi.INSTANCE.println(s);
    }
    private final String KEY;

    public AESUtil(final int key) {
        this.KEY = this.genKey(key);
    }

    private String genKey(final int a) {
        String base = Base64.getEncoder().encodeToString("114514".getBytes(StandardCharsets.UTF_8)); // this will cause a crash at no internet connection

        final long hour = Long.parseLong(new String(Base64.getDecoder().decode(base)));
        final Random random = new Random(hour * 2L);
        for (int i = a; i != 0; --i) {
            random.nextInt(63667);
        }
        final String key = DigestUtils.md5Hex(String.valueOf(random.nextInt(18833)));
        return DigestUtils.md5Hex(Base64.getEncoder().encodeToString(key.getBytes()).substring(a / 4, a / 2));
    }

    public String AESEncode(final String content) {
        try {
            final KeyGenerator keygen = KeyGenerator.getInstance("AES");
            final SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(this.KEY.getBytes());
            keygen.init(128, random);
            final SecretKey original_key = keygen.generateKey();
            final byte[] raw = original_key.getEncoded();
            final SecretKey key = new SecretKeySpec(raw, "AES");
            final Cipher cipher = Cipher.getInstance("AES");
            cipher.init(1, key);
            final byte[] byte_encode = content.getBytes(StandardCharsets.UTF_8);
            final byte[] byte_AES = cipher.doFinal(byte_encode);
            return new String(Base64.getEncoder().encode(byte_AES));
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public String Encode(final String content) {
        try {
            final KeyGenerator keygen = KeyGenerator.getInstance("AES");
            final SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(this.KEY.getBytes());
            keygen.init(128, random);
            final SecretKey original_key = keygen.generateKey();
            final byte[] raw = original_key.getEncoded();
            final SecretKey key = new SecretKeySpec(raw, "AES");
            final Cipher cipher = Cipher.getInstance("AES");
            cipher.init(1, key);
            final byte[] byte_encode = content.getBytes(StandardCharsets.ISO_8859_1);
            final byte[] byte_AES = cipher.doFinal(byte_encode);
            return new Base58(23241).encode(byte_AES);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
