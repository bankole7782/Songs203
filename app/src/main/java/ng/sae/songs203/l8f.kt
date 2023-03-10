package ng.sae.songs203

import java.io.File
import java.io.InputStream

fun getHeaderLengthFromVideo(songInputStream: InputStream): Int {
    var headerLengthStr: String = ""
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