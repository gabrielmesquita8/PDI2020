package com.PSN.crudPDI.service

import com.PSN.crudPDI.Constants
import com.PSN.crudPDI.model.PSN4
import com.PSN.crudPDI.repository.PSRepository
import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.JwtBuilder
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.mindrot.jbcrypt.BCrypt
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import java.util.*
import javax.persistence.EntityNotFoundException


@Service
class PService (
        @Autowired
        private val psRepository: PSRepository
){

    fun getAllPlayers(): MutableIterable<PSN4>
    {
        return psRepository.findAll()
    }

    fun getPlayerById(id: Long) : PSN4
    {
        return psRepository.findById(id).orElseThrow {
            EntityNotFoundException()
        }
    }

    @Transactional
    fun createNewPlayer(player: PSN4 ) : PSN4
    {
        if(player.nome.trim().isEmpty() || player.idtag.trim().isEmpty())
        {
            throw Exception()
        }

        return PSN4(
                nome = BCrypt.hashpw(player.nome, BCrypt.gensalt(10)),
                genero = player.genero,
                idtag =BCrypt.hashpw(player.idtag, BCrypt.gensalt(10)),
                jogos = player.jogos,
                trofeu = player.trofeu,
                avaliacao = player.avaliacao
        ).let {
            val savedPlayer = psRepository.save(player)
            savedPlayer
        }
    }

    @Transactional
    fun UpdateName(@PathVariable(value = "id") id: Long,
                   @RequestBody newNome: PSN4): ResponseEntity<PSN4>
    {
        return psRepository.findById(id).map { tag ->
            val updateNome: PSN4 = tag
                    .copy(nome = newNome.nome)
            if (newNome.nome.trim().isEmpty() )
            {
                throw Exception()
            }
            ResponseEntity.ok().body(psRepository.save(updateNome))
        }.orElseThrow { EntityNotFoundException() }
    }

    @Transactional
    fun UpdateGenero(@PathVariable(value = "id") id: Long,
                     @RequestBody newG: PSN4): ResponseEntity<PSN4>
    {
        return psRepository.findById(id).map { tag ->
            val updateG: PSN4 = tag
                    .copy(genero = newG.genero)
            ResponseEntity.ok().body(psRepository.save(updateG))
        }.orElseThrow { EntityNotFoundException() }
    }

    @Transactional
    fun UpdateIdTag(@PathVariable(value = "id") id: Long,
                    @RequestBody newTag: PSN4): ResponseEntity<PSN4>
    {
        return psRepository.findById(id).map { tag ->
            val updateTag: PSN4 = tag
                    .copy(idtag = newTag.idtag)
            if (newTag.idtag.trim().isEmpty() )
            {
                throw Exception()
            }
            ResponseEntity.ok().body(psRepository.save(updateTag))
        }.orElseThrow { EntityNotFoundException() }
    }

    @Transactional
    fun UpdateJogos(@PathVariable(value = "id") id: Long,
                    @RequestBody newGame: PSN4): ResponseEntity<PSN4>
    {
        return psRepository.findById(id).map { tag ->
            val updateGame: PSN4 = tag
                    .copy(jogos = newGame.jogos)
            ResponseEntity.ok().body(psRepository.save(updateGame))
        }.orElseThrow { EntityNotFoundException() }
    }

    @Transactional
    fun UpdateTrofeu(@PathVariable(value = "id") id: Long,
                     @RequestBody newTrofeu: PSN4): ResponseEntity<PSN4>
    {
        return psRepository.findById(id).map { tag ->
            val updateTrofeu: PSN4 = tag
                    .copy(trofeu = newTrofeu.trofeu)
            ResponseEntity.ok().body(psRepository.save(updateTrofeu))
        }.orElseThrow { EntityNotFoundException() }
    }

    @Transactional
    fun UpdateAvaliacao(@PathVariable(value = "id") id: Long,
                        @RequestBody newAvaliacao: PSN4): ResponseEntity<PSN4>
    {
        return psRepository.findById(id).map { tag ->
            val updateAvaliacao: PSN4 = tag
                    .copy(avaliacao = newAvaliacao.avaliacao)
            ResponseEntity.ok().body(psRepository.save(updateAvaliacao))
        }.orElseThrow { EntityNotFoundException() }
    }

    @DeleteMapping("/delPSN/{id}")
    fun deletePlayerById(@PathVariable(value = "id") id: Long): ResponseEntity<Void>
    {
        return psRepository.findById(id).map { play  ->
            psRepository.delete(play)
            ResponseEntity<Void>(HttpStatus.OK)
        }.orElseThrow { EntityNotFoundException() }

    }

    @PostMapping("/login")
    fun validatePlayer(nome: String, idtag: String): PSN4 {

        var hashNome: String = BCrypt.hashpw(nome, BCrypt.gensalt(10))
        var hashIdTag: String = BCrypt.hashpw(idtag, BCrypt.gensalt(10))

        return psRepository.findPlayerByNomeAndIdtag(nome, idtag)
    }

    fun generateJWTToken(player: PSN4): Map<String, String>
    {
        val timestamp: Long = System.currentTimeMillis()
        val token: String = Jwts.builder().signWith(SignatureAlgorithm.HS256, Constants.API_SECRET_KEY)
                .setIssuedAt(Date(timestamp))
                .setExpiration(Date(timestamp + Constants.TOKEN_VALIDITY))
                .claim("id", player.id)
                .claim("nome", player.nome)
                .claim("genero", player.genero)
                .claim("idtag", player.idtag)
                .claim("jogos", player.jogos)
                .claim("trofeu", player.trofeu)
                .claim("avaliacao", player.avaliacao)
                .compact()
        val otherMap: MutableMap<String, String> = HashMap()
        otherMap["token"] = token
        return otherMap
    }
}
