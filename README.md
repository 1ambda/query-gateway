# Query Gateway

## Supported Backends

- [ ] Trino

## Features

- [ ] Query History
- [ ] Query Routing
- [ ] Configuration Reload 

## Development

* JDK 17+

```
$ npm install -g local-ssl-proxy;

# SSL for Gateway Proxy Application 
$ local-ssl-proxy --source 9090 --target 8080
$ TRINO_PASSWORD=adminpassword trino --server https://localhost:8081 --insecure --user admin --password;

# SSL for Individual Trino Cluster
$ local-ssl-proxy --source 9090 --target 8889
$ local-ssl-proxy --source 9091 --target 8890
$ TRINO_PASSWORD=adminpassword trino --server https://localhost:9090 --insecure --user admin --password;
$ TRINO_PASSWORD=adminpassword trino --server https://localhost:9091 --insecure --user admin --password;
```