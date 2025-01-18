# Baseline Profiles 概述
Baseline Profiles 是一种用于提升Android应用性能的技术。通过在应用或库中分发基准配置文件，Android运行时（ART）可以预先（AOT）编译这些配置文件中定义的代码路径，从而在应用首次启动时提升性能。这种配置文件引导的优化（PGO）可以让应用在启动时更快，减少互动卡顿，并提高整体运行时性能。
# 工作流程
1. 生成配置文件：系统会为应用生成人类可读的配置文件规则，并在应用中将其编译为二进制文件格式。这些规则文件可以在 assets/dexopt/baseline.prof 中找到。
上传到Google Play：将包含基准配置文件的AAB上传到Google Play。Google Play会处理该配置文件，并将其与APK一起发布给用户。
安装时编译：在应用安装期间，ART会对配置文件中的方法执行AOT编译，以提升这些方法的执行速度。如果配置文件包含应用启动或帧渲染期间使用的方法，用户在使用应用时，启动速度可能会更快，并且卡顿现象可能会更少。
性能微调：此流程可与云配置文件汇总功能结合使用，在一段时间后根据应用的实际使用情况对性能进行微调。
2. 云配置文件
云配置文件由Google Play商店进行汇总，并分发给用户以进行安装时编译。虽然云配置文件是由用户与应用的实际互动驱动的，但它们在更新后需要几小时到几天的时间才能分发，这就限制了它们的可用性。此外，云配置文件仅支持搭载Android 9（API级别28）或更高版本的Android设备。
创建基准配置文件
3. 使用Macrobenchmark库：可以使用Jetpack Macrobenchmark库和BaselineProfileRule为每个应用版本自动生成配置文件。用于生成配置文件的入口点是collectBaselineProfile函数。
- 编写测试用例：在profileBlock lambda中，需要指定涵盖应用典型用户体验历程的互动。该库将运行profileBlock多次，收集调用的类和函数以进行优化，并生成设备端的Baseline Profiles。
- 复制配置文件：生成的Baseline Profiles会复制到app/src/variant/generated/baselineProfiles目录下。然后，将生成的文件重命名为baseline-prof.txt，并将其复制到应用模块的src/main目录。
- 添加依赖项：将profileinstaller依赖项添加到:app模块：
kotlin复制
dependencies {
    implementation("androidx.profileinstaller:profileinstaller:1.2.0")
}
配置Gradle插件
可以使用Gradle插件来配置Baseline Profiles的生成方式。以下是一个示例配置：
kotlin复制
// Non-specific filters applied to all the variants.
baselineProfile {
    filter { include("com.myapp.**") }
}

// Flavor-specific filters.
baselineProfile {
    variants {
        free {
            filter {
                include("com.myapp.free.**")
            }
        }
        paid {
            filter {
                include("com.myapp.paid.**")
            }
        }
    }
}

// Build-type-specific filters.
baselineProfile {
    variants {
        release {
            filter {
                include("com.myapp.**")
            }
        }
    }
}

// Variant-specific filters.
baselineProfile {
    variants {
        freeRelease {
            filter {
                include("com.myapp.**")
            }
        }
    }
}
# 总结
Baseline Profiles是一种有效的性能优化工具，可以帮助开发者提升应用的启动速度和运行时性能。通过生成和使用Baseline Profiles，开发者可以在应用首次启动时获得更好的用户体验。