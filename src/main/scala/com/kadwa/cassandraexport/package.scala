package com.kadwa

/*
 * Created by Neville Kadwa.
 */
package object cassandraexport {
  type CassandraAttribute = Tuple5[String,String,String,Long,String] // key, name, value, timestamp, extra
}
