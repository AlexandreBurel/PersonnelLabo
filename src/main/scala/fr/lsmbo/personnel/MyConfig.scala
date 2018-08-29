package fr.lsmbo.personnel

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import java.io.File

object MyConfig {
  
  lazy val config: Config = ConfigFactory.load(this.getClass().getClassLoader(), "application.conf")

  lazy val personnelFile = new File(config.getString("people.file.path"))

  lazy val putzTemplateFile: File = {
    try {
      new File(config.getString("putz.template.file.path"))
    } catch {
      case e: Exception =>
        println("Error "+e.getMessage)
        new File("classes/PutzPlanningTemplate.xlsx")
    }
  }
  lazy val putzOutputFile: File = {
    try {
      new File(config.getString("putz.output.file.path"))
    } catch {
      case e: Exception => new File("PutzPlanning.xlsx")
    }
  }
  
  lazy val startCurrentMonth: Boolean = {
    try {
      config.getBoolean("start.current.month")
    } catch {
      case e: Exception => false
    }
  }
  
  lazy val numberOfMonthes: Int = {
    try {
      config.getInt("number.of.monthes")
    } catch {
      case e: Exception => 4
    }
  }
  
  lazy val trombiOutputFile: File = {
    try {
      new File(config.getString("trombi.output.file.path"))
    } catch {
      case e: Exception => new File("Trombinoscope.xlsx")
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
  
  lazy val defaultPicture: File = new File("classes/Portrait_placeholder.png")

  lazy val birthdayOutputFile: File = {
    try {
      new File(config.getString("birthday.output.file.path"))
    } catch {
      case e: Exception => new File("Anniversaires.xlsx")
    }
  }

}
