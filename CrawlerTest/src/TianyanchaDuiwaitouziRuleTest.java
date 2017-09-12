
import java.util.ArrayList;
import java.util.List;

import com.ruixuesoft.crawler.open.RxCrawler;
import com.ruixuesoft.crawler.open.RxCrawlerException;
import com.ruixuesoft.crawler.open.RxDatabase;
import com.ruixuesoft.crawler.open.RxNode;
import com.ruixuesoft.crawler.open.RxResult;
import com.ruixuesoft.crawler.open.RxRule;
import com.ruixuesoft.crawler.open.RxTask;

public class TianyanchaDuiwaitouziRuleTest implements RxRule{
	@Override
	public RxResult execute(RxTask myTask, RxCrawler myCrawler, RxDatabase myDatabase) throws RxCrawlerException {


		HeimingdanTypeInfoExecutor exe = new HeimingdanTypeInfoExecutor(myTask,
				myCrawler, myDatabase);
		RxResult result = new RxResult();
		try
		{
			//��˾url
			String CompanyUrl = myTask.getV1();
			if(CompanyUrl.equals("") || "-1".equals(CompanyUrl))
			{
				result.setFinishCode(Constant.finish_code.code2.getValue());
			     return result;
			}
			
			/*
			//��ȡ��Դ�����
			String resource_code = myTask.getV3();
			if(resource_code.equals("") || "-1".equals(resource_code))
			{
				result.setFinishCode(Constant.finish_code.code3.getValue());
			     return result;
			}
			*/
			int finish_code = exe.doExecute(CompanyUrl);
			
			/*
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
					
					 
				}
			}
			 */
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
	 * �ر�alert������
	 */
    public static void CloseAlert(RxCrawler myCrawler) {
        try {
        	myCrawler.closeAlert();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * ����ָ��������
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
		 * ��ȡ������
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

		public int doExecute(String url) throws Exception {
			
			myCrawler.open(url);
			sleep(5000);

			return capture();
		}

		private int capture() {
			
			//List<String> HeimingdanPageInfoList = new ArrayList<String>();
			//��ȡdiv������������֤�Ƿ��з�ҳ�������һ����û�з�ҳ��������������з�ҳ��Ϣ
			List<RxNode> nodeDivList = myCrawler.getNodeListByXpath("//*[@id='_container_changeinfo']/div/div");
			//û�л�ȡ������Ͷ����Ϣ
			if(nodeDivList.equals(null) || nodeDivList.size() == 0)
			{
				return Constant.finish_code.code4.getValue();
			}
			
			List<DuiwaitouziInfo> duiwaitouziInfoList = null;
			int count = nodeDivList.size();
			if(count == 1)
			{
				myTask.log("1");
				//˵��û�з�ҳ��ֻ�е�ǰҳ
				duiwaitouziInfoList = GetDuiwaitouziInfo(null);
				if(duiwaitouziInfoList.equals(null) || duiwaitouziInfoList.size() == 0)
				{
					myTask.log("7");
					return Constant.finish_code.code4.getValue();
				}
			}
			else if(count == 2)
			{
				myTask.log("2");
				duiwaitouziInfoList = new ArrayList<DuiwaitouziInfo>();
				//˵���з�ҳ����Ҫ��ҳ��ȡ��������
				//��ȡ��ҳ��
				RxNode nodePages = nodeDivList.get(1).getNodeByXpath("./div");
				if(nodePages.equals(null)) 
				{
					myTask.log("3");
					return Constant.finish_code.code4.getValue();
				}
				int pages = Integer.valueOf(nodePages.getText().replaceAll("��", "").replaceAll("ҳ", ""));
				boolean flag = true;
				//ѭ����ȡÿҳ����
				for(int i = 1;i <= pages; i++)
				{
					List<DuiwaitouziInfo> duiwaitouziInfoListTemp = GetDuiwaitouziInfo(null);
					if(duiwaitouziInfoListTemp.equals(null) || duiwaitouziInfoListTemp.size() == 0)
					{
						myTask.log("4");
						flag = false;
						break;
					}
					//�ѵ�ǰҳ��ץȡ����Ϣ�ŵ�������
					duiwaitouziInfoList.addAll(duiwaitouziInfoListTemp);
					
					//��ת����һҳ
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
					//˵���ڷ�ҳ�л�ȡʧ��
					return Constant.finish_code.code4.getValue();
				}
			}
			
			
			//˵����ȡ�ɹ�
			for(DuiwaitouziInfo item : duiwaitouziInfoList)
			{
				myTask.log(item.getContent());
			}
			
			
			return Constant.finish_code.code4.getValue();
		}
		
		/*
		 * ��ȡ����Ͷ����Ϣ
		 */
		private List<DuiwaitouziInfo> GetDuiwaitouziInfo(List<ResourceFieldInfo> resFieldInfoList)
		{
			try
			{
				myTask.log("8");
				List<DuiwaitouziInfo> duiwaitouziInfoList = new ArrayList<DuiwaitouziInfo>();
				List<RxNode> nodeList = myCrawler.getNodeListByXpath("//*[@id='_container_changeinfo']/div/div[1]/table/tbody/tr");
				if(nodeList != null && nodeList.size() > 0)
				{			 
					for(RxNode node : nodeList)
					{
						//myTask.log(node.getText());
						DuiwaitouziInfo dwtzInfo = new DuiwaitouziInfo();
						dwtzInfo.setContent(node.getText());
						duiwaitouziInfoList.add(dwtzInfo);
					}
				}	
				
				return duiwaitouziInfoList;
			}
			catch(Exception ex)
			{
				return null;
			}
		}
		
		/**
		 * ��ת����һҳ
		 * @param currentPage
		 * @return
		 */
		private boolean NextPageIsSuccessed(int currentPage)
		{
			boolean flag = false;

			//�����һҳ
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
	}
	
	/*
	 * ����Ͷ����Ϣ
	 */
	public static class DuiwaitouziInfo {
		private String content;

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}
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
