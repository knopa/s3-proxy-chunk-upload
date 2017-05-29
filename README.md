# S3ProxyChunkUpload

Microservice which allow to upload to s3 files with smaller chunks.
# Description

If you have a mobile application that allows your customers to keep files on the Amazon S3 service,
 but your customers have a bad or unstable network connection, you will face the problem when you need repeatedly try to upload a file.

Amazon S3 is supporting chuncked upload, but each part should has a size of 5 megabytes (MB) or more.
 In most cases, the files sent via the mobile application are smaller, so for small files a different approach is needed.

To solve this problem, we have developed the S3ProxyChunkUpload that is a proxy server between your application and the Amazon S3.

# Features!


  - Upload files in any size and any number of parts to Amazon S3
  - Compress file the uploaded to Amazon file
  - Convert the file to another format


> S3ProxyChunkUpload that is a proxy server between your application and the Amazon S3
> The S3ProxyChunkUpload app allows you to upload a file
> consisting of any number and any size of parts,
> even if the user of your application has a low internet speed
> or there will be a loss of connection,
> it will be able to upload part of the file and uploading other parts of file
> when the connection will be restored.

 
 After each upload of a part of the file, a response is sent with the result of the operation,
so you can always monitor from your application the number of already uploaded parts of the files.
After all the parts of the file are uploaded, all parts of the file will be combined to create the source file and sends the file to the Amazon S3.


### Tech
---

S3ProxyChunkUpload uses a number of open source projects to work properly:

* [PlayFramework] - lightweight, stateless, web-friendly architecture
* [Postgresql] | [Mysql] -  open source database  
* [SBT] - the interactive build tool.

And of course S3ProxyChunkUpload itself is open source with a [public repository][s3proxychunkupload]
 on GitHub.

[//]: # (These are reference links used)


   [s3proxychunkupload]: <https://github.com/webinerds/s3-proxy-chunk-upload>
   [PlayFramework]: <https://www.playframework.com/>
   [Postgresql]: <https://www.postgresql.org/>
   [Mysql]: <https://www.mysql.com/>
   [SBT]: <http://www.scala-sbt.org/>
   
   ### Configurations (application.conf)
   ---
  - db.default.driver = org.postgresql.Driver
  - db.default.url = "postgres://postgres:chunkupload@localhost/s3proxychunkupload"
  - DB_TYPE = "postgres"
  - AWS_ACCESS_KEY  - Access key to the AWS
  - AWS_SECRET_KEY  - Secret key to the AWS
  - AWS_S3_BUCKET = "com.s3proxy.posts" - bucket name
  - AWS_S3_HOST  = "s3.amazonaws.com" - host name
  - UPLOAD_FOLDER  = "/s3proxychunkupload/upload" - directory where temporary files will be located
  ##### Notifications
 - SEND_UPLOAD_STATUS_URL  = ""
 - SEND_CONVERT_STATUS_URL = "http://s3proxychunkupload.com/video/convert"
  ##### Video convertation
 - AWS_ET_END_POINT        = "elastictranscoder.us.amazonaws.com"
 - VIDEO_PIPELINE_ID       = "0000000000000-xxxxxx"
 - VIDEO_PRESETS           = {"stream" = "0000000000000-xxxxxx"} - to disable the conversion functionality, do not use this option
 
 
## License

S3ProxyChunkUpload is open source software, licensed under the terms of MIT license.
   
