package com.anadea.task

import cats.implicits._
import cats.effect.{ExitCode, IO, IOApp}
import com.anadea.task.domain.PageNoteDto
import org.http4s._
import org.http4s.implicits._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.client.blaze.BlazeClientBuilder
import org.http4s.client.dsl.io._

import scala.concurrent.ExecutionContext

object CmsTestClient extends IOApp {

  private val uri = uri"http://localhost:9000"
  private def printLine(string: String = ""): IO[Unit] = IO(println(string))

  override def run(args: List[String]): IO[ExitCode] = {
    val pageNoteDto = PageNoteDto("title", "description", "slug", "menuLabel", "h1", "2020-09-09", "1", "text")

    BlazeClientBuilder[IO](ExecutionContext.global).resource
      .use { client =>
        for {
          _  <- printLine("create")
          id <- client.expect[Int](Method.POST(pageNoteDto, uri / "create"))
          _  <- printLine("new id: " + id)

          _   <- printLine("read")
          dto <- client.expect[Option[PageNoteDto]](Method.GET(uri / "read" / id.toString))
          _   <- printLine("read dto: " + dto.toString)

          _ <- printLine("update")
          n <- client.expect[Int](Method.POST(pageNoteDto.copy(contentText = "updated"), uri / "update" / id.toString))
          _ <- printLine("update line number: " + n)

          _  <- printLine("delete")
          n2 <- client.expect[Int](Method.GET(uri / "delete" / id.toString))
          _  <- printLine("delete line number: " + n2)

          _  <- printLine("all labels")
          m1 <- client.expect[Map[Long, String]](Method.GET(uri / "allLabels"))
          _  <- printLine("all label map size 3 == " + m1.size)

          _  <- printLine("published labels")
          m2 <- client.expect[Map[Long, String]](Method.GET(uri / "publishedLabels"))
          _  <- printLine("published label map size 2 == " + m2.size)

          _      <- printLine("id by slug")
          idSlug <- client.expect[Option[Long]](Method.GET(uri / "slug" / "slug1"))
          _      <- printLine("id = 1 = " + idSlug.getOrElse(-1))
        } yield ()
      }
      .as(ExitCode.Success)
  }

}
