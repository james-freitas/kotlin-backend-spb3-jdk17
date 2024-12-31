package com.codeonblue.sample.controller

import com.codeonblue.sample.exception.ResourceAlreadyExistsException
import com.codeonblue.sample.exception.ResourceNotFoundException
import com.codeonblue.sample.model.Category
import com.codeonblue.sample.service.CategoryService
import com.codeonblue.sample.controller.CategoryController
import com.fasterxml.jackson.databind.ObjectMapper
import de.huxhorn.sulky.ulid.ULID
import io.mockk.*
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [CategoryController::class])
@Import(CategoryControllerTest.TestConfig::class)
@DisplayName("Category controller unit tests")
class CategoryControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @TestConfiguration
    internal class TestConfig {
        @Bean
        fun categoryService() = mockk<CategoryService>()
    }

    @Autowired
    private lateinit var categoryService: CategoryService

    @BeforeEach
    fun beforeEach() {
        clearAllMocks()
    }

    @Nested
    inner class Create {

        @Test
        fun `should create a new category`() {

            // given
            val category = Category(id = ULID().nextULID(), name = "Cosmetics")

            // when
            val slotCategory = slot<Category>()
            every { categoryService.create(capture(slotCategory)) } returns category

            mockMvc.perform(
                post(BASE_PATH)
                    .content(
                        """
                            {
                                "name": "${category.name}"
                            } 
                        """.trimIndent()
                    )
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isCreated)
        }

        @Test
        fun `should fail to create an existing category`() {

            // given
            val category = Category(id = ULID().nextULID(), name = "Cosmetics")

            // when
            val slotCategory = slot<Category>()
            every { categoryService.create(capture(slotCategory)) } throws ResourceAlreadyExistsException()

            // validate rfc 7807 response
            mockMvc.perform(
                post(BASE_PATH)
                    .content(
                        """
                            { 
                                "name": "${category.name}"
                            }    
                        """.trimIndent()
                    )
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isConflict)
                .andExpect(MockMvcResultMatchers.jsonPath("$.type").value("about:blank"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Resource already exists"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.CONFLICT.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("Can not create existent resource"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.instance").value("/api/v1/categories"))
        }

        @Test
        fun `should fail to create a blank category`() {

            mockMvc.perform(
                post(BASE_PATH)
                    .content(
                        """
                            { 
                                "name": ""
                            }    
                        """.trimIndent()
                    )
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isBadRequest)
                .andExpect(MockMvcResultMatchers.jsonPath("$.type").value("about:blank"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("Invalid request content."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.instance").value("/api/v1/categories"))
        }
    }

    @Nested
    inner class FindAll {

        @Test
        fun `should find all the categories`() {

            // given
            val categories = listOf(
                Category(id = ULID().nextULID(), name = "Category1"),
                Category(id = ULID().nextULID(), name = "Category2")
            )

            // when
            every { categoryService.findAll() } returns categories

            mockMvc.perform(get(BASE_PATH))
                .andExpect(status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize<Int>(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("Category1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name").value("Category2"))
        }
    }

    @Nested
    inner class Delete {

        @Test
        fun `should delete a category successfully`() {

            // given
            val categoryId = ULID().nextULID()

            // when
            every { categoryService.delete(categoryId) } just Runs

            mockMvc.perform(delete("$BASE_PATH/$categoryId"))
                .andExpect(status().isNoContent)
        }

        @Test
        fun `should fail to delete a category when it does not exist`() {

            // given
            val categoryId = ULID().nextULID()

            // when
            every { categoryService.delete(categoryId) } throws ResourceNotFoundException()

            mockMvc.perform(delete("$BASE_PATH/$categoryId"))
                .andExpect(status().isNotFound)
                .andExpect(MockMvcResultMatchers.jsonPath("$.type").value("about:blank"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Resource was not found"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("Could not find resource"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.instance").value("/api/v1/categories/$categoryId"))
        }
    }

    @Nested
    inner class Update {
        @Test
        fun `should update a category successfully`() {

            // given
            val categoryId = ULID().nextULID()
            val category = Category(id = categoryId, name = "Cosmetics")

            // when
            val slotCategory = slot<Category>()
            every { categoryService.update(capture(slotCategory)) } just Runs

            mockMvc.perform(
                put("$BASE_PATH/$categoryId")
                    .content(
                        """
                            {
                                "name": "${category.name}"
                            } 
                        """.trimIndent()
                    )
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk)
        }

        @Test
        fun `should fail to update a blank category`() {

            val categoryId = ULID().nextULID()

            mockMvc.perform(
                put("$BASE_PATH/$categoryId")
                    .content(
                        """
                            { 
                                "name": ""
                            }    
                        """.trimIndent()
                    )
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isBadRequest)
                .andExpect(MockMvcResultMatchers.jsonPath("$.type").value("about:blank"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("Invalid request content."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.instance").value("/api/v1/categories/$categoryId"))
        }

        @Test
        fun `should fail to update a category when the categoryId is invalid`() {

            val categoryId = "invalid_category_id"

            mockMvc.perform(
                put("$BASE_PATH/$categoryId")
                    .content(
                        """
                            { 
                                "name": "some category"
                            }    
                        """.trimIndent()
                    )
                    .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isBadRequest)
                .andExpect(MockMvcResultMatchers.jsonPath("$.type").value("about:blank"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Bad Request"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.instance").value("/api/v1/categories/$categoryId"))
        }

        @Test
        fun `should fail to update a category when it does not exist`() {

            // given
            val categoryId = ULID().nextULID()
            val category = Category(id = categoryId, name = "Cosmetics")

            // when
            val slotCategory = slot<Category>()
            every { categoryService.update(capture(slotCategory)) } throws ResourceNotFoundException()

            mockMvc.perform(put("$BASE_PATH/$categoryId")
                .content(
                    """
                            { 
                                "name": "${category.name}"
                            }    
                        """.trimIndent()
                )
                .contentType(MediaType.APPLICATION_JSON)
            ).andExpect(status().isNotFound)
                .andExpect(MockMvcResultMatchers.jsonPath("$.type").value("about:blank"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Resource was not found"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("Could not find resource"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.instance").value("/api/v1/categories/$categoryId"))
        }
    }

    @Nested
    inner class FindById {

        @Test
        fun `should find category by id`() {

            // given
            val categoryId = ULID().nextULID()
            val category = Category(id = categoryId, name = "Category1")

            // when
            every { categoryService.findBy(categoryId) } returns category

            mockMvc.perform(get("$BASE_PATH/$categoryId"))
                .andExpect(status().isOk)
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Category1"))
        }

        @Test
        fun `should fail to get a category when it does not exist`() {

            // given
            val categoryId = ULID().nextULID()

            // when
            every { categoryService.findBy(categoryId) } throws ResourceNotFoundException()

            mockMvc.perform(get("$BASE_PATH/$categoryId"))
                .andExpect(status().isNotFound)
                .andExpect(MockMvcResultMatchers.jsonPath("$.type").value("about:blank"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("Resource was not found"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.detail").value("Could not find resource"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.instance").value("/api/v1/categories/$categoryId"))
        }
    }

    companion object {
        const val BASE_PATH = "/api/v1/categories"
    }
}