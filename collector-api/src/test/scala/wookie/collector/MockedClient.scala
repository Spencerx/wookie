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
package wookie.collector

import org.http4s.{HttpService, Response}
import org.http4s.Status._
import org.http4s.Method._
import org.http4s.client.{Client, DisposableResponse}

import scalaz.concurrent.Task

object MockedClient {

  def create(response: Response): Client = {
    val route = HttpService {
      case r if r.method == GET && r.pathInfo == "/ok" => Task.now(response)
      case r if r.method == GET && r.pathInfo == "/boom" => Task.now(Response(BadRequest))
      case r => Task.now(response)
    }
    Client(
      open = route.map(resp => DisposableResponse(resp,  Task.now(()))),
      shutdown = Task.now(())
    )
  }
}
