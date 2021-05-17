package fr.lsmbo.personnel

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import java.io.{File, FileInputStream, InputStream}
import java.text.SimpleDateFormat
import java.util.Calendar

object MyConfig {

  lazy val config: Config = ConfigFactory.load(this.getClass.getClassLoader, "application.conf")

  lazy val personnelFile = new File(config.getString("people.file.path"))

  def getTemplateFileStream: InputStream = {
    // prefer an outside file
    val file = new File("classes/"+config.getString("template.file.name"))
    // but if no file is found, try to use the file inside the jar
    if(!file.exists()) {
      this.getClass.getResourceAsStream("/"+config.getString("template.file.name"))
    } else {
      new FileInputStream(file)
    }
  }
  
  lazy val startCurrentMonth: Boolean = {
    try {
      config.getBoolean("putz.start.current.month")
    } catch {
      case _: Exception => false
    }
  }
  
  lazy val numberOfMonthes: Int = {
    try {
      config.getInt("putz.number.of.monthes")
    } catch {
      case _: Exception => 4
    }
  }

  lazy val outputFile: File = {
    try {
      val dateFormatter = new SimpleDateFormat("yyyy-MM-dd")
      new File("PersonnelLabo-"+dateFormatter.format(Calendar.getInstance.getTime)+".xlsx")
    } catch {
      case e: Exception =>
        e.printStackTrace()
        new File("PersonnelLabo.xlsx")
    }
  }

  lazy val trombiPictureFolder: File = {
    try {
      new File(config.getString("trombi.picture.folder"))
    } catch {
      case e: Exception =>
        println(s"Error on trombiPictureFolder: ${e.getMessage}")
        new File("classes/img")
    }
  }
  
  def defaultPicture : InputStream = this.getClass.getResourceAsStream("/Portrait_placeholder.png")

  lazy val trombiTitle = "Trombinoscope"
  var putzTitle = "Putz planning"
  lazy val annivTitle = "Anniversaires"
  lazy val mapTitle = "Plan du labo"

}
