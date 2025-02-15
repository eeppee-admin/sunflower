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

package com.google.samples.apps.sunflower

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.samples.apps.sunflower.compose.SunflowerApp
import com.google.samples.apps.sunflower.ui.SunflowerTheme
import dagger.hilt.android.AndroidEntryPoint

// 标记为MAIN 的activity
@AndroidEntryPoint
class GardenActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Displaying edge-to-edge
        enableEdgeToEdge()
        setContent {
            // 主题
            SunflowerTheme {
                // app托管
                SunflowerApp()
            }
        }
        
    }
}
