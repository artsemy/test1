package com.anadea.task.modules

import cats.effect.{ContextShift, Sync}
import com.anadea.task.router.TextRouters
import org.http4s.HttpRoutes

sealed abstract class Routers[F[_]: Sync: ContextShift](services: Services[F]) {
  val userRoutes: HttpRoutes[F] = TextRouters.routes[F](services)
  val routes = userRoutes
}

object Routers {
  def of[F[_]: Sync: ContextShift](services: Services[F]): Routers[F] = {
    new Routers[F](services) {}
  }
}
