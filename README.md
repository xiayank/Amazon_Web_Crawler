# Amazon_Web_Crawler

## Introduction 

This project is a crawler of website Amazon which based on jsop API. It will
crawl the product list from Amazon and transfer it to advertisement info.

Input File
--

1.Raw Query:
```java
    [query keyword],[bid price],[campaignId]
    smart watch, 4.5, 8080, 13
    
```
2.Proxy List:
```java
    [ip],[port],[port],[username],[password]
    173.208.78.40,60099,61336,username,password
```
##Output File


1.Log File

2.Ads File
```json
{"adId":0,"campaignId":8080,"keyWords":["garmin","forerunner","15","gps","running","watch","smart","activity","fitness","tracker","large","black","blue","2","charging","data"],"relevanceScore":0.0,"pClick":0.0,"bidPrice":4.5,"rankScore":0.0,"qualityScore":0.0,"costPerClick":0.0,"position":0,"title":"garmin forerunner 15 gps running watch smart activity fitness tracker large black blue 2 charging data","price":0.0,"thumbnail":"https://images-na.ssl-images-amazon.com/images/I/51q5ZIPRPIL._AC_US218_.jpg","description":null,"brand":"Garmin","detail_url":"/gp/slredirect/picassoRedirect.html","query":"smart watch","query_group_id":13,"category":"Electronics"}
```

##Run


Set Input and outout file path:
 
>In `CrawlerMain.java`, set array `args` parameter as `[rawQueryDataFilePath], [adsDataFilePath],[proxyFilePath],[logFilePath]


Build maven application:
```text
mvn clean install
```
Run the fat jar:

```text
java -jar target/Amazon-web-crawler-1.0-SNAPSHOT-jar-with-dependencies.jar
```

