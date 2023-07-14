package com.cydeo.service.impl.unit;

import com.cydeo.TestDocumentInitializer;
import com.cydeo.dto.ProductDto;
import com.cydeo.entity.Product;
import com.cydeo.enums.CompanyStatus;
import com.cydeo.exception.ProductNotFoundException;
import com.cydeo.mapper.MapperUtil;
import com.cydeo.repository.ProductRepository;
import com.cydeo.service.CompanyService;
import com.cydeo.service.impl.ProductServiceImpl;

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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ProductServiceImplUnitTest {

    @Mock
    private ProductRepository productRepository;

    @Spy
    private MapperUtil mapper = new MapperUtil(new ModelMapper());

    @Mock
    private CompanyService companyService;

    @InjectMocks
    private ProductServiceImpl productService;


    @Test
    public void should_save_product_and_return_saved_product() {
        ProductDto productDto = TestDocumentInitializer.getProduct();
        Product converted = mapper.convert(productDto, new Product());

        when(productRepository.save(any(Product.class))).thenReturn(converted);

        ProductDto savedProduct = productService.createProduct(productDto);

        assertThat(savedProduct).usingRecursiveComparison().isEqualTo(productDto);

    }

    @Test
    public void should_throw_exception_when_product_doesnt_exist_with_id() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        Throwable throwable = catchThrowable(() -> productService.findProductById(1L));
        assertThat(throwable).isInstanceOf(ProductNotFoundException.class);
        assertThat(throwable).hasMessage("Product with this id " + 1L + " has not been found");
    }

    @Test
    public void should_give_me_the_product_with_id() {
        ProductDto dto = TestDocumentInitializer.getProduct();
        dto.setId(1L);
        Product product = mapper.convert(dto, new Product());
        when(productRepository.findById(dto.getId())).thenReturn(Optional.of(product));
        ProductDto actual = productService.findProductById(1L);
        assertThat(dto).usingRecursiveComparison().isEqualTo(actual);
    }

    @Test
    public void should_get_all_products() { //49
        List<ProductDto> dtos = getProductDtos();

        List<Product> entities = getProductEntities();

        when(companyService.getCompanyDtoByLoggedInUser())
                .thenReturn(TestDocumentInitializer.getCompany(CompanyStatus.PASSIVE));
        when(productRepository.findAllByCompanyAndProductNameSort(1L)).thenReturn(entities);

        List<ProductDto> actualList = productService.listAllProducts();

        assertThat(actualList).usingRecursiveComparison().isEqualTo(dtos);

    }

    @Test
    public void should_update_product(){
        ProductDto productDto = TestDocumentInitializer.getProduct();
        productDto.setId(1L);
        productDto.setName("sgnal17");
        Product product = mapper.convert(productDto, new Product());



        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDto actual=productService.updateProduct(productDto);

        assertThat(actual).usingRecursiveComparison().isEqualTo(productDto);
    }

    @Test
    public void delete(){
        ProductDto productDto = new ProductDto();
        productDto.setName("Delete Product Test");
        productDto.setId(17L);
        Product product = mapper.convert(productDto, new Product());
        product.setIsDeleted(false);

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDto actual = productService.deleteProduct(productDto);

        assertTrue(product.getIsDeleted());
        assertNotEquals(productDto.getName(),product.getName());
    }

    @Test
    public void check_product_name_should_be_unique_in_company(){
        ProductDto productDto = new ProductDto();
        productDto.setName("sgnal17");
        Product product=mapper.convert(productDto,new Product());
        when(companyService.getCompanyDtoByLoggedInUser()).thenReturn(TestDocumentInitializer.getCompany(CompanyStatus.ACTIVE));
        when(productRepository.findProductByNameAndCategory_Company_Title("sgnal17","Test_Company")).thenReturn(Optional.of(product));
        boolean actual = productService.isNameExist(productDto);

        assertEquals(false,actual);
    }

    private List<ProductDto> getProductDtos() {
        List<ProductDto> dtos = Arrays.asList(
                  TestDocumentInitializer.getProduct()
                , TestDocumentInitializer.getProduct()
                , TestDocumentInitializer.getProduct());
        dtos.get(0).setHasInvoiceProduct(true);
        dtos.get(1).setHasInvoiceProduct(true);
        dtos.get(2).setHasInvoiceProduct(true);


        return dtos;
    }

    private List<Product> getProductEntities() {
        List<Product> entities = getProductDtos().stream().map(dto -> mapper.convert(dto, new Product())).collect(Collectors.toList());
        entities.get(0).setInvoiceProducts(Arrays.asList(TestDocumentInitializer.getInvoiceProductEntity()));
        entities.get(1).setInvoiceProducts(Arrays.asList(TestDocumentInitializer.getInvoiceProductEntity()));
        entities.get(2).setInvoiceProducts(Arrays.asList(TestDocumentInitializer.getInvoiceProductEntity()));

        return entities;
    }


}