
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Dictionary;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.ruixuesoft.crawler.open.RxCrawler;
import com.ruixuesoft.crawler.open.RxCrawlerException;
import com.ruixuesoft.crawler.open.RxDatabase;
import com.ruixuesoft.crawler.open.RxNode;
import com.ruixuesoft.crawler.open.RxResult;
import com.ruixuesoft.crawler.open.RxRule;
import com.ruixuesoft.crawler.open.RxTask;

public class TianyanchaDuiwaitouziRule1 implements RxRule{
	@Override
	public RxResult execute(RxTask myTask, RxCrawler myCrawler, RxDatabase myDatabase) throws RxCrawlerException {


		HeimingdanTypeInfoExecutor exe = new HeimingdanTypeInfoExecutor(myTask,
				myCrawler, myDatabase);
		
		RxResult result = new RxResult();
		try
		{	
			//获取资源块编码
			String resource_code = myTask.getV1();
			if(resource_code.equals("") || "-1".equals(resource_code))
			{
				 result.setFinishCode(Constant.finish_code.code1.getValue());
			     return result;
			}
			//获取公司名称
			String company_name = myTask.getV2();
			if(company_name.equals("") || "-1".equals(company_name))
			{
				 result.setFinishCode(Constant.finish_code.code2.getValue());
			     return result;
			}
			//获取任务id
			String crawler_task_id = myTask.getV3();
			if(crawler_task_id.equals("") || "-1".equals(crawler_task_id))
			{
				 result.setFinishCode(Constant.finish_code.code6.getValue());
			     return result;
			}
			
			//通过资源块编码获取url
			String url = getUrl(myDatabase,resource_code);
			myTask.log(url);
			if(url.equals(""))
			{
				myTask.log("1");
				 result.setFinishCode(Constant.finish_code.code2.getValue());
			     return result;
			}

			//String url= "https://www.tianyancha.com/search?key=北京瑞金麟网络技术服务有限公司";
			Map<String,String> HeimingdanTypeInfoList = exe.doExecute(url ,company_name);
			if(HeimingdanTypeInfoList == null || HeimingdanTypeInfoList.size() == 0)
			{
				 result.setFinishCode(Constant.finish_code.code4.getValue());
			     return result;
			}
			
			for (Map.Entry<String, String> entry : HeimingdanTypeInfoList.entrySet()) {  
				  
				myTask.log("Key = " + entry.getKey() + ", Value = " + entry.getValue());  
				//myTask.createNextRuleTask(entry.getKey(), entry.getValue(), resource_code, "");
				//break;
			} 
	
			result.setFinishCode(Constant.finish_code.code200.getValue());
		    return result;
		}
		catch(Exception ex)
		{
			myTask.log(ex.getMessage());
			result.setFinishCode(Constant.finish_code.code5.getValue());
	        return result;
		}
	}
	
	/**
	 * 
	 * @param myDatabase
	 * @param resource_code
	 * @return
	 * @throws Exception
	 */
	private String getUrl(RxDatabase myDatabase, String resource_name) throws Exception {
		
	    Object[] param = new Object[1];
	    param[0] = resource_name;
		String sql = "select resource_code,resource_name,resource_url from resource_data where resource_name = ? AND use_yn = 'Y'";
		List<ResourceInfo> resourceList = myDatabase.query(sql, ResourceInfo.class, param);		 
		String url = "";
		if(resourceList != null && resourceList.size() > 0)
		{
			 url = resourceList.get(0).getResource_url();
		}
		 
		return url;
		}
	
	/*
	 * 
	 */
	public static class ResourceInfo {
		private int resource_code;
		private String resource_name;
		private String resource_url;
		public int getResource_code() {
			return resource_code;
		}
		public void setResource_code(int resource_code) {
			this.resource_code = resource_code;
		}
		public String getResource_name() {
			return resource_name;
		}
		public void setResource_name(String resource_name) {
			this.resource_name = resource_name;
		}
		public String getResource_url() {
			return resource_url;
		}
		public void setResource_url(String resource_url) {
			this.resource_url = resource_url;
		}
		 
	}
	
	/*
	 * 关闭alert弹出框
	 */
    public static void CloseAlert(RxCrawler myCrawler) {
        try {
        	myCrawler.closeAlert();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * 休眠指定毫秒数
     */
    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
	public class HeimingdanTypeInfoExecutor {

		private RxTask myTask;
		private RxCrawler myCrawler;
		private RxDatabase myDatabase;

		public HeimingdanTypeInfoExecutor(RxTask myTask, RxCrawler myCrawler,
				RxDatabase myDatabase) {
			this.myTask = myTask;
			this.myCrawler = myCrawler;
			this.myDatabase = myDatabase;
		}

		public Map<String,String>  doExecute(String url, String company_name) throws Exception {
			
			//String url= "https://www.tianyancha.com/search?key=北京瑞金麟网络技术服务有限公司";
			String open_url = url + "?key=" + company_name;
			
			myCrawler.open(open_url);
			sleep(5000);

			Map<String,String> lstEntity = capture();

			return lstEntity;
		}

		private Map<String,String> capture() {
			
			//List<HeimingdanTypeInfo> HeimingdanTypeInfoList = new ArrayList<HeimingdanTypeInfo>();
			Map<String,String> myMap = new HashMap<String,String>();
			List<RxNode> nodeList = myCrawler.getNodeListByXpath("//div[@class='b-c-white search_result_container']/div/div[2]//a");
			if(nodeList != null && nodeList.size() > 0)
			{
				myTask.log(String.valueOf(nodeList.size()));
				String title = "";
				String url = "";
				for(RxNode node : nodeList)
				{
					title = node.getAttribute("title");
					url = node.getAttribute("href");
					if(myMap.containsKey(url))
					{
						continue;
					}
					
					myMap.put(url, url);
				}
			}
			
			
			return myMap;
		}
	}
	
	static class Constant {
		/**
		 * 获取错误码
		 * @author Administrator
		 *
		 */
		public enum finish_code {

			code200(200, 200),
			code201(201, 201),
			code1(1, 401),
			code2(2, 402),
			code3(3, 403),
			code4(4, 404),
			code5(5, 405),
			code6(6, 406),
			code7(7, 407),
			code8(8, 408),
			code9(9, 409),
			code10(10, 410),
			code11(11, 411),
			code12(12, 412),
			code13(13, 413),
			code14(14, 414),
			code15(15, 415);
			
			private int index;
			private int value;

			private finish_code(int index, int value) {
				this.index = index;
				this.value = value;
			}

			public int getIndex() {
				return index;
			}

			public int getValue() {
				return value;
			}
		}
	}
}
