package com.cydeo.service.impl;

import com.cydeo.dto.CategoryDto;
import com.cydeo.entity.Category;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.CategoryRepository;
import com.cydeo.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final MapperUtil mapperUtil;

    public CategoryServiceImpl(CategoryRepository categoryRepository, MapperUtil mapperUtil) {
        this.categoryRepository = categoryRepository;
        this.mapperUtil = mapperUtil;
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
        return mapperUtil.convert(category,new CategoryDto());
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto) {
        Category categoryInDB = categoryRepository.findById(categoryDto.getId()).orElseThrow();
        Category convertedCategory = mapperUtil.convert(categoryDto, new Category());
        convertedCategory.setId(categoryInDB.getId());
        categoryRepository.save(convertedCategory);
        return mapperUtil.convert(convertedCategory, new CategoryDto());
    }

    @Override
    public CategoryDto delete(CategoryDto categoryDto) {
        Category category = categoryRepository.findByIdAndIsDeleted(categoryDto.getId(),false);
        category.setIsDeleted(true);
        category.setDescription(categoryDto.getDescription() + " - " + categoryDto.getId());
        categoryRepository.save(category);
        return mapperUtil.convert(category,new CategoryDto());
    }
}
