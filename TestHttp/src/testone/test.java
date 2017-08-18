package testone;

import java.io.IOException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class test {


    public static String doGet(String url) throws Exception {
    	  
        HttpRequestRetryHandler myRetryHandler = new HttpRequestRetryHandler() {

            public boolean retryRequest(
                    IOException exception,
                    int executionCount,
                    HttpContext context) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
                return true;
            }
        };
        
        try (CloseableHttpClient client = HttpClients.custom().setRetryHandler(myRetryHandler).build()) {

        	
            HttpGet get = new HttpGet(url);
            // 设置请求和传输超时时间
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();
            get.setConfig(requestConfig);
            
           
            try (CloseableHttpResponse response = client.execute(get)) {
            	
            
                int code = response.getStatusLine().getStatusCode();
                if (code >= 200 && code <= 299) {
                    return EntityUtils.toString(response.getEntity(), "utf-8");
                }
                throw new Exception("Get Error in HTTP GET because StatusCodecode <= 200 || code >= 299!");
            }catch(Exception e){
            	
            }
        }
        
     
		return null;
    }
    
	public static void main(String[] args) {
		
		System.out.println("=========start========");
		try {
			String url = "http://106.2.1.39/app/APP701_CreateRuleTask.php?env=TEST";
			url += "&userSeq=26";
			url += "&appSeq=115";
			url += "&scenarioIndex=1";
			url += "&ruleIndex=1";
			url += "&accountIndex=-1";
			url += "&v1=1";
			url += "&v2=v4";
			
			System.out.println(url);
			
			String  result = doGet(url);
			System.out.println(result);
			
			
			
			//testcommit
			
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("=========end========");
	}

}
