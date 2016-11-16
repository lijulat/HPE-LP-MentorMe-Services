# Living Progress - Build - Mentor Me API

Deployment Guide

### Description

Living Progress - Build - Mentor Me API.

## Prerequisites

1. Java 8
2. Maven3+
3. MySQL 5.7.6+ that supports [ST_Distance_Sphere](http://dev.mysql.com/doc/refman/5.7/en/spatial-convenience-functions.html#function_st-distance-sphere)
4. SMTP Server You may use [FakeSMTP](https://github.com/Nilhcem/FakeSMTP)
5. Chrome with postman(to verify api only)

## Configuration


### application configuration
Edit `src/main/resources/application.properties`.
you must change **spring.datasource.url**, **spring.datasource.username**, **spring.datasource.password** to match your mysql configuration and 
**spring.mail.host**, **spring.mail.port**(more configurations please check commented email configurations for example auth,ttls and etc) to match your smtp configurations.
Others are recommend to not change but you may change with your need.
You may change port with key **server.port**, default is 8080.


### log4j configuration
Edit `src/main/resources/log4j.properties`.
You can change log level used in application with key **log4j.logger.com.livingprogress.mentorme**.

### email template configuration
Edit `src/main/resources/templates`.
It exists **subject.vm** and **body.vm** in nested folder with email name.

### test configuration
Edit `src/test/resources/test.properties`.
Same key defined `src/test/resources/test.properties` will overwrite configuration defined in `src/main/resources/application.properties`.
You must change **spring.datasource.url** if you want to test with different database defined in `src/main/resources/application.properties`.
You may change  **spring.mail.port ** to different port but please make sure not conflicts with ports in your computer it will start mock smtp server during test with this port.
so please not change **spring.mail.host** since it will listen **localhost**.


### custom configurations using command line parameters or system variables
Please check all property keys in `src/main/resources/application.properties`.
You can custom using command line parameters or system variables easily.
For example custom server port **-Dserver.port=8087** as command line parameter or using **set server.port=8087** under windows or **export SERVER_PORT=8087** under linux.
similar for database,email related configurations.


Details about order please check [Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html).
For example if you define port in command line parameter and system variable at same time it will use command line parameter.

### default institutional program id
Edit `src/main/resources/application.properties`.
You can change default institutional program id for mentee mentor program with key **menteeMentorProgram.defaultInstitutionalProgramId**.

### upload file configuration
Edit `src/main/resources/application.properties`.
You can change upload directory with key **uploadDirectory**, the folder must exist before application starts or tests.

You can change max size for **multipart.maxFileSize**, **multipart.maxRequestSize**.

If you want to view uploaded files during test please set **cleanupUploadDirectory** to **false** `src/test/resources/test.properties` and check **uploadDirectory**.

### international support
Please check message values in `src/main/resources/locale/messages.properties` 
and `src/main/resources/locale/messages_es.properties`(spanish) and 
`src/test/resources/locale/messages_en.properties`(english).

It contains description for creating/updating goal/task/menteeMentorGoal/menteeMentorTask.

### Haven On Demand API
Register account in [havenondemand](https://havenondemand.com/) and get key after login with [api keys](https://www.havenondemand.com/account/api-keys.html).
Free users only have 15 units and single standard index will cost 10 units, single explorer index will cost 1 unit but cannot create numeric_fields(for Custom_Fields flavor only) and we could use single index with different type for mentor and mentee.
You can edit **havenondemand.apiKey** in `src/main/resources/application.properties`.

If you set **havenondemand.forceDeleteIndex=true** in `src/main/resources/application.properties`, it will check exist index and recreate index, but it will not delete index with different name,
so if you have created text index with different name you must delete this index. 
If you set **havenondemand.forceDeleteIndex=false** in `src/main/resources/application.properties`, and exist index it will load updated mentees/mentors in last 24 hours by default.

### Google Maps Geocoding API
Please [get api key ](https://developers.google.com/maps/documentation/geocoding/get-api-key).
Please check [api limits](https://developers.google.com/maps/documentation/geocoding/usage-limits).
You can edit **google.geocoding.apiKey** in `src/main/resources/application.properties`.

## Mysql setup 
Create schemas with `sqls/schema.sql`.
Create tables in above schemas with `sqls/ddl.sql`.
If you want to drop all tables please run `sqls/drop.sql`.
If you want to clean all tables please run `sqls/clear.sql`.
If you want to prepare test data please run `sqls/testdata.sql`.


## Running Tests
Make sure your configurations are right and there are no special characters in file path for example whitespace.

``` bash
mvn clean test
```

You can also test with coverage report in `target/site/jacoco/index.html` after run below command.

``` bash
mvn clean test jacoco:report
```

## Check code style
You can run below command to check code style using [Checkstyle plugin](https://maven.apache.org/plugins/maven-checkstyle-plugin/)

``` bash
mvn clean checkstyle:checkstyle
```

## Deployment
You can run below mvn command to run application directly.
``` bash
mvn clean spring-boot:run
```
You can also package and run(make sure related configurations is right for example upload directory exists before run).
``` bash
mvn clean package
```
Move to `target` folder
Start api service listening `8080`
``` bash
java -jar mentorme-api.jar
```

## Swagger 
Open **http://editor.swagger.io/** and copy  `docs/swagger.yaml` to verify.

## Verification
Prepare clean and test data in mysql with `sqls/clear.sql` and `sqls/testdata.sql`.
Import Postman collection `docs/postman.json` with environment variables `docs/postman-env.json`.
You can test basic auth with username=test{X} X could be 1-14, password=password, please use basic auth feature of Postman to verify.
Almost all requests will use JWT token auth defined in environment variable, but you can change to use basic auth easily.

## Verification for Remote Service Implementation
Related codes in package/directory `com.livingprogress.mentorme.remote`.
Please prepare api key for google map and Haven On Demand API.
It will invoke real remote service so test cases are not covered.

You can still verify **remoteMatchingMentees** and **remoteMatchingMentors** endpoints in postman like previous.
You can still set configurations using system variables like before for example **HAVENONDEMAND_APIKEY** for api key of havenondemand.
You can still prepare test data like before or you can use below steps to prepare new sample data.
Prepare clean and test data in mysql with order `sqls/clear.sql`,`sqls/country.sql`,`sqls/state.sql`,`sqls/personal_interest.sql`,`sqls/professional_interest.sql`,`sqls/lookup.sql`,`sqls/remote.sql`.

I recommend to use **havenondemand.forceDeleteIndex=true** to test new index every time.
Otherwise you have to update and then index and see latest result in last 24 hours.

You can run below mvn command to run remote application directly.
``` bash
mvn clean compile exec:exec
```
You can also package and run(make sure related configurations is right).
``` bash
mvn clean package
```
Move to `target` folder
``` bash
java -jar -Dloader.main=com.livingprogress.mentorme.remote.RemoteApplication mentorme-api.jar
```
