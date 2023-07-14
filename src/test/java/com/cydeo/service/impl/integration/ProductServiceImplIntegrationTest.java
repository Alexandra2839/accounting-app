package com.cydeo.service.impl.integration;

import com.cydeo.TestDocumentInitializer;
import com.cydeo.dto.ProductDto;
import com.cydeo.exception.ProductNotFoundException;
import com.cydeo.repository.ProductRepository;
import com.cydeo.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import javax.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

@SpringBootTest
@Transactional
public class ProductServiceImplIntegrationTest {

    @Autowired
    ProductServiceImpl productService;

    @Autowired
    ProductRepository productRepository;


    @Test
    @WithMockUser(username = "manager@bluetech.com", password = "Abc1", roles = "MANAGER")
    void should_create_product(){
        ProductDto dto = TestDocumentInitializer.getProduct();
        ProductDto actual = productService.createProduct(dto);
        assertThat(actual).usingRecursiveComparison()
                .ignoringFields("id","invoiceProducts")
                .isEqualTo(dto);
        Assertions.assertNotNull(actual.getId());

    }

    @Test
    void should_find_product_by_id(){
        ProductDto productDto = productService.findProductById(1L);

        Assertions.assertEquals("HP Elite 800G1 Desktop Computer Package",productDto.getName());
    }

    @Test
    void should_not_find_product_by_id_when_id_doesnt_exist_in_the_system_and_should_throw_exception(){

        Throwable throwable=catchThrowable(()->productService.findProductById(17L));

        assertThat(throwable).isInstanceOf(ProductNotFoundException.class);
        assertThat(throwable).hasMessage("Product with this id "+17L+" has not been found");
    }

    @Test
    @WithMockUser(username = "manager@bluetech.com", password = "Abc1", roles = "MANAGER")
    void should_get_all_products(){
        List<ProductDto> dtoList = productService.listAllProducts();

        List<String> expectedList = List.of("Moto G Power","Samsung Galaxy S20 (renewed)","Samsung Galaxy S22");
        List<String> actualList = dtoList.stream().map(ProductDto::getName).collect(Collectors.toList());

        Assertions.assertEquals(expectedList,actualList);
    }

    @Test
    void should_update_product(){
        ProductDto dto = productService.findProductById(1L);
        dto.setName("sgnal17");

        ProductDto actual = productService.updateProduct(dto);

        assertThat(actual).usingRecursiveComparison().isEqualTo(dto);
        assertThat(actual.getName()).isEqualTo("sgnal17");

    }

    @Test
    void should_delete_product(){
        ProductDto productDto = productService.findProductById(1L);
        ProductDto actual = productService.deleteProduct(productDto);

        assertThat(actual).usingRecursiveComparison().isNotEqualTo(productDto);
        Assertions.assertNotEquals(actual.getName(), productDto.getName());
    }

    @Test
    @WithMockUser(username = "manager@bluetech.com", password = "Abc1", roles = "MANAGER")
    void should_return_false_if_product_name_doesnt_exist_in_the_system(){
        ProductDto productDto = TestDocumentInitializer.getProduct();
        productDto.setName("Moto G Power");
        boolean nameExist = productService.isNameExist(productDto);

        Assertions.assertTrue(nameExist);
    }








}
