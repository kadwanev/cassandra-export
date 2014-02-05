package com.kadwa.cassandraexport

import com.asimma.ScalaHadoop._
import org.apache.hadoop.io.{Text, LongWritable}
import ImplicitConversion._
import scala.collection.JavaConversions._

/*
 * Created by Neville Kadwa.
 */

object MapReduceMain extends ScalaHadoopTool {

  object CassandraExportAttributeMapper extends TypedMapper[LongWritable, Text, InterimKey, InterimValue] {

    var cassandraExport:CassandraExport = null

    override def setup(context:ContextType) = {
      System.out.println("New Cassandra Export")
      cassandraExport = new CassandraExport("src/test/resources/scottdata")
    }

    override def cleanup(context:ContextType) = {
      System.out.println("CassandraExportMapper.cleanup called. Closing CassandraExport")
      cassandraExport.close
    }

    override def map(k: LongWritable, v: Text, context: ContextType) : Unit = {
      val sstableName = v.toString

      cassandraExport.exportSSTable(sstableName, (attribute) => {
        context.write(InterimKey(attribute._1,attribute._2), InterimValue(attribute._3,attribute._4,attribute._5))
      })
    }
  }

  object CassandraExportAttributeReducer extends TypedReducer[InterimKey, InterimValue, InterimKey, InterimValue] {
    override def reduce(k: InterimKey, v: java.lang.Iterable[InterimValue], context: ContextType) : Unit = {
      context.write(k, v.min)
    }
  }

  object CassandraExportRecordMapper extends TypedMapper[InterimKey, InterimValue, Text, Text] {
    override def map(k: InterimKey, v: InterimValue, context: ContextType) : Unit = {
      val sb = new StringBuffer
      sb.append("[\"").append(k.name).append("\",\"").append(v.value).append("\",").append(v.timestamp)
      if (v.extra != null) {
        sb.append(",\"")
        sb.append(v.extra)
        sb.append("\"")
      }
      sb.append("]")
      context.write(k.key, sb.toString)
    }
  }
  object CassandraExportRecordReducer extends TypedReducer[Text, Text, Text, Text] {
    override def reduce(k: Text, v: java.lang.Iterable[Text], context: ContextType) : Unit = {
      val value = v.mkString(", ")
//      System.err.println(k + " => " + value)
      context.write(s"""{\"key\":${k}, columns:[${value}]}""", null)
    }
  }

  def run(args: Array[String]) : Int = {

    val c = MapReduceTaskChain.init() -->
      IO.Text[LongWritable, Text](args(0)).input                                      -->
      MapReduceTask.MapReduceTask(CassandraExportAttributeMapper, CassandraExportAttributeReducer)    -->
      MapReduceTask.MapReduceTask(CassandraExportRecordMapper, CassandraExportRecordReducer)          -->
      IO.Text[Text, Text](args(1)).output
    c.execute()
    0
  }

}
