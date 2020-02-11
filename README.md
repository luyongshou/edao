# edao

[![apache2.0 license](https://img.shields.io/badge/license-apache2.0-brightgreen.svg)](https://opensource.org/licenses/MIT)

1. 一个介于hibernat和mybatis之间的dao框架，采用运行期反射生成javabean的dao实现代码，然后动态编译后加载到jvm。
2. 只有在第一次获取dao实现时存在反射，运行后均为java代码直接赋值没有反射操作
3. 使用postgresql时，会自动创建分区表，支持自动分表的自动处理

