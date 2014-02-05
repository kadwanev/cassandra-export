/*
 * Created by Neville Kadwa.
 */
package com.kadwa.cassandraexport

import org.apache.cassandra.config.DatabaseDescriptor
import org.apache.cassandra.io.sstable.Descriptor
import org.apache.cassandra.tools.SSTableExport
import java.io.PrintStream
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.{JsonToken, JsonFactory}
import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

/**
 *
 */
class CassandraExport(val rootDataDir:String) {

  println("INITIALIZING CASSANDRA")
  System.setProperty("cassandra.config", s"file:${rootDataDir}/cassandra.yaml")
  DatabaseDescriptor.loadSchemas()

  class CassandraJSONExport(val outStream:java.io.OutputStream, val descriptor:Descriptor) {

    val printStream = new PrintStream(outStream)

    def run(implicit executor:ExecutionContext) = {
      executor.execute(new Runnable {
        def run = {
//          System.err.println("RUNNING EXPORT: " + descriptor.toString)
          SSTableExport.export(descriptor, printStream, null.asInstanceOf[Array[String]])
          printStream.flush
        }
      })
    }
  }

  def exportSSTable(sstableName:String, columnAcceptor:CassandraAttribute => Unit) = {
    val sstableLoc = s"${rootDataDir}/${sstableName}"
    val descriptor = Descriptor.fromFilename(sstableLoc)

    val inStream = new java.io.PipedInputStream
    val outStream = new java.io.PipedOutputStream(inStream)
    val exporter = new CassandraJSONExport(outStream, descriptor)

    exporter.run
    //    val jsonString = outStream.toString
    System.err.println("FOUND JSON!")
    //    System.err.println(jsonString)
    //    val result = Initialize.json.readValue[Map[String,List[List[AnyVal]]]](jsonString)

    CassandraExport.jsonParsing(inStream, columnAcceptor)

    System.err.println("DONE WITH JSON!")
  }


//  def exportSSTable(sstableName:String) = new Traversable[CassandraAttribute] {
//     def foreach[X]( f : CassandraAttribute => X) = exportSSTable(f(_) : Unit)
//  }

  /**
   * You must close the usage of CassandraExport. Not calling this means background threads keep the process running indefinitely
   */
  def close = {
    scala.util.control.Exception.ignoring(classOf[Exception]) {
      org.apache.cassandra.service.StorageService.instance.stopGossiping
    }
    scala.util.control.Exception.ignoring(classOf[Exception]) {
      org.apache.cassandra.service.StorageService.instance.stopRPCServer
    }
    scala.util.control.Exception.ignoring(classOf[Exception]) {
      // Does not exist < 1.2
      val method = org.apache.cassandra.service.StorageService.instance.getClass.getMethod("stopNativeTransport")
      if (method != null)
        method.invoke(org.apache.cassandra.service.StorageService.instance)
//        org.apache.cassandra.service.StorageService.instance.stopNativeTransport
    }
    scala.util.control.Exception.ignoring(classOf[Exception]) {
      org.apache.cassandra.db.commitlog.CommitLog.instance.shutdownBlocking
    }
//    StorageService.instance.stopClient
  }

}

object CassandraExport {

  val jsonFactory = new JsonFactory

  // Expected Format:
  // [ {"key": "rowkeyinhex", "columns": [[colName,colValue,timestamp,extraInfo],[...],[...]]}, {"key": ...} ]
  // Implemented with O(1) performance to support wide columns. No more than 1 object key + 1 attribute loaded at a time

  // JSON column format history
  //   column name
  //   column value (when deleted, value is hex instead of validated and formatted)
  //   column timestamp (long)
  //   Since 1.0:
  //     deleted=d
  //     expiring=e, ttl time(int), local delete time(int)
  //     counter=c, lastdelete time(long)
  //   At 0.7: no extra, columns represent
  //     deleted [true|false] (full column)
  //     expiring ttl time(int) (if exists, then expiring)
  //     expiring local delete time(int)
  //   Before 0.7: deleted only
  //     deleted [true|false]
  def jsonParsing(inStream:java.io.InputStream, columnAcceptor:CassandraAttribute => Unit):Unit = {

    val jp = jsonFactory.createParser(inStream)

    try {
      if (jp.nextToken != JsonToken.START_ARRAY)
        throw new IllegalArgumentException(s"Unexpected JSON format: Start array ${jp.getCurrentToken}")

      while (jp.nextToken() != JsonToken.END_ARRAY) {
        var key:String = null

        if (jp.getCurrentToken != JsonToken.START_OBJECT)
          throw new IllegalArgumentException(s"Unexpected JSON format: Start object ${jp.getCurrentToken}")

        // Really need key to come before attributes to be able to stream attributes
        if (jp.nextToken != JsonToken.FIELD_NAME || !jp.getCurrentName.equals("key"))
          throw new IllegalArgumentException(s"Unexpected JSON format: key field ${jp.getCurrentToken}")

        if (jp.nextToken != JsonToken.VALUE_STRING)
          throw new IllegalArgumentException(s"Unexpected JSON format: key value ${jp.getCurrentToken}")

        key = jp.getText

        if (jp.nextToken != JsonToken.FIELD_NAME || !jp.getCurrentName.equals("columns"))
          throw new IllegalArgumentException(s"Unexpected JSON format: columns field ${jp.getCurrentToken}")

        if (jp.nextToken != JsonToken.START_ARRAY)
          throw new IllegalArgumentException(s"Unexpected JSON format: columns array ${jp.getCurrentToken}")

        while (jp.nextToken != JsonToken.END_ARRAY) {
          if (jp.getCurrentToken != JsonToken.START_ARRAY)
            throw new IllegalArgumentException(s"Unexpected JSON format: column array start ${jp.getCurrentToken}")
          var i = 0
          var name, value, extra:String = null
          var timestamp:Long = 0
          while (jp.nextToken != JsonToken.END_ARRAY) {
            (jp.getCurrentToken, i) match {
              case (JsonToken.VALUE_STRING, 0) => name = jp.getText
              case (JsonToken.VALUE_STRING, 1) => value = jp.getText
              case (JsonToken.VALUE_NUMBER_INT, 2) => timestamp = jp.getValueAsLong
              case (JsonToken.VALUE_STRING, 3) => extra = jp.getText
              case (JsonToken.VALUE_TRUE, 3) => extra = "d"
              case (JsonToken.VALUE_FALSE, 3) => extra = null
              case _ => throw new UnsupportedOperationException("Not yet implemented")
            }
            i += 1
          }
          if (extra != null && extra.equals("d")) {
            value = null
          }
          columnAcceptor.apply((key,name,value,timestamp,extra))
        }

        if (jp.nextToken != JsonToken.END_OBJECT)
          throw new IllegalArgumentException(s"Unexpected JSON format: End object ${jp.getCurrentToken}")
      }
    }
    finally {
      jp.close
    }

  }

}