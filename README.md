# ms-app-medica-historial-bs

Capa **BS** (Business Service) del módulo **Historial**. Consolida el historial clínico haciendo **fan-out** a otros dos módulos: consulta `pacientes-bs` y `citas-bs` para enriquecer cada registro antes de persistirlo vía `ms-app-medica-historial-db`.

| | |
|---|---|
| **Puerto** | `9091` |
| **Patrón** | Controller → Service → Client (Feign) |
| **Ruta base** | `/api/v1/historiales` |
| **Llama a** | `historial-db` (8095) · `pacientes-bs` (8081) · `citas-bs` (8091) |
| **Pruebas** | `HistorialServiceTest` (JUnit 5 + Mockito) |
| **Swagger** | `http://localhost:9091/swagger-ui.html` |

Manejo de errores remotos: `FeignException.NotFound` → `404`; otros fallos remotos → `502 ServicioNoDisponibleException`.

## Ejecución

```bash
# Con todo el ecosistema (recomendado), desde app-medica-et-fullstack-1/
docker compose up --build

# Individual
./mvnw spring-boot:run     # mvnw.cmd en Windows
./mvnw test
```
