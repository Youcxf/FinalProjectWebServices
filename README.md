# Web Services Final Project
Youcef Si-Ramdane & Adam Benkihlef

Library management system

- `api-gateway` on port `8080`
- `member-service` on port `8081`
- `catalog-service` on port `8082`
- `borrowing-service` on port `8083`
- `library-orchestrator` on port `8084`


- `/members` -> member service
- `/books` -> catalog service
- `/loans` -> borrowing service
- `/borrowing-decisions` -> library orchestrator



JaCoCo report command:
```powershell
.\gradlew.bat clean test jacocoTestReport
```


## Test

These are the paths of the tests
- `api-gateway/build/jacocoHtml/index.html`
- `borrowing-service/build/jacocoHtml/index.html`
- `catalog-service/build/jacocoHtml/index.html`
- `library-orchestrator/build/jacocoHtml/index.html`
- `member-service/build/jacocoHtml/index.html`


Swagger UI links:
- `http://localhost:8080/swagger-ui.html`
- `http://localhost:8081/swagger-ui.html`
- `http://localhost:8082/swagger-ui.html`
- `http://localhost:8083/swagger-ui.html`
- `http://localhost:8084/swagger-ui.html`

