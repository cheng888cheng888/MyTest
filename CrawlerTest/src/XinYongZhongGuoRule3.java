
import java.sql.SQLException;
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

public class XinYongZhongGuoRule3 implements RxRule{
	@Override
	public RxResult execute(RxTask myTask, RxCrawler myCrawler, RxDatabase myDatabase) throws RxCrawlerException {


		HeimingdanTypeInfoExecutor exe = new HeimingdanTypeInfoExecutor(myTask,
				myCrawler, myDatabase);
		RxResult result = new RxResult();
		try
		{
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
			result.setResult1("starttime:" + df.format(new Date()));
			//获取黑名单类型url
			String url = myTask.getV1();
			if(url.equals("") || "-1".equals(url))
			{
				result.setFinishCode(Constant.finish_code.code1.getValue());
			     return result;
			}
			
			//获取黑名单类型hmdTypeName
			String hmdTypeName = myTask.getV2();
			if(hmdTypeName.equals("") || "-1".equals(hmdTypeName))
			{
				result.setFinishCode(Constant.finish_code.code2.getValue());
			     return result;
			}
			
			//获取资源块编码
			String resource_code = myTask.getV3();
			if(resource_code.equals("") || "-1".equals(resource_code))
			{
				result.setFinishCode(Constant.finish_code.code3.getValue());
			     return result;
			}
			
			//获取任务id
			String crawler_task_id = myTask.getV4();
			if(crawler_task_id.equals("") || "-1".equals(crawler_task_id))
			{
				 result.setFinishCode(Constant.finish_code.code7.getValue());
			     return result;
			}
			/*
			//获取版本号
			String version = myTask.getV4();
			if(version.equals("") || "-1".equals(version))
			{
				result.setFinishCode(Constant.finish_code.code4.getValue());
			     return result;
			}
			 */
			List<HeimingdanInfo> HeimingdanInfoList = exe.doExecute(url, resource_code);
			if(HeimingdanInfoList.equals(null) || HeimingdanInfoList.size() == 0)
			{
				result.setFinishCode(Constant.finish_code.code5.getValue());
			     return result;
			}
			if(HeimingdanInfoList != null && HeimingdanInfoList.size() > 0)
			{
//				for(HeimingdanInfo item : HeimingdanInfoList)
//				{
//					myTask.log(item.getQiyemingcheng() + "-->" + item.getZuzhijigoudaima() + "");
//				}
				
//				for(int i =0; i < HeimingdanInfoList.size(); i++)
//				{
//					if(i == 10)
//					{
//						break;	
//					}
//					
//					myTask.log(HeimingdanInfoList.get(i).getQiyemingcheng() + "-->" + HeimingdanInfoList.get(i).getZuzhijigoudaima() + "-->" + HeimingdanInfoList.get(i).getGuishudiyu());
//				}
				
				save(myDatabase, HeimingdanInfoList, hmdTypeName, "企业", crawler_task_id);
			}
			 
			result.setFinishCode(Constant.finish_code.code200.getValue());
	        return result;
		}
		catch(Exception ex)
		{
			myTask.log(ex.getMessage());
			result.setFinishCode(Constant.finish_code.code6.getValue());
	        return result;
		}
	}
	
	private void save(RxDatabase myDatabase, List<HeimingdanInfo> lstEntity, String blacklist_type, String gerenorqiye,String crawler_task_id) throws Exception {
			
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		
		
			String sql = "INSERT INTO t_heimingdanlist"
					+ "(`company_name`,"
					+ " `crawler_key`,"
					+ " `blacklist_type`,"
					+ " `organization_id`,"
					+ " `gerenorqiye`,"
					+ " `area`,"
					+ "`crawler_task_id`,"
					+ "`insert_time`,"
					+ "`update_time`)"
					+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?);";
			 
			
			int size = lstEntity.size();
			Object[][] params = new Object[size][];
			for (int i = 0; i < size; i++) {
				HeimingdanInfo entity = lstEntity.get(i);
				Object[] param = new Object[] {
						entity.getQiyemingcheng(),
						entity.getQiyemingcheng(),
						blacklist_type,
						entity.getZuzhijigoudaima(),
						gerenorqiye,
						entity.getGuishudiyu(),
						crawler_task_id,
						df.format(new Date()),
						df.format(new Date())
				};
				params[i] = param;
			}
			
			myDatabase.batchInsert(sql, params);
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

		public List<HeimingdanInfo> doExecute(String url,String resource_code) throws Exception {

			myCrawler.open("http://www.creditchina.gov.cn/", "首页 | 信用中国");
			sleep(5000);
			myCrawler.open(url, "信用中国");
			sleep(5000);

			List<HeimingdanInfo> lstEntity = capture(resource_code);

			return lstEntity;
		}

		private List<HeimingdanInfo> capture(String resource_code) {

			 
			List<HeimingdanInfo> HeimingdanPageInfoList = new ArrayList<HeimingdanInfo>();
		
			List<RxNode> nodeList = myCrawler.getNodeListByXpath("//div[@class='blackList-content']/dl");
			if(nodeList != null && nodeList.size() > 0)
			{
				List<ResourceFieldInfo> resFieldInfoList;
				try {
					resFieldInfoList = getResourceFieldData(myDatabase, resource_code);
				} catch (Exception e) {
				 
					 return HeimingdanPageInfoList;
				}
			 
				if(resFieldInfoList.equals(null) || resFieldInfoList.size() == 0) 
				{
					 
					return HeimingdanPageInfoList;
				}
				myTask.log(String.valueOf(nodeList.size()));
				
				for(int i = 1; i <= nodeList.size(); i++)
				{
					HeimingdanInfo hmdInfo = new HeimingdanInfo();
					for(ResourceFieldInfo item : resFieldInfoList)
					{
						if(item.getResource_field_name().equals("company_name"))
						{
							hmdInfo.setQiyemingcheng(myCrawler.getNodeByXpath(String.format(item.getResource_field_xpath(), i)).getText());
						}
						if(item.getResource_field_name().equals("organization_id"))
						{
							hmdInfo.setZuzhijigoudaima(myCrawler.getNodeByXpath(String.format(item.getResource_field_xpath(), i)).getText());
						}
						if(item.getResource_field_name().equals("area"))
						{
							hmdInfo.setGuishudiyu(myCrawler.getNodeByXpath(String.format(item.getResource_field_xpath(), i)).getText());
						}
						
						//myTask.log(myCrawler.getNodeByXpath(String.format(item.getResource_field_xpath(), i)).getText());
					}
					HeimingdanPageInfoList.add(hmdInfo);
				}
				
				myTask.log("抓取完成");
				
				/*
				for (RxNode nodeDl : nodeList) 
				{
					if (nodeDl == null)
					{
						continue;
					}
					String content = nodeDl.getText();
					String[] list = content.split("\n");
					
					HeimingdanInfo hmdInfo = new HeimingdanInfo();
					hmdInfo.setQiyemingcheng(list[0]);
					hmdInfo.setZuzhijigoudaima(list[1]);
					hmdInfo.setGuishudiyu(list[2]);
					 
				 
					HeimingdanPageInfoList.add(hmdInfo);
				}
				*/
			}

			return HeimingdanPageInfoList;
		}
	}
	
	
	private List<ResourceFieldInfo> getResourceFieldData(RxDatabase myDatabase, String resource_name) throws Exception{
		
			
		    Object[] param = new Object[1];
		    param[0] = resource_name;
			String sql = "select a.resource_field_name,a.resource_field_xpath from resource_field_data a inner join resource_data b on a.resource_code=b.resource_code where b.resource_name = ? AND a.use_yn = 'Y'";
			List<ResourceFieldInfo> resourceFieldList;
		
				resourceFieldList = myDatabase.query(sql, ResourceFieldInfo.class, param);
				return resourceFieldList;
					 
	}
	
	 
	public static class ResourceFieldInfo {
		private String resource_field_name;
		private String resource_field_xpath;
		public String getResource_field_name() {
			return resource_field_name;
		}
		public void setResource_field_name(String resource_field_name) {
			this.resource_field_name = resource_field_name;
		}
		public String getResource_field_xpath() {
			return resource_field_xpath;
		}
		public void setResource_field_xpath(String resource_field_xpath) {
			this.resource_field_xpath = resource_field_xpath;
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
	 * 黑名单信息
	 */
	public static class HeimingdanInfo {
		private String qiyemingcheng;
		private String zuzhijigoudaima;
		private String guishudiyu;
		public String getQiyemingcheng() {
			return qiyemingcheng;
		}
		public void setQiyemingcheng(String qiyemingcheng) {
			this.qiyemingcheng = qiyemingcheng;
		}
		public String getZuzhijigoudaima() {
			return zuzhijigoudaima;
		}
		public void setZuzhijigoudaima(String zuzhijigoudaima) {
			this.zuzhijigoudaima = zuzhijigoudaima;
		}
		public String getGuishudiyu() {
			return guishudiyu;
		}
		public void setGuishudiyu(String guishudiyu) {
			this.guishudiyu = guishudiyu;
		}
	}
}
