# Control Clientes
Tecnologías: Spring-boot + JPA (Hibernate) + Mysql + REST

## Abstract
Aplicación basada en ABM de clientes con seguridad incluída mediante login utilizando roles y clase de Usuarios.
La aplicación esta construida con SpringBoot, versión 2.4.3. Se hace uso de JPA con Hibernate para conectarse a la base de datos (mysql para produción). En si es un CRUD con REST incluído con métodos para acceder al detalle
de una persona y un CRUD para los ABM.
Incluye autenticación basica integrada con Spring Security utilizando Roles de la clase Usuario.

## Pasos para la realización de la aplicación
### Creación del proyecto
Para crear el proyecto inicial hacemos uso de [Spring Initializr](http://start.spring.io/). Seleccionamos las dependencias de JPA, Web, DevTools, Mysql, Lombok y Thymeleaf en un primer momento. Descargamos el proyecto resultante y lo importamos como maven project en Eclipse, que sera el IDE escogido para realizar el ejercicio.

### Implementación del modelo
Para la implementación del modelo se emplea una herramienta de generación de diagramas de clases a partir de la base de datos llamado MySQL Workbench que se puede encontrar [aquí](https://www.mysql.com/products/workbench/). Ya configurada la conexión a la base de datos, nos vamos al menú superior Database y seleccionamos la opción de Reverse Engineer, se nos mostrará una ventana donde seleccionamos nuestra conexión a la base de datos, localhost, y pulsamos Next,cuando termine el proceso, volvemos a pulsar Next,seleccionamos la base de datos que nos interese y pulsamos Next,cuando termine el proceso, volvemos a pulsar Next,seleccionar objetos a importar mediante ingeniería inversa.
En la siguiente ventana debemos seleccionar qué objetos queremos incluir en nuestro diagrama entidad relación. En nuestro caso seleccionaremos todos. Es importante seleccionar el check que aparece en la parte inferior. En algunas ocasiones me ha ocurrido que no me deja seleccionarlo porque a lo mejor hay demasiados objetos seleccionados. Pulsamos Next para continuar.
Para las distintas entidades tendremos un atributo id que será la clave primaria en la BD. De igual forma tendremos un atributo code que servirá de clave natural de la identidad.
Cuando termine el proceso, volvemos a pulsar Next.Si todo sale correctamente debería aparecernos una ventana monstrando el total de tablas leídas desde el esquema.
En base a ese modelo se generan a mano las distintas clases , con sus atributos y sus relaciones.

En un primer momento utilizamos una Mysql desplegada en local, por lo que añadimos las siguientes lineas al application.properties:
```sh
spring.datasource.url = jdbc:mysql://localhost/controlclientes?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true #Your db name
spring.datasource.username = root #Your user
spring.datasource.password = secret #Your password
spring.jpa.database-platform = org.hibernate.dialect.MySQLDialect
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

### Implementación de los repositorios o Dao
Para el acceso a persistencia usaremos repositorios de JPA. Basta con crear una interface y extender de JpaRepository. De esta manera tendremos los métodos basicos de accceso a la base de datos. Si quisieramos realizar otro tipo de consultas más complejas, basta con añadir la signatura del método indicando el campo de busqueda.Ej: buscar un persona por id (atributo id) -> añadir en la interface el siguiente método:
```sh
encontrarPersona(Persona persona);

```
Recordar que debemos añadir la anotación @Repository para que SpringBoot lo reconozca como una clase repositorio y este en el contexto.

### Implementación de los servicios
Para realizar los servicios usaremos una interface donde definiremos los métodos y su implementación, que la capa superior no tiene por que conocer. Debemos recordar que la clase implementada debe llevar la anotación @Service. Con ella decimos que esta es una clase de configuración a tener en cuenta, igual que pasaba con los repositorios.
Como los métodos necesitarán realizar llamadas al repositorio, es necesario una instancia del mismo en la clase. Para ello usaremos inyección. Gracias a haber anotado previamente el repositorio con @Repository, spring sabrá que debe inyectar. Para conseguir que funcione pondremos la anotación @Aurowired sobre el atributo del repositorio en la clase.
Los métodos a implementar son un CRUD (create, read, update & delete) en el servicio de personas.El método save solo modificará la persona si conoce el id.
A su vez, los métodos de este servicio son transacionales, para ello usamos la anotacion @Transactional en los métodos (menos en el GET). Con esta anotación se intenta ejecutar el código del método, si surgiese algún error o excepción se ejecutaría un rollback. Podemos probar que funciona si forzamos a incluir una excepcion despues de un save y veremos que la bd no sufre ninguna alteración.

### Controladores Rest
Los controladores Rest serán nuestro punto de acceso desde el exterior. Es necesario anotarlos con @Controller. A partir de aquí cada método anotado tendrá su propia URL, parametros, etc. La siguiente tabla muestra de que manera se anotan los métodos en función de que queremos implementar (usaremos el controlador de inicio como ejemplo):

| HTTP Method | CRUD Method | Anotation | URL |
| ----------- | ----------- | ------------ | ------ |
| POST 		  | guardar 	| @PostMapping | /guardar |
| GET 		  | editar 		| @GetMapping  |/editar/{idPersona} |
| GET 		  | agregar 	| @GetMapping  | /agregar |
| GET 		  | eliminar 	| @GetMapping  | /eliminar |
| GET 		  | findPaginated | @GetMapping  | /page/{pageNo} |
| GET 		  | getPersonas | @GetMapping  | /webservices/personas |
| GET 		  | getPersona 	| @GetMapping  | /webservices/personas/{idPersona} |


Además, estos métodos contienen parametros, ya sean por la URL o por json. Aquellos que vengan con la URL llevarán la anotacion
@PathParam. A las entidades que pasemos en formato json se les puede añadir la anotacion @Valid, que comprobara anotaciones dentro de las entidades que verifiquen los atributos como @NotNull o @Size.

En caso de que los métodos se ejecuten de forma correcta devolveremos un código 200 con la entidad creada, modificada... En caso 
contrario se devolverá un códido de error, se capturará y se informará al usuario.

### Autogeneración de la base de datos y datos inciales
Para generar la base de datos debemos añadir la siguiente línea al application.properties:
```sh
spring.jpa.hibernate.ddl-auto = create
```
Arrancamos la aplicación. Con esto estammos autogenerando las tablas y sus columnas en la base de datos. Una vez finalizado el arranque podemos detenerlo y comprobar que, efectivamente, la base de datos contiene las tablas que definimos con las anotaciones en las entidades. No olvidemos que esto sirve para cualquier otra base de datos como Hsqldb, oracle...

Modificamos nuevamente el valor de la propiedad en el fichero application.properties:
```sh
spring.jpa.hibernate.ddl-auto = update
```
Tambien se podría poner en "update" para que se actualizara ante cualquier cambio en el modelo.

A continuación vamos a crear unos datos iniciales. Para ello conectaremos con un persona a la base de datos y cargaremos el
siguiente script de datos inciales (Este proceso podría ser automático, más adelante, en la parte de testing se verá un ejemplo):

```sh
INSERT INTO `persona` VALUES (1,'Juan','Perez','jperez@mail.com',554433212,100);
INSERT INTO `persona` VALUES (2,'Karla','Gomez','kgomez@mail.com',33445566,150);
INSERT INTO `persona` VALUES (3,'Luis','Lopez','llopez@mail.com',443322121,200);
INSERT INTO `persona` VALUES (4,'Diego','Domingues','ddominguez@mail.com',443322122,200);
INSERT INTO `persona` VALUES (5,'Pablo','Pirez','pablopirez@mail.com',443322123,100);
INSERT INTO `persona` VALUES (6,'Ernesto','Guimenez','eguimenez@mail.com',443322124,400);
INSERT INTO `persona` VALUES (7,'Felipe','Flores','fflores@mail.com',443322125,200);
INSERT INTO `persona` VALUES (9,'Fernanda','Fernandez','ffernandez@mail.com',443322127,300);
```

### Primeras pruebas
Ya estamos en disposición de hacer las primeras pruebas. Si todo ha salido según lo esperado deberiamos tener la aplicación en ejecución, con conexión a la base de datos y las URL de acceso disponibles. En nuestro caso vamos a añadir un path de acesso previo a los servicios web. Añadimos la siguiente línea en el fichero application.properties:
```sh
server.contextPath = /api # En SpringBoot 1.5.x, la versión 2.0.x modifica el nombre de esta propiedad
```
Si quisieramos modificar el puerto, que por defecto es el 8080, añadiriamos la siguiente:
```sh
server.port = 8090
```
Ejecutamos la primera sentencia, por ejemplo para obtener la persona con identificador 5. Podemos usar el navegador o una herramienta. En este caso haremos uso de Postman:
- Indicamos la url: http://localhost:8080/webservices/personas/5
- Indicamos que es un GET
- Clicamos "Send"
Obtenemos la siguiente respuesta:
```sh
{
    "id": 5,
    "nombre": "Pablo",
	"apellido": "Pirez",
    "email": "pablopirez@mail.com",
    "telefono": 443322123,
	"saldo": 100,
}
```
A partir de aquí podemos probar todas las URL para ver como funciona el servicio web implementado. Hay que recordar que debemos 
pasar datos válidos, de lo contrario el servicio dará un error (Omitido para el usuario, dado que los capturadores están vacios
en este ejemplo a falta de implementación, aunque deberían usar ApiError y un código de respuesta de error con información para 
el usuario que indique que sucedio).

### Test unitarios: Probando los servicios
Para realizar los test, escribiremos metodos en la clase ApplicationTest con la anotacion @Test. Como queremos probar los servicios contra una base de datos, debemos crear algo aislado de la base de datos de producción. Es por esto que haremos uso de H2, una base que se mantendrá en memoria.

Incluimos su depencencia en el pom.xml y agregamos la anotación @DataJpaTest y @RunWith(SpringRunner.class) a la clase de test. Con ello indicamos que se hara uso del modelo para mapear las tablas. Es necesario incluir una serie de configuraciones en un fichero application.properties que crearemos en src/test/resources:
```sh
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.url=jdbc:h2:mem:db;DB_CLOSE_DELAY=-1
spring.datasource.username=sa
spring.datasource.password=sa
```
A partir de aquí, tendremos la base de datos operativa.

Para ejecutar test no se ha realizado está parte, pero queda pendiente para su investigación, la implementacio sería queremos un dataset inicial. Como indicabamos anteriormente, esto puede cargarse de forma automatica al iniciar los test. En el directorio src/test/resources, incluimos un fichero data.sql con los insert que se mostraron en apartados anteriores. Esto es suficiente para tener un dataset inicial.
Es necesario tener cuidado, pues en ocasiones se duplica este fichero, apareciendo en la carpeta target. Se debe eliminar si no queremos que de un error en tiempo de ejecución al lanzarse dos veces el fichero.

Ahora crearemos diversos test para los métodos de los servicios. Se tratan de test muy sencillos con casos de prueba positivos en su mayoría, dado que el esfuerzo de la práctica no se ha centrado aquí.

Faltaría por testear los servicios REST. Aunque se probee de un sistema de mockear servicios, se propone usar una libreria llamada Mockito para la misma función. No se ha realizado está parte, pero queda pendiente para su investigación.

### Incluyendo seguridad
El primer paso para incluir seguridad es hacer uso de Spring-security. Para ello añadimos su dependencia y creamos una clase que
anotaremos como @Configuration, para que sea detectada. Y le añadiremos las anotaciones @WebSecurity y @EnableGlobalMethodSecurity.

Podría validarse contra un servicio de usuarios, implementado con la clase Rol y Usuario. Añadiremos dos, uno con rol USER y otro con rol ADMIN.

Será necesario también incluir los metodos de configuración para indicar que rutas tienen permiso al público y en cuales hay que entrar bajo un cierto rol.

Si probamos ahora a hacer un get de las personas, veremos que nos pide autenticación. En caso de no darsela nos devuelve un error por no estar autorizados con rol de admin o usuario

### Documentando la aplicación con Swagger pendiente
Swagger es un framework que permite tanto documentar apis como crearlas. De igual forma, una API como es la nuestra, con las anotaciones de Swagger hace que el framework genere una UI accesible desde la web, capaz de explicar que hace cada método así como lanzar peticiones.

Para documentar usaremos varias anotaciones. Existen tanto para los controladores, como para las clases de modelo. Si observamos la UI, a la que podemos acceder mediante http://localhost:8080/controlclientes/swagger-ui.html, vemos que contiene anotaciones personalizadas tanto de lo que hace cada método como de las posibles respuestas. Además las clases de modelo que aparecen abajo, también tienen documentados sus atributos. Todo esto se logra gracias a las anotaciones @ApiOperation, @ApiResponse y @ApiModelProperty. Existen más, pero en este ejemplo son las que se han utilizado para manejar incluir Swagger.

## Construyendo la aplicación
La aplicación está lista para su despliegue y construcción. Debemos tener maven instalado en el ordenador. Si todo es correcto, podemos ejecutar el comando que aparece debajo. Con el se construye la aplicación y se crea el .jar listo para su despliegue.
```sh
mvn clean package
java -jar app-name.jar
```

## Despliegue en un contenedor Docker pendiente
Como punto final para el desarrollo del ejercicio. Vamos a desplegar la aplicación y la base de datos en contenedores Docker.
Si tenemos instalado Docker, el primer paso será descargar una imagen de mysql y arrancar la base de datos. Para ello ejecutaremos el siguiente comando:
```sh
docker run -d -p 33061:3306 --name mysql8 -e MYSQL_ROOT_PASSWORD=secret mysql:8.0 --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
```

Ahora debemos configurar el proyecto para que acepte y se construya en un contenedor docker. Añadimos las dependencias de Docker al pom.xml y creamos en la ruta src/main/docker el fichero Dockerfile con el siguiente contenido:
```sh
FROM frolvlad/alpine-oraclejdk8:slim
 VOLUME /tmp
 ADD orders-api.jar app.jar
 RUN sh -c 'touch /app.jar'
 ENV JAVA_OPTS=""
 ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
```
Posteriormente, usamos el siguiente comando en una consola en el directorio raiz del proyecto para construirlo:
```sh
mvn package docker:build
```

Ya tenemos ambos contenedores, solo es necesario enlazarlos. Tenemos que tener en cuenta que al enlazar los contenedores, el de 
mysql va a depender del de nuestra aplicación, por lo que el acceso no será desde la IP 127.0.0.1 sino desde la 172.17.0.2. Esto
nos obliga a modificar el fichero application.properties para conectar correctamente con la bd.
```sh
spring.datasource.url = jdbc:mysql://172.17.0.2:3306/controlclientes?useSSL=false
```

Volvemos a ejecutar el comando para construir la imagen de docker con la aplicación y para finalizar ejecutamos el comando que 
permitirá que esto sea posible:
```sh
docker run -p 8080:8080 --name app --link mysql8:mysql -ti controlclientes/personas
```

Finalmente, incluimos con un cliente mysql unos datos iniciales y ya tenemos nuestra aplicación lista para funcionar.

### Acceso a los contenedores Docker pendiente
Contenedor con mysql: https://hub.docker.com/r/carlosdelviento/mysql/
Contenedor con app: https://hub.docker.com/r/carlosdelviento/controlclientes-personas/