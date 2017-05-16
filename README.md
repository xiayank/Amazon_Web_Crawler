# Amazon_Web_Crawler

## Introduction 

This project is a crawler of website Amazon which based on jsop API. It will
crawl the product list from Amazon and transfer it to advertisement info.

## Input File


1.Raw Query:
```text
    [query keyword],[bid price],[campaignId]
    smart watch, 4.5, 8080, 13
    
```
2.Proxy List:
```text
    [ip],[port],[port],[username],[password]
    173.208.78.40,60099,61336,username,password
```
## Output File


1.Log File

2.Ads File
```json
{"adId":0,"campaignId":8080,"keyWords":["garmin","forerunner","15","gps","running","watch","smart","activity","fitness","tracker","large","black","blue","2","charging","data"],"relevanceScore":0.0,"pClick":0.0,"bidPrice":4.5,"rankScore":0.0,"qualityScore":0.0,"costPerClick":0.0,"position":0,"title":"garmin forerunner 15 gps running watch smart activity fitness tracker large black blue 2 charging data","price":0.0,"thumbnail":"https://images-na.ssl-images-amazon.com/images/I/51q5ZIPRPIL._AC_US218_.jpg","description":null,"brand":"Garmin","detail_url":"/gp/slredirect/picassoRedirect.html","query":"smart watch","query_group_id":13,"category":"Electronics"}
```

## Run


Set Input and outout file path:
 
>In `CrawlerMain.java`, set array `args` parameter as [rawQueryDataFilePath], [adsDataFilePath],[proxyFilePath],[logFilePath]


Build maven application:
```bash
mvn clean install
```
Run the fat jar:

```bash
java -jar target/Amazon-web-crawler-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Dependecies

#### 1. jackson : deal with json data
```xml
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-annotations</artifactId>
            <version>2.8.3</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-core</artifactId>
            <version>2.8.3</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.8.3</version>
        </dependency>
```

#### 2. jsoup : provide crawler API
```xml
        <dependency>
            <groupId>org.jsoup</groupId>
            <artifactId>jsoup</artifactId>
            <version>1.10.2</version>
        </dependency>
```

#### 3. lucene: tokenize data
```xml
        <dependency>
            <groupId>org.apache.lucene</groupId>
            <artifactId>lucene-core</artifactId>
            <version>6.5.0</version>
        </dependency>
        <dependency>
            <groupId>org.apache.lucene</groupId>
            <artifactId>lucene-analyzers-common</artifactId>
            <version>6.5.0</version>
        </dependency>
```

### Plugin : 
Build jar with dependencies
```xml
    <build>
        <plugins>
            <plugin>
                <artifactId>maven-assembly-plugin</artifactId>
                <version>3.0.0</version>
                <configuration>
                    <descriptorRefs>
                        <descriptorRef>jar-with-dependencies</descriptorRef>
                    </descriptorRefs>
                    <archive>
                        <manifest>
                            <mainClass>CrawlerMain</mainClass>
                        </manifest>
                    </archive>
                </configuration>
                <executions>
                    <execution>
                        <id>make-assembly</id> <!-- this is used for inheritance merges -->
                        <phase>package</phase> <!-- bind to the packaging phase -->
                        <goals>
                            <goal>single</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>1.7</source>
                    <target>1.7</target>
                </configuration>
            </plugin>
        </plugins>
    </build>
```