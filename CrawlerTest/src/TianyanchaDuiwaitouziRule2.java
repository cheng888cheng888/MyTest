
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

public class TianyanchaDuiwaitouziRule2 implements RxRule{
	@Override
	public RxResult execute(RxTask myTask, RxCrawler myCrawler, RxDatabase myDatabase) throws RxCrawlerException {


		HeimingdanTypeInfoExecutor exe = new HeimingdanTypeInfoExecutor(myTask,
				myCrawler, myDatabase);
		RxResult result = new RxResult();
		try
		{
			//公司url
			String CompanyUrl = myTask.getV1();
			if(CompanyUrl.equals("") || "-1".equals(CompanyUrl))
			{
				result.setFinishCode(Constant.finish_code.code2.getValue());
			     return result;
			}
			//获取资源块编码
			String resource_code = myTask.getV2();
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
			
			int finish_code = exe.doExecute(CompanyUrl, resource_code, crawler_task_id);
	        result.setFinishCode(finish_code);
	        
	        return result;
		}
		catch(Exception ex)
		{
			myTask.log(ex.getMessage());
			result.setFinishCode(Constant.finish_code.code6.getValue());
	        return result;
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

		public int doExecute(String url,String resource_code, String crawler_task_id) throws Exception {
			
			myCrawler.open(url);
			sleep(5000);

			return capture(resource_code, crawler_task_id);
		}
		
		private List<ResourceFieldInfo> aaa()
		{
			List<ResourceFieldInfo> list = new ArrayList<ResourceFieldInfo>();
			
			ResourceFieldInfo item1 = new ResourceFieldInfo();
			item1.setResource_field_name("qiyemingcheng");
			item1.setResource_field_xpath("./td[1]");
			list.add(item1);
			
			ResourceFieldInfo item2 = new ResourceFieldInfo();
			item2.setResource_field_name("fadingdaibiaoren");
			item2.setResource_field_xpath("./td[2]");
			list.add(item2);
			
			ResourceFieldInfo item3 = new ResourceFieldInfo();
			item3.setResource_field_name("zhuceziben");
			item3.setResource_field_xpath("./td[3]");
			list.add(item3);
			
			ResourceFieldInfo item4 = new ResourceFieldInfo();
			item4.setResource_field_name("touzishue");
			item4.setResource_field_xpath("./td[4]");
			list.add(item4);
			
			ResourceFieldInfo item5 = new ResourceFieldInfo();
			item5.setResource_field_name("touzizhanbi");
			item5.setResource_field_xpath("./td[5]");
			list.add(item5);
			
			ResourceFieldInfo item6 = new ResourceFieldInfo();
			item6.setResource_field_name("zhuceshijian");
			item6.setResource_field_xpath("./td[6]");
			list.add(item6);
			
			ResourceFieldInfo item7 = new ResourceFieldInfo();
			item7.setResource_field_name("zhuangtai");
			item7.setResource_field_xpath("./td[7]");
			list.add(item7);
			
			return list;
		}

		/**
		 * 
		 * @param resource_code
		 * @param crawler_task_id
		 * @return
		 */
		private int capture(String resource_code, String crawler_task_id) {
			try
			{
				//获取需要抓取的xpath
				List<ResourceFieldInfo> resFieldInfoList = getResourceFieldData(myDatabase, resource_code);
				if(resFieldInfoList == null || resFieldInfoList.size() == 0)
				{
					return Constant.finish_code.code8.getValue();
				}
				//List<String> HeimingdanPageInfoList = new ArrayList<String>();
				//获取div数量，用于验证是否有分页，如果是一个就没有分页。如果是两个就有分页信息
				List<RxNode> nodeDivList = myCrawler.getNodeListByXpath("//*[@id='_container_invest']/div/div");
				//没有获取到对外投资信息
				if(nodeDivList == null || nodeDivList.size() == 0)
				{
					return Constant.finish_code.code4.getValue();
				}
				
				List<DuiwaitouziInfo> duiwaitouziInfoList = null;
				int count = nodeDivList.size();
				if(count == 1)
				{
					myTask.log("1");
					//说明没有分页，只有当前页
					duiwaitouziInfoList = GetDuiwaitouziInfo(resFieldInfoList);
					if(duiwaitouziInfoList == null || duiwaitouziInfoList.size() == 0)
					{
						myTask.log("7");
						return Constant.finish_code.code4.getValue();
					}
				}
				else if(count == 2)
				{
					myTask.log("2");
					duiwaitouziInfoList = new ArrayList<DuiwaitouziInfo>();
					//说明有分页，需要分页获取所有数据
					//获取总页数
					RxNode nodePages = nodeDivList.get(1).getNodeByXpath("./div");
					if(nodePages == null) 
					{
						myTask.log("3");
						return Constant.finish_code.code4.getValue();
					}
					int pages = Integer.valueOf(nodePages.getText().replaceAll("共", "").replaceAll("页", ""));
					boolean flag = true;
					//循环获取每页数据
					for(int i = 1;i <= pages; i++)
					{
						List<DuiwaitouziInfo> duiwaitouziInfoListTemp = GetDuiwaitouziInfo(resFieldInfoList);
						if(duiwaitouziInfoListTemp == null || duiwaitouziInfoListTemp.size() == 0)
						{
							myTask.log("4");
							flag = false;
							break;
						}
						//把当前页面抓取的信息放到集合中
						duiwaitouziInfoList.addAll(duiwaitouziInfoListTemp);
						
						//跳转到下一页
						if(!NextPageIsSuccessed(i + 1))
						{
							myTask.log("5");
							flag = false;
							break;
						}
						sleep(3000);
					}
					
					if(!flag)
					{
						myTask.log("6");
						//说明在分页中获取失败
						return Constant.finish_code.code4.getValue();
					}
				}
				
				//说明获取成功
				for(DuiwaitouziInfo item : duiwaitouziInfoList)
				{
					myTask.log(item.getQiyemingcheng());
				}
				
				return Constant.finish_code.code4.getValue();
			}
			catch(Exception ex)
			{
				return Constant.finish_code.code4.getValue();
			}
		}
		
		/*
		 * 获取对外投资信息
		 */
		private List<DuiwaitouziInfo> GetDuiwaitouziInfo(List<ResourceFieldInfo> resFieldInfoList)
		{
			try
			{
				myTask.log("8");
				List<DuiwaitouziInfo> duiwaitouziInfoList = new ArrayList<DuiwaitouziInfo>();
				List<RxNode> nodeList = myCrawler.getNodeListByXpath("//*[@id='_container_invest']/div/div[1]/table/tbody/tr");
				if(nodeList != null && nodeList.size() > 0)
				{			 
					for(RxNode node : nodeList)
					{
						//myTask.log("-------------------------------->");
					
						String nodeText = "";
						DuiwaitouziInfo dwtzInfo = new DuiwaitouziInfo();
						for(ResourceFieldInfo item : resFieldInfoList)
						{
							//myTask.log(item.getResource_field_name() + "-->" + item.getResource_field_xpath());
							nodeText = node.getNodeByXpath(item.getResource_field_xpath()).getText();
							//myTask.log(nodeText);
							
							if(item.getResource_field_name().equals("qiyemingcheng"))
							{
								dwtzInfo.setQiyemingcheng(nodeText);
							}
							if(item.getResource_field_name().equals("fadingdaibiaoren"))
							{
								dwtzInfo.setFadingdaibiaoren(nodeText);
							}
							if(item.getResource_field_name().equals("zhuceziben"))
							{
								dwtzInfo.setZhuceziben(nodeText);
							}
							if(item.getResource_field_name().equals("touzishue"))
							{
								dwtzInfo.setTouzishue(nodeText);
							}
							if(item.getResource_field_name().equals("touzizhanbi"))
							{
								dwtzInfo.setTouzizhanbi(nodeText);
							}
							if(item.getResource_field_name().equals("zhuceshijian"))
							{
								dwtzInfo.setZhuceshijian(nodeText);
							}
							if(item.getResource_field_name().equals("zhuangtai"))
							{
								dwtzInfo.setZhuangtai(nodeText);
							}
						}
			 
						duiwaitouziInfoList.add(dwtzInfo);
					}
				}
				
				return duiwaitouziInfoList;
			}
			catch(Exception ex)
			{
				myTask.log("异常:" + ex.getMessage());
				return null;
			}
		}
		
		/**
		 * 跳转到下一页
		 * @param currentPage
		 * @return
		 */
		private boolean NextPageIsSuccessed(int currentPage)
		{
			boolean flag = false;

			//点击下一页
			RxNode node_next = myCrawler.getNodeByXpath("//*[@id=\"_container_changeinfo\"]/div/div[2]/ul//a[text()='>']");
			if(node_next != null)
			{
				node_next.click();
				flag = true;
			}
			else
			{
				flag = false;
			}

			return flag;
		}
		
		private List<ResourceFieldInfo> getResourceFieldData(RxDatabase myDatabase, String resource_name) throws Exception{
			
			
		    Object[] param = new Object[1];
		    param[0] = resource_name;
			String sql = "select a.resource_field_name,a.resource_field_xpath from resource_field_data a inner join resource_data b on a.resource_code=b.resource_code where b.resource_name = ? AND a.use_yn = 'Y'";
			List<ResourceFieldInfo> resourceFieldList;
		
				resourceFieldList = myDatabase.query(sql, ResourceFieldInfo.class, param);
				return resourceFieldList;
					 
		}
	
		
		private void save(RxDatabase myDatabase, List<DuiwaitouziInfo> lstEntity, String crawler_task_id) throws Exception {
			
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
			
			
				String sql = "INSERT INTO t_duiwaitouzilist"
						+ "(`crawler_key`,"
						+ " `crawler_task_id`,"
						+ " `touzigongsisuoshushengfen`,"
						+ " `touzigongsimingcheng`,"
						+ " `touzigongsifadingdaibiaoren`,"
						+ " `touzigongsizhuceziben`,"
						+ " `touzishue`,"
						+ " `touzizhanbi`,"
						+ " `zhuceshijian`,"
						+ " `touzigongsizhuangtai`,"
						+ " `insert_time`,"
						+ " `update_time`)"
						+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
				 
				
				int size = lstEntity.size();
				Object[][] params = new Object[size][];
				for (int i = 0; i < size; i++) {
					DuiwaitouziInfo entity = lstEntity.get(i);
					Object[] param = new Object[] {
							entity.getQiyemingcheng(),
							crawler_task_id,
							df.format(new Date()),
							df.format(new Date())
					};
					params[i] = param;
				}
				
				myDatabase.batchInsert(sql, params);
			}
	}
	
	/*
	 * 对外投资信息
	 */
	public static class DuiwaitouziInfo {
		private String content;

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}
		
		public String getQiyemingcheng() {
			return qiyemingcheng;
		}

		public void setQiyemingcheng(String qiyemingcheng) {
			this.qiyemingcheng = qiyemingcheng;
		}

		public String getFadingdaibiaoren() {
			return fadingdaibiaoren;
		}

		public void setFadingdaibiaoren(String fadingdaibiaoren) {
			this.fadingdaibiaoren = fadingdaibiaoren;
		}

		public String getZhuceziben() {
			return zhuceziben;
		}

		public void setZhuceziben(String zhuceziben) {
			this.zhuceziben = zhuceziben;
		}

		public String getTouzishue() {
			return touzishue;
		}

		public void setTouzishue(String touzishue) {
			this.touzishue = touzishue;
		}

		public String getTouzizhanbi() {
			return touzizhanbi;
		}

		public void setTouzizhanbi(String touzizhanbi) {
			this.touzizhanbi = touzizhanbi;
		}

		public String getZhuceshijian() {
			return zhuceshijian;
		}

		public void setZhuceshijian(String zhuceshijian) {
			this.zhuceshijian = zhuceshijian;
		}

		public String getZhuangtai() {
			return zhuangtai;
		}

		public void setZhuangtai(String zhuangtai) {
			this.zhuangtai = zhuangtai;
		}

		private String qiyemingcheng;
		private String fadingdaibiaoren;
		private String zhuceziben;
		private String touzishue;
		private String touzizhanbi;
		private String zhuceshijian;
		private String zhuangtai;
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
}
