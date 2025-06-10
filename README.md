# API de Gestión de Usuarios - LINSI

Este proyecto es una API REST desarrollado con **Spring Boot**, diseñado para gestionar usuarios del sistema del laboratorio **LINSI** de la UTN FRLP. Incluye autenticación segura con JWT, roles (`USER`, `ADMIN`), protección de endpoints, y despliegue con Docker.

---

## 🚀 Cómo desplegar

### 1. Construir el JAR de la aplicación

```bash
./mvnw clean package -DskipTests
```

### 2. Ejecutar Docker Compose

```bash
docker-compose --env-file .env up --build -d
```

### 3. Acceder a la aplicación

* API: [https://dominio.com](https://dominio.com)
* Documentación Swagger: [https://dominio.com/swagger-ui.html](https://dominio.com/swagger-ui.html)

> ⚠️ Requiere un dominio válido apuntando al servidor y certificados SSL en `nginx/certs`.

---

## 📍 Endpoints principales

### 🔐 Autenticación

* `POST /auth/register` — Registrar nuevo usuario
* `POST /auth/login` — Obtener token JWT

### 👥 Usuarios

* `GET /api/usuarios` — Listar todos los usuarios (solo `ADMIN`)
* `GET /api/usuarios/me` — Ver perfil propio
* `PUT /api/usuarios/{id}` — Modificar usuario (ADMIN o el mismo usuario)
* `DELETE /api/usuarios/{id}` — Eliminar usuario (solo `ADMIN`)

---

## 🔒 Seguridad

* Autenticación con JWT
* Roles y permisos: `USER`, `ADMIN`
* Token JWT en cabecera: `Authorization: Bearer <token>`
* Comunicación segura mediante HTTPS

---

## 🗃️ Base de datos

La base de datos MySQL se ejecuta en un contenedor Docker y guarda los datos en un volumen persistente:

```bash
docker volume inspect gestionusuarios_mysql_data
```

---


## 📱 Contacto

Proyecto desarrollado por **Paulo Nepotti** como parte de la **Práctica Profesional Supervisada** en el laboratorio **LINSI** — Universidad Tecnológica Nacional, Facultad Regional La Plata.

