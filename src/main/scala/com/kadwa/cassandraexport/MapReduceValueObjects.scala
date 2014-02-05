package com.kadwa.cassandraexport

import org.apache.hadoop.io.WritableComparable
import java.io.{DataOutput, DataInput}

/*
 * Created by Neville Kadwa.
 */
object InterimKey {
  def apply(key:String, name:String) = {
    val i = new InterimKey()
    i.key = key
    i.name = name
    i
  }
}
class InterimKey extends WritableComparable[InterimKey] {

  var key:String = null
  var name:String = null

  def readFields(p1: DataInput) = {
    if (p1.readBoolean())
      key = p1.readUTF
    if (p1.readBoolean())
      name = p1.readUTF
  }

  def write(p1: DataOutput) = {
    p1.writeBoolean(key != null)
    if (key != null)
      p1.writeUTF(key)
    p1.writeBoolean(name != null)
    if (name != null)
      p1.writeUTF(name)
  }

  override def toString: String = s"InterimKey(${key},${name})"

  def compareTo(o: InterimKey): Int = {
    //      System.err.println(s"${this} <=> ${o}")
    var i = this.key.compareTo(o.key)
    if (i != 0) return i

    this.name.compareTo(o.name)
  }
}

object InterimValue {
  def apply(value:String, timestamp:Long, extra:String) = {
    val i = new InterimValue()
    i.value = value
    i.timestamp = timestamp
    i.extra = extra
    i
  }
}
class InterimValue extends WritableComparable[InterimValue] {

  var value:String = null
  var timestamp:Long = -1
  var extra:String = null

  override def toString: String = s"InterimValue(${value},${timestamp},${extra})"

  def readFields(p1: DataInput) = {
    if (p1.readBoolean())
      value = p1.readUTF
    timestamp = p1.readLong
    if (p1.readBoolean())
      extra = p1.readUTF
  }

  def write(p1: DataOutput) = {
    p1.writeBoolean(value != null)
    if (value != null)
      p1.writeUTF(value)
    p1.writeLong(timestamp)
    p1.writeBoolean(extra != null)
    if (extra != null)
      p1.writeUTF(extra)
  }

  def compareTo(o: InterimValue): Int = {
    timestamp.compare(o.timestamp)
  }
}
