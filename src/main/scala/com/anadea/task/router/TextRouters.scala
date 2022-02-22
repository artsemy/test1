package com.anadea.task.router

import cats.effect.{Blocker, ContextShift, Sync}
import cats.implicits._
import com.anadea.task.domain.PageNoteDto
import com.anadea.task.modules.Services
import com.anadea.task.router.MarshalResponse.marshalResponse
import org.http4s.{HttpRoutes, MediaType, StaticFile}
import org.http4s.dsl.Http4sDsl
import org.http4s.circe.CirceEntityCodec.{circeEntityDecoder, circeEntityEncoder}

import java.io.File
import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

object TextRouters {

  def routes[F[_]: Sync: ContextShift](services: Services[F]): HttpRoutes[F] = {

    val dsl = new Http4sDsl[F] {}
    import dsl._

    def create(): HttpRoutes[F] = HttpRoutes.of[F] { case req @ POST -> Root / "create" =>
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

    def update(): HttpRoutes[F] = HttpRoutes.of[F] { case req @ POST -> Root / "update" / id =>
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

    val blockingEc = ExecutionContext.fromExecutorService(Executors.newFixedThreadPool(8))

    def openMenu(): HttpRoutes[F] = HttpRoutes.of[F] { case req @ GET -> Root =>
      StaticFile
        .fromFile[F](
          new File("src/main/resources/templates/menu.html"),
          Blocker.liftExecutionContext(blockingEc),
          Some(req)
        )
        .getOrElseF(NotFound())
    }

    def openAdd(): HttpRoutes[F] = HttpRoutes.of[F] { case req @ GET -> Root / "add" =>
      StaticFile
        .fromFile[F](
          new File("src/main/resources/templates/add.html"),
          Blocker.liftExecutionContext(blockingEc),
          Some(req)
        )
        .getOrElseF(NotFound())
    }

    def openContent(): HttpRoutes[F] = HttpRoutes.of[F] { case req @ GET -> Root / "content" =>
      StaticFile
        .fromFile[F](
          new File("src/main/resources/templates/content.html"),
          Blocker.liftExecutionContext(blockingEc),
          Some(req)
        )
        .getOrElseF(NotFound())
    }

    create() <+> read() <+> update() <+> delete() <+> allLabels() <+> publishedLabels() <+> openMenu() <+>
      openAdd() <+> openContent()
  }

}
