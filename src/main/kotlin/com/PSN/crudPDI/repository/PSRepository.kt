package com.PSN.crudPDI.repository

import com.PSN.crudPDI.model.PSN4
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

//Essa é a classe DAO, com a extensão do jpa todos os métodos CRUD são importados
@Repository
interface PSRepository : CrudRepository<PSN4, Long>