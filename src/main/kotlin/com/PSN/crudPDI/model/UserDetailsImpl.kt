package com.PSN.crudPDI.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

/*
Esta classe implementa uma interface para fazer o vínculo com a classe model PSN4
 */
class UserDetailsImpl(private val player: PSN4) : UserDetails {

    override fun getAuthorities() = mutableListOf<GrantedAuthority>()

    override fun isEnabled() = true

    override fun getUsername() =  player.nome

    override fun isCredentialsNonExpired() = true

    override fun getPassword() = player.idtag

    override fun isAccountNonExpired() = true

    override fun isAccountNonLocked() = true
}