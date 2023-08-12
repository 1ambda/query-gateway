package com.github.lambda.querygateway.proxy;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.Enumeration;
import org.apache.coyote.Response;
import org.springframework.cloud.gateway.mvc.ProxyExchange;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Proxy Controller", description = "")
@RestController
public class ProxyController {

  public static final String HEADER_GATEWAY_TARGET = "X-Gateway-Target";

  @RequestMapping(value = "/v1/statement/**", method = {GET, POST, DELETE})
  public ResponseEntity<?> handleStatementRequest(ProxyExchange<byte[]> proxy,
      HttpServletRequest request)
      throws Exception {

    // TODO: backend routing
    var targetHost = "https://localhost:7889";
    var targetUri = buildTargetUri(request, proxy, targetHost);
    setHeaders(request, proxy);

    var httpMethod = HttpMethod.valueOf(request.getMethod());
    return switch (httpMethod.toString()) {
      case "GET" -> proxy.sensitive().uri(targetUri).get();
      case "POST" -> proxy.sensitive().uri(targetUri).post();
      case "DELETE" -> proxy.sensitive().uri(targetUri).delete();
      default -> throw new IllegalStateException("Unexpected value: " + httpMethod);
    };
  }

  private URI buildTargetUri(HttpServletRequest request, ProxyExchange<byte[]> proxy,
      String targetHost) {
    var targetPath = proxy.path();
    var targetUri = targetHost + targetPath;

    var originQueryString = request.getQueryString();
    if (StringUtils.hasText(originQueryString)) {
      targetUri += "?" + originQueryString;
    }

    return URI.create(targetUri);
  }

  private <T> void setHeaders(HttpServletRequest from, ProxyExchange<T> to) {
    Enumeration<String> headerNames = from.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      to.header(headerName, from.getHeader(headerName));
    }
  }
}
