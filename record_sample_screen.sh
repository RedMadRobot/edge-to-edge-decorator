#!/bin/bash

screen_size="$(adb shell dumpsys window | grep cur= |tr -s " " | cut -d " " -f 4|cut -d "=" -f 2)"

wight="$(cut -d'x' -f1 <<<"$screen_size")"
height="$(cut -d'x' -f2 <<<"$screen_size")"

wight_center=$((wight/2))

echo height: "$height"
echo wight: "$wight"

echo wight_center: $wight_center

adb shell content insert --uri content://settings/system --bind name:s:show_touches --bind value:i:1

sleep 3
adb shell input touchscreen swipe $wight_center $((height*80/100)) $wight_center $((height*5/100)) 700
sleep 1
adb shell input touchscreen swipe $wight_center $((height*80/100)) $wight_center $((height*5/100)) 700
sleep 2

adb shell input touchscreen swipe $wight_center $((height*20/100)) $wight_center $((height*95/100)) 700
adb shell input touchscreen swipe $wight_center $((height*20/100)) $wight_center $((height*95/100)) 700
sleep 4

adb shell content insert --uri content://settings/system --bind name:s:show_touches --bind value:i:0
