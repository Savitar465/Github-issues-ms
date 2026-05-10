# Guía de Contribuciones - Github-issues-ms

Gracias por tu interés en contribuir a `Github-issues-ms`. Este documento define el proceso, estándares y expectativas.

## Tabla de contenidos
- [Código de conducta](#código-de-conducta)
- [Cómo empezar](#cómo-empezar)
- [Tipos de contribuciones](#tipos-de-contribuciones)
- [Proceso de desarrollo](#proceso-de-desarrollo)
- [Estándares de código](#estándares-de-código)
- [Pull Request](#pull-request)
- [Revisión de código](#revisión-de-código)

## Código de conducta
- Ser respetuoso y profesional.
- Reportar bugs constructivamente.
- Colaborar para resolver conflictos.
- Enfoque en el beneficio del proyecto.

## Cómo empezar

### 1. Fork y clona
```bash
git clone https://github.com/<tu-usuario>/Github-issues-ms.git
cd Github-issues-ms
git remote add upstream https://github.com/<org>/Github-issues-ms.git
```

### 2. Crea rama de trabajo
```bash
git checkout -b feat/descripcion-breve
# o
git checkout -b fix/descripcion-breve
```

**Convención de nombres**:
- `feat/<descripcion>` - nueva funcionalidad
- `fix/<descripcion>` - corrección de bug
- `refactor/<descripcion>` - refactorización
- `docs/<descripcion>` - cambios de documentación
- `test/<descripcion>` - adición/mejora de tests

### 3. Configura local
```bash
./mvnw clean install
```

## Tipos de contribuciones

### Bugs (reportar)
Incluir:
- Descripción clara del problema.
- Pasos para reproducir.
- Comportamiento esperado vs. actual.
- Logs/evidencia.
- Ambiente (versión, entorno).

### Features (proponer)
Incluir:
- Objetivo y necesidad.
- Alcance técnico.
- Criterios de aceptación.
- Riesgos y dependencias.

## Proceso de desarrollo

### Fase 1: Análisis
1. Leer `docs/ARCHITECTURE.md` y `docs/CODE-RULES.md`.
2. Entender el cambio: ¿afecta seguridad, contratos API, otros servicios?

### Fase 2: Implementación
1. Respetar arquitectura:
   ```
   controller -> service (contrato + impl) -> dao -> model -> mapper -> dto
   ```
2. Nomenclatura estándar.
3. Validaciones con Bean Validation.
4. Tests unitarios e integración.

### Fase 3: Calidad
1. Compilar sin errores: `./mvnw clean compile -q`
2. Pasar todos los tests: `./mvnw test -q`
3. Sin secretos/credenciales en código.
4. Documentar si cambia API.

## Estándares de código

### Java/Spring
- Java 17+, modern syntax.
- Sin wildcard imports.

### Arquitectura
- DTO en respuestas: nunca exponer entidades JPA.
- Mapper: usar MapStruct (no manual).
- Excepciones: custom exceptions, no genéricas.

### Tests
```java
// Estructura AAA (Arrange-Act-Assert)
@Test
void debeCrearIssueCorrectamente() {
    // Arrange
    // Act
    // Assert
}
```

## Pull Request

### Checklist de PR (obligatorio)
- [ ] **Compilación**: `mvnw clean package`
- [ ] **Tests**: `mvnw test` - todos pasan
- [ ] **Arquitectura**: respeta capas, naming, contratos
- [ ] **DTOs**: consistentes con spec OpenAPI
- [ ] **Validaciones**: Bean Validation o manejo de errores
- [ ] **Documentación**: OpenAPI, README si aplica
- [ ] **Seguridad**: sin secretos, sin datos sensibles en logs
- [ ] **Sin TODOs**: código completado, no parcial

## Revisión de código

### Para reviewers
**Do**:
- Revisar arquitectura primero (capas, separación).
- Verificar seguridad (secretos, validaciones).
- Pedir tests y casos borde.
- Confirmar DTOs + mappers.

**Don't**:
- No aprobar sin contexto.
- No aceptar secretos en código.
- No ignorar logs insuficientes.

---

**Gracias por contribuir a mejorar Github-issues-ms** 🚀
