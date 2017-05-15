package crawler;

import ad.Ad;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

//import org.apache.log4j.Logger;


/**
 * Created by john on 10/13/16.
 */


public class AmazonCrawler {
    //https://www.amazon.com/s/ref=nb_sb_noss?field-keywords=nikon+SLR&page=2
    private static final String AMAZON_QUERY_URL = "https://www.amazon.com/s/ref=nb_sb_noss?field-keywords=";
    private static final String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36";
    private final String authUser = "bittiger";
    private final String authPassword = "cs504";
    private List<String> proxyList;
    private List<String> titleList;
    private List<String> categoryList;
    private HashSet crawledUrl;

    BufferedWriter logBFWriter;

    private int index = 0;

    public AmazonCrawler(String proxy_file, String log_file) throws IOException {
        crawledUrl = new HashSet();
       // token2String(tokenize("GCoolers The First Smart cats Cooler Bags featuring a Wireless Bluetooth Thermometer. Use an app for iOS / Android to Monitor Temperature Inside Cooler for Camping, Tailgating, Outdoor Barbecues"));

        initProxyList(proxy_file);

        initHtmlSelector();

        initLog(log_file);

    }

    public void cleanup() {
        if (logBFWriter != null) {
            try {
                logBFWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<String> tokenize(String str) throws IOException {
        if(str == null )return null;

        List<String> tokens = new ArrayList<>();

        //tokenize
        StandardTokenizer standardTokenizer = new StandardTokenizer();
        standardTokenizer.setReader(new StringReader(str));
        standardTokenizer.reset();

        //
        CharArraySet charArraySet = CharArraySet.copy(StandardAnalyzer.STOP_WORDS_SET);
        StopFilter stopFilter = new StopFilter(standardTokenizer, charArraySet);

        LowerCaseFilter lowerCaseFilter = new LowerCaseFilter(stopFilter);

        while(lowerCaseFilter.incrementToken()){
            tokens.add(lowerCaseFilter.getAttribute(CharTermAttribute.class).toString());
        }
//        for(String token: tokens ){
//            System.out.println("token test = "+token);
//        }
        return tokens;

    }

    String token2String(List<String> tokens){
        String str = new String();
        for(String token : tokens){
            str += (token + ' ');
        }
        //System.out.println(str);
        return str.trim();
    }

    //raw url: https://www.amazon.com/KNEX-Model-Building-Set-Engineering/dp/B00HROBJXY/ref=sr_1_14/132-5596910-9772831?ie=UTF8&qid=1493512593&sr=8-14&keywords=building+toys
    //normalizedUrl: https://www.amazon.com/KNEX-Model-Building-Set-Engineering/dp/B00HROBJXY
    private String normalizeUrl(String url) {
        int i = url.indexOf("ref");
        String normalizedUrl = url.substring(0, i - 1);
        return normalizedUrl;
    }

    private void initProxyList(String proxy_file) {
        proxyList = new ArrayList<String>();
        try (BufferedReader br = new BufferedReader(new FileReader(proxy_file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                String ip = fields[0].trim();
                proxyList.add(ip);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Authenticator.setDefault(
                new Authenticator() {
                    @Override
                    public PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                authUser, authPassword.toCharArray());
                    }
                }
        );

        System.setProperty("http.proxyUser", authUser);
        System.setProperty("http.proxyPassword", authPassword);
        System.setProperty("socksProxyPort", "61336"); // set proxy port
    }

    private void initHtmlSelector() {
        titleList = new ArrayList<String>();
        titleList.add(" > div > div > div > div.a-fixed-left-grid-col.a-col-right > div.a-row.a-spacing-small > div:nth-child(1)  > a > h2");
        titleList.add(" > div > div > div > div.a-fixed-left-grid-col.a-col-right > div.a-row.a-spacing-small > a > h2");

        categoryList = new ArrayList<String>();
        //#refinements > div.categoryRefinementsSection > ul.forExpando > li:nth-child(1) > a > span.boldRefinementLink
        categoryList.add("#refinements > div.categoryRefinementsSection > ul.forExpando > li > a > span.boldRefinementLink");
        categoryList.add("#refinements > div.categoryRefinementsSection > ul.forExpando > li:nth-child(1) > a > span.boldRefinementLink");


    }

    private void initLog(String log_path) {
        try {
            File log = new File(log_path);
            // if file doesnt exists, then create it
            if (!log.exists()) {
                log.createNewFile();
            }
            FileWriter fw = new FileWriter(log.getAbsoluteFile());
            logBFWriter = new BufferedWriter(fw);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void setProxy() {
        //rotate
        if (index == proxyList.size()) {
            index = 0;
        }
        String proxy = proxyList.get(index);
        System.setProperty("socksProxyHost", proxy); // set proxy server
        index++;
    }

    private void testProxy() {
        System.setProperty("socksProxyHost", "199.101.97.149"); // set proxy server
        System.setProperty("socksProxyPort", "61336"); // set proxy port
        String test_url = "http://www.toolsvoid.com/what-is-my-ip-address";
        try {
            Document doc = Jsoup.connect(test_url).userAgent(USER_AGENT).timeout(10000).get();
            String iP = doc.select("body > section.articles-section > div > div > div > div.col-md-8.display-flex > div > div.table-responsive > table > tbody > tr:nth-child(1) > td:nth-child(2) > strong").first().text(); //get used IP.
            System.out.println("IP-Address: " + iP);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public List<Ad> GetAdBasicInfoByQuery(String query, double bidPrice, int campaignId, int queryGroupId) throws IOException {
        List<Ad> products = new ArrayList<>();
        try {
            if (false) {
                testProxy();
                return products;
            }

            setProxy();
            final int numOfPage = 2;
            for(int curPage = 1; curPage <= numOfPage; curPage++) {
                String pagePara = (curPage == 1)? "" : "&page ="+ curPage;
                System.out.println("start crawl page "+ curPage + ".");
                String url = AMAZON_QUERY_URL + query + pagePara;

                //System.out.println("myprintquery"+query);
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
                headers.put("Accept-Encoding", "gzip, deflate, sdch, br");
                headers.put("Accept-Language", "en-US,en;q=0.8");
                // System.out.println("myprint url"+url);
                try {
                    Document doc = Jsoup.connect(url).headers(headers).userAgent(USER_AGENT).timeout(100000).get();


                    //Document doc = Jsoup.connect(url).userAgent(USER_AGENT).timeout(100000).get();

                    //System.out.println(doc.text());
                    //mutiple elements

                    Elements results = doc.select("li[data-asin]");
                    //when "li" tag include "data-asin",select it.
                    System.out.println("num of results = " + results.size());

                    for (int i = 0; i < results.size(); i++) {
                        Ad ad = new Ad();

                        //detail url
                        String detail_path = "#result_" + Integer.toString(i) + " > div > div > div > div.a-fixed-left-grid-col.a-col-left > div > div > a";
                        Element detail_url_ele = doc.select(detail_path).first();
                        if (detail_url_ele != null) {
                            String detail_url = detail_url_ele.attr("href");
                            System.out.println("detail = " + detail_url);
                            String normalizedUrl = normalizeUrl(detail_url);
                            if (crawledUrl.contains(normalizedUrl)) {
                                logBFWriter.write("crawled url:" + normalizedUrl);
                                logBFWriter.newLine();
                                continue;
                            }
                            crawledUrl.add(normalizedUrl);
                            System.out.println("normalized url  = " + normalizedUrl);

                            ad.detail_url = normalizedUrl;
                        } else {
                            logBFWriter.write("cannot parse detail for query:" + query + ", title: " + ad.title);
                            logBFWriter.newLine();
                            continue;
                        }

                        ad.query = query;
                        ad.query_group_id = queryGroupId;

                        //title
                        ad.keyWords = new ArrayList<>();
                        //#result_2 > div > div > div > div.a-fixed-left-grid-col.a-col-right > div.a-row.a-spacing-small > div:nth-child(1) > a > h2
                        //#result_3 > div > div > div > div.a-fixed-left-grid-col.a-col-right > div.a-row.a-spacing-small > div:nth-child(1) > a > h2
                        //#result_0 > div > div > div > div.a-fixed-left-grid-col.a-col-right > div.a-row.a-spacing-small > div:nth-child(1) > a > h2
                        //#result_1 > div > div > div > div.a-fixed-left-grid-col.a-col-right > div.a-row.a-spacing-small > div:nth-child(1) > a > h2
                        for (String title : titleList) {
                            //titleList -> multiple title HTML selector
                            String title_ele_path = "#result_" + Integer.toString(i) + title;
                            Element title_ele = doc.select(title_ele_path).first();
                            if (title_ele != null) {
                                //selector not work, change to next

                                ad.title = token2String(tokenize(title_ele.text()));
                                System.out.println("title = " + ad.title);
//                          ad.title = title_ele.text();
                                ad.keyWords = tokenize(title_ele.text());
                                break;
                            }
                        }
                        //all the selector do not work
                        if (ad.title == "") {
                            logBFWriter.write("cannot parse title for query: " + query);
                            logBFWriter.newLine();
                            continue;
                        }
                        //#result_0 > div > div > div > div.a-fixed-left-grid-col.a-col-left > div > div > a > img

                        //thumbnail
                        String thumbnail_path = "#result_" + Integer.toString(i) + " > div > div > div > div.a-fixed-left-grid-col.a-col-left > div > div > a > img";
                        Element thumbnail_ele = doc.select(thumbnail_path).first();
                        if (thumbnail_ele != null) {
                            //System.out.println("thumbnail = " + thumbnail_ele.attr("src"));
                            ad.thumbnail = thumbnail_ele.attr("src");
                        } else {
                            logBFWriter.write("cannot parse thumbnail for query:" + query + ", title: " + ad.title);
                            logBFWriter.newLine();
                            continue;
                        }

                        //brand
                        String brand_path = "#result_" + Integer.toString(i) + " > div > div > div > div.a-fixed-left-grid-col.a-col-right > div.a-row.a-spacing-small > div > span:nth-child(2)";
                        Element brand = doc.select(brand_path).first();
                        if (brand != null) {
                            //System.out.println("brand = " + brand.text());
                            ad.brand = brand.text();
                        }
                        //#result_2 > div > div > div > div.a-fixed-left-grid-col.a-col-right > div:nth-child(3) > div.a-column.a-span7 > div.a-row.a-spacing-none > a > span > span > span
                        ad.bidPrice = bidPrice;
                        ad.campaignId = campaignId;
                        ad.price = 0.0;
                        //#result_0 > div > div > div > div.a-fixed-left-grid-col.a-col-right > div:nth-child(3) > div.a-column.a-span7 > div.a-row.a-spacing-none > a > span > span > span

                        //price
                        String price_whole_path = "#result_" + Integer.toString(i) + " > div > div > div > div.a-fixed-left-grid-col.a-col-right > div:nth-child(3) > div.a-column.a-span7 > div.a-row.a-spacing-none > a > span > span > span";
                        String price_fraction_path = "#result_" + Integer.toString(i) + " > div > div > div > div.a-fixed-left-grid-col.a-col-right > div:nth-child(3) > div.a-column.a-span7 > div.a-row.a-spacing-none > a > span > span > sup.sx-price-fractional";
                        Element price_whole_ele = doc.select(price_whole_path).first();
                        if (price_whole_ele != null) {
                            String price_whole = price_whole_ele.text();
                            //System.out.println("price whole = " + price_whole);
                            //remove ","
                            //1,000
                            if (price_whole.contains(",")) {
                                price_whole = price_whole.replaceAll(",", "");
                            }

                            try {
                                ad.price = Double.parseDouble(price_whole);
                            } catch (NumberFormatException ne) {
                                // TODO Auto-generated catch block
                                ne.printStackTrace();
                                //log
                            }
                        }

                        Element price_fraction_ele = doc.select(price_fraction_path).first();
                        if (price_fraction_ele != null) {
                            //System.out.println("price fraction = " + price_fraction_ele.text());
                            try {
                                ad.price = ad.price + Double.parseDouble(price_fraction_ele.text()) / 100.0;
                            } catch (NumberFormatException ne) {
                                ne.printStackTrace();
                            }
                        }
                        //System.out.println("price = " + ad.price );

                        //category
                        for (String category : categoryList) {
                            Element category_ele = doc.select(category).first();
                            if (category_ele != null) {
                                //System.out.println("category = " + category_ele.text());
                                ad.category = category_ele.text();
                                break;
                            }
                        }
                        if (ad.category == "") {
                            logBFWriter.write("cannot parse category for query:" + query + ", title: " + ad.title);
                            logBFWriter.newLine();
                            continue;
                        }
                        products.add(ad);
                    }

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            }catch (Exception e){
            logBFWriter.write(e.getStackTrace().toString());
        }
            return products;
        }

}
