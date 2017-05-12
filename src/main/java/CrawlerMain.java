/**
 * Created by john on 10/12/16.
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;

import java.io.IOException;
import java.util.List;

import ad.Ad;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import crawler.AmazonCrawler;


public class CrawlerMain {
    public static void main(String[] args) throws IOException {
        args = new String[]{"/Users/NIC/Documents/504_BankEnd/HW/HW3_Clawer/MyCode/Amazon_web_crawler/InputFile/RawQuery3.txt", "/Users/NIC/Documents/504_BankEnd/HW/HW3_Clawer/MyCode/Amazon_web_crawler/InputFile/proxylist_bittiger.csv",
                "/Users/NIC/Documents/504_BankEnd/HW/HW3_Clawer/MyCode/Amazon_web_crawler/OutputFile/adsData.txt","/Users/NIC/Documents/504_BankEnd/HW/HW3_Clawer/MyCode/Amazon_web_crawler/OutputFile/logFile.txt"};
//        args = new String[]{"rawQueryData_path\\rawQuery.txt", "proxyFile_path\\Proxylist.csv",
//                "adsDataFile_path\\adsData.txt","logFile_path\\logFile.txt"};
        if(args.length < 2)
        {
            System.out.println("Usage: Crawler <rawQueryDataFilePath> <adsDataFilePath> <proxyFilePath> <logFilePath>");
            System.exit(0);
        }
        ObjectMapper mapper = new ObjectMapper();
        String rawQueryDataFilePath = args[0];
        String adsDataFilePath = args[2];
        String proxyFilePath = args[1];
        String logFilePath = args[3];
        AmazonCrawler crawler = new AmazonCrawler(proxyFilePath, logFilePath);
        File file = new File(adsDataFilePath);
        // if file doesnt exists, then create it
        if (!file.exists()) {
            file.createNewFile();
        }

        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        try (BufferedReader br = new BufferedReader(new FileReader(rawQueryDataFilePath))) {

            String line;
            while ((line = br.readLine()) != null) {
                if(line.isEmpty())
                    continue;
                System.out.println(line);
                String[] fields = line.split(",");
                String query = fields[0].trim();
                double bidPrice = Double.parseDouble(fields[1].trim());
                int campaignId = Integer.parseInt(fields[2].trim());
                int queryGroupId = Integer.parseInt(fields[3].trim());

                List<Ad> ads =  crawler.GetAdBasicInfoByQuery(query, bidPrice, campaignId, queryGroupId);
                for(Ad ad : ads) {
                    String jsonInString = mapper.writeValueAsString(ad);
                    //System.out.println(jsonInString);
                    bw.write(jsonInString);
                    bw.newLine();
                }
                Thread.sleep(2000);
            }
            bw.close();
        }catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        crawler.cleanup();
    }
}
