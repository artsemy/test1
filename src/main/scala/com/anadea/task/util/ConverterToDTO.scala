package com.anadea.task.util

import com.anadea.task.domain.{PageNote, PageNoteDto}

object ConverterToDTO {

  def convertPageNote(pageNote: PageNote, text: String): PageNoteDto = {
    PageNoteDto(
      pageNote.title,
      pageNote.description,
      pageNote.slug,
      pageNote.menuLabel,
      pageNote.h1,
      pageNote.publishedAt.toString,
      pageNote.priority.toString,
      text
    )
  }

}
