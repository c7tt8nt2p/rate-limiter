# Rate Limiter

This rate limiter is implemented based on simple **Leaky Bucket** algorithm with configurable requests limit.
The APIs will reject requests on a specific endpoint by returning **429 Too Many Requests**.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine.

### Prerequisites

* JDK11 (see [https://adoptopenjdk.net](https://adoptopenjdk.net))

### Installing

```
mvnw clean package
```

### Running the tests

```
mvnw clean test
```

### Running

Available configurations
```
endpoint.city.requests.limit.every.5.seconds
endpoint.room.requests.limit.every.10.seconds
endpoint.any.requests.limit.per.10.seconds
```

Simple startup
```
mvnw clean spring-boot:run -Dspring.profiles.active=production
```

Startup with custom rate configuration, e.g.
```
mvnw clean spring-boot:run -Dspring.profiles.active=production -Dendpoint.city.requests.limit.every.5.seconds=5 -Dendpoint.room.requests.limit.every.10.seconds=10
```

Example endpoints:

```
http://localhost:8080/api/v1/hotels/city?city=Bangkok
http://localhost:8080/api/v1/hotels/city?city=Bangkok&priceSorting=ASC
http://localhost:8080/api/v1/hotels/city?city=Bangkok&priceSorting=DESC

http://localhost:8080/api/v1/hotels/room?room=Superior
http://localhost:8080/api/v1/hotels/room?room=Superior&priceSorting=ASC
http://localhost:8080/api/v1/hotels/room?room=Superior&priceSorting=DESC
```

## Built With

* [Spring Boot](https://spring.io/projects/spring-boot/)
* [Maven](https://maven.apache.org/)

## Authors

* **Chantapat Tancharoen** - *Initial work* - [GitHub](https://github.com/c7tt8nt2p)
