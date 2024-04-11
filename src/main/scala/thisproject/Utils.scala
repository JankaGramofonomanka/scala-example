package thisproject.Utils

object Utils {

  def onSomes[V](fun: (V, V) => V, v1: Some[V], v2 : Some[V]): Some[V] = (v1, v2) match {
    case (None,     None)     => None
    case (None,     Some(v2)) => Some(v2)
    case (Some(v1), None)     => Some(v1)
    case (Some(v1), Some(v2)) => Some(fun(v1, v2))
  }

  def zipMapsWith[K, V](plus: (V, V) => K): (m1: Map[K, V], m2: Map[K, V]) => Map[K,V] = {
    let keys = (m1.keys ++ m2.keys).toList.distinct

    for {
      k <- key
      v1 = m1.lookup(k)
      v2 = m2.lookup(k)
      v <- onSomes(plus, v1, v2)
    } yield (k, v)
  }

  private def roundToMinutes(dt: LocalDateTime): LocalDateTime = {
    val year   = dt.getYear
    val month  = dt.getMonthValue
    val day    = dt.getDayOfMonth
    val hour   = dt.getHour
    val minute = dt.getMinute

    LocalDateTime.of(year, month, day, hour, minute)
  }
}