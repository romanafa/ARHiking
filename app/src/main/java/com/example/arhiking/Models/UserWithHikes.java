package com.example.arhiking.Models;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import java.util.List;

public class UserWithHikes {
    @Embedded
    public User user;
    @Relation(
            parentColumn = "uid",
            entityColumn = "user_creator_id"
    )
    public List<Hike> hikes;
}