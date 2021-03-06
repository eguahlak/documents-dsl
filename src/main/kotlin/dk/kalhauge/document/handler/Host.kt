package dk.kalhauge.document.handler

import dk.kalhauge.document.dsl.Configuration
import dk.kalhauge.document.dsl.Configuration.OutputLevel.*
import dk.kalhauge.util.copyTo
import dk.kalhauge.util.of
import dk.kalhauge.util.stackOf
import java.io.File
import java.io.IOException
import java.io.PrintWriter
import java.net.URL

interface LineSource {
  fun readLines(filename: String): List<String>
  }

interface Host : LineSource {
  val configuration: Configuration
  var indent: Int
  fun print(text: String?)
  fun printLine(text: String?, emptyLineCount: Int = 1)
  fun open(filename: String)
  fun close()
  fun printLine() { printLine("", 0) }
  fun printLine(prefix: String, text: String?, emptyLineCount: Int = 1) {
    if (text != null) printLine("$prefix $text", emptyLineCount)
    }
  fun updateFile(sourcePath: String, targetPath: String)
  fun downloadFile(url: String, targetPath: String)
  }

class ConsoleHost(override val configuration: Configuration) : Host {
  private val filenames = stackOf<String>()

  override var indent: Int = 0
    set(value) {
      field = value
      if (field < 0) throw IllegalArgumentException("Indent can't be negative")
      }

  override fun readLines(filename: String): List<String> {
    return emptyList()
    }

  override fun print(text: String?) {
    if (text == null) return
    kotlin.io.print(text)
    }

  override fun printLine(text: String?, emptyLineCount: Int) {
    if (text == null) return
    println("${indent of " "}$text")
    for (i in 1..emptyLineCount) println()
    }

  override fun open(filename: String) {
    filenames.push(filename)
    println(">>> OPEN : $filename")
    }

  override fun close() {
    println("<<< CLOSE: ${filenames.pop()}")
    }

  override fun updateFile(sourcePath: String, targetPath: String) {
    println("=== UPDATING: $sourcePath to $targetPath")
    }

  override fun downloadFile(url: String, targetPath: String) {
    println("=== DONLOADING: $url to $targetPath")
    }

  }

class FileHost(override val configuration: Configuration) : Host {
  private val courseRoot get() = configuration.contextRoot
  val root: File get() = configuration.root
  private val outputs = stackOf<PrintWriter>()

  private fun console(level: Configuration.OutputLevel, line: String) {
    if (configuration.outputLevel >= level) println(line)
    }

  override var indent: Int = 0
    set(value) {
      field = value
      if (field < 0) throw IllegalArgumentException("Indent can't be negative")
      }

  override fun print(text: String?) {
    if (text == null) return
    outputs.peek().print(text)
    }

  override fun printLine(text: String?, emptyLineCount: Int) {
    // top file's active writer should never be null
    if (text == null) return
    with (outputs.peek()) {
      println("${indent of " "}$text")
      for (i in 1..emptyLineCount) println()
      }
    }

  override fun readLines(filename: String): List<String> {
    val file = File(courseRoot, filename)
    if (file.exists()) return file.readLines()
    return listOf("File: ${file.absolutePath} can't be found")
    }

  override fun open(filename: String) {
    val file = File(courseRoot, filename)
    val parent = file.parentFile
    parent.mkdirs()
    file.delete()
    file.createNewFile()
    outputs.push(file.printWriter())
    }

  override fun close() {
    outputs.pop().close()
    }

  override fun updateFile(sourcePath: String, targetPath: String) {
    val source = File(sourcePath)
    val target = File(courseRoot, targetPath)
    if (!source.exists()) {
      if (!target.exists()) console(ERROR, "UPDATE: $sourcePath doesn't exist on this machine")
      else console(VERBOSE, "UPDATE: using existing target for $sourcePath")
      }
    else if (!target.exists() || source.lastModified() > target.lastModified()) {
      source.copyTo(target, overwrite = true)
      console(INFO, "UPDATE: $sourcePath updated to $targetPath!")
      }
    else console(VERBOSE, "UPDATE: $targetPath already up to date!")
    }

  override fun downloadFile(url: String, targetPath: String) {
    val source = URL(url)
    val target = File(courseRoot, targetPath)
    if (!target.exists()) {
      try {
        source.copyTo(target)
        console(INFO, "DOWNLOAD: $url downloaded to $targetPath!")
        }
      catch (e : IOException) {
        console(ERROR, "DOWNLOAD: problems downloading $url")
        }
      }
    else {
      console(VERBOSE, "DOWNLOAD: $url already downloaded!")
      }
    }

  }