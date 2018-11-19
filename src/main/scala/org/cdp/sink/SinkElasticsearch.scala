package org.cdp.sink

import com.typesafe.config.Config
import org.apache.flink.api.common.accumulators.LongCounter
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}
import org.cdp.bean.BeanStreamFilter
import org.slf4j.LoggerFactory

import scalaj.http.Http

/*================================================================================*\
  时间: 2018/06/16
  作者: 陈大炮
  内容: es sink 目前用scalaj 以 http的方式插入
\*================================================================================*/

class SinkElasticsearch(config: Config) extends RichSinkFunction[BeanStreamFilter] {

  /* ***********************************************************************************************************
   * 全局变量
   */
  private val LOG = LoggerFactory.getLogger(classOf[SinkElasticsearch])
  val sink_es: LongCounter = new LongCounter()


  /* ***********************************************************************************************************
   * open 阶段
   */
  override def open(parameters: Configuration): Unit = {
    super.open(parameters)
    getRuntimeContext.addAccumulator("sink_es", sink_es)
  }


  /* ***********************************************************************************************************
   * invoke 阶段
   */
  override def invoke(value: BeanStreamFilter, context: SinkFunction.Context[_]): Unit = {
    // super.invoke(value, context)

    //拿到结果json对象
    val resultBsonObject = value.resultData.get

    //获取配置文件内的值
    val esIp = config.getString("IP")
    val esIndex = resultBsonObject.getString("INDEX")
    val esType = resultBsonObject.getString("TYPE")
    val esId = resultBsonObject.getString("ID")

    //构建请求url 和 body
    val requestUrl = s"$esIp/$esIndex/$esType/$esId"
    val requestBody = s"${resultBsonObject.toString}"

    //发起es插入请求
    val response = Http(requestUrl)
      .postData(requestBody)
      .timeout(1000,5000)
      .asString

    //得到相应状态码
    val responseCode = response.code

    //非200类的全部认为有错误,输出接收到的case class
    if(responseCode < 200 && responseCode > 299 ) {
      LOG.error(s"$requestUrl insert failed responsebody: ${response.body} result: $value")
    } else {
      sink_es.add(1)
      LOG.info(s"$requestUrl insert success")
    }

  }

  /* ***********************************************************************************************************
   * close 阶段
   */
  override def close(): Unit = super.close()

}
