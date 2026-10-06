# SonarQube local

Esta configuración levanta SonarQube Community y PostgreSQL únicamente para
desarrollo local. El código, los tests y la cobertura se analizan con el plugin
oficial de Gradle; el token nunca se guarda en el repositorio. La imagen de
SonarQube está fijada a una versión concreta para que el entorno sea repetible.

## 1. Iniciar SonarQube

Desde `backend/`:

```bash
docker compose --profile sonar up -d sonar-db sonarqube
docker compose --profile sonar ps
```

SonarQube tarda uno o dos minutos en quedar disponible. Se puede seguir el
arranque con:

```bash
docker compose --profile sonar logs -f sonarqube
```

Abrir <http://localhost:9000>. En una instalación nueva las credenciales
iniciales son `admin` / `admin`; SonarQube obliga a cambiar la contraseña.

## 2. Crear un token y analizar

En SonarQube, ir a **My Account > Security**, crear un token y exportarlo sólo
en la terminal actual:

```bash
export SONAR_TOKEN="reemplazar-con-token-local"
./gradlew clean test jacocoTestReport sonar -Dsonar.token="$SONAR_TOKEN"
```

El proyecto aparecerá con la clave `ministore-backend`. El reporte HTML local
de JaCoCo queda en `build/reports/jacoco/test/html/index.html` y el XML que
consume SonarQube en `build/reports/jacoco/test/jacocoTestReport.xml`.

Para usar otra instancia:

```bash
SONAR_HOST_URL="https://sonar.example.com" \
  ./gradlew clean sonar -Dsonar.token="$SONAR_TOKEN"
```

## 3. Detener

```bash
docker compose --profile sonar down
```

Los volúmenes conservan proyectos y configuración. Para reiniciar SonarQube
desde cero se pueden eliminar explícitamente con `docker compose --profile
sonar down -v`; este último comando borra todos los datos locales de SonarQube.

## Alcance de cobertura

JaCoCo mide el código productivo. Se excluyen de la métrica de cobertura el
punto de arranque, clases de configuración y DTOs, pero siguen formando parte
del análisis estático. Los artefactos generados bajo `build/` se excluyen del
análisis.
