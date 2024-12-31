package com.codeonblue.sample.repository

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CategoryRepository: JpaRepository<CategoryEntity, String> {

    fun findByName(name: String): CategoryEntity?
}

@Entity
@Table(name = "category")
class CategoryEntity {

    @Id
    var id: String? = null
    var name: String? = null

    constructor(id: String, name: String) {
        this.id = id
        this.name = name
    }
}
