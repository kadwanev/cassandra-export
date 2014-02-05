import cascading.flow.FlowProcess
import cascading.flow.hadoop.HadoopFlowProcess
import cascading.operation.{FunctionCall, BaseOperation}
import cascading.tuple.{Fields, Tuple}
import com.twitter.scalding.RichPipe

/*
 * Created by Neville Kadwa.
 */
class ScaldingHelper {

//    .then { p =>  ScaldingHelper.addCounter(p, "my_group", "my_counter") }

  def addCounter(pipe:RichPipe, group:String, counter:String) = {

//    pipe.each(() -> ('addCounter)) { fields =>
//      new BaseOperation[Any](fields) with Function[Any] {
//        def operate(flowProcess:FlowProcess[_], functionCall:FunctionCall[Any]) {
//          try {
//            flowProcess.asInstanceOf[HadoopFlowProcess].increment(group, counter, 1L)
//            functionCall.getOutputCollector.add(new Tuple(new Array[Object](1) : _*))
//          } catch {
//            case cce: ClassCastException =>
  //            HadoopFlowProcess is not available in local mode
//          }
//        }
//      }
//    }.discard('addCounter)
  }

}
