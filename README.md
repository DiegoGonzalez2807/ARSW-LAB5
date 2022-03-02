### Escuela Colombiana de Ingeniería

### Arquitecturas de Software

## Integrantes

Cristian Andres Castellanos Fino
Diego Alejandro Gonzalez



#### API REST para la gestión de planos.

En este ejercicio se va a construír el componente BlueprintsRESTAPI, el cual permita gestionar los planos arquitectónicos de una prestigiosa compañia de diseño. La idea de este API es ofrecer un medio estandarizado e 'independiente de la plataforma' para que las herramientas que se desarrollen a futuro para la compañía puedan gestionar los planos de forma centralizada.
El siguiente, es el diagrama de componentes que corresponde a las decisiones arquitectónicas planteadas al inicio del proyecto:

![](img/CompDiag.png)

Donde se definió que:

* El componente BlueprintsRESTAPI debe resolver los servicios de su interfaz a través de un componente de servicios, el cual -a su vez- estará asociado con un componente que provea el esquema de persistencia. Es decir, se quiere un bajo acoplamiento entre el API, la implementación de los servicios, y el esquema de persistencia usado por los mismos.

Del anterior diagrama de componentes (de alto nivel), se desprendió el siguiente diseño detallado, cuando se decidió que el API estará implementado usando el esquema de inyección de dependencias de Spring (el cual requiere aplicar el principio de Inversión de Dependencias), la extensión SpringMVC para definir los servicios REST, y SpringBoot para la configurar la aplicación:


![](img/ClassDiagram.png)

### Parte I

1. Integre al proyecto base suministrado los Beans desarrollados en el ejercicio anterior. Sólo copie las clases, NO los archivos de configuración. Rectifique que se tenga correctamente configurado el esquema de inyección de dependencias con las anotaciones @Service y @Autowired.

#### Se inserta el laboratorio 4 en la carpeta de blueprints
![](https://github.com/DiegoGonzalez2807/ARSW-LAB5/blob/master/img/CARPETAS.png)

2. Modifique el bean de persistecia 'InMemoryBlueprintPersistence' para que por defecto se inicialice con al menos otros tres planos, y con dos asociados a un mismo autor.

#### Se genera una función para automatizar la creación de los blueprints. Esta maneja una variable VALUE_PRINTS la cuál nos da la cantidad de prints que se tienen que crear
```java
/**
     * Funcion generada para crear dos blueprints que esten con el mismo autor y una cantidad definida por el
     * usuario donde no se puedan repetir (Segundo ciclo for)
     */
    private void initializePrints() {
        Random random = new Random();
        //Dos prints que tienen que estar con el mismo autor
        for(int i = 0;i<2;i++){
            Blueprint newBp = new Blueprint("Diego Gonzalez","Blueprint"+i);
        }
        //Da valores aleatorios para cada autor para que nunca se repitan
        for(int i = 0;i<VALUE_PRINTS;i++){
            Blueprint bp = new Blueprint("Author"+random.nextInt(100)+10,"Blueprint"+i);
        }
    }
```

3. Configure su aplicación para que ofrezca el recurso "/blueprints", de manera que cuando se le haga una petición GET, retorne -en formato jSON- el conjunto de todos los planos. Para esto:

	* Modifique la clase BlueprintAPIController teniendo en cuenta el siguiente ejemplo de controlador REST hecho con SpringMVC/SpringBoot:

	```java
	@RestController
	@RequestMapping(value = "/url-raiz-recurso")
	public class XXController {
    
        
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> manejadorGetRecursoXX(){
        try {
            //obtener datos que se enviarán a través del API
            return new ResponseEntity<>(data,HttpStatus.ACCEPTED);
        } catch (XXException ex) {
            Logger.getLogger(XXController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("Error bla bla bla",HttpStatus.NOT_FOUND);
        }        
	}

	```  
	
	* Para la implementacion del recurso blueprints tenemos:
	
	```java
	@RestController
	@RequestMapping(value = "/blueprints")
	public class BlueprintAPIController {

    @Qualifier("Service")
    BlueprintsServices service;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<String> manejadorGetRecursoXX(){
        Set<Blueprint> bps = null;
        InMemoryBlueprintPersistence imbp = null;
        try {
            service = new BlueprintsServices();
            bps = service.getAllBlueprints();
        } catch(BlueprintNotFoundException e){
            e.printStackTrace();
        }catch(BlueprintPersistenceException bpPe){
            bpPe.printStackTrace();
        }
        return new ResponseEntity<String> (bps.toString(), HttpStatus.ACCEPTED);
    }
   	
	```  

	* Haga que en esta misma clase se inyecte el bean de tipo BlueprintServices (al cual, a su vez, se le inyectarán sus dependencias de persisntecia y de filtrado de puntos).
	* Para la implementación de el recurso /blueprints con el filtro de Submuestreo tenemos:
	
	```java
	@Service
	@RestController
	public class BlueprintAPIController {

	    @Autowired
	    //@Qualifier("Service")
	    BlueprintsServices service;

	    @RequestMapping(value = "/blueprints",method = RequestMethod.GET)
	    public ResponseEntity<String> manejadorGetBluePrints(){
		Set<Blueprint> bps = null;
		InMemoryBlueprintPersistence imbp = null;
		try {
		    bps = service.getAllBlueprints();
		    service.applyFilter(bps);
		} catch(BlueprintNotFoundException e){
		    e.printStackTrace();
		}catch(BlueprintPersistenceException bpPe){
		    bpPe.printStackTrace();
		}
		return new ResponseEntity<String> (bps.toString(), HttpStatus.ACCEPTED);
	    }
	```  

4. Verifique el funcionamiento de a aplicación lanzando la aplicación con maven:

	```bash
	$ mvn compile
	$ mvn spring-boot:run
	
	```
	Y luego enviando una petición GET a: http://localhost:8080/blueprints. Rectifique que, como respuesta, se obtenga un objeto jSON con una lista que contenga el detalle de los planos suministados por defecto, y que se haya aplicado el filtrado de puntos correspondiente.
	
	Peticion GET del servicio Blueprints
	![ServicioBlueprints](https://github.com/DiegoGonzalez2807/ARSW-LAB5/blob/master/img/media/JSonTest.jpg)  
	Peticion GET del servicio Blueprints con filtro
	![ServicioBlueprintsConFiltroSub](https://github.com/DiegoGonzalez2807/ARSW-LAB5/blob/master/img/media/JSonFilterTest.jpg)


5. Modifique el controlador para que ahora, acepte peticiones GET al recurso /blueprints/{author}, el cual retorne usando una representación jSON todos los planos realizados por el autor cuyo nombre sea {author}. Si no existe dicho autor, se debe responder con el código de error HTTP 404. Para esto, revise en [la documentación de Spring](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html), sección 22.3.2, el uso de @PathVariable. De nuevo, verifique que al hacer una petición GET -por ejemplo- a recurso http://localhost:8080/blueprints/juan, se obtenga en formato jSON el conjunto de planos asociados al autor 'juan' (ajuste esto a los nombres de autor usados en el punto 2).
	Para esta peticion hacemos uso de:  
	```java
	    @RequestMapping(value = "/blueprints/{author}",method = RequestMethod.GET)
    public ResponseEntity<String> manejadorGetBluePrintsByAuthor(@PathVariable String author){
        ResponseEntity<String> mensaje;
        Set<Blueprint> bps = null;
        InMemoryBlueprintPersistence imbp = null;
        try {
            bps = service.getBlueprintsByAuthor(author);
            mensaje = new ResponseEntity<String>(bps.toString(),HttpStatus.ACCEPTED);
        } catch (BlueprintNotFoundException e) {
            mensaje = new ResponseEntity<String>("No se encontro el autor",HttpStatus.NOT_FOUND);
        } catch (BlueprintPersistenceException e) {
            mensaje = new ResponseEntity<String>("Algo salio mal", HttpStatus.BAD_REQUEST);
        }
        return mensaje;
    }
	```
	
	### Para la peticion de /blueprints  
	![SericioBluePrints](https://github.com/DiegoGonzalez2807/ARSW-LAB5/blob/master/img/media/JSonTestBP.jpg) 
	### Para la peticion de /blueprints/Diego Gonzalez
	![ServicioBluePrintsByAuthor](https://github.com/DiegoGonzalez2807/ARSW-LAB5/blob/master/img/media/JSonTestBPByAuthor.jpg)  
	### Para la peticion de /blueprints/juan
	
	

6. Modifique el controlador para que ahora, acepte peticiones GET al recurso /blueprints/{author}/{bpname}, el cual retorne usando una representación jSON sólo UN plano, en este caso el realizado por {author} y cuyo nombre sea {bpname}. De nuevo, si no existe dicho autor, se debe responder con el código de error HTTP 404. 
	Para esta peticion tenemos...
	```java
	    @RequestMapping(value = "/blueprints/{author}/{bpname}")
    public ResponseEntity<String> namejadorGetBluePrint(@PathVariable String author, @PathVariable String bpname){
        Blueprint bp = null;
        InMemoryBlueprintPersistence imbp = null;
        ResponseEntity<String> mensaje;
        try{
            bp = service.getBlueprint(author,bpname);
            mensaje = new ResponseEntity<>(bp.toString(),HttpStatus.ACCEPTED);
        } catch (BlueprintNotFoundException e) {
            mensaje = new ResponseEntity<String>("No se encontro el autor",HttpStatus.NOT_FOUND);
        }
        return mensaje;
    }
	
	```  
	### Para la peticion /blueprints/Diego Gonzalez  
	![PeticionBlueprintsByAuthor](https://github.com/DiegoGonzalez2807/ARSW-LAB5/blob/master/img/media/JSonTestAuthor.jpg)  
	### Para la peticion /blueprints/Diego Gonzalez/Bleprint1  
	![PeticionBlueprint](https://github.com/DiegoGonzalez2807/ARSW-LAB5/blob/master/img/media/JSonTestBPName.jpg)
	




### Parte II

1.  Agregue el manejo de peticiones POST (creación de nuevos planos), de manera que un cliente http pueda registrar una nueva orden haciendo una petición POST al recurso ‘planos’, y enviando como contenido de la petición todo el detalle de dicho recurso a través de un documento jSON. Para esto, tenga en cuenta el siguiente ejemplo, que considera -por consistencia con el protocolo HTTP- el manejo de códigos de estados HTTP (en caso de éxito o error):

	```	java
	@RequestMapping(method = RequestMethod.POST)	
	public ResponseEntity<?> manejadorPostRecursoXX(@RequestBody TipoXX o){
        try {
            //registrar dato
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (XXException ex) {
            Logger.getLogger(XXController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>("Error bla bla bla",HttpStatus.FORBIDDEN);            
        }        
 	
	}
	```	
	
	Para nuestro caso se realiza  
	```java
	    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> manejadorPostRecursoBluePrint(@RequestBody Blueprint bp){
        ResponseEntity<?> mensaje;
        try {
            //registrar dato
            service.addNewBlueprint(bp);
            mensaje = new ResponseEntity<>(HttpStatus.CREATED);
        } catch (BlueprintPersistenceException e) {
            Logger.getLogger(BlueprintAPIController.class.getName()).log(Level.FATAL, null, e);
            mensaje = new ResponseEntity<>("EL nombre del plano ya existe",HttpStatus.NOT_ACCEPTABLE);
        }
        return mensaje;
    }
    ```  
    


2.  Para probar que el recurso ‘planos’ acepta e interpreta
    correctamente las peticiones POST, use el comando curl de Unix. Este
    comando tiene como parámetro el tipo de contenido manejado (en este
    caso jSON), y el ‘cuerpo del mensaje’ que irá con la petición, lo
    cual en este caso debe ser un documento jSON equivalente a la clase
    Cliente (donde en lugar de {ObjetoJSON}, se usará un objeto jSON correspondiente a una nueva orden:

	```	
	$ curl -i -X POST -HContent-Type:application/json -HAccept:application/json http://URL_del_recurso_ordenes -d '{ObjetoJSON}'
	```	

	Con lo anterior, registre un nuevo plano (para 'diseñar' un objeto jSON, puede usar [esta herramienta](http://www.jsoneditoronline.org/)):
	

	Nota: puede basarse en el formato jSON mostrado en el navegador al consultar una orden con el método GET.  
	
	### Atraves de la terminal de gitBash, ingresamos el comando y obtenemos.   
	![pruebaPost](https://github.com/DiegoGonzalez2807/ARSW-LAB5/blob/master/img/media/MetiendoDatos.jpg)  
	### En caso que el plano ya exita.  
	![PostFail](https://github.com/DiegoGonzalez2807/ARSW-LAB5/blob/master/img/media/ErrorPost.jpg)  
	

3. Teniendo en cuenta el autor y numbre del plano registrado, verifique que el mismo se pueda obtener mediante una petición GET al recurso '/blueprints/{author}/{bpname}' correspondiente.

	### Cuando pedimos el recurso de "/blueprints/Cristian Castellanos".    
	![CCastellanosGet](https://github.com/DiegoGonzalez2807/ARSW-LAB5/blob/master/img/media/CCastellanosBP.jpg)  
	### Cuando pedimos el recurso de "/blueprints/Cristian Castellanos/PruebaPost".  
	![PruebaPost](https://github.com/DiegoGonzalez2807/ARSW-LAB5/blob/master/img/media/PruebaPost.jpg)  
	

4. Agregue soporte al verbo PUT para los recursos de la forma '/blueprints/{author}/{bpname}', de manera que sea posible actualizar un plano determinado.


### Parte III

El componente BlueprintsRESTAPI funcionará en un entorno concurrente. Es decir, atederá múltiples peticiones simultáneamente (con el stack de aplicaciones usado, dichas peticiones se atenderán por defecto a través múltiples de hilos). Dado lo anterior, debe hacer una revisión de su API (una vez funcione), e identificar:

* Qué condiciones de carrera se podrían presentar?
	- Se tiene la primer condición de carrera de modificar un blueprint mientras que se está consultando estos. Ese comportamiento genera inconsistencia en los datos que se 	proporciona a lo usuarios debido a que uno de los planos no tendrá la misma información que la proporcionada en la búsqueda
	
	- Se tiene la condición de carrera de agregar un nuevo blueprint a la vez que se consulta estos. Ese comportamiento genera inconsistencias en los datos proporcionados 		al usuario debido a que la cantidad de planos que se entregaron en la búsqueda no concuerda con el número de planos registrados hasta el momento de la inserción del 		nuevo blueprint.
* Cuales son las respectivas regiones críticas?
	- Las regiones críticas son todos aquellos métodos que funcionen mediante peticiones HTTP (GET,POST,PUT,DELETE). Esto debido  que estas llaman un recurso compartido que es el arreglo de blueprints ya sea para modificarlo, añadir planos nuevos o eliminar el que el usuario elija. Esto puede generar problemas como deadLocks o comportamientos no deseados como condiciones de carrera en los datos

Ajuste el código para suprimir las condiciones de carrera. Tengan en cuenta que simplemente sincronizar el acceso a las operaciones de persistencia/consulta DEGRADARÁ SIGNIFICATIVAMENTE el desempeño de API, por lo cual se deben buscar estrategias alternativas.
```java
	@Component
@Qualifier("Memory")
public class InMemoryBlueprintPersistence implements BlueprintsPersistence{


    private final int VALUE_PRINTS = 5;

    private final ConcurrentHashMap<Tuple<String,String>,Blueprint> blueprints=new ConcurrentHashMap<>();
```  
#### Como solución a las secciones críticas y condiciones de carrera que se presentan. Se vuelve Thread-Safe la persistencia implementada, la cual en este caso es InMemoryBlueprintPersistence. Esto genera que el recurso compartido no pueda ser usado por más de un hilo a la vez. El recurso era el hashMap blueprints. Tenía condiciones de carrera donde si se consultaba y a la vez se insertaba, no se tenía consistencia en los datos. De igual manera, para las regiones críticas, cada hilo tendrá que esperar para poder ejecutar su función. El cambio que se hace es de volver tipo atómico el hashmap (ConcurrentHashMap).

Escriba su análisis y la solución aplicada en el archivo ANALISIS_CONCURRENCIA.txt

#### Criterios de evaluación

1. Diseño.
	* Al controlador REST implementado se le inyectan los servicios implementados en el laboratorio anterior.
	* Todos los recursos asociados a '/blueprint' están en un mismo Bean.
	* Los métodos que atienden las peticiones a recursos REST retornan un código HTTP 202 si se procesaron adecuadamente, y el respectivo código de error HTTP si el recurso solicitado NO existe, o si se generó una excepción en el proceso (dicha excepción NO DEBE SER de tipo 'Exception', sino una concreta)	
2. Funcionalidad.
	* El API REST ofrece los recursos, y soporta sus respectivos verbos, de acuerdo con lo indicado en el enunciado.
3. Análisis de concurrencia.
	* En el código, y en las respuestas del archivo de texto, se tuvo en cuenta:
		* La colección usada en InMemoryBlueprintPersistence no es Thread-safe (se debió cambiar a una con esta condición).
		* El método que agrega un nuevo plano está sujeta a una condición de carrera, pues la consulta y posterior agregación (condicionada a la anterior) no se realizan de forma atómica. Si como solución usa un bloque sincronizado, se evalúa como R. Si como solución se usaron los métodos de agregación condicional atómicos (por ejemplo putIfAbsent()) de la colección 'Thread-Safe' usada, se evalúa como B.
