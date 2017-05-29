# S3ProxyChunkUpload

A microservice that lets you upload S3 files in smaller chunks
# Description

When your mobile application stores files on Amazon S3, 
you can run into problems with repeatedly trying to upload files when your users have poor or unstable network connections. 
Amazon S3 supports chunked uploading, but each chunk must be 5 MB or more.
In most cases, files sent via mobile apps are smaller than 5 MB, and so a different approach is needed. 
To solve the problem of repeated attempts to upload a file to S3 storage, we have developed S3ProxyChunkUpload, 
a proxy server that sits between your application and Amazon S3.

# Features!


  - Upload files of any size in any number of chunks to Amazon S3 storage
  - Compress uploaded files
  - Convert files to different formats


> The S3ProxyChunkUpload app allows you to upload a file in any number of chunks – 
> and those chunks can be of any size.
> By uploading in chunks, part of a file may be successfully uploaded
> even if a user has a slow internet connection or the connection drops,
> and other parts of the file can be sent when the connection is restored.

 
After each chunk of a file is successfully uploaded to the proxy server, 
a response is sent by the proxy server to your app to keep tabs on the status of the upload. 
After all chunks of a file are uploaded, they are combined on the proxy server to create a source file that is then sent to Amazon S3.


### Installation
---
S3ProxyChunkUpload requires [PlayFramework](https://www.playframework.com/) v2.5.0+ to run.

Install the dependencies and start the server.

```sh
$ cd s3-proxy-chunk-upload
$ activator run
```

### Tech
---

S3ProxyChunkUpload relies on a number of open source projects:

* [PlayFramework] – lightweight, stateless, web-friendly architecture
* [Postgresql] | [Mysql] – open source database
* [SBT] – interactive build tool

And of course, S3ProxyChunkUpload is open source itself, with a [public repository][s3proxychunkupload]
 on GitHub.

   
   ### Configurations (application.conf)
   ---
  - db.default.driver = org.postgresql.Driver
  - db.default.url = "postgres://postgres:chunkupload@localhost/s3proxychunkupload"
  - DB_TYPE = "postgres"
  - AWS_ACCESS_KEY (access key for AWS)
  - AWS_SECRET_KEY (secret key for AWS)
  - AWS_S3_BUCKET = "com.s3proxy.posts" - bucket name
  - AWS_S3_HOST  = "s3.amazonaws.com" (hostname)
  - UPLOAD_FOLDER  = "/s3proxychunkupload/upload" (directory where temporary files will be located)
  ##### Notifications
 - SEND_UPLOAD_STATUS_URL  = ""
 - SEND_CONVERT_STATUS_URL = "http://s3proxychunkupload.com/video/convert"
  ##### Video conversion
 - AWS_ET_END_POINT        = "elastictranscoder.us.amazonaws.com"
 - VIDEO_PIPELINE_ID       = "0000000000000-xxxxxx"
 - VIDEO_PRESETS           = {"stream" = "0000000000000-xxxxxx"} (to disable the conversion functionality, do not use this option)
 
 
## License

S3ProxyChunkUpload is open source software licensed under the terms of the MIT license.

[//]: # (These are reference links used)


   [s3proxychunkupload]: <https://github.com/webinerds/s3-proxy-chunk-upload>
   [PlayFramework]: <https://www.playframework.com/>
   [Postgresql]: <https://www.postgresql.org/>
   [Mysql]: <https://www.mysql.com/>
   [SBT]: <http://www.scala-sbt.org/>
   
