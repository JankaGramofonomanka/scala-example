package thisproject

import java.time.LocalDateTime

import thisproject.Utils

object Data {

  final case class Stock(value: String) extends AnyVal

  final case class Price(value: Integer) extends AnyVal {
    def <(that: Price): Boolean = value < that.value
    def >(that: Price): Boolean = value > that.value
  }
  object Price {
    def <(lhs: Price, rhs: Price): Boolean = lhs.value < rhs.value
    def >(lhs: Price, rhs: Price): Boolean = lhs.value > rhs.value
  }
  
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

  final case class ServiceItem(timestamp: Timestamp, data: Map[Stock, Price])

  final case class AggregateItem(bucket: Bucket, data: Map[Stock, Aggregate])
  object AggregateItem {
    def fromServiceItem(item: ServiceItem): AggregateItem
      = AggregateItem(item.timestamp.getBucket, item.data.map((k, v) => (k, Aggregate.fromPrice(v))))
  }

  // Time
  final case class Timestamp(value: LocalDateTime) extends AnyVal {
    def getBucket: Bucket = Bucket(Utils.roundToHours(value))
  }

  final case class Bucket(private val value: LocalDateTime) extends AnyVal {
    def display: String = value.toString
  }

  // Interfaces
  trait WebService[F[_]] {
    def getPresentValue(stocks: List[Stock]): F[List[Price]]
  }

  trait KeyValueDB[F[_], K, V] {
    def put(key: K, value: V): F[Unit]
    def get(key: K): F[Option[V]]
  }

  trait Timer[F[_]] {
    def getTimestamp: F[Timestamp]
  }

}