package com.example.tarea1grupog3.databases

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.google.android.gms.maps.model.LatLng

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "app.db"
        private const val DATABASE_VERSION = 1

        // Tablas
        private const val TABLE_USERS = "usuarios"
        private const val TABLE_PLACES = "lugares"

        // Columnas de la tabla de usuarios
        private const val COLUMN_USER_ID = "id"
        private const val COLUMN_USER_EMAIL = "correo"
        private const val COLUMN_USER_PASSWORD = "password"

        // Columnas de la tabla de lugares
        private const val COLUMN_PLACE_ID = "id"
        private const val COLUMN_PLACE_NAME = "nombre"
        private const val COLUMN_PLACE_LAT = "latitud"
        private const val COLUMN_PLACE_LNG = "longitud"
    }

    // Crear las tablas
    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_USERS_TABLE = ("CREATE TABLE $TABLE_USERS ("
                + "$COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_USER_EMAIL TEXT UNIQUE, "
                + "$COLUMN_USER_PASSWORD TEXT)")

        val CREATE_PLACES_TABLE = ("CREATE TABLE $TABLE_PLACES ("
                + "$COLUMN_PLACE_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_PLACE_NAME TEXT, "
                + "$COLUMN_PLACE_LAT REAL, "
                + "$COLUMN_PLACE_LNG REAL)")

        db.execSQL(CREATE_USERS_TABLE)
        db.execSQL(CREATE_PLACES_TABLE)

        val users = listOf(
            Pair("usuario1@gmail.com", "password1"),
            Pair("usuario2@gmail.com", "password2"),
            Pair("usuario3@gmail.com", "password3")
        )

        for (user in users) {
            val values = ContentValues().apply {
                put(COLUMN_USER_EMAIL, user.first)
                put(COLUMN_USER_PASSWORD, user.second)
            }
            db.insert(TABLE_USERS, null, values)
        }

        val places = listOf(
            Triple("Estadio Alberto Gallardo", -12.037929, -77.045074),
            Triple("Estadio Alejandro Villanueva", -12.068362, -77.022061),
            Triple("Estadio Monumental", -12.055559, -76.937087),
            Triple("Estadio Nacional", -12.067242, -77.033635),
            Triple("Estadio Olimpico UNMSM", -12.057407, -77.083410)
        )

        for (place in places){
            val values = ContentValues().apply {
                put(COLUMN_PLACE_NAME, place.first)
                put(COLUMN_PLACE_LAT, place.second)
                put(COLUMN_PLACE_LNG, place.third)
            }
            db.insert(TABLE_PLACES, null, values)
        }
    }

    // Si se necesita una nueva versión de la base de datos
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PLACES")
        onCreate(db)
    }

    // CRUD para Usuarios

    // Validar usuario en el login
    fun validateUser(correo: String, password: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USER_EMAIL = ? AND $COLUMN_USER_PASSWORD = ?"
        val cursor: Cursor = db.rawQuery(query, arrayOf(correo, password))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    // CRUD para Lugares

    // Obtener todos los lugares
    fun getAllLugares(): List<Pair<String, LatLng>> {
        val lugaresList = mutableListOf<Pair<String, LatLng>>()
        val db = this.readableDatabase
        val query = "SELECT $COLUMN_PLACE_NAME, $COLUMN_PLACE_LAT, $COLUMN_PLACE_LNG FROM $TABLE_PLACES"
        val cursor: Cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLACE_NAME))
                val latitud = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PLACE_LAT))
                val longitud = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PLACE_LNG))
                val coordenada = LatLng(latitud, longitud)
                lugaresList.add(Pair(nombre, coordenada))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return lugaresList
    }
}
