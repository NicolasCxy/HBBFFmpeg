# Hbb_ffmepg_code
### FFMEPG 解码H264 + OPENGL渲染

##### 功能版本：1.1
- 初步实现读取本地H264数据，传到native交给ffmpeg解码
- 解码完成之后通过JNI反馈到上层通过OPENGL 渲染YUV数据

##### 功能版本：1.2
- 支持同时多路解码
- 添加业务端布局模块
- 遗留问题：实时解码延迟会越来越高


##### 功能版本：1.3
- 添加队列，C层开线程做解码操作
- 解决实时解码延迟递增问题：还没有测试