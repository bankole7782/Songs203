package ng.sae.songs203

import java.io.InputStream

fun getHeaderLengthFromVideo(songInputStream: InputStream): Int {
    var headerLengthStr = ""
    while (true) {
        val buffer = ByteArray(1)
        songInputStream.read(buffer)
        if (String(buffer) != "\n") {
            headerLengthStr += String(buffer)
        } else {
            break
        }
    }

    return headerLengthStr.toInt()
}

data class VideoHeader (
    val Meta: Map<String,String>,
    val LaptopUniqueFrames: List<List<Int>>,
    val LaptopFrames: Map<Int,Int>,
    val MobileUniqueFrames: List<List<Int>>,
    val MobileFrames: Map<Int,Int>,
    val AudioSize: Int,
    val LaptopVideoSize: Int,
    val MobileVideoSize: Int,
)

fun readHeaderFromVideo(songInputStream: InputStream): VideoHeader {
    val headerLength = getHeaderLengthFromVideo(songInputStream)
    val buffer = ByteArray(headerLength)
    songInputStream.read(buffer, 0, headerLength)
    val headerStr = String(buffer)

    // begin parsing video Header
    val metaBeginPart = headerStr.indexOf("meta:")
    val metaEndPart = headerStr.substring(metaBeginPart).indexOf("::")
    val metaPart = headerStr.substring(metaBeginPart + "meta:\n".length, metaBeginPart+metaEndPart)

    val meta = HashMap<String, String>()
    for (line in metaPart.lines()) {
        val templine = line.trim()
        if (templine == "") {
            continue
        }
        val partsOfLine = templine.split(":")
        meta[partsOfLine[0]] = partsOfLine[1].trim()
    }

    val luniqueFramesBeginPart = headerStr.indexOf("laptop_unique_frames:")
    val luniqueFramesEndPart = headerStr.substring(luniqueFramesBeginPart).indexOf("::")
    val luniqueFramesPart = headerStr.substring(luniqueFramesBeginPart+"laptop_unique_frames:\n".length,
        luniqueFramesBeginPart+luniqueFramesEndPart)
    val luniqueFrames = ArrayList<List<Int>>()
    for (line in luniqueFramesPart.lines()) {
        val templine = line.trim()
        if (templine == "") {
            continue
        }
        val partsOfLine = templine.split(":")
        val tempList = listOf(partsOfLine[0].toInt(), partsOfLine[1].trim().toInt())
        luniqueFrames.add(tempList)
    }

    val lframesBeginPart = headerStr.indexOf("laptop_frames:")
    val lframesEndPart = headerStr.substring(lframesBeginPart).indexOf("::")
    val lframesPart = headerStr.substring(lframesBeginPart+"laptop_frames:\n".length,
        lframesBeginPart+lframesEndPart)
    val lframes = HashMap<Int, Int>()
    for (line in lframesPart.lines()) {
        val templine = line.trim()
        if (templine == "") {
            continue
        }
        val partsOfLine = templine.split(":")
        lframes[partsOfLine[0].toInt()] = partsOfLine[1].trim().toInt()
    }

    val muniqueFramesBeginPart = headerStr.indexOf("mobile_unique_frames:")
    val muniqueFramesEndPart = headerStr.substring(muniqueFramesBeginPart).indexOf("::")
    val muniqueFramesPart = headerStr.substring(muniqueFramesBeginPart+"mobile_unique_frames:\n".length,
        muniqueFramesBeginPart+muniqueFramesEndPart)
    val muniqueFrames = ArrayList<List<Int>>()
    for (line in muniqueFramesPart.lines()) {
        val templine = line.trim()
        if (templine == "") {
            continue
        }
        val partsOfLine = templine.split(":")
        val tempList = listOf(partsOfLine[0].toInt(), partsOfLine[1].trim().toInt())
        muniqueFrames.add(tempList)
    }

    val mframesBeginPart = headerStr.indexOf("mobile_frames:")
    val mframesEndPart = headerStr.substring(mframesBeginPart).indexOf("::")
    val mframesPart = headerStr.substring(mframesBeginPart+"mobile_frames:\n".length,
        mframesBeginPart+mframesEndPart)
    val mframes = HashMap<Int, Int>()
    for (line in mframesPart.lines()) {
        val templine = line.trim()
        if (templine == "") {
            continue
        }
        val partsOfLine = templine.split(":")
        mframes[partsOfLine[0].toInt()] = partsOfLine[1].trim().toInt()
    }

    val binaryBeginPart = headerStr.indexOf("binary:")
    val binaryEndPart = headerStr.substring(binaryBeginPart).indexOf("::")
    val binaryPart = headerStr.substring(binaryBeginPart+"binary:\n".length,
        binaryBeginPart+binaryEndPart)
    val lines = binaryPart.lines()
    val audioSize = lines[0].substring("audio: ".length).toInt()
    val lVideoSize = lines[1].substring("laptop_frames_lump: ".length).toInt()
    val mVideoSize = lines[2].substring("mobile_frames_lump: ".length).toInt()


    return VideoHeader(meta, luniqueFrames, lframes, muniqueFrames, mframes, audioSize, lVideoSize, mVideoSize)
}