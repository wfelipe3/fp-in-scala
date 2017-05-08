package com.wf.scheduler.lambda

import java.io.{InputStream, StringWriter}

import com.wf.scheduler.config.Configs
import com.wf.scheduler.notification.MailNotification
import com.wf.scheduler.notification.MailNotification._
import org.apache.commons.io.IOUtils

import scalaz.Scalaz._
import scalaz._

/**
  * Created by feliperojas on 5/7/17.
  */
class LambdaApp {

  def handle(in: InputStream) = {

    val env = Configs.getEnvConfig

    MailNotification.sendEmail(
      emailAuth = Auth(User(env.user), Password(env.password)),
      email = Email(
        from = Account(env.from, "Felipe Rojas"),
        to = Account(env.to, "william f"),
        subject = "test",
        message = """<html><h1>test message</h1></html>"""
      )
    ).recover {
      case e: Exception => e.printStackTrace()
    }
  }

  def toString(in: InputStream): String = {
    val writer = new StringWriter()
    IOUtils.copy(in, writer, "utf-8")
    writer.toString
  }
}
