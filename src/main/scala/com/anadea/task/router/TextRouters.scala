package com.anadea.task.router

import cats.effect.Sync
import cats.implicits._
import com.anadea.task.domain.PageNoteDto
import com.anadea.task.modules.Services
import com.anadea.task.router.MarshalResponse.marshalResponse
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}
import org.http4s.dsl.Http4sDsl
import org.http4s.util.CaseInsensitiveString

object TextRouters {

  def routes[F[_]: Sync](services: Services[F]): HttpRoutes[F] = {

    val dsl = new Http4sDsl[F] {}
    import dsl._

    def create(): HttpRoutes[F] = HttpRoutes.of[F] { case req @ GET -> Root / "create" =>
      val res = for {
        pageNoteDto <- req.as[PageNoteDto]
        created     <- services.noteService.createNote(pageNoteDto)
      } yield created
      marshalResponse(res)
    }

    def read(): HttpRoutes[F] = HttpRoutes.of[F] { case GET -> Root / "read" / id =>
      val res = services.noteService.readNote(id)
      marshalResponse(res)
    }

    def update(): HttpRoutes[F] = HttpRoutes.of[F] { case req @ GET -> Root / "update" / id =>
      val res = for {
        pageNoteDto <- req.as[PageNoteDto]
        updated     <- services.noteService.updateNote(id, pageNoteDto)
      } yield updated
      marshalResponse(res)
    }

    def delete(): HttpRoutes[F] = HttpRoutes.of[F] { case GET -> Root / "delete" / id =>
      val res = services.noteService.deleteNote(id)
      marshalResponse(res)
    }

    def allLabels(): HttpRoutes[F] = HttpRoutes.of[F] { case GET -> Root / "allLabels" =>
      val res = services.noteService.readAllLabels()
      marshalResponse(res)
    }

    def publishedLabels(): HttpRoutes[F] = HttpRoutes.of[F] { case GET -> Root / "publishedLabels" =>
      val res = services.noteService.readPublishedLabels()
      marshalResponse(res)
    }

    create() <+> read() <+> update() <+> delete() <+> allLabels() <+> publishedLabels()
  }

}
