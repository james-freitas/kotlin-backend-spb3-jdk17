package com.codeonblue.sample.model

import com.codeonblue.sample.controller.CategoryRequest
import com.codeonblue.sample.repository.CategoryEntity

data class Category(
    val id: String? = null,
    val name: String
) {
    fun toEntity() = CategoryEntity(
        id = id!!,
        name = name
    )

    companion object {
        fun valueOf(categoryRequest: CategoryRequest) = Category(name = categoryRequest.name)
        fun valueOf(categoryEntity: CategoryEntity) = Category(
            id = categoryEntity.id,
            name = categoryEntity.name!!
        )
    }
}