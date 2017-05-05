package com.lambda

import java.io.{InputStream, StringWriter}

import org.apache.commons.io.IOUtils

import scalaz.Scalaz._

/**
  * Created by williame on 4/30/17.
  */
class Main {

  def helloWorld(in: InputStream): String = {
    s"the input is ${toString(in)}" |> println
    toString(in)
  }

  def toString(in: InputStream): String = {
    val writer = new StringWriter()
    IOUtils.copy(in, writer, "utf-8")
    writer.toString
  }
}
