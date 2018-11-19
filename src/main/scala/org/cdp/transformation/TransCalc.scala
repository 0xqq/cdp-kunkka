package org.cdp.transformation

import grizzled.slf4j.Logger
import org.apache.flink.api.common.functions.RichMapFunction
import org.apache.flink.configuration.Configuration
import org.cdp.bean.{BeanStreamCalc, BeanStreamFilter}


class TransCalc extends RichMapFunction[BeanStreamFilter, BeanStreamCalc] {

  val log = Logger(getClass)

  override def open(parameters: Configuration): Unit = super.open(parameters)

  override def map(in: BeanStreamFilter): BeanStreamCalc = ???
  //map阶段实现自己的业务逻辑，in就是你数据流入进来的类型，可以直接用in点出来里面的元素
  //最后计算完成返回的类型为 BeanStreamCalc

  override def close(): Unit = super.close()

}
