[![](https://jitpack.io/v/deepanshuDPS/CustomFiles.svg)](https://jitpack.io/#deepanshuDPS/CustomFiles)

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
  
  OR
  
```
<repositories>
  <repository>
	  <id>jitpack.io</id>
	  <url>https://jitpack.io</url>
  </repository>
</repositories>
```
  
Step 2. Add the dependency
```
dependencies {
  implementation 'com.github.deepanshuDPS:CustomFiles:1.0.0'
}
```
  
  OR
```
<dependency>
  <groupId>com.github.deepanshuDPS</groupId>
  <artifactId>CustomFiles</artifactId>
  <version>1.0.0</version>
</dependency>
```

NOW LET'S SEE HOW TO USE THIS LIBRARY 

There are 3 types of Activities in this library:
1) DocumentsActivity (For Documents Selections according to your MimeTypes Given)
2) MusicsActivity (For Musics Selections)
3) GalleryActivity (For Images And Video Selections)

First of all let's what are the Must needs things of this Library

1) Set Data Binding enabled to your "app build.gradle" file
```
dataBinding {
  enabled = true
  }
```

2) Check target SDK versions of your project and Use "androidx" and "material designs" for your project
```
compileSdkVersion 28
buildToolsVersion "28.0.3"

implementation 'androidx.appcompat:appcompat:1.1.0'
implementation 'com.google.android.material:material:1.0.0'
```
3) Now, Lets start making custom theme for the Activities you want to use from My Library

```
<!-- Here Make Parent to Our "CustomAppTheme" To get your theme compatible with our Activities -->

<style name="YourTheme" parent="CustomAppTheme">
  <!-- Customize your theme here. -->
  <item name="colorPrimary">@color/colorPrimary</item>
  <item name="colorPrimaryDark">@color/colorPrimaryDark</item>
  <item name="colorAccent">@color/colorAccent</item>
</style>

<!-- If you want to change toolbar title color, icons of toolbar etc. then you have to add this in your color.xml-->
  
<color name="colorOnSecondary">YourColorHere</color>
  
```

4) Now choose Any Activity as mentioned above and add to your AndroidManifest.xml file with your customize theme "YourTheme"
```
<activity android:name="com.dps.custom_files.activities.DocumentsActivity"
android:theme="@style/YourTheme"/>

```

5) Now lets start activity for getting custom files

```
Intent intent = new Intent(this, DocumentsActivity.class);
intent.setAction(CustomIntent.ALLOW_MULTIPLE_SELECTION);  // for allowing multiple selection at a time (optional)
intent.putExtra(CustomIntent.SELECTED_TYPES,new String[]{MimeTypes.PDF,MimeTypes.IMAGE_PNG});  // giving array of mime types of string
startActivityForResult(intent, REQUEST_CODE);

```

6) How to get data onActivityResult()

```
if (data != null && resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
     ArrayList<String> selectedFilesPath = data.getStringArrayListExtra(AppConstants.FILES_PATH);
     
     for(String path:selectedFilesPath) // for multiple files
          Log.d("file_path",path); 
          
     // if only one file is selected then you can directly access by using
     String oneFile = selectedFilesPath.get(0); // for single file
}
```

7) Now how to Access Gallery Files and Audio Files (File Getting steps are same as Step-6)

```
// How to access gallery files
Intent intent = new Intent(this, GalleryActivity.class);
intent.setAction(CustomIntent.ALLOW_MULTIPLE_SELECTION);  // for allowing multiple selection at a time (optional)
intent.setType(CustomIntent.PICK_IMAGES_ONLY);  // for pick images only if not mentioned then videos are also pickable
startActivityForResult(intent, REQUEST_CODE);

// How to access music files
Intent intent = new Intent(this, MusicsActivity.class);
intent.setAction(CustomIntent.ALLOW_MULTIPLE_SELECTION);  // for allowing multiple selection at a time (optional)
startActivityForResult(intent, REQUEST_CODE);

```

