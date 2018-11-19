#!/usr/bin/env bash

source ~/.bash_profile && source /etc/profile

echo "+-------------+"
echo "+  fuck data  +"
echo "+-------------+"

current_dir=$(dirname "$PWD")
lib=`ls $current_dir/lib | awk '{print "'$current_dir/lib'/"$0}' | tr "\n" ":"`

flink-1.4.2/bin/flink -c org.cdp.Sailing $lib $lib/conf/test/application.conf
