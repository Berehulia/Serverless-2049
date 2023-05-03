# SERVERLESS 2049++

## Overview

![image](https://user-images.githubusercontent.com/58912194/225840150-11853256-092f-4821-8de4-a803df9de79c.png)

## Scenarios

### 01. HTTP triggered function writes to the serverless NoSQL database

**Description**  
- The serverless function, which triggers 1000 times and writes a record into the serverless NoSQL database.

[Scenario](https://github.com/Berehulia/Serverless-2049/blob/master/scenarios/01/scenario-generic.yaml)

#### AWS

**Java 11 runtime**

|                  | Memory, MB | Execution cost, $ | Execution time, ms | Cold start, ms |
|:----------------:|:----------:|:-----------------:|:------------------:|:--------------:|
| Average cheapest |    512     |    0.000000092    |         10         |      4751      |
| Average fastest  |    512     |    0.000000092    |         10         |      4751      |

**Java 11 runtime (ARM)**

|                  | Memory, MB | Execution cost, $ | Execution time, ms | Cold start, ms |
|:----------------:|:----------:|:-----------------:|:------------------:|:--------------:|
| Average cheapest |    512     |    0.000000068    |         10         |      4076      |
| Average fastest  |    512     |    0.000000068    |         10         |      4076      |

**Java 11 runtime (SnapStart)**

|                  | Memory, MB | Execution cost, $ | Execution time, ms | Cold start, ms |
|:----------------:|:----------:|:-----------------:|:------------------:|:--------------:|
| Average cheapest |    256     |    0.000000076    |         18         |      8454      |
| Average fastest  |    768     |    0.000000139    |         10         |      3098      |

**Custom runtime (Java 11 & GraalNI)**

|                  | Memory, MB | Execution cost, $ | Execution time, ms | Cold start, ms |
|:----------------:|:----------:|:-----------------:|:------------------:|:--------------:|
| Average cheapest |    256     |    0.000000034    |         8          |      696       |
| Average fastest  |    1024    |    0.000000134    |         7          |      477       |


**Quarkus**

|                  | Memory, MB | Execution cost, $ | Execution time, ms | Cold start, ms |
|:----------------:|:----------:|:-----------------:|:------------------:|:--------------:|
| Average cheapest |    512     |    0.000000168    |         19         |      9212      |
| Average fastest  |    1024    |    0.000000202    |         12         |      4732      |


**Quarkus GraalVM**

|                  | Memory, MB | Execution cost, $ | Execution time, ms | Cold start, ms |
|:----------------:|:----------:|:-----------------:|:------------------:|:--------------:|
| Average cheapest |    256     |    0.000000034    |         7          |      864       |
| Average fastest  |    2048    |    0.000000234    |         7          |      399       |

**Java 11 runtime (Micronaut)**

|                  | Memory, MB | Execution cost, $ | Execution time, ms | Cold start, ms |
|:----------------:|:----------:|:-----------------:|:------------------:|:--------------:|
| Average cheapest |    768     |    0.000000176    |         13         |      5222      |
| Average fastest  |    2048    |    0.000000400    |         11         |      4253      |

**Java 11 runtime (Micronaut & GraalNI)**

|                  | Memory, MB | Execution cost, $ | Execution time, ms | Cold start, ms |
|:----------------:|:----------:|:-----------------:|:------------------:|:--------------:|
| Average cheapest |    256     |    0.000000038    |         8          |      475       |
| Average fastest  |    2048    |    0.000000302    |         8          |      468       |


**Python 3.9**

|                  | Memory, MB | Execution cost, $ | Execution time, ms | Cold start, ms |
|:----------------:|:----------:|:-----------------:|:------------------:|:--------------:|
| Average cheapest |    256     |    0.000007195    |         18         |      463       |
| Average fastest  |    2048    |    0.000029602    |         9          |      439       |


**Python 3.9 (ARM)**

|                  | Memory, MB | Execution cost, $ | Execution time, ms | Cold start, ms |
|:----------------:|:----------:|:-----------------:|:------------------:|:--------------:|
| Average cheapest |    256     |    0.000006962    |         16         |      439       |
| Average fastest  |    1024    |    0.000012403    |         8          |      437       |


### 02. HTTP triggered function writes to serverless storage

**Description**
- The serverless function, which triggers 50 times and writes a record into the serverless storage.

[Scenario](https://github.com/Berehulia/Serverless-2049/blob/master/scenarios/02/scenario-generic.yaml)

#### AWS

**Java 11 runtime**

|                  | Memory, MB | Execution cost, $ | Execution time, ms | Cold start, ms |
|:----------------:|:----------:|:-----------------:|:------------------:|:--------------:|
| Average cheapest |    256     |     0.0000130     |        3092        |     28235      |
| Average fastest  |    8192    |     0.0000790     |        587         |      2185      |

**Java 11 runtime (ARM)**

|                  | Memory, MB | Execution cost, $ | Execution time, ms | Cold start, ms |
|:----------------:|:----------:|:-----------------:|:------------------:|:--------------:|
| Average cheapest |    256     |     0.0000088     |        2592        |     24697      |
| Average fastest  |    8192    |     0.0000550     |        508         |      2074      |

**Java 11 runtime (SnapStart)**

|                  | Memory, MB | Execution cost, $ | Execution time, ms | Cold start, ms |
|:----------------:|:----------:|:-----------------:|:------------------:|:--------------:|
| Average cheapest |    256     |     0.0000131     |        3127        |     28667      |
| Average fastest  |    8192    |     0.0000740     |        548         |      2551      |

**Custom runtime (Java 11 & GraalNI)**

|                  | Memory, MB | Execution cost, $ | Execution time, ms | Cold start, ms |
|:----------------:|:----------:|:-----------------:|:------------------:|:--------------:|
| Average cheapest |    1024    |     0.0000320     |        1890        |      2569      |
| Average fastest  |    2048    |     0.0000410     |        1218        |      1953      |
