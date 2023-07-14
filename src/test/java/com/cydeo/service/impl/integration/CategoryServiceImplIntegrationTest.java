package com.cydeo.service.impl.integration;

import com.cydeo.TestDocumentInitializer;
import com.cydeo.dto.CategoryDto;
import com.cydeo.entity.Category;
import com.cydeo.exception.CategoryNotFoundException;
import com.cydeo.repository.CategoryRepository;
import com.cydeo.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import javax.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CategoryServiceImplIntegrationTest {

    @Autowired
    CategoryServiceImpl categoryService;

    @Autowired
    CategoryRepository categoryRepository;

    @Test
    void should_find_category_by_id(){
        CategoryDto dto = categoryService.findById(1L);

        assertNotNull(dto);
        assertEquals("Computer",dto.getDescription());
    }

    @Test
    void should_throw_exception_when_category_not_found(){

        Throwable throwable=catchThrowable(()->categoryService.findById(100L));

        assertInstanceOf(CategoryNotFoundException.class,throwable);
        assertEquals("Category with this id "+100L+" not found.",throwable.getMessage());
    }


    @Test
    @WithMockUser(username = "manager@bluetech.com", password = "Abc1", roles = "MANAGER")
    void  should_get_categoryDto_list(){
        List<CategoryDto> categoryDtoList = categoryService.listOfCategories();
        List<String> expectedDescriptions=List.of("Monitor","Phone","TV");
        List<String> actualDescriptions = categoryDtoList.stream().map(CategoryDto::getDescription).collect(Collectors.toList());

        assertEquals(expectedDescriptions,actualDescriptions);
    }

    @Test
    @WithMockUser(username = "manager@bluetech.com", password = "Abc1", roles = "MANAGER")
    void should_save_category(){
        CategoryDto dto = TestDocumentInitializer.getCategory();
        CategoryDto actual = categoryService.save(dto);

        assertThat(dto).usingRecursiveComparison()
                .ignoringFields("id","company")
                .isEqualTo(actual);

        assertNotNull(actual.getId());
        assertNotNull(actual.getCompany());
    }

    @Test
    @WithMockUser(username = "manager@bluetech.com", password = "Abc1", roles = "MANAGER")
    void should_update_category(){
        CategoryDto dto = categoryService.findById(1L);
        dto.setDescription("Updating");

        CategoryDto actual = categoryService.update(dto);

        assertThat(actual).usingRecursiveComparison().isEqualTo(dto);

    }

    @Test
    @WithMockUser(username = "manager@bluetech.com", password = "Abc1", roles = "MANAGER")
    void should_not_update_category_if_it_doesnt_exist_in_the_system(){
        CategoryDto dto = TestDocumentInitializer.getCategory();
        dto.setId(17L);

        Throwable throwable=catchThrowable(()->categoryService.update(dto));

        assertThat(throwable).isInstanceOf(CategoryNotFoundException.class);
        assertThat(throwable).hasMessage("Category has not been found");
    }

    @Test
    @WithMockUser(username = "manager@bluetech.com", password = "Abc1", roles = "MANAGER")
    void should_delete_category(){
        CategoryDto dto = categoryService.findById(1L);

        categoryService.delete(dto);

        Category category=categoryRepository.findById(1L).orElseThrow(()-> new CategoryNotFoundException("Category not found"));
        assertThat(category.getIsDeleted()).isEqualTo(true);

    }

    @Test
    @WithMockUser(username = "manager@bluetech.com", password = "Abc1", roles = "MANAGER")
    void should_return_true_if_description_exist_in_the_system(){
        CategoryDto dto = TestDocumentInitializer.getCategory();
        dto.setDescription("Phone");
        boolean actual = categoryService.isDescriptionExist(dto);
        assertTrue(actual);
    }
}

