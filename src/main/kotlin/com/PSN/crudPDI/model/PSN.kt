package com.PSN.crudPDI.model

import com.sun.istack.NotNull
import javax.persistence.*

//data class automaticamente gera toString, equals e hash
@Entity
@Table(name="PSN4")
data class PSN4(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long = 0,
        @field:NotNull
        val nome: String = "",
        val genero: String = "",
        @field:NotNull
        val idtag: String = "",
        val jogos: Int,
        val trofeu: Int = 0,
        val avaliacao: Int
)