\# ReservaMS - Review Service



\## Descripcion



Este microservicio administra las resenas y calificaciones que los clientes dejan sobre los hoteles.



Permite guardar una calificacion asociada a un cliente, un hotel y una reserva.



\## Responsabilidades



\- Crear resenas.

\- Listar resenas.

\- Buscar resenas por hotel.

\- Buscar resenas por cliente.

\- Buscar resenas por reserva.

\- Actualizar resenas.

\- Eliminar resenas.

\- Evitar mas de una resena por reserva.



\## Puerto



8089



\## Base de datos



reservams\_review\_db



\## Endpoints principales



\- GET /api/v1/reviews

\- GET /api/v1/reviews/{id}

\- GET /api/v1/reviews/hotel/{hotelId}

\- GET /api/v1/reviews/client/{clientUserId}

\- GET /api/v1/reviews/reservation/{reservationId}

\- POST /api/v1/reviews

\- PUT /api/v1/reviews/{id}

\- DELETE /api/v1/reviews/{id}



\## Ejecucion



1\. Crear la base de datos reservams\_review\_db.

2\. Ejecutar el script SQL ubicado en la carpeta database.

3\. Levantar Eureka Server.

4\. Ejecutar el review-service.

5\. Probar los endpoints desde Postman o desde el API Gateway.



