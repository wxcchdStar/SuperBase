### Introduction
该项目是所有项目的基础库<br/>
`app`用于演示`base`及各个`lib`的使用方式<br/>
`base`是基础架构库，在此库的基础上开发项目<br/>
`lib_xxx`是单独的组件库，根据项目需求选择性地集成到项目中<br/>

### Base library
包简介
1. `api`下是基于RxJava2+Retrofit的二次封装，旨在简化Api调用。
2. `app`下是对Activity、Fragment等页面相关的类的封装，旨在简化开发、提高松耦合。
3. `manager`下仅包含`ImageLoader`、`RxBus`。`ImageLoader`用于加载本地、网络图片；`RxBus`用于替换`EventBus`、`Otto`。
4. `utils`下是各种工具类。
5. `view`下是各种自定义View
