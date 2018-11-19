package org.cdp.transformation

import com.alibaba.fastjson.JSON
import com.mongodb.{BasicDBObject => mongodb}
import org.apache.flink.api.common.accumulators.LongCounter
import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.configuration.Configuration
import org.cdp.bean.BeanStreamFilter
import org.slf4j.LoggerFactory

/*================================================================================*\
  时间: 2018-06-21
  作者: 陈大炮
  内容: 消费kafka的数据,做一些基本校验,过滤垃圾数据
\*================================================================================*/

class TransFilter() extends RichMapFunction[String, BeanStreamFilter] {

  /* ***********************************************************************************************************
   * 全局变量,一些配置东西
   */
  private val LOG = LoggerFactory.getLogger(classOf[TransFilter])

  val trans_filter: LongCounter = new LongCounter()

  /* ***********************************************************************************************************
   * open 阶段
   */
  override def open(parameters: Configuration): Unit = {
    getRuntimeContext.addAccumulator("trans_filter", trans_filter)
  }

  /* ***********************************************************************************************************
   * map 阶段
   */
  override def map(in: String): BeanStreamFilter = {

    //计数器
    trans_filter.add(1)

    //source 接收到的数据分成kv进行json解析处理
    val inData = in.split("\t")
    val (keyData, valueData) = try {
      (JSON.parseObject(inData.head),
        mongodb.parse(inData.last))
    } catch {
      case j: com.alibaba.fastjson.JSONException =>
        LOG.error(s"JSON parse exception: ${j.getMessage}")
        return BeanStreamFilter(
          exceptionData = Some(j.getMessage),
          originData = Some(in),
          resultData = None
        )
      case m: org.bson.json.JsonParseException =>
        LOG.error(s"JOSN parse exception: ${m.getMessage}")
        return BeanStreamFilter(
          exceptionData = Some(m.getMessage),
          originData = Some(in),
          resultData = None
        )
    }

    // 这个地方是进行filer后的结果
    // 添加自己想要的业务逻辑
    val result: mongodb = null

    //返回一个case class
    BeanStreamFilter(None, None, Some(result))

  }

  /* ***********************************************************************************************************
   * close 阶段
   */
  override def close(): Unit = super.close()

}
