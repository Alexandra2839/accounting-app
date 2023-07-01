package com.cydeo.service;

import com.cydeo.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto findById(Long id);

    List<CategoryDto> listOfCategories();

    CategoryDto save(CategoryDto categoryDto);

    CategoryDto update(CategoryDto categoryDto);

    CategoryDto delete(CategoryDto categoryDto);


}
