# Bear Map Project
This project is my implementation of the backend web server for one of the proposed project from UC Berkely's CS61B, Spring 2018. It was originally inspired by the [Open Street Map](https://wiki.openstreetmap.org/wiki/Main_Page) project from where the map data was downloaded utilized. This project is an ongoing project of mine, and it is currently being developed as a hobby projec to which I am continually making optimations and refinement.

**Underlying Data Structres**
| Function | Data Structure |
| -- | -- |
| Map Rastering | None, pure math </br>Alternative: Quad-tree Implementation, optimized for vectored graph (**to be implemented**) |
| Graph Building | HashMap |
| Routing | Heap (Min Priority Queue)</br>HashMap </br>KD-Tree: Log time Node search (**to be implemented, currently linear time**)|
| Auto Complete | Trie (Retrieval Tree)</br>HashMap |

**File Structure Deisgn for Major Features**
```
bear-map-project
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

Maping data used for this project can be downloaded from [here](https://github.com/JunlinDu/bear-map-presist-data.git)


## Rasterisation
The process of rasterisation is achieved by ```Rasterer.java```. Rasterer takes the user's request from the browser, which requests a region of the world, and constructs from a group of small images a large image that covers the region that is apporiate to what is being requested. It is also the rasterer's resposibility to provide an image that covers correct distance per pixel (LonDPP) to satisfy the user's visual demand when viewed from a certian zoom level. 

| Name | Function |
| -- | -- |
| [Rasterer](https://github.com/JunlinDu/bear-map-project/blob/300bb9f45421a3b53eae8b6245ebd868ee6efa78/src/main/java/Rasterer.java) | Performs Rasterisation |

**Rastering result preview**</br>
![raster_sr](https://github.com/JunlinDu/bear-map-project/blob/300bb9f45421a3b53eae8b6245ebd868ee6efa78/docs/rasterer_sr_ls.gif)

## Graph Building
Graph building builds a in-memory represention of the graph which the program can interact with and perform path-searching on. The dataset used for graph building is in the [OSM XML](https://wiki.openstreetmap.org/wiki/OSM_XML) format. The dataset contains complex real-world mapping data sets, therefore there is a portion (a few aspects, not all) of the data is utilized, which will be enough to enable major functionalities to be achieved in the project. The dataset can be downloaded from [here](https://download.bbbike.org/osm/).

An industry-strength XML praser, [SAX Parser](https://docs.oracle.com/javase/tutorial/jaxp/sax/parsing.html), is used for parsing the XML file.

| Name | Function |
| -- | -- |
| [GraphBuildingHandler](https://github.com/JunlinDu/bear-map-project/blob/876e7513b9f72b17992914793e17e7c427d30f4e/src/main/java/GraphBuilder/GraphBuildingHandler.java) | Prase the OSM XML file and load the presistent data into memory |
| [GraphDB](https://github.com/JunlinDu/bear-map-project/blob/876e7513b9f72b17992914793e17e7c427d30f4e/src/main/java/GraphBuilder/GraphDB.java) | The in-memory representation of the graph represneting the map |

## Routing
Routing take directional factors in to account to bias the dijkstra's algorithm A* Algorithm (function implemented, descriptions to be done)

Bearing, relative, absolute.
curvature of the earth.
Driving directions.
KD-Tree for Log time nearset node searching. Current implementation is linear time.

**Routing Preview**</br>
![routing_sr_lr](https://github.com/JunlinDu/bear-map-project/blob/876e7513b9f72b17992914793e17e7c427d30f4e/docs/routing_sr_ls.gif)

**Driving Directions Preview**

## Auto Complete

**Auto Complete Preview**

