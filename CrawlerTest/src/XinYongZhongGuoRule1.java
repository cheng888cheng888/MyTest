
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

public class XinYongZhongGuoRule1 implements RxRule{
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
			//获取任务id
			String crawler_task_id = myTask.getV3();
			if(crawler_task_id.equals("") || "-1".equals(crawler_task_id))
			{
				 result.setFinishCode(Constant.finish_code.code6.getValue());
			     return result;
			}
			//通过资源块编码获取url
			String url = getUrl(myDatabase,resource_code);
			if(url.equals(""))
			{
				myTask.log("1");
				 result.setFinishCode(Constant.finish_code.code2.getValue());
			     return result;
			}
			
			/*
			//获取版本号
			String version = myTask.getV2();
			if(version.equals("") || "-1".equals(resource_code))
			{
				 result.setFinishCode(Constant.finish_code.code3.getValue());
			     return result;
			}
			*/
			Map<String,String> HeimingdanTypeInfoList = exe.doExecute(url);
			if(HeimingdanTypeInfoList.equals(null) || HeimingdanTypeInfoList.size() == 0)
			{
				 result.setFinishCode(Constant.finish_code.code4.getValue());
			     return result;
			}
			for (Map.Entry<String, String> entry : HeimingdanTypeInfoList.entrySet()) {  
				  
				//myTask.log("Key = " + entry.getKey() + ", Value = " + entry.getValue());  
				//myTask.createNextRuleTask(entry.getKey(), entry.getValue(), resource_code, "");
				
				RxTask newNewRuleTask = new RxTask();
				newNewRuleTask.setV1(entry.getKey());
				newNewRuleTask.setV2(entry.getValue());
				newNewRuleTask.setV3(resource_code);
				newNewRuleTask.setV4(crawler_task_id);
				myTask.createNextRuleTask(newNewRuleTask);
				break;
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

		public Map<String,String>  doExecute(String url) throws Exception {
			
			//String url = "http://www.creditchina.gov.cn/";
			
			myCrawler.open(url, "首页 | 信用中国");
			sleep(5000);

			Map<String,String> lstEntity = capture();

			return lstEntity;
		}

		private Map<String,String> capture() {
			
			//List<HeimingdanTypeInfo> HeimingdanTypeInfoList = new ArrayList<HeimingdanTypeInfo>();
			Map<String,String> myMap = new HashMap<String,String>();
			List<RxNode> nodeList = myCrawler.getNodeListByXpath("//*[@id=\"train-content\"]/ul//a");
			if(nodeList != null && nodeList.size() > 0)
			{
				myTask.log(String.valueOf(nodeList.size()));
				String title = "";
				String url = "";
				for(RxNode node : nodeList)
				{
					title = node.getAttribute("title");
					url = node.getAttribute("href");
					if(myMap.containsKey(title))
					{
						continue;
					}
					
					myMap.put(title, url);
					
//					HeimingdanTypeInfo hmdInfo = new HeimingdanTypeInfo();
//					hmdInfo.setType_name(title);
//					hmdInfo.setType_url(url);
//					HeimingdanTypeInfoList.add(hmdInfo);
				
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
	
	/*
	 * 黑名单类型信息
	 */
	public static class HeimingdanTypeInfo {
		private String type_name;
		private String type_url;
		public String getType_name() {
			return type_name;
		}
		public void setType_name(String type_name) {
			this.type_name = type_name;
		}
		public String getType_url() {
			return type_url;
		}
		public void setType_url(String type_url) {
			this.type_url = type_url;
		} 
	}
}
