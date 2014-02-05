package com.asimma.ScalaHadoop;

// @link https://github.com/bsdfish/ScalaHadoop/blob/master/src/ScalaHadoopTool.scala

abstract class ScalaHadoopTool extends org.apache.hadoop.conf.Configured with org.apache.hadoop.util.Tool  {

  def main(args: Array[String]):Unit = {
    org.apache.hadoop.util.ToolRunner.run(this, args)
  }
}