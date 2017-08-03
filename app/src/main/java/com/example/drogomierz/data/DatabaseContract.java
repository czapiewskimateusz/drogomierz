package com.example.drogomierz.data;

import android.provider.BaseColumns;

/**
 * Created by Mateusz on 01.08.2017.
 */

public class DatabaseContract {

    public static final class DatabaseEntry implements BaseColumns {
        public static final String TABLE_NAME = "odometer";
        public static final String COLUMN_DISTANCE = "distance";
        public static final String COLUMN_STATUS = "status";
    }


}
