#!/system/bin/sh

# Harry van der Wolf, V1.0, 03-01-2022
# Backup script for uis7862, uis8518 and sc9853i
# V1.1 replace 7z for zip
# V1.1.2 
#   - correct zip command to not add /oem as folder prefix in the zip
#   - use busybox to be able to use tee for parallel output and log to file
# V1.2 Back to 7zip -store links as links; more verbose output
# V1.3 The Fyt Extra Tool version

# First storage
ls -l /storage
# emulated
ls -l /storage/sdcard0

rm -rf /cache/FET
mkdir 755 /cache/FET
cp -r /storage/sdcard0/7zss /cache/FET
chmod -R 755 /cache/FET/7zss
cd /cache/FET

/cache/FET/7zzs a -r -tzip -snh -snl -bb1 -mx=0 -p048a02243bb74474b25233bda3cd02f8 /storage/sdcard1/BACKUP/AllAppUpdate.bin /oem/*

rm -rf /storage/sdcard0/lsec_updatesh
rm -rf /storage/sdcard0/lsec6315update
rm -rf /storage/sdcard0/7zzs

##############################################################################
# You will now find on your usb-stick inside the folder BACKUP
# four files:
#    AllAppUpdate.bin
#    config.txt
#    updatecfg.txt
#    lsec6315update
#
# If you copy these four files with a 6315_1.zip
# to a clean USB-disk, you can always restore
# your uis7862 (ums512) unit.
