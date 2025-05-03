package com.example.capital_taxi.data.utils

import com.example.capital_taxi.domain.Trip
import com.example.capital_taxi.domain.driver.model.Instruction
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import com.google.maps.android.SphericalUtil

import org.osmdroid.util.GeoPoint
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


fun getCurrentInstructionIndex(
    location: LatLng,
    instructions: List<Instruction>,
    polyline: List<LatLng>
): Int {
    var closestIndex = -1
    var minDistance = Double.MAX_VALUE

    instructions.forEachIndexed { i, instruction ->
        val interval = instruction.interval
        if (interval.size < 2) return@forEachIndexed

        val segment = polyline.subList(interval[0], interval[1] + 1)
        segment.forEach { point ->
            val dist = SphericalUtil.computeDistanceBetween(location, point)
            if (dist < minDistance) {
                minDistance = dist
                closestIndex = i
            }
        }
    }

    return closestIndex
}
fun calculateRemainingDistance(
    currentIndex: Int,
    location: LatLng,
    instructions: List<Instruction>,
    polyline: List<LatLng>
): Double {
    if (currentIndex == -1) return 0.0

    val currentInstruction = instructions[currentIndex]
    val interval = currentInstruction.interval
    if (interval.size < 2) return 0.0

    val segment = polyline.subList(interval[0], interval[1] + 1)

    // 1. نحسب من موقع السائق لأقرب نقطة في الـ segment
    val closestPoint = segment.minByOrNull { SphericalUtil.computeDistanceBetween(location, it) } ?: return 0.0
    val indexOfClosest = segment.indexOf(closestPoint)

    var distance = 0.0

    // 2. من موقع السائق حتى نهاية هذا segment
    for (i in indexOfClosest until segment.size - 1) {
        distance += SphericalUtil.computeDistanceBetween(segment[i], segment[i + 1])
    }

    // 3. نضيف باقي التعليمات بعد الخطوة الحالية
    for (i in currentIndex + 1 until instructions.size) {
        distance += instructions[i].distance
    }

    return distance
}
fun calculateRemainingTime(
    currentIndex: Int,
    elapsedTimeInCurrentStep: Long,
    instructions: List<Instruction>
): Long {
    if (currentIndex == -1) return 0L

    var remainingTime = instructions[currentIndex].time - elapsedTimeInCurrentStep
    if (remainingTime < 0) remainingTime = 0

    for (i in currentIndex + 1 until instructions.size) {
        remainingTime += instructions[i].time
    }

    return remainingTime
}
fun getTrimmedPolyline(
    currentIndex: Int,
    location: LatLng,
    instructions: List<Instruction>,
    polyline: List<LatLng>
): List<LatLng> {
    if (currentIndex == -1) return polyline

    val currentInstruction = instructions[currentIndex]
    val interval = currentInstruction.interval
    if (interval.size < 2) return polyline

    val segment = polyline.subList(interval[0], interval[1] + 1)

    val closestPoint = segment.minByOrNull { SphericalUtil.computeDistanceBetween(location, it) } ?: return polyline
    val indexOfClosest = segment.indexOf(closestPoint)

    val result = mutableListOf<LatLng>()

    // من النقطة الحالية لنهاية الخطوة
    result.addAll(segment.subList(indexOfClosest, segment.size))

    // باقي الخطوات
    for (i in currentIndex + 1 until instructions.size) {
        val inst = instructions[i]
        if (inst.interval.size < 2) continue
        result.addAll(polyline.subList(inst.interval[0], inst.interval[1] + 1))
    }

    return result
}
