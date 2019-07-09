# Aggregation Consumer Service

The service listens at `TCP 9000` port, and consumers `csv` messages. The service then aggregates the data if the numbers of messages are equal to `SIZE` param flushing it into a file at given location 

## Design
1. The consumer service listens at at 9000 TCP port.
2. For each line(UTF-8) consumer-service passes it to aggregation-service.
3. consumer-service open up the socket with producer on a different coroutine backed by an IO thread pool.
4. aggregation-service processes the request in the same thread, however for dumping data into the file it switches into a different thread.
5. The size of data aggregation, target file path and the port can be configured in `application.conf` file. Note that we could control the effective value with environment variables also.
    
### Requirements to run/build
1. Gradle 5.4.1, Gradle wrapper is shipped together to download teh gradle version
2. Kotlin 1.3.40+ (Only if running locally)
3. JDK 8+
4. Docker

### Instructions to use
The project could either be run from gradle, or directly with the jar. Following command will create docker image and starts the aggregation service.
```bash
#From project root
SIZE=1000 ./gradlew composeUp
```

`SIZE` is environment variable which gets replaced in the application config. It specifies the size of message set, after which aggregation service has to dump data. 
The task above also builds the _fat-jar_ of the project. There is an entry point which needs to be specified. Either use IDE or commandLine to start the services by giving entry point in the argument.
```bash
#API Service
SIZE=1000 java -jar build/libs/aggregation-consumer-1.0.jar -s API
```   

At this point we can send data to the API as written in the example as
```bash
java -jar src/test/resources/producer.jar  --tcp
```

While the services are running tests can be run for verification. Functional tests are provided which cover end to end cases for each service taking them as black-box. We also have test suite for configuration.

After development is done, complete end to end tests can be done as
```bash
#stop containers
./gradlew composeDown

# Note that we have to delete directory at the path, or test will fail
rm -rf /tmp/aggregation-consumer/

#run tests
./gradlew test
``` 

### Data Assumptions
1. 4th data point, for the recent value is always big enough to fit in `long` data type.
2. The `SIZE` will always be judicially used, so we can always keep data in memory for a batch.s
3. Some precision loss when computing average of floating point number is acceptable.  
4. The sum of data point 5, will be large and it is safer to use BigInteger.

### Key Design/Architecture Decisions

#### Build System
Gradle provides enough power required for building the package and is among most popular build system in Java world.

#### Packaging System
The project is packed into a fat-jar which includes all the non jdk dependencies. This jar is further packed into `OpenJDK:11-slim` based docker image. 

#### Programming Language/Platform
JVM platform and Kotlin was selected simply for my familiarity and preference. The code is compiled to `JDK8` bytecode to have wider reach as higher versions are still not widely adopted. However it is being run at `JRE11` due to major improvements in JVM and GC.

#### Config System
HOCON config is selected due to it being *typesafe* and more *powerful* than plain yaml and json based configs. HOCON also uses environment variables. Thus many of the properties can be configured at the site by just changing the environment variables platform.  

#### Web Server Framework
KTor is an extremely lightweight framework for writing micro services. Supports NIO and is very easy to configure. It is based on Kotlin coroutines to allow sub-thread level task switching, without blocking for NIO. 

#### RxJava/2
RxJava/2 implements observer pattern and provides a monadic-like api to easily compose functions(chaining!) and do error handling. It is un-opinionated on multithreading and provides utilities to the developers to configure concurrency.

#### Dependency Injection
Guice is a feature packed DI framework, making it easy to provide dependencies.   

### Salient Features

- I tried to keep minimal blocking. `Ktor` provides coroutine wrappers over Java NIO Sockets. Within the code synchronization is done over very small cpu-bound code (like reference switching).
- Aggregation service uses RxChain for executing the logic, but uses a custom IO thread pool for saving data, with back pressure. This prevents blocking the IO thread used by Ktor, but also adds back-pressure to the consumer to avoid OOM or creating too many threads/tasks, which is the case with the default schedulers in Rx library. 
- Coding Style: I tried to keep coding _type safe, immutable and null safe_ wherever possible. However some places I used mutability for efficiency. For e.g. reference to message data is mutable and switched, to avoid the need of deep-copy    
- Docker images are tagged with the commit id. 
- Functional tests are given for both aggregation and consumer service, testing the actual scenario given in the example.             

### Required Improvements  
The project was highly limited as a POC and several things can be improved. Following things come to the mind before similar could be attempted for production.
1. The service could have supported both tcp and udp, which could be controlled from config. 
2. We could transform floating point number to a different units (x1000?), to improve precision.
3. For the csv field mapping in the code, I could have used better names If I was aware of domain.    
4. HOCON is hard to configure when every property needs to managed at the site. For that Consul or ConfigMap might be a better choice.
5. Metrics should be collected, may be with Micrometer. They should be exported, either by exposing an endpoint or writing them into another system like statsd, from where they could be exported into prometheus or influxdb. 
6. Overall code quality could still be improved. Due to time constraints I have cut some corners like forced change from nullable to non-nullable types etc.  
    
