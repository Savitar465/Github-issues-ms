# Guia de codificacion - Github-issues-ms

## Objetivo
Estandarizar como realizar codigo en `Github-issues-ms` para mantener orden, consistencia y calidad.

## Alcance
Aplica a todo codigo nuevo en:
- `controller`
- `service/contratos`
- `service/implementacion`
- `dao`
- `model`
- `mapper`
- `util/errorhandling`
- `test`

## Arquitectura obligatoria
Flujo obligatorio:

```text
controller -> service (contrato + impl) -> dao -> model -> mapper -> dto (generado)
```

Reglas:
- `controller` no contiene logica de negocio compleja.
- `service` concentra reglas de negocio y orquestacion.
- `dao` solo acceso a datos.
- `mapper` (MapStruct) hace conversion entidad <-> DTO.
- Nunca devolver entidades JPA desde endpoints.
- El `repoId` siempre se construye como `owner + "/" + repo`.

## Reglas de realizar codigo

### 1) Estructura y nombres
- Crear clases con convencion:
  - `*Controller`
  - `*Service` (interface)
  - `*ServiceImpl` (implementacion)
  - `*Dao`
  - `*Mapper`
- Mantener paquetes por capa sin mezclar responsabilidades.

### 2) Endpoints REST
- Versionado obligatorio: `/v1/repos/{owner}/{repo}/<recurso>`.
- Usar `ResponseEntity` con codigos HTTP explicitos.
- Aplicar `@Valid` en request body.

### 3) DTOs
- Los DTOs son generados por OpenAPI Generator desde `src/main/openapi/IssueApi.json`.
- No crear DTOs manuales que dupliquen los generados.
- Usar Bean Validation si se crean request bodies adicionales.

### 4) Mapper
- Usar MapStruct (`@Mapper(componentModel = "spring")`).
- Toda conversion entidad/DTO debe pasar por mapper.
- Usar `expression = "java(...)"` para transformaciones de tipo (UUID->String, Instant->String, enum).

### 5) Manejo de errores
- Usar excepciones de negocio especificas en `util/errorhandling`.
- Centralizar respuestas de error en `GlobalExceptionHandler`.
- No usar `Exception` generica para casos de negocio.

### 6) Seguridad
- No hardcodear secretos, tokens, passwords o credenciales.
- No loggear datos sensibles.
- Usar `AuthenticatedUserResolver` para obtener el usuario actual del JWT.

### 7) Logging
- Usar logs estructurados con contexto.
- Evitar `System.out.println`.
- Mensajes de log utiles: recurso afectado, numero de issue o ID, usuario.

### 8) Testing minimo
Para cada modulo nuevo, incluir como minimo:
- Test de controller (ruta feliz y al menos un caso de error 404).
- Test de service (logica de negocio principal y caso de excepcion).

## Definition of Done (DoD) para codigo
Antes de entregar codigo, validar:
- [ ] Respeta arquitectura por capas.
- [ ] Endpoints bajo `/v1/repos/{owner}/{repo}/<recurso>`.
- [ ] DTOs generados o consistentes con el spec OpenAPI.
- [ ] Mapper MapStruct implementado.
- [ ] Manejo de errores consistente.
- [ ] Sin secretos hardcodeados.
- [ ] Tests minimos incluidos.
- [ ] Compila y tests pasan.
