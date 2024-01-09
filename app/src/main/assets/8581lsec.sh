#!/system/bin/sh

# Harry van der Wolf, V1.0, 03-01-2022
# Backup script for uis7862, uis8518 and sc9853i
# V1.1 replace 7z for zip
# V1.1.2 
#   - correct zip command to not add /oem as folder prefix in the zip
#   - use busybox to be able to use tee for parallel output and log to file
# V1.2 Back to 7zip -store links as links; more verbose output

rm -rf /storage/sdcard1/BACKUP
mkdir -p /storage/sdcard1/BACKUP

echo "twipe_all" > /storage/sdcard1/BACKUP/updatecfg.txt
cp /storage/sdcard1/lsec6316update /storage/sdcard1/BACKUP
cp /oem/app/config.txt /storage/sdcard1/BACKUP

/storage/sdcard1/7zzs a -r -tzip -snh -snl -bb1 -mx=0 -p048a02243bb74474b25233bda3cd02f8 /storage/sdcard1/BACKUP/AllAppUpdate.bin /oem/*


##############################################################################
# You will now find on your usb-stick inside the folder BACKUP
# four files:
#    AllAppUpdate.bin
#    config.txt
#    updatecfg.txt
#    lsec6316update
#
# If you copy these four files with a 6316_1.zip
# to a clean USB-disk, you can always restore
# your uis7862 (ums512) unit.
