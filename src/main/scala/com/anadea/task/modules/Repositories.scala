package com.anadea.task.modules

import cats.effect.Sync
import com.anadea.task.dao.NoteDAO
import doobie.Transactor

sealed abstract class Repositories[F[_]](val NoteDAO: NoteDAO[F])

object Repositories {
  def of[F[_]: Sync](tx: Transactor[F]): Repositories[F] = {
    new Repositories[F](NoteDAO.of[F](tx)) {}
  }
}
