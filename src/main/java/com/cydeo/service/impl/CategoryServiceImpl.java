package com.cydeo.service.impl;

import com.cydeo.dto.CategoryDto;
import com.cydeo.dto.CompanyDto;
import com.cydeo.entity.Category;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.CategoryRepository;
import com.cydeo.service.CategoryService;
import com.cydeo.service.CompanyService;
import org.springframework.stereotype.Service;

import java.util.List;
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
        Category category = categoryRepository.findById(id).orElseThrow();
        return mapperUtil.convert(category, new CategoryDto());
    }

    @Override
    public List<CategoryDto> listOfCategories() {
        return categoryRepository.findAll().stream().map(C -> mapperUtil.convert(C, new CategoryDto())).collect(Collectors.toList());
    }

    @Override
    public CategoryDto save(CategoryDto categoryDto) {
        Category category = mapperUtil.convert(categoryDto, new Category());
        categoryRepository.save(category);
        return mapperUtil.convert(category, new CategoryDto());
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto) {
        Category categoryInDB = categoryRepository.findByIdAndIsDeleted(categoryDto.getId(), false);
        Category convertedCategory = mapperUtil.convert(categoryDto, new Category());
        convertedCategory.setId(categoryInDB.getId());
        categoryRepository.save(convertedCategory);
        return mapperUtil.convert(convertedCategory, new CategoryDto());
    }

    @Override
    public CategoryDto delete(CategoryDto categoryDto) {
        Category category = categoryRepository.findByIdAndIsDeleted(categoryDto.getId(), false);
        category.setIsDeleted(true);
        category.setDescription(categoryDto.getDescription() + " - " + categoryDto.getId());
        categoryRepository.save(category);
        return mapperUtil.convert(category, new CategoryDto());
    }

    @Override
    public List<CategoryDto> getAllCategoriesByCompany(){
        CompanyDto dto =companyService.getCompanyDtoByLoggedInUser();
        return categoryRepository.findAll().stream()
                .filter(c->c.getCompany().getId()== dto.getId())
                .map(c->mapperUtil.convert(c,new CategoryDto()))
                .collect(Collectors.toList());
    }
}
