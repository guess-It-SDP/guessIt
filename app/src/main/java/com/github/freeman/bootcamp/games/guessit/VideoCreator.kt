package com.github.freeman.bootcamp.games.guessit

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.core.net.toUri
import com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS
import com.arthenica.mobileffmpeg.FFmpeg
import com.github.freeman.bootcamp.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.io.FileWriter

/**
 * Helper class that allows to create a GuessIt recap video for a given gameId
 * from a set of images stored in the firebase storage
 */
class VideoCreator {

    companion object {
        /**
         * Creates a recap video for the given game
         *
         * @param context context of the app
         * @param gameId id of the game
         */
        fun createRecap(context: Context, gameId: String) {
            val storageRef = Firebase.storage.reference

            val videoName = "recap"

            val selfiesFileList = mutableListOf<StorageReference>()
            val drawingsFileList = mutableListOf<StorageReference>()
            var combinedFileList: List<StorageReference>
            val combinedList = mutableListOf<Bitmap>()

            // create the local directory if not existing
            val videoDir = File(context.getExternalFilesDir(null), context.getString(R.string.game_recaps_local_path))
            if (!videoDir.exists()) {
                videoDir.mkdirs()
            }

            val userIdRefList = mutableListOf<StorageReference>()

            storageRef
                .child(context.getString(R.string.game_recaps_path))
                .child(gameId)
                .listAll()
                .addOnSuccessListener { userIdListResult ->
                    userIdListResult.prefixes.forEach { userIdRef ->
                        // fetch every user
                        userIdRefList.add(userIdRef)
                    }

                    for (i in 0 until userIdRefList.size) {
                        userIdRefList[i]
                        .child(context.getString(R.string.game_recaps_selfies_path))
                            .listAll()
                            .addOnSuccessListener { selfieListResult ->
                                // fetch every selfies of a user
                                selfieListResult.items.forEach { item ->
                                    selfiesFileList.add(item)
                                }

                                userIdRefList[i]
                                    .child(context.getString(R.string.game_recaps_drawings_path))
                                    .listAll()
                                    .addOnSuccessListener { drawingListResult ->
                                        // fetch every drawings of a user
                                        drawingListResult.items.forEach { item ->
                                            drawingsFileList.add(item)
                                        }

                                        // create the video only when all images are fetched
                                        if (i == userIdRefList.size - 1) {
                                            combinedFileList = drawingsFileList.zip(selfiesFileList).flatMap { listOf(it.first, it.second) }

                                            // downloads all files
                                            for (file in combinedFileList) {
                                                val maxDownloadSize = 5 * 1024 * 1024.toLong()
                                                file.getBytes(maxDownloadSize).addOnSuccessListener { bytes ->
                                                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                                                    combinedList.add(bitmap)
                                                    if (combinedList.size == combinedFileList.size) {
                                                        createVideoFromImages(context, combinedList, videoName, videoDir, gameId)
                                                    }
                                                }
                                            }
                                        }
                                    }
                            }
                    }
                }
        }

        private fun createVideoFromImages(context: Context, bitmapList: List<Bitmap>, videoName: String, videoDir: File, gameID: String) {

            val videoFileName = "$videoName.mp4"
            val videoFile = File(videoDir, videoFileName)
            val videoFilePath = videoFile.absolutePath

            val cacheDir = context.cacheDir
            val imageList = mutableListOf<Pair<Int, String>>()

            for (i in bitmapList.indices) {
                val fos = context.openFileOutput("image_$i.png", Context.MODE_PRIVATE)
                bitmapList[i].compress(Bitmap.CompressFormat.PNG, 10, fos)
                fos.close()
                imageList.add(Pair(i, context.getFileStreamPath("image_$i.png").absolutePath))
            }

            val imageDuration = 5 // in seconds

            // Sort the list of image file paths based on the index values in the Pair objects
            imageList.sortBy { it.first }

            // Generate the input file list for FFmpeg
            val inputFileList = StringBuilder()
            for (pair in imageList) {
                val imageFilePath = pair.second
                inputFileList.append("file '$imageFilePath'\n")
                inputFileList.append("duration $imageDuration\n")
            }

            // Write the input file list to a text file
            val inputFileListPath = File(cacheDir, "input_file_list.txt").absolutePath
            FileWriter(inputFileListPath).use { writer ->
                writer.write(inputFileList.toString())
            }

            // Run FFmpeg command to create the video
            val ffmpegCommand = "-y -f concat -safe 0 -i $inputFileListPath -c:v libx264 -r 30 -pix_fmt yuv420p $videoFilePath"

            val rc = FFmpeg.execute(ffmpegCommand)
            if (rc == RETURN_CODE_SUCCESS) {
                // video created successfully
                Toast.makeText(context, "video created", Toast.LENGTH_SHORT).show()
                val storageRef = Firebase.storage.reference.child(context.getString(R.string.game_recaps_path))
                    .child(gameID).child(videoFileName)
                storageRef.putFile(videoFile.toUri())
            } else {
                // error creating video
                Toast.makeText(context, "error $rc : video not created", Toast.LENGTH_SHORT).show()
            }
        }
    }
}