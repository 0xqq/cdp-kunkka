package org.cdp.bean

import com.mongodb.BasicDBObject
case class BeanStreamFilter(
                           exceptionData: Option[String],
                           originData: Option[String],
                           resultData: Option[BasicDBObject]
                           ) {

}
