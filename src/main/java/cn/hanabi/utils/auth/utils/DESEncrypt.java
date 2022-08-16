package cn.hanabi.utils.auth.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;
import java.util.Base64;

public class DESEncrypt {
        private final static String IV_PARAMETER = "12345678";
        private static final String ALGORITHM = "DES";
        private static final String CIPHER_ALGORITHM = "DES/CBC/PKCS5Padding";
        private static final String CHARSET = "utf-8";

        public DESEncrypt() {}

        private static Key generateKey(String password) throws Exception {
            DESKeySpec dks = new DESKeySpec(password.getBytes(CHARSET));
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            return keyFactory.generateSecret(dks);
        }


        public String encrypt(String password, String data) {
            if (password== null || password.length() < 8) {
                throw new RuntimeException("unknown error");
            }
            if (data == null)
                return null;
            try {
                Key secretKey = generateKey(password);
                Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
                IvParameterSpec iv = new IvParameterSpec(IV_PARAMETER.getBytes(CHARSET));
                cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
                byte[] bytes = cipher.doFinal(data.getBytes(CHARSET));
                return new String(Base64.getEncoder().encode(bytes));
            } catch (Exception e) {
                e.printStackTrace();
                return data;
            }
        }

}
