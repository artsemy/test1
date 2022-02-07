package com.anadea.task.domain

import java.time.LocalDate

case class PageNote(
  title:       String,
  description: String,
  slug:        String,
  menuLabel:   String,
  h1:          String,
  publishedAt: LocalDate,
  priority:    Int
)
