(!) Use elasticsearch version 1.3, spring-data-elasticsearch does not support higher versions.

1) Download *.zip;
2) Unpackage;
3) Run gradlew;
4) In ../src/main/resources/logback.xml, change path to log file;
5) From source directory, start console and write command: 'gradlew build';

Dependencies:
- Elasticsearch version 1.3.x, tested on 1.3.0;
- RabbitMQ, tested on 3.5.3;
- Java 1.8, tested on 1.8.0_71;

