package org.cdp.source

import java.util.Properties

import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010
import com.typesafe.config.Config
import org.cdp.util.{KeyedDeserializationSchemaWithKey, UtilStringSchema}

/*================================================================================*\
  时间: 2018/06/16
  作者: 陈大炮
  内容: 消费kafka工具类
\*================================================================================*/

object SourceKafka {

  /* ***********************************************************************************************************
   * kafka consumer010 消费方法
   */
  def consumer010(kafkaConfig: Config): FlinkKafkaConsumer010[String] = {

    val topic = kafkaConfig.getString("TOPIC")

    val props = new Properties()
    props.put("bootstrap.servers", kafkaConfig.getString("BOOTSTRAP"))
    props.put("zookeeper.connect", kafkaConfig.getString("ZOOKEEPER"))
    props.put("key.deserializer", kafkaConfig.getString("KDESERIALIZER"))
    props.put("value.deserializer", kafkaConfig.getString("VDESERIALIZER"))
    props.put("group.id", kafkaConfig.getString("GROUP.ID"))
    props.put("auto.offset.reset", kafkaConfig.getString("AUTO.OFFSET.RESET"))
    props.put("auto.commit.enable", kafkaConfig.getString("AUTO.COMMIT.ENABLE"))

    new FlinkKafkaConsumer010[String](topic,
      new KeyedDeserializationSchemaWithKey(UtilStringSchema),
      props)
  }


}
