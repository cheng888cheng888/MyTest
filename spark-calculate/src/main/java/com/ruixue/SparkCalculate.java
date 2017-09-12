package com.ruixue;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.ForeachFunction;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruixue.label.translate.LabelMapper;
import com.ruixue.label.translate.LabelResult;
import com.ruixue.label.translate.LabelSource;
import com.ruixue.label.translate.LabelSourceResult;
import com.ruixue.label.translate.SourceData;
import com.ruixue.label.translate.TranslateJob;

public class SparkCalculate {
	
	public static SparkConf conf = new SparkConf()
			.setMaster("yarn-client")
			.setAppName("appName")
			.set("spark.executor.memory", "2g")
			.set("spark.cores.max", "1")
			.set("spark.dynamicAllocation.maxExecutors", "10");
	
	public static JavaSparkContext sc = new JavaSparkContext(conf);
	
	public static SQLContext sqlc = new SQLContext(sc);
	
	static final Logger logger = LoggerFactory.getLogger(SparkCalculate.class);

	/**
	 * @param args
	 */
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		

	
		if(args.length == 0){
			System.out.println("需要传递参数");
			return;
		}
		

		
		if(args.length == 1 ){
			System.out.println("任务参数");
			return;
		}else{
			if(args[1].equals("trans")){
				if(args.length < 6){
					System.out.println("参数不全");
				}else{
					Dataset<LabelMapper> dsLabelMapper = sqlc.read().json(args[2]).as(Encoders.bean(LabelMapper.class));

					Dataset<LabelSource> dsLabelSource = sqlc.read().json(args[3]).as(Encoders.bean(LabelSource.class));

					Dataset<SourceData> dsSourceData = sqlc.read().json(args[4]).as(Encoders.bean(SourceData.class));

					Dataset<LabelResult> dsResult = TranslateJob.translate(dsLabelSource, dsLabelMapper, dsSourceData);
					
					TranslateJob.store(dsResult, args[5]);
				}
			}else if(args[1].equals("edit")){
				if(args.length < 4){
					System.out.println("参数不全");
				}else{
					String[] paths = args[2].split(";");
					for(int i = 0; i < paths.length; i++){

						Dataset<LabelSourceResult> dsLabelSourceResult = sqlc.read().json(paths[i]).as(Encoders.bean(LabelSourceResult.class));
						
						Dataset<LabelResult> result = TranslateJob.edit(dsLabelSourceResult);
						
						TranslateJob.storeAll(result, args[3]);
					}
				}
				
			}else{
				
			}
		}
	
	}

}
