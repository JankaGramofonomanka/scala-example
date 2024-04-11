package thisproject.Data

import java.time.LocalDateTime

import thisproject.Utils

object Data {

  final case class Stock(value: String) extends AnyVal

  final case class Price(value: Integer) extends AnyVal

  final case class Aggregate(min: Price, max: Price, avg: Price, private val numItems: Integer)
  object Aggregate {
    def +(rhs: Aggregate, rhs: Aggregate): Aggregate
      = Aggregate(
        if (lhs.min < rhs.min) lhs.min else rhs.min,
        if (lhs.max > rhs.max) lhs.max else rhs.max,
        (lhs.numItems*lhs.avg + rhs.numItems*rhs.avg) / (lhs.numItems + rhs.numItems),
        lhs.numItems + rhs.numItems,
      )
    def fromPrice(price: Price): Aggregate = Aggregate(price, price, price, 1)
  }

  final case class ServiceItem(timestamp: Timestamp, data: Map[Stock, Price])

  final case class AggregateItem(bucket: Bucket, data: Map[Stock, Aggregate])
  object AggregateItem {
    def fromServiceItem(item: ServiceItem): AggregateItem = AggregateItem(item.timestamp.getBucket, item.data.map(Aggregate.fromPrice))
  }

  // Time
  final case class Timestamp(value: LocalDateTime) extends AnyVal {
    def getBucket: Bucket = Bucket(Utils.roundToMinutes(value))

    def isAfter (ts: Timestamp): Boolean = value.isAfter  (ts.value)
    def isBefore(ts: Timestamp): Boolean = value.isBefore (ts.value)
    def isEqual (ts: Timestamp): Boolean = value.isEqual  (ts.value)
  }

  final case class Bucket(private val value: LocalDateTime) extends AnyVal {
    def addMinutes(n: Long): Bucket = Bucket(value.plus(n, ChronoUnit.MINUTES))
    
    def getSeconds: Long = value.toEpochSecond(UTC)
    
    def toTimestamp: Timestamp = Timestamp(value)
    def toDateTime: DateTime = value

    def isAfter (bucket: Bucket): Boolean = value.isAfter (bucket.value)
    def isBefore(bucket: Bucket): Boolean = value.isBefore(bucket.value)
    def isEqual (bucket: Bucket): Boolean = value.isEqual (bucket.value)
  }

  // Interfaces
  trait WebService[F[_]] {
    def getPresentValue(stocks: Array[Stock]): F[Array[Price]]
  }

  trait KeyValueDB[F[_], K, V] {
    def put(key: K, value: V): F[Unit]
    def get(key: K): F[Some[V]]
  }

  trait Timer[F[_]] {
    def getTimestamp: F[Timestamp]
  }

}