
### Escuela Colombiana de Ingeniería
### Arquitecturas de Software - ARSW
### Elaborado por: Laura Natalia Rojas y Ana Maria Duran
## Ejercicio Introducción al paralelismo - Hilos - Caso BlackListSearch


### Dependencias:
####   Lecturas:
*  [Threads in Java](http://beginnersbook.com/2013/03/java-threads/)  (Hasta 'Ending Threads')
*  [Threads vs Processes]( http://cs-fundamentals.com/tech-interview/java/differences-between-thread-and-process-in-java.php)

### Descripción
  Este ejercicio contiene una introducción a la programación con hilos en Java, además de la aplicación a un caso concreto.
  

**Parte I - Introducción a Hilos en Java**

1. De acuerdo con lo revisado en las lecturas, complete las clases CountThread, para que las mismas definan el ciclo de vida de un hilo que imprima por pantalla los números entre A y B.
2. Complete el método __main__ de la clase CountMainThreads para que:
	1. Cree 3 hilos de tipo CountThread, asignándole al primero el intervalo [0..99], al segundo [99..199], y al tercero [200..299].
	2. Inicie los tres hilos con 'start()'.
	3. Ejecute y revise la salida por pantalla. 
	4. Cambie el incio con 'start()' por 'run()'. Cómo cambia la salida?, por qué?.
       5. El comportamiento cambia, con run() se ejecuta el metodo, no en un hilo separado sino en el hilo principal. Esto quiere decir que los números se imprimirán en el mismo hilo que llama a run, sin ejecución concurrente (porque se imprimen secuencialmente, uno tras otro).
          - ![img.png](img/img.png)
       6. Con start() los números se imprimen concurrentemente y en la consola podemos ver como llegan a mezclarse las ejecuciones, esto debido a que los hilos se ejecutan en paralelo.
          - ![img_1.png](img/img_1.png)
       
**Parte II - Ejercicio Black List Search**


Para un software de vigilancia automática de seguridad informática se está desarrollando un componente encargado de validar las direcciones IP en varios miles de listas negras (de host maliciosos) conocidas, y reportar aquellas que existan en al menos cinco de dichas listas. 

Dicho componente está diseñado de acuerdo con el siguiente diagrama, donde:

- HostBlackListsDataSourceFacade es una clase que ofrece una 'fachada' para realizar consultas en cualquiera de las N listas negras registradas (método 'isInBlacklistServer'), y que permite también hacer un reporte a una base de datos local de cuando una dirección IP se considera peligrosa. Esta clase NO ES MODIFICABLE, pero se sabe que es 'Thread-Safe'.

- HostBlackListsValidator es una clase que ofrece el método 'checkHost', el cual, a través de la clase 'HostBlackListDataSourceFacade', valida en cada una de las listas negras un host determinado. En dicho método está considerada la política de que al encontrarse un HOST en al menos cinco listas negras, el mismo será registrado como 'no confiable', o como 'confiable' en caso contrario. Adicionalmente, retornará la lista de los números de las 'listas negras' en donde se encontró registrado el HOST.

![](img/Model.png)

Al usarse el módulo, la evidencia de que se hizo el registro como 'confiable' o 'no confiable' se dá por lo mensajes de LOGs:

INFO: HOST 205.24.34.55 Reported as trustworthy

INFO: HOST 205.24.34.55 Reported as NOT trustworthy


Al programa de prueba provisto (Main), le toma sólo algunos segundos análizar y reportar la dirección provista (200.24.34.55), ya que la misma está registrada más de cinco veces en los primeros servidores, por lo que no requiere recorrerlos todos. Sin embargo, hacer la búsqueda en casos donde NO hay reportes, o donde los mismos están dispersos en las miles de listas negras, toma bastante tiempo.

Éste, como cualquier método de búsqueda, puede verse como un problema [vergonzosamente paralelo](https://en.wikipedia.org/wiki/Embarrassingly_parallel), ya que no existen dependencias entre una partición del problema y otra.

Para 'refactorizar' este código, y hacer que explote la capacidad multi-núcleo de la CPU del equipo, realice lo siguiente:

1. Cree una clase de tipo Thread que represente el ciclo de vida de un hilo que haga la búsqueda de un segmento del conjunto de servidores disponibles. Agregue a dicha clase un método que permita 'preguntarle' a las instancias del mismo (los hilos) cuantas ocurrencias de servidores maliciosos ha encontrado o encontró.

2. Agregue al método 'checkHost' un parámetro entero N, correspondiente al número de hilos entre los que se va a realizar la búsqueda (recuerde tener en cuenta si N es par o impar!). Modifique el código de este método para que divida el espacio de búsqueda entre las N partes indicadas, y paralelice la búsqueda a través de N hilos. Haga que dicha función espere hasta que los N hilos terminen de resolver su respectivo sub-problema, agregue las ocurrencias encontradas por cada hilo a la lista que retorna el método, y entonces calcule (sumando el total de ocurrencuas encontradas por cada hilo) si el número de ocurrencias es mayor o igual a _BLACK_LIST_ALARM_COUNT_. Si se da este caso, al final se DEBE reportar el host como confiable o no confiable, y mostrar el listado con los números de las listas negras respectivas. Para lograr este comportamiento de 'espera' revise el método [join](https://docs.oracle.com/javase/tutorial/essential/concurrency/join.html) del API de concurrencia de Java. Tenga también en cuenta:

	* Dentro del método checkHost Se debe mantener el LOG que informa, antes de retornar el resultado, el número de listas negras revisadas VS. el número de listas negras total (línea 60). Se debe garantizar que dicha información sea verídica bajo el nuevo esquema de procesamiento en paralelo planteado.

	* Se sabe que el HOST 202.24.34.55 está reportado en listas negras de una forma más dispersa, y que el host 212.24.24.55 NO está en ninguna lista negra.


**Parte II.I Para discutir la próxima clase (NO para implementar aún)**

La estrategia de paralelismo antes implementada es ineficiente en ciertos casos, pues la búsqueda se sigue realizando aún cuando los N hilos (en su conjunto) ya hayan encontrado el número mínimo de ocurrencias requeridas para reportar al servidor como malicioso. Cómo se podría modificar la implementación para minimizar el número de consultas en estos casos?, qué elemento nuevo traería esto al problema?

- Podemos modificar la implementación para que los hilos se detengan tan pronto como se alcance el número mínimo de ocurrencias requeridas. Esto se puede lograr utilizando una bandera global de terminación.
  - Es una variable booleana que se utiliza para indicar si los hilos deben detener su trabajo. Esta bandera se define en el hilo principal y es accesible para todos los hilos secundarios.
  - Los hilos secundarios deben verificar periódicamente el estado de esta bandera. Cuando el hilo principal detecta que se ha alcanzado el número mínimo de ocurrencias, cambia el valor de la bandera para indicar que los hilos deben detenerse.
  - Utilizar una bandera global de terminación permite al hilo principal gestionar la ejecución de los hilos secundarios de manera eficiente sin necesidad de que los hilos se coordinen entre sí.


**Parte III - Evaluación de Desempeño**

A partir de lo anterior, implemente la siguiente secuencia de experimentos para realizar las validación de direcciones IP dispersas (por ejemplo 202.24.34.55), tomando los tiempos de ejecución de los mismos (asegúrese de hacerlos en la misma máquina):

1. Un solo hilo.
    - ![Time1Hilo](https://github.com/user-attachments/assets/7ce3d3dd-f12d-4a5a-b2f1-6a9e1b49714c)
2. Tantos hilos como núcleos de procesamiento (haga que el programa determine esto haciendo uso del [API Runtime](https://docs.oracle.com/javase/7/docs/api/java/lang/Runtime.html)).
   - ![TimeNucleosProcesamiento](https://github.com/user-attachments/assets/001c4b40-20f6-43ae-8035-3e3d41609a2e)
3. Tantos hilos como el doble de núcleos de procesamiento.
   - ![TimeDobleNucleosProcesamiento](https://github.com/user-attachments/assets/bf372eff-031c-47ce-bd7d-65f65c402d3b)
4. 50 hilos.
   - ![Time50Hilos](https://github.com/user-attachments/assets/0b8da2d7-8949-4219-a195-0f989ad4fc3c)
5. 100 hilos.
   - ![Time100Hilos](https://github.com/user-attachments/assets/fc51eca9-d4c9-414f-b7af-39f97e4d5c5f)

Al iniciar el programa ejecute el monitor jVisualVM, y a medida que corran las pruebas, revise y anote el consumo de CPU y de memoria en cada caso. ![](img/jvisualvm.png)

Con lo anterior, y con los tiempos de ejecución dados, haga una gráfica de tiempo de solución vs. número de hilos. Analice y plantee hipótesis con su compañero para las siguientes preguntas (puede tener en cuenta lo reportado por jVisualVM):
1. 1 hilo
   * ![CPU1Hilo](https://github.com/user-attachments/assets/9a58518b-fa0a-428e-8307-3e3af32a91a9)
   * ![Time1HiloVisualVM](https://github.com/user-attachments/assets/98cc6e83-e635-4b6b-9a59-2f16533d09d6)
2. Tantos hilos como núcleos de procesamiento
   * ![CPUNucleos](https://github.com/user-attachments/assets/d7284230-d91d-4bde-a4c1-5ca073ce8c19)
   * ![CPUNucleosVisualVM](https://github.com/user-attachments/assets/9797c38f-d47a-4b8d-801a-959cd3fd9667)
3. Tantos hilos como el doble de núcleos de procesamiento.
   * ![CPUDobleNucleos](https://github.com/user-attachments/assets/74bfffaf-9cea-48fc-9237-0b69767f24aa)
   * ![TimeDobleNucleosVM](https://github.com/user-attachments/assets/1baf073a-1ef6-4d39-9c91-a4b011f5b52e)
4. 50 hilos.
   * ![CPU50Hilos](https://github.com/user-attachments/assets/facd0fe0-2588-4dd1-b39b-dc9d2856d146)
   * ![Time50HilosVisualVM](https://github.com/user-attachments/assets/0862f92a-1aa1-4901-9c30-dc751a42dc6c)

     Acá se puede ver que el tiempo de solución fue tan pequeño que no se alcanza a apreciar el monitoreo de la CPU
5. 100 Hilos.
   * ![CPU100Hilos](https://github.com/user-attachments/assets/a319aaa9-9283-4d35-a5d5-8b1c25077253)
   * ![Time100HilosVisualVM](https://github.com/user-attachments/assets/3282bfdb-df54-45d2-bc06-63839cfb236c)
6. Gráfica de tiempo de solución vs número de hilos
   
   * ![image](https://github.com/user-attachments/assets/119a7d22-36eb-4d87-a70a-73b613d31950)

**Parte IV - Ejercicio Black List Search**

1. Según la [ley de Amdahls](https://www.pugetsystems.com/labs/articles/Estimating-CPU-Performance-using-Amdahls-Law-619/#WhatisAmdahlsLaw?):

	![](img/ahmdahls.png), donde _S(n)_ es el mejoramiento teórico del desempeño, _P_ la fracción paralelizable del algoritmo, y _n_ el número de hilos, a mayor _n_, mayor debería ser dicha mejora. Por qué el mejor desempeño no se logra con los 500 hilos?, cómo se compara este desempeño cuando se usan 200?. 

2. Cómo se comporta la solución usando tantos hilos de procesamiento como núcleos comparado con el resultado de usar el doble de éste?.

3. De acuerdo con lo anterior, si para este problema en lugar de 100 hilos en una sola CPU se pudiera usar 1 hilo en cada una de 100 máquinas hipotéticas, la ley de Amdahls se aplicaría mejor?. Si en lugar de esto se usaran c hilos en 100/c máquinas distribuidas (siendo c es el número de núcleos de dichas máquinas), se mejoraría?. Explique su respuesta.



