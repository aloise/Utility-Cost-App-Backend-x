package models

import java.time.LocalDateTime

import models.base.{IndexedRow, IndexedTable, IndexedTableQuery}
import models.helpers.SlickColumnExtensions._
import org.joda.money.{CurrencyUnit, Money}
import slick.driver.H2Driver.api._

/**
  * User: aloise
  * Date: 23.05.16
  * Time: 23:39
  */
case class Bill(
  override val id:Option[Int] = None,
  placeId:Int,
  serviceId:Int,
  serviceRateId:Int,
  readout: BigDecimal = BigDecimal(0),// service - readout
  value:Money, // how much does it cost
  created: LocalDateTime = LocalDateTime.now(),
  paid:Option[LocalDateTime] = None,
  override val isDeleted:Boolean = false
) extends IndexedRow


class BillsTable(tag:Tag) extends IndexedTable[Bill](tag, "bills") {

  def placeId = column[Int]("place_id")
  def serviceId = column[Int]("service_id")
  def serviceRateId = column[Int]("service_rate_id")
  def created = column[LocalDateTime]("created")
  def readout = column[BigDecimal]("readout")
  def paid = column[Option[LocalDateTime]]("paid")

  def valueAmount = column[BigDecimal]("value_amount")
  def valueCurrency = column[String]("value_currency")


  def * = ( id.?, placeId, serviceId, serviceRateId, readout, ( valueAmount, valueCurrency ), created, paid, isDeleted ).shaped <> (
    { case ( id, placeId, serviceId, serviceRateId, readout, value, created, paid, isDeleted ) =>
      Bill( id, placeId, serviceId, serviceRateId, readout, value, created, paid, isDeleted )
    },
    { bill:Bill =>
      Some( ( bill.id, bill.placeId, bill.serviceId, bill.serviceRateId, bill.readout, ( BigDecimal(bill.value.getAmount), bill.value.getCurrencyUnit.getCode ), bill.created, bill.paid, bill.isDeleted ) )
    }
  )

}

object BillsQuery extends IndexedTableQuery[Bill,BillsTable]( tag => new BillsTable(tag) ) {

}
