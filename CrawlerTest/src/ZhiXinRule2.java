
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
			
			myCrawler.open(url, "全国法院被执行人信息查询 - 被执行人查询");
			sleep(5000);

			List<ZhixingrenInfo> lstEntity = capture();

			return lstEntity;
		}

		private List<ZhixingrenInfo> capture() {
			
			//myTask.log("1");
			//获取执行人姓名和code参数
			String bzxr_name = myTask.getV1();
			String bzxr_code = myTask.getV2();
			//如果name不为空，把name的值放入文本框
			if(!"".equals(bzxr_name) && !"-1".equals(bzxr_name))
			{
				//获取名称文本域节点
				RxNode inputName = myCrawler.getNodeByXpath("//*[@id='pname']");
				if(inputName != null)
				{
					inputName.input(bzxr_name);
				}
			}
			//如果name不为空，把name的值放入文本框
			if(!"".equals(bzxr_code) && !"-1".equals(bzxr_code))
			{
				//获取名称文本域节点
				RxNode inputCode = myCrawler.getNodeByXpath("//*[@id='cardNum']");
				if(inputCode != null)
				{
					inputCode.input(bzxr_code);
				}
			}
			
			if(!VerificationCodeChecked())
			{
				//验证码错误
				return null;
			}
			//myTask.log("2");
			List<ZhixingrenInfo> ZhixingrenInfoList = new ArrayList<ZhixingrenInfo>();
			RxNode node = myCrawler.getNodeByXpath("//*[@id='ResultlistBlock']/div");
			//myTask.log("页数信息：" + node.getText());
			String[] page_info = node.getText().split(" ");
			//总条数
			int number = Integer.valueOf(page_info[6].replaceAll("共", "").replaceAll("条", ""));
			if(page_info != null && page_info.length > 0)
			{
				int page_size = Integer.parseInt(page_info[5].substring(2), 10);
				for(int i = 1; i <= page_size; i++)
				{
					List<ZhixingrenInfo> ZhixingrenInfoListTemp = GetZhixingrenInfo();
					
					myTask.log("第 " + String.valueOf(i) + " 页 " + " 条数 ： " + ZhixingrenInfoListTemp.size());
					if(i < page_size && ZhixingrenInfoListTemp.size() < 10)
					{
						//myTask.log("10");
						//说明当前页没有抓全，信息不完整
						break;
					}
					if(i == page_size && ZhixingrenInfoListTemp.size() != (number - ((i - 1) * 10)))
					{
						//myTask.log("11");
						//说明当前页没有抓全，信息不完整
						break;
					}
					//把当前页面抓取的信息放到集合中
					ZhixingrenInfoList.addAll(ZhixingrenInfoListTemp);
					
			 
					//判断是否成功跳到下一页
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
			//点击下一页//*[@id="ResultlistBlock"]/div/a[1]
			RxNode node_next = myCrawler.getNodeByXpath("//*[@id='ResultlistBlock']/div/a[3]",false);
			if(node_next != null)
			{
				myTask.log("7");
				node_next.click();
				 //判断是否有验证码错误提示
                RxNode nodeYzmErrorMsg = myCrawler.getNodeByXpath("/html/body/div/h4/span");
                if(nodeYzmErrorMsg != null && nodeYzmErrorMsg.getText().contains("验证码错误"))
                {
                	myTask.log("5");
                	//重新识别验证码查询
                	if(VerificationCodeChecked())
                	{
                		myTask.log("10");
                		//实现跳到指定页
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
                	//说明说明成功跳到下一页
                	flag = true;
                }
			}
			else
			{
				myTask.log("8");
			}
			*/
			
			
			//实现跳到指定页
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
    				 //判断是否有验证码错误提示
                    RxNode nodeYzmErrorMsg = myCrawler.getNodeByXpath("/html/body/div/h4/span");
                    if(nodeYzmErrorMsg != null && nodeYzmErrorMsg.getText().contains("验证码错误"))
                    {
                    	//myTask.log("33");
                    	
                    	//重新识别验证码查询
                    	if(VerificationCodeChecked())
                    	{
                    		//myTask.log("66");
                    		//实现跳到指定页
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
		 * 验证码验证
		 */
		private boolean VerificationCodeChecked()
		{
			boolean flag = true;
			
			 //点击换一张
	        RxNode changeInputCode = myCrawler.getNodeByXpath("//*[@id='searchForm']/table/tbody/tr[4]/td/span/a");
	        changeInputCode.click();
	        sleep(1000);
	        
			for(int i =0 ;i < 3; i++)
			{
				// 点击换一张验证码
                if ( i != 0 ) {
                	RxNode nodeYzm = myCrawler.getNodeByXpath("//*[@id='searchForm']/table/tbody/tr[4]/td/span/a");
                	nodeYzm.click();
                    sleep(1000);
                }
                //获取验证码
                myCrawler.inputVerifyCode("//*[@id='captchaImg']" , "//*[@id='j_captcha']");
                
                RxNode nodeSearch = myCrawler.getNodeByXpath("//*[@id='button']");
                nodeSearch.click();
                sleep(1000);
                //判断是否有验证码错误提示
                RxNode nodeYzmErrorMsg = myCrawler.getNodeByXpath("/html/body/div/h4/span");
                if(nodeYzmErrorMsg != null && nodeYzmErrorMsg.getText().contains("验证码错误"))
                {
                	flag = false;
                	continue;
                }
                else	 
                {
                	//说明验证码通过
                	flag = true;
                	break;
                }
			}
			
			return flag;
		}
		
		/*
		 * 验证码验证
		 */
		private boolean VerificationCodeCheckedByChakan(String xpath)
		{	
			//myTask.log("4.1");	
			boolean flag = true;
			 //点击换一张
	        RxNode changeInputCode = myCrawler.getNodeByXpath("//*[@id='searchForm']/table/tbody/tr[4]/td/span/a");
	        changeInputCode.click();
	        sleep(1000);
	        
			for(int i =0 ;i < 3; i++)
			{
				//myTask.log("验证码第 " + String.valueOf(i) + " 次尝试 ");	
				// 点击换一张验证码
                if ( i != 0 ) {
                	RxNode nodeYzm = myCrawler.getNodeByXpath("//*[@id='searchForm']/table/tbody/tr[4]/td/span/a");
                	nodeYzm.click();
                    sleep(1000);
                }
                //myTask.log("4.4");	
                //获取验证码
                myCrawler.inputVerifyCode("//*[@id='captchaImg']" , "//*[@id='j_captcha']");
                //点击查看
                List<RxNode> nodeCheck = myCrawler.getNodeListByXpath(xpath);
    			nodeCheck.get(0).click();
    			sleep(1000);
    			
				//myTask.log("4.5");	
				//先执行一下关闭alert
				CloseAlert(myCrawler);
				//判断是否弹出明细
				if(myCrawler.getNodeByXpath("//*[@id='ResultView']").getAttribute("style").contains("display: block"))
				{
					//说明验证码通过
                	flag = true;
                	//myTask.log("验证码第 " + String.valueOf(i) + " 次尝试  ，说明验证码通过");	
                	break;
				}
				else
				{
					flag = false;
					//myTask.log("验证码第 " + String.valueOf(i) + " 次尝试  ，说明验证码失败");	
                	continue;
				}
			}
			
			return flag;
		}
		
		/*
		 * 分页获取执行人信息
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
						
						//myTask.log("第  " + String.valueOf(k) + " 条");	
						//先执行一下关闭alert
						CloseAlert(myCrawler);
						//判断是否弹出明细
						if(!myCrawler.getNodeByXpath("//*[@id='ResultView']").isDisplayed())
						{
							//myTask.log("3.4");	
							//如果不包含，说明验证码错误，需要重新识别验证码
							if(!VerificationCodeCheckedByChakan(xpath))
							{
								//myTask.log("3.3");
								break;
							}
						}

						//读取明细信息
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
	 * 执行人信息
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
