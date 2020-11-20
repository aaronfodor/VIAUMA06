package com.arpadfodor.communityparking.android.app.model.repository

import android.graphics.Bitmap
import com.arpadfodor.communityparking.android.app.model.MediaHandler
import com.arpadfodor.communityparking.android.app.model.db.ApplicationDB
import com.arpadfodor.communityparking.android.app.model.db.dataclasses.DbUserRecognition
import com.arpadfodor.communityparking.android.app.model.repository.dataclasses.UserRecognition

object UserRecognitionRepository {

    /**
     * User recognitions
     **/
    fun postUserRecognition(recognition: UserRecognition, callback: (Boolean) -> Unit){

        Thread {

            val database = ApplicationDB.getDatabase(GeneralRepository.context)
            var isSuccess = false

            var imagePath: String? = null

            try {

                recognition.image?.let { image ->
                    MediaHandler.getExternalDbImagesDirOfUser(recognition.reporterEmail)?.let { dir ->
                        imagePath = MediaHandler.saveImage(dir, image)
                    }
                }

                val dbUserRecognition = userRecognitionToDbUserRecognition(recognition, imagePath)

                // run delete, insert, etc. in an atomic transaction
                database.runInTransaction {
                    database.userRecognitionTable().insert(dbUserRecognition)
                }

                //update the local flag
                isSuccess = true

            }
            catch (e: Exception) {
                e.printStackTrace()
            }
            finally {
                callback(isSuccess)
            }

        }.start()

    }

    fun getByUser(userId: String, callback: (List<UserRecognition>) -> Unit) {

        Thread {

            val reports = mutableListOf<DbUserRecognition>()
            val reportImages = mutableListOf<Bitmap?>()

            val database = ApplicationDB.getDatabase(GeneralRepository.context)

            try {

                // run delete, insert, etc. in an atomic transaction
                database.runInTransaction {

                    val dbContent = database.userRecognitionTable().getByReporter(userId) ?: listOf()

                    for (element in dbContent) {
                        reports.add(element)
                        reportImages.add(MediaHandler.getImageByPath(element.imagePath))
                    }

                }

            }
            catch (e: Exception) {
                e.printStackTrace()
            }
            finally {
                callback(dbUserRecognitionListToUserRecognitionList(reports, reportImages))
            }

        }.start()

    }

    fun updateSentFlagByIdAndUser(id: Int, userId: String, sentFlag: Boolean, callback: (Boolean) -> Unit) {

        Thread {

            val database = ApplicationDB.getDatabase(GeneralRepository.context)
            var isSuccess = false

            try {

                // run delete, insert, etc. in an atomic transaction
                database.runInTransaction {
                    database.userRecognitionTable().updateSentFlagByIdAndReporter(id, userId, sentFlag)
                }
                isSuccess = true

            }
            catch (e: Exception) {
                e.printStackTrace()
            }
            finally {
                callback(isSuccess)
            }

        }.start()

    }

    fun updateMessageByIdAndUser(id: Int, userId: String, message: String, callback: (Boolean) -> Unit) {

        Thread {

            val database = ApplicationDB.getDatabase(GeneralRepository.context)
            var isSuccess = false

            try {

                // run delete, insert, etc. in an atomic transaction
                database.runInTransaction {
                    database.userRecognitionTable().updateMessageByIdAndReporter(id, userId, message)
                }
                isSuccess = true

            }
            catch (e: Exception) {
                e.printStackTrace()
            }
            finally {
                callback(isSuccess)
            }


        }.start()

    }

    fun deleteByIdAndUser(id: Int, userId: String, callback: (Boolean) -> Unit) {

        Thread {

            val database = ApplicationDB.getDatabase(GeneralRepository.context)
            var isSuccess = false

            try {

                var userRecognitionToDelete: DbUserRecognition? = null

                // run delete, insert, etc. in an atomic transaction
                database.runInTransaction {
                    userRecognitionToDelete = database.userRecognitionTable().getByIdAndReporter(id, userId)
                }

                userRecognitionToDelete?.imagePath?.let {
                    MediaHandler.deleteImage(it)
                }

                // run delete, insert, etc. in an atomic transaction
                database.runInTransaction {
                    database.userRecognitionTable().deleteByIdAndReporter(id, userId)
                }

                isSuccess = true

            }
            catch (e: Exception) {
                e.printStackTrace()
            }
            finally {
                callback(isSuccess)
            }

        }.start()

    }

    fun deleteAllFromUser(userId: String, callback: (Boolean) -> Unit) {

        Thread {

            val database = ApplicationDB.getDatabase(GeneralRepository.context)
            var isSuccess = false

            try {

                // run delete, insert, etc. in an atomic transaction
                database.runInTransaction {
                    database.userRecognitionTable().deleteAllByReporter(userId)
                }

                MediaHandler.deleteExternalDbImagesOfUser(userId)

                isSuccess = true

            }
            catch (e: Exception) {
                e.printStackTrace()
            }
            finally {
                callback(isSuccess)
            }

        }.start()

    }

    private fun userRecognitionToDbUserRecognition(source: UserRecognition, imagePath: String?) : DbUserRecognition {
        return DbUserRecognition(null, source.reporterEmail, source.latitude, source.longitude,
            source.timestampUTC, source.message, source.isReserved, source.feePerHour, imagePath, source.isSent)
    }

    private fun dbUserRecognitionToUserRecognition(source: DbUserRecognition, image: Bitmap?) : UserRecognition {
        val artificialId = source.Id?.toInt() ?: 0

        return UserRecognition(artificialId, source.reporterEmail, source.latitude, source.longitude,
            source.timestampUTC, source.message, source.isReserved, source.feePerHour,
            image, false, source.isSent)
    }

    private fun dbUserRecognitionListToUserRecognitionList(sourceList: List<DbUserRecognition>, sourceImages: List<Bitmap?>) : List<UserRecognition>{

        val userRecognitionList = mutableListOf<UserRecognition>()

        for(index in sourceList.indices){
            val element = sourceList[index]
            val image = sourceImages[index]

            val constructed = dbUserRecognitionToUserRecognition(element, image)
            userRecognitionList.add(constructed)
        }

        return userRecognitionList

    }

}