package wookie.yql.analytics

import com.javadocmd.simplelatlng.LatLng
import org.apache.spark.streaming.{Seconds, Minutes}
import org.rogach.scallop.ScallopConf
import shapeless._
import wookie.spark.SparkStreamingApp
import wookie.spark.cli._
import wookie.spark.filters.FilterStream
import wookie.spark.geo.Location
import wookie.spark.mappers.{Keyer, MapStream}
import wookie.spark.mappers.Maps.from
import wookie.spark.streaming.{JoinStreamInWindow, KafkaTypedStream, TwitterStream}
import wookie.spark.streaming.TwitterFilters.{country, language}
import wookie.spark.streaming.TwitterMaps._

case class Tweet(user: String, refUsers: List[String], refUrls: List[String], tags: List[String],
                 location: Option[Location], latLong: Option[LatLng], text: String)

trait MediaMergerConf extends Name with Duration with Twitter with Kafka

object MediaMergeUtils {
  val countryCode = "US"
  def notEmptyTweet: Tweet => Boolean = t => !t.text.isEmpty
  def extractors = user :: refUsers :: urls :: tags :: location :: latLong :: text :: HNil
  def onlyUS = country(countryCode)
  def english = language("en")
  def defaultLoc = Location("", "USA")
}

object MediaMerger extends SparkStreamingApp[MediaMergerConf](new ScallopConf(_) with MediaMergerConf) {

  import MediaMergeUtils._
  import ScallopConfsConverter._
  
  def runStreaming(opt: MediaMergerConf): Unit = {
    val pipeline = for {
      tweets <- TwitterStream(opt)
      onlyUSEnglish <- FilterStream(tweets, onlyUS, english)
      cleanTweets <- MapStream(onlyUSEnglish, from(extractors).to[Tweet])
      notEmptyCleanTweets <- FilterStream(cleanTweets, notEmptyTweet)
      weatherStream <- KafkaTypedStream[Weather](opt.brokers(), Weather.queueName, Weather.parse)
      weatherWithId <- MapStream(weatherStream, Keyer.withId((a:Weather) => a.region))
      tweetsWithId <- MapStream(notEmptyCleanTweets, Keyer.withId((a:Tweet) => a.location.getOrElse(defaultLoc).region))
      joined <- JoinStreamInWindow(tweetsWithId, weatherWithId, Seconds(100), Seconds(20))
    } yield {
      joined.saveAsTextFiles("joined")
    }
    pipeline(this)

  }
}