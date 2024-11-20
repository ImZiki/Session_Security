package com.es.seguridadsession.utils;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import java.util.Base64;

@Component
public class CipherUtils {

    // PARTE PARA HASHEAR CONTRASEÑAS Y COMPROBAR SI SON IGUALES
    public String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }

    // PARTE PARA CIFRAR SIMÉTRICAMENTE UNA CADENA -> GENERAR UN TOKEN
    private static final String ALGORITHM = "AES";
    private static final String SECRET_KEY = "claveSuperSecreta"; // 16 caracteres

    public String encrypt(String nombreUsuario) throws Exception {
        // EL TOKEN A GENERAR ES nombreUsuario+clave_secreta
        String tokenSinCifrar = nombreUsuario+SECRET_KEY;

        SecretKeySpec secretKeySpec = new SecretKeySpec(Arrays.copyOf(SECRET_KEY.getBytes(),16), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] tokenCifrado = cipher.doFinal(tokenSinCifrar.getBytes());
        return Base64.getEncoder().encodeToString(tokenCifrado);
    }

    public String decrypt(String tokenCifrado) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(Arrays.copyOf(SECRET_KEY.getBytes(),16), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] tokenDescifrado = cipher.doFinal(Base64.getDecoder().decode(tokenCifrado));
        return new String(tokenDescifrado);
    }

}
