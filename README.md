# Living Progress - Build - Mentor Me API
This is the deployment guide for the Mentor Me API.

## Prerequisites
1. Java 8
2. Maven 3+
3. MySQL 5.7.6+ that supports [ST_Distance_Sphere](http://dev.mysql.com/doc/refman/5.7/en/spatial-convenience-functions.html#function_st-distance-sphere)
4. SMTP Server. You may use [FakeSMTP](https://github.com/Nilhcem/FakeSMTP) for development testing purpose
5. Chrome with postman add-on (to verify api only)

## MySQL Setup
- Create database schema with `sqls/schema.sql`.
- Create tables in each schema with `sqls/ddl.sql`.
- If you want to drop all tables please run `sqls/drop.sql` on each schema.
- If you want to clean all tables please run `sqls/clear.sql` on each schema.
- To initialize the basic data required by the application you should execute the `sqls/init.sql` on the mentorme schema. 
- If you want to prepare test data please run `sqls/testdata.sql` into the mentorme-test schema.

## API Configuration
### application configuration
Edit file `src/main/resources/application.properties`:
- **spring.datasource.url**: MySQL server connection url
- **spring.datasource.username**: MySQL Server username
- **spring.datasource.password**: MySQL Server password
- **spring.mail.host**: SMTP Server host
- **spring.mail.port**: SMTP Server port
- **server.port**: the server port on which the API will run on
- **menteeMentorProgram.defaultInstitutionalProgramId**: you can change default institutional program id for mentee mentor program with this key (optional).
- **uploadDirectory**: the upload directory path, the folder must exist before starting the services or tests
- **multipart.maxFileSize**: the max file size, you can use the default value.
- **multipart.maxRequestSize**: the max request size, in most cases you can just use the default value.

You can keep the rest of the parameters unchanged.

### log4j configuration
Edit file `src/main/resources/log4j.properties`:
- **log4j.logger.com.livingprogress.mentorme**: the log level to be used

### email template configuration
Edit file `src/main/resources/templates`:
- **subject.vm**: The email subject template
- **body.vm**: the email body template

### test configuration
Test configuration can be found and modified in `src/test/resources/test.properties`. Please note that same keys defined in `src/test/resources/test.properties` will overwrite configuration defined in `src/main/resources/application.properties`:
- **spring.datasource.url** if you want to test with different database defined in `src/main/resources/application.properties`, you should update this url (and likely the username and password too)
- **spring.mail.port**: you can change this to a different port but please make sure it doesn't conflict with ports already in use since the unit tests will start mock smtp server during test with this port.
- **cleanupUploadDirectory**: this should be set to **false** if you want to check uploaded files during tests.

Do NOT change **spring.mail.host** since since unit tests will listen on **localhost**.

### customize configurations using command line parameters or system variables
Please check all property keys in `src/main/resources/application.properties`.
You can customize any of these keys using command line parameters or system variables if you want to.

For example: you can customize server port **-Dserver.port=8087** as command line parameter or use **set server.port=8087** under windows or **export SERVER_PORT=8087** under linux.  In the Topcoder development environment weve been running the Mentor Me English Services on port 8080 and the Mentor Me Spanish Services on port 8081.

This can be done for all other parameters too.

When you have all of these set, please check [Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html) to see which takes priority.

For example if you define port in command line parameter and system variable at same time it will use command line parameter instead.

### international support
Please check message values in `src/main/resources/locale/messages.properties`
and `src/main/resources/locale/messages_es.properties`(spanish) and
`src/test/resources/locale/messages_en.properties`(english).

It contains description for creating/updating goal/task/menteeMentorGoal/menteeMentorTask.

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

## Running the Remote Application / Batch Process
**Make sure you already have the API properly configured and running as stated above before doing the following.**

### Haven On Demand API Configuration
- Register account in [havenondemand](https://havenondemand.com/) and get key after login with [api keys](https://www.havenondemand.com/account/api-keys.html).
- Free users only have 15 units and single standard index will cost 10 units, single explorer index will cost 1 unit but cannot create numeric_fields(for Custom_Fields flavor only) and we could use single index with different type for mentor and mentee.
- You can edit **havenondemand.apiKey** in `src/main/resources/application.properties` to use your api key.
- **havenondemand.forceDeleteIndex**: if set this to **true** in `src/main/resources/application.properties`, it will check exist index and recreate index, but it will not delete index with different name,
so if you have created text index with different name you must delete this index.
If you set it to **false** in `src/main/resources/application.properties`, it will load updated mentees/mentors in last 24 hours by default into the existing index.

### Google Maps Geocoding API Configuration
- Follow the [get api key page ](https://developers.google.com/maps/documentation/geocoding/get-api-key). to get an API key. Please check [api limits](https://developers.google.com/maps/documentation/geocoding/usage-limits).
- You can edit **google.geocoding.apiKey** in `src/main/resources/application.properties`.

### Verification for Remote Service Implementation
Related code is in package/directory `com.livingprogress.mentorme.remote`.
Please configure api key for google map and Haven On Demand API as stated above.

You can still verify **remoteMatchingMentees** and **remoteMatchingMentors** endpoints in postman, they shouldn't be affected by the remote service implementation.

You can also set configurations using system variables as stated in previous sections. For example **HAVENONDEMAND_APIKEY** for api key of havenondemand.

You can still prepare test data like before or you can use below steps to prepare new sample data:
- Prepare clean and test data in mysql by executing the following scripts in the specified order: `sqls/clear.sql`, `sqls/country.sql`, `sqls/state.sql`, `sqls/personal_interest.sql`, `sqls/professional_interest.sql`, `sqls/lookup.sql`, `sqls/remote.sql`.

Seting **havenondemand.forceDeleteIndex=true** is recommended in order to test new index on each run. Otherwise you will only see results if there are updates to the users in the last 24 hours.

You can run below mvn command to run remote application directly:
``` bash
mvn clean compile exec:exec
```
You can also package and run(make sure related configurations is right):
``` bash
mvn clean package
```
Move to `target` folder:
``` bash
cd target
java -jar -Dloader.main=com.livingprogress.mentorme.remote.RemoteApplication mentorme-api.jar
```
