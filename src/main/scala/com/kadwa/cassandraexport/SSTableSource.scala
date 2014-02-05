package com.kadwa.cassandraexport

import cascading.pipe.Pipe
import com.fasterxml.jackson.core.{JsonToken, JsonFactory}
import com.fasterxml.jackson.databind.ObjectMapper
import com.twitter.scalding._
import com.twitter.scalding.TextLine
import java.io.{PrintWriter, StringWriter, PrintStream}
import scala.collection.mutable

/*
 * Created by Neville Kadwa.
 */
case class SSTableSource(p : String) extends FixedPathSource(p) with TextLineScheme {

  private val cassandraExport = new CassandraExport("src/test/resources/localdata")

  override def transformForRead(pipe:Pipe) = {
    import Dsl._

    RichPipe(pipe).mapTo('line -> 'record) {
      sstableName:String => {
        val result = new mutable.ListBuffer[(String,String,Any,Long,String)]
        cassandraExport.exportSSTable(sstableName, (attribute) => {
          result += attribute
        })
        result
      }
    }
  }

}
