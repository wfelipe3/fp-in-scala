package com.wf.scheduler.app

import java.util

import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.dynamodbv2.document.spec.{GetItemSpec, QuerySpec, ScanSpec, UpdateItemSpec}
import com.amazonaws.services.dynamodbv2.document.utils.{NameMap, ValueMap}
import com.amazonaws.services.dynamodbv2.document.{DynamoDB, Item}
import com.amazonaws.services.dynamodbv2.model._
import com.amazonaws.services.dynamodbv2.{AmazonDynamoDB, AmazonDynamoDBClientBuilder}
import com.wf.scheduler.lambda.LambdaApp

import scala.collection.JavaConverters._
import scala.util.Try
import scalaz.Scalaz._
import scalaz._

/**
  * Created by feliperojas on 5/7/17.
  */
object SchedulerApp extends App {

  //new LambdaApp().handle(null)
  val client: AmazonDynamoDB = AmazonDynamoDBClientBuilder
    .standard()
    //.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-west-2"))
    .withRegion("us-west-2")
    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("", "")))
    .build()

  val dynamoDB = new DynamoDB(client)

  //createTable(dynamoDB)
  //insertElements(dynamoDB)
  //getItem(dynamoDB) |> println
  //updateItem(dynamoDB)
  query(dynamoDB).asScala.map(i => s"${i.getInt("id")}, ${i.getInt("partition")}, ${i.getString("template")}").foreach(println)

  private def updateItem(dynamoDB: DynamoDB) = {
    val table = dynamoDB.getTable("mail-templates")

    val nameMap = new util.HashMap[String, String]()
    nameMap.put("#s", "state")

    val updatespec = new UpdateItemSpec()
      .withPrimaryKey("partition", 1, "id", 1)
      .withUpdateExpression("set #s = :st")
      .withNameMap(nameMap)
      .withValueMap(new ValueMap().withNumber(":st", 0))
      .withReturnValues(ReturnValue.UPDATED_NEW)

    table.updateItem(updatespec)
  }

  private def getItem(dynamo: DynamoDB) = {
    val table = dynamo.getTable("movies")
    new GetItemSpec().withPrimaryKey("year", 2000) |> table.getItem
  }

  private def query(dynamoDB: DynamoDB) = {
    val table = dynamoDB.getTable("mail-templates")
    val nameMap = new util.HashMap[String, String]()
    nameMap.put("#p", "partition")
    nameMap.put("#s", "state")

    val valueMap = new util.HashMap[String, AnyRef]()
    valueMap.put(":st", 1.asInstanceOf[AnyRef])

    val spec = new QuerySpec()
      .withKeyConditionExpression("#p = :p and id = :id and #s= :st")
      .withNameMap(nameMap)
      .withValueMap(valueMap)

    val scan = new ScanSpec().withProjectionExpression("#p, id, #s, template")
      .withFilterExpression("#s = :st")
      .withNameMap(nameMap)
      .withValueMap(valueMap)

    table.scan(scan)
  }

  private def insertElements(dynamo: DynamoDB) = {
    val table = dynamo.getTable("mail-templates")
    new Item()
      .withPrimaryKey("partition", 1, "id", 2)
      .withString("template","""<html><h1>test</h1></html>""")
      .withInt("state", 1) |> table.putItem
  }

  private def createTable(dynamo: DynamoDB) = {
    Try {
      val table = dynamo.createTable(
        "movies",
        util.Arrays.asList(new KeySchemaElement("year", KeyType.HASH)),
        util.Arrays.asList(new AttributeDefinition("year", ScalarAttributeType.N)),
        new ProvisionedThroughput(10L, 10L)
      )
      table.waitForActive()
    }.recover {
      case e: Exception => e.printStackTrace()
    }
  }
}
