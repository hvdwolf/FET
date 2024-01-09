#!/system/bin/sh

# Harry van der Wolf, V1.0, 03-01-2022
# Backup script for uis7862, uis8518 and sc9853i
# V1.1 replace 7z for zip
# V1.1.2 
#   - correct zip command to not add /oem as folder prefix in the zip
#   - use busybox to be able to use tee for parallel output and log to file
# V1.2 Back to 7zip -store links as links; more verbose output

#rm -rf /storage/emulated/0/BACKUP
#mkdir -p /storage/emulated/0/BACKUP

echo "twipe_all" > /storage/emulated/0/BACKUP/updatecfg.txt
#cp /storage/sdcard1/lsec6315update /storage/sdcard1/BACKUP
cp /oem/app/config.txt /storage/emulated/0//BACKUP

/data/data/xyz.hvdw.fytextratool/cache/7zzs a -r -tzip -snh -snl -bb1 -mx=0 -p048a02243bb74474b25233bda3cd02f8 /storage/emulated/0/BACKUP/AllAppUpdate.bin /oem/*


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
