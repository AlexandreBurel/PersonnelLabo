package fr.lsmbo.personnel

import fr.lsmbo.personnel

object LocationList extends Enumeration {
  type LocationList = Value
  val R5: personnel.LocationList.Value = Value("R5-N0")
  val R2: personnel.LocationList.Value = Value("R2-N0")
  val IPHC: personnel.LocationList.Value = Value("27")
  val UNKNOWN: personnel.LocationList.Value = Value("Non défini")
  def withNameOpt(s: String): Option[Value] = values.find(_.toString == s)
}

object CorpsList extends  Enumeration {
  type Corps = Value
  val DOCTORANT: CorpsList.Value = Value("Doctorant")
  val STAGIAIREM2: CorpsList.Value = Value("Stagiaire M2")
  val STAGIAIREM1: CorpsList.Value = Value("Stagiaire M1")
  val CDD_CHERCHEUR: CorpsList.Value = Value("CDD - Chercheur")
  val CDD_IR: CorpsList.Value = Value("CDD - IR")
  val CDD_IE: CorpsList.Value = Value("CDD - IE")
  val CDD_AI: CorpsList.Value = Value("CDD - AI")
  val CDD_T: CorpsList.Value = Value("CDD - T")
  val PERMANENT_DR: CorpsList.Value = Value("Permanent - DR")
  val CDI_IR: CorpsList.Value = Value("CDI - IR")
  val CDI_AI: CorpsList.Value = Value("CDI - AI")
  val PERMANENT_CR: CorpsList.Value = Value("Permanent - CR")
  val PERMANENT_IE: CorpsList.Value = Value("Permanent - IE")
  val PERMANENT_IR: CorpsList.Value = Value("Permanent - IR")
  val PERMANENT_MCU: CorpsList.Value = Value("Permanent  - MCU")
  val PERMANENT_PU: CorpsList.Value = Value("Permanent - PU")
  val PERMANENT_T: CorpsList.Value = Value("Permanent - T")
  val AUTRE: CorpsList.Value = Value("Autre")
  val POST_DOC: CorpsList.Value = Value("Post-doc")
  val PERMANENT_AI: CorpsList.Value = Value("Permanent - AI")
  val LICENCE_L3: CorpsList.Value = Value("Licence 3ème année")
  val CHERCHEUR_INVITE: CorpsList.Value = Value("Chercheur invité")
  val CDI_IE: CorpsList.Value = Value("CDI - IE")
  val ATER: CorpsList.Value = Value("ATER")
  def withNameOpt(s: String): Option[Value] = values.find(_.toString == s)
}
case class Corps(value: CorpsList.Value, name: String) {
  def this(name: String) = this(CorpsList.withNameOpt(name).getOrElse(CorpsList.AUTRE), name)
  def get: String = if(value.equals(CorpsList.AUTRE)) name else value.toString
  lazy val order: Int = {
    value match {
      case CorpsList.PERMANENT_DR => 1
      case CorpsList.PERMANENT_PU => 2
      case CorpsList.PERMANENT_CR => 3
      case CorpsList.PERMANENT_MCU => 4
      case CorpsList.PERMANENT_IR => 5
      case CorpsList.PERMANENT_IE => 6
      case CorpsList.PERMANENT_AI => 7
      case CorpsList.PERMANENT_T => 8
      case CorpsList.CDI_IR => 9
      case CorpsList.CDI_IE => 10
      case CorpsList.CDI_AI => 11
      case CorpsList.CHERCHEUR_INVITE => 12
      case CorpsList.CDD_CHERCHEUR => 13
      case CorpsList.POST_DOC => 14
      case CorpsList.ATER => 15
      case CorpsList.CDD_IR => 16
      case CorpsList.CDD_IE => 17
      case CorpsList.CDD_AI => 18
      case CorpsList.CDD_T => 19
      case CorpsList.DOCTORANT => 20
      case CorpsList.STAGIAIREM2 => 21
      case CorpsList.STAGIAIREM1 => 22
      case CorpsList.LICENCE_L3 => 23
      case CorpsList.AUTRE => 24
      case _ => 25
    }
  }
}