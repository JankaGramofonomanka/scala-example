package thisproject

import java.time.LocalDateTime
import cats.effect.IO
import cats.implicits._

import thisproject.Data._

object Mocks {

  object Examples {
    val stock1: Stock = Stock("ABC")
    val stock2: Stock = Stock("XYZ")
  }

  class MockDB extends KeyValueDB[IO, Bucket, Map[Stock, Aggregate]] {
  
    private var aggregateMap: Map[Bucket, Map[Stock, Aggregate]] = Map.empty
  
    def put(key: Bucket, value: Map[Stock, Aggregate]) = IO.delay {
      aggregateMap = aggregateMap.updated(key, value)
    }

    def get(key: Bucket) = IO.delay(aggregateMap.get(key))

    def listContents: IO[Unit] = IO.delay {
      for {
        (bucket, aggregates) <- aggregateMap.toList
        _ <- { println(bucket.toString); List(()) }
        (stock, aggr) <- aggregates
        _ <- {
          println(s"    ${stock.value}: min = ${aggr.min}, max = ${aggr.max}, avg = ${aggr.avg}")
          List(())
        }
      } yield ()
    }
  }

  class MockWebService extends WebService[IO] {
    var iterator: Integer = 0
    private def iteratePV(p1: Integer, p2: Integer, p3: Integer): IO[Price] = IO.delay {
      val result = if (iterator == 0) Price(p1)
              else if (iterator == 1) Price(p2)
              else if (iterator == 2) Price(p3)
              else                    Price(0)
      iterator = (iterator + 1) % 3
      result
    }
      
    private def getPV(stock: Stock): IO[Price]
         = if (stock == Examples.stock1) iteratePV(100, 150, 200)
      else if (stock == Examples.stock2) iteratePV(300, 100, 100)
      else                               iteratePV(100, 100, 100)
    
    def getPresentValue(stocks: List[Stock]) = stocks.traverse(getPV)
  }

  class MockTimer(baseTime: LocalDateTime) extends Timer[IO] {
    var iterator: Integer = 0
    
    def getTimestamp = IO.delay {
      val result = Timestamp(baseTime.plusMinutes(20*iterator))
      iterator += 1
      result
    }
  }

}