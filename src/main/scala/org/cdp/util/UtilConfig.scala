package org.cdp.util

import java.io.File

import com.typesafe.config.ConfigFactory
import org.cdp.bean.BeanConfig
import org.slf4j.LoggerFactory

object UtilConfig {
  val configUtil = new UtilConfig
  def loadParams(args: Array[String]): String = configUtil.loadParams(args)
  def loadConfig(filePath: String): BeanConfig = configUtil.loadConfig(filePath)
}

class UtilConfig {

  private val LOG = LoggerFactory.getLogger(classOf[UtilConfig])

  def loadParams(args: Array[String]): String = args match {
//    case a if a.length != 1 =>
//      LOG.error("Params number size error, Use a configuration file!")
//      System.exit(0)
//    case b if !b.head.endsWith("conf") =>
//      LOG.error("Configuration file name error!")
//      System.exit(0)
//    case c if !c.head.startsWith("/") =>
//      LOG.error("Use absolute paths for configuration files!")
//      System.exit(0)
//    case d if !d.head.contains(System.getProperty("user.dir") + "/src/main/resources") =>
//      LOG.error(s"Configuration file usage error, Use the configuration file in the ${System.getProperty("user.dir")}/src/main/resources directory!")
//      System.exit(0)
    case _ => args(0)
  }

  def loadConfig(filePath: String): BeanConfig = {
    val config = ConfigFactory.parseFile(new File(filePath))

    val schemaConfig = config.getConfig("SCHEMA")
    val kafkaConfig = config.getConfig("KAFKA")
    val esConfig = config.getConfig("ES")
    val flinkConfig = config.getConfig("FLINK")

    BeanConfig(schemaConfig, kafkaConfig, esConfig, flinkConfig)
  }
}
