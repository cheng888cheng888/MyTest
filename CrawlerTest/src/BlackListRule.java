
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
import org.apache.commons.lang.StringUtils;
import com.ruixuesoft.crawler.open.RxCrawler;
import com.ruixuesoft.crawler.open.RxCrawlerException;
import com.ruixuesoft.crawler.open.RxDatabase;
import com.ruixuesoft.crawler.open.RxNode;
import com.ruixuesoft.crawler.open.RxResult;
import com.ruixuesoft.crawler.open.RxRule;
import com.ruixuesoft.crawler.open.RxTask;
import com.ruixuesoft.crawler.open.TaskModel;

public class BlackListRule implements RxRule{
	@Override
	public void execute(RxTask myTask, RxCrawler myCrawler, RxDatabase myDatabase,
			RxResult myResult) throws RxCrawlerException, Throwable {


		Scenario1HistoryWeatherExecutor exe = new Scenario1HistoryWeatherExecutor(myTask,
				myCrawler, myDatabase, myResult);
		Map<String, String> map = exe.doExecute();
		
		String result = "";
		for (Map.Entry<String, String> entry : map.entrySet()) {  
			  
			result += entry.getKey() + ": " + entry.getValue() + "\r\n";
		} 
		
		myTask.log(result);
	}
	
	private TaskModel entity2rule(String url) {
		TaskModel rule = new TaskModel();
		rule.setScenarioIndex(1);
		rule.setRuleIndex(2);
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

		public Map<String, String> doExecute() throws Exception {
			 
			String url = "http://www.creditchina.gov.cn/";

			myCrawler.open(url, "首页 | 信用中国");
			sleep(5000);

			Map<String, String> lstEntity = capture();
			
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

		private Map<String, String> capture() {
			 
			//记录黑名单
			Dictionary<String, String> myDic = new Hashtable<String, String>();  
			Map<String ,String> myMap = new HashMap<String ,String>();
			List<RxNode> lstUl = myCrawler.getNodeListByXpath("//div[@id='train-content']/ul");
			if (lstUl == null || lstUl.size() == 0)
				return null;

			String black_list_type_url = "";
			String black_list_type_name = "";
			for (RxNode node : lstUl) {
				if (node == null)
					continue;

				List<RxNode> lstLi = node.getNodeListByXpath("./li");
				if (lstLi == null || lstLi.size() == 0)
					continue;

				for (RxNode nodeLi : lstLi) {
					
					if (nodeLi != null) {
						try {
							RxNode a = nodeLi.getNodeByXpath("./a");
							if (a != null) {
								black_list_type_name = StringUtils.trim(a.getText());
								black_list_type_url = a.getAttribute("href");
								if(!myMap.containsKey(black_list_type_name))
								{
									myMap.put(black_list_type_name, black_list_type_url);
								}
							}
						}
						catch (Exception e) {
							 
						}
					}
				}
			}

			return myMap;
		}
	}
}
