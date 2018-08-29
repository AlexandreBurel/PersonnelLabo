package fr.lsmbo.personnel

object LocationList extends Enumeration {
  type LocationList = Value
  val R5 = Value("R5-N0")
  val R2 = Value("R2-N0")
  val IPHC = Value("27")
  val UNKNOWN = Value("Non défini")
  def withNameOpt(s: String): Option[Value] = values.find(_.toString == s)
}

object CorpsList extends  Enumeration {
  type Corps = Value
  val DOCTORANT = Value("Doctorant")
  val STAGIAIREM2 = Value("Stagiaire M2")
  val STAGIAIREM1 = Value("Stagiaire M1")
  val CDD_CHERCHEUR = Value("CDD - Chercheur")
  val CDD_IR = Value("CDD - IR")
  val CDD_IE = Value("CDD - IE")
  val CDD_AI = Value("CDD - AI")
  val CDD_T = Value("CDD - T")
  val PERMANENT_DR = Value("Permanent - DR")
  val CDI_IR = Value("CDI - IR")
  val CDI_AI = Value("CDI - AI")
  val PERMANENT_CR = Value("Permanent - CR")
  val PERMANENT_IE = Value("Permanent - IE")
  val PERMANENT_IR = Value("Permanent - IR")
  val PERMANENT_MCU = Value("Permanent  - MCU")
  val PERMANENT_PU = Value("Permanent - PU")
  val PERMANENT_T = Value("Permanent - T")
  val AUTRE = Value("Autre")
  def withNameOpt(s: String): Option[Value] = values.find(_.toString == s)
}
case class Corps(value: CorpsList.Value, name: String) {
  def this(name: String) = this(CorpsList.withNameOpt(name).getOrElse(CorpsList.AUTRE), name)
  def get: String = if(value.equals(CorpsList.AUTRE)) name else value.toString()
  lazy val order: Int = {
    value match {
      case CorpsList.DOCTORANT => 13
      case CorpsList.STAGIAIREM2 => 14
      case CorpsList.STAGIAIREM1 => 15
      case CorpsList.CDD_CHERCHEUR => 8
      case CorpsList.CDD_IR => 9
      case CorpsList.CDD_IE => 10
      case CorpsList.CDD_AI => 11
      case CorpsList.CDD_T => 12
      case CorpsList.PERMANENT_DR => 1
      case CorpsList.CDI_IR => 5
      case CorpsList.CDI_AI => 7
      case CorpsList.PERMANENT_CR => 3
      case CorpsList.PERMANENT_IE => 6
      case CorpsList.PERMANENT_IR => 5
      case CorpsList.PERMANENT_MCU => 4
      case CorpsList.PERMANENT_PU => 2
      case CorpsList.PERMANENT_T => 7
      case CorpsList.AUTRE => 16
      case _ => 17
    }
  }
}