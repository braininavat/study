package com.example.testproject.service.impl;

import com.example.testproject.data.dto.NaverUriDTO;
import com.example.testproject.data.dto.ShortUrlResponseDTO;
import com.example.testproject.data.entity.ShortUrlEntity;
import com.example.testproject.service.ShortUrlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;

@Service
public class ShortUrlServiceImpl implements ShortUrlService {

    private final Logger LOGGER = LoggerFactory.getLogger(ShortUrlServiceImpl.class);
    private final ShortUrlDAO shortUrlDAO;
    private final ShortUrlRedisRepository shortUrlRedisRepository;


    @Override
    public ShortUrlResponseDTO getShortUrl(String clientId, String clientSecret, String originalUrl) {
        return null;
    }

    @Override
    public ShortUrlResponseDTO generateShortUrl(String clientId, String clientSecret, String originalUrl) {
        LOGGER.info("[generateShortUrl] request Data :{}",originalUrl );

        ResponseEntity<NaverUriDTO> responseEntity = requestShortUrl(clientId,clientSecret,originalUrl);

        String orgUrl = responseEntity.getBody().getResult().getOrgUrl();
        String shortUrl = responseEntity.getBody().getResult().getUrl();
        String hash = responseEntity.getBody().getResult().getHash();

        ShortUrlEntity shortUrlEntity = new ShortUrlEntity();
        shortUrlEntity.setOrgUrl(orgUrl);
        shortUrlEntity.setUrl(shortUrl);
        shortUrlEntity.setHash(hash);

        shortUrlDAO.saveShortUrl(shortUrlEntity);

        ShortUrlResponseDTO shortUrlResponseDTO = new ShortUrlResponseDTO(orgUrl, shortUrl);
        LOGGER.info("[generateShortUrl] Response DTO : {}", shortUrlResponseDTO);
        return shortUrlResponseDTO;
    }

    @Override
    public ShortUrlResponseDTO updateShortUrl(String clientId, String clientSecret, String originalUrl) {
        return null;
    }

    @Override
    public ShortUrlResponseDTO deleteByShortUrl(String shortUrl) {
        return null;
    }

    @Override
    public ShortUrlResponseDTO deleteByOriginalUrl(String originalUrl) {
        return null;
    }

    private ResponseEntity<NaverUriDTO> requestShortUrl(String clientId, String clientSecret,
                                                        String originalUrl) {
        LOGGER.info("[requestShortUrl] client ID : ***, client Secret : ***, original URL : {}", originalUrl);

        URI uri = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com")
                .path("/v1/util/shorturl")
                .queryParam("url", originalUrl)
                .encode()
                .build()
                .toUri();

        LOGGER.info("[requestShortUrl] set HTTP Request Header");
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(new MediaType[]{MediaType.APPLICATION_JSON}));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);

        HttpEntity<String> entity = new HttpEntity<>("", headers);
        //Body 와 Header 를 조합

        RestTemplate restTemplate = new RestTemplate();
        
        LOGGER.info("[requestShortUrl] request by restTemplate");
        ResponseEntity<NaverUriDTO> responseEntity = restTemplate.exchange(uri, HttpMethod.GET,
                entity, NaverUriDTO.class);
        //exchange 로 호출

        LOGGER.info("[requestShortUrl] request has been successfully complete.");

        return responseEntity;
    }
}
