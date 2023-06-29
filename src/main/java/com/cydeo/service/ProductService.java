package com.cydeo.service;

import com.cydeo.dto.ProductDto;

import java.util.List;

public interface ProductService {

    ProductDto createProduct(ProductDto productDto);

    List<ProductDto> listAllProducts();

    ProductDto findProductById(Long id);

    ProductDto updateProduct(ProductDto productDto);

    ProductDto deleteProduct(ProductDto productDto);


}
