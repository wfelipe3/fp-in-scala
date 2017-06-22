package com.wf.scheduler.config

import com.wf.scheduler.notification.MailNotification.MailConfig
import com.wf.scheduler.storage.EmailDynamoStorage.DynamoConfig

/**
  * Created by feliperojas on 5/7/17.
  */
object Configs {

  def notificationConfig(props: Map[String, String]): Option[MailConfig] =
    for {
      host <- props.get("smtp_host")
      port <- props.get("smtp_port")
      user <- props.get("smtp_user")
      pass <- props.get("smtp_pass")
    } yield MailConfig(host, port.toInt, user, pass)

  def dynamoConfig(props: Map[String, String]): Option[DynamoConfig] =
    for {
      region <- props.get("dynamo_region")
      user <- props.get("dynamo_user")
      pass <- props.get("dynamo_pass")
    } yield DynamoConfig(user, pass, region)

}
