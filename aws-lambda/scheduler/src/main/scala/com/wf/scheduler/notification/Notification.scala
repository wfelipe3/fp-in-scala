package com.wf.scheduler.notification

import scala.util.Try

/**
  * Created by feliperojas on 5/7/17.
  */
object Notification {

  trait Notifier[A, B] {
    def notify(a: A, b: B): Try[B]
  }

  trait NotifierConfig[A] {
    def getConfig(): Option[A]
  }

  def notify[A: NotifierConfig, B: Notifier](config: A, notification: B): Try[B] = {
    val config = implicitly[NotifierConfig[A]]
    val notifier = implicitly[Notifier[A, B]]
    config.getConfig()
      .map(c => notifier.notify(c, notification))
      .getOrElse(util.Failure(new Exception("no config found")))
  }

}
