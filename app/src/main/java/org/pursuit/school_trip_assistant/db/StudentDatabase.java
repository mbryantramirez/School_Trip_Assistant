package org.pursuit.school_trip_assistant.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.pursuit.school_trip_assistant.model.Student;

import java.io.File;
import java.util.LinkedList;

public final class StudentDatabase extends SQLiteOpenHelper {
  public static final String DATABASE_NAME = "STUDENT_DATABASE.db";
  private static final String TABLE_NAME = "Students";
  private static final int SCHEMA_VERSION = 1;
  private static StudentDatabase instance;

  private StudentDatabase(Context context) {
    super(context, DATABASE_NAME, null, SCHEMA_VERSION);
  }

  public static StudentDatabase getInstance(Context context) {
    if (instance == null) {
      instance = new StudentDatabase(context);
    }
    return instance;
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(
      "CREATE TABLE " + TABLE_NAME +
        " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        "last_name TEXT, first_name TEXT, emergency_number TEXT, image TEXT);");
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    //No-Op
  }

  public void addStudent(Student student) {
    Cursor cursor = getReadableDatabase().rawQuery(
      "SELECT * FROM " + TABLE_NAME + " WHERE last_name = '" +
        student.lastName + "' AND first_name = '" + student.firstName + "';", null);
    if (cursor.getCount() == 0) {
      getWritableDatabase().execSQL("INSERT INTO " + TABLE_NAME +
        "(last_name, first_name, emergency_number, image ) VALUES('" +
        student.lastName + "', '" +
        student.firstName + "', '" +
        student.ePhoneNumber + "', '" +
        student.image.getAbsolutePath() + "');");
      cursor.close();
    }
    cursor.close();
  }

  public boolean deleteStudent(String value) {
    SQLiteDatabase db = this.getWritableDatabase();
    db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE  _id ='" + value + "'");
    db.close();
    return true;
  }

  public Student getStudent(int iD) {
    Student student = null;
    Cursor cursor = getReadableDatabase().rawQuery("SELECT * FROM " + TABLE_NAME +
      " WHERE _id = " + iD + "; ", null);

    if (cursor != null) {
      cursor.moveToFirst();
      student = new Student(
        cursor.getString(cursor.getColumnIndex("first_name")),
        cursor.getString(cursor.getColumnIndex("last_name")),
        cursor.getString(cursor.getColumnIndex("emergency_number")),
        new File(cursor.getString(cursor.getColumnIndex("image"))), iD);
      cursor.close();
    }
    return student;
  }

  public LinkedList<Student> getStudentList() {
    LinkedList<Student> studentList = new LinkedList<>();
    Cursor cursor = getReadableDatabase().rawQuery(
      "SELECT * FROM " + TABLE_NAME + ";", null);
    if (cursor != null) {
      if (cursor.moveToFirst()) {
        do {
          Student student = new Student(
            cursor.getString(cursor.getColumnIndex("first_name")),
            cursor.getString(cursor.getColumnIndex("last_name")),
            cursor.getString(cursor.getColumnIndex("emergency_number")),
            new File(cursor.getString(cursor.getColumnIndex("image"))),
            cursor.getInt(cursor.getColumnIndex("_id")));
          studentList.add(student);
        } while (cursor.moveToNext());
      }
      cursor.close();
    }
    return studentList;
  }

  public boolean deleteAll(Context context) {
    return context.deleteDatabase(DATABASE_NAME);
  }
}
