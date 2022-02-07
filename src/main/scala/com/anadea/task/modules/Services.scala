package com.anadea.task.modules

import cats.effect.Sync
import com.anadea.task.service.NoteService

sealed abstract class Services[F[_]](
  val noteService: NoteService[F]
)

object Services {
  def of[F[_]: Sync](
    repositories: Repositories[F]
  ): Services[F] = {
    new Services[F](
      NoteService.of[F](repositories.NoteDAO)
    ) {}
  }
}
