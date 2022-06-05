# auth
Token service for authentication, support Redis, database, memory and JWT Token.

Designed for Spring Boot projects.
## Maven
```xml
<dependency>
    <groupId>cn.spark2fire.alberti</groupId>
    <artifactId>auth-jwt</artifactId>
    <version>0.0.7</version>
</dependency>
```
## Support Types
### Redis
Store the token in Redis server.
### Database
Store the token in database table.
### Memory
Default
### JWT
JWT Token store in client.
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
Authorization: {{TOKEN}}
```
### Get Principal
```http request
GET /accounts/principal
Authorization: {{TOKEN}}
```
## Token Usage
Add `token` in Header or Parameter `Authorization`.
# Configuration
```yaml
cn:
  spark2fire:
    auth:
      idle-timeout: 30
      header-name: "Authorization"
      table-name: "t_token"
      redis-prefix: "TOKEN:a:"
      jwt:
        secretKey: ""
        issuer: "spark2fire"
        subject: "auth0"
        audience: "web"
```
## Example
```kotlin
package cn.spark2fire.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ExampleApplication

fun main(args: Array<String>) {
    runApplication<ExampleApplication>(*args)
}
```
