package com.anadea.task.dao.impl

import cats.Functor
import cats.implicits._
import cats.effect.Bracket
import com.anadea.task.dao.{NoteDAO, TextFileIO}
import com.anadea.task.domain.PageNote
import doobie.util.transactor.Transactor
import doobie.implicits._
import doobie.implicits.javasql._
import doobie.implicits.javatime._
import doobie.postgres._
import doobie.postgres.implicits._
import doobie.postgres.pgisimplicits._
import doobie.util.fragment.Fragment

import java.time.LocalDate

class NoteDAOImpl[F[_]: Functor: Bracket[*[_], Throwable]](tx: Transactor[F]) extends NoteDAO[F] {

  override def createNote(pageNote: PageNote, text: String): F[Int] = {
    val fr = fr"insert into " ++
      fr"notes(title, description, slug, menu_label, h1, published_at, priority)" ++
      fr"values" ++
      fr"(${pageNote.title}, ${pageNote.description}, ${pageNote.slug}, ${pageNote.menuLabel}, " ++
      fr"${pageNote.h1}, ${pageNote.publishedAt}, ${pageNote.priority})"
    for {
      id <- fr.update.withUniqueGeneratedKeys[Int]("id").transact(tx)
      _   = TextFileIO.write(id, text) //check id value
    } yield id
  }

  override def readNote(id: Long): F[Option[(PageNote, String)]] = {
    val fr = fr"select * from notes where id = $id"
    val note = fr
      .query[(Long, PageNote)]
      .map { case (_, pageNote) =>
        pageNote
      }
      .option
      .transact(tx)
    note.map {
      case None => None
      case Some(pageNote) =>
        val text = TextFileIO.read(id)
        Some((pageNote, text))
    }
  }

  override def updateNote(id: Long, pageNote: PageNote, text: String): F[Int] = {
    val fr = fr"update notes set " ++
      fr"title = ${pageNote.title}, " ++
      fr"description = ${pageNote.description}, " ++
      fr"slug = ${pageNote.slug}, " ++
      fr"menu_label = ${pageNote.menuLabel}, " ++
      fr"h1 = ${pageNote.h1}, " ++
      fr"published_at = ${pageNote.publishedAt}, " ++
      fr"priority = ${pageNote.priority}" ++
      fr"where id = $id"
    TextFileIO.write(id, text)
    fr.update.run.transact(tx)
  }

  override def deleteNote(id: Long): F[Int] = {
    val fr = fr"delete from notes where id = $id"
    TextFileIO.delete(id)
    fr.update.run.transact(tx)
  }

  override def readAllLabels(): F[Map[Long, String]] = {
    val fr = fr"select id, menu_label from notes"
    fr.query[(Long, String)].toMap.transact(tx)
  }

  override def readPublishedLabels(todayDate: LocalDate): F[Map[Long, String]] = {
    val fr = fr"select id, menu_label from notes where published_at < $todayDate"
    fr.query[(Long, String)].toMap.transact(tx)
  }
}
