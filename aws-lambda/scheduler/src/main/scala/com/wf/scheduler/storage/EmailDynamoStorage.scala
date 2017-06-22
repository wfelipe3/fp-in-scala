package com.wf.scheduler.storage

import java.util

import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.services.dynamodbv2.document.DynamoDB
import com.amazonaws.services.dynamodbv2.document.spec.{ScanSpec, UpdateItemSpec}
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap
import com.amazonaws.services.dynamodbv2.model.ReturnValue
import com.amazonaws.services.dynamodbv2.{AmazonDynamoDB, AmazonDynamoDBClientBuilder}

import scala.collection.JavaConverters._
import scala.util.Try

/**
  * Created by williame on 5/8/17.
  */
object EmailDynamoStorage {

  private val TABLE = "mail-templates"

  case class EmailTemplate(id: Int, to: String, from: String, template: String, subject: String)

  case class DynamoConfig(user: String, password: String, region: String)

  def getActiveTemplates(config: DynamoConfig): Try[Seq[EmailTemplate]] = Try {
    val dynamoDB = dynamo(config)
    val table = dynamoDB.getTable(TABLE)
    val nameMap = new util.HashMap[String, String]()
    nameMap.put("#p", "partition")
    nameMap.put("#s", "state")
    nameMap.put("#t", "to")
    nameMap.put("#f", "from")

    val valueMap = new util.HashMap[String, AnyRef]()
    valueMap.put(":st", 1.asInstanceOf[AnyRef])

    val scan = new ScanSpec().withProjectionExpression("#p, id, #t, #f, template, subject")
      .withFilterExpression("#s = :st")
      .withNameMap(nameMap)
      .withValueMap(valueMap)

    table.scan(scan).iterator.asScala
      .map { i =>
        EmailTemplate(
          id = i.getInt("id"),
          to = i.getString("to"),
          from = i.getString("from"),
          template = i.getString("template"),
          subject = i.getString("subject"))
      }
      .toList
  }

  def markTemplateAsInactive(config: DynamoConfig, template: EmailTemplate): Try[Unit] = Try {
    val table = dynamo(config).getTable(TABLE)

    val nameMap = new util.HashMap[String, String]()
    nameMap.put("#s", "state")

    val updateSpec = new UpdateItemSpec()
      .withPrimaryKey("partition", 1, "id", template.id)
      .withUpdateExpression("set #s = :st")
      .withNameMap(nameMap)
      .withValueMap(new ValueMap().withNumber(":st", 0))
      .withReturnValues(ReturnValue.UPDATED_NEW)

    table.updateItem(updateSpec)
  }

  private def dynamo(config: DynamoConfig) = {
    val client: AmazonDynamoDB = AmazonDynamoDBClientBuilder
      .standard()
      .withRegion(config.region)
      .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(config.user, config.password)))
      .build()

    new DynamoDB(client)
  }
}
