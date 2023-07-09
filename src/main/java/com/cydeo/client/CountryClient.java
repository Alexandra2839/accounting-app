package com.cydeo.client;

import com.cydeo.dto.CountryDTO;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Headers({
        "Content-Type: application/json",
        "Authorization: Bearer {authToken}"
})
@FeignClient(url="https://www.universal-tutorial.com",name="COUNTRY-CLIENT")
public interface CountryClient {

    @GetMapping("/api/countries")
    List<CountryDTO> getCountryList(@Param("authToken") String authToken);
}
