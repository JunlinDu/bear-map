# Bear Map Project

This project is my implementation of the backend web server for one of the proposed project from UC Berkely's CS61B, Spring 2018. It was originally inspired by the [Open Street Map](https://wiki.openstreetmap.org/wiki/Main_Page) project from where the map data was downloaded utilized. This project is an ongoing project of mine, and it is currently being developed as a hobby project which I am continually refactoring, and making optimations and refinements to.

## Underlying Data Structres

| Function | Data Structure |
| -- | -- |
| [Map Rastering](#Rasterisation) | None, pure math </br>Alternative: Quad-tree Implementation, optimized for vectored graph (**to be implemented**) |
| [Graph Building](#Graph-Building) | HashMap |
| [Routing](#Routing) | Heap (Min Priority Queue)</br>HashMap </br>KD-Tree: Log time Node search (**to be implemented, currently linear time**)|
| [Auto Complete](#Auto-Complete) | Trie (Retrieval Tree)</br>HashMap |

</br>

## File Structures

```file structure
bear-map
├── README.md
├── bearmap.iml
├── pom.xml
├── src
│   ├── main
│   │   ├── java
│   │   │   └── AutoCompleteUtils
│   │   │       ├── Trie.java
│   │   │       └── TrieSet.java
│   │   ├── GraphBuilder
│   │   │   ├── GraphBuildingHandler.java
│   │   │   └── GraphDB.java
│   │   ├── Router
│   │   │   ├── ArrayHeapMinPQ.java
│   │   │   ├── ExtrinsicMinPQ.java
│   │   │   └── Router.java
│   │   ├── MapServer.java
│   │   └── Rasterer.java
│   ├── Static ...
│   └── test ...

```

## Running the Application Locally

| Prerequisites |
| --|
| JDK 1.8 or above |
| Apache Maven 3.3+ |
</br>

* Download the [project](https://github.com/JunlinDu/bear-map.git) and the [project dataset](https://github.com/JunlinDu/bear-map-presist-data.git), place them into a directory structured as indicated below:

```file structure
opt
├── bear-map        -- project directory
└── library-sp18    -- dataset directory
```

* From the command line, ```cd``` to the project root folder ```/bear-map```, and compile the project

```shell
mvn compile
```

* Runing the map server:

```shell
mvn exec:java -Dexec.mainClass="MapServer"
```

* Once the server has started, open your web browser and access port 4567 on localhost by typing in ```localhost:4567``` to the browser.

</br></br>

## Rasterisation

The process of rasterisation is achieved by ```Rasterer.java```. Rasterer takes the user's request from the browser, which requests a region of the world, and constructs from a group of small images a large image that covers the region that is apporiate to what is being requested. It is also the rasterer's resposibility to provide an image that covers correct distance per pixel (LonDPP) to satisfy the user's visual demand when viewed from a certian zoom level.

| Name | Function |
| -- | -- |
| [Rasterer](src/main/java/Rasterer.java) | Performs Rasterisation |

**Rastering result preview**</br>
![raster_sr](docs/rasterer_sr_ls.gif)

## Graph Building

Graph building builds a in-memory represention of the graph which the program can interact with and perform path-searching on. The dataset used for graph building is in the [OSM XML](https://wiki.openstreetmap.org/wiki/OSM_XML) format. The OSM XML dataset contains large, complex real-world mapping data, most of which (not all) are utilized, which are enough to enable major functionalities to be achieved in this project. </br>

An industry-strength XML praser, [SAX Parser](https://docs.oracle.com/javase/tutorial/jaxp/sax/parsing.html), is used in the application for parsing the XML file. Below are key XML tags in the XML file:
| Name | Description |
| -- | -- |
| [\<node>](https://wiki.openstreetmap.org/wiki/Node) | A node defines a single point in space which has an **id**, **longitude**, and **latitude**. All of which are essential for path searching. |
| [\<way>](https://wiki.openstreetmap.org/wiki/Way) | A way is a sequence of nodes that are related in certain ways, which can represent a street or an area. The graph building in the application is mainly concered with the road versions of ways. They are used for constructing edges in the graph. |
| [\<relation>](https://wiki.openstreetmap.org/wiki/Relation) | Relations are not used, it is beyond the scope of this project |
</br>

Since the OpenStreetMap OSM XML dataset is not 100% accurate in terms of marking one-ways and speed limits, they are disregarded and not implemented in the application. The  dataset was downloaded from [BBBike's free download server](https://download.bbbike.org/osm/).

| Name | Function |
| -- | -- |
| [GraphBuildingHandler](src/main/java/GraphBuilder/GraphBuildingHandler.java) | Prase the OSM XML file and load the presistent data into memory |
| [GraphDB](src/main/java/GraphBuilder/GraphDB.java) | The in-memory representation of the graph represneting the map |

## Routing

Routing take directional factors in to account to bias the dijkstra's algorithm A* Algorithm (function implemented, descriptions to be done)

Bearing, relative, absolute, curvature of the earth. </br>
Driving directions. </br>
To be implemented: KD-Tree for Log time nearset node searching. Current implementation is linear time. </br>

**Routing Preview**</br>
![routing_sr_ls](docs/routing_sr_ls.gif)

**Driving Directions Preview**</br>

## Auto Complete

Underlying data structures have been implemented. Feature to be implemented.

**Auto Complete Preview**</br>

## Deployment
