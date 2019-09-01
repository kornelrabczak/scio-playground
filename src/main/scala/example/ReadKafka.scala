package example

import org.apache.beam.sdk.io.kafka.KafkaIO
import com.spotify.scio._
import com.google.common.collect.ImmutableMap
import org.apache.kafka.common.serialization.StringDeserializer
import com.spotify.scio.values.SCollection
import org.slf4j.LoggerFactory
import scala.concurrent.duration._

object ReadKafka {

    private val logger = LoggerFactory.getLogger(this.getClass)

    def main(cmdlineArgs: Array[String]): Unit = {
        val (sc, args) = ContextAndArgs(cmdlineArgs)

        getKafka(sc, args.getOrElse("kafkaServers", ""), args.getOrElse("topic", "")).transform("counter") {
            // Split input lines, filter out empty tokens and expand into a collection of tokens
            _
            .flatMap(_.split("[^a-zA-Z']+")
            .filter(_.nonEmpty))
            // Count occurrences of each unique `String` to get `(String, Long)`
            .countByValue
          }
          // Map `(String, Long)` tuples into strings
          .map { t =>
                logger.info(t.toString())
                t._1 + ": " + t._2
          }
          // Save result as text files under the output path
          .saveAsTextFile(path = args("output"), numShards = 1)

        sc.run().waitUntilFinish(10.second, true) 
        ()
    }

    private def getKafka(sc: ScioContext, servers: String, topic: String): SCollection[String] = {
        val messages =  sc.customInput("ReadFromKafka", KafkaIO.read()
            .withBootstrapServers(servers)
            .withTopic(topic)
            .withConsumerConfigUpdates(ImmutableMap.of("auto.offset.reset", "earliest".asInstanceOf[Object]))
            .withKeyDeserializer(classOf[StringDeserializer])
            .withValueDeserializer(classOf[StringDeserializer])
            .withMaxNumRecords(5)
            .withoutMetadata())

        messages.map(_.getValue).withName("keys filtered out")
    }
}