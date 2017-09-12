
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ruixuesoft.crawler.open.RxCrawler;
import com.ruixuesoft.crawler.open.RxCrawlerException;
import com.ruixuesoft.crawler.open.RxDatabase;
import com.ruixuesoft.crawler.open.RxNode;
import com.ruixuesoft.crawler.open.RxResult;
import com.ruixuesoft.crawler.open.RxRule;
import com.ruixuesoft.crawler.open.RxTask;

public class Rule5 implements RxRule {

	//private static final Logger logger = Logger.getLogger("OpenPlatform");

	@Override
	public void execute(RxTask myTask, RxCrawler myCrawler, RxDatabase myDatabase,
			RxResult myResult) throws RxCrawlerException {


		Scenario1HistoryWeatherExecutor exe = new Scenario1HistoryWeatherExecutor(myTask,
				myCrawler, myDatabase, myResult);
		List<CompanyDetailInfoEntity> lstEntity = exe.doExecute();
		
		String result = "";
		if(lstEntity != null)
		{
			int size = lstEntity.size();
			for (int i = 0; i < size; i++) {
				CompanyDetailInfoEntity entity = lstEntity.get(i);
				result += entity.getCompany_gszch() + "		" + entity.getCompany_fddbr() + "\r\n";
			}
		}
		
		myTask.log(result);
	}
	

    public static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
	public class Scenario1HistoryWeatherExecutor {

		private RxTask myTask;
		private RxCrawler myCrawler;
		private RxDatabase myDatabase;
		private RxResult myResult;

		public Scenario1HistoryWeatherExecutor(RxTask myTask, RxCrawler myCrawler,
				RxDatabase myDatabase, RxResult myResult) {
			this.myTask = myTask;
			this.myCrawler = myCrawler;
			this.myDatabase = myDatabase;
			this.myResult = myResult;
		}

		public List<CompanyDetailInfoEntity> doExecute() throws Exception {
			 
			String url = myTask.getV1() ;
			myTask.log(url);
			myCrawler.open(url);
			sleep(5000);

			List<CompanyDetailInfoEntity> lstEntity = capture();
			 
		

			return lstEntity;
		}

		private List<CompanyDetailInfoEntity> capture() {
			List<CompanyDetailInfoEntity> lstEntity = new ArrayList<>();

			 /*
			RxNode node = myCrawler
					.getNodeByXpath("//ul[@class='creditsearch-tagsinfo-ul']");
		
			if (node == null)
			{
				myTask.log("11");
				return lstEntity;
			}
			else
			{
				myTask.log("22");
			}
				*/
			String company_gszch = "";
			String company_fddbr = "";
			
			

				List<RxNode> lstLi = myCrawler.getNodeListByXpath("//ul[@class='creditsearch-tagsinfo-ul']/li");
				
				 
				if (lstLi == null || lstLi.size() == 0)
				{
					//myTask.log("33");
					return lstEntity;
				}
				else
				{
					//myTask.log("44");
				}
				
				// 日期
				String reportDate = "";
				// URL
				String companyUrl = "";
				
				String companyName = "";
				
			
				for (RxNode nodeLi : lstLi) {
					
					 
				
					if (nodeLi == null) 
						{
						 
						continue;
						
						}
					 
						try {
							
							
							RxNode a = nodeLi.getNodeByXpath("./strong");
							if (a != null) {
								
								//myTask.log(a.getText());
								if(StringUtils.trim(a.getText()).equals("工商注册号："))
								{
									company_gszch = StringUtils.trim(nodeLi.getText());
								}
								if(StringUtils.trim(a.getText()).equals("法定代表人："))
								{
									company_fddbr = StringUtils.trim(nodeLi.getText());
								}
							}
							else
							{
								//myTask.log(nodeLi.getText() + " strong获取失败");
							}
							
							
							//company_gszch = StringUtils.trim(nodeLi.getText());
						}
						catch (Exception e) {
							//myTask.log(e.getMessage());
						}
				}
				
				 
				//myTask.log("company_gszch " + company_gszch);
				//myTask.log("company_fddbr " + company_fddbr);
				CompanyDetailInfoEntity entity = new CompanyDetailInfoEntity();
				 
				entity.setCompany_gszch(company_gszch);
				entity.setCompany_fddbr(company_fddbr);

				lstEntity.add(entity);
		 

			return lstEntity;
		}
		 
	}

	static class Constant {

		public static final String BASE_URL2222 = "http://lishi.tianqi.com/";

		public static final String ENV = "TEST";
		public static final String USER_SEQ = "10";
		public static final int APP_SEQ = 63;

		public static final String DATA_TABLE_TEMPLATE = "history_weather";

		public enum AppScenario {

			SCENARIO1(1, "scenario1"),
			SCENARIO2(2, "scenario2"),
			SCENARIO3(3, "scenario3"),
			SCENARIO4(4, "scenario4"),
			SCENARIO5(5, "scenario5"),
			SCENARIO6(6, "scenario6"),
			SCENARIO7(7, "scenario7"),
			SCENARIO8(8, "scenario8"),
			SCENARIO9(9, "scenario9");

			private int index;
			private String value;

			private AppScenario(int index, String value) {
				this.index = index;
				this.value = value;
			}

			public String getValue() {
				return value;
			}

			public int getIndex() {
				return index;
			}
		}

		public enum AppRule {

			RULE1(1, "rule1"),
			RULE2(2, "rule2"),
			RULE3(3, "rule3"),
			RULE4(4, "rule4"),
			RULE5(5, "rule5"),
			RULE6(6, "rule6"),
			RULE7(7, "rule7"),
			RULE8(8, "rule8"),
			RULE9(9, "rule9");

			private int index;
			private String value;

			private AppRule(int index, String value) {
				this.index = index;
				this.value = value;
			}

			public int getIndex() {
				return index;
			}

			public String getValue() {
				return value;
			}
		}

		public enum TaskStatus {
			NEW("NEW"), READY("READY"), STARTED("STARTED"), FINISHED("FINISHED");

			private String status;

			private TaskStatus(String status) {
				this.status = status;
			}

			public String getValue() {
				return status;
			}
		}

		public enum RefStatus {
			INIT("-1"), NEW("NEW"), READY("READY"), TASKED("TASKED");

			private String status;

			private RefStatus(String status) {
				this.status = status;
			}

			public String getValue() {
				return status;
			}
		}

		public enum DataStatus {
			INIT("-1");

			private String status;

			private DataStatus(String status) {
				this.status = status;
			}

			public String getValue() {
				return status;
			}
		}

		public enum FinishCode {
			SUCCESS(200),
			NO_DATA(400),
			UNKNOWN(900);

			private int code;

			private FinishCode(int code) {
				this.code = code;
			}

			public int getValue() {
				return code;
			}
		}

		public enum FloodExceptionCode {
			DATA_CONNECTION_NULL(501, "数据连接空"),
			TASK_INFO_NULL(502, "任务信息空"),
			MESSAGE_NULL(503, "消息空"),
			CREATE_TABLE_FAILED(511, "数据表创建失败"),
			UNKNOWN_EXCEPTION(999, "未知异常");

			private int index;
			private String name;

			private FloodExceptionCode(int code, String name) {
				this.index = code;
				this.name = name;
			}

			public int getCode() {
				return index;
			}

			public String getName() {
				return name;
			}
		}

		public enum Message {
			SEPARATOR(">>>>>"),
			TITLE("信用中国"),
			UNKNOW_EXCEPTION("未知异常"),
			START("历史天气爬虫 %s 启动..."),

			S1R1_START("S1R1 : 启动"),
			S1R1_END("S1R1 : 结束"),
			S1R2_START("S1R2 : 启动"),
			S1R2_END("S1R2 : 结束"),
			S1R3_START("S1R3 : 启动"),
			S1R3_END("S1R3 : 结束"),
			S1R4_START("S1R4 : 启动"),
			S1R4_END("S1R4 : 结束"),
			S1R5_START("S1R5 : 启动"),
			S1R5_END("S1R5 : 结束"),

			S2R1_START("S2R1 : 启动"),
			S2R1_END("S2R1 : 结束"),
			S2R2_START("S2R2 : 启动"),
			S2R2_END("S2R2 : 结束"),
			S2R3_START("S2R3 : 启动"),
			S2R3_END("S2R3 : 结束"),
			S2R4_START("S2R4 : 启动"),
			S2R4_END("S2R4 : 结束"),
			S2R5_START("S2R5 : 启动"),
			S2R5_END("S2R5 : 结束"),
			S2R6_START("S2R6 : 启动"),
			S2R6_END("S2R6 : 结束"),

			S3R1_START("S3R1 : 启动"),
			S3R1_END("S3R1 : 结束"),
			S3R2_START("S3R2 : 启动"),
			S3R2_END("S3R2 : 结束"),
			S3R3_START("S3R3 : 启动"),
			S3R3_END("S3R3 : 结束"),
			S3R4_START("S3R4 : 启动"),
			S3R4_END("S3R4 : 结束"),
			S3R5_START("S3R5 : 启动"),
			S3R5_END("S3R5 : 结束"),
			S3R6_START("S3R6 : 启动"),
			S3R6_END("S3R6 : 结束"),

			S4R1_START("S4R1 : 启动"),
			S4R1_END("S4R1 : 结束"),
			S4R2_START("S4R2 : 启动"),
			S4R2_END("S4R2 : 结束"),
			S4R3_START("S4R3 : 启动"),
			S4R3_END("S4R3 : 结束"),
			S4R4_START("S4R4 : 启动"),
			S4R4_END("S4R4 : 结束"),
			S4R5_START("S4R5 : 启动"),
			S4R5_END("S4R5 : 结束"),
			S4R6_START("S4R6 : 启动"),
			S4R6_END("S4R6 : 结束");

			private String msg;

			private Message(String msg) {
				this.msg = msg;
			}

			public String getValue() {
				return msg;
			}
		}
	}

	static class CommonBusiness {

		public static String formatMessage(String... messages) {
			if (messages == null || messages.length == 0)
				return "";

			StringBuffer sb = new StringBuffer();
			sb.append(Constant.Message.TITLE.getValue());
			for (String msg : messages) {
				sb.append(Constant.Message.SEPARATOR.getValue());
				sb.append(msg);
			}

			return sb.toString();
		}

		public static String generateUUID() {
			return String.valueOf(UUID.randomUUID());
		}

		public static int getDaysOfMonth(String ym) throws ParseException {
			Date d = new SimpleDateFormat("yyyyMM").parse(ym);

			Calendar cal = GregorianCalendar.getInstance();
			cal.setTime(d);
			return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		}
	}
	
	 
	public static class CompanyUrlEntity {

	 
		private String company_url;

		public String getCompany_url() {
			return company_url;
		}

		public void setCompany_url(String company_url) {
			this.company_url = company_url;
		}
 
	}
	
	public static class CompanyDetailInfoEntity {

		 
		private String company_gszch;

		private String company_fddbr;

		public String getCompany_gszch() {
			return company_gszch;
		}

		public void setCompany_gszch(String company_gszch) {
			this.company_gszch = company_gszch;
		}

		public String getCompany_fddbr() {
			return company_fddbr;
		}

		public void setCompany_fddbr(String company_fddbr) {
			this.company_fddbr = company_fddbr;
		}
 
	}
}
