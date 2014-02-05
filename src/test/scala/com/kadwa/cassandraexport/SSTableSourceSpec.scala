package com.kadwa.cassandraexport


/*
 * Created by Neville Kadwa.
 */
class SSTableSourceSpec extends FlatSpec with Matchers with TupleConversions {

  /*
  "SSTableSource" should "open sstables" in {
    JobTest("GenerateDatabase").
      arg("input", "testdata/tablelist.txt").
      arg("output", "outputFile.dat").
      source(SSTableSource("testdata/tablelist.txt"), List("0" -> "hack hack hack")).
      sink[(String,Int)](Tsv("output")){ outputBuffer =>
      val outMap = outputBuffer.toMap
    }.
      run.
      finish
  }
*/

}
