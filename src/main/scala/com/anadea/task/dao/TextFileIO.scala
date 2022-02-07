package com.anadea.task.dao

import java.io.{BufferedWriter, File, FileWriter}
import scala.io.Source
import com.anadea.task.constant.ConstantStrings

//make Resources
object TextFileIO {

  def write(id: Long, text: String): Unit = {
    val file = new File(ConstantStrings.filePath + id + ".txt")
    val bw   = new BufferedWriter(new FileWriter(file))
    bw.write(text)
    bw.close()
  }

  def read(id: Long): String = {
    val source = Source.fromFile(ConstantStrings.filePath + id + ".txt")
    val res    = source.getLines().reduce(_ + "\n" + _)
    source.close()
    res
  }

  def delete(id: Long): Unit = {
    val file = new File(ConstantStrings.filePath + id + ".txt")
    file.delete()
    ()
  }

}
