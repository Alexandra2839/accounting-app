package com.cydeo.client;

import com.cydeo.dto.CountryDTO;
import com.cydeo.dto.TokenDto;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(url="https://www.universal-tutorial.com",name="COUNTRY-CLIENT")
public interface CountryClient {

    @GetMapping(value = "/api/getaccesstoken", consumes = MediaType.APPLICATION_JSON_VALUE)
    TokenDto auth(@RequestHeader("user-email") String email, @RequestHeader("api-token") String apiToken );


    @GetMapping(value = "/api/countries", consumes = MediaType.APPLICATION_JSON_VALUE)
    List<CountryDTO> getCountryList(@RequestHeader("Authorization") String bearerToken);
}
