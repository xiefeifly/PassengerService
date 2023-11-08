package com.passenger.sound

private const val MP3 = ".mp3"

class Sounds(val assetpath: String) {
    val name = assetpath.split("/").last().removeSuffix(MP3)

}