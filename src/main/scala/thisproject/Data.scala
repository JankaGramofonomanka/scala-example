package thisproject

import java.time.LocalDateTime

import thisproject.Utils

object Data {

  final case class Stock(
    // Name of the stock
    value: String
  ) extends AnyVal

  final case class Price(
    // The numbner of cents
    value: Integer
  ) extends AnyVal {
    def <(that: Price): Boolean = value < that.value
    def >(that: Price): Boolean = value > that.value
  }
  
  // Aggregates for a single stock
  final case class Aggregate(min: Price, max: Price, avg: Price, private val numItems: Integer)
  object Aggregate {

    def +(lhs: Aggregate, rhs: Aggregate): Aggregate
      = Aggregate(
        if (lhs.min < rhs.min) lhs.min else rhs.min,
        if (lhs.max > rhs.max) lhs.max else rhs.max,
        Price((lhs.numItems*lhs.avg.value + rhs.numItems*rhs.avg.value) / (lhs.numItems + rhs.numItems)),
        lhs.numItems + rhs.numItems,
      )

    def fromPrice(price: Price): Aggregate = Aggregate(price, price, price, 1)
  }

  // A data item returned by the web srevice
  final case class ServiceItem(timestamp: LocalDateTime, data: Map[Stock, Price])

  // A data item stored in the database
  final case class AggregateItem(bucket: Bucket, data: Map[Stock, Aggregate])
  object AggregateItem {
    def fromServiceItem(item: ServiceItem): AggregateItem
      = AggregateItem(Bucket.fromDateTime(item.timestamp), item.data.map((k, v) => (k, Aggregate.fromPrice(v))))
  }

  // A datetime rounded to an hour
  final case class Bucket(private val value: LocalDateTime) extends AnyVal {
    def display: String = value.toString
  }

  object Bucket {
    def fromDateTime(datetime: LocalDateTime): Bucket = Bucket(Utils.roundToHours(datetime))
  }

  // Interfaces ---------------------------------------------------------------
  trait WebService[F[_]] {
    // Returns the list of prices of stocks given as the argument, in the same order, at the time of calling.
    def getPresentValue(stocks: List[Stock]): F[List[Price]]
  }

  trait KeyValueDB[F[_], K, V] {
    def put(key: K, value: V): F[Unit]
    def get(key: K): F[Option[V]]
  }

  // An environment where one can check the current time
  trait Timer[F[_]] {
    def getTimestamp: F[LocalDateTime]
  }
}
