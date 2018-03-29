/*
 *    Copyright (C) 2016 Amit Shekhar
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.flatbuffer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.flatbuffer.flatmodel.Friend;
import com.flatbuffer.flatmodel.People;
import com.flatbuffer.flatmodel.PeopleList;
import com.flatbuffer.jsonmodel.FriendJson;
import com.flatbuffer.jsonmodel.PeopleJson;
import com.flatbuffer.jsonmodel.PeopleListJson;
import com.flatbuffer.utils.Utils;
import com.google.flatbuffers.FlatBufferBuilder;
import com.google.gson.Gson;

import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    private TextView textViewFlat, textViewJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textViewFlat = (TextView) findViewById(R.id.textViewFlat);
        textViewJson = (TextView) findViewById(R.id.textViewJson);
    }

    /*  流程：
           1.集成FlatBuffer的java源码https://github.com/google/flatbuffers /trunk/java目录
           2.https://github.com/google/flatbuffers/releases下载flatc.exe
           3.编写fbs文件
           4.flatc.exe --java  xx.fbs生成java文件
           5.如下编解码
        */
    public void loadFromFlatBuffer(View view) {
        String jsonText = new String(Utils.readRawResource(getApplication(), R.raw.sample_json));
        PeopleListJson plJson = new Gson().fromJson(jsonText, PeopleListJson.class);
        FlatBufferBuilder fbb = new FlatBufferBuilder();
        int[] peopleOffset = new int[plJson.peoples.size()];
        int i = 0;
        for (PeopleJson people : plJson.peoples) {
            int idOffset = fbb.createString(people.id);
            int genderOffset = fbb.createString(people.gender);
            int guidOffset = fbb.createString(people.guid);
            int nameOffset = fbb.createString(people.name);
            int companyOffset = fbb.createString(people.company);
            int emailOffset = fbb.createString(people.email);


            int friendsOffset = 0;
            if (people.friends != null && people.friends.size() != 0) {
                int[] fOffsets = new int[people.friends.size()];
                int k = 0;
                for (FriendJson fJson : people.friends) {
                    int fnameOffset = fbb.createString(fJson.name);
                    fOffsets[k] = Friend.createFriend(fbb,fJson.id,fnameOffset);
                    k++;
                }
                //People中的frineds列表
                friendsOffset = People.createFriendsVector(fbb,fOffsets);
            }

            //PeopleList列表中的People
            peopleOffset[i] = People.createPeople(fbb,idOffset,
            people.index,
            guidOffset,
            nameOffset,
            genderOffset,
            companyOffset,
            emailOffset,
            friendsOffset);
            i++;
        }
        //相当于Array.addArray
        int listOffset = PeopleList.createPeoplesVector(fbb,peopleOffset);
        PeopleList.startPeopleList(fbb);
        PeopleList.addPeoples(fbb,listOffset);
        int endOffset = PeopleList.endPeopleList(fbb);
        fbb.finish(endOffset);

        long startTime = System.currentTimeMillis();
        ByteBuffer bb = fbb.dataBuffer();
        PeopleList peopleList = PeopleList.getRootAsPeopleList(bb);
        Log.i(TAG, "peopleList size:" + peopleList.peoplesLength());
       /* int length = peopleList.peoplesLength();
        for (int m = 0; m < length; m++) {
            People p = peopleList.peoples(m);
            Log.i(TAG, "name:" + p.name() + " gender:" + p.gender() + " friends size:" + p.friendsLength());
                //+ "  end frined name:" + p.friends(p.friendsLength() - 1).name());
        }*/
        long timeTaken = System.currentTimeMillis() - startTime;
        String logText = "FlatBuffer : " + timeTaken + "ms";
        textViewFlat.setText(logText);
        Log.d(TAG, "loadFromFlatBuffer " + peopleList.toString());
    }

    public void loadFromJson(View view) {
        String jsonText = new String(Utils.readRawResource(getApplication(), R.raw.sample_json));
        long startTime = System.currentTimeMillis();
        PeopleListJson peopleList = new Gson().fromJson(jsonText, PeopleListJson.class);
        long timeTaken = System.currentTimeMillis() - startTime;
        String logText = "Json : " + timeTaken + "ms";
        textViewJson.setText(logText);
        Log.d(TAG, "loadFromJson " + logText);
    }
}
