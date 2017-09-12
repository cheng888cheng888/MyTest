
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
import com.ruixuesoft.crawler.open.TaskModel;


public class Rule4 implements RxRule {

	//private static final Logger logger = Logger.getLogger("OpenPlatform");

	@Override
	public void execute(RxTask myTask, RxCrawler myCrawler, RxDatabase myDatabase,
			RxResult myResult) throws RxCrawlerException, Throwable {


		Scenario1HistoryWeatherExecutor exe = new Scenario1HistoryWeatherExecutor(myTask,
				myCrawler, myDatabase, myResult);
		List<CompanyUrlEntity> lstEntity = exe.doExecute();
		
		String result = "";
		int size = lstEntity.size();
		for (int i = 0; i < size; i++) {
			CompanyUrlEntity entity = lstEntity.get(i);
			myTask.log(entity.getCompany_url());
			//myTask.createNextRuleTask(entity2rule(entity.getCompany_url()));
		}
		
		//myTask.log(result);
	}
	
	private TaskModel entity2rule(String url) {
		TaskModel rule = new TaskModel();
		rule.setScenarioIndex(Constant.AppScenario.SCENARIO1.getIndex());
		rule.setRuleIndex(Constant.AppRule.RULE2.getIndex());
		//rule.setSourceDataTableName("ref_area");
		//rule.setSourceDataSeq(entity.getSeq().intValue());
		rule.setV1(url);
		 
		return rule;
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

		public List<CompanyUrlEntity> doExecute() throws Exception {
			 
			String url = myTask.getV1() + "search_all#keyword=" + myTask.getV2();

			myCrawler.open(url, Constant.Message.TITLE.getValue());
			sleep(5000);

			List<CompanyUrlEntity> lstEntity = capture();
			
			/*
			if(lstEntity!= null && lstEntity.size() > 0)
			{
				for (int i = 0; i < lstEntity.size(); i++) {
					CompanyUrlEntity entity = lstEntity.get(i);
					myCrawler.open(entity.getCompany_url(), "信用信息共享搜索 | 信用中国");
					sleep(5000);
				}
			}
			*/

			return lstEntity;
		}

		private List<CompanyUrlEntity> capture() {
			List<CompanyUrlEntity> lstEntity = new ArrayList<>();

			List<RxNode> lstUl = myCrawler
					.getNodeListByXpath("//div[@class='media-list clearfix']/ul");
			if (lstUl == null || lstUl.size() == 0)
				return lstEntity;

			for (RxNode node : lstUl) {
				if (node == null)
					continue;

				List<RxNode> lstLi = node.getNodeListByXpath("./li");
				if (lstLi == null || lstLi.size() == 0)
					continue;

				// 日期
				String reportDate = "";
				// URL
				String companyUrl = "";
				
				String companyName = "";
				
				for (RxNode nodeLi : lstLi) {
					
					if (nodeLi != null) {
						try {
							RxNode a = nodeLi.getNodeByXpath("./dl/dd[1]/a");
							if (a != null) {
								reportDate = StringUtils.trim(a.getText());
								companyUrl = a.getAttribute("href");
							}
						}
						catch (Exception e) {
							 
						}
					}
					

					CompanyUrlEntity entity = new CompanyUrlEntity();
				 
					entity.setCompany_url(companyUrl);

					lstEntity.add(entity);
				}
			 
			}

			return lstEntity;
		}
		
		private List<String> captureDetail222() {
			List<String> list = new ArrayList<String>();
			RxNode node = myCrawler
					.getNodeByXpath("//div[@class='creditsearch-tagsinfo-ul']/ul");
			if (node == null)
				return list;

			List<RxNode> lstLi = node.getNodeListByXpath("./li");
			if (lstLi == null || lstLi.size() == 0)
				return list;

			 
			
			
			for (RxNode nodeLi : lstLi) {
				
				if (nodeLi != null) {
					try {
						RxNode a = nodeLi.getNodeByXpath("./strong");
						//list.add(StringUtils.trim(a.getText() + StringUtils.trim(nodeLi.getText())));
						//company_fddbr = StringUtils.trim(a.getText() + StringUtils.trim(nodeLi.getText()));
					}
					catch (Exception e) {
						 
					}
				}
				

			}
		 
			return list;
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
	
	public static class HistoryWeatherEntity {

		private Long seq;
		private Integer task_seq;
		private Integer scheduler_seq;
		private String status;
		private String uuid;
		private Date create_time;
		private Date update_time;
		private String area_id;
		private String area_name;
		private String report_date;
		private String max_temperature;
		private String min_temperature;
		private String summary;
		private String wind_direction;
		private String wind_power;
		private String detail_url;

		public HistoryWeatherEntity() {}
		
		public Long getSeq() {
			return seq;
		}

		public void setSeq(Long seq) {
			this.seq = seq;
		}

		public Integer getTask_seq() {
			return task_seq;
		}

		public void setTask_seq(Integer task_seq) {
			this.task_seq = task_seq;
		}

		public Integer getScheduler_seq() {
			return scheduler_seq;
		}

		public void setScheduler_seq(Integer scheduler_seq) {
			this.scheduler_seq = scheduler_seq;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getUuid() {
			return uuid;
		}

		public void setUuid(String uuid) {
			this.uuid = uuid;
		}

		public Date getCreate_time() {
			return create_time;
		}

		public void setCreate_time(Date create_time) {
			this.create_time = create_time;
		}

		public Date getUpdate_time() {
			return update_time;
		}

		public void setUpdate_time(Date update_time) {
			this.update_time = update_time;
		}

		public String getArea_name() {
			return area_name;
		}

		public void setArea_name(String area_name) {
			this.area_name = area_name;
		}

		public String getReport_date() {
			return report_date;
		}

		public void setReport_date(String report_date) {
			this.report_date = report_date;
		}

		public String getMax_temperature() {
			return max_temperature;
		}

		public void setMax_temperature(String max_temperature) {
			this.max_temperature = max_temperature;
		}

		public String getMin_temperature() {
			return min_temperature;
		}

		public void setMin_temperature(String min_temperature) {
			this.min_temperature = min_temperature;
		}

		public String getSummary() {
			return summary;
		}

		public void setSummary(String summary) {
			this.summary = summary;
		}

		public String getWind_direction() {
			return wind_direction;
		}

		public void setWind_direction(String wind_direction) {
			this.wind_direction = wind_direction;
		}

		public String getWind_power() {
			return wind_power;
		}

		public void setWind_power(String wind_power) {
			this.wind_power = wind_power;
		}

		public String getDetail_url() {
			return detail_url;
		}

		public void setDetail_url(String detail_url) {
			this.detail_url = detail_url;
		}

		public String getArea_id() {
			return area_id;
		}

		public void setArea_id(String area_id) {
			this.area_id = area_id;
		}

	}
	
	public static class CompanyUrlEntity {

		private Long seq;
		private String company_url;

		public String getCompany_url() {
			return company_url;
		}

		public void setCompany_url(String company_url) {
			this.company_url = company_url;
		}

		public Long getSeq() {
			return seq;
		}

		public void setSeq(Long seq) {
			this.seq = seq;
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
