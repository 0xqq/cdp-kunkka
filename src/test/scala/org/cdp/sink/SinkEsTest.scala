package org.cdp.sink

import com.alibaba.fastjson.JSON
import com.mongodb.{BasicDBObject => mongodb}

import scala.io.Source
import scalaj.http.Http

object SinkEsTest {
  def main(args: Array[String]): Unit = {

    val data = Source.fromFile("/Users/cdp/Documents/testdata/moxie_taobao_data.csv","utf-8").getLines().toList

    data
      .foreach {
        x =>
          val line = x.split("######")
          val key = JSON.parseObject(line.head)
          val tmpValue = mongodb.parse(line.last)

          val dbName = key.getString("dbName")
          val tableName = key.getString("tableName")
          val _id = if (tmpValue.getString("_id") != null) tmpValue.getString("_id") else tmpValue.getString("id")

          tmpValue.remove("_id")

          val timeReg = """\{[^\"]*\"\$date\"[^:]*:\s*\"?([^\"]+)\"?\}"""
          val idReg = """\{[^\"]*\"\$oid\"[^:]*:\s*\"?([^\"]+)\"?\}"""

          val tmp1 = tmpValue.toJson.replaceAll(timeReg, "$1")
          val tmp2 = tmp1.replaceAll(idReg, "$1")

          val result = mongodb.parse(tmp2)

          val esIp = "http://localhost:9200"
          val esIndex = dbName
          val esType = tableName
          val esId = _id

          val requestUrl = s"$esIp/$esIndex/$esType/$esId"
          val requestBody = s"${result.toString}"

          val response = Http(requestUrl)
            .header("Content-Type", "application/json")
            .postData(requestBody)
            .timeout(1000,5000)
            .asString

          println(response)
      }

  }
}
