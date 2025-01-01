# Sample Kotlin Backend Application
Sample API using Spring Boot

## ✔️ Requirements
- Java Development Kit 17 (jdk17)

## 🍔 Stack
- Spring boot 3.2.4
- PostgreSQL
- Prometheus

## 📖 Swagger
1. Run the application: `docker-compose up`, `./gradlew bootRun`
2. Open in any browser: `http://localhost:8080/docs`

## ✈️ How to run locally
1. First add all environment variables or run through the IDE using the `local.env` file
```shell
## Variables you have to create on system or env file
SPRING_DATASOURCE_URL=jdbc:postgresql://127.0.0.1:5431/sample
SPRING_DATASOURCE_USERNAME=sample
SPRING_DATASOURCE_PASSWORD=
```
2. Start the dependencies
```shell
## run docker compose
docker-compose up
```
3. Execute the application (it will start on port 8080)
```shell
## start web application
./gradlew bootRun
```

## 🧪 Performance tests with JMeter
1. Put the [Postgres jar](https://repo1.maven.org/maven2/org/postgresql/postgresql/42.2.20/postgresql-42.2.20.jar) on JMeter's lib folder
2. Start the dependencies
```shell
## run docker compose
docker-compose up
```
3. Execute the application (it will start on port 8080)
```shell
## start web application
./gradlew bootRun
```
4. Run JMeter tests which are on jmeter's folder

## ⚙️ Performance tests with k6

1. Start the dependencies
```shell
## run docker compose
docker-compose up
```
2. Execute the application (it will start on port 8080)
```shell
## start web application
./gradlew bootRun
```
3. Run the k6 tests
```
k6 run stress-test/k6-get-all.js  
k6 run stress-test/k6-create-categories.js  
```

## 📈 Prometheus
1. To view metrics on Prometheus open in any browser: `http://localhost:9090`
2. Send some requests to the API
3. In **Graph** > **Graph** tab search for `http_server_requests_seconds_count` and press **Execute**

## 📊 Grafana
1. To view metrics on Grafana open in any browser: `http://localhost:3000/login` (user: **admin**, password: **admin**)
2. Send some requests to the API
3. Go to **Home** > **Dashboards** > **Services** and checkout the following dashboards:
    - JVM (Micrometer)
    - Spring Boot Statistics & Endpoint Metrics