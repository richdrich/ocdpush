# ocdpush

This is a simple utility to push an Intel hex file to an openocd instance (listening on localhost:4444, currently).

## Prerequisites
openocd (http://www.freddiechopin.info/en/download/category/4-openocd)

Run openocd with (for the Arduino Zero):

    openocd --file PATH\configs\openocd_arduino_zero.cfg
    
## Command line

    java -jar PATH\build\libs\ocdpush-1.0-SNAPSHOT-all.jar <file>
