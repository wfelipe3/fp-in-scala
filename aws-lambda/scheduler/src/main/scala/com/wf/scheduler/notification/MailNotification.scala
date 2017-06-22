package com.wf.scheduler.notification

import com.wf.scheduler.storage.EmailDynamoStorage.EmailTemplate
import org.apache.commons.mail.{DefaultAuthenticator, HtmlEmail}

import scala.collection.JavaConverters._
import scala.util.Try

/**
  * Created by feliperojas on 5/7/17.
  */
object MailNotification {

  case class MailConfig(host: String, port: Int, user: String, password: String)

  def sendEmail(a: MailConfig, b: EmailTemplate): Try[EmailTemplate] = {
    Try {
      val smtp = getSmtp(a.user, a.password, a.host, a.port)
      smtp.setFrom(b.from)
      smtp.setSubject(b.subject)
      smtp.setHtmlMsg(b.template)
      smtp.addTo(b.to)
      smtp.send()
      b
    }
  }


  private def getSmtp(user: String, pass: String, host: String, port: Int) = {
    val gmail = new HtmlEmail
    gmail.setHostName(host)
    gmail.setSmtpPort(port)
    gmail.setSSLCheckServerIdentity(true)
    gmail.setAuthenticator(new DefaultAuthenticator(user, pass))
    gmail.getMailSession.getProperties.asScala.put("mail.smtps.auth", "true")
    gmail.getMailSession.getProperties.put("mail.debug", "true")
    gmail.getMailSession.getProperties.put("mail.smtps.port", port.toString)
    gmail.getMailSession.getProperties.put("mail.smtps.socketFactory.port", port.toString)
    gmail.getMailSession.getProperties.put("mail.smtps.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
    gmail.getMailSession.getProperties.put("mail.smtps.socketFactory.fallback", "false")
    gmail.getMailSession.getProperties.put("mail.smtp.starttls.enable", "true")
    gmail
  }
}

