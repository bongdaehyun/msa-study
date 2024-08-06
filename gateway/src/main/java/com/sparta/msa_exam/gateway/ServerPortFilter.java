package com.sparta.msa_exam.gateway;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

@RequiredArgsConstructor
@Slf4j
public class ServerPortFilter implements GlobalFilter {

    private final DiscoveryClient discoveryClient;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            // Get the URI from the exchange attributes
            URI requestUrl = (URI) exchange.getAttributes().get(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);

            if (requestUrl != null) {
                // Get the service ID from the URI path
                log.info("serviceId" + requestUrl.getHost());
                String serviceId = requestUrl.getHost();
                // Retrieve the port information from the DiscoveryClient
                var intancse = discoveryClient.getInstances(serviceId);
                if(intancse.isEmpty())
                {
                    log.warn("intance empty" + serviceId);
                }else {


                    String serverPort = discoveryClient.getInstances(serviceId)
                            .stream()
                            .findFirst()
                            .map(instance -> instance.getMetadata().get("port"))
                            .orElse("unknown");

                    log.info("Server port: " + serverPort);
                    log.info(discoveryClient.getInstances(serviceId).toString());
                    // Add the port information to the response header
                    exchange.getResponse().getHeaders().add("Service-Port", serverPort);
                }
//                discoveryClient.getInstances(serviceId).stream()
//                        .findFirst()
//                        .ifPresent(instance -> {
//                            String port = String.valueOf(instance.getPort());
//                            exchange.getResponse().getHeaders().add("Server-Port", port);
//                        });
            }
        }));
    }
}
