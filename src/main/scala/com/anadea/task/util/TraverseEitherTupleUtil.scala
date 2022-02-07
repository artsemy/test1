package com.anadea.task.util

import com.anadea.task.service.PageNoteError

object TraverseEitherTupleUtil {

  def traverseTwoTypes[K <: PageNoteError, T1, T2](
    e1: Either[K, T1],
    e2: Either[K, T2]
  ): Either[K, (T1, T2)] = {
    (e1, e2) match {
      case (Right(v1), Right(v2)) => Right(v1, v2)
      case (Left(v1), _)          => Left(v1)
      case (_, Left(v2))          => Left(v2)
    }
  }

  def traverseThreeTypes[K <: PageNoteError, T1, T2, T3](
    e1: Either[K, T1],
    e2: Either[K, T2],
    e3: Either[K, T3]
  ): Either[K, (T1, T2, T3)] = {
    (e1, e2, e3) match {
      case (Right(v1), Right(v2), Right(v3)) => Right(v1, v2, v3)
      case (Left(v1), _, _)                  => Left(v1)
      case (_, Left(v2), _)                  => Left(v2)
      case (_, _, Left(v3))                  => Left(v3)
    }
  }

}
