# Plantilla estandar de estructura y arquitectura para microservicios

## 1) Objetivo
Este documento define un formato base reusable para construir microservicios con arquitectura por capas.
Sirve como guia de implementacion para equipos de desarrollo y como contexto para generacion asistida por IA.

## 2) Stack de referencia (adaptable)
- Java 17+
- Spring Boot 3.x
- Spring Data JPA
- Spring Security (OAuth2 Resource Server con JWT)
- OpenAPI (springdoc)
- MapStruct + Lombok
- Base de datos relacional (PostgreSQL)
- Docker + Kubernetes

> Nota: este stack es una base sugerida. Ajustar segun el contexto del dominio y requerimientos no funcionales.

## 3) Estructura del microservicio Github-issues-ms

```text
src/
  main/
    java/com/githubx/Github_issues_ms/
      config/
        security/
      controller/
        IssueController.java
        IssueCommentController.java
        LabelController.java
      dao/
        IssueDao.java
        IssueCommentDao.java
        LabelDao.java
      mapper/
        IssueMapper.java
        IssueCommentMapper.java
      model/
        Issue.java
        IssueComment.java
        IssueState.java
        Label.java
      service/
        contratos/
          IssueService.java
          IssueCommentService.java
          LabelService.java
        implementacion/
          IssueServiceImpl.java
          IssueCommentServiceImpl.java
          LabelServiceImpl.java
      util/
        errorhandling/
    resources/
      application.yaml
  test/
    java/com/githubx/Github_issues_ms/
      controller/
        IssueControllerTest.java
      service/
        IssueServiceTest.java
model/
  common/
    common.smithy
  issue/
    issue.smithy
    operations/
      issue-operations.smithy
    services/
      issue-services.smithy
    types/
      issue-types.smithy
```

## 4) Arquitectura aplicada

### 4.1 Capas
- `controller`: expone endpoints REST, valida entrada y delega al servicio.
- `service/contratos`: define interfaces de negocio.
- `service/implementacion`: implementa reglas de negocio y orquestacion.
- `dao`: acceso a datos con repositorios Spring Data.
- `model`: entidades del dominio persistente.
- `mapper`: transforma entidad <-> DTO (MapStruct).
- `config`: configuraciones transversales (security, openapi).
- `util/errorhandling`: manejo estandarizado de excepciones.

### 4.2 Flujo canonico de una peticion
1. `Controller` recibe request y aplica validaciones.
2. `Service` evalua reglas de negocio.
3. `DAO` persiste o consulta datos.
4. `Mapper` transforma a DTO de respuesta.

### 4.3 Dominio del microservicio

Este microservicio gestiona tres recursos principales:

| Recurso       | Descripcion                                    | Endpoints base                              |
|---------------|------------------------------------------------|---------------------------------------------|
| `Issue`       | Tickets de problemas/tareas en un repositorio  | `/v1/repos/{owner}/{repo}/issues`           |
| `IssueComment`| Comentarios en issues                          | `/v1/repos/{owner}/{repo}/issues/{n}/comments` |
| `Label`       | Etiquetas para categorizar issues              | `/v1/repos/{owner}/{repo}/labels`           |

### 4.4 Seguridad
- API stateless con JWT.
- Extraccion de `userId` y `username` del token via `AuthenticatedUserResolver`.
- Conversion de claims del token a authorities en `JwtAuthConverter`.

### 4.5 Persistencia
- Un datasource PostgreSQL.
- IDs generados con UUID v4.
- `repoId` se construye como `{owner}/{repo}` para aislar recursos por repositorio.
- `comments_count` en `issues` se mantiene actualizado via triggers en BD.

## 5) Convenciones

### 5.1 Nomenclatura
- `*Controller`
- `*Service` (interface)
- `*ServiceImpl` (implementacion)
- `*Dao`
- `*Mapper`

### 5.2 Endpoints
- Versionado obligatorio: `/v1/<recurso>`.
- Usar `ResponseEntity` con codigos HTTP explicitos.
- Validaciones con Bean Validation (`@Valid`, `@NotNull`, `@Size`, etc.).

### 5.3 Manejo de errores
- Excepciones de negocio especificas (`EntityNotFoundException`, `EntityConflictException`, `ForbiddenOperationException`).
- Manejo centralizado con `@RestControllerAdvice` en `GlobalExceptionHandler`.
- Error response estable y consistente para consumidores.

### 5.4 DTO y mapeo
- Los DTOs son generados por el plugin `openapi-generator-maven-plugin` a partir de `src/main/openapi/IssueApi.json`.
- Nunca exponer entidades JPA directamente en la API.
- Centralizar conversiones en MapStruct.

## 6) Checklist de calidad antes de merge
- Compila y pasa tests.
- Endpoints documentados en OpenAPI.
- Validaciones de entrada completas.
- DTO + Mapper implementados (sin exponer entidades).
- Excepciones de negocio correctamente mapeadas.
- Sin secretos en codigo ni YAML versionado.
- Logging y trazabilidad adecuados.
