package com.anadea.task.context

import cats.effect.{Async, Concurrent, ContextShift, Resource}
import com.anadea.task.conf.app.AppConf
import com.anadea.task.conf.db.{migrator, transactor}
import com.anadea.task.modules.{Repositories, Routers, Services}
import org.http4s.HttpApp
import org.http4s.implicits._

object AppContext {

  def setUp[F[_]: ContextShift: Async: Concurrent](conf: AppConf): Resource[F, HttpApp[F]] = for {
    tx <- transactor[F](conf.db)

    migrator <- Resource.eval(migrator[F](conf.db))
    _        <- Resource.eval(migrator.migrate())

    repositories = Repositories.of[F](tx)

    services = Services.of[F](repositories)

    routes = Routers.of[F](services).routes.orNotFound

  } yield routes

}
