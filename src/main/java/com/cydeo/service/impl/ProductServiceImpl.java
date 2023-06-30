package com.cydeo.service.impl;

import com.cydeo.dto.ProductDto;
import com.cydeo.entity.Product;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.ProductRepository;
import com.cydeo.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final MapperUtil mapper;

    public ProductServiceImpl(ProductRepository productRepository, MapperUtil mapper) {
        this.productRepository = productRepository;
        this.mapper = mapper;
    }

    @Override
    public ProductDto createProduct(ProductDto productDto) {
        Product product = mapper.convert(productDto, new Product());
        productRepository.save(product);
        return mapper.convert(product, new ProductDto());
    }

    @Override
    public List<ProductDto> listAllProducts() {
        List<Product> productList = productRepository.findAll();
        return productList.stream()
                .map(p -> mapper.convert(p, new ProductDto()))
                .collect(Collectors.toList());
    }

    @Override
    public ProductDto findProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Product has not been found"));

        return mapper.convert(product, new ProductDto());
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        Product product = mapper.convert(productDto, new Product());
        product.setId(productDto.getId());
        product.setQuantityInStock(findProductById(productDto.getId()).getQuantityInStock());
        productRepository.save(product);

        return findProductById(productDto.getId());
    }

    @Override
    public ProductDto deleteProduct(ProductDto productDto) {
        Product product = productRepository.findById(productDto.getId())
                .orElseThrow(() -> new NoSuchElementException("Product has not been found"));
        product.setIsDeleted(true);
        product.setName(productDto.getName() + " / " + productDto.getId());
        productRepository.save(product);

        return mapper.convert(product, new ProductDto());
    }


}
