

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

public class Rule1 implements RxRule {

	private static final Logger logger = Logger.getLogger("OpenPlatform");

	@Override
	public void execute(RxTask myTask, RxCrawler myCrawler, RxDatabase myDatabase,
			RxResult myResult) throws RxCrawlerException, Throwable {

		logger.info(CommonBusiness.formatMessage(Constant.AppScenario.SCENARIO2.getValue(),
				"��ʼ��������"));

		List<AreaEntity> lstArea = queryArea(myDatabase);
		if (lstArea == null || lstArea.size() == 0) {
			myResult.setFinishCode(Constant.FinishCode.NO_DATA.getValue());
			return;
		}

		for (AreaEntity entity : lstArea) {
			newTask(myDatabase, myTask, entity);
		}

		myResult.setFinishCode(Constant.FinishCode.SUCCESS.getValue());

		logger.info(CommonBusiness.formatMessage(Constant.AppScenario.SCENARIO2.getValue(),
				"��ʼ�������"));
	}

	private List<AreaEntity> queryArea(RxDatabase myDatabase) throws SQLException {
		String sql = "SELECT"
				+ " `seq`,"
				+ " `status`,"
				+ " `create_time`,"
				+ " `update_time`,"
				+ " `name`,"
				+ " `id`,"
				+ " `url`"
				+ " FROM `ref_area`"
				+ " WHERE `status` = 'READY';";

		return myDatabase.query(sql, AreaEntity.class, null);
	}

	private void newTask(RxDatabase myDatabase, RxTask myTask, AreaEntity entity)
			throws Exception {
		
		myTask.createNextRuleTask(entity2rule(entity));
		updateAreaStatus(myDatabase, entity.getSeq());
	}

	private TaskModel entity2rule(AreaEntity entity) {
		TaskModel rule = new TaskModel();
		rule.setScenarioIndex(Constant.AppScenario.SCENARIO2.getIndex());
		rule.setRuleIndex(Constant.AppRule.RULE2.getIndex());
		rule.setSourceDataTableName("ref_area");
		rule.setSourceDataSeq(entity.getSeq().intValue());
		rule.setV1(entity.getId());
		rule.setV2(entity.getName());
		rule.setV3(entity.getUrl());

		return rule;
	}

	private void updateAreaStatus(RxDatabase myDatabase, long seq) throws SQLException {
		String sql = "update ref_area set `status` = ? where seq = ?;";
		Object[] params = new Object[] { Constant.RefStatus.TASKED.getValue(), seq };
		myDatabase.update(sql, params);
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

		public enum Message {
			SEPARATOR(">>>>>"),
			TITLE("����"),
			UNKNOW_EXCEPTION("δ֪�쳣"),
			START("��ʷ�������� %s ����..."),

			S1R1_START("S1R1 : ����"),
			S1R1_END("S1R1 : ����"),
			S1R2_START("S1R2 : ����"),
			S1R2_END("S1R2 : ����"),
			S1R3_START("S1R3 : ����"),
			S1R3_END("S1R3 : ����"),
			S1R4_START("S1R4 : ����"),
			S1R4_END("S1R4 : ����"),
			S1R5_START("S1R5 : ����"),
			S1R5_END("S1R5 : ����"),

			S2R1_START("S2R1 : ����"),
			S2R1_END("S2R1 : ����"),
			S2R2_START("S2R2 : ����"),
			S2R2_END("S2R2 : ����"),
			S2R3_START("S2R3 : ����"),
			S2R3_END("S2R3 : ����"),
			S2R4_START("S2R4 : ����"),
			S2R4_END("S2R4 : ����"),
			S2R5_START("S2R5 : ����"),
			S2R5_END("S2R5 : ����"),
			S2R6_START("S2R6 : ����"),
			S2R6_END("S2R6 : ����"),

			S3R1_START("S3R1 : ����"),
			S3R1_END("S3R1 : ����"),
			S3R2_START("S3R2 : ����"),
			S3R2_END("S3R2 : ����"),
			S3R3_START("S3R3 : ����"),
			S3R3_END("S3R3 : ����"),
			S3R4_START("S3R4 : ����"),
			S3R4_END("S3R4 : ����"),
			S3R5_START("S3R5 : ����"),
			S3R5_END("S3R5 : ����"),
			S3R6_START("S3R6 : ����"),
			S3R6_END("S3R6 : ����"),

			S4R1_START("S4R1 : ����"),
			S4R1_END("S4R1 : ����"),
			S4R2_START("S4R2 : ����"),
			S4R2_END("S4R2 : ����"),
			S4R3_START("S4R3 : ����"),
			S4R3_END("S4R3 : ����"),
			S4R4_START("S4R4 : ����"),
			S4R4_END("S4R4 : ����"),
			S4R5_START("S4R5 : ����"),
			S4R5_END("S4R5 : ����"),
			S4R6_START("S4R6 : ����"),
			S4R6_END("S4R6 : ����");

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
