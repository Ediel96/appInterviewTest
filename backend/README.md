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
│   └── application.yml                   # Configuración centralizada
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

Archivo: `src/main/resources/application.yml`

Toda la configuración de la API (endpoints, validaciones, errores y cliente externo)
vive en `application.yml` y se lee con clases `@ConfigurationProperties`. No hay
URLs, límites ni mensajes de error escritos en el código.

```yaml
api:
  docs:
    title: MiniStore API
    description: API intermediaria entre la aplicación React Native y DummyJSON
    version: 1.0.0
    server-url: http://localhost:8080
    server-description: Entorno local
    endpoints:
      products-base-path: /api/products
      product-id-path: /{id}

  validation:
    product-id:
      minimum: 1
    page:
      minimum: 0
      default-value: 0
    size:
      minimum: 0      # 0 = sin límite
      maximum: 100
      default-value: 0
    search:
      min-length: 2
      max-length: 100
    category:
      min-length: 1
      max-length: 50

  errors:
    definitions:
      invalid-product-id:
        code: INVALID_PRODUCT_ID
        message: "Invalid Product ID: the value must be a positive integer"
        http-status: 400
        title: ID de producto inválido
      # ... una entrada por cada ErrorCode

clients:
  dummy-json:
    base-url: https://dummyjson.com
    paths:
      products: /products
      product-by-id: /products/{id}
    query:
      all-products-limit: 0
    timeouts:
      connect: 3s
      read: 5s
```

Las clases que enlazan estos bloques son:

| Bloque | Clase (`configuration/properties`) |
|--------|-------------------------------------|
| `api.docs` | `ApiDocsProperties` |
| `api.validation` | `ValidationRulesProperties` |
| `api.errors` | `ErrorHandlingProperties` |
| `clients.dummy-json` | `DummyJsonClientProperties` |

Para cambiar la URL de DummyJSON (por ejemplo, para testing local):
```yaml
clients:
  dummy-json:
    base-url: http://localhost:3000
```

> Los límites de `api.validation` alimentan a la vez los validadores y el esquema
> OpenAPI (`OpenApiCustomizerConfig`), por lo que la documentación y la validación
> real nunca se desincronizan.


## Comandos principales

### Ejecutar la aplicación

```bash
./gradlew bootRun
```

La aplicación arranca en `http://localhost:8080`

### Ejecutar tests

```bash
./gradlew test
```

Ejecuta todas las pruebas unitarias y de integración.

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

**Parámetros de consulta** (todos opcionales):

| Nombre | Tipo | Restricción (desde `api.validation`) |
|--------|------|----------------------------------------|
| `page` | integer | `>= 0`; por defecto `0` |
| `size` | integer | `0..100`; `0` devuelve el catálogo completo (ignora `page`) |
| `search` | string | longitud `2..100` |
| `category` | string | longitud `1..50` |

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
- `400 Bad Request`: algún parámetro viola las restricciones de `api.validation`
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

Todos los errores devuelven una respuesta consistente. El `status`, el `code` y el
`message` se resuelven desde `api.errors.definitions`, de modo que el contrato y el
comportamiento salen de la misma configuración:

```json
{
  "status": 404,
  "code": "PRODUCT_NOT_FOUND",
  "message": "Product with ID 999 not found",
  "path": "/api/products/999",
  "timestamp": "2024-09-30T10:30:00Z"
}
```

### Códigos de estado y códigos de error

| HTTP | `code` | Cuándo |
|------|--------|--------|
| 200 | — | Petición correcta |
| 400 | `INVALID_PRODUCT_ID` | El id no cumple el mínimo configurado (vía núcleo) |
| 400 | `VALIDATION_FAILED` | `page`, `size`, `search` o `category` incumplen `api.validation` |
| 400 | `TYPE_MISMATCH` | Un parámetro tiene un formato incorrecto (p. ej. `abc` como id) |
| 404 | `PRODUCT_NOT_FOUND` | El producto no existe en DummyJSON |
| 404 | `ENDPOINT_NOT_FOUND` | La ruta solicitada no existe |
| 502 | `EXTERNAL_SERVICE_ERROR` | Error al comunicarse con DummyJSON |
| 504 | `EXTERNAL_SERVICE_TIMEOUT` | Timeout con DummyJSON |
| 500 | `UNEXPECTED_ERROR` | Error inesperado no controlado |

## Testing

El proyecto incluye pruebas completas:

- **Pruebas unitarias del dominio**: Validación del modelo `Product`
- **Pruebas unitarias de mappers**: Conversión entre DTOs y dominio
- **Pruebas unitarias de servicio**: Lógica de negocio con mocks
- **Pruebas del adaptador DummyJSON**: Con `MockRestServiceServer` (sin red)
- **Pruebas del controlador web**: Con `@WebMvcTest`
- **Pruebas de binding de configuración**: `ValidationRulesPropertiesBindingTest`
- **Pruebas de validadores configurables**: `ConstraintValidatorsTest`
- **Pruebas del catálogo de errores**: `ErrorHandlingPropertiesTest`
- **Pruebas del contrato OpenAPI**: Incluye que los límites publicados coincidan con `api.validation`

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

Cambiar puerto en `application.yml`:
```yaml
server:
  port: 8081
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
