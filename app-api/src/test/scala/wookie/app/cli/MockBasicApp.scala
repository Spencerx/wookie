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
package wookie.app.cli

import org.rogach.scallop.ScallopConf

trait AllConf extends Input with Topics with URLQuery

object MockBasicApp extends BasicApp[AllConf](a => new ScallopConf(a) with AllConf) {
  var inputURL: String = null
  var query: Map[String, String] = null
  var topics: List[String] = null

  override def run(opt: AllConf): Unit = {
    inputURL = opt.inputURL()
    query = opt.query()
    topics = opt.topics()
  }
}
