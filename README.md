# compute-scala-benchmark

Isolated benchmark of ops on the [Compute.scala](https://github.com/ThoughtWorksInc/Compute.scala) library. Uses the sbt-jmh plugin.

To build and run the benchmark, you must have an OpenCL compatible GPU and SBT installed. Clone the repository, then run the following command:

```
sbt clean compile 'Jmh/run AmiBaiC'
```
