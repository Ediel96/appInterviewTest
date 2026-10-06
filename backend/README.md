# MiniStore API - Backend

Microservicio Spring Boot que actúa como intermediario entre la aplicación móvil React Native y la API externa DummyJSON, proporcionando un catálogo de productos.

## Objetivo

Este microservicio backend sirve como capa de abstracción entre la aplicación móvil y servicios externos. Expone endpoints REST documentados con OpenAPI para que la aplicación React Native pueda consultar productos sin depender directamente de APIs externas.

## Flujo de datos

```
React Native App → Spring Boot Backend → DummyJSON API
                ←                      ←
```

La aplicación móvil **no llama directamente a DummyJSON**. Todas las peticiones pasan por este backend, que se encarga de:
- Consultar la API de DummyJSON
- Transformar respuestas al modelo de dominio
- Exponer un contrato estable y documentado
- Manejar errores de manera consistente

**Nota importante**: El backend **no persiste favoritos** ni ningún otro dato. Es completamente stateless y delega la persistencia de favoritos a la aplicación móvil.

## Stack tecnológico

- **Spring Boot**: 3.5.16
- **Java**: 21
- **Gradle**: 9.7.1
- **Gradle Wrapper**: incluido
- **Springdoc OpenAPI**: 2.9.1
- **JUnit**: 5
- **Mockito**: integrado con Spring Boot

## Arquitectura

El proyecto sigue una **arquitectura hexagonal (puertos y adaptadores)**:

```
backend/
├── src/main/java/com/backend/hamilton/
│   ├── domain/                          # Capa de dominio (sin dependencias externas)
│   │   ├── model/
│   │   │   └── Product.java            # Modelo del dominio
│   │   └── exception/                   # Excepciones de negocio
│   │       ├── InvalidProductIdException.java
│   │       ├── ProductNotFoundException.java
│   │       ├── ExternalServiceException.java
│   │       └── ExternalServiceTimeoutException.java
│   │
│   ├── application/                     # Capa de aplicación (casos de uso)
│   │   ├── port/
│   │   │   ├── in/                      # Puertos de entrada
│   │   │   │   ├── GetProductsUseCase.java
│   │   │   │   └── GetProductByIdUseCase.java
│   │   │   └── out/                     # Puertos de salida
│   │   │       └── ProductCatalogPort.java
│   │   ├── service/
│   │   │   └── ProductService.java      # Implementación de casos de uso
│   │   └── config/
│   │       └── ApplicationConfig.java
│   │
│   ├── adapter/                         # Adaptadores (infraestructura)
│   │   ├── in/web/                      # Adaptador de entrada HTTP
│   │   │   ├── ProductController.java
│   │   │   ├── ProductWebMapper.java
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   └── dto/
│   │   │       ├── ProductResponse.java
│   │   │       ├── ProductListResponse.java
│   │   │       └── ApiErrorResponse.java
│   │   └── out/dummyjson/               # Adaptador de salida DummyJSON
│   │       ├── DummyJsonProductAdapter.java
│   │       ├── DummyJsonProductDto.java
│   │       ├── DummyJsonProductsResponse.java
│   │       ├── DummyJsonProductMapper.java
│   │       └── config/
│   │           └── DummyJsonAdapterConfig.java
│   │
│   ├── configuration/                   # Configuración general
│   │   └── OpenApiConfiguration.java
│   │
│   └── HamiltonApplication.java         # Punto de entrada
│
├── src/main/resources/
│   └── application.properties           # Configuración de la aplicación
│
└── src/test/java/                       # Tests unitarios e integración
    └── com/backend/hamilton/
        ├── domain/model/
        ├── application/service/
        ├── adapter/in/web/
        ├── adapter/out/dummyjson/
        └── configuration/
```

## Dependencias principales

- `spring-boot-starter-web`: Framework web REST
- `spring-boot-starter-validation`: Validación de datos
- `spring-boot-starter-actuator`: Health checks y métricas
- `springdoc-openapi-starter-webmvc-ui`: Documentación OpenAPI/Swagger
- `spring-boot-starter-test`: Testing con JUnit y Mockito

## Configuración

### Variables de entorno / Properties

Archivo: `src/main/resources/application.properties`

```properties
# DummyJSON API
dummyjson.api.base-url=https://dummyjson.com
dummyjson.api.timeout=10000

# OpenAPI Documentation
openapi.title=MiniStore API
openapi.description=Product catalog API powered by DummyJSON
openapi.version=1.0.0
openapi.server-url=http://localhost:8080
openapi.server-description=Development server
```

Para cambiar la URL de DummyJSON (por ejemplo, para testing local):
```properties
dummyjson.api.base-url=http://localhost:3000
```

## Comandos principales

### Ejecutar la aplicación

```bash
./gradlew bootRun
```

La aplicación arranca en `http://localhost:8080`

### Ejecutar con Docker

Desde el directorio `backend/`, construir la imagen e iniciar el servicio:

```bash
docker compose up --build
```

La API estará disponible en `http://localhost:8080`; Swagger UI, en
`http://localhost:8080/swagger-ui.html`. Para detener el servicio, usa
`Ctrl+C`; si lo iniciaste en segundo plano con `docker compose up --build -d`,
ejecuta `docker compose down`.

La URL de DummyJSON se puede cambiar al iniciar el contenedor:

```bash
DUMMYJSON_API_BASE_URL=https://dummyjson.com docker compose up --build
```

### Infraestructura AWS

La infraestructura Terraform de staging y producción está junto al backend en
[`infra/terraform/`](infra/terraform/). La guía incluye el bootstrap del estado
remoto, publicación inicial en ECR, planificación y validación:

```bash
cd backend/infra/terraform/environments/staging
terraform init -backend-config=backend.hcl
terraform validate
```

### Despliegue continuo

Actualmente no hay workflows CI/CD versionados: el build y el despliegue se
ejecutan manualmente con los comandos de esta sección y de la
[guía Terraform](infra/terraform/README.md). El flujo propuesto para cada push o
pull request es:

```text
push/PR → tests Gradle → JaCoCo → Sonar/Quality Gate → imagen Docker
        → tag inmutable en ECR → Terraform staging → health check
        → aprobación manual → promoción de la misma imagen a producción
```

Las pruebas y sus reportes se ejecutan en CI y SonarQube; **no se suben a AWS**.
AWS recibe únicamente la imagen aprobada y la infraestructura declarada. La
imagen se construye una sola vez: tras validar staging, se copia el mismo digest
o tag inmutable al ECR de producción, sin recompilar, y se actualiza `image_tag`
en el plan de producción.

Comandos esenciales, ejecutados desde `backend/`:

```bash
./gradlew clean test jacocoTestReport sonar \
  -Dsonar.token="$SONAR_TOKEN" -Dsonar.qualitygate.wait=true

docker build --platform linux/amd64 -t "$ECR_URL:$IMAGE_TAG" .
docker push "$ECR_URL:$IMAGE_TAG"

cd infra/terraform/environments/staging
terraform init -backend-config=backend.hcl
terraform plan -var-file=terraform.tfvars -out=deployment.tfplan
terraform apply deployment.tfplan
curl "$(terraform output -raw application_url)/actuator/health"
```

Un futuro workflow de GitHub Actions debe vivir en `.github/workflows/` en la
raíz del repositorio, aunque Terraform esté dentro de `backend/`. El job de
staging se ejecutaría automáticamente sólo después del Quality Gate; producción
usaría un environment protegido con aprobación manual. CI debe autenticarse en
AWS mediante OIDC y roles temporales de mínimo privilegio, nunca con access keys
permanentes guardadas como secretos.

### Ejecutar tests

```bash
./gradlew test
```

Ejecuta todas las pruebas unitarias y de integración.

### Cobertura y SonarQube local

El proyecto genera cobertura JaCoCo y puede analizarse contra SonarQube local:

```bash
docker compose --profile sonar up -d sonar-db sonarqube
export SONAR_TOKEN="token-creado-en-http://localhost:9000"
./gradlew clean test jacocoTestReport sonar -Dsonar.token="$SONAR_TOKEN"
```

La guía completa, incluyendo arranque, credenciales iniciales, configuración y
limpieza, está en [SONARQUBE.md](SONARQUBE.md).

### Compilar y construir

```bash
./gradlew clean build
```

Compila el código, ejecuta tests y genera el JAR en `build/libs/`.

### Generar documentación OpenAPI

```bash
./gradlew generateOpenApiDocs
```

Genera el archivo `build/docs/openapi.json` con la especificación completa de la API.

**Nota**: La aplicación debe estar corriendo en `http://localhost:8080` para que la generación funcione.

## Endpoints

### Productos

#### Listar todos los productos

```
GET /api/products
```

**Respuesta exitosa (200)**:
```json
{
  "products": [
    {
      "id": 1,
      "title": "Essence Mascara Lash Princess",
      "description": "The Essence Mascara Lash Princess is a popular mascara...",
      "price": 9.99,
      "rating": 2.56,
      "thumbnail": "https://cdn.dummyjson.com/product-images/1/thumbnail.jpg",
      "images": [
        "https://cdn.dummyjson.com/product-images/1/1.jpg"
      ],
      "category": "beauty",
      "brand": "Essence"
    }
  ],
  "total": 194
}
```

**Errores posibles**:
- `502 Bad Gateway`: Error al comunicarse con DummyJSON
- `504 Gateway Timeout`: Timeout al llamar a DummyJSON

#### Obtener producto por ID

```
GET /api/products/{id}
```

**Parámetros**:
- `id` (path, requerido): ID del producto (debe ser > 0)

**Respuesta exitosa (200)**:
```json
{
  "id": 1,
  "title": "Essence Mascara Lash Princess",
  "description": "The Essence Mascara Lash Princess is a popular mascara...",
  "price": 9.99,
  "rating": 2.56,
  "thumbnail": "https://cdn.dummyjson.com/product-images/1/thumbnail.jpg",
  "images": [
    "https://cdn.dummyjson.com/product-images/1/1.jpg"
  ],
  "category": "beauty",
  "brand": "Essence"
}
```

**Errores posibles**:
- `400 Bad Request`: ID inválido (null, 0 o negativo)
- `404 Not Found`: Producto no encontrado
- `502 Bad Gateway`: Error al comunicarse con DummyJSON
- `504 Gateway Timeout`: Timeout al llamar a DummyJSON

### Health Check

```
GET /actuator/health
```

**Respuesta**:
```json
{
  "status": "UP"
}
```

### Documentación

#### Swagger UI

```
GET /swagger-ui.html
```

Interfaz interactiva para explorar y probar la API.

**URL**: http://localhost:8080/swagger-ui.html

#### OpenAPI JSON

```
GET /v3/api-docs
```

Especificación OpenAPI en formato JSON.

**URL**: http://localhost:8080/v3/api-docs

#### OpenAPI YAML

```
GET /v3/api-docs.yaml
```

Especificación OpenAPI en formato YAML.

**URL**: http://localhost:8080/v3/api-docs.yaml

#### OpenAPI generado

Después de ejecutar `./gradlew generateOpenApiDocs`, el archivo se encuentra en:

```
build/docs/openapi.json
```

## Formato de errores

Todos los errores devuelven una respuesta consistente:

```json
{
  "status": 404,
  "message": "Product with ID 999 not found",
  "timestamp": "2024-09-30T10:30:00Z"
}
```

### Códigos de estado

| Código | Significado | Ejemplo |
|--------|-------------|---------|
| 200 | OK | Producto encontrado |
| 400 | Bad Request | ID inválido o malformado |
| 404 | Not Found | Producto no existe |
| 502 | Bad Gateway | Error en DummyJSON |
| 504 | Gateway Timeout | Timeout con DummyJSON |

## Testing

El proyecto incluye pruebas completas:

- **Pruebas unitarias del dominio**: Validación del modelo `Product`
- **Pruebas unitarias de mappers**: Conversión entre DTOs y dominio
- **Pruebas unitarias de servicio**: Lógica de negocio con mocks
- **Pruebas del adaptador DummyJSON**: Con `MockRestServiceServer` (sin red)
- **Pruebas del controlador web**: Con `@WebMvcTest`
- **Pruebas del contrato OpenAPI**: Validación de documentación

Todas las pruebas son **rápidas, deterministas y sin dependencias externas**.

```bash
# Ejecutar solo tests unitarios de dominio
./gradlew test --tests "com.backend.hamilton.domain.*"

# Ejecutar tests de un adaptador específico
./gradlew test --tests "com.backend.hamilton.adapter.out.dummyjson.*"
```

## Desarrollo

### Requisitos

- Java 21 o superior
- Gradle (incluido wrapper)

### Verificar instalación de Java

```bash
java -version
# Debería mostrar: openjdk version "21.x.x"
```

### Compilar sin ejecutar tests

```bash
./gradlew build -x test
```

### Ver todas las tareas disponibles

```bash
./gradlew tasks
```

## Notas importantes

1. **El backend no persiste datos**: Es completamente stateless. Los favoritos y preferencias se gestionan en la app móvil.

2. **La app móvil no llama a DummyJSON directamente**: Todas las peticiones pasan por este backend para mantener un contrato estable.

3. **Arquitectura hexagonal**: El dominio no conoce detalles de infraestructura (Spring, DummyJSON, HTTP).

4. **Sin autenticación**: Este es un backend de demostración. En producción se requeriría autenticación.

5. **DummyJSON público**: La API de DummyJSON es pública y no requiere API keys.

## Troubleshooting

### Error: "Cannot connect to DummyJSON"

Verificar conectividad:
```bash
curl https://dummyjson.com/products/1
```

### Error: "Port 8080 already in use"

Cambiar puerto en `application.properties`:
```properties
server.port=8081
```

### Tests fallan

Verificar que no haya una instancia corriendo:
```bash
./gradlew --stop
./gradlew clean test
```

## Contribuir

1. El código sigue arquitectura hexagonal estricta
2. Todos los cambios deben incluir tests
3. Los commits siguen Conventional Commits
4. El dominio no debe importar clases de Spring

## Licencia

Proyecto de demostración para MiniStore.
