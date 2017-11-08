# logparser
This is a simple spring-boot-batch project that should find IP addresses in the provided access file that exceed the threshold of specified requests for the specified duration after the specified start date(think DDoS or quota exceeded)
## What it does:
1. Loads all entries from the specified log file into a mysql database using spring-batch
2. Finds any specific IP addresses that have a number of entries exceeding the specified threshold during the specified duration as of the specified start date
3. Print offending IP addresses to the commandline
4. Loads offending IP addresses to the mysql database with a reason why they were   
## How to build and run
### Configure and build
1. Open `logparsers/src/resources/application.yml`
    and replace `localhost` with your current ip address for the property `spring:datasource:url` 
2. Build the project using maven: `mvn install`
3. Use the supplied `docker-compose.yml` file to easily stand up a mysql database:

From the same directory of the file run: `docker-compose up`
4. Ensure you have a log file with entries matching the following format:
    
    `YYYY-DD-MM HH:mm:ss.SSS|IP address|"request type"|status|client`
    
    IE: `2017-01-01 00:00:23.003|192.168.169.194|"GET / HTTP/1.1"|200|"Mozilla/5.0 (Windows NT 10.0; Win64; x64)...`
### Execute
1. The application will be built into `target/Parser.jar` 
2. From the jar location run it like the below example:

`java -jar Parser.jar --accesslog=/path/to/file --startDate=2017-01-01.13:00:00 --duration=hourly --threshold=100`
