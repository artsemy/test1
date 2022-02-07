package com.anadea.task.service

import com.anadea.task.domain.{PageNote, PageNoteDto}
import com.anadea.task.service.PageNoteValidationError._

import java.time.LocalDate
import scala.util.Try

trait PageNoteValidationError extends PageNoteError

object PageNoteValidationError {

  case object PageNoteFormat extends PageNoteValidationError {
    override def message(): String = "pageNote"
  }
  case object TitleFormat extends PageNoteValidationError {
    override def message(): String = "title"
  }
  case object DescriptionFormat extends PageNoteValidationError {
    override def message(): String = "description"
  }
  case object SlugFormat extends PageNoteValidationError {
    override def message(): String = "slug"
  }
  case object MenuLabelFormat extends PageNoteValidationError {
    override def message(): String = "menuLabel"
  }
  case object HeaderFormat extends PageNoteValidationError {
    override def message(): String = "header"
  }
  case object PublisherAtFormat extends PageNoteValidationError {
    override def message(): String = "publishedAt"
  }
  case object PriorityFormat extends PageNoteValidationError {
    override def message(): String = "priority"
  }
  case object ContentTextFormat extends PageNoteValidationError {
    override def message(): String = "text"
  }
  case object IdFormat extends PageNoteValidationError {
    override def message(): String = "id"
  }

}

object PageNoteValidator {

  def validate(pageNoteDto: PageNoteDto): Either[PageNoteValidationError, (PageNote, String)] = for {
    title <- validateTitle(pageNoteDto.title)
    desc  <- validateDescription(pageNoteDto.description)
    slug  <- validateSlug(pageNoteDto.slug)
    menu  <- validateMenuLabel(pageNoteDto.menuLabel)
    head  <- validateHeader(pageNoteDto.h1)
    pub   <- validatePublishedAt(pageNoteDto.publishedAt)
    prior <- validatePriority(pageNoteDto.priority)
    text  <- validateContentText(pageNoteDto.contentText)
  } yield (PageNote(title, desc, slug, menu, head, pub, prior), text)

  def validateTitle(title: String): Either[PageNoteValidationError, String] = Right(title)

  def validateDescription(description: String): Either[PageNoteValidationError, String] = Right(description)

  def validateSlug(slug: String): Either[PageNoteValidationError, String] = Right(slug)

  def validateMenuLabel(menuLabel: String): Either[PageNoteValidationError, String] = Right(menuLabel)

  def validateHeader(h1: String): Either[PageNoteValidationError, String] = Right(h1)

  def validatePublishedAt(publishedAt: String): Either[PageNoteValidationError, LocalDate] = {
    Try(LocalDate.parse(publishedAt)).toEither.left.map(_ => PublisherAtFormat)
  }

  def validatePriority(priority: String): Either[PageNoteValidationError, Int] = {
    priority.toIntOption.toRight(PriorityFormat)
  }

  def validateContentText(contentText: String): Either[PageNoteValidationError, String] = Right(contentText)

  def validateId(id: String): Either[PageNoteValidationError, Long] = {
    id.toLongOption.toRight(IdFormat)
  }
}
