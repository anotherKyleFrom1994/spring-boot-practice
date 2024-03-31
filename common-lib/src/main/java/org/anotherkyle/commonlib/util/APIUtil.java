package org.anotherkyle.commonlib.util;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClientRequest;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;


@SuppressWarnings("unchecked")
public class APIUtil {
    protected static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(APIUtil.class);

    private static final WebClient webClient = WebClient.builder()
            .exchangeStrategies(ExchangeStrategies.builder()
                    //TODO Modify this limit when the statistics are analyzed
                    .codecs(c -> c.defaultCodecs().maxInMemorySize(30 * 1024 * 1024))
                    .build())
            .build();

    private static WebClient.RequestHeadersSpec<?> prepareSpec(HttpMethod httpMethod, String uri, Map<String, List<String>> headers, Object requestBody) {
        WebClient.RequestBodySpec requestSpec = webClient.method(httpMethod).uri(uri);
        WebClient.RequestHeadersSpec<?> requestHeadersSpec = requestBody != null ? requestSpec.bodyValue(requestBody) : requestSpec;

        requestHeadersSpec = requestHeadersSpec
                .accept(MediaType.APPLICATION_JSON, MediaType.MULTIPART_FORM_DATA, MediaType.IMAGE_PNG, MediaType.TEXT_PLAIN)
                .acceptCharset(Charset.defaultCharset(), StandardCharsets.UTF_8, StandardCharsets.ISO_8859_1)
                .httpRequest(req -> {
                    HttpClientRequest nativeRequest = req.getNativeRequest();
                    log.debug(nativeRequest.toString());
                    log.debug("Request: {} {}", req.getMethod(), req.getURI());
                    req.getHeaders().forEach((name, values) -> values.forEach(value -> log.debug("{}={}", name, value)));
                });

        if (requestBody instanceof MultiValueMap)
            requestHeadersSpec = requestSpec
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData((MultiValueMap<String, HttpEntity<?>>) requestBody));

        if (headers != null)
            requestHeadersSpec = requestHeadersSpec.headers(h -> h.putAll(headers));
        return requestHeadersSpec;
    }

    public static Mono<Response> processCallRemote(HttpMethod httpMethod,
                                                   String uri,
                                                   int timeout,
                                                   @Nullable Object requestBody) {
        return processCallRemote(httpMethod, uri, null, timeout, requestBody);
    }

    public static Flux<Response> processCallRemoteMany(HttpMethod httpMethod,
                                                       String uri,
                                                       int timeout,
                                                       @Nullable Object requestBody) {
        return processCallRemoteMany(httpMethod, uri, null, timeout, requestBody);
    }

    public static Flux<Response> processCallRemoteMany(HttpMethod httpMethod,
                                                       String uri,
                                                       Map<String, List<String>> headers,
                                                       int timeout,
                                                       @Nullable Object requestBody) {
        if (httpMethod == null) return Flux.error(new NullPointerException("Null HttpMethod"));
        if (!StringUtils.hasText(uri)) return Flux.error(new NullPointerException("Blank or null string"));

        return prepareSpec(httpMethod, uri, headers, requestBody)
                .exchangeToFlux(APIUtil::getResponseFlux)
                .timeout(Duration.ofSeconds(timeout))
                .onErrorMap(RuntimeException::new);
    }

    public static Mono<Response> processCallRemote(HttpMethod httpMethod,
                                                   String uri,
                                                   Map<String, List<String>> headers,
                                                   int timeout,
                                                   @Nullable Object requestBody) {
        if (httpMethod == null) return Mono.error(new NullPointerException("Null HttpMethod"));
        if (!StringUtils.hasText(uri)) return Mono.error(new NullPointerException("Blank or null string"));

        return prepareSpec(httpMethod, uri, headers, requestBody)
                .exchangeToMono(APIUtil::getResponseMono)
                .timeout(Duration.ofSeconds(timeout))
                .onErrorMap(RuntimeException::new);
    }

    private static Mono<Response> getResponseMono(ClientResponse resp) {
        ClientResponse.Headers headers = resp.headers();
        Mono<Response> res;
        log.debug(String.valueOf(headers.asHttpHeaders()));

        if (headers.header("content-type").contains(MediaType.APPLICATION_JSON_VALUE)
                || headers.header("Content-Type").contains(MediaType.APPLICATION_JSON_VALUE)) {
            res = resp.bodyToMono(JsonNode.class)
                    .map(o -> new Response(HttpStatus.valueOf(HttpStatus.valueOf(resp.statusCode().value()).value()), o));
        } else
            res = resp.bodyToMono(String.class)
                    .map(s -> new Response(HttpStatus.valueOf(resp.statusCode().value()), JsonUtil.wrapPureText(s)))
                    .defaultIfEmpty(new Response(HttpStatus.valueOf(resp.statusCode().value()), JsonUtil.objectMapper.createObjectNode()));

        return res.onErrorMap(RuntimeException::new);
    }

    private static Flux<Response> getResponseFlux(ClientResponse resp) {
        ClientResponse.Headers headers = resp.headers();
        Flux<Response> res;
        log.debug(String.valueOf(headers.asHttpHeaders()));

        if (headers.contentType().filter(MediaType.APPLICATION_JSON::equals).isPresent()) {
            res = resp.bodyToFlux(JsonNode.class)
                    .map(o -> new Response(HttpStatus.valueOf(resp.statusCode().value()), o));
        } else
            res = resp.bodyToFlux(String.class)
                    .map(s -> new Response(HttpStatus.valueOf(resp.statusCode().value()), JsonUtil.wrapPureText(s)))
                    .defaultIfEmpty(new Response(HttpStatus.valueOf(resp.statusCode().value()), JsonUtil.objectMapper.createObjectNode()));

        return res.onErrorMap(RuntimeException::new);
    }

    @Data
    @AllArgsConstructor
    public static class Response {
        private HttpStatus status;
        private JsonNode responseBody;
    }
}
