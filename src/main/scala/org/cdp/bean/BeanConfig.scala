package org.cdp.bean

import com.typesafe.config.Config

case class BeanConfig(
                       schemaConfig: Config,
                       kafkaConfig: Config,
                       esConfig: Config,
                       flinkConfig: Config
                     ) {

}
