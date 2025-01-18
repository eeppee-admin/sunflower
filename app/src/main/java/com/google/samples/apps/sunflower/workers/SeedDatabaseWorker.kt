/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.sunflower.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.samples.apps.sunflower.data.AppDatabase
import com.google.samples.apps.sunflower.data.Plant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SeedDatabaseWorker(
        context: Context,
        workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val filename = inputData.getString(KEY_FILENAME)
            if (filename != null) {
                applicationContext.assets.open(filename).use { inputStream ->
                    JsonReader(inputStream.reader()).use { jsonReader ->
                        val plantType = object : TypeToken<List<Plant>>() {}.type
                        val plantList: List<Plant> = Gson().fromJson(jsonReader, plantType)

                        val database = AppDatabase.getInstance(applicationContext)
                        database.plantDao().upsertAll(plantList)

                        Result.success()
                    }
                }
            } else {
                Log.e(TAG, "Error seeding database - no valid filename")
                Result.failure()
            }
        } catch (ex: Exception) {
            Log.e(TAG, "Error seeding database", ex)
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "SeedDatabaseWorker"
        const val KEY_FILENAME = "PLANT_DATA_FILENAME"
    }
}

/*
这段代码是一个Kotlin编写的SeedDatabaseWorker类，它继承自CoroutineWorker，用于在后台线程中执行数据库初始化或数据种子（seed）加载的任务。以下是对这段代码的详细解析：
类定义
kotlin复制
class SeedDatabaseWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams)
SeedDatabaseWorker：这是自定义的Worker类名称。
context: Context：构造函数参数，提供应用上下文，用于访问应用资源等。
workerParams: WorkerParameters：构造函数参数，包含Worker的配置信息，如输入数据等。
CoroutineWorker：这是SeedDatabaseWorker继承的父类，它允许在Worker中使用协程来执行异步任务。
doWork方法
kotlin复制
override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
    // 方法实现
}
doWork：这是Worker类中需要重写的方法，用于定义Worker的具体工作内容。
suspend：表示这是一个挂起函数，可以在协程中使用，支持异步操作。
withContext(Dispatchers.IO)：将代码块中的操作切换到IO调度器执行，适用于进行磁盘I/O或网络请求等操作。
方法实现
kotlin复制
try {
    val filename = inputData.getString(KEY_FILENAME)
    if (filename != null) {
        applicationContext.assets.open(filename).use { inputStream ->
            JsonReader(inputStream.reader()).use { jsonReader ->
                val plantType = object : TypeToken<List<Plant>>() {}.type
                val plantList: List<Plant> = Gson().fromJson(jsonReader, plantType)

                val database = AppDatabase.getInstance(applicationContext)
                database.plantDao().upsertAll(plantList)

                Result.success()
            }
        }
    } else {
        Log.e(TAG, "Error seeding database - no valid filename")
        Result.failure()
    }
} catch (ex: Exception) {
    Log.e(TAG, "Error seeding database", ex)
    Result.failure()
}
inputData.getString(KEY_FILENAME)：从Worker的输入数据中获取文件名。
applicationContext.assets.open(filename)：通过应用上下文打开assets目录下的文件。
JsonReader和Gson()：使用JsonReader读取JSON数据，并通过Gson库将其解析为List<Plant>对象。
AppDatabase.getInstance(applicationContext)：获取应用数据库的单例实例。
database.plantDao().upsertAll(plantList)：通过数据库的DAO（Data Access Object）插入或更新植物数据。
Result.success()和Result.failure()：分别表示任务执行成功或失败。
Companion Object
kotlin复制
companion object {
    private const val TAG = "SeedDatabaseWorker"
    const val KEY_FILENAME = "PLANT_DATA_FILENAME"
}
TAG：用于日志记录的标签。
KEY_FILENAME：定义输入数据中文件名的键。
总结
这个SeedDatabaseWorker类的主要功能是从assets目录中读取一个JSON文件，解析为植物数据列表，并将其插入到应用数据库中。如果文件名无效或在执行过程中出现异常，会记录错误日志并返回失败结果。这个Worker可以被WorkManager调度执行，适用于在应用启动时或特定条件下初始化数据库。

 */