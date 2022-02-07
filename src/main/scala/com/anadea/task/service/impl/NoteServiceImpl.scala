package com.anadea.task.service.impl

import cats.implicits._
import cats.Monad
import cats.effect.Sync
import com.anadea.task.dao.NoteDAO
import com.anadea.task.domain.{PageNote, PageNoteDto}
import com.anadea.task.service.{NoteService, PageNoteError, PageNoteValidator}
import com.anadea.task.util.TraverseEitherTupleUtil._
import com.anadea.task.util.ConverterToDTO._
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

import java.time.LocalDate

class NoteServiceImpl[F[_]: Monad: Sync](noteDAO: NoteDAO[F]) extends NoteService[F] {

  implicit def unsafeLogger[F[_]: Sync] = Slf4jLogger.getLogger[F]

  override def createNote(pageNoteDto: PageNoteDto): F[Either[PageNoteError, Int]] = for {
    _ <- Logger[F].info("create note: try")
    res <- PageNoteValidator.validate(pageNoteDto).traverse { case (pageNote, text) =>
      noteDAO.createNote(pageNote, text)
    }
    _ <- Logger[F].info("create note: finish")
  } yield res

  override def readNote(id: String): F[Either[PageNoteError, Option[PageNoteDto]]] = for {
    _ <- Logger[F].info("read note: try")
    res <- PageNoteValidator
      .validateId(id)
      .traverse(noteDAO.readNote)
      .map(x => x.map(y => y.map { case (pageNote, text) => convertPageNote(pageNote, text) }))
    _ <- Logger[F].info("read note: finish")
  } yield res

  override def updateNote(id: String, pageNoteDto: PageNoteDto): F[Either[PageNoteError, Int]] = for {
    _ <- Logger[F].info("update note: try")
    res <- traverseTwoTypes(PageNoteValidator.validateId(id), PageNoteValidator.validate(pageNoteDto))
      .traverse { case (ind, (pageNote, text)) => noteDAO.updateNote(ind, pageNote, text) }
    _ <- Logger[F].info("update note: finish")
  } yield res

  override def deleteNote(id: String): F[Either[PageNoteError, Int]] = for {
    _   <- Logger[F].info("delete note: try")
    res <- PageNoteValidator.validateId(id).traverse(noteDAO.deleteNote)
    _   <- Logger[F].info("delete note: finish")
  } yield res

  override def readAllLabels(): F[Either[PageNoteError, Map[Long, String]]] = for {
    _      <- Logger[F].info("all labels: try")
    resMap <- noteDAO.readAllLabels()
    res: Either[PageNoteError, Map[Long, String]] = resMap.asRight
    _ <- Logger[F].info("all labels: finish")
  } yield res

  override def readPublishedLabels(): F[Either[PageNoteError, Map[Long, String]]] = for {
    _      <- Logger[F].info("published labels: try")
    resMap <- noteDAO.readPublishedLabels(LocalDate.now())
    res: Either[PageNoteError, Map[Long, String]] = resMap.asRight
    _ <- Logger[F].info("published labels: finish")
  } yield res
}
