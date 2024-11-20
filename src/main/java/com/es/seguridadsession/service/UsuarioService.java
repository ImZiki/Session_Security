package com.es.seguridadsession.service;

import com.es.seguridadsession.dto.UsuarioDTO;
import com.es.seguridadsession.dto.UsuarioInsertDTO;
import com.es.seguridadsession.model.Session;
import com.es.seguridadsession.model.Usuario;
import com.es.seguridadsession.repository.SessionRepository;
import com.es.seguridadsession.repository.UsuarioRepository;
import com.es.seguridadsession.utils.CipherUtils;
import com.es.seguridadsession.utils.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private CipherUtils cipherUtils;
    @Autowired
    private Mapper mapper;

    public String login(UsuarioDTO userLogin) {

        // Comprobar si user y pass son correctos -> obtener de la BDD el usuario
        String nombreUser = userLogin.getNombre();
        String passUser = userLogin.getPassword();
        String passUserHashed = cipherUtils.hashPassword(passUser);

        Usuario user = usuarioRepository
                .findByNombre(nombreUser)
                .orElseThrow(()->new RuntimeException("Usuario no encontrado"));

        if(user.getNombre().equals(nombreUser) && cipherUtils.checkPassword(passUser, user.getPassword())) {
            // Si coincide -> Insertar una sesión
            // Genero un TOKEN
            String token = "";
            try {
                // GENERO UN TOKEN mediante el uso de clave simétrica
                token = cipherUtils.encrypt(user.getNombre());
                System.out.println("Token generado: "+token);
            } catch (Exception e) {
                // Lanzo excepcion propia
                throw new RuntimeException("ERROR AL ENCRIPTAR EL TOKEN");
            }

            // Almaceno la Session en la base de datos
            Session s = new Session();
            s.setToken(token);
            s.setUsuario(user);
            s.setExpirationDate(LocalDateTime.now().plusMinutes(1));

            sessionRepository.save(s);

            return token;

        } else {
            // LANZO UNA EXCEPCION PROPIA
            throw new RuntimeException("CREDENCIALES INCORRECTAS");
        }

    }

    public UsuarioDTO insert(UsuarioInsertDTO nuevoUser) {

        // Lógica de Negocio
        // a) Comprobación mínima de los campos
        if(nuevoUser.getNombre() == null || nuevoUser.getNombre().isBlank()
            || nuevoUser.getPassword1() == null || nuevoUser.getPassword1().isBlank()
            || nuevoUser.getPassword2() == null || nuevoUser.getPassword2().isBlank()
            || nuevoUser.getRol() == null || nuevoUser.getRol().isBlank()) {
            // LANZAMOS UNA EXCEPCION
        }

        // b) Comprobación de si ambas contraseñas coinciden
        if(!nuevoUser.getPassword1().equals(nuevoUser.getPassword2())) {
            // LANZAMOS UNA EXCEPCION
        }

        // c) Comprobación de si el rol es válido
        if(!nuevoUser.getRol().equalsIgnoreCase("USER")
                || !nuevoUser.getRol().equalsIgnoreCase("ADMIN")) {
            // LANZAMOS UNA EXCEPCION
        }

        // d) Hasheamos la contaseña
        String passwordEncrypted = cipherUtils.hashPassword(nuevoUser.getPassword1());
        Usuario u = new Usuario();
        u.setNombre(nuevoUser.getNombre());
        u.setPassword(passwordEncrypted);
        u.setRol(nuevoUser.getRol());

        // GUARDAMOS LA ENTIDAD USUARIO
        usuarioRepository.save(u);

        // DEVUELVO UN USUARIODTO
        return mapper.entityToDTO(u);
    }
}
