package com.cydeo.service.impl.unit;

import com.cydeo.TestDocumentInitializer;
import com.cydeo.dto.CategoryDto;
import com.cydeo.dto.CompanyDto;
import com.cydeo.entity.Category;
import com.cydeo.entity.Product;
import com.cydeo.enums.CompanyStatus;
import com.cydeo.exception.CategoryNotFoundException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.CategoryRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CompanyService companyService;

    @Spy
    private MapperUtil mapper = new MapperUtil(new ModelMapper());

    @InjectMocks
    private CategoryServiceImpl categoryService;


    @Test
    void should_give_the_category_by_id_when_category_exist_in_the_system() {
        CategoryDto categoryDto = TestDocumentInitializer.getCategory();
        Category categoryEntity = TestDocumentInitializer.getCategoryEntity();

        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(categoryEntity));

        CategoryDto actual = categoryService.findById(1L);

        assertThat(categoryDto).usingRecursiveComparison().isEqualTo(actual);
    }

    @Test
    void should_throw_exception_retrieving_category_by_id_when_category_doesnt_exist_in_the_system() {

        Throwable throwable = catchThrowable(() -> categoryService.findById(1L));

        assertThat(throwable).isInstanceOf(CategoryNotFoundException.class);
        assertThat(throwable).hasMessage("Category with this id " + 1L + " not found.");


    }

    @Test
    void listOfCategories() {
        List<CategoryDto> categoryDtos = getCategoryDtos();
        List<Category> categoryEntities = getCategoryEntities();
        when(companyService.getCompanyDtoByLoggedInUser()).thenReturn(TestDocumentInitializer.getCompany(CompanyStatus.ACTIVE));
        when(categoryRepository.getAllProductsByCompanySorted(anyLong())).thenReturn(categoryEntities);

        List<CategoryDto> actual = categoryService.listOfCategories();

        assertThat(categoryDtos).usingRecursiveComparison().isEqualTo(actual);

    }

    @Test
    void save() {
        CategoryDto categoryDto = TestDocumentInitializer.getCategory();
        Category category = mapper.convert(categoryDto, new Category());
        when(companyService.getCompanyDtoByLoggedInUser()).thenReturn(TestDocumentInitializer.getCompany(CompanyStatus.ACTIVE));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryDto actual = categoryService.save(categoryDto);

        assertThat(categoryDto).usingRecursiveComparison().isEqualTo(actual);
    }

    @Test
    void update() {
        CategoryDto categoryDto = TestDocumentInitializer.getCategory();
        categoryDto.setDescription("Updating Category");
        categoryDto.setId(1L);
        Category categoryEntity = mapper.convert(categoryDto, new Category());

        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(categoryEntity));
        when(categoryRepository.save(any(Category.class))).thenReturn(categoryEntity);

        CategoryDto actual = categoryService.update(categoryDto);

        assertThat(categoryDto).usingRecursiveComparison().isEqualTo(actual);
    }

    @Test
    void should_not_update_and_throw_exception_when_category_not_found() {
        CategoryDto categoryDto = TestDocumentInitializer.getCategory();
        categoryDto.setId(1L);
        Throwable throwable = catchThrowable(() -> categoryService.update(categoryDto));

        assertThat(throwable).isInstanceOf(CategoryNotFoundException.class);
        assertThat(throwable).hasMessage("Category has not been found");
    }

    @Test
    void delete() {
        CategoryDto categoryDto = TestDocumentInitializer.getCategory();
        categoryDto.setId(1L);
        Category category = mapper.convert(categoryDto, new Category());
        category.setIsDeleted(true);
        category.setDescription(categoryDto.getDescription() + " - " + categoryDto.getId());

        when(categoryRepository.findById(anyLong())).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        CategoryDto actual = categoryService.delete(categoryDto);

        assertThat(categoryDto).usingRecursiveComparison().isNotEqualTo(actual);
    }

    @Test
    void if_description_doesnt_exist_should_return_false() {
        CompanyDto companyDto = TestDocumentInitializer.getCompany(CompanyStatus.ACTIVE);
        CategoryDto categoryDto = TestDocumentInitializer.getCategory();
        Category category = mapper.convert(categoryDto, new Category());

        when(companyService.getCompanyDtoByLoggedInUser()).thenReturn(companyDto);
        when(categoryRepository
                .findCategoriesByDescriptionAndCompanyTitle(anyString(), anyString()))
                .thenReturn(Optional.ofNullable(category));

        boolean actual = categoryService.isDescriptionExist(categoryDto);

        Assertions.assertFalse(actual);

    }

    private List<CategoryDto> getCategoryDtos() {
        List<CategoryDto> categoryDtoList = Arrays.asList(
                TestDocumentInitializer.getCategory()
                , TestDocumentInitializer.getCategory()
                , TestDocumentInitializer.getCategory()
        );
        categoryDtoList.get(0).setHasProduct(true);
        categoryDtoList.get(1).setHasProduct(true);
        categoryDtoList.get(2).setHasProduct(true);

        return categoryDtoList;

    }


    private List<Category> getCategoryEntities() {
        List<Category> categoryList = getCategoryDtos().stream()
                .map(dto -> mapper.convert(dto, new Category()))
                .collect(Collectors.toList());
        categoryList.get(0).setListOfProducts(Arrays.asList(new Product()));
        categoryList.get(1).setListOfProducts(Arrays.asList(new Product()));
        categoryList.get(2).setListOfProducts(Arrays.asList(new Product()));
        return categoryList;
    }

}