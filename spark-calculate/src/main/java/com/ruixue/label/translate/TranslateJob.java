package com.ruixue.label.translate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.api.java.function.ForeachFunction;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.api.java.function.MapGroupsFunction;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.ml.param.ParamPair;
import org.apache.spark.rdd.PairRDDFunctions;
import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.GroupedDataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ruixue.SparkCalculate;
import com.ruixue.label.translate.LabelMapper;
import com.ruixue.label.translate.LabelResult;
import com.ruixue.label.translate.LabelSource;
import com.ruixue.label.translate.SourceData;

import breeze.linalg.cond;
import breeze.linalg.fliplr;
import scala.Tuple2;

public class TranslateJob {
	
	public static Dataset<LabelResult> translate(Dataset<LabelSource> dsLabelSource,
			Dataset<LabelMapper> dsLabelMapper,Dataset<SourceData> dsSourceData){
		Column col_1 = new Column("source");
		
		Column col_2 = new Column("labelDesc");
		
		Column col_3 = new Column("labelID");
		
		Column col_4 = new Column("labelSourceID");
		
		Dataset<Tuple2<LabelSource, LabelMapper>> ds1 = dsLabelSource.joinWith(dsLabelMapper, col_1.equalTo(col_2),"left_outer");
		
		Dataset<LabelSourceMapper> dsSourceMapper = ds1.map(new MapFunction<Tuple2<LabelSource, LabelMapper>,LabelSourceMapper>() {

			@Override
			public LabelSourceMapper call(Tuple2<LabelSource, LabelMapper> arg0) throws Exception {
				// TODO Auto-generated method stub
				LabelSourceMapper sourceMapper = new LabelSourceMapper();
				sourceMapper.setLabelSourceID(arg0._1.getLabelID());
				sourceMapper.setLabelName(arg0._1.getLabelName());
				sourceMapper.setLabelDesc(arg0._1.getLabelDesc());
				sourceMapper.setNewLabel(arg0._2.getNewLabel());
				return sourceMapper;
			}
		}, Encoders.bean(LabelSourceMapper.class));
		
		Dataset<Tuple2<SourceData, LabelSourceMapper>> dsResult = dsSourceData.joinWith(dsSourceMapper, col_3.equalTo(col_4), "left_outer");
		
		return dsResult.map(new MapFunction<Tuple2<SourceData, LabelSourceMapper>,LabelResult>() {

			@Override
			public LabelResult call(Tuple2<SourceData, LabelSourceMapper> arg0) throws Exception {
				// TODO Auto-generated method stub
				LabelResult labelResult = new LabelResult();
				String tagValue = arg0._2.getLabelName();
				String tagPath = arg0._2.getNewLabel();
				String labelID = DigestUtils.md5Hex(tagPath + "_" + tagValue);
				labelResult.setTagValueCode(labelID);
				labelResult.setLabelDesc(arg0._2.getLabelDesc());
				labelResult.setTagValue(tagValue);
				labelResult.setPersonID(arg0._1.getPersonID());
				labelResult.setStatPeriod(arg0._1.getStatPeriod());
				labelResult.setCreateTime(arg0._1.getCreateTime());
				return labelResult;
			}
		}, Encoders.bean(LabelResult.class));
			
	}
	
	public static void storeAll(Dataset<LabelResult> data, String storePath){
		Column col = new Column("labelDesc");
		List<Row> list = data.select(col).distinct().collectAsList();
		for(int i = 0; i < list.size(); i++){
			String labelDesc = list.get(i).getString(0);
			String strPath = storePath + "//" + labelDesc + ".tagged";
			Dataset<LabelResult> result = data.filter(new FilterFunction<LabelResult>() {

				@Override
				public boolean call(LabelResult value) throws Exception {
					// TODO Auto-generated method stub
					if(value.getLabelDesc().equals(labelDesc)){
						return true;
					}else{
						return false;
					}
				}
			});
			
			if(result.count() > 0){
				result.toDF().repartition(200).write().json(strPath);
			}
		}
	}
	
	public static void store(Dataset<LabelResult> data, String storePath){
		Column col = new Column("labelDesc");
		List<Row> list = data.select(col).distinct().collectAsList();
		for(int j = 0; j < list.size(); j++){
			if(list.get(j).get(0).equals("ageDistribution")){
				list.remove(j);
			}else if(list.get(j).get(0).equals("averageWorkYear")){
				list.remove(j);
			}else if(list.get(j).get(0).equals("collegeName")){
				list.remove(j);
			}else if(list.get(j).get(0).equals("is211")){
				list.remove(j);
			}else if(list.get(j).get(0).equals("presentCity")){
				list.remove(j);
			}else if(list.get(j).get(0).equals("salaryLevel")){
				list.remove(j);
			}else if(list.get(j).get(0).equals("languageSkillRWJP")){
				list.remove(j);
			}else{}
		}
		for(int i = 0; i < list.size(); i++){
			String labelDesc = list.get(i).getString(0);
			String strPath = storePath + "//" + labelDesc + ".tagged";
			Dataset<LabelResult> result = data.filter(new FilterFunction<LabelResult>() {

				@Override
				public boolean call(LabelResult value) throws Exception {
					// TODO Auto-generated method stub
					if(value.getLabelDesc().equals(labelDesc)){
						return true;
					}else{
						return false;
					}
				}
			});
			
			if(result.count() > 0){
				result.toDF().repartition(200).write().json(strPath);
			}
		}
		
	}
	
	public static void store1(Dataset<LabelResult> data, String storePath){
		
		Column col = new Column("labelDesc");
		
		Dataset<Tuple2<String, List>> dsTran1 = data.groupBy(col).mapGroups(new MapGroupsFunction<Row,LabelResult,Tuple2<String,List>>() {

			@Override
			public Tuple2 call(Row arg0, Iterator<LabelResult> arg1) throws Exception {
				// TODO Auto-generated method stub
				List<LabelResult> list = new ArrayList<>();
				while(arg1.hasNext()){
					list.add(arg1.next());
				}
//				Dataset<LabelResult> ds = SparkCalculate.sqlc.createDataset(list, Encoders.bean(LabelResult.class));
				Tuple2<String, List<LabelResult>> tuple = new Tuple2<String, List<LabelResult>>(arg0.get(0).toString(),list);
				return tuple;
			}
		},Encoders.tuple(Encoders.STRING(), Encoders.kryo(List.class)));
		
		List<Tuple2<String, List>> list1 = dsTran1.collectAsList();
		
		for(int i = 0; i < list1.size(); i++){
			Tuple2<String, List> tuple = list1.get(i);
			String labelDesc = tuple._1;
			String strPath = storePath + "//" + labelDesc + ".tagged";
			List<LabelResult> list = (List<LabelResult>) tuple._2;
			Dataset<LabelResult> ds = SparkCalculate.sqlc.createDataset(list, Encoders.bean(LabelResult.class));
//			ds.toDF().repartition(10).write().json(strPath);
			ds.toDF().show();
			ds.cache();
		}
	}
	
	public static Dataset<LabelResult> edit(Dataset<LabelSourceResult> data){
		Dataset<LabelResult> result = data.map(new MapFunction<LabelSourceResult,LabelResult>() {

			@Override
			public LabelResult call(LabelSourceResult value) throws Exception {
				// TODO Auto-generated method stub
				LabelResult labelResult = new LabelResult();
				labelResult.setCreateTime(value.getCreateTime());
				labelResult.setLabelDesc(value.getLabelDesc());
				labelResult.setPersonID(value.getPersonID());
				labelResult.setStatPeriod(value.getStatPeriod());
				labelResult.setTagValue(value.getLabelName());
				labelResult.setTagValueCode(value.getLabelID());
				return labelResult;
			}
		}, Encoders.bean(LabelResult.class));
		
		return result;
		
	}

}
