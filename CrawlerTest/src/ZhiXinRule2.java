
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

public class ZhiXinRule2 implements RxRule{
	@Override
	public RxResult execute(RxTask myTask, RxCrawler myCrawler, RxDatabase myDatabase) throws RxCrawlerException {


		ZhingxingrenInfoExecutor exe = new ZhingxingrenInfoExecutor(myTask,
				myCrawler, myDatabase);
		 
		try
		{
			List<ZhixingrenInfo> ZhixingrenInfoList = exe.doExecute();
			/*
			if(ZhixingrenInfoList != null && ZhixingrenInfoList.size() > 0)
			{
				for(ZhixingrenInfo item : ZhixingrenInfoList)
				{
					myTask.log(item.mingcheng);
				}
			}*/
			 
			String version = myTask.getV3();
			if(ZhixingrenInfoList != null && ZhixingrenInfoList.size() > 0)
			{
				save(myDatabase, ZhixingrenInfoList, version);
			}
		}
		catch(Exception ex)
		{
			myTask.log(ex.getMessage());
		}
		
		RxResult result = new RxResult();
        result.setFinishCode(200);
        return result;
	}
	
	/**
	 * 
	 * @param myDatabase
	 * @param lstEntity
	 * @param version
	 * @throws Exception
	 */
	private void save(RxDatabase myDatabase, List<ZhixingrenInfo> lstEntity, String version) throws Exception {
		 
		String sql = "INSERT INTO t_beizhixingren_conpany_info"
				+ "(`mingcheng`,"
				+ " `bianhao`,"
				+ " `lianshijian`,"
				+ " `anhao`,"
				+ " `zhixingfayuan`,"
				+ " `zhixingbiaodi`,"
				+ " `version`)"
				+ " VALUES(?, ?, ?, ?, ?, ?, ?);";
		
		int size = lstEntity.size();
		Object[][] params = new Object[size][];
		for (int i = 0; i < size; i++) {
			ZhixingrenInfo entity = lstEntity.get(i);
			Object[] param = new Object[] {
					entity.getMingcheng(),
					entity.getBianhao(),
					entity.getLianshijian(),
					entity.getAnhao(),
					entity.getZhixingfayuan(),
					entity.getZhixingbiaodi(),
					version
			};
			params[i] = param;
		}
		
		myDatabase.batchInsert(sql, params);
		
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
    
	public class ZhingxingrenInfoExecutor {

		private RxTask myTask;
		private RxCrawler myCrawler;
		private RxDatabase myDatabase;

		public ZhingxingrenInfoExecutor(RxTask myTask, RxCrawler myCrawler,
				RxDatabase myDatabase) {
			this.myTask = myTask;
			this.myCrawler = myCrawler;
			this.myDatabase = myDatabase;
		}

		public List<ZhixingrenInfo> doExecute() throws Exception {
			 
			String url = "http://zhixing.court.gov.cn/search/";
			
			myCrawler.open(url, "ȫ����Ժ��ִ������Ϣ��ѯ - ��ִ���˲�ѯ");
			sleep(5000);

			List<ZhixingrenInfo> lstEntity = capture();

			return lstEntity;
		}

		private List<ZhixingrenInfo> capture() {
			
			//myTask.log("1");
			//��ȡִ����������code����
			String bzxr_name = myTask.getV1();
			String bzxr_code = myTask.getV2();
			//���name��Ϊ�գ���name��ֵ�����ı���
			if(!"".equals(bzxr_name) && !"-1".equals(bzxr_name))
			{
				//��ȡ�����ı���ڵ�
				RxNode inputName = myCrawler.getNodeByXpath("//*[@id='pname']");
				if(inputName != null)
				{
					inputName.input(bzxr_name);
				}
			}
			//���name��Ϊ�գ���name��ֵ�����ı���
			if(!"".equals(bzxr_code) && !"-1".equals(bzxr_code))
			{
				//��ȡ�����ı���ڵ�
				RxNode inputCode = myCrawler.getNodeByXpath("//*[@id='cardNum']");
				if(inputCode != null)
				{
					inputCode.input(bzxr_code);
				}
			}
			
			if(!VerificationCodeChecked())
			{
				//��֤�����
				return null;
			}
			//myTask.log("2");
			List<ZhixingrenInfo> ZhixingrenInfoList = new ArrayList<ZhixingrenInfo>();
			RxNode node = myCrawler.getNodeByXpath("//*[@id='ResultlistBlock']/div");
			//myTask.log("ҳ����Ϣ��" + node.getText());
			String[] page_info = node.getText().split(" ");
			//������
			int number = Integer.valueOf(page_info[6].replaceAll("��", "").replaceAll("��", ""));
			if(page_info != null && page_info.length > 0)
			{
				int page_size = Integer.parseInt(page_info[5].substring(2), 10);
				for(int i = 1; i <= page_size; i++)
				{
					List<ZhixingrenInfo> ZhixingrenInfoListTemp = GetZhixingrenInfo();
					
					myTask.log("�� " + String.valueOf(i) + " ҳ " + " ���� �� " + ZhixingrenInfoListTemp.size());
					if(i < page_size && ZhixingrenInfoListTemp.size() < 10)
					{
						//myTask.log("10");
						//˵����ǰҳû��ץȫ����Ϣ������
						break;
					}
					if(i == page_size && ZhixingrenInfoListTemp.size() != (number - ((i - 1) * 10)))
					{
						//myTask.log("11");
						//˵����ǰҳû��ץȫ����Ϣ������
						break;
					}
					//�ѵ�ǰҳ��ץȡ����Ϣ�ŵ�������
					ZhixingrenInfoList.addAll(ZhixingrenInfoListTemp);
					
			 
					//�ж��Ƿ�ɹ�������һҳ
					if(!NextPageIsSuccessed((i+1)))
					{
						//myTask.log("12");
						break;
					}
				}
			}
			
			
			
			if(number != ZhixingrenInfoList.size())
			{
				return null;
			}
			else
			{
				return ZhixingrenInfoList;
			}
		}
		
		private boolean NextPageIsSuccessed(int currentPage)
		{
			//myTask.log("4");
			boolean flag = false;
			
			/*
			//�����һҳ//*[@id="ResultlistBlock"]/div/a[1]
			RxNode node_next = myCrawler.getNodeByXpath("//*[@id='ResultlistBlock']/div/a[3]",false);
			if(node_next != null)
			{
				myTask.log("7");
				node_next.click();
				 //�ж��Ƿ�����֤�������ʾ
                RxNode nodeYzmErrorMsg = myCrawler.getNodeByXpath("/html/body/div/h4/span");
                if(nodeYzmErrorMsg != null && nodeYzmErrorMsg.getText().contains("��֤�����"))
                {
                	myTask.log("5");
                	//����ʶ����֤���ѯ
                	if(VerificationCodeChecked())
                	{
                		myTask.log("10");
                		//ʵ������ָ��ҳ
                		RxNode node_currentpage = myCrawler.getNodeByXpath("//*[@id='pagenum']");
                		if(node_currentpage != null)
                		{
                			node_currentpage.clear();
                			node_currentpage.input(String.valueOf(currentPage));
                			RxNode node_to_currentpage = myCrawler.getNodeByXpath("//*[@id='ResultlistBlock']/div/input[1]");
                			if(node_to_currentpage != null)
                			{
                				node_to_currentpage.click();
                				sleep(2000);
                				flag = true;
                			}
                		}
                	}
                	else
                	{
                		myTask.log("6");
                		 flag = false;
                	}
                }
                else	 
                {
                	myTask.log("9");
                	//˵��˵���ɹ�������һҳ
                	flag = true;
                }
			}
			else
			{
				myTask.log("8");
			}
			*/
			
			
			//ʵ������ָ��ҳ
    		RxNode node_currentpage = myCrawler.getNodeByXpath("//*[@id='pagenum']");
    		if(node_currentpage != null)
    		{
    			node_currentpage.clear();
    			node_currentpage.input(String.valueOf(currentPage));
    			RxNode node_to_currentpage = myCrawler.getNodeByXpath("//*[@id='ResultlistBlock']/div/input[1]");
    			if(node_to_currentpage != null)
    			{
    				node_to_currentpage.click();
    				sleep(1000);
    				 //�ж��Ƿ�����֤�������ʾ
                    RxNode nodeYzmErrorMsg = myCrawler.getNodeByXpath("/html/body/div/h4/span");
                    if(nodeYzmErrorMsg != null && nodeYzmErrorMsg.getText().contains("��֤�����"))
                    {
                    	//myTask.log("33");
                    	
                    	//����ʶ����֤���ѯ
                    	if(VerificationCodeChecked())
                    	{
                    		//myTask.log("66");
                    		//ʵ������ָ��ҳ
                    		node_currentpage.clear();
                			node_currentpage.input(String.valueOf(currentPage));
                			node_to_currentpage.click();
            				sleep(1000);
            				flag = true;
                    	}
                    	else
                    	{
                    		//myTask.log("55");
                    		 flag = false;
                    	}
                    }
                    else
                    {
                    	//myTask.log("44");
                    	flag = true;
                    }
    			}
    			else
    			{
    				//myTask.log("22");
    				flag = false;
    			}
    		}
    		else
    		{
    			//myTask.log("11");
    		}
			
			return flag;
		}
	
		/*
		 * ��֤����֤
		 */
		private boolean VerificationCodeChecked()
		{
			boolean flag = true;
			
			 //�����һ��
	        RxNode changeInputCode = myCrawler.getNodeByXpath("//*[@id='searchForm']/table/tbody/tr[4]/td/span/a");
	        changeInputCode.click();
	        sleep(1000);
	        
			for(int i =0 ;i < 3; i++)
			{
				// �����һ����֤��
                if ( i != 0 ) {
                	RxNode nodeYzm = myCrawler.getNodeByXpath("//*[@id='searchForm']/table/tbody/tr[4]/td/span/a");
                	nodeYzm.click();
                    sleep(1000);
                }
                //��ȡ��֤��
                myCrawler.inputVerifyCode("//*[@id='captchaImg']" , "//*[@id='j_captcha']");
                
                RxNode nodeSearch = myCrawler.getNodeByXpath("//*[@id='button']");
                nodeSearch.click();
                sleep(1000);
                //�ж��Ƿ�����֤�������ʾ
                RxNode nodeYzmErrorMsg = myCrawler.getNodeByXpath("/html/body/div/h4/span");
                if(nodeYzmErrorMsg != null && nodeYzmErrorMsg.getText().contains("��֤�����"))
                {
                	flag = false;
                	continue;
                }
                else	 
                {
                	//˵����֤��ͨ��
                	flag = true;
                	break;
                }
			}
			
			return flag;
		}
		
		/*
		 * ��֤����֤
		 */
		private boolean VerificationCodeCheckedByChakan(String xpath)
		{	
			//myTask.log("4.1");	
			boolean flag = true;
			 //�����һ��
	        RxNode changeInputCode = myCrawler.getNodeByXpath("//*[@id='searchForm']/table/tbody/tr[4]/td/span/a");
	        changeInputCode.click();
	        sleep(1000);
	        
			for(int i =0 ;i < 3; i++)
			{
				//myTask.log("��֤��� " + String.valueOf(i) + " �γ��� ");	
				// �����һ����֤��
                if ( i != 0 ) {
                	RxNode nodeYzm = myCrawler.getNodeByXpath("//*[@id='searchForm']/table/tbody/tr[4]/td/span/a");
                	nodeYzm.click();
                    sleep(1000);
                }
                //myTask.log("4.4");	
                //��ȡ��֤��
                myCrawler.inputVerifyCode("//*[@id='captchaImg']" , "//*[@id='j_captcha']");
                //����鿴
                List<RxNode> nodeCheck = myCrawler.getNodeListByXpath(xpath);
    			nodeCheck.get(0).click();
    			sleep(1000);
    			
				//myTask.log("4.5");	
				//��ִ��һ�¹ر�alert
				CloseAlert(myCrawler);
				//�ж��Ƿ񵯳���ϸ
				if(myCrawler.getNodeByXpath("//*[@id='ResultView']").getAttribute("style").contains("display: block"))
				{
					//˵����֤��ͨ��
                	flag = true;
                	//myTask.log("��֤��� " + String.valueOf(i) + " �γ���  ��˵����֤��ͨ��");	
                	break;
				}
				else
				{
					flag = false;
					//myTask.log("��֤��� " + String.valueOf(i) + " �γ���  ��˵����֤��ʧ��");	
                	continue;
				}
			}
			
			return flag;
		}
		
		/*
		 * ��ҳ��ȡִ������Ϣ
		 */
		private List<ZhixingrenInfo> GetZhixingrenInfo()
		{
			//myTask.log("3");
			List<ZhixingrenInfo> ZhixingrenInfoList = new ArrayList<ZhixingrenInfo>();
			RxNode nodeSearch = myCrawler.getNodeByXpath("//table[@id='Resultlist']");
			if(nodeSearch != null)
			{
				//myTask.log("3.1");
				List<RxNode> lstTr = myCrawler.getNodeListByXpath("//*[@id='Resultlist']/tbody/tr[position() > 1]");
				if(lstTr != null && lstTr.size() > 0)
				{		
					//myTask.log("3.2");		 
					String xpathBase = "//*[@id='Resultlist']/tbody/tr[position() > %s]/td[5]/a";
					for(int k = 1;k<= lstTr.size();k++)
					{
						String xpath = String.format(xpathBase, k);
						List<RxNode> nodeCheck = myCrawler.getNodeListByXpath(xpath);
						nodeCheck.get(0).click();
						sleep(1000);
						
						//myTask.log("��  " + String.valueOf(k) + " ��");	
						//��ִ��һ�¹ر�alert
						CloseAlert(myCrawler);
						//�ж��Ƿ񵯳���ϸ
						if(!myCrawler.getNodeByXpath("//*[@id='ResultView']").isDisplayed())
						{
							//myTask.log("3.4");	
							//�����������˵����֤�������Ҫ����ʶ����֤��
							if(!VerificationCodeCheckedByChakan(xpath))
							{
								//myTask.log("3.3");
								break;
							}
						}

						//��ȡ��ϸ��Ϣ
						ZhixingrenInfo zxrInfo = new ZhixingrenInfo();
						RxNode node_mingcheng = myCrawler.getNodeByXpath("//*[@class='t_middle_top_select_block_middle']/table/tbody/tr[1]/td[2]");
						if(node_mingcheng != null)zxrInfo.setMingcheng(node_mingcheng.getText());
						RxNode node_bianhao = myCrawler.getNodeByXpath("//*[@class='t_middle_top_select_block_middle']/table/tbody/tr[2]/td[2]");
						if(node_bianhao != null)zxrInfo.setBianhao(node_bianhao.getText());
						RxNode node_zhixingfayuan = myCrawler.getNodeByXpath("//*[@class='t_middle_top_select_block_middle']/table/tbody/tr[3]/td[2]");
						if(node_zhixingfayuan != null)zxrInfo.setZhixingfayuan(node_zhixingfayuan.getText());
						RxNode node_lianshijian = myCrawler.getNodeByXpath("//*[@class='t_middle_top_select_block_middle']/table/tbody/tr[4]/td[2]");
						if(node_lianshijian != null)zxrInfo.setLianshijian(node_lianshijian.getText());
						RxNode node_anhao = myCrawler.getNodeByXpath("//*[@class='t_middle_top_select_block_middle']/table/tbody/tr[5]/td[2]");
						if(node_anhao != null)zxrInfo.setAnhao(node_anhao.getText());
						RxNode node_zhixingbiaodi = myCrawler.getNodeByXpath("//*[@class='t_middle_top_select_block_middle']/table/tbody/tr[6]/td[2]");
						if(node_zhixingbiaodi != null)zxrInfo.setZhixingbiaodi(node_zhixingbiaodi.getText());
						ZhixingrenInfoList.add(zxrInfo);
						
						RxNode close_node = myCrawler.getNodeByXpath("//*[@id='CloseResultView']");
						close_node.click();
						sleep(500);
					}
				}		 
			}
			
			return ZhixingrenInfoList;
		}
	}
	
	/*
	 * ִ������Ϣ
	 */
	public static class ZhixingrenInfo {
		private String mingcheng;
		private String bianhao;
		private String lianshijian;
		private String anhao;
		private String zhixingfayuan;
		private String zhixingbiaodi;
		public String getMingcheng() {
			return mingcheng;
		}
		public void setMingcheng(String mingcheng) {
			this.mingcheng = mingcheng;
		}
		public String getBianhao() {
			return bianhao;
		}
		public void setBianhao(String bianhao) {
			this.bianhao = bianhao;
		}
		public String getLianshijian() {
			return lianshijian;
		}
		public void setLianshijian(String lianshijian) {
			this.lianshijian = lianshijian;
		}
		public String getAnhao() {
			return anhao;
		}
		public void setAnhao(String anhao) {
			this.anhao = anhao;
		}
		public String getZhixingfayuan() {
			return zhixingfayuan;
		}
		public void setZhixingfayuan(String zhixingfayuan) {
			this.zhixingfayuan = zhixingfayuan;
		}
		public String getZhixingbiaodi() {
			return zhixingbiaodi;
		}
		public void setZhixingbiaodi(String zhixingbiaodi) {
			this.zhixingbiaodi = zhixingbiaodi;
		}
	}
}
