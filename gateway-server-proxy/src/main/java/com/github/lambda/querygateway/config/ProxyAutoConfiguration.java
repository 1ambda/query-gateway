package com.github.lambda.querygateway.config;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.gateway.mvc.config.ProxyExchangeArgumentResolver;
import org.springframework.cloud.gateway.mvc.config.ProxyProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ProxyProperties.class)
@Slf4j
public class ProxyAutoConfiguration implements WebMvcConfigurer {

  @Autowired
  private ApplicationContext context;

  @Bean
  @ConditionalOnMissingBean
  public ProxyExchangeArgumentResolver proxyExchangeArgumentResolver(
      Optional<RestTemplateBuilder> optional,
      ProxyProperties proxy) {

    RestTemplateBuilder builder = optional.orElse(new RestTemplateBuilder());
    RestTemplate template = builder.build();

    template.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    template.setErrorHandler(new NoOpResponseErrorHandler());
    template.getMessageConverters().add(new ByteArrayHttpMessageConverter() {
      @Override
      public boolean supports(Class<?> clazz) {
        return true;
      }
    });

    ProxyExchangeArgumentResolver resolver = new ProxyExchangeArgumentResolver(template);
    resolver.setHeaders(proxy.convertHeaders());
    resolver.setAutoForwardedHeaders(proxy.getAutoForward());
    resolver.setSensitive(proxy.getSensitive()); // can be null
    return resolver;
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
    argumentResolvers.add(context.getBean(ProxyExchangeArgumentResolver.class));
  }

  private static class NoOpResponseErrorHandler extends DefaultResponseErrorHandler {

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
    }

  }

}
