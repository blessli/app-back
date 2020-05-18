# grep
- 忽略匹配样式中的字符大小写

echo "hello world" | grep -i "HELLO"    
hello
- 统计多个文件中某字符串出现的行数

cat ldm.conf ldm1.conf | grep "sds" | wc -l
