# SERVERLESS 2049++

## Overview

![image](https://user-images.githubusercontent.com/58912194/225840150-11853256-092f-4821-8de4-a803df9de79c.png)

## Scenarios

### 01. From WEB to the serverless NoSQL database

**Description**  
- The serverless function, which triggers 1000 times and writes a record into the serverless NoSQL database.

[Scenario](https://github.com/Berehulia/Serverless-2049/blob/master/scenarios/01/scenario-generic.yaml)

#### AWS

| **Java11Function** | Memory, MB | Execution cost, $ | Execution time, ms | Cold start, ms |
|:------------------:|:----------:|:-----------------:|:------------------:|:--------------:|
|  Average cheapest  |    256     |    0.000000071    |         16         |      8201      |
|  Average fastest   |    1024    |    0.000000202    |         11         |      2440      |

| **Java11ArmFunction** | Memory, MB | Execution cost, $ | Execution time, ms | Cold start, ms |
|:---------------------:|:----------:|:-----------------:|:------------------:|:--------------:|
|   Average cheapest    |    512     |    0.000000068    |         10         |      4076      |
|    Average fastest    |    512     |    0.000000068    |         10         |      4076      |
---

