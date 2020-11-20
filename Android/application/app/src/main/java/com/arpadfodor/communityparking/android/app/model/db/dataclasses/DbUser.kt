package com.arpadfodor.communityparking.android.app.model.db.dataclasses

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.arpadfodor.communityparking.android.app.model.db.ApplicationDB
import java.io.Serializable

@Entity(tableName = ApplicationDB.USER_TABLE_NAME)
data class DbUser(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "email")
    var email: String = "",
    @ColumnInfo(name = "password")
    val password: String = "",
    @ColumnInfo(name = "name")
    var name: String = "",
    @ColumnInfo(name = "hint")
    val hint: String = "",
    @ColumnInfo(name = "reserved_lot_id")
    var reservedLotId: String = ""
) : Serializable