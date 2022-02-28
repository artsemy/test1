package com.anadea.task.service.impl

import cats.effect.{IO, Sync}
import com.anadea.task.dao.NoteDAO
import com.anadea.task.domain.{PageNote, PageNoteDto}
import com.anadea.task.service.PageNoteError
import com.anadea.task.service.PageNoteValidationError._
import org.scalamock.scalatest.MockFactory
import org.scalatest.freespec.AnyFreeSpec
import org.typelevel.log4cats.slf4j.Slf4jLogger

import java.time.LocalDate

class NoteServiceImplTest extends AnyFreeSpec with MockFactory {

  implicit def unsafeLogger[F[_]: Sync] = Slf4jLogger.getLogger[F]

  "Methods tests" - {

    val testValidPageNoteDto   = PageNoteDto("title", "description", "slug", "menuLabel", "h1", "2020-09-09", "1", "text")
    val testInvalidPageNoteDto = PageNoteDto("title", "description", "slug", "menuLabel", "h1", "bad", "bad", "text")
    val testValidPageNote =
      PageNote("title", "description", "slug", "menuLabel", "h1", LocalDate.parse("2020-09-09"), 1)
    val testValidText = "text"

    "createNote" - {

      "create valid" in {
        val noteDAO     = mock[NoteDAO[IO]]
        val noteService = new NoteServiceImpl[IO](noteDAO)

        val validDto = testValidPageNoteDto
        val expected = Right(5)

        (noteDAO.createNote _).expects(*, *).returning(IO(5)).once()

        val actual = noteService.createNote(validDto).unsafeRunSync()

        assert(actual == expected)
      }

      "create invalid" in {
        val noteDAO     = mock[NoteDAO[IO]]
        val noteService = new NoteServiceImpl[IO](noteDAO)

        val invalidDto = testInvalidPageNoteDto.copy(publishedAt = "2020-12-12")
        val expected   = Left(PriorityFormat)

        (noteDAO.createNote _).expects(*, *).returning(IO(5)).never()

        val actual = noteService.createNote(invalidDto).unsafeRunSync()

        assert(actual == expected)
      }
    }

    "readNote" - {

      "read valid" in {
        val noteDAO     = mock[NoteDAO[IO]]
        val noteService = new NoteServiceImpl[IO](noteDAO)

        val validId  = "1"
        val expected = Right(Some(testValidPageNoteDto))

        (noteDAO.readNote _).expects(*).returning(IO(Some(testValidPageNote, testValidText))).once()

        val actual = noteService.readNote(validId).unsafeRunSync()

        assert(actual == expected)
      }

      "read invalid" in {
        val noteDAO     = mock[NoteDAO[IO]]
        val noteService = new NoteServiceImpl[IO](noteDAO)

        val invalidId = "a"
        val expected  = Left(IdFormat)

        (noteDAO.readNote _).expects(*).returning(IO(Some(testValidPageNote, testValidText))).never()

        val actual = noteService.readNote(invalidId).unsafeRunSync()

        assert(actual == expected)
      }
    }

    "updateNote" - {

      "update valid" in {
        val noteDAO     = mock[NoteDAO[IO]]
        val noteService = new NoteServiceImpl[IO](noteDAO)

        val validId  = "1"
        val validDto = testValidPageNoteDto
        val expected = Right(1)

        (noteDAO.updateNote _).expects(*, *, *).returning(IO(1)).once()

        val actual = noteService.updateNote(validId, validDto).unsafeRunSync()

        assert(actual == expected)
      }

      "update invalid" in {
        val noteDAO     = mock[NoteDAO[IO]]
        val noteService = new NoteServiceImpl[IO](noteDAO)

        val validId    = "1"
        val invalidDto = testInvalidPageNoteDto.copy(priority = "1")
        val expected   = Left(PublisherAtFormat)

        (noteDAO.updateNote _).expects(*, *, *).returning(IO(1)).never()

        val actual = noteService.updateNote(validId, invalidDto).unsafeRunSync()

        assert(actual == expected)
      }

    }

    "deleteNote" - {

      "delete valid" in {
        val noteDAO     = mock[NoteDAO[IO]]
        val noteService = new NoteServiceImpl[IO](noteDAO)

        val validId  = "1"
        val expected = Right(1)

        (noteDAO.deleteNote _).expects(*).returning(IO(1)).once()

        val actual = noteService.deleteNote(validId).unsafeRunSync()

        assert(actual == expected)
      }

      "delete invalid" in {
        val noteDAO     = mock[NoteDAO[IO]]
        val noteService = new NoteServiceImpl[IO](noteDAO)

        val invalidId = "a"
        val expected  = Left(IdFormat)

        (noteDAO.deleteNote _).expects(*).returning(IO(1)).never()

        val actual = noteService.deleteNote(invalidId).unsafeRunSync()

        assert(actual == expected)
      }

    }

    "readAllLabels" in {
      val noteDAO     = mock[NoteDAO[IO]]
      val noteService = new NoteServiceImpl[IO](noteDAO)

      val expected: Either[PageNoteError, Map[Long, String]] = Right(Map(1L -> "label1", 2L -> "label2"))

      (noteDAO.readAllLabels _).expects().returning(IO(Map(1L -> "label1", 2L -> "label2"))).once()

      val actual = noteService.readAllLabels().unsafeRunSync()

      assert(actual == expected)
    }

    "readPublishedLabels" in {
      val noteDAO     = mock[NoteDAO[IO]]
      val noteService = new NoteServiceImpl[IO](noteDAO)

      val expected: Either[PageNoteError, Map[Long, String]] = Right(Map(1L -> "label1", 2L -> "label2"))

      (noteDAO.readPublishedLabels _).expects(*).returning(IO(Map(1L -> "label1", 2L -> "label2"))).once()

      val actual = noteService.readPublishedLabels().unsafeRunSync()

      assert(actual == expected)
    }

    "getIdBySlug" in {
      val noteDAO     = mock[NoteDAO[IO]]
      val noteService = new NoteServiceImpl[IO](noteDAO)

      val validSlug = "slug"
      val expected: Either[PageNoteError, Option[Long]] = Right(Some(1))

      (noteDAO.readIdBySlug _).expects(*).returning(IO(Some(1))).once()

      val actual = noteService.readIdBySlug(validSlug).unsafeRunSync()

      assert(actual == expected)
    }

  }

}
