package thisproject.Mocks

import thisproject.Data

object Mocks {

  object Examples {
    val stock1: Stock = Stock("ABC")
    val stock2: Stock = Stock("XYZ")
  }

  class MockDB extends KeyValueDB[IO, Bucket, Map[Stock, Aggregate]] {
    var aggregateMap: Map[Bucket, Map[Stock, Aggregate]] = Map.empty
    def put(key: Bucket, value: Map[Stock, Aggregate]) = IO.delay {
      aggregateMap = aggregateMap.updated(key, value)
    }
  }

  class MockWebService extends WebService[IO] {
    var iterator: Integer = 0
    private def iteratePV(p1: Integer, p2: Integer, p3: Integer): IO[Price] = IO.delay {
      val result = if (iterator == 0) p1
              else if (iterator == 1) p2
              else if (iterator == 2) p3
              else                    Stock(0)
      iterator = (iterator + 1) % 3
      result
    }
      
    private def getPV(stock: Stock): IO[Price]
         = if (stock == Examples.stock1) iteratePV(100, 150, 200)
      else if (stock == Examples.stock2) iteratePV(300, 100, 100)
      else                               iteratePV(100, 100, 100)
    
    def getPresentValue(stocks: Array[Stock]) = stocks.traverse(getPV)
  }

  class MockTimer(baseTime: LocalDateTime) extends Timer[IO] {
    var iterator: Integer = 0
    
    def getTimestamp = IO.delay {
      val result = baseTime.plusMinutes(20*iterator)
      iterator += 1
      result
    }
  }


}