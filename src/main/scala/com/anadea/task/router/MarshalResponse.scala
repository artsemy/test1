package com.anadea.task.router

import cats.effect.Sync
import cats.implicits._
import com.anadea.task.service.PageNoteError
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.dsl.Http4sDsl
import org.http4s.{EntityEncoder, Response}

object MarshalResponse {

  def marshalResponse[F[_]: Sync, T, K <: PageNoteError](
    result: F[Either[K, T]]
  )(
    implicit E: EntityEncoder[F, T]
  ): F[Response[F]] = {
    val dsl = new Http4sDsl[F] {}
    import dsl._

    def UserErrorToHttpResponse(error: K): F[Response[F]] =
      error match {
        case e => Ok("error: " + e.message()) //BadRequest, Ok for testing only
      }

    result
      .flatMap {
        case Left(error) => UserErrorToHttpResponse(error)
        case Right(dto)  => Ok(dto)
      }
      .handleErrorWith { ex =>
        InternalServerError(ex.getMessage) //not checked by tests
      }
  }

}
