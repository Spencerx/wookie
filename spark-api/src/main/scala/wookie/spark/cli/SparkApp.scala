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

import org.apache.spark.sql.SparkSession
import org.apache.spark.{SparkConf, SparkContext}

/**
  *
  * @param options function that will create parsed arguments of type A
  * @tparam A type of cmd line arguments, at least name of application needs to be passed
  */
abstract class SparkApp[A <: Name](options: Array[String] => A) {

  protected var _sc: SparkContext = _
  protected var _conf: SparkConf = _
  protected var _ss: SparkSession = _
  protected var _opt: A = _

  def sc: SparkContext = _sc
  def conf: SparkConf = _conf
  def session: SparkSession = _ss
  def opt: A = _opt

  def run(opt: A): Unit

  def configure(conf: SparkConf, sessionBuilder: SparkSession.Builder): SparkSession.Builder = sessionBuilder

  final def main(args: Array[String]): Unit = {
    _opt = options(args)
    _opt.afterInit()
    _opt.assertVerified()

    _conf = new SparkConf().setAppName(opt.name())

    _ss = configure(_conf, SparkSession.builder().config(_conf)).getOrCreate()
    _sc = _ss.sparkContext

    run(opt)
  }
}
