package com.anadea.task.service

trait PageNoteError extends Throwable {
  def message(): String
}
