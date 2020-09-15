# auth
Token service for authentication, support Redis, database, memory and JWT Token.

Designed for Spring Boot projects.
##Maven
```xml
<dependency>
    <groupId>cn.har01d</groupId>
    <artifactId>auth</artifactId>
    <version>0.0.1</version>
</dependency>
```
## Support Types
### Redis
`@EnableRedisToken`
### Database
`@EnableDatabaseToken`
### Memory
Default
### JWT
`@EnableJwtToken`
## Endpoints
### Login
```http request
POST /accounts/login
{
	"username": "user",
	"password": "bf3916a0-a7e0-4177-9b74-fa5b0b7e438f",
	"rememberMe": false
}
```
### Logout
```http request
POST /accounts/logout
X-ACCESS-TOKEN: {{TOKEN}}
```
### Get Principal
```http request
GET /accounts/principal
X-ACCESS-TOKEN: {{TOKEN}}
```
## Token Usage
Add `token` in Header or Parameter `X-ACCESS-TOKEN`.
# Configuration
```properties
cn.har01d.auth.idle-timeout=30
cn.har01d.auth.header-name="X-ACCESS-TOKEN"
cn.har01d.auth.table-name=""t_token
cn.har01d.auth.redis-prefix="TOKEN:a:"
cn.har01d.auth.jwt.secretKey=
cn.har01d.auth.jwt.issuer="Har01d"
cn.har01d.auth.jwt.subject="auth0"
cn.har01d.auth.jwt.audience="web"
```
## Example
```kotlin
package cn.har01d.example

import cn.har01d.auth.annotation.EnableJwtToken
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableJwtToken
class ExampleApplication

fun main(args: Array<String>) {
    runApplication<ExampleApplication>(*args)
}
```
