#!/bin/bash

source ~/.profile

resList=( 1080 720 480 360 240 144 )
fn="$1"
folderPath=$2

width=`ffprobe -v error -select_streams v:0 -show_entries stream=width -of default=nw=1:nk=1 $folderPath/$fn`
height=`ffprobe -v error -select_streams v:0 -show_entries stream=height -of default=nw=1:nk=1 $folderPath/$fn`
input_nb_pixels=`bc <<< $width*$height`

for res in "${resList[@]}"
do
	desired_height=$res
	desired_width=`bc <<< $desired_height*16/9`
	if [ $res -eq 480 ]
	then
		desired_width=$((desired_width+1))
	fi
	nb_pixels=`bc <<< $desired_height*$desired_width`
	if [ $nb_pixels -le $input_nb_pixels ]
	then
		mkdir $folderPath/$res
		ffmpeg -i $folderPath/$fn -vf scale=$desired_width:$desired_height $folderPath/$res/temp_$res.mp4
		ffmpeg -i $folderPath/$res/temp_$res.mp4 -c:v libx264 -c:a aac $folderPath/$res/$res.mp4
		rm $folderPath/$res/temp_$res.mp4
	fi
done	
















