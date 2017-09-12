import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.ruixuesoft.crawler.open.RxCrawler;
import com.ruixuesoft.crawler.open.RxCrawlerException;
import com.ruixuesoft.crawler.open.RxDatabase;
import com.ruixuesoft.crawler.open.RxNode;
import com.ruixuesoft.crawler.open.RxResult;
import com.ruixuesoft.crawler.open.RxRule;
import com.ruixuesoft.crawler.open.RxTask;

public class ZhiXinRule1 implements RxRule{
	@Override
	public RxResult execute(RxTask myTask, RxCrawler myCrawler, RxDatabase myDatabase) throws RxCrawlerException {

		ZhingxingrenInfoExecutor exe = new ZhingxingrenInfoExecutor(myTask,
				myCrawler, myDatabase);
		RxResult result = new RxResult();
		try
		{
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//�������ڸ�ʽ
			result.setResult1("starttime:" + df.format(new Date()));
			//��ȡ��Դ�����
			String resource_code = myTask.getV1();
			if(resource_code.equals("") || "-1".equals(resource_code))
			{
				 result.setFinishCode(Constant.finish_code.code1.getValue());
			     return result;
			}
			
			//��ȡ��˾����
			String bzxr_name = myTask.getV2();
			if(bzxr_name.equals("") || "-1".equals(bzxr_name))
			{
				 result.setFinishCode(Constant.finish_code.code2.getValue());
			     return result;
			}
			
			//��ȡ����id
			String crawler_task_id = myTask.getV3();
			if(crawler_task_id.equals("") || "-1".equals(crawler_task_id))
			{
				 result.setFinishCode(Constant.finish_code.code6.getValue());
			     return result;
			}
			/*
			//��ȡ�汾��
			String version = myTask.getV2();
			if(version.equals("") || "-1".equals(version))
			{
				 result.setFinishCode(401);
			     return result;
			}
			*/
			//ͨ����Դ������ȡurl
			String url = getUrl(myDatabase,resource_code);
			if(url.equals(""))
			{
				 myTask.log("1");
				 result.setFinishCode(Constant.finish_code.code3.getValue());
			     return result;
			}
			
			int finish_code = exe.doExecute(url, bzxr_name, resource_code, crawler_task_id);
	        result.setFinishCode(finish_code);
	        return result;
		}
		catch(Exception ex)
		{
			myTask.log(ex.getMessage());
			result.setFinishCode(Constant.finish_code.code4.getValue());
		    return result;
		}
	}
	
	/**
	 * 
	 * @param myDatabase
	 * @param resource_code
	 * @return
	 * @throws Exception
	 */
	private String getUrl(RxDatabase myDatabase, String resource_name) throws Exception {
		
		    Object[] param = new Object[1];
		    param[0] = resource_name;
			String sql = "select resource_code,resource_name,resource_url from resource_data where resource_name = ? AND use_yn = 'Y'";
			List<ResourceInfo> resourceList = myDatabase.query(sql, ResourceInfo.class, param);		 
			String url = "";
			if(resourceList != null && resourceList.size() > 0)
			{
				 url = resourceList.get(0).getResource_url();
			}
			 
			return url;
		}
	
	private List<ResourceFieldInfo> getResourceFieldData(RxDatabase myDatabase, String resource_name) throws Exception{
		
		
	    Object[] param = new Object[1];
	    param[0] = resource_name;
		String sql = "select a.resource_field_name,a.resource_field_xpath from resource_field_data a inner join resource_data b on a.resource_code=b.resource_code where b.resource_name = ? AND a.use_yn = 'Y'";
		List<ResourceFieldInfo> resourceFieldList;
	
			resourceFieldList = myDatabase.query(sql, ResourceFieldInfo.class, param);
			return resourceFieldList;
				 
}
	
	/**
	 * 
	 * @param myDatabase
	 * @param lstEntity
	 * @param version
	 * @throws Exception
	 */
	private void save(RxDatabase myDatabase, List<ZhixingrenInfo> lstEntity, String crawler_task_id) throws Exception {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//�������ڸ�ʽ
		String sql = "INSERT INTO t_beizhixingrenxinxilist"
				+ "(`crawler_key`,"
				+ "	`mingcheng`,"
				+ " `bianhao`,"
				+ " `lianshijian`,"
				+ " `anhao`,"
				+ " `zhixingfayuan`,"
				+ " `zhixingbiaodi`,"
				+ " `crawler_task_id`,"
				+ "`insert_time`,"
				+ "`update_time`)"
				+ " VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
		
		int size = lstEntity.size();
		Object[][] params = new Object[size][];
		for (int i = 0; i < size; i++) {
			ZhixingrenInfo entity = lstEntity.get(i);
			Object[] param = new Object[] {
					entity.getMingcheng(),
					entity.getMingcheng(),
					entity.getBianhao(),
					entity.getLianshijian(),
					entity.getAnhao(),
					entity.getZhixingfayuan(),
					entity.getZhixingbiaodi(),
					crawler_task_id,
					df.format(new Date()),
					df.format(new Date())
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

		public int doExecute(String url,String bzxr_name, String resource_code, String crawler_task_id) throws Exception {
			 
			myCrawler.open(url, "ȫ����Ժ��ִ������Ϣ��ѯ - ��ִ���˲�ѯ");
			sleep(5000);
			int finish_code = capture(bzxr_name, resource_code, crawler_task_id);
			return finish_code;
		}

		private int capture(String bzxr_name, String resource_code, String crawler_task_id) {
			
		try {
				//��ȡ�����ı���ڵ�
				RxNode inputName = myCrawler.getNodeByXpath("//*[@id='pname']");
				if(inputName != null)
				{
					inputName.input(bzxr_name);
				}
				else
				{
					return Constant.finish_code.code5.getValue();
				}
				
				if(!VerificationCodeChecked())
				{
					//��֤�����
					return Constant.finish_code.code7.getValue();
				}
				//��ȡ��Ҫץȡ��xpath
				List<ResourceFieldInfo> resFieldInfoList = getResourceFieldData(myDatabase, resource_code);
				if(resFieldInfoList.equals(null) || resFieldInfoList.size() == 0)
				{
					return Constant.finish_code.code8.getValue();
				}
				//myTask.log("2");
				List<ZhixingrenInfo> ZhixingrenInfoList = new ArrayList<ZhixingrenInfo>();
				RxNode node = myCrawler.getNodeByXpath("//*[@id='ResultlistBlock']/div");
				//myTask.log("ҳ����Ϣ��" + node.getText());
				String[] page_info = node.getText().split(" ");
				if(page_info != null && page_info.length > 0)
				{
					//������
					int number = Integer.valueOf(page_info[6].replaceAll("��", "").replaceAll("��", ""));
					if(number == 0) {return Constant.finish_code.code201.getValue();}
					int page_size = Integer.parseInt(page_info[5].substring(2), 10);
					for(int i = 1; i <= page_size; i++)
					{
						List<ZhixingrenInfo> ZhixingrenInfoListTemp = GetZhixingrenInfo(resFieldInfoList);
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
					
					if(number != ZhixingrenInfoList.size())
					{
						return Constant.finish_code.code11.getValue();
					}
					else
					{
						//��������
						save(myDatabase, ZhixingrenInfoList, crawler_task_id);
						return Constant.finish_code.code200.getValue();
					}
				}
				else
				{
					return Constant.finish_code.code10.getValue();
				}
			} 
			catch (Exception e) {
				// TODO Auto-generated catch block
				return Constant.finish_code.code9.getValue();
			}
		}
		
		private boolean NextPageIsSuccessed(int currentPage)
		{
			//myTask.log("4");
			boolean flag = false;
			
			
			//�����һҳ//*[@id="ResultlistBlock"]/div/a[1]
			RxNode node_next = myCrawler.getNodeByXpath("//*[@id=\"ResultlistBlock\"]/div/a[text()='��һҳ']");
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
			
			
			
			/*
			
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
    		
    		*/
			
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
		private List<ZhixingrenInfo> GetZhixingrenInfo(List<ResourceFieldInfo> resFieldInfoList)
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
						if(!myCrawler.getNodeByXpath("//*[@id='ResultView']").getAttribute("style").contains("display: block"))
						{
							//myTask.log("3.4");	
							//�����������˵����֤�������Ҫ����ʶ����֤��
							if(!VerificationCodeCheckedByChakan(xpath))
							{
								//myTask.log("3.3");
								break;
							}
						}

						RxNode node;
						String nodeText = "";
						ZhixingrenInfo zxrInfo = new ZhixingrenInfo();
						for(ResourceFieldInfo item : resFieldInfoList)
						{
							node = myCrawler.getNodeByXpath(item.getResource_field_xpath());
							if(node != null)
							{
								nodeText = node.getText();
							}
							
							if(item.getResource_field_name().equals("mingcheng"))
							{
								zxrInfo.setMingcheng(nodeText);
							}
							if(item.getResource_field_name().equals("bianhao"))
							{
								zxrInfo.setBianhao(nodeText);
							}
							if(item.getResource_field_name().equals("zhixingfayuan"))
							{
								zxrInfo.setZhixingfayuan(nodeText);
							}
							if(item.getResource_field_name().equals("lianshijian"))
							{
								zxrInfo.setLianshijian(nodeText);
							}
							if(item.getResource_field_name().equals("anhao"))
							{
								zxrInfo.setAnhao(nodeText);
							}
							if(item.getResource_field_name().equals("zhixingbiaodi"))
							{
								zxrInfo.setZhixingbiaodi(nodeText);
							}
						}
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
	
	public static class ResourceInfo {
		private int resource_code;
		private String resource_name;
		private String resource_url;
		public int getResource_code() {
			return resource_code;
		}
		public void setResource_code(int resource_code) {
			this.resource_code = resource_code;
		}
		public String getResource_name() {
			return resource_name;
		}
		public void setResource_name(String resource_name) {
			this.resource_name = resource_name;
		}
		public String getResource_url() {
			return resource_url;
		}
		public void setResource_url(String resource_url) {
			this.resource_url = resource_url;
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
}
