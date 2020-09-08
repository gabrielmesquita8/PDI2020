package com.PSN.crudPDI.service

import com.PSN.crudPDI.model.UserDetailsImpl
import com.PSN.crudPDI.repository.PSRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

/*
Esta classe serve para criar uma instância concreta de UserDetailsService.
Desta forma é necessário sobreescrever o método LoadByUserName que retorna um UserDetails.
UserDetails é responsável por guardar informações de um usuário no spring.
 */
@Service
class UserDetailsServiceImpl : UserDetailsService {

    @Autowired
    private lateinit var userRepository: PSRepository


    override fun loadUserByUsername(nome: String?): UserDetails {
        val user = userRepository.findByNome(nome) ?: throw UsernameNotFoundException(nome)

        return UserDetailsImpl(user)
    }
}