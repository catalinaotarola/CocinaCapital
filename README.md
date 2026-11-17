# Cocina Capital

Aplicación Android que conecta a las personas con promociones gastronómicas de locales de Concón (Región de Valparaíso). Recomienda promociones según los gustos, la ubicación y las fechas especiales de cada usuario, entrega cupones con código QR, permite a los locales validarlos desde su propio panel y muestra reseñas y locales destacados calculados por un microservicio propio.

Proyecto de la asignatura **DSY1105 · Desarrollo de Aplicaciones Móviles** (Duoc UC).

## Integrantes

| Integrante | Rol principal en el proyecto |
|---|---|
| Catalina Otárola | App Android (pantallas, ViewModels, Retrofit), pruebas unitarias, firma del APK |
| Catalina Reyes | Microservicios Spring Boot, base de datos, pruebas de endpoints, tablero Trello |

Tablero Trello: `PEGAR AQUÍ EL LINK DEL TABLERO`

## Funcionalidades

- **Cuenta y privacidad:** registro, inicio de sesión, sesión recordada con DataStore y eliminación de cuenta.
- **Perfil y preferencias:** categorías favoritas, presupuesto, restricciones y radio de cercanía.
- **Promociones:** listado con filtros y buscador. Cada promoción tiene días de la semana, horario, cupos, ticket de prepago y ocasión.
- **Cupones:** reserva por tiempo limitado, código QR y botón para copiar el código (portapapeles).
- **Fechas especiales:** avisos 15, 8 y 1 día antes de la fecha, con 50 %, 35 % y 20 % de descuento. La frecuencia de los avisos se adapta al uso de la app.
- **Correo nativo:** se abre la app de correo con un resumen cada vez que se guarda una fecha especial o una promoción.
- **Mapa "Cerca de ti":** GPS y osmdroid con los locales cercanos.
- **Panel del local:** crear y editar promociones y validar cupones escaneando el QR.
- **Notificaciones** con WorkManager.
- **Clima actual (API externa Open-Meteo):** sugerencia según el clima en la pantalla de Inicio, sin tocar los datos locales.
- **Reseñas (microservicio propio):** CRUD completo desde la app. Se pueden crear, ver, editar y eliminar las reseñas propias.
- **Locales destacados (microservicio propio):** recomendación por calidad de las reseñas, cercanía y gustos del usuario.
- **Catálogo sincronizado:** la app descarga locales y promociones desde el microservicio de catálogo y los guarda en Room.

## Arquitectura

```
App Android (Kotlin + Jetpack Compose, MVVM, Room, Retrofit)
   │
   ├── Retrofit ──► ms-catalogo  (Spring Boot, puerto 8081, BD H2/MySQL)
   ├── Retrofit ──► ms-resenas   (Spring Boot, puerto 8082, BD H2/MySQL)
   │                    └── RestClient ──► ms-catalogo (valida locales)
   └── Retrofit ──► Open-Meteo   (API externa de clima)
```

## Endpoints

### Microservicio ms-catalogo (`http://localhost:8081`)

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/salud` | Estado del servicio |
| GET | `/api/catalogo` | Locales y promociones juntos (lo usa la app) |
| GET | `/api/locales` | Lista los locales |
| GET | `/api/locales/{id}` | Un local |
| POST | `/api/locales` | Crea un local (201) |
| PUT | `/api/locales/{id}` | Edita un local |
| DELETE | `/api/locales/{id}` | Elimina un local y sus promociones (204) |
| GET | `/api/promociones?localId=` | Lista las promociones (filtro opcional por local) |
| GET | `/api/promociones/{id}` | Una promoción |
| POST | `/api/promociones` | Crea una promoción (201) |
| PUT | `/api/promociones/{id}` | Edita una promoción |
| DELETE | `/api/promociones/{id}` | Elimina una promoción (204) |

### Microservicio ms-resenas (`http://localhost:8082`)

Las rutas de reseñas reciben el encabezado `X-Usuario` con el correo del usuario.

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/salud` | Estado del servicio |
| GET | `/api/locales/{localId}/resenas` | Reseñas de un local (marca `esMia`) |
| POST | `/api/resenas` | Crea una reseña (201). 409 si ya existe una del mismo usuario para ese local |
| PUT | `/api/resenas/{id}` | Edita la reseña propia (403 si es de otra persona) |
| DELETE | `/api/resenas/{id}` | Elimina la reseña propia (204) |
| GET | `/api/recomendaciones?lat=&lon=&radioKm=3&categorias=Pizza,Sushi&limite=5` | Locales destacados |

Los errores llegan como JSON: `{"error": "mensaje"}`.

### API externa: Open-Meteo

| Método | Ruta | Descripción |
|---|---|---|
| GET | `https://api.open-meteo.com/v1/forecast?latitude=&longitude=&current=temperature_2m,weather_code&timezone=auto` | Temperatura y código del clima actual |

## Pasos para ejecutar

### Requisitos

- Android Studio con un emulador de Android 8.0 o superior.
- Java 17 o superior. Sirve el que trae Android Studio, en `C:\Program Files\Android\Android Studio\jbr`.

### 1. Microservicios

En una terminal `cmd`:

```bat
set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr
cd microservicios\ms-catalogo
mvnw.cmd spring-boot:run
```

En otra terminal `cmd`:

```bat
set JAVA_HOME=C:\Program Files\Android\Android Studio\jbr
cd microservicios\ms-resenas
mvnw.cmd spring-boot:run
```

- Las bases de datos H2 se crean solas en `microservicios/<servicio>/datos/` y se llenan con datos de ejemplo la primera vez.
- Para revisar las tablas, entra a `http://localhost:8081/h2-console` y a `http://localhost:8082/h2-console`. Los datos son JDBC URL `jdbc:h2:file:./datos/catalogo` o `jdbc:h2:file:./datos/resenas`, usuario `sa` y clave vacía.
- Para usar MySQL en vez de H2, agrega `-Dspring-boot.run.profiles=mysql` al comando de arranque.

### 2. App

1. Abre la carpeta del proyecto en Android Studio y espera a que termine **Gradle Sync**.
2. Con los dos microservicios encendidos, ejecuta la app en el emulador. El emulador llega al computador por `10.0.2.2`.
3. Usuario de prueba: `sofia@correo.cl` / `cocina123`.

### 3. Pruebas

- **App:** clic derecho en `app/src/test/java` y luego **Run 'Tests in 'java'' with Coverage**.
- **Microservicios:** ejecuta `mvnw.cmd test` dentro de cada carpeta.

## APK firmado

- APK: [`entrega/CocinaCapital-v1.0.apk`](entrega/CocinaCapital-v1.0.apk), compilado como *release*.
- Llave: [`keystore/cocinacapital.jks`](keystore/cocinacapital.jks), formato PKCS12, alias `cocinacapital`.
- La configuración de la firma está en [`keystore.properties`](keystore.properties) y en el bloque `signingConfigs` de [`app/build.gradle.kts`](app/build.gradle.kts).
- Para generarlo: **Build ▸ Generate Signed App Bundle or APK ▸ APK**, o ejecutar `gradlew assembleRelease`.

![APK firmado](docs/capturas/apk-firmado.png)

![Archivo .jks](docs/capturas/jks.png)

> La llave y sus claves están en el repositorio porque la evaluación lo pide. En un proyecto real nunca se suben.
