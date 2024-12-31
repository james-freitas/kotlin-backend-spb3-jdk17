# API JMeter
Application with API accessing DB to test JMeter

## ✔️ Requirements
- Java Development Kit 21 (jdk21)

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
SPRING_DATASOURCE_URL=jdbc:postgresql://127.0.0.1:5431/apijmeter
SPRING_DATASOURCE_USERNAME=apijmeter
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

## 🧪 Performance tests
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

## 📈 Prometheus
1. To view metrics on Prometheus open in any browser: `http://localhost:9090`
2. Send some requests to the API
3. In **Graph** > **Graph** tab search for `http_server_requests_seconds_count` and press **Execute**