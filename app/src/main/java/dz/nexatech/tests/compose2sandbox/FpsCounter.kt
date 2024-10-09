package dz.nexatech.tests.compose2sandbox

val t0 = System.currentTimeMillis() - 1000L
var fpsHistory = IntArray(3600)
var currFps = 0

fun printFpsHistory() {
    println("FPS: " + fpsHistory.copyOf(fpsHistory.let {
        for (i in fpsHistory.size - 1 downTo 0) {
            if (fpsHistory[i] != 0) return@let i + 1
        }
        fpsHistory.size
    }).contentToString())
}