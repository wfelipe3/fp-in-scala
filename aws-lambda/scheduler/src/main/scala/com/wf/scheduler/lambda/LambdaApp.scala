package com.wf.scheduler.lambda

import java.io.{InputStream, StringWriter}

import com.wf.scheduler.config.Configs
import com.wf.scheduler.notification.MailNotification
import com.wf.scheduler.storage.EmailDynamoStorage
import com.wf.scheduler.storage.EmailDynamoStorage.EmailTemplate
import org.apache.commons.io.IOUtils

import scala.collection.JavaConverters._
import scala.util.Try

/**
  * Created by feliperojas on 5/7/17.
  */
class LambdaApp {

  def handle(in: InputStream) = {
    val env = Map[String, String](System.getenv.asScala.toList: _*)

    val configs = for {
      notiConfig <- Configs.notificationConfig(env)
      dynamoConfig <- Configs.dynamoConfig(env)
    } yield (notiConfig, dynamoConfig)

    configs
      .map { c =>
        val (notiConfig, dynamoConfig) = c
        for {
          templates <- EmailDynamoStorage.getActiveTemplates(dynamoConfig)
          head <- templates.headOption.fold[Try[EmailTemplate]](util.Failure(new Exception("No more templates")))(util.Success(_))
          email <- MailNotification.sendEmail(notiConfig, head)
          _ <- EmailDynamoStorage.markTemplateAsInactive(dynamoConfig, email)
        } yield ()
      }
      .getOrElse(util.Failure(new Exception("no config found")))
      .get

  }

  def toString(in: InputStream): String = {
    val writer = new StringWriter()
    IOUtils.copy(in, writer, "utf-8")
    writer.toString
  }
}
