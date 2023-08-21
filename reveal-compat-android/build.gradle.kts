plugins {
	alias(libs.plugins.android.library)
	alias(libs.plugins.jetbrains.kotlin.android)
	`maven-publish`
	signing
}

android {
	namespace = "com.svenjacobs.reveal.compat.android"
	compileSdk = Android.compileSdk

	defaultConfig {
		minSdk = Android.minSdk

		aarMetadata {
			minCompileSdk = Android.minSdk
		}

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro",
			)
		}
	}

	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}

	kotlinOptions {
		jvmTarget = "11"
		freeCompilerArgs += "-Xexplicit-api=strict"
	}

	buildFeatures {
		compose = true
	}

	composeOptions {
		kotlinCompilerExtensionVersion = libs.versions.androidx.compose.compiler.get()
	}

	publishing {
		singleVariant("release") {
			withSourcesJar()
			withJavadocJar()
		}
	}

	lint {
		baseline = file("lint-baseline.xml")
	}
}

dependencies {
	api(project(":reveal-common"))

	val composeBom = platform(libs.androidx.compose.bom)

	implementation(composeBom)
	api(libs.androidx.compose.foundation)
	api(libs.androidx.compose.ui)

	debugApi(libs.androidx.compose.ui.tooling)
	debugApi(libs.androidx.compose.ui.test.manifest)

	testImplementation(libs.junit)
	androidTestImplementation(composeBom)
	androidTestImplementation(libs.androidx.test.ext.junit)
	androidTestImplementation(libs.androidx.test.espresso.core)
	androidTestImplementation(libs.androidx.compose.ui.test.junit4)

	lintChecks(libs.slack.compose.lint.checks)
}

publishing {
	publications {
		register<MavenPublication>("release") {
			groupId = Publication.group
			version = Publication.version
			artifactId = "reveal-compat-android"

			afterEvaluate {
				from(components["release"])
			}

			pomAttributes(name = "Reveal (Compat Android)")
		}
	}
}

signing {
	// Store key and password in environment variables
	// ORG_GRADLE_PROJECT_signingKey and ORG_GRADLE_PROJECT_signingPassword
	val signingKey: String? by project
	val signingPassword: String? by project
	useInMemoryPgpKeys(signingKey, signingPassword)

	sign(publishing.publications["release"])
}