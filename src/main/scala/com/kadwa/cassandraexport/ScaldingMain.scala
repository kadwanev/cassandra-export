package com.kadwa.cassandraexport

import com.twitter.scalding.{Tsv, Job, Args, TextLine}

/*
 * Created by Neville Kadwa.
 */
class ScaldingMain(args : Args) extends Job(args) {

  def createState = new {
    var lastLine: String = null
    def release() {}
  }

//  def data(f : Int => Unit) = for(i <- 1 to 10) {
//    println("Generating " + i)
//    f(i)
//  }
//  def toTraversable[T]( func : (T => Unit) => Unit) = new Traversable[T] {
//    def foreach[X]( f : T => X) = func(f(_) : Unit)
//  }
//  toTraversable(data).view.take(3).sum


  Tsv( args("input"), ('filename) ).pipe
//    .using(createState)
    .flatMapTo('filename -> ('key, 'name, 'value, 'timestamp, 'extra)) { filename:String => {
      new Iterable[CassandraAttribute] {
        def iterator: Iterator[CassandraAttribute] = new Iterator[CassandraAttribute] {
          val exporter = new CassandraExport("src/test/resources/scottdata")

          var numAttributes = 0
          exporter.exportSSTable("place_directory_development/Places/place_directory_development-Places-ib-17-Data.db", attribute => {
            numAttributes += 1
            //      println(attribute)
          })

          exporter.close

          def hasNext: Boolean = ???

          def next(): _root_.com.kadwa.cassandraexport.CassandraAttribute = ???
        }
      }
    }}
//    .filter('line) { line:String => { !ignore_lines.contains(line) }}
//    .mapTo('line -> 'key) { line : String => SiteJsonMapper.map(line) }
//    .filter('key) { json:String => { !json.equals("E") } }
//    .groupBy('key) { keyGroup => keyGroup.size }
    .write( Tsv( args("output") ) )

}
