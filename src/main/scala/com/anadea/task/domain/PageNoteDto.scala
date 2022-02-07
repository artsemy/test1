package com.anadea.task.domain

import io.circe.generic.JsonCodec

@JsonCodec
case class PageNoteDto(
  title:       String,
  description: String,
  slug:        String,
  menuLabel:   String,
  h1:          String,
  publishedAt: String,
  priority:    String,
  contentText: String
)
