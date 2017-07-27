/*
 * Copyright (C) 2014-2015 by Nokia.
 * See the LICENCE.txt file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package wookie.spark.cli

import org.apache.spark.SparkContext
import org.apache.spark.sql.{SQLImplicits, SparkSession}
import org.junit.runner.RunWith
import org.rogach.scallop.ScallopConf
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import wookie.cli.Name

@RunWith(classOf[JUnitRunner])
class SparkAppSpec extends Specification {

  "Should init spark context before running" in {
    var localSc: SparkContext = null
    var localSQL: SparkSession = null
    var appName: String = null
    val app = new SparkApp(new ScallopConf(_) with Name) {
      override def run(opt: ScallopConf with Name, spark: SparkSession): Unit = {
        localSc = spark.sparkContext
        localSQL = spark
        appName = opt.name()
      }
    }
    System.setProperty("spark.master", "local")
    app.main(Array("--name", "xxx"))
    localSc.stop()
    Option(localSc) must beSome
    Option(localSQL) must beSome
    appName must_== "xxx"
  }
}
