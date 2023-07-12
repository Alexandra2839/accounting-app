package com.cydeo.service.impl;

import com.cydeo.dto.CategoryDto;
import com.cydeo.dto.CompanyDto;
import com.cydeo.entity.Category;
import com.cydeo.entity.Company;
import com.cydeo.exception.CategoryNotFoundException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.CategoryRepository;
import com.cydeo.service.CategoryService;
import com.cydeo.service.CompanyService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final MapperUtil mapperUtil;
    private final CompanyService companyService;


    public CategoryServiceImpl(CategoryRepository categoryRepository, MapperUtil mapperUtil, CompanyService companyService) {
        this.categoryRepository = categoryRepository;
        this.mapperUtil = mapperUtil;
        this.companyService = companyService;
    }

    @Override
    public CategoryDto findById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(()->new CategoryNotFoundException("Category with this id "+id+" not found."));
        return mapperUtil.convert(category, new CategoryDto());
    }

    @Override
    public List<CategoryDto> listOfCategories() {
        List<Category> categoryList = categoryRepository
                .getAllProductsByCompanySorted(companyService.getCompanyDtoByLoggedInUser().getId());

        return categoryList.stream()
                .map(c -> {
                    CategoryDto dto = mapperUtil.convert(c, new CategoryDto());
                    dto.setHasProduct(!c.getListOfProducts().isEmpty());
                    return dto;
                })
                .collect(Collectors.toList());


    }

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        Category category = mapperUtil.convert(categoryDto, new Category());
        category.setCompany(mapperUtil.convert(companyService.getCompanyDtoByLoggedInUser(), new Company()));
        categoryRepository.save(category);
        return mapperUtil.convert(category, new CategoryDto());
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto) {
        Category categoryInDB = categoryRepository.findById(categoryDto.getId())
                .orElseThrow(() -> new CategoryNotFoundException("Category has not been found"));

        categoryDto.setId(categoryInDB.getId());
        Category convert = mapperUtil.convert(categoryDto, new Category());
        convert.setCompany(categoryInDB.getCompany());

        Category saved = categoryRepository.save(convert);

        return mapperUtil.convert(saved, new CategoryDto());
    }

    @Override
    public CategoryDto delete(CategoryDto categoryDto) {
        Category category = categoryRepository.findById(categoryDto.getId())
                .orElseThrow(()->new CategoryNotFoundException("Category not found"));
        category.setIsDeleted(true);
        category.setDescription(categoryDto.getDescription() + " - " + categoryDto.getId());
        categoryRepository.save(category);
        return mapperUtil.convert(category, new CategoryDto());
    }

    @Override
    public boolean isDescriptionExist(CategoryDto categoryDto) {
        CompanyDto companyDto = companyService.getCompanyDtoByLoggedInUser();

        Category category = categoryRepository
                .findCategoriesByDescriptionAndCompanyTitle(categoryDto.getDescription(),
                        companyDto.getTitle())
                .orElse(null);
        if (category == null) return false;

        return !Objects.equals(categoryDto.getId(), category.getId());
    }


}
