# Origin

### Prerequisites
Docker, Git

### Build in docker
```
docker build --target rest -t origin/rest .
docker build --target aggregator -t origin/aggregator .
docker build --target storage -t origin/storage .
```

### Run in Docker Compose
```
docker-compose up 
```

### Open application
REST Service Swagger UI
```
open http://localhost:8080/swagger-ui/index.html
```

Kafka topic browser
```
open localhost:8888
```