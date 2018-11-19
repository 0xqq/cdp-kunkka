package org.cdp.sink

import java.util.Properties

import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}
import org.apache.flink.streaming.connectors.fs.StringWriter
import org.apache.flink.streaming.connectors.fs.bucketing.{BucketingSink, DateTimeBucketer}


class SinkHdfs[T] extends RichSinkFunction[T] {

  override def open(parameters: Configuration): Unit = super.open(parameters)

  override def invoke(value: T, context: SinkFunction.Context[_]): Unit = super.invoke(value, context)

  override def close(): Unit = super.close()

  def hdfsSink(props: Properties): BucketingSink[(String, String)] = {

    val sink = new BucketingSink[(String, String)]("hdfs://localhost/file")
    sink.setBucketer(new DateTimeBucketer[(String, String)]("yyyy-MM-dd--HHmm"))
    sink.setWriter(new StringWriter[(String, String)]())

    val defInterval = 5 * 60 * 1000L
    sink.setInactiveBucketThreshold(defInterval)
    sink.setInactiveBucketCheckInterval(defInterval)

    val defaultSize = 1024 * 1024 * 128 //128 MB
    sink.setBatchSize(defaultSize)

    val defaultTime = 1000L * 60 * 30  //30 min
    sink.setAsyncTimeout(defaultTime)
    sink.setPartPrefix(System.currentTimeMillis().toString)

    sink

  }

}
