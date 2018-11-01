### Introduction
该项目是所有项目的基础库<br/>
`app`用于演示`base`及各个`lib`的使用方式<br/>
`base`是基础架构库，在此库的基础上开发项目<br/>
`lib_xxx`是单独的组件库，根据项目需求选择性地集成到项目中<br/>

### Base library
已使用的第三方库
1. [RxJava](https://github.com/ReactiveX/RxJava)
2. 网络请求：[Retrofit](https://github.com/square/retrofit)+[OkHttp](https://github.com/square/okhttp)
3. 图片加载：[Glide](https://github.com/bumptech/glide)
4. 数据库：[GreenDAO](https://github.com/greenrobot/greenDAO)
5. 日志：[LLogger](https://github.com/wxcchdStar/LLogger)
6. 状态栏：[StatusBarUtil](https://github.com/laobie/StatusBarUtil)
7. 弹窗：[MaterialDialogs](https://github.com/afollestad/material-dialogs)
8. 图片预览：[PhotoView](https://github.com/chrisbanes/PhotoView)
9. 二维码扫描：[zxing](https://github.com/zxing/zxing)

包简介
1. `api`下是基于RxJava+Retrofit的二次封装，旨在简化Api调用。
2. `app`下是对Activity、Fragment等页面相关的类的封装，旨在简化开发、提高松耦合。
3. `manager`下仅包含`ImageLoader`、`RxBus`。`ImageLoader`用于加载本地、网络图片；`RxBus`用于替换`EventBus`、`Otto`。
4. `utils`下是各种工具类。
5. `view`下是各种自定义View

### Our libraries
1. 图片预览：ImagePreview
2. 图片选择：ImagePicker
3. 二维码扫描：QrcodeScanner

### Third libraries
1. [MaterialDateTimePicker](https://github.com/wdullaer/MaterialDateTimePicker)
2. [CircleImageView](https://github.com/hdodenhof/CircleImageView)
3. [RoundedImageView](https://github.com/vinc3m1/RoundedImageView)