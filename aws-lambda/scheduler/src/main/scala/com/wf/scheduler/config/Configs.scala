package com.wf.scheduler.config

import scala.collection.JavaConverters._

/**
  * Created by feliperojas on 5/7/17.
  */
object Configs {

  def getEnvConfig: EnvConfig = {
    val env = System.getenv.asScala
    val user = env.getOrElse("user", "")
    val pass = env.getOrElse("pass", "")
    val from = env.getOrElse("from", "")
    val to = env.getOrElse("to", "")
    EnvConfig(user, pass, from, to)
  }

  case class User(userName: String) extends AnyVal

  case class Password(pass: String) extends AnyVal

  case class Auth(user: User, password: Password)

  sealed trait NotificationConfig

  case class MailNotificationConfig(host: String, port: Int, auth: Auth) extends NotificationConfig

  case class EnvConfig(user: String, password: String, from: String, to: String)

}
