package com.anadea.task.dao

import cats.effect.Sync
import com.anadea.task.dao.impl.NoteDAOImpl
import com.anadea.task.domain.PageNote
import doobie.util.transactor.Transactor

import java.time.LocalDate

trait NoteDAO[F[_]] {
  def createNote(pageNote: PageNote, text: String): F[Int]
  def readNote(id:         Long): F[Option[(PageNote, String)]]
  def updateNote(id:       Long, pageNote: PageNote, text: String): F[Int]
  def deleteNote(id:       Long): F[Int]

  def readAllLabels(): F[Map[Long, String]]
  def readPublishedLabels(todayDate: LocalDate): F[Map[Long, String]]
}

object NoteDAO {
  def of[F[_]: Sync](tx: Transactor[F]): NoteDAO[F] = new NoteDAOImpl(tx)
}
