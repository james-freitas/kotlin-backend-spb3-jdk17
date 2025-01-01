package com.codeonblue.sample.controller

import com.codeonblue.sample.model.Category
import com.codeonblue.sample.service.CategoryService
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*


@RequestMapping("/api/v1/categories")
@RestController
@Validated
class CategoryController(val categoryService: CategoryService) {

    private val logger = LoggerFactory.getLogger(CategoryController::class.java)

    @GetMapping
    fun findAll(): List<CategoryResponse> {
        return categoryService.findAll().map { CategoryResponse(it.id!!, it.name) }.also {
            logger.info ("Categories successfully returned.")
        }
    }

    @PostMapping
    fun create(@RequestBody @Valid categoryRequest: CategoryRequest): ResponseEntity<CategoryResponse> {
        val category = categoryService.create(Category.valueOf(categoryRequest))
        return ResponseEntity<CategoryResponse>(CategoryResponse.valueOf(category), HttpStatus.CREATED)
    }

    @DeleteMapping("/{categoryId}")
    fun delete(@PathVariable categoryId: String): ResponseEntity<Void> {
        categoryService.delete(categoryId)
        logger.info("Category of id: $categoryId removed successfully")
        return ResponseEntity<Void>(HttpStatus.NO_CONTENT)
    }

    @GetMapping("/{categoryId}")
    fun getById(@PathVariable categoryId: String): ResponseEntity<CategoryResponse> {
        val category = categoryService.findBy(categoryId)
        logger.info("Category of id: $categoryId retrieved successfully")
        return ResponseEntity<CategoryResponse>(CategoryResponse.valueOf(category), HttpStatus.OK)
    }

    @PutMapping("/{categoryId}")
    fun update(
        @PathVariable @Pattern(regexp = "^[0-9A-HJKMNP-TV-Za-hjkmnp-tv-z]{26}$")categoryId: String,
        @RequestBody @Valid request: CategoryRequest
    ): ResponseEntity<Void> {
        categoryService.update(Category(id = categoryId, name = request.name))
        logger.info("Category updated successfully")
        return ResponseEntity<Void>(HttpStatus.OK)
    }

    @PostMapping("/cleanup")
    fun deleteAll(): ResponseEntity<Void> {
        categoryService.deleteAll()
        logger.info("All categories deleted")
        return ResponseEntity<Void>(HttpStatus.OK)
    }
}

data class CategoryResponse(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("name")
    val name: String
) {
    companion object {
        fun valueOf(category: Category) = CategoryResponse(
            id = category.id!!,
            name = category.name
        )
    }
}

data class CategoryRequest(
    @field:NotBlank(message = "Category name cannot be blank")
    val name: String
)

