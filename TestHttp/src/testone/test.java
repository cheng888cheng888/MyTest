package testone;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
			
			
			List<Map<String,String>> ZhixingrenInfoList = new ArrayList<Map<String,String>>();
			Map<String,String> map = new HashMap<String,String>();
			map.put("A", "1");
			map.put("B", "2");
			map.put("C", "3");
			ZhixingrenInfoList.add(map);;
			Map<String,String> map2 = new HashMap<String,String>();
			map2.put("A", "4");
			map2.put("B", "5");
			map2.put("C", "6");
			ZhixingrenInfoList.add(map2);
			Map<String,String> map3 = new HashMap<String,String>();
			map3.put("A", "7");
			map3.put("B", "8");
			map3.put("C", "9");
			ZhixingrenInfoList.add(map3);
			
			
			List<String> listColumn = new ArrayList<String>();
			for (Map.Entry<String, String> entry : ZhixingrenInfoList.get(0).entrySet()) {  
					//System.out.println(entry.getKey() + ": " + entry.getValue());
				 listColumn.add(entry.getKey()); 
				}
			
			int size = ZhixingrenInfoList.size();
			Object[][] params = new Object[size][];
			
			String result = "";
			
			/*
			for(Map<String,String> item : ZhixingrenInfoList)
			{
				Object[] param = new Object[item.size()];
				for(int i = 0; i < item.size(); i++)
				{
					param[i] = item.get(listColumn.get(i));
				}
				
				
				for (Map.Entry<String, String> entry : item.entrySet()) {  
					
				 
				 
				}
					 
				
				
				for(int i = 0;i < param.length; i++)
				{
					System.out.println(param[i]);
				}
				
			}
		
		 	*/
			
			
			for(int i = 0; i < ZhixingrenInfoList.size(); i++)
			{
				Map<String,String> item = ZhixingrenInfoList.get(i);
				Object[] param = new Object[item.size()];
				for(int k = 0; k < item.size(); k++)
				{
					param[k] = item.get(listColumn.get(k));
				}
				
				params[i] = param;
			}
			
		
			for(int i = 0;i<params.length;i++)
			{
				System.out.println(params[i].length);
			}
			
			
			
			//System.out.println(result);
			/*
			int size = lstEntity.size();
			Object[][] params = new Object[size][];
			for (int i = 0; i < size; i++) {
				Map<String,String> entity = lstEntity.get(i);
				Object[] param = new Object[] {
						bzxr_code,
						entity.get(""),
						entity.getBianhao(),
						entity.getLianshijian(),
						entity.getAnhao(),
						entity.getZhixingfayuan(),
						entity.getZhixingbiaodi(),
						version,
						df.format(new Date()),
						df.format(new Date())
				};
				params[i] = param;
			}
			*/
			
			/*
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
			
			*/
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("=========end========");
		
		//修改说明222222
	}

}
