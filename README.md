Distributed Systems Assignment 2024 CrowdFunding
===

# Start Spring

```bash
mvn spring-boot:run
```

# Start postgres db as container

```bash
docker run --name crowdfunding-db --rm \
-e POSTGRES_PASSWORD=pass123 \
-e POSTGRES_USER=dbuser \
-e POSTGRES_DB=crowdfunding \
-d \
-p 5432:5432 \
-v ds-lab-vol:/var/lib/postgresql/data \
postgres:14
```

## Dockerize
```bash
./mvnw package -Dmaven.test.skip
```
start
```bash
docker-compose up
```
docker compose
```bash
docker-compose up
```

### **Notice**

If you use git protocol in frontend build, run 
```bash
export DOCKER_BUILDKIT=0
export COMPOSE_DOCKER_CLI_BUILD=0

docker-compose up --build


```
# Links

* [Postgres Docker Hub](https://hub.docker.com/_/postgres)
* [JPA EntityManager example in Spring Boot](https://www.bezkoder.com/jpa-entitymanager-spring-boot/)
* [JPA/Hibernate Persistence Context](https://www.baeldung.com/jpa-hibernate-persistence-context)
