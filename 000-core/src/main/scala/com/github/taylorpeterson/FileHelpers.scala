package com.github.taylorpeterson

import java.io.FileNotFoundException

object FileHelpers {
  def processFile[T](fileProcessor: Iterator[String] => T, file: String): Option[T] = {
    val path = if (file.startsWith("/")) file else (os.pwd / os.RelPath(file)).toString()
    try {
      val bufferedSource = io.Source.fromFile(path)
      val counts = fileProcessor(bufferedSource.getLines())
      bufferedSource.close()
      Some(counts)
    } catch {
      case _: FileNotFoundException => Console.err.println(s"$file: No such file."); sys.exit(1)
    }
  }
}
