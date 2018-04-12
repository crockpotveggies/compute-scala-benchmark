package benchmark

import com.thoughtworks.compute.gpu._
import org.openjdk.jmh.annotations._
import java.util.concurrent.TimeUnit

import com.thoughtworks.compute.{OpenCL, Tensors}
import com.typesafe.scalalogging.StrictLogging
import org.lwjgl.opencl.CL10

object benchmarks {

  trait TensorState {
    @Param(Array("GPU"))
    protected var tensorDeviceType: String = _

    @Param(Array("5"))
    protected var numberOfCommandQueuesPerDevice: Int = _

    trait BenchmarkTensors
      extends StrictLogging
        with Tensors.UnsafeMathOptimizations
        with Tensors.SuppressWarnings
        with OpenCL.LogContextNotification
        with OpenCL.UseAllDevicesByType
        with OpenCL.GlobalExecutionContext
        with OpenCL.CommandQueuePool
        with OpenCL.SynchronizedCreatingKernel
        with Tensors.WangHashingRandomNumberGenerator {

      protected val deviceType: Int =
        classOf[CL10].getField(s"CL_DEVICE_TYPE_$tensorDeviceType").get(null).asInstanceOf[Int]

      protected val numberOfCommandQueuesPerDevice: Int = TensorState.this.numberOfCommandQueuesPerDevice

    }
  }

  @State(Scope.Thread)
  class SetupState {
    final val numberOfDimensions = 3
    final val numberOfIterations = 100
    final val size = 128

    var singleArray = Tensor.randomNormal(Array.fill(numberOfDimensions)(size))
    var a: Tensor = Tensor.randomNormal(Array.fill(numberOfDimensions)(size))
    var b: Tensor = Tensor.randomNormal(Array.fill(numberOfDimensions)(size))
    var c: Tensor = Tensor.randomNormal(Array.fill(numberOfDimensions)(size))
  }

  @Threads(value = 1)
  @Warmup(iterations = 5)
  @Measurement(iterations = 5)
  @Timeout(time = 2, timeUnit = TimeUnit.SECONDS)
  @Fork(1)
  @State(Scope.Benchmark)
  class AmiBaiC extends TensorState {

    @Benchmark
    @BenchmarkMode(Array(Mode.SampleTime))
    @OutputTimeUnit(TimeUnit.MICROSECONDS)
    def doBenchmark(state: SetupState): Unit = {
      (0 until state.numberOfIterations).foreach { _i =>
        state.a = state.a * state.b + state.c
      }
      state.a.nonInline.cache().close()
    }
  }
}