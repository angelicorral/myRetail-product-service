# myRetail-product-service
myRetail Product Service is a service that aggregates a Product entity from different product sources

# Authorization
*  This app uses Basic Authentication.

#####  Roles:
* ADMIN 
 (username: "admin", password: "adminPassword")
* READER 
(username: "reader", password: "readerPassword")

# Authentication
* Both GET and PUT endpoints are available to ADMIN users
* Only GET endpoints are available to READER users

# REST APIs

##### GET /v1/products/{id}

   - To ensure SLA, both upstream services have configurable timeout values.
   - 500 INTERNAL SERVER ERROR will be returned in case any of the upstream services encounter a timeout.
   - The Product aggregate is composed from RedskyProductRepository's ProductEnity and PricingRepository's PricingProductEntity.
   - The Product aggregate will return 404 NOT FOUND if nothing is returned from RedskyProductRepository's
   - The Product aggregate will still return 200 SUCCESS even if no value is found from PricingRepository

##### PUT /v1/products/{id}

 - Use json body with same structure as GET response
 - The following validation will be performed, else 400 BAD REQUEST is returned
 - the path id must be the same as provided id in body
 - the price values must be greater than 0 and must be in USD
 - If no record exists in the database, 404 NOT FOUND is returned

# To run locally

- Pull the dse cassandra image

```
docker pull datastax/dse-server:6.7.7
```

- Create the dse cassandra container, expose port 9042

```
docker run -d -e DS_LICENSE=accept --name cassandra-6-7 -p 9042:9042 dse-server:6.7.7
```

- Open cqlsh

```
docker exec -ti cassandra-6-7 cqlsh
```

- Run the create table scripts and insert scripts in the cql folder
- Or run the app as an executable jar

```
cd /service/target
java -jar myRetail-product-service-1.0.0-SNAPSHOT.jar -Dspring.active.profiles=local
```
- Or run the application using mvn

```
mvn spring-boot:run -Dspring.profiles.active=local
```

- Check if app is up by posting this on browser

```
http://localhost:8080/info
```




