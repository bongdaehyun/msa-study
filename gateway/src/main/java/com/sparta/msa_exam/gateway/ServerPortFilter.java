package com.sparta.msa_exam.gateway;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import java.util.List;

/**
 * gateway 단에서 response 헤더에 server port 추가
 * 1. 요청이 들어오면 route를 이용하여 해당 서비스를 호출
 * 2. 사용한 route의 service id를 추출
 * 3. eureka instance의 정보들에서 server port추출
 * 4. response header에 server port 추가
 *
 **/
@RequiredArgsConstructor
@Slf4j
@Component
public class ServerPortFilter implements GlobalFilter {

    //eureka client들의 인스턴스 정보들에 접근 가능
    private final DiscoveryClient discoveryClient;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            //ServerWebExchangeUtils : 주로 필터나 라우팅 관련 속성에 접근하는 메서드를 제공
            Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
            if (route != null) {
                String serviceId = route.getId();
                log.info("serviceId : {}", serviceId);

                List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
                if (!instances.isEmpty()) {
                    ServiceInstance instance = instances.get(0);
                    String port = String.valueOf(instance.getPort());
                    log.info("Service: {}, Host: {}, Port: {}", serviceId, instance.getHost(), port);
                    exchange.getResponse().getHeaders().add("Server-Port", port);
                } else {
                    log.warn("NOT FOUND INSTANCES {}", serviceId);
                }
            } else {
                log.warn("NOT FOUND ROUTE IN GATEWAY");
            }
        }));
    }


}
