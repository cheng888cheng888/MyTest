
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

public class XinYongZhongGuoRule2 implements RxRule{
	@Override
	public RxResult execute(RxTask myTask, RxCrawler myCrawler, RxDatabase myDatabase) throws RxCrawlerException {


		HeimingdanTypeInfoExecutor exe = new HeimingdanTypeInfoExecutor(myTask,
				myCrawler, myDatabase);
		RxResult result = new RxResult();
		try
		{
			//获取黑名单类型
			String hmdTypeName = myTask.getV1();
			if(hmdTypeName.equals("") || "-1".equals(hmdTypeName))
			{
				result.setFinishCode(Constant.finish_code.code1.getValue());
			     return result;
			}
			//获取黑名单类型url
			String hmdTypeUrl = myTask.getV2();
			if(hmdTypeUrl.equals("") || "-1".equals(hmdTypeUrl))
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
			//myTask.log("黑名单类型：" + hmdTypeName);
			List<String> HeimingdanPageInfoList = exe.doExecute(hmdTypeUrl);
			if(HeimingdanPageInfoList.equals(null) || HeimingdanPageInfoList.size() == 0)
			{
				result.setFinishCode(Constant.finish_code.code5.getValue());
			    return result;
			}
			if(HeimingdanPageInfoList != null && HeimingdanPageInfoList.size() > 0)
			{
				myTask.log("1");
				for(String item : HeimingdanPageInfoList)
				{
					//myTask.log("2");
					//myTask.log(item + "  " + hmdTypeName + "  " +resource_code);
					//myTask.createNextRuleTask(item, hmdTypeName, resource_code, "");
					
					RxTask newNewRuleTask = new RxTask();
					newNewRuleTask.setV1(item);
					newNewRuleTask.setV2(hmdTypeName);
					newNewRuleTask.setV3(resource_code);
					newNewRuleTask.setV4(crawler_task_id);
					myTask.createNextRuleTask(newNewRuleTask);
					break;
				}
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

		public List<String> doExecute(String hmdTypeUrl) throws Exception {
			
			myCrawler.open(hmdTypeUrl, "信用中国");
			sleep(5000);

			List<String> lstEntity = capture();

			return lstEntity;
		}

		private List<String> capture() {
			
			List<String> HeimingdanPageInfoList = new ArrayList<String>();
			List<RxNode> nodeList = myCrawler.getNodeListByXpath("//*[@id='pagination']/li");
			if(nodeList != null && nodeList.size() > 0)
			{
				int size = nodeList.size();
				int page_size = Integer.valueOf(nodeList.get(size-2).getText());
				myTask.log("总页数： " + String.valueOf(page_size));
				//http://www.creditchina.gov.cn/toPunishList#getNo=3&page=2
				for(int i = 1; i <= page_size; i++)
				{
					HeimingdanPageInfoList.add("http://www.creditchina.gov.cn/toPunishList#getNo=3&page=" + String.valueOf(i));
				}
			}

			return HeimingdanPageInfoList;
		}
	}
}
