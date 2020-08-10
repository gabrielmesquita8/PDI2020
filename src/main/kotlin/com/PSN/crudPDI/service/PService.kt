package com.PSN.crudPDI.service

import com.PSN.crudPDI.model.PSN4
import com.PSN.crudPDI.repository.PSRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import java.util.*
import javax.persistence.EntityNotFoundException


@Service
class PService (
        @Autowired
        private val psRepository: PSRepository,
        private val objectMapper: ObjectMapper
){

    fun getAllPlayers(): MutableIterable<PSN4>
    {
        return psRepository.findAll()
    }

    fun getPlayerById(id: Long) : Optional<PSN4>
    {
        validatePlayerExist(id)
        return psRepository.findById(id)
    }

    @Transactional
    fun createNewPlayer(player: PSN4 ) : PSN4
    {
        if(player.nome.trim().isEmpty() || player.idtag.trim().isEmpty())
        {
            throw Exception()
        }

        return PSN4(
                nome = player.nome,
                genero = player.genero,
                idtag = player.idtag,
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
        validatePlayerExist(id)
        return psRepository.findById(id).map { tag ->
            val updateNome: PSN4 = tag
                    .copy(nome = newNome.nome)
            if (newNome.nome.trim().isEmpty() )
            {
                throw Exception()
            }
            ResponseEntity.ok().body(psRepository.save(updateNome))
        }.orElse(ResponseEntity.notFound().build())
    }

    @Transactional
    fun UpdateGenero(@PathVariable(value = "id") id: Long,
                     @RequestBody newG: PSN4): ResponseEntity<PSN4>
    {
        validatePlayerExist(id)
        return psRepository.findById(id).map { tag ->
            val updateG: PSN4 = tag
                    .copy(genero = newG.genero)
            if (newG.genero.trim().isEmpty() )
            {
                throw Exception()
            }
            ResponseEntity.ok().body(psRepository.save(updateG))
        }.orElse(ResponseEntity.notFound().build())
    }

    @Transactional
    fun UpdateIdTag(@PathVariable(value = "id") id: Long,
                    @RequestBody newTag: PSN4): ResponseEntity<PSN4>
    {
        validatePlayerExist(id)
        return psRepository.findById(id).map { tag ->
            val updateTag: PSN4 = tag
                    .copy(idtag = newTag.idtag)
            if (newTag.idtag.trim().isEmpty() )
            {
                throw Exception()
            }
            ResponseEntity.ok().body(psRepository.save(updateTag))
        }.orElse(ResponseEntity.notFound().build())
    }

    @Transactional
    fun UpdateJogos(@PathVariable(value = "id") id: Long,
                    @RequestBody newGame: PSN4): ResponseEntity<PSN4>
    {
        validatePlayerExist(id)
        return psRepository.findById(id).map { tag ->
            val updateGame: PSN4 = tag
                    .copy(jogos = newGame.jogos)
            ResponseEntity.ok().body(psRepository.save(updateGame))
        }.orElse(ResponseEntity.notFound().build())
    }

    @Transactional
    fun UpdateTrofeu(@PathVariable(value = "id") id: Long,
                     @RequestBody newTrofeu: PSN4): ResponseEntity<PSN4>
    {
        validatePlayerExist(id)
        return psRepository.findById(id).map { tag ->
            val updateTrofeu: PSN4 = tag
                    .copy(trofeu = newTrofeu.trofeu)
            ResponseEntity.ok().body(psRepository.save(updateTrofeu))
        }.orElse(ResponseEntity.notFound().build())
    }

    @Transactional
    fun UpdateAvaliacao(@PathVariable(value = "id") id: Long,
                        @RequestBody newAvaliacao: PSN4): ResponseEntity<PSN4>
    {
        validatePlayerExist(id)
        return psRepository.findById(id).map { tag ->
            val updateAvaliacao: PSN4 = tag
                    .copy(avaliacao = newAvaliacao.avaliacao)
            ResponseEntity.ok().body(psRepository.save(updateAvaliacao))
        }.orElse(ResponseEntity.notFound().build())
    }

    @DeleteMapping("/delPSN/{id}")
    fun deletePlayerById(@PathVariable(value = "id") id: Long): ResponseEntity<Void>
    {
        validatePlayerExist(id)
        return psRepository.findById(id).map { play  ->
            psRepository.delete(play)
            ResponseEntity<Void>(HttpStatus.OK)
        }.orElse(ResponseEntity.notFound().build())

    }

    /*
     Método que valida a existência do id.
     Esse throw é substituído pela classe CustomErrorMessage
     Ele continua aqui porque sem ele não entra na classe da exceção
     */
    private fun validatePlayerExist(id: Long): Boolean
    {
        if(psRepository.existsById(id))
        {
            return true
        }
        else
        {
            throw EntityNotFoundException("Player com $id não encontrado")
        }
    }

}
