package com.es.seguridadsession.utils;

import com.es.seguridadsession.dto.UsuarioDTO;
import com.es.seguridadsession.dto.UsuarioInsertDTO;
import com.es.seguridadsession.model.Usuario;
import jakarta.persistence.Column;
import org.springframework.stereotype.Component;

@Component
public class Mapper {

    public Usuario dtoToEntity(UsuarioInsertDTO usuarioDTO) {
        return new Usuario(
                usuarioDTO.getNombre(),
                usuarioDTO.getPassword1(),
                usuarioDTO.getRol());
    }

    public UsuarioDTO entityToDTO(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getNombre(),
                usuario.getPassword()
        );
    }



}
