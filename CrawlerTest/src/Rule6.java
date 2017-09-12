
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.ruixuesoft.crawler.open.RxCrawler;
import com.ruixuesoft.crawler.open.RxCrawlerException;
import com.ruixuesoft.crawler.open.RxDatabase;
import com.ruixuesoft.crawler.open.RxNode;
import com.ruixuesoft.crawler.open.RxResult;
import com.ruixuesoft.crawler.open.RxRule;
import com.ruixuesoft.crawler.open.RxTask;

public class Rule6 implements RxRule {
	@Override
	public void execute(RxTask myTask, RxCrawler myCrawler, RxDatabase myDatabase,
			RxResult myResult) throws RxCrawlerException {
		Scenario1HistoryWeatherExecutor exe = new Scenario1HistoryWeatherExecutor(myTask,
				myCrawler, myDatabase, myResult);
		myTask.log(Constant.Message.START.msg);
		 
		String result = "";
		
		/*
		if(lstEntity != null && lstEntity.size() > 0)
		{
			StringBuffer sb = new StringBuffer();
			 
			
			for(int i = 0;i<lstEntity.size();i++)
			{
				if(i == 10)
				{
					break;
				}
				sb.append(lstEntity.get(i).getCompany_name() + lstEntity.get(i).getOrganization_id() + lstEntity.get(i).getBlacklist_type() + lstEntity.get(i).getArea() + "\r\n");
			}
			
			
			result = sb.toString();

			
			myTask.log("1");
		}
		else
		{
			myTask.log("2");
		}
			
		
		//save(myDatabase, lstEntity);

		 */
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

		public List<CompanyBlackListInfo> doExecute() throws Exception {

			String url = myTask.getV1();
			//myTask.log("爬取URL：" + url);
			myCrawler.open(url);
			sleep(5000);

			List<CompanyBlackListInfo> lstEntity = capture();
			
			return lstEntity;
		}

		private List<CompanyBlackListInfo> capture() {
			List<CompanyBlackListInfo> lstEntity = new ArrayList<>();
			String blacklist_name = "";
			RxNode blacklist_node = myCrawler.getNodeByXpath("//ol[@class='breadcrumb']/li[3]/a");
			if(blacklist_node != null)
			{
				blacklist_name = blacklist_node.getText();
				myTask.log(blacklist_name);
			}
			
			 
			
			List<RxNode> lstDl = myCrawler.getNodeListByXpath("//div[@class='blackList-content']/dl");

			if (lstDl == null || lstDl.size() == 0)
			{
				myTask.log("无企业信息.");
				return lstEntity;
			}
			 
			RxNode node = lstDl.get(0);
		 
			String content = node.getText();
			 
			String[] list = content.split("\n");
			CompanyBlackListInfo entity = new CompanyBlackListInfo();
			entity.setCompany_name(list[0]);
			entity.setOrganization_id(list[1]);
			entity.setArea(list[2]);
			entity.setBlacklist_type(blacklist_name);
			lstEntity.add(entity);
			 
		
			/*
			for (RxNode nodeDl : lstDl) {
				if (nodeDl == null) continue;
				try {
					
					 
					 
			String content = nodeDl.getText();
			String[] list = content.split("\n");
			CompanyBlackListInfo entity = new CompanyBlackListInfo();
			entity.setCompany_name(list[0]);
			entity.setOrganization_id(list[1]);
			entity.setArea(list[2]);
			entity.setBlacklist_type(blacklist_name);
			lstEntity.add(entity);
				}
				catch (Exception e) {
				}
			}
			
	*/
			

			return lstEntity;
		}
		
	}

	static class Constant {
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
			START("开始爬取数据..."),
			END("爬取结束.");
			
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

	public static class CompanyBlackListInfo {
		private String company_name;

		private String organization_id;
		
		private String area;
		
		private String blacklist_type;

		public String getCompany_name() {
			return company_name;
		}

		public void setCompany_name(String company_name) {
			this.company_name = company_name;
		}

		public String getOrganization_id() {
			return organization_id;
		}

		public void setOrganization_id(String organization_id) {
			this.organization_id = organization_id;
		}

		public String getArea() {
			return area;
		}

		public void setArea(String area) {
			this.area = area;
		}

		public String getBlacklist_type() {
			return blacklist_type;
		}

		public void setBlacklist_type(String blacklist_type) {
			this.blacklist_type = blacklist_type;
		}
	}
	
	private void save(RxDatabase myDatabase, List<CompanyBlackListInfo> lstEntity) throws Exception {
		
		String sql = "INSERT INTO t_blacklist"
				+ "(`company_name`,"
				+ " `blacklist_type`,"
				+ " `organization_id`,"
				+ " `area`)"
				+ " VALUES(?, ?, ?, ?);";
		 
		
		int size = lstEntity.size();
		Object[][] params = new Object[size][];
		for (int i = 0; i < size; i++) {
			CompanyBlackListInfo entity = lstEntity.get(i);
			Object[] param = new Object[] {
					entity.getCompany_name(),
					entity.getBlacklist_type(),
					entity.getOrganization_id(),
					entity.getArea()
			};
			params[i] = param;
		}
		
		myDatabase.batchInsert(sql, params);
	}
}