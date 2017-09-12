
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.ruixuesoft.crawler.open.RxCrawler;
import com.ruixuesoft.crawler.open.RxCrawlerException;
import com.ruixuesoft.crawler.open.RxDatabase;
import com.ruixuesoft.crawler.open.RxResult;
import com.ruixuesoft.crawler.open.RxRule;
import com.ruixuesoft.crawler.open.RxTask;
import com.ruixuesoft.crawler.open.TaskModel;

public class Rule2 implements RxRule {

	private static final Logger logger = Logger.getLogger("OpenPlatform");

	@Override
	public void execute(RxTask myTask, RxCrawler myCrawler, RxDatabase myDatabase,
			RxResult myResult) throws RxCrawlerException, Throwable {

		logger.info(CommonBusiness.formatMessage(Constant.AppScenario.SCENARIO2.getValue(),
				"历史月份采集任务启动"));

		String areaId = myTask.getV1();
		String month = parseMonth();
		HistoryMonthEntity entity = findByAreaIdMonth(myDatabase, areaId, month);
		if (entity == null) {
			entity = new HistoryMonthEntity();
			entity.setTask_seq(myTask.getTaskSeq());
			entity.setStatus(Constant.DataStatus.INIT.getValue());
			entity.setArea_id(areaId);
			entity.setArea_name(myTask.getV2());
			entity.setMonth(month);
			entity.setUrl(Constant.BASE_URL + areaId + "/" + month + ".html");

			entity.setSeq(Long.valueOf(insert(myDatabase, entity)));
		}

		myTask.createNextRuleTask(entity2rule(myTask, entity));

		updateAreaMonths(myDatabase, myTask.getSourceDataSeq());

		myResult.setFinishCode(Constant.FinishCode.SUCCESS.getValue());

		logger.info(CommonBusiness.formatMessage(Constant.AppScenario.SCENARIO2.getValue(),
				"历史月份采集任务结束"));
	}

	private TaskModel entity2rule(RxTask myTask,
			HistoryMonthEntity entity) {

		TaskModel rule = new TaskModel();
		rule.setScenarioIndex(Constant.AppScenario.SCENARIO2.getIndex());
		rule.setRuleIndex(Constant.AppRule.RULE3.getIndex());
		rule.setSourceDataTableName("history_month");
		rule.setSourceDataSeq(entity.getSeq().intValue());
		rule.setV1(myTask.getV1());
		rule.setV2(myTask.getV2());
		rule.setV3(myTask.getV3());
		rule.setV4(entity.getUrl());
		rule.setV5(entity.getMonth());

		return rule;
	}

	private int insert(RxDatabase myDatabase, HistoryMonthEntity entity)
			throws SQLException {

		String sql = "INSERT INTO `history_month`"
				+ "(`task_seq`,"
				+ " `scheduler_seq`,"
				+ " `status`,"
				+ " `area_id`,"
				+ " `area_name`,"
				+ " `month`,"
				+ " `url`)"
				+ "VALUES(?, ?, ?, ?, ?, ?, ?);";

		Object[] params = new Object[] {
				entity.getTask_seq(),
				entity.getScheduler_seq(),
				entity.getStatus(),
				entity.getArea_id(),
				entity.getArea_name(),
				entity.getMonth(),
				entity.getUrl()
		};

		return myDatabase.insert(sql, params);
	}

	private void updateAreaMonths(RxDatabase myDatabase, long seq) throws SQLException {
		String sql = "SELECT"
				+ " `seq`,"
				+ " `status`,"
				+ " `create_time`,"
				+ " `update_time`,"
				+ " `name`,"
				+ " `id`,"
				+ " `url`"
				+ " FROM `ref_area`"
				+ " WHERE seq = ?";
		List<AreaEntity> lstEntity = myDatabase.query(sql, AreaEntity.class,
				new Object[] { seq });
		if (lstEntity != null && lstEntity.size() > 0) {
			Integer months = lstEntity.get(0).getMonths();
			if (months == null)
				months = 1;
			else
				months++;

			String updSql = "update ref_area set `months` = ? where seq = ?;";
			myDatabase.update(updSql, new Object[] { months, seq });
		}
	}

	private String parseMonth() {
		Date now = new Date();
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(now);
		cal.add(GregorianCalendar.MONTH, -1);

		return new SimpleDateFormat("yyyyMM").format(cal.getTime());
	}

	private HistoryMonthEntity findByAreaIdMonth(RxDatabase myDatabase, String areaId,
			String month)
			throws SQLException {

		String sql = "SELECT"
				+ " `seq`,"
				+ " `task_seq`,"
				+ " `scheduler_seq`,"
				+ " `status`,"
				+ " `create_time`,"
				+ " `update_time`,"
				+ " `area_id`,"
				+ " `area_name`,"
				+ " `month`,"
				+ " `url`"
				+ " FROM `history_month`"
				+ " WHERE `area_id` = ? and `month` = ?"
				+ " limit 1;";

		Object[] params = new Object[] { areaId, month };
		List<HistoryMonthEntity> lstEntity = myDatabase.query(sql,
				HistoryMonthEntity.class, params);
		if (lstEntity != null && lstEntity.size() > 0)
			return lstEntity.get(0);

		return null;
	}

	public static class Constant {

		public static final String BASE_URL = "http://lishi.tianqi.com/";

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
			TITLE("天气"),
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

	public static class CommonBusiness {

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

	public static class HistoryMonthEntity {

		private Long seq;
		private Integer task_seq;
		private Integer scheduler_seq;
		private String status;
		private Date create_time;
		private Date update_time;
		private String area_id;
		private String area_name;
		private String month;
		private String url;
		
		public HistoryMonthEntity() {}

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

		public String getMonth() {
			return month;
		}

		public void setMonth(String month) {
			this.month = month;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getArea_id() {
			return area_id;
		}

		public void setArea_id(String area_id) {
			this.area_id = area_id;
		}
	}

	public static class AreaEntity {

		private Long seq;
		private String status;
		private Date create_time;
		private Date update_time;
		private String name;
		private String id;
		private String url;
		private Integer months;

		public AreaEntity() {}
		
		public Long getSeq() {
			return seq;
		}

		public void setSeq(Long seq) {
			this.seq = seq;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
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

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public Integer getMonths() {
			return months;
		}

		public void setMonths(Integer months) {
			this.months = months;
		}
	}
}
