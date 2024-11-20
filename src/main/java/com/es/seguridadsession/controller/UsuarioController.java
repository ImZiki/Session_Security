package com.es.seguridadsession.controller;

import com.es.seguridadsession.dto.UsuarioDTO;
import com.es.seguridadsession.dto.UsuarioInsertDTO;

import com.es.seguridadsession.service.UsuarioService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // CR
    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<UsuarioDTO> login(
            @RequestBody UsuarioDTO userLogin,
            HttpServletResponse response
    ) {

        // 1º Asegurarnos que userLogin no viene null
        if(userLogin == null || userLogin.getNombre() == null || userLogin.getPassword() == null) {
            // Lanzamos una excepcion
            throw new RuntimeException("DATOS INCORRECTOS");
        }

        // 2º Comprobar usuario y contraseña en el service y obtener token
        String token = usuarioService.login(userLogin);

        // 3º añado la cookie a la respuesta
        Cookie cookie = new Cookie("tokenSession", token);
        response.addCookie(cookie);

        return new ResponseEntity<>(userLogin, HttpStatus.OK);
    }


    // INSERT
    @PostMapping("/")
    public ResponseEntity<UsuarioDTO> insert(
            @RequestBody UsuarioInsertDTO nuevoUser
    ) {

        // Comprobación mínima en el controller
        if(nuevoUser == null) {
            // Lanzamos excepcion
        }

        // Llamamos al service para realizar la inserción
        UsuarioDTO usuarioDTO = usuarioService.insert(nuevoUser);

        return new ResponseEntity<>(usuarioDTO, HttpStatus.OK);


    }

}
