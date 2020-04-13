# Geco file organizer

This is a simple file organizer written in Java.
 
Even if it is specifically designed to archive photos and videos, it can be configured 
to organize any file type. 

It has been created with the need to organize thousands of photos and videos taken with my
cameras and smartphone chronologically, within a well-defined directory tree with year/month/day.
In that way it can be easy to create photo-albums or edit videos by picking the media files
of the right time window. 

The application supports both MOVE and COPY actions:
- MOVE action quickly re-organize media files into the filesystem, by creating
customizable directory trees and removing duplicates to save disk space;
- COPY action can be used to create incremental backups on external drives, by copying 
any file in a customizable directory tree.

For example, given a set of images on the filesystem, this application is able to automatically
organize all of them in folders like:

```
  |__ My Photos
  |    |__ ...
  |    |__ 2018
  |    |__ 2019
  |    |__ 2020
  |         |__ 01-January-2020
  |              |__ Jan 05 2020
  |              |   |__ IMG_3242.JPG
  |              |   |__ IMG_3449.JPG
  |              |__ Jan 07 2020
  |                  |__ IMG_3600.JPG        
  |                  |__ IMG_3602.JPG
  |                  |__ IMG_3605.JPG
  |__ My Videos
       |__ 2020
            |__ 01-January-2020
            |    |__ Jan 10 2020
            |        |__ video_01.mp4
            |        |__ video_02.mp4
            |__ 02-February-2020
                 |__ Feb 14 2020
                     |__ video_05.mp4        
                     |__ video_07.mp4
                     |__ video_12.mp4
```

The application is able to read EXIF metadata from main media types to get the creation date.

## Status
The project is still under development. Main features are available and working fine but not 
all the options are available. 

## Getting Started

The project can be imported as a Maven project. Executable binaries (jar) are also available in 
the [Releases](https://github.com/gprovenz/geco-file-organizer/releases). 

### Prerequisites

To execute the program by using the binaries it is required to have Java Runtime Environment (JRE) 
at version 1.8 or higher.
To contribute to the project and compile the files it is recommended JDK version 1.8 
or higher and Maven.

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management

### Build the project

To create the binaries, just call:
```
mvn clean install
```
It will create a couple of jars in the target directory. 
Take the one like _geco-file-organizer-<version>-jar-with-dependencies.jar_.

### Running the application

Running the application is quite simple. From the command line, just run:

```
java -jar geco-file-organizer-<version>-with-dependencies.jar settings.json
```

## Settings JSON files

The application can be configured with JSON files that declare all the options. 

Examples:

- _settings.json_ file content to copy files for backup purposes:
```json
{
  "operation": "COPY",
  "source_path": "/my-document-folder",
  "destination_path": "/my-backup-folder",
  "destination_path_structure": "${file_type}/${year}/${month}-${month_name}-${year}/${day}-${month_name_short}-${year}",
  "on_existing_file_action": "OVERWRITE",
  "locale": "en-us",
  "file_types": [
    {
      "file_type": "Photo",
      "extensions": ["jpg", "jpeg", "raw", "cr2", "gif", "bmp", "psd", "tiff", "tif", "png"],
      "read_exif_metadata": true
    },
    {
      "file_type": "Video",
      "extensions": ["mpg", "mpeg", "mkv", "mp4", "avi", "vid", "mov", "3gp", "asf"]
    },
    {
      "file_type": "Other",
      "extensions": ["*"],
      "ignore": true
    }
  ]
}
```
- _settings.json_ file content to move files to a new directory structure:
```json
{
  "operation": "MOVE",
  "source_path": "/my-document-folder",
  "destination_path": "/my-destination-folder",
  "destination_path_structure": "${file_type}/${year}/${month}-${month_name}-${year}/${day}-${month_name_short}-${year}",
  "remove_duplicates": "true",
  "remove_empty_folders": "true",
  "locale": "en-us",
  "file_types": [
    {
      "file_type": "Photo",
      "extensions": ["jpg", "jpeg", "raw", "cr2", "gif", "bmp", "psd", "tiff", "tif", "png"],
      "read_exif_metadata": true
    },
    {
      "file_type": "Video",
      "extensions": ["mpg", "mpeg", "mkv", "mp4", "avi", "vid", "mov", "3gp", "asf"]
    },
    {
      "file_type": "Other",
      "extensions": ["*"],
      "ignore": true
    }
  ]
}
```

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/gprovenz/geco-file-organizer/tags). 

## Authors

* **Gaspare Provenzano** - *Initial work* 

See also the list of [contributors](https://github.com/gprovenz/geco-file-organizer/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](https://github.com/gprovenz/geco-file-organizer/blob/master/LICENSE) file for details

