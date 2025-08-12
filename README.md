# API de Gestión de Usuarios - LINSI

API RESTful construida con **Spring Boot** para el sistema del laboratorio LINSI de la UTN FRLP. Provee gestión de entidades (usuarios, proyectos, becas, materias, actividades, áreas de investigación), autenticación segura con JWT, roles (`ADMINISTRADOR`, `DOCENTE`, `BECARIO`), protección de endpoints y un entorno de despliegue contenerizado con Docker.

---

## 📋 Tabla de Contenidos

- ✨ Features
- 🛠️ Stack Tecnológico
- 🚀 Cómo Empezar
  - Prerrequisitos
  - Configuración
  - Despliegue con Docker (Recomendado)
  - Ejecución Local (Alternativa para Desarrollo)
- 📍 Endpoints Principales
- 📄 Documentación de la API
- 🔒 Seguridad
- 🗃️ Base de Datos
- 📱 Contacto

---

## ✨ Features

*   **Autenticación y Autorización:** Sistema seguro basado en JSON Web Tokens (JWT).
*   **Gestión de Roles:** Roles predefinidos (`ADMINISTRADOR`, `DOCENTE`, `BECARIO`) para control de acceso granular.
*   **Operaciones CRUD:** Gestión completa para múltiples entidades del sistema.
*   **Endpoints Protegidos:** Acceso restringido a endpoints según el rol del usuario.
*   **Documentación de API:** Documentación interactiva autogenerada con Swagger (OpenAPI).
*   **Contenerización:** Lista para desplegar con Docker y Docker Compose, incluyendo base de datos y proxy inverso.

---

## 🛠️ Stack Tecnológico

*   **Backend:** Spring Boot 3
*   **Seguridad:** Spring Security
*   **Base de Datos:** Spring Data JPA, MySQL 8
*   **Build Tool:** Maven
*   **Contenerización:** Docker, Docker Compose
*   **Servidor Web (Proxy Inverso):** Nginx

---

## 🚀 Cómo Empezar

Sigue estas instrucciones para tener el proyecto funcionando en tu máquina local.

### Prerrequisitos

Asegúrate de tener instalado el siguiente software:

*   Java Development Kit (JDK) 17 o superior
*   Apache Maven
*   Docker y Docker Compose

### Configuración

1.  **Crear el archivo `.env`:** En la raíz del proyecto, crea un archivo llamado `.env`. Este archivo es ignorado por Git para mantener tus secretos a salvo.

2.  **Completar las variables:** Rellena el archivo `.env` con tus credenciales.

    ```dotenv
    # Base de Datos
    MYSQL_DATABASE=
    MYSQL_USER=
    MYSQL_PASSWORD=
    MYSQL_ROOT_PASSWORD=

    # VARIABLES PARA EL USUARIO ADMIN DE LA APLICACIÓN
    ADMIN_NOMBRE=
    ADMIN_APELLIDO=
    ADMIN_PASSWORD=
    ADMIN_EMAIL=
    ADMIN_DNI=

    # URL de la base de datos para Spring (usada por el contenedor 'app')
    SPRING_DATASOURCE_URL=
    SPRING_DATASOURCE_USERNAME=
    SPRING_DATASOURCE_PASSWORD=

    # Credenciales de Email (para envío de correos)
    MAIL_USERNAME=
    MAIL_PASSWORD=

    # Seguridad de la Aplicación
    JWT_SECRET=
    ```
    > ⚠️ **Importante:** Usa un `JWT_SECRET` fuerte y único. Para `MAIL_PASSWORD`, si usas Gmail, necesitas generar una "Contraseña de aplicación".

### Despliegue con Docker (Recomendado)

Este método levanta toda la infraestructura (API, Base de Datos, Nginx, phpMyAdmin).

1.  **Construir el JAR de la aplicación:**
    ```bash
    ./mvnw.cmd clean package -DskipTests
    ```

2.  **Ejecutar Docker Compose:**
    ```bash
    docker-compose --env-file .env up --build -d
    ```

3.  **Acceder a los servicios:**
    *   **API:** `https://localhost` (gracias a Nginx)
    *   **Documentación Swagger:** `https://localhost/swagger-ui.html`

### Ejecución Local (Alternativa para Desarrollo)

Si prefieres ejecutar la aplicación desde tu IDE (IntelliJ, VS Code, etc.):

1.  **Levantar solo la base de datos con Docker:**
    ```bash
    docker-compose up -d mysql
    ```

2.  **Ejecutar la aplicación Spring Boot:**
    Usa tu IDE para lanzar la aplicación o ejecuta el siguiente comando:
    ```bash
    ./mvnw.cmd spring-boot:run
    ```
    > Nota: Para que esto funcione, necesitarás configurar las variables de entorno en tu IDE o usar un perfil de Spring para desarrollo que apunte a `localhost:3306`.

---

## 📍 Endpoints Principales

A continuación se detallan los principales grupos de endpoints de la API.

### 🔐 Autenticación

| Método | Endpoint                | Descripción                       | Acceso  |
| :----- | :---------------------- | :-------------------------------- | :------ |
| `POST` | `/api/v1/auth/register` | Registra un nuevo usuario.        | Público |
| `POST` | `/api/v1/auth/login`    | Inicia sesión y obtiene un token. | Público |

### 👥 Gestión de Usuarios

| Método   | Endpoint                | Descripción                               | Acceso                                |
| :------- | :---------------------- | :---------------------------------------- | :------------------------------------ |
| `GET`    | `/api/v1/usuarios`      | Lista todos los usuarios.                 | `ADMINISTRADOR`                       |
| `GET`    | `/api/v1/usuarios/me`   | Obtiene los datos del perfil propio.      | `ADMINISTRADOR`, `DOCENTE`, `BECARIO` |
| `PUT`    | `/api/v1/usuarios/{id}` | Modifica un usuario.                      | `ADMINISTRADOR` o el propio usuario   |
| `DELETE` | `/api/v1/usuarios/{id}` | Elimina un usuario.                       | `ADMINISTRADOR`                       |

### 🏗️ Gestión de Proyectos

| Método   | Endpoint                  | Descripción                         | Acceso                                   |
| :------- | :------------------------ | :---------------------------------- | :--------------------------------------- |
| `POST`   | `/api/v1/proyectos`       | Crea un nuevo proyecto.             | `ADMINISTRADOR`, `DOCENTE`               |
| `GET`    | `/api/v1/proyectos`       | Lista todos los proyectos.          | `ADMINISTRADOR`                          |
| `GET`    | `/api/v1/proyectos/{id}`  | Obtiene un proyecto por su ID.      | `ADMINISTRADOR` o el director/integrante |
| `PUT`    | `/api/v1/proyectos/{id}`  | Actualiza un proyecto.              | `ADMINISTRADOR` o el director            |
| `DELETE` | `/api/v1/proyectos/{id}`  | Elimina un proyecto.                | `ADMINISTRADOR` o el director            |

> Nota: En ausencia del director del proyecto, el permiso lo tiene el DOCENTE creador del mismo

### 📚 Gestión de Materias

| Método   | Endpoint                | Descripción                       | Acceso                             |
| :------- | :---------------------- | :-------------------------------- | :--------------------------------- |
| `POST`   | `/api/v1/materias`      | Crea una nueva materia.           | `ADMINISTRADOR`                    |
| `GET`    | `/api/v1/materias`      | Lista todas las materias.         | `ADMINISTRADOR`                    |
| `GET`    | `/api/v1/materias/{id}` | Obtiene una materia por su ID.    | `ADMINISTRADOR` o si es integrante |
| `PUT`    | `/api/v1/materias/{id}` | Actualiza una materia.            | `ADMINISTRADOR`                    |
| `DELETE` | `/api/v1/materias/{id}` | Elimina una materia.              | `ADMINISTRADOR`                    |

### 🎓 Gestión de Becas

| Método   | Endpoint              | Descripción                     | Acceso                           |
| :------- | :-------------------- | :------------------------------ | :------------------------------- |
| `POST`   | `/api/v1/becas`       | Crea una nueva beca.            | `ADMINISTRADOR`                  |
| `GET`    | `/api/v1/becas`       | Lista todas las becas.          | `ADMINISTRADOR`                  |
| `GET`    | `/api/v1/becas/{id}`  | Obtiene una beca por su ID.     | `ADMINISTRADOR` o el propietario |
| `PUT`    | `/api/v1/becas/{id}`  | Actualiza una beca.             | `ADMINISTRADOR`                  |
| `DELETE` | `/api/v1/becas/{id}`  | Elimina una beca.               | `ADMINISTRADOR`                  |

### 📝 Gestión de Actividades

| Método   | Endpoint                    | Descripción                      | Acceso                                        |
| :------- | :-------------------------- | :------------------------------- | :-------------------------------------------- |
| `POST`   | `/api/v1/actividades`       | Registra una nueva actividad.    | `ADMINISTRADOR` o el responsable del proyecto |
| `GET`    | `/api/v1/actividades`       | Listar actividades               | `ADMINISTRADOR`                               |
| `GET`    | `/api/v1/actividades/{id}`  | Obtiene una actividad por su ID. | `ADMINISTRADOR` o el responsable del proyecto |
| `PUT`    | `/api/v1/actividades/{id}`  | Actualiza una actividad.         | `ADMINISTRADOR` o el responsable del proyecto |
| `DELETE` | `/api/v1/actividades/{id}`  | Elimina una actividad.           | `ADMINISTRADOR` o el responsable del proyecto |

### 🛡️ Gestión de Roles

| Método | Endpoint       | Descripción                         | Acceso          |
| :----- | :------------- | :---------------------------------- | :-------------- |
| `GET`  | `/api/v1/roles`| Lista todos los roles disponibles.  | `ADMINISTRADOR` |

### 🌐 Gestión de Áreas

| Método | Endpoint       | Descripción                              | Acceso                                |
| :----- | :------------- | :--------------------------------------- | :------------------------------------ |
| `GET`  | `/api/v1/areas`| Lista todas las áreas de investigación.  | `ADMINISTRADOR`, `DOCENTE`, `BECARIO` |

---

## 📄 Documentación de la API

Una vez que la aplicación está en ejecución, puedes acceder a la documentación interactiva de Swagger UI para explorar y probar los endpoints:

*   **URL (con Docker):** `https://localhost/swagger-ui.html`

---

## 🔒 Seguridad

*   **Autenticación:** Se utiliza JWT para la autenticación sin estado. El token debe ser enviado en la cabecera `Authorization` con el prefijo `Bearer`.
    *   *Ejemplo:* `Authorization: Bearer <tu-token-jwt>`
*   **Roles y Permisos:** El acceso a los endpoints está restringido por roles (`ADMINISTRADOR`, `DOCENTE`, `BECARIO`) usando Spring Security.
*   **HTTPS:** La configuración de Docker Compose incluye un servicio de Nginx preparado para manejar certificados SSL y servir la aplicación bajo HTTPS en un entorno de producción.

---

## 🗃️ Base de Datos

*   **Motor:** MySQL 8, ejecutándose en un contenedor Docker.
*   **Persistencia:** Los datos se guardan en un volumen de Docker (`mysql_data`) para que no se pierdan al reiniciar los contenedores. Puedes inspeccionarlo con `docker volume inspect gestion-usuarios_mysql_data`.

----

## 📱 Contacto

Proyecto desarrollado por **Paulo Nepotti** como parte de la **Práctica Profesional Supervisada** en el laboratorio **LINSI** — Universidad Tecnológica Nacional, Facultad Regional La Plata.
