package com.anadea.task.service

import cats.effect.Sync
import com.anadea.task.dao.NoteDAO
import com.anadea.task.domain.{PageNote, PageNoteDto}
import com.anadea.task.service.impl.NoteServiceImpl

trait NoteService[F[_]] {
  def createNote(pageNoteDto: PageNoteDto): F[Either[PageNoteError, Int]]
  def readNote(id:            String): F[Either[PageNoteError, Option[PageNoteDto]]]
  def updateNote(id:          String, pageNoteDto: PageNoteDto): F[Either[PageNoteError, Int]]
  def deleteNote(id:          String):      F[Either[PageNoteError, Int]]

  def readAllLabels():       F[Either[PageNoteError, Map[Long, String]]]
  def readPublishedLabels(): F[Either[PageNoteError, Map[Long, String]]]

  def readIdBySlug(slug: String): F[Either[PageNoteError, Option[Long]]]
}

object NoteService {
  def of[F[_]: Sync](nodeDAO: NoteDAO[F]): NoteService[F] = new NoteServiceImpl(nodeDAO)
}
