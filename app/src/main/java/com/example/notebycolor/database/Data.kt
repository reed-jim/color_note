package com.example.notebycolor.database

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

class TextNote() : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var text: String = ""
    var color: String = ""
    var createdTime: RealmInstant = RealmInstant.now()
    var modifiedTime: RealmInstant = RealmInstant.now()

    constructor(text: String = "", color: String = "Red") : this() {
        this.text = text
        this.color = color
        createdTime = RealmInstant.now()
        modifiedTime = RealmInstant.now()
    }
}

fun realmFun() {
    val config = RealmConfiguration.Builder(schema = setOf(TextNote::class))
        .deleteRealmIfMigrationNeeded()
        .build()

    val realm: Realm = Realm.open(config)

//    realm.writeBlocking {
//        copyToRealm(Item().apply {
//            summary = "Do the laundry"
//            isComplete = false
//        })
//    }
}