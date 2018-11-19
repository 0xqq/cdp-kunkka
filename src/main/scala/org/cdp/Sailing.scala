package org.cdp

import org.apache.flink.api.common.restartstrategy.RestartStrategies
import org.apache.flink.runtime.state.filesystem.FsStateBackend
import org.apache.flink.streaming.api.CheckpointingMode
import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}
import org.cdp.bean.BeanStreamFilter
import org.cdp.sink.SinkElasticsearch
import org.cdp.transformation.TransFilter
import org.cdp.util.UtilConfig
import org.cdp.source.SourceKafka.consumer010

/*================================================================================*\
  时间: 2018/06/16
  作者: 陈大炮
  内容: 程序主类,启动所用
\*================================================================================*/

object Sailing {

  def main(args: Array[String]): Unit = {

    /* ***********************************************************************************************************
     * 基本配置阶段
     */
    //启动任务的入参工厂方法
    println("confpath")
    val confPath = UtilConfig.loadParams(args)

    println("conf")
    //配置文件的工厂方法,会把各类配置分类,方便使用
    val conf = UtilConfig.loadConfig(confPath)


    /* ***********************************************************************************************************
     * flink 配置阶段
     */
    //环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    //并行
    env.setParallelism(conf.flinkConfig.getInt("PARALLELISM"))

    //checkpoint
    env.enableCheckpointing(1000 * 60)
    env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE)
    env.getCheckpointConfig.setCheckpointTimeout(conf.flinkConfig.getInt("CHECKPOINT.TIMEOUT"))
    env.getCheckpointConfig.setMaxConcurrentCheckpoints(1)
    env.setStateBackend(new FsStateBackend(conf.flinkConfig.getString("CHECKPOINT.PATH")))

    //重启策略
    env.setRestartStrategy(RestartStrategies.fixedDelayRestart(
      conf.flinkConfig.getInt("RESTART.NUM"),
      conf.flinkConfig.getInt("RESTART.TIME")
    ))


    /* ***********************************************************************************************************
     * 加载计算逻辑
     */
    //val transLogic = ???


    /* ***********************************************************************************************************
     * source 阶段
     */
    //消费kafka的数据
    val sourceKafkaStream = env
      .addSource(consumer010(conf.kafkaConfig))
      .name("source_kafka")


    /* ***********************************************************************************************************
     * transformation 阶段
     */
    val transFilterStream: DataStream[BeanStreamFilter] = sourceKafkaStream
      .map(new TransFilter)
      .name("trans_filter")


    /* ***********************************************************************************************************
     * sink 阶段
     */
    //过滤流没有通过的数据,为问题数据,流入hdfs中,分析问题原因

    //先直接试试过滤后就存es
    transFilterStream
      .filter(_.exceptionData.isEmpty)
      .addSink(new SinkElasticsearch(conf.esConfig))
      .name("sink_filter")

    //计算流的数据最后流入es中
    //    transCalcStream
    //      .addSink(new SinkElasticsearch[BeanStreamCalc])
    //      .name("sink_calc")


    /* ***********************************************************************************************************
     * start 阶段
     */
    env.execute(conf.flinkConfig.getString("JOBNAME"))

  }

}
