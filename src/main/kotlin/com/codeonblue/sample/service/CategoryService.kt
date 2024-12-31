package com.codeonblue.sample.service

import com.codeonblue.sample.exception.ResourceAlreadyExistsException
import com.codeonblue.sample.exception.ResourceNotFoundException
import com.codeonblue.sample.model.Category
import com.codeonblue.sample.repository.CategoryEntity
import com.codeonblue.sample.repository.CategoryRepository
import com.codeonblue.sample.trimAndUppercase
import de.huxhorn.sulky.ulid.ULID
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CategoryService(val categoryRepository: CategoryRepository) {

    private val logger = LoggerFactory.getLogger(CategoryService::class.java)

    fun findAll(): List<Category> = categoryRepository.findAll().map { Category(it.id, it.name!!) }

    fun create(category: Category): Category {

        val formattedName = category.name.trimAndUppercase()
        if (categoryExists(formattedName)) {
            throw ResourceAlreadyExistsException()
        }

        val categorySaved = categoryRepository.save(
            CategoryEntity(
                id = ULID().nextULID(),
                name = formattedName
            )
          )
        return Category.valueOf(categorySaved)
    }

    private fun categoryExists(formattedName: String) = categoryRepository.findByName(formattedName) != null

    fun delete(categoryId: String) {
        categoryRepository.findById(categoryId).ifPresentOrElse(
            { category -> categoryRepository.delete(category) },
            {
                logger.error("Could not delete category [$categoryId]. It was not found")
                throw ResourceNotFoundException()
            }
        )
    }

    fun update(categoryToUpdate: Category) {
        categoryRepository.findById(categoryToUpdate.id!!).ifPresentOrElse(
            { categoryRepository.save(categoryToUpdate.toEntity()) },
            {
                logger.error("Could not update category [${categoryToUpdate.id}]. It was not found")
                throw ResourceNotFoundException()
            }
        )
    }

    fun findBy(categoryId: String): Category {
        val categoryFound = categoryRepository.findById(categoryId)
        if (categoryFound.isEmpty) {
            logger.error("Could not delete category [$categoryId]. It was not found")
            throw ResourceNotFoundException()
        }
        return Category.valueOf(categoryFound.get())
    }
}