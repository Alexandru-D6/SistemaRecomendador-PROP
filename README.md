# Sistema recomanador

## Miembros
- Pol Rivero Sallent
- Pablo José Galván Calderón
- Ferran de la Varga Antoja : [ferranjrr](https://github.com/ferranjrr)
- Alexandru Dumitru Maroz : [Alexandru-D6](https://github.com/Alexandru-D6)


## Documentació

- [`DOC/old/PrimerLliurament.pdf`](./DOC/old/PrimerLliurament.pdf): Casos d'ús, esquema conceptual de les dades i documentació sobre els algorismes KMS1 i KNN.
- [`DOC/old/SegonLliurament.pdf`](./DOC/old/SegonLliurament.pdf): Diagrames de classes del domini, presentació i dades. Documentació sobre els algorismes KMS1, KNN i Mixt.
- [`DOC/TercerLliurament.pdf`](./DOC/TercerLliurament.pdf): Manual d'usuari i testing del programa.


## Com executar

**Avis**

Abans d'executar el programa, caldrà assignar almenys una clau de Google Search API per habilitar la funció de buscador d'imatges. La configuració es realitza en l'arxiu [`FONT/Utilities/ImageFinder.java`](./FONT/Utilities/ImageFinder.java).

**Executar la GUI:**
```
make gui
```
- Es recomana llegir l'apartat *manual d'usuari* de la tercera entrega.
- Si hi ha algun error, recompilar el programa amb `make clean gui`.

**Executar el driver:**
```
make run
```
- Veure l'apartat [Funcionament del driver](#funcionament-del-driver) per a més informació.
- Si hi ha algun error, recompilar el programa amb `make clean run`.
- El driver executarà les comandes guardades al fitxer `commands.txt` automàticament. Es pot utilitzar un altre fitxer amb `make run ARGS=myFile.txt`

**Executar el tester (main) de KmeansSlopeOne y KNearestNeighbours:**
```shell
./testing.sh configFile.info fileQueries.txt

# Exemple (cal canviar inputqueries.txt per un fitxer de queries):
./testing.sh Movielens/2250/datasetTester.info inputqueries.txt

```
- `configFile.info` és el path al fitxer de configuració d'un dataset. Ha de ser relatiu al directori `DATA` i no hauria de contenir el nom dels fitxers Known i Unknown (per assegurar que el programa no pot utilitzar els resultats de Unknown directament).
- `inputqueries.txt` és el path al fitxer de queries, generat a partir dels fitxers known i unknown corresponents. Aquest fileQueries s'ha de generar de la següent manera:

```
100 -> nr de Querries que tindra el ficher.
2378 4 10 8 -> Primera Querry, on els elements representen el següent; userId, nr KnownRatings, nr. UnknownRatings, nr. outputRecomanacions.
466 3.5 -> Primer Item amb la seva valoració.
1252 0.5
4034 4
58 1
236 -> Primer Item sense valoració.
163
2692
1358
2699
2640
4886
1639
172
592
```

**Executar el tester (main) de Rendiment de tots el algoritmes:**
```shell
make test TEST_CLASS=runTimeTest ARGS="configFile.info testSize"

# Exemple (cal canviar my_input_queries.txt per un fitxer de queries):
make test TEST_CLASS=TestTimeExecution ARGS="Movielens/250/dataset.info 3"
```
- `configFile.info` és el path al fitxer de configuració d'un dataset. Ha de ser relatiu al directori `DATA` i no hauria de contenir el nom dels fitxers Known i Unknown (per assegurar que el programa no pot utilitzar els resultats de Unknown directament).
- `testSize` és la quantitat de recomanacions que calculara per cada algoritme y cada distancia per despres fer la seva mitjana.

**Executar els tests unitaris de la classe DistanceStrategy:**
```
make junit
```

## Organització de les carpetes
**DATA:**
Conté les dades dels corresponents jocs de proves en format .csv.

**DOC:**
Documentació del projecte (diagrama de classes, diagrama de casos d'ús, explicaions detallades...)

**FONT:**
Conté tot el codi del projecte: 
- Packages:
    - `Data`:
    Conté les classes dels controladors de la capa de dades
        - `GenericDBCtrl`
        - `ItemDBCtrl`
        - `RatingDBCtrl`
        - `UserDBCrl`

    - `Domain`:
    Conté els packages de la capa de domini:
        - `DataInterface`: Classes per accedir a la capa de dades, i la Factoria per crear-les.
        - `Distance`: Classes relacionades amb el càlcul de distàncies.
        - `Recommendation`: Classes i packages dels algorismes de recomanació SlopeOne i KNearestNeighbours.
        - `TransactionControllers`: Classes del patró Transacció.
        - `Value`: Classes dels tipus d'atributs i valors 

    - `Exceptions`: Tractament d'excepcions
        - `DBException`
        - `ImplementationError`
        - `ObjectAlreadyExistsException`
        - `ObjectDoesNotExistException`

    - `Presentation`: Aquí hi hauran les futures classes de la capa de presentació.

    - `Utilities`: Classes que hem creat que ens han sigut d'utilitat: `CSVReader`, `Pair` `RandomNumber`

- `Driver.java`: Permet probar totes les opcions del programa a través de comandes (consultar com utilitzar-lo [aquí](#funcionament-del-driver)).

- `JUnitDistance.java`: Tester per la classe DistanceStrategy (i tots els fills)
    Una DistanceStrategy, donat un vector de distancies parcials, calcula la distancia total (es a dir, calcula el modul del vector de distancies parcials)
    ```
    Objecte de la prova: DistanceStrategy
    Altres elements integrats: cap
    Fitxers de dades necessaris: cap
    Valors estudiats: Proves de tipus "Caixa Gris" amb valors positius, negatius, zeros, nulls, NaN i Infinit.
    Efectes estudiats: Correctesa del valor de retorn i temps d'execució.
    Operativa: Executar aquest joc de proves des de la terminal utilitzant "make junit"
    ```
- `Main_AlgorithmExecution.java`:
    Tester per executar de manera ràpida els alogoritmes de recomanació
    El resultat pot ser tant una recomanació per l'usuari com el valor DCG de tots dos algorismes per a un DataSet concret.
    ```
    Objecte de la prova: KmeansSlopeOne y KNearestNeighbours
    Altres elements integrats: TxLectorCSV, Cluster, SlopeOne, KNN
    Fitxers de dades necessaris: Qualsevol DataSet (items y ratings) i el commandsAE
    Valors estudiats: Són probes de caixa negra, ja que estem comprobant l'output final de cada classe.
    Efectes estudiats: Excecució dels algoritmes
    Operativa: Executar el programa a través de la comanda [make clean test TEST_CLASS=Main_AlgorithmExecution].
    ```
- `Main_RendimientoKmeans_kidea.java`:
    Tester per observar els temps d'execució del Kmeans, Elbow and Silhouette method
    El resultat serà d'una banda el temps d'execució i d'altra banda la sortida dels dos mètodes per obtenir la k ideal pel DataSet 2250 (encara que se li pot aplicar qualsevol DataSet).
    ```
    Objecte de la prova: Kmeans
    Altres elements integrats: TxLectorCSV, Cluster
    Fitxers de dades necessaris: Qualsevol DataSet (items y ratings)
    Valors estudiats: Són probes de caixa negra, ja que el que ens interesa és el valor final del que hauria de retornar (segons Elbow i Silhouette).
    Efectes estudiats: Velocitat dels algorismes
    Operativa: Executar el programa a través de la comanda [make clean test TEST_CLASS=Main_RendimientoKmeans_kidea].
    ```

- `Main_TestingClusterAlgorithm.java`:
    Tester per observar si el cluster agrupa els ususaris de manera correcta.
    Els resultats són les agrupacions dels ususaris dentre diferents clústers i els seus tamanys.
    Destacar que per la naturalesa de l'algoritme, com que al principi i entre execucions s'utilitzen coordenades random, pot passar que el cúster no agrupi correctament
    Per comprobar el correcte funcionament s'hauria d'executar-lo varies vegades per corroboarar la desició final.
    ```
    Objecte de la prova: Kmeans.
    Altres elements integrats: Cap.
    Fitxers de dades necessaris: DataSet Kmeans2D (items, ratings.db, ratings.db2).
    Valors estudiats: És una proba gris, ja que traballem amb un DataSet reduït amb el proposit de saber com serà l'agrupació després de l'excecució
    Efectes estudiats: Agrupació del dataset.
    Operativa: Executar aquest programa a través de la comanda [make clean test TEST_CLASS=Main_TestingClusterAlgorithm].
    ```
    
- `Main_TestingAllAlgorithms.java`:
    Tester que comprova el correcte funcionament dels algorismes, utilitza un 
    dataset generat especialment per a aquesta prova, concretament el Kmeans2D db3.
    ```
    Objecte de la prova: KmeansSlopeOne, KNN, Hybrid.
    Altres elements integrats: TxLectorCSV, TxObtenirRecomanacions.
    Fitxers de dades necessaris: DataSet Kmeans2D construit per aquestes proves (items y ratings).
    Valors estudiats: Son probes de caixa negra, ja que el que són interessa és el valor final del que hauria de retornar.
    Efectes estudiats: Precisió y correct funcionament dels algoritmes.
    Operativa: Executar aquest programa a través de la comanda [make clean test TEST_CLASS=Main_TestingAllAlgorithms].
    ```

**LIB:**
Hi ha totes les llibreries necessàries per poder executar el programa.



## Funcionament del driver

Descripció de les comandes acceptades (no distingueix entre majúscules i minúscules):

---
- `CONFIGURE_MODEL`

    Es determinarà:
    - els pesos de tots els atributs
    - l'algorisme a seguir pel càlcul de distàncies
    - l'algorisme a seguir pel càlcul de recomanacions

---

- `EVALUATE`

    El Sistema llegeix dos fitxers CSV:
    - `DATA/[DATASET]/ratings.test.known.csv`
    - `DATA/[DATASET]/ratings.test.unkonwn.csv`
    
    El Sistema prepara un conjunt de queries a partir dels continguts dels CSV.
    
    S'executa cada query i calcula el seu DCG.
    El Sistema mostrarà la mitjana del DCG de totes les queries.

---

- `SEE_ATTR`

    S'imprimeix els atributs amb els pesos corresponents

---

- `DELETE_ATTR index`

    - `index` és l'índex d'un atribut
    
    Elimina l'atribut amb l'índex = `index`.
    Es recomana utilitzar `SEE_ATTR` primer per veure l'índex de cada atribut.

---

- `SET_WEIGHT index newWeight`

    - `index` de l'atribut
    - `newWeight`: nou pes que tindrà l'atribut amb índex = `index`.
    
    Es recomana utilitzar `SEE_ATTR` primer per veure l'índex de cada atribut.

---

- `NEW_ITEM value[0]..value[n-1]`

    - `value` = vector d'Strings, cada string és el valor d'un atribut de l'item.
    
    Es crea un nou item amb els values passats.

---

- `DELETE_ITEM id`

    S'elimina l'item amb identificador = `id`.

---

- `NEW_USER`

    Es crea un nou usuari sense valoracions i imprimeix l'id del nou usuari.

---

- `DELETE_USER id`

    S'elimina l'usuari amb identificador = `id`.

---

- `RATE_ITEM idUser idItem rating`

    - `idUser`: enter identificador de l'usuari
    - `idItem`: enter Identificador del Item
    - `rating`: double valoració
    
    Es crea una nova valoració de l'usuari `idUser` de l'item `idItem`.

---

- `SET_DISTANCE distance` 
    - `distance`: Algorisme a utilitzar per calcular les distàncies. Ha de ser una de: {`Euclidean`, `Manhattan`, `Average`, `AverageSquare`}

---

- `SET_ALGORITHM algorithm`
    - `algorithm`: Algorisme a utilitzar per calcular les recomanacions. Ha de ser una de: {`KNN`, `KMS1`}

---

- `RECOMMEND idUsuari idItemsQuery[0]..idItemsQuery[n-1] Q`

    - `idUsuari`: enter identificador de l'usuari
    - `idItemsQuery`: vector de ids de Items
    - `Q`: enter
    
    Retorna els `Q` millors items (del vector `IdItemsQuery`) recomanats per l'usuari `idUsuari` segons l'algorisme indicat de càlcul de recomanació.

---

- `SAVE_DATA datasetSortida`

    - `datasetSortida`: nom del directori (relatiu a `DATA`) on es guardarà el resultat.
    
    Crea el directori `DATA/[datasetSortida]` (si cal) i hi guarda tots els fitxers CSV necessaris per preservar l'estat del sistema. Quan es torna a iniciar el programa, seleccionar el nou dataset (`DATA/[datasetSortida]/dataset.info`) per restaurar les dades guardades.

---

- `EXIT`

    Sortir del programa

---


**Exemple:**

Donat un fitxer `commands.txt` com el següent:

```r
#######################################
# Lines starting with '#' are ignored #
#######################################

# Use the biggest dataset
Movielens/6750

# Name of the attribute to be used as ID (same name as the header)
id

# Maximum rating
5.0


# COMMANDS START HERE
# Set recommendation algorithm to KNearestNeighbours (not needed, KNN is the default)
set_algorithm KNN

# Set distance strategy to euclidean (not needed, Euclidean is the default)
set_distance Euclidean

# Evaluate the model
evaluate

# Set recommendation algorithm to KMeans+SlopeOne
set_algorithm KMS1

# The first time that KMS1 is used, it will build the clusters (it will be slower)
evaluate

# Set distance strategy to manhattan
set_distance Manhattan

# Evaluate the model again (it will be faster)
evaluate

# Save the data in a new dataset
save_data OUTPUT

exit
```
Una possible sortida del driver seria:
```
Welcome to the driver!
Please enter the name of your dataset (directory located at DATA):
>Movielens/6750
Enter the name of the attribute to be used as id:
>id
Enter the maximum rating:
>5.0
Initializing model, please wait... Done!

[S'OMET LLISTAT DE COMANDES]

>set_algorithm KNN
Recommendation algorithm set to KNearestNeighbours

>set_distance Euclidean
Distance function set to Euclidean

>evaluate
Evaluating model...
Resulting NDCG: 0.8199592586438621
Elapsed time: 0.23615155 seconds

>set_algorithm KMS1
Recommendation algorithm set to KMeansSlope1

>evaluate
Evaluating model...
Resulting NDCG: 0.8854455720319117
Elapsed time: 6.932362609 seconds

>set_distance Manhattan
Distance function set to Manhattan

>evaluate
Evaluating model...
Resulting NDCG: 0.8838488121818249
Elapsed time: 1.903211606 seconds

>save_data OUTPUT
Data saved in DATA/OUTPUT

>exit
```
