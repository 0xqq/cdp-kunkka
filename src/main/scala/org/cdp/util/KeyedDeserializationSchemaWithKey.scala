package org.cdp.util

import java.io.IOException

import org.apache.flink.api.common.serialization.DeserializationSchema
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.util.serialization.KeyedDeserializationSchema

/** *****************************************************************************************************************
  * 消费数据的序列化类
  * 并且将消费的数据以 \t 分隔的方式返回
  */
class KeyedDeserializationSchemaWithKey(deserializationSchema: DeserializationSchema[String])
  extends KeyedDeserializationSchema[String] {

  val serialVersionUID = 2651665280744549932L

  @throws[IOException]
  override def deserialize(messageKey: Array[Byte], message: Array[Byte], topic: String, partition: Int, offset: Long): String = {

    val key = if (messageKey == null || messageKey.length == 0) "" else this.deserializationSchema.deserialize(messageKey)

    val value = if (message == null || message.length == 0) "" else this.deserializationSchema.deserialize(message)

    s"$key\t$value"

  }

  override def isEndOfStream(nextElement: String): Boolean = this.deserializationSchema.isEndOfStream(nextElement)

  override def getProducedType: TypeInformation[String] = this.deserializationSchema.getProducedType

}
