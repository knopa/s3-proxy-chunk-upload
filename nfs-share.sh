#!/bin/bash
REMOTE_SERVER=S3ProxyApp-A.local
FOLDER=/s3proxychunkupload/upload

if [[ "$REMOTE_SERVER" == "$(mount | grep -o $REMOTE_SERVER)" ]]
then
   echo $REMOTE_SERVER NFS share has already been mounted
   echo Unmounting it...
   sudo /bin/umount $REMOTE_SERVER:$FOLDER
   echo Done.
fi

echo
echo Sync local folder with remote server $REMOTE_SERVER
# ending slash in source path is important for rsync
rsync -az --progress $FOLDER/ $REMOTE_SERVER:$FOLDER
echo

echo Mount NFS share from server $REMOTE_SERVER
echo $FOLDER
sudo /bin/mount $REMOTE_SERVER:$FOLDER
echo Done.