# Moodle Mobile Client

Aplicación móvil desarrollada en Android (Kotlin) que consume la API REST de Moodle, como parte del Proyecto Integrador de la asignatura **Programación de Dispositivos Móviles**.

## Contexto del proyecto
Este proyecto simula el escenario real de una institución educativa que requiere una aplicación móvil conectada a su campus virtual Moodle. La aplicación permite autenticación segura y el acceso a cursos, actividades, tareas y foros mediante servicios web REST.

## Objetivos
- Consumir servicios REST de Moodle desde una aplicación móvil.
- Implementar autenticación segura mediante token.
- Gestionar cursos, actividades, tareas y foros.
- Aplicar una arquitectura por capas para mejorar mantenibilidad.
- Integrar funcionalidades adicionales como notificaciones push.

## Funcionalidades implementadas

### Alcance obligatorio
- Autenticación del usuario mediante token de Moodle.
- Listado de cursos matriculados.
- Visualización de actividades del curso (tareas y foros).
- Envío de tareas con texto y archivos adjuntos.
- Visualización de discusiones en foros.
- Publicación de participaciones en foros.

### Alcance opcional
- **Notificaciones push mediante Firebase Cloud Messaging (FCM)** para informar al usuario sobre eventos relevantes dentro de la aplicación.

## Tecnologías utilizadas
- Android nativo (Kotlin)
- Retrofit para consumo de API REST
- API REST de Moodle
- Firebase Cloud Messaging (FCM)

## Arquitectura
La aplicación fue desarrollada utilizando una **arquitectura por capas**, separando:
- Capa de presentación (Activities, layouts y navegación).
- Capa de servicios (consumo de API REST y manejo de tokens).
- Capa de datos (modelos y mapeo de respuestas JSON).

Esta arquitectura facilita el mantenimiento del código y su escalabilidad.

## Entorno de ejecución
- Android Studio
- Dispositivo o emulador con Android 8.0 o superior
- Servidor Moodle local con servicios web REST habilitados
- Token de acceso generado en Moodle

## Mejoras futuras
- Implementar almacenamiento local mediante Room/SQLite.
- Incorporar modo offline.
- Mejorar la interfaz gráfica y experiencia de usuario.

## Repositorio del proyecto
Repositorio Git:
https://github.com/sabinamontalvo52/moodle-mobile-client-MONTALVO-SABINA.git

## Autor
**Sabina Montalvo**
