
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.client.HttpClient;
import org.apache.log4j.Logger;

public class HTTP {

    private static final Logger logger = Logger.getLogger(HTTP.class);
     
	public static void main(String[] args) {
		
//		try {
//			HTTP.doGet("http://www.baidu.com");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		System.out.println("=========start========");
//		try {
//			HTTP.doPost("http://59.110.10.13/app/APP707_PushAppTaskLog.php?appSeq=76&scenarioIndex=1&ruleIndex=1&ruleVersion=78&scheduleType=TEST&logMessage=RUNNING%7C16%3A21%3A51%7CcloseNewTab+start");
//		} catch (Exception e) {
//			e.printStackTrace();
		
		
		
//		}
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("=========end========");
	}
	
	  public static String sendPost(String params, String requestUrl,
	            String authorization) throws IOException {

	        byte[] requestBytes = params.getBytes("utf-8"); // ������תΪ��������
	        HttpClient httpClient = new HttpClient();// �ͻ���ʵ����
	    
	      
	         
	      
	         
	        InputStream soapResponseStream = postMethod.getResponseBodyAsStream();// ��ȡ���ص���
	        byte[] datas = null;
	        try {
	            datas = readInputStream(soapResponseStream);// ���������ж�ȡ����
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        String result = new String(datas, "UTF-8");// ����������תΪString
	        // ��ӡ���ؽ��
	        // System.out.println(result);

	        return result;

	    }


}
