package com.kadwa.cassandraexport

/*
 * Created by Neville Kadwa.
 */

import scala.collection.mutable
import com.kadwa.cassandraexport.CassandraExport

class CassandraExportSpec extends FlatSpec with Matchers with PrivateMethodTester {

  "CassandraExport" should "open cass1.2" in {
    val exporter = new CassandraExport("src/test/resources/scottdata")

    var numAttributes = 0
    exporter.exportSSTable("place_directory_development/Places/place_directory_development-Places-ib-17-Data.db", attribute => {
      numAttributes += 1
//      println(attribute)
    })

    assertResult(646503)(numAttributes)
    exporter.close
  }

  "CassandraExport" should "parse basic json" in {
    val json = "[{\"key\": \"3564653535373138666165363131653238333063346162366562633232316333\",\"columns\": [[\"approval_status\",\"approved\",1375387811075789], [\"city\",\"Morganton\",1375387811075769], [\"created_at\",\"2013-08-01T20:10:09.930989Z\",1375387811075795]]}]"

    val results = new mutable.ListBuffer[CassandraAttribute]
    CassandraExport.jsonParsing(new java.io.ByteArrayInputStream(json.getBytes()), attribute => {
      results += attribute
    })

    assertResult(3)(results.size)
    assertResult(("3564653535373138666165363131653238333063346162366562633232316333", "approval_status", "approved", 1375387811075789l, null))(results(0))
    assertResult(("3564653535373138666165363131653238333063346162366562633232316333", "city", "Morganton", 1375387811075769l, null))(results(1))
    assertResult(("3564653535373138666165363131653238333063346162366562633232316333", "created_at", "2013-08-01T20:10:09.930989Z", 1375387811075795l, null))(results(2))
  }

  it should "handle deletes in 1.0+" in {
    val json = "[{\"key\": \"simple\",\"columns\": [[\"approval_status\",\"approved\",1375387811075789], [\"city\",\"Morganton\",1375387811075769,\"d\"]]}]"

    val results = new mutable.ListBuffer[CassandraAttribute]
    CassandraExport.jsonParsing(new java.io.ByteArrayInputStream(json.getBytes()), attribute => {
      results += attribute
    })

    assertResult(2)(results.size)
    assertResult(("simple", "approval_status", "approved", 1375387811075789l, null))(results(0))
    assertResult(("simple", "city", null, 1375387811075769l, "d"))(results(1))
  }

}
