package com.sample;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import com.squareup.sqldelight.RowMapper;
import com.squareup.sqldelight.SqlDelightCompiledStatement;
import java.lang.Override;
import java.lang.String;

public interface MessageModel {
  String TABLE_NAME = "message";

  String MID = "mid";

  String FID = "fid";

  String CREATE_TABLE = ""
      + "CREATE TABLE message (\n"
      + "    mid         INTEGER PRIMARY KEY NOT NULL,\n"
      + "    fid         INTEGER NOT NULL\n"
      + ")";

  String DELETE_ORPHANS = ""
      + "DELETE FROM folder WHERE folder.fid IN (\n"
      + "  SELECT folder.fid FROM folder\n"
      + "  LEFT JOIN message ON message.fid=folder.fid\n"
      + ")";

  String DELETE_ORPHANS_2 = ""
      + "DELETE FROM folder WHERE folder.fid IN (\n"
      + "  SELECT folder.fid FROM folder WHERE fid = fid\n"
      + ")";

  long mid();

  long fid();

  interface Creator<T extends MessageModel> {
    T create(long mid, long fid);
  }

  final class Mapper<T extends MessageModel> implements RowMapper<T> {
    private final Factory<T> messageModelFactory;

    public Mapper(Factory<T> messageModelFactory) {
      this.messageModelFactory = messageModelFactory;
    }

    @Override
    public T map(@NonNull Cursor cursor) {
      return messageModelFactory.creator.create(
          cursor.getLong(0),
          cursor.getLong(1)
      );
    }
  }

  final class Factory<T extends MessageModel> {
    public final Creator<T> creator;

    public Factory(Creator<T> creator) {
      this.creator = creator;
    }
  }

  final class Delete_orphans extends SqlDelightCompiledStatement.Delete {
    public Delete_orphans(SQLiteDatabase database) {
      super("folder", database.compileStatement(""
              + "DELETE FROM folder WHERE folder.fid IN (\n"
              + "  SELECT folder.fid FROM folder\n"
              + "  LEFT JOIN message ON message.fid=folder.fid\n"
              + ")"));
    }
  }

  final class Delete_orphans_2 extends SqlDelightCompiledStatement.Delete {
    public Delete_orphans_2(SQLiteDatabase database) {
      super("folder", database.compileStatement(""
              + "DELETE FROM folder WHERE folder.fid IN (\n"
              + "  SELECT folder.fid FROM folder WHERE fid = fid\n"
              + ")"));
    }
  }
}
